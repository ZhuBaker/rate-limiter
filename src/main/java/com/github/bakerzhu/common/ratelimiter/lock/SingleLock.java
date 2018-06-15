package com.github.bakerzhu.common.ratelimiter.lock;

import com.github.bakerzhu.common.ratelimiter.bean.LockBean;
import com.github.bakerzhu.common.ratelimiter.config.RateLimiterConfig;
import com.github.bakerzhu.common.ratelimiter.constants.RateLimiterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月11日
 * @modifytime:
 */
public class SingleLock implements Lock {

    private final static Logger logger = LoggerFactory.getLogger(SingleLock.class);
    private static Map<String,Semaphore> lockMap = null;


    public SingleLock() {
    }

    public SingleLock(RateLimiterConfig rateLimiterConfig) {
        if(null == rateLimiterConfig) {
            rateLimiterConfig = new RateLimiterConfig();
        }
        initLockMap(rateLimiterConfig);
    }

    protected void initLockMap(RateLimiterConfig rateLimiterConfig) {
        if(null == lockMap) {
            lockMap = new HashMap<String,Semaphore>();
        }
        List<LockBean> lockList = rateLimiterConfig.getLockList();
        Semaphore semaphore = null;
        for(LockBean lockBean : lockList) {
            semaphore = new Semaphore(lockBean.getPermits());
            lockMap.put(lockBean.getUniqueKey(),semaphore);
            logger.debug("单机并发锁-加载并发配置>>>uniqueKey = [{}],time = [{}],count = [{}]",lockBean.getUniqueKey(), lockBean.getExpireTime(), lockBean.getPermits());
        }
    }

    /**
     * 加锁
     *
     * @param uniqueKey 加锁唯一键标识
     * @return 加锁/获取锁成功则返回true，否则返回false
     */
    @Override
    public boolean lock(String uniqueKey) {
        return lock(uniqueKey,RateLimiterConstants.LOCK_DEFAULT_EXPIRE_TIME);
    }

    /**
     * @param uniqueKey  加锁唯一键标识
     * @param expireTime 过期时间，单位秒
     * @return 加锁/获取锁成功则返回true，否则返回false
     */
    @Override
    public boolean lock(String uniqueKey, int expireTime) {
        return lock(uniqueKey, expireTime , RateLimiterConstants.LOCK_DEFAULT_PERMITS_NUM);
    }

    /**
     * @param uniqueKey  加锁唯一键标识
     * @param expireTime 过期时间，单位秒
     * @param permits    许可数量(最大并发量，默认为1)
     * @return 加锁/获取锁成功则返回true，否则返回false
     */
    @Override
    public boolean lock(String uniqueKey, int expireTime, int permits) {
        Semaphore semaphore = lockMap.get(uniqueKey);

        if (null != semaphore) {
            return semaphore.tryAcquire();
        }
        boolean isGetLock = dynamicAddLock(uniqueKey, expireTime, permits);

        return isGetLock;
    }


    public boolean dynamicAddLock(String uniqueKey , int expireTime , int permits) {
        synchronized (uniqueKey.intern()) {
            Semaphore semaphore = lockMap.get(uniqueKey);
            if (null != semaphore) {
                return semaphore.tryAcquire();
            }
            semaphore = new Semaphore(permits);
            lockMap.put(uniqueKey,semaphore);
            return semaphore.tryAcquire();
        }
    }

    /**
     * 释放锁
     * @param uniqueKey 加锁唯一键标识
     * @return
     */
    @Override
    public boolean releaseLock(String uniqueKey) {
        Semaphore semaphore = lockMap.get(uniqueKey);
        if (null != semaphore) {
            semaphore.release();
        }
        return true;
    }
}
