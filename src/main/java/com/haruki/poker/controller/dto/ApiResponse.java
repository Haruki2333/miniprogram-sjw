package com.haruki.poker.controller.dto;

import java.util.Map;

import lombok.Data;

@Data
public class ApiResponse {
    private String retCode;
    private String retMsg;
    private Map<String, Object> data;

    public static ApiResponse success(Map<String, Object> data) {
        ApiResponse response = new ApiResponse();
        response.setRetCode("SUCCESS");
        response.setData(data);
        return response;
    }

    public static ApiResponse success() {
        return success(null);
    }

    public static ApiResponse fail(String message) {
        ApiResponse response = new ApiResponse();
        response.setRetCode("FAIL");
        response.setRetMsg(message);
        return response;
    }
}