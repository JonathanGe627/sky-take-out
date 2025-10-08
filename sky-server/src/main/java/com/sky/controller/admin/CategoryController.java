package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端分类管理
 */
@Api(tags = "管理端分类管理接口")
@RestController("adminCategoryController")
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类
     * @param categoryDTO
     * @return
     */
    @ApiOperation("添加分类")
    @PostMapping
    public Result insertCategory(@RequestBody @Validated CategoryDTO categoryDTO){
        categoryService.insertCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 分页查询分类列表，支持根据分类名称模糊查询
     * @param categoryPageQueryDTO
     * @return
     */
    @ApiOperation("分页模糊查询分类列表")
    @GetMapping("/page")
    public Result<PageResult<Category>> getCategoryList(CategoryPageQueryDTO categoryPageQueryDTO){
        PageResult<Category> categoryPageResult = categoryService.page(categoryPageQueryDTO);
        return Result.success(categoryPageResult);
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @ApiOperation("根据id删除分类")
    @DeleteMapping
    public Result deleteCategory(@RequestParam("id") Long id){
        categoryService.deleteCategory(id);
        return Result.success();
    }


    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @ApiOperation("修改分类")
    @PutMapping
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO){
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 启用/禁用分类
     * @param id
     * @param status
     * @return
     */
    @ApiOperation("启用/禁用分类")
    @PostMapping("/status/{status}")
    public Result updateCategoryStatus(@RequestParam("id") Long id, @PathVariable Integer status){
        categoryService.updateCategoryStatus(id, status);
        return Result.success();
    }

    /**
     * 获取所有分类
     * @param type
     * @return
     */
    @ApiOperation("获取所有分类")
    @GetMapping("/list")
    public Result<List<Category>> list(@RequestParam("type") Integer type){
        List<Category> categoryList = categoryService.list(type);
        return Result.success(categoryList);
    }
}
