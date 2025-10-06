package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {
    /**
     * 添加菜品
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void insertDish(Dish dish);

    /**
     * 菜品分页查询，支持根据菜品分类或菜品名称模糊查询
     * @param name
     * @param status
     * @param categoryId
     * @return
     */
    Page<DishVO> getDishList(String name, Integer status, Integer categoryId);

    /**
     * 根据id批量获取菜品列表
     * @param ids
     * @return
     */
    List<Dish> getDishListByIds(List<Long> ids);

    /**
     * 根据id集合删除对应的菜品
     * @param ids
     */
    void deleteDishes(List<Long> ids);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getDishById(Long id);

    /**
     *修改菜品
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    @Update("update dish set name=#{name}, category_id=#{categoryId}, price=#{price}, image=#{image}, description=#{description}, status=#{status}, update_time=#{updateTime}, update_user=#{updateUser} where id = #{id}")
    void updateDish(Dish dish);
}
