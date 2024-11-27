package com.haruki.poker.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haruki.poker.repository.RoomRepository;
import com.haruki.poker.repository.RoomTransactionRepository;
import com.haruki.poker.repository.UserRoomRepository;
import com.haruki.poker.repository.entity.Room;
import com.haruki.poker.repository.entity.RoomTransaction;

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
        transaction.setActionType("B"); // B 表示买入(Buy In)
        transaction.setActionAmount(buyInAmount);

        // 更新买入码量
        if (userRoomRepository.updateBuyIn(roomId, openid, buyInAmount) > 0) {
            if (roomTransactionRepository.insert(transaction) > 0) {
                return true;
            } else {
                // 插入流水失败，回滚
                userRoomRepository.updateBuyIn(roomId, openid, -buyInAmount);
            }
        }

        return false;
    }

    @Transactional
    public boolean processSettle(String roomId, String openid, int finalAmount) {
        // 记录结算交易
        RoomTransaction transaction = new RoomTransaction();
        transaction.setRoomId(roomId);
        transaction.setOpenid(openid);
        transaction.setActionType("S"); // S 表示结算(Settle)
        transaction.setActionAmount(finalAmount);

        if (userRoomRepository.settle(roomId, openid, finalAmount) > 0) {
            if (roomTransactionRepository.insert(transaction) > 0) {
                return true;
            } else {
                // 插入流水失败，取消结算
                userRoomRepository.cancelSettle(roomId, openid);
            }
        }

        return false;
    }

    @Transactional
    public boolean processCancelSettle(String roomId, String openid) {
        // 记录取消结算交易
        RoomTransaction transaction = new RoomTransaction();
        transaction.setRoomId(roomId);
        transaction.setOpenid(openid);
        transaction.setActionType("C"); // C 表示取消结算(Cancel)
        transaction.setActionAmount(0);

        if (userRoomRepository.cancelSettle(roomId, openid) > 0) {
            if (roomTransactionRepository.insert(transaction) > 0) {
                return true;
            }
        }

        return false;
    }
}
