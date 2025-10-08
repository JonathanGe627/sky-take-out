package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端分类管理
 */
@Api(tags = "用户端分类管理接口")
@RestController("userCategoryController")
@RequestMapping("/user/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据类型获取已启用的分类
     * @param type
     * @return
     */
    @ApiOperation("根据类型获取已启用的分类")
    @GetMapping("/list")
    public Result<List<Category>> getCategoryList(@RequestParam(value = "type", required = false) Integer type){
        List<Category> categoryList = categoryService.list(type, 1);
        return Result.success(categoryList);
    }

}
