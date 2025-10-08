package com.sky.controller.user;

import com.sky.dto.UserLoginDTO;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/user/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @ApiOperation("微信登录")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        UserLoginVO userLoginVO = userService.wxLogin(userLoginDTO);
        return Result.success(userLoginVO);
    }
}
