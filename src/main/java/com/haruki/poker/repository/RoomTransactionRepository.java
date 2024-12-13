package com.haruki.poker.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.haruki.poker.repository.entity.RoomTransaction;

@Repository
public interface RoomTransactionRepository {

    /**
     * 插入新的房间流水记录
     */
    @Insert("INSERT INTO room_transaction (room_id, openid, action_type, action_amount, created_time) " +
            "VALUES (#{roomId}, #{openid}, #{actionType}, #{actionAmount}, DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'))")
    int insert(RoomTransaction roomTransaction);

    /**
     * 查询房间的买入交易记录
     */
    @Select("SELECT t.transaction_id, t.room_id, t.openid, t.action_type, " +
            "t.action_amount, t.created_time, u.nickname as user_nickname " +
            "FROM room_transaction t " +
            "JOIN user u ON t.openid = u.openid " +
            "WHERE t.room_id = #{roomId} " +
            "AND t.action_type = 'B' " +
            "ORDER BY t.created_time DESC")
    List<RoomTransaction> findTransactionRecordsWithUserInfo(String roomId);

    /**
     * 查询用户在指定房间的买入记录
     */
    @Select("SELECT transaction_id, room_id, openid, action_type, " +
            "action_amount, created_time " +
            "FROM room_transaction " +
            "WHERE room_id = #{roomId} " +
            "AND openid = #{openid} " +
            "AND action_type = 'B'")
    List<RoomTransaction> findUserBuyInRecords(String roomId, String openid);
}
