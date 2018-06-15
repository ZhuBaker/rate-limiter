package com.github.bakerzhu.common.limiter;

import com.github.bakerzhu.common.config.CustomRateLimiterConfig;
import com.github.bakerzhu.common.config.RateRedisConfig;
import com.github.bakerzhu.common.ratelimiter.config.RateLimiterConfig;
import com.github.bakerzhu.common.ratelimiter.limiter.Limiter;
import com.github.bakerzhu.common.ratelimiter.limiter.LimiterFactory;
import com.github.bakerzhu.commons.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月14日
 * @modifytime:
 */
public class LimiterTest {
    private final static Logger logger		= LoggerFactory.getLogger(LimiterTest.class);

    private static Limiter limiter		= null;
    private volatile static int	maxNum		= 0;
    private final static String	ROUTER_NAME	= "/thread-test";

    public static void main(String[] args) {
        RedisConfig redisConfig = new RateRedisConfig();
        RateLimiterConfig rateLimiterConfig = new CustomRateLimiterConfig();

        //singleLimiter(rateLimiterConfig);// 实例化单机限流对象

        DistributedLimiter(rateLimiterConfig, redisConfig); // 示例化分布式限流对象

        simulateConcurrentThread(); // 模拟并发线程
    }

    private static void simulateConcurrentThread() {
        DoThing dt = null;
        Thread t = null;
        for (int i = 0; i < 6; i++) {
            dt = new DoThing("Thread " + i);
            t = new Thread(dt);
            t.start();
        }
    }

    private static void singleLimiter(RateLimiterConfig rateLimiterConfig) {
        limiter = LimiterFactory.getInstance().single(rateLimiterConfig);
    }

    private static void DistributedLimiter(RateLimiterConfig rateLimiterConfig, RedisConfig redisConfig) {
        limiter = LimiterFactory.getInstance().distributed(rateLimiterConfig, redisConfig);
    }

    /**
     * 自定义线程，用于模拟并发
     */
    static class DoThing implements Runnable {
        String name;

        public DoThing(String name) {
            this.name = name;
        }

        @SuppressWarnings("static-access")
        @Override
        public void run() {
            try {
                for (int i = 0; i < 20; i++) {
                    if (!limiter.execute(ROUTER_NAME, 4, 1)) {// 进行限流控制
                        logger.info("Thread Name is [{}]，调用频率太高了.", name);
                        Thread.currentThread().sleep(1000);
                        continue;
                    }
                    maxNum++;
                    logger.info("Thread Name is [{}]，最新maxNum的值 = [" + maxNum + "]", name);
                }
            } catch (InterruptedException e) {
                logger.error("Thread Name [{}] is Error.", name, e);
            }
        }
    }
}
