package com.sky.service;

import com.sky.entity.SetmealDish;

import java.util.List;

public interface SetmealDishService {

    /**
     * 获取与菜品列表关联的套餐的数量
     * @param ids
     * @return
     */
    Integer getSetmealCountByDishIds(List<Long> ids);

    /**
     * 新增套餐和菜品的连接表
     * @param setmealDishList
     */
    void insertSetmealDish(List<SetmealDish> setmealDishList);

    /**
     * 删除套餐-菜品关联表
     * @param ids
     */
    void deleteSetmealdishes(List<Long> ids);

    /**
     * 修改套餐的菜品信息
     * @param setmealDishList
     */
    void updateSetmealDish(Long id, List<SetmealDish> setmealDishList);
}
