package com.github.bakerzhu.common.base;

import com.github.bakerzhu.common.config.CustomRateLimiterConfig;
import com.github.bakerzhu.common.config.RateRedisConfig;
import com.github.bakerzhu.common.ratelimiter.App;
import com.github.bakerzhu.common.ratelimiter.lock.DistributedLock;
import com.github.bakerzhu.common.ratelimiter.lock.Lock;
import com.github.bakerzhu.common.ratelimiter.lock.LockFactory;
import com.github.bakerzhu.commons.config.RedisConfig;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月11日
 * @modifytime:
 */
public class DistributedLockTest {

    @Test
    public void test() {
        DistributedLock lock = new DistributedLock();
        System.out.println(lock);
    }


    @Test
    public void test2() {
        LockFactory instance = LockFactory.getInstance();
        CustomRateLimiterConfig limitConfig = new CustomRateLimiterConfig();
        RedisConfig redisConfig = new RateRedisConfig();
        Lock distributed = instance.distributed(limitConfig, redisConfig);
        System.out.println(distributed.getClass());

    }
}
