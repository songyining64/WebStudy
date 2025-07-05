-- 修复系统公告表中的时间字段，添加随机的时分秒
-- 这个脚本会将固定的08:00:00时间更新为随机的时间

-- 显示当前时间记录的状态
SELECT 'Before update - Current state of system_notice times:' AS message;
SELECT id, title, DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') as create_time, 
       DATE_FORMAT(update_time, '%Y-%m-%d %H:%i:%s') as update_time 
FROM system_notice;

-- 更新创建时间，添加随机的小时（9-23）和分钟（0-59）
UPDATE system_notice
SET create_time = DATE_ADD(
    DATE_FORMAT(create_time, '%Y-%m-%d 00:00:00'), 
    INTERVAL (9 + FLOOR(RAND() * 15)) HOUR + INTERVAL FLOOR(RAND() * 60) MINUTE
)
WHERE TIME(create_time) = '08:00:00';

-- 更新更新时间，确保更新时间不早于创建时间
UPDATE system_notice
SET update_time = DATE_ADD(
    create_time,
    INTERVAL FLOOR(RAND() * 120) MINUTE
)
WHERE TIME(update_time) = '08:00:00';

-- 确保状态字段正确设置
UPDATE system_notice
SET status = 1
WHERE status IS NULL;

-- 打印更新后的结果
SELECT 'After update - Updated system_notice times:' AS message;
SELECT id, title, DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') as create_time, 
       DATE_FORMAT(update_time, '%Y-%m-%d %H:%i:%s') as update_time 
FROM system_notice 
ORDER BY create_time DESC; 