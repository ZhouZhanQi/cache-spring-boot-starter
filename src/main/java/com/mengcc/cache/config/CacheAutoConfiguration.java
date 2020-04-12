package com.mengcc.cache.config;

import com.mengcc.cache.help.RedisConfigHelper;
import com.mengcc.cache.storage.impl.RedisCacheStorage;
import com.mengcc.cache.model.enums.CacheTypeEnum;
import com.mengcc.cache.storage.CacheStorage;
import com.mengcc.cache.storage.impl.LocalMemoryCacheStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author zhouzq
 * @version 1.0
 * @date 2020/4/12 2:40 上午
 * @desc 自动装配类
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration implements InitializingBean, DisposableBean {


    @Autowired
    private CacheProperties cacheProperties;

    @Autowired(required = false)
    private LettuceConnectionFactory connectionFactory;


    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Bean
    @ConditionalOnMissingBean
    public CacheStorage cacheStorage() {
        if (StringUtils.isBlank(cacheProperties.getType())) {
            log.warn(">> yijian.cache.type未配置, 不启用缓存");
            return null;
        }
        if (!cacheProperties.isEnable()) {
            log.warn(">> yijian.cache.enable设置为false, 缓存关闭");
            return null;
        }

        if (CacheTypeEnum.LOCAL.getCode().equalsIgnoreCase(cacheProperties.getType())) {
            log.info(">> 使用本地内存作为缓存");
            return new LocalMemoryCacheStorage(cacheProperties.getPrefix());
        } else if (redisTemplate == null) {
            log.warn(">> redisTemplate为null, 启用redis缓存失败");
            return null;
        } else {
            log.info(">> 使用redis作为缓存");
            return new RedisCacheStorage(cacheProperties.getPrefix(), redisTemplate);
        }
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        return RedisConfigHelper.redisTemplate(connectionFactory);
    }


    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
