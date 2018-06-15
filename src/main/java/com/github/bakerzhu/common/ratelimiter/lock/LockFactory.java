package com.github.bakerzhu.common.ratelimiter.lock;

import com.github.bakerzhu.common.ratelimiter.base.AbstractBaseFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月11日
 * @modifytime:
 */
public class LockFactory extends AbstractBaseFactory<Lock>{

    private final static LockFactory factory = new LockFactory();

    public LockFactory() {
        super(SingleLock.class, DistributedLock.class);
    }

    public static LockFactory getInstance() {
        return factory;
    }
}
