package com.github.bakerzhu.common.ratelimiter.base;

import com.github.bakerzhu.common.ratelimiter.config.RateLimiterConfig;
import com.github.bakerzhu.common.ratelimiter.constants.RateLimiterConstants;
import com.github.bakerzhu.common.ratelimiter.utils.ProxyHelper;
import com.github.bakerzhu.commons.config.RedisConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月11日
 * @modifytime:
 */
public abstract class AbstractBaseFactory<T> {

    /**
     * 单机实现 实例集合
     */
    private static Map<String, Object> singleMap		 = new HashMap<String, Object>();

    /**
     * 分布式实现 实例集合
     */
    private static Map<String, Object> distributedMap = new HashMap<String, Object>();

    private Class<T>				   singleClass;
    private Class<T>				   distributedClass;

    @SuppressWarnings("unchecked")
    public AbstractBaseFactory(Class<?> singleClass, Class<?> distributedClass) {
        this.singleClass = (Class<T>) singleClass;
        this.distributedClass = (Class<T>) distributedClass;
    }

    @SuppressWarnings("hiding")
    public <T> T single() {
        return single(null);
    }

    @SuppressWarnings({ "unchecked", "hiding" })
    public <T> T single(RateLimiterConfig rateLimiterConfig) {
        T limiter = null;
        if (null == rateLimiterConfig) {// 如果配置变量为空，则启用默认配置，默认配置需要依赖:rate-limiter.properties文件
            String rateLimiterDefaultConfigName = RateLimiterConstants.RATE_LIMITER_CONFIG_FILE;
            limiter = (T) singleMap.get(rateLimiterDefaultConfigName);
            if (null != limiter) {
                return limiter;
            }
            limiter = (T) ProxyHelper.getInstance(singleClass);

            singleMap.put(rateLimiterDefaultConfigName, limiter);
            return limiter;
        }

        limiter = (T) singleMap.get(rateLimiterConfig.getConfigFile());
        if (null != limiter) {
            return limiter;
        }
        limiter = (T) ProxyHelper.getInstance(singleClass, new Class[] { RateLimiterConfig.class }, new Object[] { rateLimiterConfig });
        singleMap.put(rateLimiterConfig.getConfigFile(), limiter);
        return limiter;
    }


    @SuppressWarnings("hiding")
    public <T> T distributed() {
        return distributed(null);
    }

    @SuppressWarnings("hiding")
    public <T> T distributed(RateLimiterConfig rateLimiterConfig) {
        return distributed(rateLimiterConfig, null);
    }


    @SuppressWarnings({ "unchecked", "hiding" })
    public <T> T distributed(RateLimiterConfig rateLimiterConfig, RedisConfig redisConfig) {
        T limiter = null;
        if (null == rateLimiterConfig) {// 如果配置变量为空，则启用默认配置，默认配置需要依赖:rate-limiter.properties文件
            String rateLimiterDefaultConfigName = RateLimiterConstants.RATE_LIMITER_CONFIG_FILE;
            limiter = (T) distributedMap.get(rateLimiterDefaultConfigName);
            if (null != limiter) {
                return limiter;
            }
            limiter = (T) ProxyHelper.getInstance(distributedClass);
            distributedMap.put(rateLimiterDefaultConfigName, limiter);
            return limiter;
        }

        limiter = (T) distributedMap.get(rateLimiterConfig.getConfigFile());
        if (null != limiter) {
            return limiter;
        }
        limiter = (T) ProxyHelper.getInstance(distributedClass, new Class[] { RateLimiterConfig.class, RedisConfig.class }, new Object[] { rateLimiterConfig, redisConfig });
        distributedMap.put(rateLimiterConfig.getConfigFile(), limiter);
        return limiter;
    }


}
