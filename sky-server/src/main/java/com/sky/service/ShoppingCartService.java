package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加菜品或套餐到购物车
     * @param shoppingCartDTO
     */
    void insertIntoShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 获取当前登录用户的购物车数据
     * @return
     */
    List<ShoppingCart> getShoppingCartList();

    /**
     * 清空当前登录用户的购物车数据
     */
    void cleanShoppingCart();

    /**
     * 购物车中的商品数量减一
     * @param shoppingCartDTO
     */
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
