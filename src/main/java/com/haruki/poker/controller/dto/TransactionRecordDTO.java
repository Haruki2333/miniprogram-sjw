package com.haruki.poker.controller.dto;

import com.haruki.poker.constants.ActionType;
import lombok.Data;

@Data
public class TransactionRecordDTO {
    private String timestamp;
    private String userNickname;
    private String actionType;
    private Integer actionAmount;
    
    public String setActionType(String actionType) {
        return ActionType.getDescription(actionType);
    }
} 