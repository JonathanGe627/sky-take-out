package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    /**
     * 新增套餐
     * @param setmealDTO
     */
    void insertSetmeal(SetmealDTO setmealDTO);

    /**
     * 分页查询套餐，支持根据分类，启用状态，以及名称模糊查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult<SetmealVO> page(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteSetmeals(List<Long> ids);

    /**
     * 根据种类id查询套餐
     * @param categoryId
     * @return
     */
    List<Setmeal> getSetmealsByCategoryId(Long categoryId);

    /**
     * 根据种类id查询已上架/或下架的套餐
     * status = 1 为上架，status = 0 为下架
     * @param categoryId
     * @param status
     * @return
     */
    List<Setmeal> getSetmealsByCategoryId(Long categoryId, Integer status);

    /**
     * 修改套餐
     * @param setmealDTO
     */
    void updateSetmeal(SetmealDTO setmealDTO);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO getSetmealById(Long id);

    /**
     * 启售/停售套餐
     * @param id
     * @param status
     */
    void updateSetmealStatus(Long id, Integer status);

    /**
     * 根据套餐id查询包含的菜品
     * @param setmealId
     * @return
     */
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);
}
