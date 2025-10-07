package com.sky.service.impl;

import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.service.DishFlavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishFlavorServiceImpl implements DishFlavorService {

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 批量添加菜品口味
     * @param dishFlavorList
     */
    @Override
    public void insertDishFlavorList(List<DishFlavor> dishFlavorList){
        dishFlavorMapper.insertDishFlavorList(dishFlavorList);
    }

    /**
     * 删除指定id菜品所对应的口味
     * @param ids
     */
    @Override
    public void deleteDishFlavors(List<Long> ids){
        dishFlavorMapper.deleteDishFlavors(ids);
    }

    /**
     * 根据菜品id更新口味
     * @param id
     * @param dishFlavorList
     */
    @Transactional
    @Override
    public void updateDishFlavor(Long id, List<DishFlavor> dishFlavorList) {
        ArrayList<Long> ids = new ArrayList<>(1);
        ids.add(id);
        dishFlavorMapper.deleteDishFlavors(ids);
        dishFlavorMapper.insertDishFlavorList(dishFlavorList);
    }
}
