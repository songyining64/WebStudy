CREATE TABLE IF NOT EXISTS `emotion_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(64) COMMENT '用户名',
    `emotion` VARCHAR(32) COMMENT '情绪类型',
    `record_time` DATETIME COMMENT '记录时间',
    `remark` VARCHAR(255) COMMENT '备注',
    `content` TEXT COMMENT '内容',
    `suggestions` TEXT COMMENT '建议',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_record_time` (`record_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='情绪记录表'; 