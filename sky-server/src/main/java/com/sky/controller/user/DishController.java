package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "用户端菜品管理接口")
@RestController("userDishController")
@RequestMapping("/user/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 根据种类id查询已上架的菜品及口味
     * @param categoryId
     * @return
     */
    @ApiOperation("根据种类id查询已上架的菜品")
    @GetMapping("/list")
    public Result<List<DishVO>> getDishListByCategoryId(@RequestParam("categoryId") Long categoryId){
        List<DishVO> dishVOList = dishService.getDishListByCategoryId(categoryId, 1);
        return Result.success(dishVOList);
    }
}
