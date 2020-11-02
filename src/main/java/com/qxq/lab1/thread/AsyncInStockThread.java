package com.qxq.lab1.thread;

import com.qxq.lab1.entity.po.OrderPO;
import com.qxq.lab1.service.StockService;
import com.qxq.lab1.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author: QXQ
 * @time: 2020/11/1 16:11
 * @desc: TODO
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncInStockThread implements DisposableBean, CommandLineRunner, ApplicationListener<ContextRefreshedEvent> {

    final RedisUtil redisUtil;
    final StockService stockService;

    @Override
    public void destroy() throws Exception {
        log.info("程序关闭~~");
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("异步入库任务线程开启");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        OrderPO orderPO = (OrderPO) redisUtil.rpop("item-queue");
                        if(orderPO != null) {
                            log.info("异步入库:{} {}", orderPO.getUserId(), orderPO.getItemId());
                            stockService.saveRecord(orderPO.getUserId(), orderPO.getItemId());
                            TimeUnit.MILLISECONDS.sleep(10);
                        } else {
//                            log.info("empty queue");
                            TimeUnit.SECONDS.sleep(1);
                        }
                    } catch (Exception e) {
                        log.error("异步入库任务异常", e);
                    }

                }
            }
        }).start();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("spring容器初始化完成");
    }
}
