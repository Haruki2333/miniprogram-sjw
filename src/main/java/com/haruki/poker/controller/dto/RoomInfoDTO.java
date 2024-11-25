package com.haruki.poker.controller.dto;

import lombok.Data;

@Data
public class RoomInfoDTO {
    private String roomId;
    private String roomName;
    private String roomCode;
    private Integer chipAmount;
} 