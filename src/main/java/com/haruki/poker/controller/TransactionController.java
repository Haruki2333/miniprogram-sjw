package com.haruki.poker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haruki.poker.Service.TransactionService;
import com.haruki.poker.controller.dto.ApiResponse;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/api/room/buyIn")
    public ApiResponse buyIn(@RequestHeader("X-WX-OPENID") String openid,
            @RequestParam String roomId, @RequestParam(defaultValue = "1") int hands) {
        if (hands > 10) {
            return ApiResponse.fail("每次最多只能带入10手");
        }

        boolean success = transactionService.processBuyIn(roomId, openid, hands);

        if (success) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail("带入操作失败");
        }
    }

    @PostMapping("/api/room/settle")
    public ApiResponse settle(@RequestHeader("X-WX-OPENID") String openid,
            @RequestParam String roomId, 
            @RequestParam int finalAmount) {
        
        if (finalAmount < 0) {
            return ApiResponse.fail("结算码量不能小于0");
        }
        
        boolean success = transactionService.processSettle(roomId, openid, finalAmount);
        
        if (success) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail("结算操作失败");
        }
    }

    @PostMapping("/api/room/cancelSettle")
    public ApiResponse cancelSettle(@RequestHeader("X-WX-OPENID") String openid,
            @RequestParam String roomId) {
        
        boolean success = transactionService.processCancelSettle(roomId, openid);
        
        if (success) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail("取消结算操作失败");
        }
    }

    
}
