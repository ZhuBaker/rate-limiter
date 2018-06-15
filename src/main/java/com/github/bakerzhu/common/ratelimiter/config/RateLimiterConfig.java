package com.github.bakerzhu.common.ratelimiter.config;

import com.github.bakerzhu.common.ratelimiter.bean.LimiterBean;
import com.github.bakerzhu.common.ratelimiter.bean.LockBean;
import com.github.bakerzhu.common.ratelimiter.constants.RateLimiterConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月11日
 * @modifytime:
 */
public class RateLimiterConfig extends AbstractConfig {

    private final String FILE_NAME = RateLimiterConstants.RATE_LIMITER_CONFIG_FILE;

    private final String MAX_LOCK_TASK_COUNT_NAME       = "rate.limiter.lock.task.count";
    private final String LOCK_TASK_ROUTE_NAME_PER       = "rate.limiter.lock.router.";
    private final String LOCK_TASK_EXPIRE_TIME_PRE      = "rate.limiter.lock.expire.time.";
    private final String LOCK_TASK_PERMITS_COUNT_PRE    = "rate.limiter.lock.permits.count.";

    private final String MAX_LIMITER_TASK_COUNT_NAME    = "rate.limiter.limiter.task.count";
    private final String LIMITER_TASK_ROUTER_NAME_PRE   = "rate.limiter.limiter.router.";
    private final String LIMITER_TASK_TIMER_NAME_PRE    = "rate.limiter.limiter.time.";
    private final String LIMITER_TASK_COUNT_NAME_PRE    = "rate.limiter.limiter.count.";


    public RateLimiterConfig() {
        reloadConfig();
    }

    @Override
    public String getConfigFile() {
        return FILE_NAME;
    }

    /**
     * 从本地配置文件中加载并发配置列表 ， 并组装成 List<LockBean> 对象
     * @return
     */
    public List<LockBean> getLockList() {
        int defaultLockListSize = getPropertyToInt(MAX_LOCK_TASK_COUNT_NAME,RateLimiterConstants.MAX_RATE_LIMITER_LOCK_LIMIT);
        List<LockBean> lockList = null;
        LockBean lockBean = null;
        String lockUniqueKey = null;
        int expireTime ;
        int permitsCount;

        for (int i = 0 ; i < defaultLockListSize ; i++ ){
            // 并发控制路由地址
            lockUniqueKey = getProperty(LOCK_TASK_ROUTE_NAME_PER + (i + 1));
            if (null == lockUniqueKey) {
                continue;
            }
            lockBean = new LockBean();
            lockBean.setUniqueKey(lockUniqueKey);

            expireTime = getPropertyToInt(LOCK_TASK_EXPIRE_TIME_PRE + (i + 1) , RateLimiterConstants.LOCK_DEFAULT_EXPIRE_TIME );
            lockBean.setExpireTime(expireTime);

            permitsCount = getPropertyToInt(LOCK_TASK_PERMITS_COUNT_PRE + (i + 1) , RateLimiterConstants.LOCK_DEFAULT_PERMITS_NUM);
            lockBean.setPermits(permitsCount);

            if (null == lockList) {
                lockList = new ArrayList<LockBean>(defaultLockListSize);
            }
            lockList.add(lockBean);
        }
        return lockList;
    }


    public List<LimiterBean> getLimiterList() {
        int defaultLimiterListSize = getPropertyToInt(MAX_LIMITER_TASK_COUNT_NAME , RateLimiterConstants.MAX_RATE_LIMITER_LIMITER_LIMIT);
        List<LimiterBean> limiterList = null;
        LimiterBean limiterBean = null;
        String limiterRouter = null;
        int limiterTime;
        int limiterCount;

        for(int i = 0; i < defaultLimiterListSize  ; i ++) {
            limiterRouter = getProperty(LIMITER_TASK_ROUTER_NAME_PRE + (i + 1));
            if (null == limiterRouter) {
                continue;
            }
            limiterBean = new LimiterBean();
            limiterBean.setRouter(limiterRouter);

            limiterTime = getPropertyToInt(LIMITER_TASK_TIMER_NAME_PRE + (i + 1) , Integer.MAX_VALUE);
            limiterBean.setTime(limiterTime);

            limiterCount = getPropertyToInt(LIMITER_TASK_COUNT_NAME_PRE + (i + 1) , 1);
            limiterBean.setCount(limiterCount);

            if (null == limiterList) {
                limiterList = new ArrayList<LimiterBean>(defaultLimiterListSize);
            }
            limiterList.add(limiterBean);
        }
        return limiterList;
    }



}
