package com.haruki.poker.controller.dto;

import lombok.Data;

@Data
public class TransactionRecordDTO {
    private String timestamp;
    private String userNickname;
    private String actionType;
    private Integer actionAmount;
} 