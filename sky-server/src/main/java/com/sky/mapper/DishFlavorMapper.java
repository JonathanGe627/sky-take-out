package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量添加菜品口味
     * @param dishFlavorList
     */
    void insertDishFlavorList(List<DishFlavor> dishFlavorList);

    /**
     * 删除指定id菜品所对应的口味
     * @param ids
     */
    void deleteDishFlavors(List<Long> ids);

    /**
     * 根据菜品id查询所对应的口味
     * @param id
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getDishFlavorByDishId(Long id);
}
