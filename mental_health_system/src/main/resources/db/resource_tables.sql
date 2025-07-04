-- 检查外键约束是否存在，如果存在则删除
SET @fk1 = (SELECT COUNT(*) FROM information_schema.table_constraints 
            WHERE constraint_name = 'fk_video_resource_user' 
            AND table_name = 'video_resource');
SET @sql1 = IF(@fk1 > 0, 'ALTER TABLE video_resource DROP FOREIGN KEY fk_video_resource_user', 'SELECT 1');
PREPARE stmt1 FROM @sql1;
EXECUTE stmt1;
DEALLOCATE PREPARE stmt1;

SET @fk2 = (SELECT COUNT(*) FROM information_schema.table_constraints 
            WHERE constraint_name = 'fk_text_resource_user' 
            AND table_name = 'text_resource');
SET @sql2 = IF(@fk2 > 0, 'ALTER TABLE text_resource DROP FOREIGN KEY fk_text_resource_user', 'SELECT 1');
PREPARE stmt2 FROM @sql2;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;

-- 修改字段，允许为空
ALTER TABLE video_resource MODIFY uploader_id BIGINT NULL;
ALTER TABLE text_resource MODIFY author_id BIGINT NULL;

-- 检查情绪标签字段是否存在，如果不存在则添加
SET @col1 = (SELECT COUNT(*) FROM information_schema.columns 
             WHERE table_name = 'video_resource' 
             AND column_name = 'emotion_tag');
SET @sql3 = IF(@col1 = 0, 'ALTER TABLE video_resource ADD COLUMN emotion_tag VARCHAR(50) DEFAULT "default" COMMENT "情绪标签"', 'SELECT 1');
PREPARE stmt3 FROM @sql3;
EXECUTE stmt3;
DEALLOCATE PREPARE stmt3;

SET @col2 = (SELECT COUNT(*) FROM information_schema.columns 
             WHERE table_name = 'text_resource' 
             AND column_name = 'emotion_tag');
SET @sql4 = IF(@col2 = 0, 'ALTER TABLE text_resource ADD COLUMN emotion_tag VARCHAR(50) DEFAULT "default" COMMENT "情绪标签"', 'SELECT 1');
PREPARE stmt4 FROM @sql4;
EXECUTE stmt4;
DEALLOCATE PREPARE stmt4;

-- 视频资源表（如果不存在则创建）
CREATE TABLE IF NOT EXISTS `video_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '标题',
  `description` text COMMENT '描述',
  `url` varchar(1024) NOT NULL COMMENT '视频URL',
  `thumbnail_url` varchar(1024) COMMENT '缩略图URL',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `uploader_id` bigint NULL COMMENT '上传者ID',
  `emotion_tag` varchar(50) DEFAULT 'default' COMMENT '情绪标签',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频资源表';

-- 文案资源表（如果不存在则创建）
CREATE TABLE IF NOT EXISTS `text_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `author_id` bigint NULL COMMENT '作者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `emotion_tag` varchar(50) DEFAULT 'default' COMMENT '情绪标签',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文案资源表';

-- 初始视频数据
INSERT IGNORE INTO `video_resource` (`title`, `description`, `url`, `thumbnail_url`, `emotion_tag`, `uploader_id`)
VALUES 
('放松冥想指导', '10分钟放松冥想，帮助缓解压力和焦虑', 'https://example.com/videos/meditation.mp4', 'https://example.com/thumbnails/meditation.jpg', 'low_risk', 1),
('情绪管理技巧', '学习如何识别和管理负面情绪', 'https://example.com/videos/emotion-management.mp4', 'https://example.com/thumbnails/emotion.jpg', 'medium_risk', 1),
('寻求帮助指南', '当你感到overwhelmed时如何寻求专业帮助', 'https://example.com/videos/seeking-help.mp4', 'https://example.com/thumbnails/help.jpg', 'high_risk', 1);

-- 初始文案数据
INSERT IGNORE INTO `text_resource` (`title`, `content`, `emotion_tag`, `author_id`)
VALUES
('每日积极肯定', '每天花5分钟对自己说积极的肯定语，例如"我很强大"、"我能够克服困难"等，可以帮助建立积极的思维模式。', 'low_risk', 1),
('情绪日记指南', '记录你的情绪可以帮助你更好地理解自己。每天花几分钟写下你的感受、触发因素以及如何应对，长期坚持会看到情绪模式。', 'medium_risk', 1),
('危机应对计划', '当你感到极度不安或有自伤想法时，请遵循以下步骤：1. 深呼吸 2. 联系信任的人 3. 使用分散注意力的技巧 4. 如有必要，拨打心理危机热线。', 'high_risk', 1); 