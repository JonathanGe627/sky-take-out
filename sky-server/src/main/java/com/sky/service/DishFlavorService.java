package com.sky.service;

import com.sky.entity.DishFlavor;

import java.util.List;

public interface DishFlavorService {

    /**
     * 批量添加菜品口味
     *
     * @param dishFlavorList
     */
    void insertDishFlavorList(List<DishFlavor> dishFlavorList);

    /**
     * 删除指定id菜品所对应的口味
     *
     * @param ids
     */
    void deleteDishFlavors(List<Long> ids);

    /**
     * 根据菜品id更新口味
     * @param id
     * @param dishFlavorList
     */
    void updateDishFlavor(Long id, List<DishFlavor> dishFlavorList);
}
