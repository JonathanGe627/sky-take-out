package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 添加菜品
     *
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void insertDish(Dish dish);

    /**
     * 菜品分页查询，支持根据菜品分类或菜品名称模糊查询
     *
     * @param name
     * @param status
     * @param categoryId
     * @return
     */
    Page<DishVO> page(String name, Integer status, Integer categoryId);

    /**
     * 根据id批量查询菜品列表
     *
     * @param ids
     * @return
     */
    List<DishVO> getDishListByIds(List<Long> ids);

    /**
     * 根据分类id查询已上架的菜品列表
     *
     * @param categoryId
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId} and status = 1")
    List<Dish> getDishesByCategoryId(Long categoryId);

    /**
     * 根据id集合删除对应的菜品
     *
     * @param ids
     */
    void deleteDishes(List<Long> ids);

    /**
     * 修改菜品
     *
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void updateDish(Dish dish);

    /**
     * 根据分类查询已上架/或下架的菜品及口味
     * status = 1 为上架，status = 0 为下架, null为全部
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

    /**
     * 启售/停售菜品
     * @param id
     * @param status
     */
    @Update("update dish set status = #{status} where id = #{id}")
    void updateDishStatus(Long id, Integer status);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
