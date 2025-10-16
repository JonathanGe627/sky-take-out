package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private DishService dishService;

    /**
     * 添加菜品或套餐到购物车
     *
     * @param shoppingCartDTO
     */
    @Override
    public void insertIntoShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();
        Long userId = BaseContext.getCurrentId();
        // 1.判断要添加菜品或套餐是否在当前用户的购物车中已经存在
        ShoppingCart queryCart = BeanUtil.copyProperties(shoppingCartDTO, ShoppingCart.class);
        queryCart.setUserId(userId);
        ShoppingCart shoppingCartDB = shoppingCartMapper.getShoppingCartByUserIdAndDishIdOrSetmealId(queryCart);
        if (shoppingCartDB != null){
            // 2.如果已经存在，根据口味判断是新增购物车数据还是将原数据数量+1
            // 当StrUtil.equals(shoppingCartDTO.getDishFlavor(), shoppingCartDB.getDishFlavor())成立时，有三种可能性
            // ①.传来的是套餐 ②.传来的是菜品，且和购物车中已存在的该菜品的口味相同 ③.传来的是菜品，但菜品口味为空
            if (StrUtil.equals(shoppingCartDTO.getDishFlavor(), shoppingCartDB.getDishFlavor())){
                shoppingCartDB.setNumber(shoppingCartDB.getNumber() + 1);
                shoppingCartMapper.updateShoppingCart(shoppingCartDB);
                return;
            }
        }
        // 3.如果不存在，则需要新增shoppingCart，给shoppingCart对象公共字段赋值
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCart.setNumber(1);
        // 4. 判断传来的是菜品还是套餐
        if (dishId == null) {
            // 4.1 dishId == null，说明传来的是套餐，进行健壮性判断，并给shoppingCart对象赋值
            SetmealVO setmealVO = setmealService.getSetmealVOById(setmealId);
            if (setmealVO == null) {
                throw new ShoppingCartBusinessException(MessageConstant.SETMEAL_NOT_EXIST);
            }
            shoppingCart.setSetmealId(setmealId);
            shoppingCart.setName(setmealVO.getName());
            shoppingCart.setAmount(setmealVO.getPrice());
            shoppingCart.setImage(setmealVO.getImage());
        } else {
            // 4.2 dishId != null，说明传来的是菜品，进行健壮性判断，并给shoppingCart对象赋值
            DishVO dishVO = dishService.getDishById(dishId);
            if (dishVO == null) {
                throw new ShoppingCartBusinessException(MessageConstant.DISH_NOT_EXIST);
            }
            shoppingCart.setDishId(dishId);
            shoppingCart.setDishFlavor(shoppingCartDTO.getDishFlavor());
            shoppingCart.setName(dishVO.getName());
            shoppingCart.setAmount(dishVO.getPrice());
            shoppingCart.setImage(dishVO.getImage());
        }
        // 5.添加对象
        shoppingCartMapper.insertIntoShoppingCart(shoppingCart);
    }

    /**
     * 获取当前登录用户的购物车数据
     * @return
     */
    @Override
    public List<ShoppingCart> getShoppingCartList() {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.getShoppingCartList(userId);
        return shoppingCartList;
    }

    /**
     * 清空当前登录用户的购物车数据
     */
    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteShoppingCartByUserId(userId);
    }

    /**
     * 购物车中的商品数量减一
     * @param shoppingCartDTO
     */
    @Transactional
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = BeanUtil.copyProperties(shoppingCartDTO, ShoppingCart.class);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        ShoppingCart shoppingCartDB = shoppingCartMapper.getShoppingCartByUserIdAndDishIdOrSetmealId(shoppingCart);
        // 1.数量为1，直接删除
        if (shoppingCartDB.getNumber() == 1){
            shoppingCartMapper.deleteShoppingCartById(shoppingCartDB.getId());
            return;
        }
        // 2.数量大于1，数量减1
        shoppingCartDB.setNumber(shoppingCartDB.getNumber() - 1);
        shoppingCartMapper.updateShoppingCart(shoppingCartDB);
    }

    /**
     * 再来一单
     * @param orderDetailList
     */
    @Override
    public void repetition(List<OrderDetail> orderDetailList) {
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(orderDetail -> {
            ShoppingCart shoppingCart = BeanUtil.copyProperties(orderDetail, ShoppingCart.class);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartMapper.insertIntoShoppingCartBatch(shoppingCartList);
    }
}

