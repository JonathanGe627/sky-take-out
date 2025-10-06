package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 获取与菜品列表关联的套餐的数量
     * @param ids
     * @return
     */
    Integer getSetmealCountByDishIds(List<Long> ids);
}
