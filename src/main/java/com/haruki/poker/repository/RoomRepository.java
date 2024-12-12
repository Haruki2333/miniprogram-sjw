package com.haruki.poker.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.haruki.poker.repository.entity.Room;

@Repository
public interface RoomRepository {
    
    /**
     * 根据roomId查询房间
     */
    @Select("SELECT room_id, room_name, room_code, chip_amount, owner_openid, created_time, created_date " +
           "FROM room " +
           "WHERE room_id = #{roomId}")
    Room selectByRoomId(String roomId);
    
    /**
     * 根据roomCode查询最近创建的一条纪录
     */
    @Select("SELECT room_id, room_name, room_code, chip_amount, owner_openid, created_time, created_date " +
           "FROM room " +
           "WHERE room_code = #{roomCode} " +
           "ORDER BY created_time DESC " +
           "LIMIT 1")
    Room selectByRoomCode(String roomCode);

    /**
     * 插入新房间，并自动获取生成的主键
     */
    @Insert("INSERT INTO room (room_name, room_code, chip_amount, owner_openid, created_time, created_date) " +
           "VALUES (#{roomName}, #{roomCode}, #{chipAmount}, #{ownerOpenid}, " +
           "DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), " +
           "DATE_FORMAT(NOW(), '%Y%m%d'))")
    @Options(useGeneratedKeys = true, keyProperty = "roomId")
    int insert(Room room);

    /**
     * 更新房间信息
     */
    @Update("UPDATE room " +
           "SET room_name = #{roomName}, " +
           "    chip_amount = #{chipAmount} " +
           "WHERE room_id = #{roomId}")
    int update(Room room);
}
