package com.sky.constant;

/**
 * Redis相关常量
 */
public class RedisConstant {
    public static final String SHOP_STATUS_KEY = "shop:status";
    public static final String CATEGORY_CACHE_KEY_PREFIX = "category:status:";
    public static final Long CATEGORY_CACHE_TTL = 604800L;
    public static final String DISH_CACHE_KEY_PREFIX = "dish:category:";
    public static final Long DISH_CACHE_TTL = 604800L;
    public static final String SETMEAL_CACHE_KEY_PREFIX = "setmeal:category:";
    public static final Long SETMEAL_CACHE_TTL = 604800L;
    public static final String DISH_SETMEAL_CACHE_KEY_PREFIX = "dish:setmeal:";
    public static final Long DISH_SETMEAL_CACHE_TTL = 604800L;

}
