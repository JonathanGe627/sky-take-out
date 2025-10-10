package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "购物车管理接口")
@RestController
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加菜品或套餐到购物车
     * @param shoppingCartDTO
     * @return
     */
    @ApiOperation("添加菜品或套餐到购物车")
    @PostMapping("/add")
    public Result insertIntoShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.insertIntoShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 获取当前登录用户的购物车数据
     * @return
     */
    @ApiOperation("获取当前登录用户的购物车数据")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> getShoppingCartList(){
        List<ShoppingCart> shoppingCartList = shoppingCartService.getShoppingCartList();
        return Result.success(shoppingCartList);
    }

    /**
     * 清空当前登录用户的购物车数据
     * @return
     */
    @ApiOperation("清空当前登录用户的购物车数据")
    @DeleteMapping("/clean")
    public Result cleanShoppingCart(){
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }

    /**
     * 购物车中的商品数量减一
     * @param shoppingCartDTO
     * @return
     */
    @ApiOperation("购物车中的商品数量减一")
    @PostMapping("/sub")
    public Result subShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success();
    }
}
