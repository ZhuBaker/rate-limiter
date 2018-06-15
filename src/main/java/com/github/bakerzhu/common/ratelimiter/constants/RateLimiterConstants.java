package com.github.bakerzhu.common.ratelimiter.constants;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description: 常量配置类
 * @time: 2018年06月11日
 * @modifytime:
 */
public class RateLimiterConstants {

    /**
     * 全局默认的配置文件名称
     */
    public static final String RATE_LIMITER_CONFIG_FILE         = "rate-limiter.properties";

    /**
     * 限流控制 - 默认的限流任务总数
     */
    public static final int MAX_RATE_LIMITER_LIMITER_LIMIT      = 10;

    /**
     * 限流控制 - 默认的并发控制任务总数 (并发 Lock 总数)
     */
    public static final int MAX_RATE_LIMITER_LOCK_LIMIT         = 10;

    /**
     * 并发锁 - 获取锁时默认获取许可数量
     */
    public static final int LOCK_DEFAULT_PERMITS_NUM            = 1;

    /**
     * 并发锁 - 默认的锁失效时间 ， 单位 秒
     */
    public static final int LOCK_DEFAULT_EXPIRE_TIME            = 3;

}
