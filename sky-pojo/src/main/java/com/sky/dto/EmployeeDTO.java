package com.sky.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class EmployeeDTO implements Serializable {

    private Long id;

    @NotBlank(message = "用户名不能为空！")
    private String username;

    @NotBlank(message = "姓名不能为空！")
    private String name;

    private String phone;

    private String sex;

    private String idNumber;

}
