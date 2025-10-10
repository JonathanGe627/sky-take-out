package com.sky.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class CacheUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public <T> void setCacheList(String key, List<T> dataList, long timeout, TimeUnit timeUnit){
        List<String> stringList = dataList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
        stringRedisTemplate.opsForList().rightPushAll(key, stringList);
        stringRedisTemplate.expire(key, timeout, timeUnit);
    }

    public <R> List<R> getCacheList(String key, Class<R> beanClass){
        List<String> stringList = stringRedisTemplate.opsForList().range(key, 0, -1);
        if (CollUtil.isEmpty(stringList)){
            return Collections.emptyList();
        }
        return stringList.stream().map(str -> JSONUtil.toBean(str, beanClass)).collect(Collectors.toList());
    }

    public void deleteCache(String key){
        stringRedisTemplate.delete(key);
    }

    public void deleteCache(Collection<String> keys){
        stringRedisTemplate.delete(keys);
    }

    public void deleteCachePattern(String pattern){
        Set<String> keys = stringRedisTemplate.keys(pattern + "*");
        if (CollUtil.isNotEmpty(keys)){
            stringRedisTemplate.delete(keys);
        }
    }
}
