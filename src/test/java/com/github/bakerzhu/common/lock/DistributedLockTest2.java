package com.github.bakerzhu.common.lock;

import com.github.bakerzhu.common.config.CustomRateLimiterConfig;
import com.github.bakerzhu.common.config.RateRedisConfig;
import com.github.bakerzhu.common.ratelimiter.config.RateLimiterConfig;
import com.github.bakerzhu.common.ratelimiter.lock.Lock;
import com.github.bakerzhu.common.ratelimiter.lock.LockFactory;
import com.github.bakerzhu.commons.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月12日
 * @modifytime:
 */
public class DistributedLockTest2 {

    private final static Logger logger	   = LoggerFactory.getLogger(DistributedLockTest2.class);
    private static String		UNIQUE_KEY = "/lock1";
    private static Lock lock	   = null;
    private volatile static int	successNum = 0;

    public static void main(String[] args) {
        RateLimiterConfig rateLimiterConfig = new CustomRateLimiterConfig();
        RedisConfig redisConfig = new RateRedisConfig();
        lock = getDistributedLock(rateLimiterConfig,redisConfig);
        simulateConcurrentThread(100);
    }



    private static Lock getSingleLock(RateLimiterConfig rateLimiterConfig) {
        return LockFactory.getInstance().single(rateLimiterConfig);
    }

    public static Lock getDistributedLock(RateLimiterConfig rateLimiterConfig , RedisConfig redisConfig) {
        return LockFactory.getInstance().distributed(rateLimiterConfig, redisConfig);
    }


    private static void simulateConcurrentThread(int threadNum) {
        DoThing dt = null;
        Thread t = null;
        for (int i = 0; i < threadNum; i++) {
            dt = new DoThing("Thread " + i);
            t = new Thread(dt);
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }// 模拟程序执行时间
            t.start();
        }
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
                if (lock.lock(UNIQUE_KEY)) {  // 进行并发控制

                    logger.info("Thread Name is [{}] 成功获得锁，正在处理中...", name);

                    successNum++;
                    logger.info("当前成功并发数successNum的值为 [" + successNum + "]");
                    Thread.currentThread().sleep(2000);// 模拟程序执行时间

                    successNum--;
                    lock.releaseLock(UNIQUE_KEY);
                }
                else {
                    logger.warn("Thread Name is [{}] 尝试获得锁失败", name);
                }
            }
            catch (InterruptedException e) {
                logger.error("Thread Name [{}] is Error.", name, e);
            }
        }
    }



}
