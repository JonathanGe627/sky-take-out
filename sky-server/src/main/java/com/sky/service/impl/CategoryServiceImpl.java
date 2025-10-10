package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.RedisConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.utils.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CacheUtil cacheUtil;

    /**
     * 添加分类
     * @param categoryDTO
     */
    @Override
    public void insertCategory(CategoryDTO categoryDTO) {
        Category category = BeanUtil.copyProperties(categoryDTO, Category.class);
        category.setStatus(StatusConstant.DISABLE);
        categoryMapper.insertCategory(category);
    }

    /**
     * 分页查询分类列表，支持根据分类名称模糊查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult<Category> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.page(categoryPageQueryDTO.getName(), categoryPageQueryDTO.getType());
        PageResult<Category> pageResult = new PageResult<>(page.getTotal(), page.getResult());
        return pageResult;
    }

    /**
     * 根据id删除分类
     * @param id
     */
    @Override
    public void deleteCategory(Long id) {
        // 1.如果分类关联了菜品,不能删除
        List<Dish> dishList = dishService.getDishesByCategoryId(id);
        if (CollUtil.isNotEmpty(dishList)){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        // 2.如果分类关联了套餐,不能删除
        List<Setmeal> setmealList = setmealService.getSetmealsByCategoryId(id);
        if (CollUtil.isNotEmpty(setmealList)){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        // 3.删除分类
        categoryMapper.deleteCategory(id);
        // 4.删除分类缓存
        cacheUtil.deleteCache(RedisConstant.CATEGORY_CACHE_KEY_PREFIX + 1);
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        Category category = BeanUtil.copyProperties(categoryDTO, Category.class);
        categoryMapper.updateCategory(category);
        cacheUtil.deleteCache(RedisConstant.CATEGORY_CACHE_KEY_PREFIX + 1);
    }

    /**
     * 启用/禁用分类
     * @param id
     * @param status
     */
    @Override
    public void updateCategoryStatus(Long id, Integer status) {
        Category category = Category.builder().id(id).status(status).build();
        categoryMapper.updateCategory(category);
        cacheUtil.deleteCache(RedisConstant.CATEGORY_CACHE_KEY_PREFIX + 1);
    }

    /**
     * 根据类型获取分类
     * @param type
     * @return
     */
    @Override
    public List<Category> list(Integer type) {
        List<Category> categoryList = categoryMapper.list(type, null);
        return categoryList;
    }

    /**
     * 根据类型获取启用/禁用状态的分类
     * status = 1 为启用，status = 0 为禁用
     * @param type
     * @param status
     * @return
     */
    @Override
    public List<Category> list(Integer type, Integer status) {
        // 1.先从缓存中查询数据
        List<Category> cacheList = cacheUtil.getCacheList(RedisConstant.CATEGORY_CACHE_KEY_PREFIX + status, Category.class);
        if (CollUtil.isNotEmpty(cacheList)){
            return cacheList;
        }
        // 2.缓存中没有数据，则从数据库中查询数据
        List<Category> categoryList = categoryMapper.list(type, status);
        // 3.将数据缓存
        cacheUtil.setCacheList(
                RedisConstant.CATEGORY_CACHE_KEY_PREFIX + status,
                categoryList,
                RedisConstant.CATEGORY_CACHE_TTL,
                TimeUnit.SECONDS);
        // 4.返回数据
        return categoryList;
    }
}
