package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {

    /**
     * 添加分类
     * @param categoryDTO
     */
    void insertCategory(CategoryDTO categoryDTO);

    /**
     * 分页查询分类列表，支持根据分类名称模糊查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult<Category> page(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除分类
     * @param id
     */
    void deleteCategory(Long id);

    /**
     * 修改分类
     * @param categoryDTO
     */
    void updateCategory(CategoryDTO categoryDTO);

    /**
     * 启用/禁用分类
     * @param id
     * @param status
     */
    void updateCategoryStatus(Long id, Integer status);

    /**
     * 根据类型获取分类
     * @param type
     * @return
     */
    List<Category> list(Integer type);

    /**
     * 根据类型获取启用/禁用状态的分类
     * status = 1 为启用，status = 0 为禁用
     * @param type
     * @param status
     * @return
     */
    List<Category> list(Integer type, Integer status);
}
