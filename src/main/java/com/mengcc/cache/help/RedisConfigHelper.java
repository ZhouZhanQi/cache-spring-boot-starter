package com.mengcc.cache.help;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.mengcc.cache.model.enums.CacheTypeEnum;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author zhouzq
 * @version 1.0
 * @date 2020/4/12 2:45 上午
 * @desc redis缓存配置类
 */
public class RedisConfigHelper {


    /**
     * yijian.cache.type的值是否正确设置, 有效值为: redis 或者 local
     * @param cacheType
     * @return
     */
    public static boolean isValidType(String cacheType) {
        return CacheTypeEnum.REDIS.getCode().equals(cacheType) || CacheTypeEnum.LOCAL.getCode().equals(cacheType);
    }


    /**
     * 获取缓存操作助手对象
     *
     * @return
     */
    public static RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory) {

        GenericJackson2JsonRedisSerializer serializer = newJsonRedisSerializer();
        //创建Redis缓存操作助手RedisTemplate对象
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        // 设置value的序列化规则
        // 默认的GenericJackson2JsonRedisSerializer对特殊的类型的反序列化会有问题，所以需要调整ObjectMapper的规则
        // 例如：List<LocalDateTime> 类型的反序列会出异常，无法正常反序列化
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);
        //默认的实例化规则
        redisTemplate.setDefaultSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    /**
     * 自定义json序列化配置的json序列化类
     * @return
     */
    public static GenericJackson2JsonRedisSerializer newJsonRedisSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        // 按照field来序列化, 忽略constructors/factory,setXXX()/getXXX()/isXXX()表示的属性
        mapper.setVisibility(mapper.getVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        // 序列化时带上Class<T>类型信息
        mapper.activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        // 字段值为默认值时(如: boolean的false, int的0, string/object的<null>), 忽略该字段, 减少序列化后的字节长度
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        // 日期序列化为long
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 反序列化时, 忽略不认识的字段, 而不是抛出异常
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return new GenericJackson2JsonRedisSerializer();
    }
}
