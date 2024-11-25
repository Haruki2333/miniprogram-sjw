package com.haruki.poker.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.haruki.poker.controller.dto.TransactionRecordDTO;
import com.haruki.poker.repository.entity.RoomTransaction;

@Repository
public interface RoomTransactionRepository {

    /**
     * 根据roomId查询房间流水记录
     */
    @Select("SELECT transaction_id, room_id, openid, action_type, action_amount, created_time " +
            "FROM room_transaction " +
            "WHERE room_id = #{roomId}")
    List<RoomTransaction> selectByRoomId(String roomId);

    /**
     * 插入新的房间流水记录
     */
    @Insert("INSERT INTO room_transaction (room_id, openid, action_type, action_amount, created_time) " +
            "VALUES (#{roomId}, #{openid}, #{actionType}, #{actionAmount}, DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'))")
    int insert(RoomTransaction roomTransaction);

    /**
     * 查询房间的交易记录
     */
    @Select("SELECT t.created_time as timestamp, u.nickname as userNickname, " +
            "t.action_type as actionType, t.action_amount as actionAmount " +
            "FROM transaction_record t " +
            "JOIN user u ON t.openid = u.openid " +
            "WHERE t.room_id = #{roomId} " +
            "ORDER BY t.created_time DESC")
    List<TransactionRecordDTO> findTransactionRecords(String roomId);
}
