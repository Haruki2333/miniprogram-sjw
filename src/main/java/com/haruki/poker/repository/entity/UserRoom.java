package com.haruki.poker.repository.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRoom {
    private int relationId;
    private String roomId;
    private String openid;
    private String userNickname;
    private int buyIn;
    private int finalAmount;
    private int profitLoss;
    private String settlementStatus;
    private String createdTime;
    private String updatedTime;
}