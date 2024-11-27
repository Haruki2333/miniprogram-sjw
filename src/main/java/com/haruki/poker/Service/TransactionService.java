package com.haruki.poker.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haruki.poker.repository.RoomRepository;
import com.haruki.poker.repository.RoomTransactionRepository;
import com.haruki.poker.repository.UserRoomRepository;
import com.haruki.poker.repository.entity.Room;
import com.haruki.poker.repository.entity.RoomTransaction;
import com.haruki.poker.repository.entity.UserRoom;

@Service
public class TransactionService {
    
    @Autowired
    private RoomTransactionRepository roomTransactionRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private UserRoomRepository userRoomRepository;
    
    @Transactional
    public boolean processBuyIn(String roomId, String openid, int hands) {
        // 获取房间信息
        Room room = roomRepository.selectByRoomId(roomId);
        if (room == null) {
            return false;
        }
        
        // 计算实际买入码量
        int buyInAmount = hands * room.getChipAmount();
        
        // 记录买入交易
        RoomTransaction transaction = new RoomTransaction();
        transaction.setRoomId(roomId);
        transaction.setOpenid(openid);
        transaction.setActionType("B");  // B 表示买入(Buy In)
        transaction.setActionAmount(buyInAmount);
        
        // 更新用户房间关系
        UserRoom userRoom = userRoomRepository.selectByRoomIdAndOpenid(roomId, openid);
        if (userRoom == null) {
            // 不存在关系则认为是非法数据
            return false;
        }
        
        // 更新买入手数
        userRoom.setBuyIn(userRoom.getBuyIn() + hands);
        userRoomRepository.update(userRoom);
        
        return roomTransactionRepository.insert(transaction) > 0;
    }

    @Transactional
    public boolean processSettle(String roomId, String openid, int finalAmount) {
        // 获取房间信息
        Room room = roomRepository.selectByRoomId(roomId);
        if (room == null) {
            return false;
        }
        // 获取用户在该房间的所有买入记录
        List<RoomTransaction> buyInRecords = roomTransactionRepository
            .findUserBuyInRecords(roomId, openid);
            
        // 计算总买入量
        int totalBuyIn = buyInRecords.stream()
            .mapToInt(RoomTransaction::getActionAmount)
            .sum();
        
        // 记录结算交易
        RoomTransaction transaction = new RoomTransaction();
        transaction.setRoomId(roomId);
        transaction.setOpenid(openid);
        transaction.setActionType("S");  // S 表示结算(Settle)
        transaction.setActionAmount(finalAmount);
        
        return roomTransactionRepository.insert(transaction) > 0;
    }
}
