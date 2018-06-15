package com.github.bakerzhu.common.ratelimiter.bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description: Limiter限流的JavaBean对象
 * 表示：在 ${time} 秒内允许通过 ${count} 次访问
 *
 * @time: 2018年06月11日
 * @modifytime:
 */
public class LimiterBean {

    private String router;

    private int    time;

    private int    count;

    /**
     * 限流的路由地址
     */
    public String getRouter() {
        return router;
    }

    public void setRouter(String router) {
        this.router = router;
    }

    /**
     * 限流时间，单位为 秒(s)
     */
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    /**
     * 限流数量
     */
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
