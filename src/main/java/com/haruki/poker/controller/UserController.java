package com.haruki.poker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haruki.poker.Service.UserService;
import com.haruki.poker.controller.dto.ApiResponse;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 修改用户昵称
     * @param openid 从请求头获取的微信openid
     * @param nickname 新昵称
     * @return 修改结果
     */
    @PostMapping("/api/user/update-nickname")
    public ApiResponse updateNickname(
            @RequestHeader("X-WX-OPENID") String openid,
            @RequestParam String nickname) {

        if (openid == null || openid.isEmpty()) {
            return ApiResponse.fail("未获取到用户身份信息");
        }
        if (nickname == null || nickname.isEmpty()) {
            return ApiResponse.fail("昵称不能为空");
        }
        if (nickname.length() > 32) {
            return ApiResponse.fail("昵称长度不能超过32个字符");
        }

        try {
            userService.updateNickname(openid, nickname);
            return ApiResponse.success();

        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
