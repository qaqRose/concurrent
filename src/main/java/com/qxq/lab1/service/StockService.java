package com.qxq.lab1.service;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.qxq.lab1.dao.RecordDao;
import com.qxq.lab1.dao.StockDao;
import com.qxq.lab1.entity.Record;
import com.qxq.lab1.entity.Stock;
import com.qxq.lab1.entity.po.OrderPO;
import com.qxq.lab1.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author qiu
 * @date 2020/10/31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    final StockDao stockDao;
    final RecordDao recordDao;
    final RedissonClient redissonClient;
    final RedisUtil redisUtil;

    // 随机终端id (偷懒写法)
    Snowflake snowflake = IdUtil.getSnowflake(RandomUtil.randomInt(10), 1);

    @Transactional
    public void saveRecord(long userId, long itemId) {

        Record record = new Record();
        record.setId(snowflake.nextId());
        record.setCreateDate(new Date());
        record.setNum(1L);
        record.setItemId(itemId);
        record.setUserId(userId);
        recordDao.save(record);
    }


    /**
     * 异步入库
     * 这里采用放在队列（redis实现）中
     *
     * @param userId
     * @param itemId
     */
    private void asyncSaveRecord(long userId, long itemId) {
        redisUtil.lpush("item-queue", new OrderPO(userId, itemId));
    }

    /**
     * 错误示范
     *
     * @param userId
     * @param itemId
     * @return
     */
    @Transactional
    public boolean transaction1(long userId, long itemId) {
        // 查询库存
        Stock stock = stockDao.getByItemId(itemId);

        if (stock.getCount() > 0L) {
            // 扣减库存
            stockDao.reduceStock(stock.getId());

            // 增加记录
            saveRecord(userId, itemId);
            return true;
        }
        return false;
    }

    /**
     * 对象锁
     */
    private volatile static Object objLock = new Object();

    /**
     * java synchronized
     *
     * @param userId
     * @param itemId
     * @return
     */
    public boolean transaction11(long userId, long itemId) {
        synchronized (objLock) {
            return transaction1(userId, itemId);
        }
    }

    /**
     * 非公平锁
     */
    private Lock lock = new ReentrantLock(false);

    /**
     * java Lock
     *
     * @param userId
     * @param itemId
     * @return
     */
    public boolean transaction12(long userId, long itemId) {
        boolean lockResult = false;
        try {
            lockResult = lock.tryLock(3, TimeUnit.SECONDS);
            if (lockResult) {
                return transaction1(userId, itemId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lockResult) {
                lock.unlock();
            }
        }
        return false;
    }

    /**
     * 悲观锁 for update
     *
     * @param userId
     * @param itemId
     * @return
     */
    @Transactional
    public boolean transaction2(long userId, long itemId) {
        // 查询库存
        Stock stock = stockDao.selectByItemIdForUpdate(itemId);

        if (stock.getCount() > 0L) {
            // 扣减库存
            stockDao.reduceStock(stock.getId());

            // 增加记录
            saveRecord(userId, itemId);
            return true;
        }
        return false;
    }

    /**
     * 乐观锁 version
     *
     * @param userId
     * @param itemId
     * @return
     */
    @Transactional
    public boolean transaction3(long userId, long itemId) {
        // 查询库存
        Stock stock = stockDao.getByItemId(itemId);

        if (stock.getCount() > 0L) {
            // 扣减库存
            int i = stockDao.reduceStockWithVersion(stock.getId(), stock.getVersion());

            if (i == 1) {
                // 增加记录
                saveRecord(userId, itemId);
                return true;
            }
        }
        return false;
    }

    /**
     * 分布式锁 redisson
     *
     * @param userId
     * @param itemId
     * @return
     */
    //@Transactional 这里不能加事务
    public boolean transaction4(long userId, long itemId) {
        RLock lock = redissonClient.getFairLock(itemId + "");
        boolean result = false;
        boolean lockFlag = false;
        try {
            lockFlag = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (lockFlag) {
                result = transaction1(userId, itemId);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            result = false;
        } finally {
            if (lockFlag) {
                lock.unlock();
            }
        }

        return result;
    }


    /**
     * 分布式锁 redisson + 缓存
     *
     * @param userId
     * @param itemId
     * @return
     */
    //@Transactional 这里不能加事务
    public boolean transaction5(long userId, long itemId) {

        Integer value = (Integer) redisUtil.getValue("stock-" + itemId);
        if (value <= 0) return false;

        RLock lock = redissonClient.getLock(itemId + "");
        boolean result = false;
        boolean lockFlag = false;
        try {
            lockFlag = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (lockFlag) {
                value = (Integer) redisUtil.getValue("stock-" + itemId);
                // 增加记录
                if (value > 0) {
                    redisUtil.decr("stock-" + itemId, 1);
                    saveRecord(userId, itemId);
                    result = true;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            result = false;
        } finally {
            if (lockFlag) {
                lock.unlock();
            }
        }
        return result;
    }


    /**
     * 分布式锁 redisson + 缓存 (异步入库)
     *
     * @param userId
     * @param itemId
     * @return
     */
    public boolean transaction6(long userId, long itemId) {

        Integer value = (Integer) redisUtil.getValue("stock-" + itemId);
        if (value <= 0) return false;

        RLock lock = redissonClient.getLock(itemId + "");
        boolean result = false;
        boolean lockFlag = false;
        try {
            lockFlag = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (lockFlag) {
                value = (Integer) redisUtil.getValue("stock-" + itemId);
                // 增加记录
                if (value > 0) {
                    redisUtil.decr("stock-" + itemId, 1);
                    // 异步入库
                    asyncSaveRecord(userId, itemId);
                    result = true;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            result = false;
        } finally {
            if (lockFlag) {
                lock.unlock();
            }
        }
        return result;
    }
}
