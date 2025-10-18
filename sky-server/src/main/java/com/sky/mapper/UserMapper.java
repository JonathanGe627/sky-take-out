package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查找用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{id}")
    User getUserByOpenid(String openid);

    /**
     * 新增用户
     * @param user
     */
    void insertUser(User user);

    /**
     * 根据userId获取用户
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{userId}")
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

    /**
     * 根据动态条件统计用户数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
