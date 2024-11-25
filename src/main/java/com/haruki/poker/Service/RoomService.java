package com.haruki.poker.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haruki.poker.controller.dto.RoomInfoDTO;
import com.haruki.poker.controller.dto.RoomTransactionDTO;
import com.haruki.poker.controller.dto.TransactionRecordDTO;
import com.haruki.poker.controller.dto.UserDetailDTO;
import com.haruki.poker.repository.RoomRepository;
import com.haruki.poker.repository.UserRoomRepository;
import com.haruki.poker.repository.entity.Room;
import com.haruki.poker.repository.entity.User;
import com.haruki.poker.repository.entity.UserRoom;

@Service
public class RoomService {
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private UserRoomRepository userRoomRepository;

    @Autowired
    private UserService userService;
    
    /**
     * 获取用户最近参与的房间列表
     * @param openid 用户的openid
     * @return 房间信息列表
     */
    public List<RoomInfoDTO> getRecentRooms(String openid) {
        List<Room> rooms = userRoomRepository.findRecentRoomsByOpenid(openid);
        if (rooms == null || rooms.isEmpty()) {
            return List.of();
        }
        
        return rooms.stream().map(room -> {
            RoomInfoDTO dto = new RoomInfoDTO();
            dto.setRoomId(room.getRoomId());
            dto.setRoomName(room.getRoomName());
            dto.setRoomCode(room.getRoomCode());
            dto.setChipAmount(room.getChipAmount());
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 创建新房间
     * @param ownerOpenid 房主openid
     * @param roomName 房间名称
     * @param chipAmount 每手码量
     * @return 房间信息
     */
    public Map<String, Object> createRoom(String ownerOpenid, String roomName, Integer chipAmount) {
        // 获取用户信息
        User user = userService.getUserByOpenid(ownerOpenid);
        UserDetailDTO userDetail = new UserDetailDTO();
        userDetail.setUserNickname(user.getNickname());

        // 创建房间记录
        Room room = new Room();
        room.setRoomName(roomName);
        room.setChipAmount(chipAmount);
        room.setOwnerOpenid(ownerOpenid);
        room.setRoomCode(generateRoomCode()); // 生成6位随机房间码
        roomRepository.insert(room);
        
        // 加入房间
        userRoomRepository.insert(room.getRoomId(), ownerOpenid);
        
        RoomInfoDTO roomInfo = new RoomInfoDTO();
        roomInfo.setRoomId(room.getRoomId());
        roomInfo.setRoomCode(room.getRoomCode());
        roomInfo.setRoomName(room.getRoomName());
        roomInfo.setChipAmount(room.getChipAmount());

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("roomInfo", roomInfo);
        result.put("userDetail", userDetail);
        
        return result;
    }
    
    /**
     * 生成6位随机房间码
     */
    private String generateRoomCode() {
        // 生成6位数字房间码
        int code = (int) ((Math.random() * 900000) + 100000);
        return String.valueOf(code);
    }
    
    /**
     * 加入房间
     * @param openid 用户openid
     * @param roomCode 房间号
     * @return 房间信息和交易信息
     */
    public Map<String, Object> joinRoom(String openid, String roomCode) {
        
        // 获取当前用户信息
        User currentUser = userService.getUserByOpenid(openid);
        
        // 查找房间
        Room room = roomRepository.selectByRoomCode(roomCode);
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }

        // 检查房间是否超过48小时
        String createdTime = room.getCreatedTime();
        LocalDateTime roomCreatedTime = LocalDateTime.parse(createdTime, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(roomCreatedTime, now);
        if (duration.toHours() > 48) {
            throw new RuntimeException("房间已超过48小时,不能再加入");
        }

        // 检查用户是否已在房间中
        UserRoom currentUserRoom = userRoomRepository.selectByRoomIdAndOpenid(room.getRoomId(), openid);
        if (currentUserRoom == null) {
            // 新用户加入房间
            userRoomRepository.insert(room.getRoomId(), openid);

            // 初始化用户房间关系对象
            currentUserRoom = new UserRoom();
            currentUserRoom.setRoomId(room.getRoomId());
            currentUserRoom.setOpenid(openid);
            currentUserRoom.setBuyInCount(0);
            currentUserRoom.setFinalAmount(0);
            currentUserRoom.setProfitLoss(0);
            currentUserRoom.setSettlementStatus("U");
        }

        // 获取房间基本信息
        RoomInfoDTO roomInfo = new RoomInfoDTO();
        roomInfo.setRoomId(room.getRoomId());
        roomInfo.setRoomName(room.getRoomName());
        roomInfo.setRoomCode(room.getRoomCode());
        roomInfo.setChipAmount(room.getChipAmount());

        // 获取交易信息
        RoomTransactionDTO transaction = new RoomTransactionDTO();
        
        // 设置当前用户详情
        UserDetailDTO userDetails = new UserDetailDTO();
        userDetails.setUserNickname(currentUser.getNickname());
        userDetails.setBuyInCount(currentUserRoom.getBuyInCount());
        userDetails.setSettlementStatus(currentUserRoom.getSettlementStatus());
        userDetails.setFinalAmount(currentUserRoom.getFinalAmount());
        userDetails.setProfitLoss(currentUserRoom.getProfitLoss());
        transaction.setUserDetail(userDetails);

        // 获取其他用户详情
        List<UserDetailDTO> allUserDetails = getAllUsersDetails(room.getRoomId());
        transaction.setAllUserDetails(allUserDetails);

        // 获取交易记录
        List<TransactionRecordDTO> records = getTransactionRecords(room.getRoomId());
        transaction.setTransactionRecords(records);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("roomInfo", roomInfo);
        result.put("transaction", transaction);
        
        return result;
    }

    /**
     * 获取房间内所有用户详情
     */
    private List<UserDetailDTO> getAllUsersDetails(String roomId) {
        List<UserRoom> allUserDetails = userRoomRepository.selectByRoomIdWithNickname(roomId);
        
        return allUserDetails.stream().map(userRoom -> {
            UserDetailDTO dto = new UserDetailDTO();
            dto.setUserNickname(userRoom.getUserNickname());
            dto.setBuyInCount(userRoom.getBuyInCount());
            dto.setSettlementStatus(userRoom.getSettlementStatus());
            dto.setFinalAmount(userRoom.getFinalAmount());
            dto.setProfitLoss(userRoom.getProfitLoss());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 获取房间交易记录
     */
    private List<TransactionRecordDTO> getTransactionRecords(String roomId) {
        // TODO: 实现从RoomTransactionRepository获取交易记录并转换为DTO
        return new ArrayList<>(); // 临时返回空列表
    }
}
