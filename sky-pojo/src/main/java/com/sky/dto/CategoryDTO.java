package com.sky.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CategoryDTO implements Serializable {

    //主键
    private Long id;

    //类型 1 菜品分类 2 套餐分类
    @NotNull(message = "{categoryDTO.type.NotNull.message}")
    private Integer type;

    //分类名称
    @NotBlank(message = "{categoryDTO.name.NotBlank.message}")
    private String name;

    //排序
    @NotNull(message = "{categoryDTO.sort.NotNull.message}")
    private Integer sort;

}
