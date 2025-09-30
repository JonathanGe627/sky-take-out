package com.sky.dto;

import com.sky.annotation.Gender;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class EmployeeDTO implements Serializable {

    @NotNull(message = "{id.NotNull.message}")
    private Long id;

    @NotBlank(message = "{username.NotBlank.message}")
    private String username;

    @NotBlank(message = "{name.NotBlank.message}")
    private String name;

    @NotBlank(message = "{phone.NotBlank.message}")
    private String phone;

    @Gender
    private String sex;

    @NotBlank(message = "{idNumber.NotBlank.message}")
    private String idNumber;

}
