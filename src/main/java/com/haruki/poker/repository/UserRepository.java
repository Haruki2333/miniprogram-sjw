package com.haruki.poker.repository;


import org.springframework.stereotype.Repository;

import com.haruki.poker.repository.entity.*;

import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;

@Repository
public interface UserRepository {
    
    /**
     * 根据openid查询用户
     */
    @Select("SELECT openid, nickname " +
           "FROM user " +
           "WHERE openid = #{openid}")
    User selectByOpenid(String openid);

    /**
     * 插入新用户
     */
    @Insert("INSERT INTO user (openid, nickname, created_time, updated_time) " +
           "VALUES (#{openid}, #{nickname}, DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'))")
    int insert(User user);

    /**
     * 更新用户信息
     */
    @Update("UPDATE user " +
           "SET nickname = #{nickname}, " +
           "    updated_time = DATE_FORMAT(NOW(), '%Y%m%d%H%i%s') " +
           "WHERE openid = #{openid}")
    int update(User user);
}
