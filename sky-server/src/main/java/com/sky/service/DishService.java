package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 添加菜品
     * @param dishDTO
     */
    void insertDish(DishDTO dishDTO);

    /**
     * 菜品分页查询，支持根据菜品分类或菜品名称模糊查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id批量删除菜品
     * @param ids
     */
    void deleteDishes(List<Long> ids);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    DishVO getDishById(Long id);

    /**
     * updateDish
     * @param dishDTO
     */
    void updateDish(DishDTO dishDTO);

    /**
     * 启售/停售菜品
     * @param id
     * @param status
     */
    void updateDishStatus(Long id, Integer status);

    /**
     * 根据分类查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> getDishesByCategoryId(Long categoryId);

    /**
     * 根据分类查询已上架/或下架的菜品及口味
     * status = 1 为上架，status = 0 为下架
     * @param categoryId
     * @param status
     * @return
     */
    List<DishVO> getDishListByCategoryId(Long categoryId, Integer status);

    /**
     * 根据套餐id查询包含的菜品
     * @param setmealId
     * @return
     */
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);
}
