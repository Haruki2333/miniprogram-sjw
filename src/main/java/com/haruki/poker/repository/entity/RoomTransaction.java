package com.haruki.poker.repository.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoomTransaction {
    private int transactionId;
    private String roomId;
    private String openid;
    private String actionType;
    private int actionAmount;
    private String createdTime;
}
