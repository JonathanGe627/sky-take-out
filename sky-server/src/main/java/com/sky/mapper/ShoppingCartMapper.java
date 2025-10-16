package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 根据用户id和菜品id（或套餐id）查询购物车数据
     * @param shoppingCart
     * @return
     */
    ShoppingCart getShoppingCartByUserIdAndDishIdOrSetmealId(ShoppingCart shoppingCart);

    /**
     * 更新购物车数据
     * @param shoppingCart
     */
    void updateShoppingCart(ShoppingCart shoppingCart);

    /**
     * 插入新的购物车数据
     * @param shoppingCart
     */
    void insertIntoShoppingCart(ShoppingCart shoppingCart);

    /**
     * 根据userId获取购物车数据
     * @param userId
     * @return
     */
    List<ShoppingCart> getShoppingCartList(Long userId);

    /**
     * 根据userId删除购物车数据
     * @param userId
     */
    void deleteShoppingCartByUserId(Long userId);

    /**
     * 根据id删除ShoppingCart数据
     * @param id
     */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteShoppingCartById(Long id);

    /**
     * 将菜品或套餐批量添加到购物车
     * @param shoppingCartList
     */
    void insertIntoShoppingCartBatch(List<ShoppingCart> shoppingCartList);
}
