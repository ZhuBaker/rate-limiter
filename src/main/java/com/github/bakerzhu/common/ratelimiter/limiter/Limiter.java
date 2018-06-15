package com.github.bakerzhu.common.ratelimiter.limiter;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description: 限流接口类
 * @time: 2018年06月13日
 * @modifytime:
 */
public interface Limiter {


    /**
     * 执行限流控制，如果通过则返回true，如果不通过则返回false。
     *
     * @param routerName 路由名称
     * @return
     */
    boolean execute(String routerName);

    /**
     * 执行限流控制，如果通过则返回true，如果不通过则返回false。 如果限流规则不存在，则往规则集合中添加当前限流规则
     *
     * @param routerName 路由名称
     * @param limitCount 限流数量
     * @return
     */
    boolean execute(String routerName,int limitCount);

    /**
     * 执行限流控制，如果通过则返回true，如果不通过则返回false。 如果限流规则不存在，则往规则集合中添加当前限流规则
     *
     * @param routerName 路由名称
     * @param limitCount 限流数量
     * @param time  限流时间，单位是秒
     * @return
     */
    boolean execute(String routerName ,int limitCount ,int time);


}
