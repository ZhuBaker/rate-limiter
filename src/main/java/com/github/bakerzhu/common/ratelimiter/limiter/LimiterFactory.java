package com.github.bakerzhu.common.ratelimiter.limiter;

import com.github.bakerzhu.common.ratelimiter.base.AbstractBaseFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月14日
 * @modifytime:
 */
public class LimiterFactory extends AbstractBaseFactory<Limiter>{

    private static final LimiterFactory factory = new LimiterFactory();

    public LimiterFactory() {
        super(SingleLimiter.class, DistributedLimiter.class);
    }

    public static LimiterFactory getInstance() {
        return factory;
    }
}
