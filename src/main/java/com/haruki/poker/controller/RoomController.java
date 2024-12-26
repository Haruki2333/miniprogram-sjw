package com.haruki.poker.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haruki.poker.Service.RoomService;
import com.haruki.poker.controller.dto.ApiResponse;
import com.haruki.poker.controller.dto.RoomInfoDTO;

@RestController
public class RoomController {

    @Autowired
    private RoomService roomService;

    /**
     * 获取最近房间列表
     * 
     * @param openid 用户的openid
     * @return 房间列表信息
     */
    @GetMapping("/api/room/recent")
    public ApiResponse getRecentRooms(@RequestHeader("X-WX-OPENID") String openid) {
        if (openid == null || openid.isEmpty()) {
            return ApiResponse.fail("未获取到用户身份信息");
        }

        try {
            List<RoomInfoDTO> rooms = roomService.getRecentRooms(openid);
            Map<String, Object> data = new HashMap<>();
            data.put("rooms", rooms);
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 创建房间
     * @param openid 房主的openid
     * @param roomName 房间名称
     * @param chipAmount 每手码量
     * @return 创建的房间信息
     */
    @PostMapping("/api/room/create")
    public ApiResponse createRoom(
            @RequestHeader("X-WX-OPENID") String openid,
            @RequestParam String roomName,
            @RequestParam(defaultValue = "0") Integer chipAmount) {
        
        if (openid == null || openid.isEmpty()) {
            return ApiResponse.fail("未获取到用户身份信息");
        }
        if (roomName == null || roomName.trim().isEmpty()) {
            return ApiResponse.fail("房间名称不能为空");
        }
        if (roomName.length() > 32) {
            return ApiResponse.fail("房间名称不能超过32个字符");
        }

        try {
            Map<String, Object> result = roomService.createRoom(openid, roomName, chipAmount);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 加入房间
     * @param openid 用户的openid
     * @param roomId 房间ID（可选）
     * @param roomCode 房间号（可选）
     * @return 房间信息和交易信息
     */
    @PostMapping("/api/room/join")
    public ApiResponse joinRoom(
            @RequestHeader("X-WX-OPENID") String openid,
            @RequestParam(required = false) String roomId,
            @RequestParam(required = false) String roomCode) {
        
        if (openid == null || openid.isEmpty()) {
            return ApiResponse.fail("未获取到用户身份信息");
        }
        if ((roomId == null || roomId.trim().isEmpty()) 
            && (roomCode == null || roomCode.trim().isEmpty())) {
            return ApiResponse.fail("房间号和房间ID不能同时为空");
        }

        try {
            // 加入房间并获取信息
            Map<String, Object> result = roomService.joinRoom(openid, roomId, roomCode);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
