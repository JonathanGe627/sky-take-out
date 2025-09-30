package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import com.sky.vo.EmployeeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@Api(tags = "员工管理接口")
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation("员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody @Validated EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation("员工登出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 添加员工
     * @param employeeDTO
     * @return
     */
    @ApiOperation("添加员工")
    @PostMapping
    public Result insertEmployee(@RequestBody @Validated EmployeeDTO employeeDTO){
        employeeService.insertEmployee(employeeDTO);
        return Result.success();
    }

    /**
     * 分页查询员工列表，支持根据员工姓名模糊查询
     * @param employeePageQueryDTO
     * @return
     */
    @ApiOperation("分页模糊查询员工列表")
    @GetMapping("/page")
    public Result<PageResult<Employee>> getEmployeeList(EmployeePageQueryDTO employeePageQueryDTO){
        PageResult<Employee> employeePageResult = employeeService.getEmployeeList(employeePageQueryDTO);
        return Result.success(employeePageResult);
    }

    /**
     * 启用/禁用员工账号
     * @param id
     * @param status
     * @return
     */
    @ApiOperation("启用/禁用员工账号")
    @PostMapping("/status/{status}")
    public Result updateEmployeeStatus(@RequestParam("id") Long id, @PathVariable("status") Integer status){
        employeeService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @ApiOperation("根据id查询员工")
    @GetMapping("/{id}")
    public Result<EmployeeVO> getEmployeeById(@PathVariable("id") Long id){
        EmployeeVO employeeVO = employeeService.getEmployeeById(id);
        return Result.success(employeeVO);
    }

    /**
     * 更改员工信息
     * @param employeeDTO
     * @return
     */
    @ApiOperation("更改员工信息")
    @PutMapping
    public Result updateEmployee(@RequestBody EmployeeDTO employeeDTO){
        employeeService.updateEmployee(employeeDTO);
        return Result.success();
    }
}
