-- =============================================
-- TongRong_Property_Company_2nd 商品评论表
-- 数据库: PurchaseBase (与 products 表同一个库)
-- 表: comments (商品评论) / comment_messages (收到评论消息)
-- =============================================
USE `PurchaseBase`;

CREATE TABLE IF NOT EXISTS `comments` (
    `id`           INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论ID(主键)',
    `product_name` VARCHAR(100) NOT NULL COMMENT '被评论的商品名称',
    `username`     VARCHAR(50)  NOT NULL COMMENT '评论用户(登录账号)',
    `content`      TEXT         NOT NULL COMMENT '评论内容',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    PRIMARY KEY (`id`),
    KEY `idx_product_name` (`product_name`) COMMENT '商品名索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品评论表';

-- =============================================
-- 收到评论消息表：当有人回复你的评论时生成一条消息
-- 回复格式约定: 内容以 [回复#父评论ID] 开头 (前端自动拼接)
-- =============================================
CREATE TABLE IF NOT EXISTS `comment_messages` (
    `id`           INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息ID(主键)',
    `receiver`     VARCHAR(50)  NOT NULL COMMENT '接收者(被回复人)用户名',
    `sender`       VARCHAR(50)  NOT NULL COMMENT '回复者用户名',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `comment_id`   INT UNSIGNED NOT NULL COMMENT '对应回复评论的ID',
    `content`      TEXT         NOT NULL COMMENT '回复内容',
    `is_read`      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已读:0-未读,1-已读',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息时间',
    PRIMARY KEY (`id`),
    KEY `idx_receiver_read` (`receiver`, `is_read`) COMMENT '接收者+已读索引',
    KEY `idx_comment_id` (`comment_id`) COMMENT '评论ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收到评论消息表';
