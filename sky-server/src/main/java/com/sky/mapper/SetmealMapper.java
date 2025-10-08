package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;

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
     * 根据分类id查询已上架/或下架的套餐
     * status = 1 为上架，status = 0 为下架, null为全部
     * @param categoryId
     * @param status
     * @return
     */
    List<Setmeal> getSetmealsByCategoryId(Long categoryId, Integer status);

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
