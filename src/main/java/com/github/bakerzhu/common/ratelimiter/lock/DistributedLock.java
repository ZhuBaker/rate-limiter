package com.github.bakerzhu.common.ratelimiter.lock;

import com.github.bakerzhu.common.ratelimiter.bean.LockBean;
import com.github.bakerzhu.common.ratelimiter.config.RateLimiterConfig;
import com.github.bakerzhu.common.ratelimiter.constants.RateLimiterConstants;
import com.github.bakerzhu.commons.config.RedisConfig;
import com.github.bakerzhu.commons.core.RedisClient;
import com.github.bakerzhu.commons.core.RedisClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description: 分布式锁 实现
 * @time: 2018年06月11日
 * @modifytime:
 */
public class DistributedLock implements Lock {

    private final static Logger             logger		= LoggerFactory.getLogger(DistributedLock.class);
    private static RedisClient<?>           redisClient = null;
    private static Map<String, LockBean>    lockBeanMap = null;


    public DistributedLock() {
        this(null, null);
    }

    public DistributedLock(RateLimiterConfig rateLimiterConfig , RedisConfig redisConfig) {

        if(null == rateLimiterConfig) {
            rateLimiterConfig = new RateLimiterConfig();
        }
        initLockConfig(rateLimiterConfig, redisConfig);
    }

    protected void initLockConfig(RateLimiterConfig rateLimiterConfig , RedisConfig redisConfig) {
        // 当redisClient不为空，意味着限流配置已经初始化到缓存中
        if (null != redisClient) {
            return ;
        }
        redisClient = RedisClientFactory.getClient(redisConfig);
        lockBeanMap = new HashMap<String, LockBean>();

        List<LockBean> lockList = rateLimiterConfig.getLockList();
        for(LockBean lockBean : lockList) {
            logger.debug("分布式并发锁-加载并发配置>>>uniqueKey = [{}],time = [{}],count = [{}]", lockBean.getUniqueKey(), lockBean.getExpireTime(), lockBean.getPermits());
            redisClient.setnx(lockBean.getUniqueKey(),"0",lockBean.getExpireTime());
            lockBeanMap.put(lockBean.getUniqueKey(),lockBean);
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
        return lock(uniqueKey, RateLimiterConstants.LOCK_DEFAULT_EXPIRE_TIME);
    }

    /**
     * @param uniqueKey  加锁唯一键标识
     * @param expireTime 过期时间，单位秒
     * @return 加锁/获取锁成功则返回true，否则返回false
     */
    @Override
    public boolean lock(String uniqueKey, int expireTime) {
        return lock(uniqueKey, expireTime, RateLimiterConstants.LOCK_DEFAULT_PERMITS_NUM);
    }

    /**
     *
     * 我们可以在方法级别的粒度控制到某个服务同时间最多不能超过XXX次请求被调用
     *
     * @param uniqueKey  加锁唯一键标识
     * @param expireTime 过期时间，单位秒
     * @param permits    许可数量(最大并发量，默认为1)
     * @return 加锁/获取锁成功则返回true，否则返回false
     */
    @Override
    public boolean lock(String uniqueKey, int expireTime, int permits) {
        // 如果有锁对象 则直接返回 如果没有则创建一个新锁，放入 map 然后返回新锁对象
        LockBean lockBean = getLockBean(uniqueKey, expireTime, permits);
        expireTime = lockBean.getExpireTime();
        permits = lockBean.getPermits();
        // 为 uniqueKey 设置一个初始值, 返回 1获取成功 0获取失败
        long result = redisClient.setnx(uniqueKey, "0", lockBean.getExpireTime());
        result = redisClient.incr(uniqueKey);

        // 访问请求数 大于 许可数
        if (result > permits) {
            redisClient.decr(uniqueKey);
            logger.warn("并发锁，检测到当前KEY = [{}] , VALUE = [{}] , permits = [{}]，超过许可范围，因而获取锁失败.", uniqueKey, result, permits);
            return false;
        }

        if (result > 0 && result <= permits) {
            logger.info("并发锁，检测到当前KEY = [{}] , VALUE = [{}] , permits = [{}]，满足许可范围，因为成功获得锁.", uniqueKey, result, permits);
            return true;
        }

        logger.debug("并发锁，检测到当前KEY = [{}] , VALUE = [{}] , permits = [{}]，异常逻辑导致获得锁失败.", uniqueKey, result, permits);
        return false;
    }


    private LockBean getLockBean(String uniqueKey , int expireTime  , int permits) {
        LockBean lockBean = lockBeanMap.get(uniqueKey);
        if (null == lockBean) {
            lockBean = new LockBean();
            lockBean.setUniqueKey(uniqueKey);
            lockBean.setExpireTime(expireTime);
            lockBean.setPermits(permits);
            lockBeanMap.put(uniqueKey,lockBean);
        }
        return lockBean;
    }

    /**
     * 释放锁
     *
     * @param uniqueKey 加锁唯一键标识
     * @return
     */
    @Override
    public boolean releaseLock(String uniqueKey) {
        LockBean lockBean = lockBeanMap.get(uniqueKey);
        if (null == lockBean) {
            // TODO 抛出异常
            return false;
        }
        /**
         * 0 失败 说明存在锁占用
         * 1 成功 说明不存在锁占用
         */
        long result = redisClient.setnx(uniqueKey, "0", lockBean.getExpireTime());
        if (result == 0) {
            redisClient.decr(uniqueKey);
        }
        return true;
    }
}
