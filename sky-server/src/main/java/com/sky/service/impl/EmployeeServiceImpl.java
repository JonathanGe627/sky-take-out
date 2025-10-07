package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.vo.EmployeeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getEmployeeByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 添加员工
     *
     * @param employeeDTO
     */
    @Override
    public void insertEmployee(EmployeeDTO employeeDTO) {
        Employee employee = BeanUtil.copyProperties(employeeDTO, Employee.class);
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employee.setStatus(StatusConstant.ENABLE);
        employeeMapper.insertEmployee(employee);
    }

    /**
     * 分页查询员工列表，支持根据员工姓名模糊查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult<Employee> page(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.page(employeePageQueryDTO.getName());
        PageResult<Employee> pageResult = new PageResult<>(page.getTotal(), page.getResult());
        return pageResult;
    }

    /**
     * 启用/禁用员工账号
     *
     * @param id
     * @param status
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        Employee employee = Employee.builder().id(id).status(status).build();
        employeeMapper.updateEmployee(employee);
    }

    /**
     * 根据id查询员工
     *
     * @param id
     * @return
     */
    @Override
    public EmployeeVO getEmployeeById(Long id) {
        Employee employee = employeeMapper.getEmployeeById(id);
        EmployeeVO employeeVO = BeanUtil.copyProperties(employee, EmployeeVO.class);
        return employeeVO;
    }

    /**
     * 更改员工信息
     *
     * @param employeeDTO
     */
    @Override
    public void updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = BeanUtil.copyProperties(employeeDTO, Employee.class);
        employeeMapper.updateEmployee(employee);
    }

}
