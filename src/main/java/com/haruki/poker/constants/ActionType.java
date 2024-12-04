package com.haruki.poker.constants;

import lombok.Getter;

@Getter
public enum ActionType {
    B("买入"),
    S("结算"),
    C("取消结算");
    
    private final String description;
    
    ActionType(String description) {
        this.description = description;
    }
    
    public static String getDescription(String code) {
        try {
            return ActionType.valueOf(code).getDescription();
        } catch (IllegalArgumentException e) {
            return code;
        }
    }
} 