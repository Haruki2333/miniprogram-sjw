-- 用户表
CREATE TABLE `user` (
    `openid` VARCHAR(32) NOT NULL COMMENT '用户的微信OpenID',
    `nickname` VARCHAR(32) NOT NULL COMMENT '用户昵称',
    `created_time` VARCHAR(14) NOT NULL COMMENT '创建时间',
    `updated_time` VARCHAR(14) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 房间表
CREATE TABLE `room` (
    `room_id` INT NOT NULL AUTO_INCREMENT COMMENT '房间ID',
    `room_name` VARCHAR(32) NOT NULL COMMENT '房间名称',
    `room_code` VARCHAR(6) NOT NULL COMMENT '房间号',
    `chip_amount` INT NOT NULL DEFAULT 0 COMMENT '每手码量',
    `owner_openid` VARCHAR(32) NOT NULL COMMENT '房主OpenID',
    `created_time` VARCHAR(14) NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`room_id`),
    INDEX `idx_room_code` (`room_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间表';

-- 用户房间关系表
CREATE TABLE `user_room` (
    `relation_id` INT NOT NULL AUTO_INCREMENT COMMENT '关系ID',
    `room_id` VARCHAR(32) NOT NULL COMMENT '房间ID',
    `openid` VARCHAR(32) NOT NULL COMMENT '用户OpenID',
    `buy_in` INT NOT NULL DEFAULT 0 COMMENT '带入码量',
    `final_amount` INT NOT NULL DEFAULT 0 COMMENT '结算码量',
    `profit_loss` INT NOT NULL DEFAULT 0 COMMENT '盈亏情况',
    `settlement_status` VARCHAR(1) NOT NULL DEFAULT 'U' COMMENT '结算状态 U-未结算 S-已结算',
    `created_time` VARCHAR(14) NOT NULL COMMENT '创建时间',
    `updated_time` VARCHAR(14) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`relation_id`),
    INDEX `idx_room_id` (`room_id`),
    INDEX `idx_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户房间关系表';

-- 房间流水记录表
CREATE TABLE `room_transaction` (
    `transaction_id` INT NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    `room_id` VARCHAR(32) NOT NULL COMMENT '房间ID',
    `openid` VARCHAR(32) NOT NULL COMMENT '用户OpenID',
    `action_type` VARCHAR(1) NOT NULL COMMENT '操作类型 B-带入 S-结算 C-取消结算',
    `action_amount` INT NOT NULL DEFAULT 0 COMMENT '操作码量',
    `created_time` VARCHAR(14) NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`transaction_id`),
    INDEX `idx_room_id` (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间流水记录表';