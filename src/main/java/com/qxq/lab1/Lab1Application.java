package com.qxq.lab1;

import cn.hutool.core.util.RandomUtil;
import com.qxq.lab1.common.Result;
import com.qxq.lab1.dao.StockDao;
import com.qxq.lab1.entity.Stock;
import com.qxq.lab1.entity.po.OrderPO;
import com.qxq.lab1.service.StockService;
import com.qxq.lab1.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@SpringBootApplication
@RequiredArgsConstructor
public class Lab1Application {

	public static void main(String[] args) {
		SpringApplication.run(Lab1Application.class, args);
	}

	final StockService stockService;

	final StockDao stockDao;

	final RedisUtil redisUtil;

	@RequestMapping("/init")
	@ResponseBody
	public Result<Stock> init() {
		Stock stock = new Stock();
		stock.setCount(100L);
		stock.setItemId(RandomUtil.randomLong(1000));
		stock.setVersion(0L);
		stock = stockDao.save(stock);
		return Result.ok(stock);
	}

	@RequestMapping("/init-cache")
	@ResponseBody
	public Result initCache() {
		Result<Stock> result = init();
		Stock data = result.getData();

		redisUtil.cacheValue("stock-"+data.getItemId(), data.getCount());
//		redisUtil.cacheValue("queue-"+data.getItemId(), data.getCount());
		return result;
	}

	@RequestMapping("/push")
	@ResponseBody
	public Result push(String test) {
		redisUtil.lpush("hahah", new OrderPO(123L, 234L));
		return Result.ok();
	}

	@RequestMapping("/pop")
	@ResponseBody
	public Result pop(String test) {
		return Result.ok(redisUtil.rpop("hahah"));
	}

	@RequestMapping("/start1")
	@ResponseBody
	public Result start1(long userId, long itemId) {

		try {
			if (stockService.transaction1(userId, itemId)) {
				log.info("用户:{} 商品:{}, 抢购成功", userId, itemId);
				return Result.ok();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.warn("用户:{} 商品:{}, 抢购失败", userId, itemId);
		return Result.error();
	}

	@RequestMapping("/start11")
	@ResponseBody
	public Result start11(long userId, long itemId) {

		try {
			if (stockService.transaction11(userId, itemId)) {
				log.info("用户:{} 商品:{}, 抢购成功", userId, itemId);
				return Result.ok();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.warn("用户:{} 商品:{}, 抢购失败", userId, itemId);
		return Result.error();
	}
	@RequestMapping("/start12")
	@ResponseBody
	public Result start12(long userId, long itemId) {

		try {
			if (stockService.transaction12(userId, itemId)) {
				log.info("用户:{} 商品:{}, 抢购成功", userId, itemId);
				return Result.ok();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.warn("用户:{} 商品:{}, 抢购失败", userId, itemId);
		return Result.error();
	}

	@RequestMapping("/start2")
	@ResponseBody
	public Result start2(long userId, long itemId) {

		try {
			if (stockService.transaction2(userId, itemId)) {
				log.info("用户:{} 商品:{}, 抢购成功", userId, itemId);
				return Result.ok();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.warn("用户:{} 商品:{}, 抢购失败", userId, itemId);
		return Result.error();
	}

	@RequestMapping("/start3")
	@ResponseBody
	public Result start3(long userId, long itemId) {

		try {
			if (stockService.transaction3(userId, itemId)) {
				log.info("用户:{} 商品:{}, 抢购成功", userId, itemId);
				return Result.ok();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.warn("用户:{} 商品:{}, 抢购失败", userId, itemId);
		return Result.error();
	}

	@RequestMapping("/start4")
	@ResponseBody
	public Result start4(long userId, long itemId) {

		try {
			if (stockService.transaction4(userId, itemId)) {
				log.info("用户:{} 商品:{}, 抢购成功", userId, itemId);
				return Result.ok();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.warn("用户:{} 商品:{}, 抢购失败", userId, itemId);
		return Result.error();
	}

	@RequestMapping("/start5")
	@ResponseBody
	public Result start5(long userId, long itemId) {

		try {
			if (stockService.transaction5(userId, itemId)) {
				log.info("用户:{} 商品:{}, 抢购成功", userId, itemId);
				return Result.ok();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.warn("用户:{} 商品:{}, 抢购失败", userId, itemId);
		return Result.error();
	}

	@RequestMapping("/start6")
	@ResponseBody
	public Result start6(long userId, long itemId) {

		try {
			if (stockService.transaction6(userId, itemId)) {
				log.info("用户:{} 商品:{}, 抢购成功", userId, itemId);
				return Result.ok();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.warn("用户:{} 商品:{}, 抢购失败", userId, itemId);
		return Result.error();
	}
}
