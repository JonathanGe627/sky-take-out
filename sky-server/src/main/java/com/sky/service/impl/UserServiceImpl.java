package com.sky.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class UserServiceImpl implements UserService {

    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public UserLoginVO wxLogin(UserLoginDTO userLoginDTO) {
        // 1. 根据小程序端传来的code获取openid
        String code = userLoginDTO.getCode();
        String openid = getOpenid(code);
        // 2.校验openid的合法性
        if (StrUtil.isBlank(openid)){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 3.根据openid查找对应用户
        User user = userMapper.getUserByOpenid(openid);
        // 4.若用户不存在，则注册新用户
        if (user == null){
            user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
            userMapper.insertUser(user);
        }
        // 5.生成jwt令牌
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);
        // 6.包装返回值
        UserLoginVO userLoginVO = UserLoginVO.builder().
                openid(openid).
                id(user.getId()).
                token(token).
                build();
        return userLoginVO;
    }

    /**
     * 根据userId获取用户
     * @param userId
     * @return
     */
    @Override
    public User getUserById(Long userId) {
        User user = userMapper.getUserById(userId);
        return user;
    }

    private String getOpenid(String code) {
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
