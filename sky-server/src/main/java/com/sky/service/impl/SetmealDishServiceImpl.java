package com.sky.service.impl;

import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.service.SetmealDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealDishServiceImpl implements SetmealDishService {

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 获取与菜品列表关联的套餐的数量
     * @param ids
     * @return
     */
    @Override
    public Integer getSetmealCountByDishIds(List<Long> ids) {
        Integer count = setmealDishMapper.getSetmealCountByDishIds(ids);
        return count;
    }

    /**
     * 新增套餐和菜品的连接表
     * @param setmealDishList
     */
    @Override
    public void insertSetmealDish(List<SetmealDish> setmealDishList) {
        setmealDishMapper.insertSetmealDish(setmealDishList);
    }

    /**
     * 删除套餐-菜品关联表
     * @param ids
     */
    @Override
    public void deleteSetmealdishes(List<Long> ids) {
        setmealDishMapper.deleteSetmealdishes(ids);
    }

    /**
     * 修改套餐的菜品信息
     * @param id
     * @param setmealDishList
     */
    @Transactional
    @Override
    public void updateSetmealDish(Long id, List<SetmealDish> setmealDishList) {
        ArrayList<Long> idList = new ArrayList<>(1);
        idList.add(id);
        deleteSetmealdishes(idList);
        setmealDishList.forEach(setmealDish -> {
            setmealDish.setSetmealId(id);
        });
        insertSetmealDish(setmealDishList);
    }
}
