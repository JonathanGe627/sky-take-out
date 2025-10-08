package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "用户端套餐管理接口")
@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据分类id获取已经上架的套餐
     * @param categoryId
     * @return
     */
    @ApiOperation("根据分类id获取已经上架的套餐")
    @GetMapping("/list")
    public Result<List<Setmeal>> getSetmealListByCategoryId(@RequestParam("categoryId") Long categoryId){
        List<Setmeal> setmealList = setmealService.getSetmealsByCategoryId(categoryId, 1);
        return Result.success(setmealList);
    }

    /**
     * 根据套餐id查询包含的菜品
     * @param id
     * @return
     */
    @ApiOperation("根据套餐id查询包含的菜品")
    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> getDishItemBySetmealId(@PathVariable("id") Long id){
        List<DishItemVO> dishItemVOList = setmealService.getDishItemBySetmealId(id);
        return Result.success(dishItemVOList);
    }
}
