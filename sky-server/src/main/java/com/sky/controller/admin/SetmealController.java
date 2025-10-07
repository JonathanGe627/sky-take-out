package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "套餐管理接口")
@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @ApiOperation("新增套餐")
    @PostMapping
    public Result insertSetmeal(@RequestBody SetmealDTO setmealDTO){
        setmealService.insertSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 分页查询套餐，支持根据分类，启用状态，以及名称模糊查询
     * @param setmealPageQueryDTO
     * @return
     */
    @ApiOperation("分页查询套餐")
    @GetMapping("/page")
    public Result<PageResult<SetmealVO>> page(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult<SetmealVO> pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @ApiOperation("批量删除套餐")
    @DeleteMapping
    public Result deleteSetmeals(@RequestParam("ids") List<Long> ids){
        setmealService.deleteSetmeals(ids);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @ApiOperation("根据id查询套餐")
    @GetMapping("/{id}")
    public Result<SetmealVO> getSetmealById(@PathVariable("id") Long id){
        SetmealVO setmealVO = setmealService.getSetmealById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @ApiOperation("修改套餐")
    @PutMapping
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO){
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 启售/停售套餐
     * @param id
     * @param status
     * @return
     */
    @ApiOperation("启售/停售套餐")
    @PostMapping("/status/{status}")
    public Result updateSetmealStatus(@RequestParam("id") Long id, @PathVariable Integer status){
        setmealService.updateSetmealStatus(id, status);
        return Result.success();
    }
}
