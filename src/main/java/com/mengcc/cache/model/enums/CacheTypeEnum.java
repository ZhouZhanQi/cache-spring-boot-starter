package com.mengcc.cache.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhouzq
 * @version 1.0
 * @date 2020/4/12 2:34 上午
 * @desc 缓存类型枚举
 */
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public enum CacheTypeEnum {

    /**
     * redis缓存
     */
    REDIS("redis", "redis缓存"),

    /**
     * 本地缓存
     */
    LOCAL("local", "本地缓存");

    private String code;

    private String desc;
}
