package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishFlavorService;
import com.sky.service.DishService;
import com.sky.service.SetmealDishService;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 添加菜品
     * @param dishDTO
     */
    @Transactional
    @Override
    public void insertDish(DishDTO dishDTO) {
        Dish dish = BeanUtil.copyProperties(dishDTO, Dish.class);
        dishMapper.insertDish(dish);

        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();
        if (CollUtil.isEmpty(dishFlavorList)){
            return;
        }
        dishFlavorList.forEach(dishFlavor -> {
            dishFlavor.setDishId(dish.getId());
        });
        dishFlavorService.insertDishFlavorList(dishFlavorList);
    }

    /**
     * 菜品分页查询，支持根据菜品分类或菜品名称模糊查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult<DishVO> page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> dishList = dishMapper.page(dishPageQueryDTO.getName(), dishPageQueryDTO.getStatus(), dishPageQueryDTO.getCategoryId());
        PageResult<DishVO> pageResult = new PageResult<>(dishList.getTotal(), dishList.getResult());
        return pageResult;
    }

    /**
     * 根据id批量删除菜品，不可删除正在售卖的菜品和关联了套餐的菜品
     * @param ids
     */
    @Transactional
    @Override
    public void deleteDishes(List<Long> ids) {
        // 1.根据id获取菜品列表
        List<DishVO> dishVOList = dishMapper.getDishListByIds(ids);
        boolean flag = true;
        for (DishVO dishVO : dishVOList) {
            if (dishVO.getStatus() == 1){
                flag = false;
                break;
            }
        }
        if (!flag){
            // 2.如果有菜品处于售卖状态，则禁止删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        Integer count = setmealDishService.getSetmealCountByDishIds(ids);
        if (count > 0){
            // 3.如果有菜品关联了某个套餐，则禁止删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 4.删除菜品
        dishMapper.deleteDishes(ids);
        // 5.删除菜品对应的口味
        dishFlavorService.deleteDishFlavors(ids);
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getDishById(Long id) {
        ArrayList<Long> idList = new ArrayList<>(1);
        idList.add(id);
        List<DishVO> dishVOList = dishMapper.getDishListByIds(idList);
        return dishVOList.get(0);
    }

    /**
     * 更新菜品及口味信息
     * @param dishDTO
     */
    @Transactional
    @Override
    public void updateDish(DishDTO dishDTO) {
        Dish dish = BeanUtil.copyProperties(dishDTO, Dish.class);
        dishMapper.updateDish(dish);
        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();
        if (CollUtil.isEmpty(dishFlavorList)){
            return;
        }
        dishFlavorService.updateDishFlavor(dish.getId(), dishFlavorList);
    }

    /**
     * 启售/停售菜品
     * @param id
     * @param status
     */
    @Override
    public void updateDishStatus(Long id, Integer status) {
        Dish dish = Dish.builder().id(id).status(status).build();
        dishMapper.updateDish(dish);
    }

    /**
     * 根据分类查询已上架的菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getDishesByCategoryId(Long categoryId) {
        List<Dish> dishList = dishMapper.getDishesByCategoryId(categoryId);
        return dishList;
    }
}
