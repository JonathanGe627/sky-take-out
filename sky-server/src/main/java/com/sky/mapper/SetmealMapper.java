package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 新增套餐
     *
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    void insertSetmeal(Setmeal setmeal);

    /**
     * 分页查询套餐，支持根据分类，启用状态，以及名称模糊查询
     *
     * @param name
     * @param categoryId
     * @param status
     * @return
     */
    Page<SetmealVO> page(String name, Integer categoryId, Integer status);

    /**
     * 根据id查询套餐
     *
     * @param ids
     * @return
     */
    List<SetmealVO> getSetmealsByIds(List<Long> ids);

    /**
     * 根据种类id查询套餐
     *
     * @param id
     * @return
     */
    @Select("select * from setmeal where category_id = #{id}")
    List<Setmeal> getSetmealsByCategoryId(Long id);

    /**
     * 删除套餐
     *
     * @param ids
     */
    void deleteSetmeals(List<Long> ids);

    /**
     * 更新套餐信息
     *
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void updateSetmeal(Setmeal setmeal);
}
