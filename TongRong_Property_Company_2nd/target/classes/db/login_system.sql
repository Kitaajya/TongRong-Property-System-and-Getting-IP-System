-- =============================================
-- TongRong_Property_Company_2nd 登录系统
-- 参考 IDE 数据库控制台 "登录/注册购物系统"(LogIn 数据库) 的设计
-- 数据库: LogIn
-- 表: users (用户) / login_logs (登录日志)
-- =============================================
DROP DATABASE IF EXISTS `LogIn`;
CREATE DATABASE IF NOT EXISTS `LogIn`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `LogIn`;

-- =============================================
-- 表: users (用户主表)
-- 密码存储: SHA2(CONCAT(明文密码, salt), 256) 十六进制
-- =============================================
CREATE TABLE IF NOT EXISTS `users` (
    `id`              INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID(主键)',
    `username`        VARCHAR(50)  NOT NULL COMMENT '用户名(登录账号)',
    `email`           VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',
    `password_hash`   CHAR(64)     NOT NULL COMMENT '密码哈希值 SHA2(明文+salt,256)',
    `salt`            VARCHAR(32)  NOT NULL COMMENT '密码盐值',
    `full_name`       VARCHAR(100) DEFAULT NULL COMMENT '真实姓名',
    `phone`           VARCHAR(20)  DEFAULT NULL COMMENT '手机号码',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态:1-正常,0-禁用',
    `is_ordinary_user` TINYINT     NULL DEFAULT 0 COMMENT '是否商家:1-是,0-否(NULL按0处理)',
    `last_login_ip`   VARCHAR(45)  DEFAULT NULL COMMENT '最后登录IP',
    `last_login_time` DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    `login_count`     INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '登录总次数',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`) COMMENT '用户名唯一索引',
    UNIQUE KEY `uk_email` (`email`) COMMENT '邮箱唯一索引',
    KEY `idx_status` (`status`) COMMENT '状态索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基本信息表';

-- =============================================
-- 表: login_logs (登录日志表)
-- =============================================
CREATE TABLE IF NOT EXISTS `login_logs` (
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id`      INT UNSIGNED    NOT NULL COMMENT '用户ID',
    `login_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    `ip_address`   VARCHAR(45)     DEFAULT NULL COMMENT '登录IP地址',
    `user_agent`   VARCHAR(500)    DEFAULT NULL COMMENT '浏览器/设备信息',
    `login_result` TINYINT         NOT NULL DEFAULT 1 COMMENT '登录结果:1-成功,0-失败',
    `fail_reason`  VARCHAR(100)    DEFAULT NULL COMMENT '失败原因(如:密码错误,账号禁用)',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
    CONSTRAINT `fk_login_logs_user` FOREIGN KEY (`user_id`)
        REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户登录日志表';

-- =============================================
-- 初始测试账号 (密码均为 123456)
-- =============================================
INSERT INTO `users` (`username`, `email`, `password_hash`, `salt`, `full_name`, `phone`, `status`)
VALUES
    ('admin',    'admin@example.com',    SHA2(CONCAT('123456', 'fixed_salt'), 256), 'fixed_salt', '管理员', '13800000000', 1),
    ('zhangsan', 'zhangsan@example.com', SHA2(CONCAT('123456', 'fixed_salt'), 256), 'fixed_salt', '张三',   '13800000001', 1),
    ('lisi',     'lisi@example.com',     SHA2(CONCAT('123456', 'fixed_salt'), 256), 'fixed_salt', '李四',   '13800000002', 1);

SELECT id, username, full_name, status FROM `users`;

-- =============================================
-- 存量数据库升级: 新增 "是否商家" 可空列
-- 已建过表(不含该列)时手动执行以下语句:
-- =============================================
-- ALTER TABLE `users` ADD COLUMN `is_ordinary_user` TINYINT NULL DEFAULT 0
--     COMMENT '是否商家:1-是,0-否(NULL按0处理)' AFTER `status`;
