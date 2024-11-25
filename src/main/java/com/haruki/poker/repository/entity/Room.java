package com.haruki.poker.repository.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Room {
    private String roomId;
    private String roomName;
    private String roomCode;
    private int chipAmount;
    private String ownerOpenid;
    private String createdTime;
}