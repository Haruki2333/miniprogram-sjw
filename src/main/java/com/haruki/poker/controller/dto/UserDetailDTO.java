package com.haruki.poker.controller.dto;

import lombok.Data;

@Data
public class UserDetailDTO {
    private String userNickname;
    private Integer buyIn = 0;
    private String settlementStatus = "U";
    private Integer finalAmount = 0;
    private Integer profitLoss = 0;
} 