package com.github.bakerzhu.common.ratelimiter.lock;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月11日
 * @modifytime:
 */
public interface Lock {


    /**
     * 加锁
     * @param uniqueKey  加锁唯一键标识
     * @return  加锁/获取锁成功则返回true，否则返回false
     */
    boolean lock(String uniqueKey);

    /**
     *
     * @param uniqueKey  加锁唯一键标识
     * @param expireTime  过期时间，单位秒
     *
     * @return  加锁/获取锁成功则返回true，否则返回false
     */
    boolean lock(String uniqueKey , int expireTime);

    /**
     *
     * @param uniqueKey  加锁唯一键标识
     * @param expireTime  过期时间，单位秒
     * @param permits   许可数量(最大并发量，默认为1)
     * @return  加锁/获取锁成功则返回true，否则返回false
     */
    boolean lock(String uniqueKey , int expireTime , int permits);

    /**
     * 释放锁
     * @param uniqueKey  加锁唯一键标识
     * @return
     */
    boolean releaseLock(String uniqueKey);



}
