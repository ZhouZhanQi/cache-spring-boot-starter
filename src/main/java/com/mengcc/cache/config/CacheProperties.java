package com.mengcc.cache.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mengcc.cache.model.enums.CacheTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhouzq
 * @version 1.0
 * @date 2020/4/12 2:28 上午
 * @desc 缓存配置
 */
@Data
@ConfigurationProperties(prefix = "mengcc.cache")
public class CacheProperties {

    private String type = CacheTypeEnum.REDIS.getCode();

    private String prefix = "";

    private boolean enable = false;

    @JsonProperty("clear-on-startup")
    private boolean clearOnStartup = false;
}
