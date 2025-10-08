package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "管理端菜品管理接口")
@RestController("adminDishController")
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 添加菜品
     * @param dishDTO
     * @return
     */
    @ApiOperation("添加菜品")
    @PostMapping
    public Result insertDish(@RequestBody DishDTO dishDTO){
        dishService.insertDish(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询，支持根据菜品分类或菜品名称模糊查询
     * @param dishPageQueryDTO
     * @return
     */
    @ApiOperation("菜品分页模糊查询")
    @GetMapping("/page")
    public Result<PageResult<DishVO>> page(DishPageQueryDTO dishPageQueryDTO){
        PageResult<DishVO> pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id批量删除菜品
     * @param ids
     * @return
     */
    @ApiOperation("根据id批量删除菜品")
    @DeleteMapping
    public Result deleteDishes(@RequestParam("ids") List<Long> ids){
        dishService.deleteDishes(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @ApiOperation("根据id查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> getDishById(@PathVariable("id") Long id){
        DishVO dishVO = dishService.getDishById(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @ApiOperation("修改菜品")
    @PutMapping
    public Result updateDish(@RequestBody DishDTO dishDTO){
        dishService.updateDish(dishDTO);
        return Result.success();
    }

    @ApiOperation("启售/停售菜品")
    @PostMapping("/status/{status}")
    public Result updateDishStatus(@RequestParam("id") Long id, @PathVariable("status") Integer status){
        dishService.updateDishStatus(id, status);
        return Result.success();
    }

    /**
     * 根据分类查询已上架的菜品
     * @param categoryId
     * @return
     */
    @ApiOperation("根据分类查询已上架的菜品")
    @GetMapping("/list")
    public Result<List<Dish>> getDishesByCategoryId(@RequestParam("categoryId") Long categoryId){
        List<Dish> dishList = dishService.getDishesByCategoryId(categoryId);
        return Result.success(dishList);
    }
}
