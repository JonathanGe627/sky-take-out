package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealDishService;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishService setmealDishService;

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
        // 1.查询当前套餐状态
        List<SetmealVO> setmealVOList = setmealMapper.getSetmealsByIds(ids);
        boolean flag = true;
        for (SetmealVO setmealVO : setmealVOList) {
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
    }

    /**
     * 根据种类id查询套餐
     * @param id
     * @return
     */
    @Override
    public List<Setmeal> getSetmealsByCategoryId(Long id) {
        List<Setmeal> setmealList = setmealMapper.getSetmealsByCategoryId(id);
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
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getSetmealById(Long id) {
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
        Setmeal setmeal = Setmeal.builder().id(id).status(status).build();
        setmealMapper.updateSetmeal(setmeal);
    }
}
