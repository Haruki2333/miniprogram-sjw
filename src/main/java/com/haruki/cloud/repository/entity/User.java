package com.haruki.cloud.repository.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private String openid;
    private String nickname;
}
