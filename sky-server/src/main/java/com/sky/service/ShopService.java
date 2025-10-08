package com.sky.service;

public interface ShopService {

    /**
     * 修改店铺营业状态
     * @param status
     */
    void updateShopStatus(Integer status);

    /**
     * 获取店铺营业状态
     * @return
     */
    Integer getShopStatus();

}
