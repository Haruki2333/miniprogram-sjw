package com.haruki.poker.repository.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoomTransaction {
    private String transactionId;
    private String roomId;
    private String openid;
    private String actionType;
    private Integer actionAmount;
    private String createdTime;
    private String userNickname;
}
