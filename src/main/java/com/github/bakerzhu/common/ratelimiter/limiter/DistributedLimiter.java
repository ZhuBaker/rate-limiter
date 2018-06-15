package com.github.bakerzhu.common.ratelimiter.limiter;

import com.github.bakerzhu.common.ratelimiter.bean.LimiterBean;
import com.github.bakerzhu.common.ratelimiter.bean.LockBean;
import com.github.bakerzhu.common.ratelimiter.config.RateLimiterConfig;
import com.github.bakerzhu.common.ratelimiter.lock.DistributedLock;
import com.github.bakerzhu.commons.config.RedisConfig;
import com.github.bakerzhu.commons.core.RedisClient;
import com.github.bakerzhu.commons.core.RedisClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月14日
 * @modifytime:
 */
public class DistributedLimiter implements Limiter{

    private static final Logger logger = LoggerFactory.getLogger(DistributedLimiter.class);

    private static RedisClient<Jedis> redisClient = null;
    private static Map<String,LimiterBean> limiterBeanMap = null;


    public DistributedLimiter() {
        new DistributedLimiter(null,null);
    }

    public DistributedLimiter(RateLimiterConfig rateLimiterConfig , RedisConfig redisConfig) {
        if(null == rateLimiterConfig) {
            rateLimiterConfig = new RateLimiterConfig();
        }
        initLimiterConfig(rateLimiterConfig, redisConfig);
    }

    protected void initLimiterConfig(RateLimiterConfig rateLimiterConfig , RedisConfig redisConfig) {
        if (null != redisClient) {
            return ;
        }
        redisClient = RedisClientFactory.getClient(redisConfig);
        limiterBeanMap = new HashMap<String,LimiterBean>();
        List<LimiterBean> limiterList = rateLimiterConfig.getLimiterList();
        for (LimiterBean limiterBean : limiterList) {
            redisClient.setnx(limiterBean.getRouter(),"",limiterBean.getTime());
            limiterBeanMap.put(limiterBean.getRouter(),limiterBean);
            logger.debug("分布式限流-加载限流配置>>>router = [{}] , time = [{}] , count = [{}] " ,limiterBean.getRouter(),limiterBean.getTime() , limiterBean.getCount());
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
        LimiterBean limiterBean = limiterBeanMap.get(routerName);
        if(null == limiterBean) {  // 没有相关配置则直接返回成功
            return true;
        }
        int limiterCount = limiterBean.getCount();
        // redisClient.setnx(limiterBean.getRouter(),"0",limiterBean.getTime());
        // 调用上面代码会每次设置不成功也会重置redis过期时间，会存在问题
        Jedis jedis = redisClient.getJedis();
        Long result = jedis.setnx(limiterBean.getRouter(), "0");
        if(result == 1) { // 设置成功
            jedis.expire(limiterBean.getRouter(),limiterBean.getTime());
        }
        long currentCount = redisClient.incr(routerName);
        if (currentCount > limiterCount) {   // 如果超过限流值，则直接返回false
            return false;
        }
        return true;
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
        LimiterBean limiterBean = limiterBeanMap.get(routerName);
        if (null == limiterBean) {
            limiterBean = new LimiterBean();
            limiterBean.setRouter(routerName);
            limiterBean.setCount(limitCount);
            limiterBean.setTime(time);
            limiterBeanMap.put(routerName,limiterBean);
            logger.debug("分布式限流-动态限流配置>>>router = [{}],time = [{}],count = [{}]" , routerName,time,limitCount);
        }
        return execute(routerName);
    }
}
