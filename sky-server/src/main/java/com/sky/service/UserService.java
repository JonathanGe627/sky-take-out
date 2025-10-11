package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserLoginVO;

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
}
