package com.sky.service.impl;

import com.sky.constant.RedisConstant;
import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 修改店铺营业状态
     * @param status
     */
    @Override
    public void updateShopStatus(Integer status) {
        stringRedisTemplate.opsForValue().set(RedisConstant.SHOP_STATUS_KEY, String.valueOf(status));
    }

    /**
     * 获取店铺营业状态
     * @return
     */
    @Override
    public Integer getShopStatus() {
        String statusStr = stringRedisTemplate.opsForValue().get(RedisConstant.SHOP_STATUS_KEY);
        return Integer.parseInt(statusStr);
    }
}
