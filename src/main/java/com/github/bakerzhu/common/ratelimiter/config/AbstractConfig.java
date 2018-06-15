package com.github.bakerzhu.common.ratelimiter.config;

import com.github.bakerzhu.commons.config.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月11日
 * @modifytime:
 */
public abstract class AbstractConfig {
    private static Logger logger            = LoggerFactory.getLogger(AbstractConfig.class);
    private Properties    configProperties  = null;


    public synchronized boolean reloadConfig() {
        logger.debug("Base config reloadConfig().");
        if (configProperties != null) {
            configProperties.clear();
        }
        String fileName = getConfigFile();
        configProperties = FileUtils.loadStaticProperties(fileName);
        if (configProperties == null) {
            return false;
        }
        return true;
    }

    public String getProperty(String key) {
        if(configProperties == null) {
            return null;
        }
        return configProperties.getProperty(key);
    }

    public String getProperty(String key , String defaultV) {
        String strValue = defaultV;
        String temp = getProperty(key);
        if(StringUtils.isNotBlank(temp)) {
            strValue = temp;
        }
        return strValue;
    }

    public int getPropertyToInt(String key , int defaultV) {
        int intValue = defaultV;
        String temp = getProperty(key);
        try {
            intValue = Integer.parseInt(temp);
        }catch (NumberFormatException e) {
        }
        return intValue;
    }

    public abstract String getConfigFile();

}
