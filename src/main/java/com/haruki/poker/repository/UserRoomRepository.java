package com.haruki.poker.repository;

import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import java.util.List;
import com.haruki.poker.repository.entity.UserRoom;
import com.haruki.poker.repository.entity.Room;

@Repository
public interface UserRoomRepository {

    /**
     * 根据roomId和openid查询用户房间关系
     */
    @Select("SELECT relation_id, room_id, openid, buy_in, final_amount, profit_loss, " +
            "settlement_status, created_time, updated_time " +
            "FROM user_room " +
            "WHERE room_id = #{roomId} AND openid = #{openid}")
    UserRoom selectByRoomIdAndOpenid(String roomId, String openid);

    /**
     * 插入新的用户房间关系
     */
    @Insert("INSERT INTO user_room (room_id, openid, created_time, updated_time) " +
            "VALUES (#{roomId}, #{openid}, DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'))")
    int insert(String roomId, String openid);

    /**
     * 更新用户房间关系
     */
    @Update("UPDATE user_room " +
            "SET buy_in = #{buyIn}, " +
            "    final_amount = #{finalAmount}, " +
            "    profit_loss = #{profitLoss}, " +
            "    settlement_status = #{settlementStatus}, " +
            "    updated_time = DATE_FORMAT(NOW(), '%Y%m%d%H%i%s') " +
            "WHERE relation_id = #{relationId}")
    int update(UserRoom userRoom);

    /**
     * 查询用户最近参与的房间列表
     * @param openid 用户ID
     * @return 房间列表，包含房间基本信息和用户加入时间
     */
    @Select("SELECT " +
            "    r.room_id, " +
            "    r.room_name, " +
            "    r.room_code, " + 
            "    r.chip_amount, " +
            "    r.owner_openid, " +
            "    ur.created_time" +
            "FROM room r " +
            "INNER JOIN user_room ur ON r.room_id = ur.room_id " +
            "WHERE ur.openid = #{openid} ")
    List<Room> findRecentRoomsByOpenid(String openid);

    /**
     * 根据roomId查询用户房间关系列表（包含用户昵称）
     */
    @Select("SELECT ur.relation_id, ur.room_id, ur.openid, ur.buy_in, " +
            "ur.final_amount, ur.profit_loss, ur.settlement_status, " +
            "ur.created_time, ur.updated_time, u.nickname as user_nickname " +
            "FROM user_room ur " + 
            "INNER JOIN user u ON ur.openid = u.openid " +
            "WHERE ur.room_id = #{roomId}")
    List<UserRoom> selectByRoomIdWithNickname(String roomId);
}
