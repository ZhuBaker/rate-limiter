package com.github.bakerzhu.common.config;

import com.github.bakerzhu.common.ratelimiter.config.RateLimiterConfig;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月11日
 * @modifytime:
 */
public class CustomRateLimiterConfig extends RateLimiterConfig {

    private final String CONFIG_FILE = "test-rate-limiter.properties";

    @Override
    public String getConfigFile() {
        return CONFIG_FILE;
    }
}
