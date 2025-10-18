package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserLoginVO;

import java.time.LocalDate;
import java.util.List;

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    UserLoginVO wxLogin(UserLoginDTO userLoginDTO);

    /**
     * 根据userId获取用户
     * @param userId
     * @return
     */
    User getUserById(Long userId);

    /**
     * 用户总数统计
     * @param dateList
     * @return
     */
    List<Integer> totalUserStatistics(List<LocalDate> dateList);

    /**
     * 用户新增统计
     * @param dateList
     * @return
     */
    List<Integer> newUserStatistics(List<LocalDate> dateList);
}
