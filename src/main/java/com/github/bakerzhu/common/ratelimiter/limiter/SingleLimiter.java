package com.github.bakerzhu.common.ratelimiter.limiter;

import com.github.bakerzhu.common.ratelimiter.bean.LimiterBean;
import com.github.bakerzhu.common.ratelimiter.config.RateLimiterConfig;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月13日
 * @modifytime:
 */
public class SingleLimiter implements Limiter{

    private static final Logger logger = LoggerFactory.getLogger(SingleLimiter.class);

    private static Map<String,RateLimiter> rateLimiterMap = null;

    public SingleLimiter(RateLimiterConfig rateLimiterConfig) {
        if (null == rateLimiterConfig) {
            rateLimiterConfig = new RateLimiterConfig();
        }
        initRateLimiterMap(rateLimiterConfig);
    }


    /**
     *
     * <b>initRateLimiterMap</b> <br/>
     * <br/>
     *
     * 初始化限流配置<br/>
     * 初始化的限流配置在存储在Map中，支持后期动态添加。动态添加路由配置请参考 [method：dynamicAddRouter]
     *
     * @param rateLimiterConfig
     *            限流配置
     *
     */
    protected void initRateLimiterMap(RateLimiterConfig rateLimiterConfig) {
        if (null != rateLimiterMap)
            return;
        List<LimiterBean> limiterList = rateLimiterConfig.getLimiterList();
        rateLimiterMap = new ConcurrentHashMap<String, RateLimiter>();
        for (LimiterBean limiterBean : limiterList) {
            rateLimiterMap.put(limiterBean.getRouter(), RateLimiter.create(limiterBean.getCount() * 1.0 / limiterBean.getTime()));
            logger.debug("单机限流-加载限流配置>>>router = [{}],time = [{}],count = [{}]", limiterBean.getRouter(), limiterBean.getTime(), limiterBean.getCount());
        }
    }




    /**
     * 执行限流控制，如果通过则返回true，如果不通过则返回false。
     *
     * @param routerName 路由名称
     * @return
     */
    @Override
    public boolean execute(String routerName) {
        RateLimiter rateLimiter = rateLimiterMap.get(routerName);
        if(rateLimiter == null) {
            return true;
        }
        return rateLimiter.tryAcquire();

    }

    /**
     * 执行限流控制，如果通过则返回true，如果不通过则返回false。 如果限流规则不存在，则往规则集合中添加当前限流规则
     *
     * @param routerName 路由名称
     * @param limitCount 限流数量
     * @return
     */
    @Override
    public boolean execute(String routerName, int limitCount) {
        return execute(routerName, limitCount, Integer.MAX_VALUE);
    }

    /**
     * 执行限流控制，如果通过则返回true，如果不通过则返回false。 如果限流规则不存在，则往规则集合中添加当前限流规则
     *
     * @param routerName 路由名称
     * @param limitCount 限流数量
     * @param time       限流时间，单位是秒
     * @return
     */
    @Override
    public boolean execute(String routerName, int limitCount, int time) {
        RateLimiter rateLimiter = rateLimiterMap.get(routerName);
        if(null != rateLimiter){
            return rateLimiter.tryAcquire();
        }
        boolean isGetPermit = dynamicAddRouter(routerName, limitCount, time);
        return isGetPermit;
    }

    /**
     *
     * 当限流配置不存在的时候，需要进行动态限流控制
     * 当多个线程同事进行动态配置时会存在并发问题， 所以需要利用常量池特性 String.intern() 进行同一路由加锁设置
     *
     * @param routerName    路由名称
     * @param limitCount    限流数量
     * @param time          限流时间，单位是秒
     * @return
     */
    public boolean dynamicAddRouter(String routerName , int limitCount , int time ){
        synchronized (routerName.intern()) {
            RateLimiter rateLimiter = rateLimiterMap.get(routerName);
            if (null == rateLimiter) {
                rateLimiter = RateLimiter.create(limitCount * 1.0 / time);
                rateLimiterMap.put(routerName,rateLimiter);
                logger.info("单机限流-动态限流控制>>>router = [{}] , time = [{}] , count = [{}] " , routerName, time, limitCount);
            } else {
                logger.warn("(重复添加限流配置)>>>router = [{}] , time = [{}] , count = [{}] ",routerName,time,limitCount);
            }
            return rateLimiter.tryAcquire();
        }
    }


}
