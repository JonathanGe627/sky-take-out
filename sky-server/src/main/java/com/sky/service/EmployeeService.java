package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 添加员工
     * @param employeeDTO
     */
    void insertEmployee(EmployeeDTO employeeDTO);

    /**
     * 分页查询员工列表，支持根据员工姓名模糊查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult<Employee> getEmployeeList(EmployeePageQueryDTO employeePageQueryDTO);

}
