package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.RedisConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.service.SetmealDishService;
import com.sky.service.SetmealService;
import com.sky.utils.CacheUtil;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CacheUtil cacheUtil;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void insertSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = BeanUtil.copyProperties(setmealDTO, Setmeal.class);
        setmealMapper.insertSetmeal(setmeal);
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        setmealDishList.forEach(setmealDish -> setmealDish.setSetmealId(setmeal.getId()));
        setmealDishService.insertSetmealDish(setmealDishList);
        cacheUtil.deleteCache(RedisConstant.SETMEAL_CACHE_KEY_PREFIX + setmealDTO.getCategoryId());
    }

    /**
     * 分页查询套餐，支持根据分类，启用状态，以及名称模糊查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult<SetmealVO> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.page(setmealPageQueryDTO.getName(), setmealPageQueryDTO.getCategoryId(), setmealPageQueryDTO.getStatus());
        PageResult<SetmealVO> pageResult = new PageResult<>(page.getTotal(), page.getResult());
        return pageResult;
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Transactional
    @Override
    public void deleteSetmeals(List<Long> ids) {
        // 1.查询当前套餐状态，并构建缓存key
        List<SetmealVO> setmealVOList = setmealMapper.getSetmealsByIds(ids);
        Set<String> setmealCacheSet = new HashSet<>();
        Set<String> setmealDishCacheSet = new HashSet<>();
        boolean flag = true;
        for (SetmealVO setmealVO : setmealVOList) {
            setmealCacheSet.add(RedisConstant.SETMEAL_CACHE_KEY_PREFIX + setmealVO.getCategoryId());
            setmealDishCacheSet.add(RedisConstant.DISH_SETMEAL_CACHE_KEY_PREFIX + setmealVO.getId());
            if (setmealVO.getStatus() == 1){
                flag = false;
                break;
            }
        }
        if (!flag){
            // 2.若套餐状态为启售，则无法删除
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        // 3.删除套餐
        setmealMapper.deleteSetmeals(ids);
        // 4.删除套餐-菜品关联表
        setmealDishService.deleteSetmealdishes(ids);
        // 5.删除缓存
        cacheUtil.deleteCache(setmealCacheSet);
        cacheUtil.deleteCache(setmealDishCacheSet);
    }

    /**
     * 根据种类id查询套餐
     * @param categoryId
     * @return
     */
    @Override
    public List<Setmeal> getSetmealsByCategoryId(Long categoryId) {
        List<Setmeal> setmealList = setmealMapper.getSetmealsByCategoryId(categoryId, null);
        return setmealList;
    }

    /**
     * 根据种类id查询已上架/或下架的套餐
     * status = 1 为上架，status = 0 为下架
     * @param categoryId
     * @param status
     * @return
     */
    @Override
    public List<Setmeal> getSetmealsByCategoryId(Long categoryId, Integer status) {
        List<Setmeal> cacheList = cacheUtil.getCacheList(
                RedisConstant.SETMEAL_CACHE_KEY_PREFIX + categoryId,
                Setmeal.class);
        if (CollUtil.isNotEmpty(cacheList)){
            return cacheList;
        }
        List<Setmeal> setmealList = setmealMapper.getSetmealsByCategoryId(categoryId, status);
        cacheUtil.setCacheList(
                RedisConstant.SETMEAL_CACHE_KEY_PREFIX + categoryId,
                setmealList,
                RedisConstant.SETMEAL_CACHE_TTL,
                TimeUnit.SECONDS);
        return setmealList;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void updateSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = BeanUtil.copyProperties(setmealDTO, Setmeal.class);
        setmealMapper.updateSetmeal(setmeal);
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        setmealDishService.updateSetmealDish(setmeal.getId(),setmealDishList);
        cacheUtil.deleteCachePattern(RedisConstant.SETMEAL_CACHE_KEY_PREFIX);
        cacheUtil.deleteCache(RedisConstant.DISH_SETMEAL_CACHE_KEY_PREFIX + setmealDTO.getId());
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getSetmealVOById(Long id) {
        ArrayList<Long> idList = new ArrayList<>(1);
        idList.add(id);
        List<SetmealVO> setmealVOList = setmealMapper.getSetmealsByIds(idList);
        return setmealVOList.get(0);
    }

    /**
     * 启售/停售套餐
     * @param id
     * @param status
     */
    @Override
    public void updateSetmealStatus(Long id, Integer status) {
        setmealMapper.updateSetmealStatus(id, status);
        cacheUtil.deleteCachePattern(RedisConstant.SETMEAL_CACHE_KEY_PREFIX);
    }

    /**
     * 根据套餐id查询包含的菜品
     * @param setmealId
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemBySetmealId(Long setmealId) {
        List<DishItemVO> cacheList = cacheUtil.getCacheList(
                RedisConstant.DISH_SETMEAL_CACHE_KEY_PREFIX + setmealId,
                DishItemVO.class);
        if (CollUtil.isNotEmpty(cacheList)){
            return cacheList;
        }
        List<DishItemVO> dishItemVOList = dishService.getDishItemBySetmealId(setmealId);
        cacheUtil.setCacheList(
                RedisConstant.DISH_SETMEAL_CACHE_KEY_PREFIX + setmealId,
                dishItemVOList,
                RedisConstant.DISH_SETMEAL_CACHE_TTL,
                TimeUnit.SECONDS);
        return dishItemVOList;
    }
}
