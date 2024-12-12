package com.haruki.poker.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haruki.poker.controller.dto.RoomInfoDTO;
import com.haruki.poker.controller.dto.TransactionRecordDTO;
import com.haruki.poker.controller.dto.UserDetailDTO;
import com.haruki.poker.repository.RoomRepository;
import com.haruki.poker.repository.RoomTransactionRepository;
import com.haruki.poker.repository.UserRoomRepository;
import com.haruki.poker.repository.entity.Room;
import com.haruki.poker.repository.entity.RoomTransaction;
import com.haruki.poker.repository.entity.User;
import com.haruki.poker.repository.entity.UserRoom;

@Service
public class RoomService {
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private UserRoomRepository userRoomRepository;

    @Autowired
    private RoomTransactionRepository roomTransactionRepository;

    @Autowired
    private UserService userService;

    private static final Integer ROOM_EXPIRED_DAYS = 7;
    
    /**
     * 获取用户最近参与的房间列表（房间创建时间在7天内）
     * @param openid 用户的openid
     * @return 房间信息列表
     */
    public List<RoomInfoDTO> getRecentRooms(String openid) {
        List<Room> rooms = userRoomRepository.findRecentRoomsByOpenid(openid);
        if (rooms == null || rooms.isEmpty()) {
            return List.of();
        }
        
        return rooms.stream()
            .sorted((r1, r2) -> r2.getCreatedTime().compareTo(r1.getCreatedTime()))
            .map(room -> {
                RoomInfoDTO dto = new RoomInfoDTO();
                dto.setRoomId(room.getRoomId());
                dto.setRoomName(room.getRoomName());
                dto.setRoomCode(room.getRoomCode());
                dto.setChipAmount(room.getChipAmount());
                dto.setCreatedTime(room.getCreatedTime());
                return dto;
            })
            .collect(Collectors.toList());
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
     * TODO 统一管理起来
     */
    private String generateRoomCode() {
        // 生成6位数字房间码
        int code = (int) ((Math.random() * 900000) + 100000);
        return String.valueOf(code);
    }
    
    /**
     * 加入房间
     * @param openid 用户openid
     * @param roomId 房间ID（可选）
     * @param roomCode 房间号（可选）
     * @return 房间信息和交易信息
     */
    public Map<String, Object> joinRoom(String openid, String roomId, String roomCode) {
        // 获取当前用户信息
        User currentUser = userService.getUserByOpenid(openid);
        
        // 查找房间
        Room room = null;
        if (roomId != null && !roomId.trim().isEmpty()) {
            room = roomRepository.selectByRoomId(roomId);
        } else if (roomCode != null && !roomCode.trim().isEmpty()) {
            room = roomRepository.selectByRoomCode(roomCode);
        }
        
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }

        // 检查房间是否超过7天
        String createdDate = room.getCreatedDate();
        LocalDate roomCreatedDate = LocalDate.parse(createdDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate now = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(roomCreatedDate, now);
        if (daysBetween > ROOM_EXPIRED_DAYS) {
            throw new RuntimeException("房间已过期");
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
            currentUserRoom.setBuyIn(0);
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
        
        // 设置当前用户详情
        UserDetailDTO userDetail = new UserDetailDTO();
        userDetail.setUserNickname(currentUser.getNickname());
        userDetail.setBuyIn(currentUserRoom.getBuyIn());
        userDetail.setSettlementStatus(currentUserRoom.getSettlementStatus());
        userDetail.setFinalAmount(currentUserRoom.getFinalAmount());
        userDetail.setProfitLoss(currentUserRoom.getProfitLoss());

        // 获取其他用户详情
        List<UserDetailDTO> allPlayerDetails = getAllUsersDetails(room.getRoomId());

        // 获取房间流水记录
        List<TransactionRecordDTO> transactionRecords = getTransactionRecords(room.getRoomId());

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("roomInfo", roomInfo);
        result.put("userDetail", userDetail);
        result.put("allPlayerDetails", allPlayerDetails);
        result.put("transactionRecords", transactionRecords);
        
        return result;
    }

    /**
     * 获取房间内所有用户详情
     */
    private List<UserDetailDTO> getAllUsersDetails(String roomId) {
        List<UserRoom> allUserDetails = userRoomRepository.selectByRoomIdWithNickname(roomId);
        
        return allUserDetails.stream()
            .sorted((a, b) -> {
                // 优先按结算状态排序
                int statusCompare = b.getSettlementStatus().compareTo(a.getSettlementStatus());
                if (statusCompare != 0) {
                    return statusCompare;
                }
                // 结算状态相同时按创建时间倒序，从新到旧
                return b.getCreatedTime().compareTo(a.getCreatedTime());
            })
            .map(userRoom -> {
                UserDetailDTO dto = new UserDetailDTO();
                dto.setUserNickname(userRoom.getUserNickname());
                dto.setBuyIn(userRoom.getBuyIn());
                dto.setSettlementStatus(userRoom.getSettlementStatus());
                dto.setFinalAmount(userRoom.getFinalAmount());
                dto.setProfitLoss(userRoom.getProfitLoss());
                return dto;
            })
            .collect(Collectors.toList());
    }

    /**
     * 获取房间交易记录
     */
    private List<TransactionRecordDTO> getTransactionRecords(String roomId) {
        List<RoomTransaction> transactions = roomTransactionRepository.findTransactionRecordsWithUserInfo(roomId);
        
        return transactions.stream()
            .map(transaction -> {
                TransactionRecordDTO dto = new TransactionRecordDTO();
                dto.setTimestamp(transaction.getCreatedTime());
                dto.setUserNickname(transaction.getUserNickname());
                dto.setActionType(transaction.getActionType());
                dto.setActionAmount(transaction.getActionAmount());
                return dto;
            })
            .collect(Collectors.toList());
    }
}
