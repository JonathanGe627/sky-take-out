package com.sky.dto;

import com.sky.annotation.Gender;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class EmployeeDTO implements Serializable {

    private Long id;

    @NotBlank(message = "{username.NotBlank.message}")
    private String username;

    @NotBlank(message = "{name.NotBlank.message}")
    private String name;

    private String phone;

    @Gender
    private String sex;

    private String idNumber;

}
