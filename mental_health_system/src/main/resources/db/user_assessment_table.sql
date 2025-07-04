CREATE TABLE IF NOT EXISTS `user_assessment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(64) COMMENT '用户名',
    `assessment_title` VARCHAR(128) COMMENT '问卷标题',
    `score` INT COMMENT '得分',
    `submit_time` DATETIME COMMENT '提交时间',
    `detail` TEXT COMMENT '详细信息（JSON格式）',
    `questionnaire_id` BIGINT COMMENT '问卷ID，关联到问卷表',
    `answers` TEXT COMMENT '用户问卷答案（JSON格式）',
    `content` TEXT COMMENT '问卷内容',
    `suggestions` TEXT COMMENT '建议',
    `report` TEXT COMMENT '评估报告',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_submit_time` (`submit_time`),
    KEY `idx_questionnaire_id` (`questionnaire_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户评估问卷表'; 