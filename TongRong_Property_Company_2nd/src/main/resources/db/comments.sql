-- =============================================
-- TongRong_Property_Company_2nd 商品评论表
-- 数据库: PurchaseBase (与 products 表同一个库)
-- 表: comments (商品评论)
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
