package com.github.bakerzhu.common.ratelimiter.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description: Limiter并发锁的JavaBean对象
 *
 * @time: 2018年06月11日
 * @modifytime:
 */
public class LockBean {

    private String  uniqueKey ;

    private int     expireTime ;

    private int     permits;

    /**
     * 加锁唯一键标识
     */
    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    /**
     * 过期时间，单位秒
     */
    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * 许可数量（最大并发量，默认1）
     */
    public int getPermits() {
        return permits;
    }

    public void setPermits(int permits) {
        this.permits = permits;
    }
}
