package com.sky.dto;

import com.sky.annotation.Gender;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class EmployeeDTO implements Serializable {

    private Long id;

    @NotBlank(message = "{employeeDTO.username.NotBlank.message}")
    private String username;

    @NotBlank(message = "{employeeDTO.name.NotBlank.message}")
    private String name;

    @NotBlank(message = "{employeeDTO.phone.NotBlank.message}")
    private String phone;

    @Gender
    private String sex;

    @NotBlank(message = "{employeeDTO.idNumber.NotBlank.message}")
    private String idNumber;

}
