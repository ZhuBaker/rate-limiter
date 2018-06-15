package com.github.bakerzhu.common.config;

import com.github.bakerzhu.commons.config.RedisConfig;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月11日
 * @modifytime:
 */
public class RateRedisConfig extends RedisConfig{

    private final String CONFIG_FILE = "test-redis_config.properties";

    @Override
    public String getConfigFile() {
        return CONFIG_FILE;
    }

}
