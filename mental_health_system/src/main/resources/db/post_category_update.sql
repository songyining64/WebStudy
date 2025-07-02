-- 添加分类字段到帖子表
ALTER TABLE post ADD COLUMN category VARCHAR(50) COMMENT '帖子分类';

-- 更新已有帖子的分类（根据标签提取分类）
UPDATE post 
SET category = CASE
    WHEN tags LIKE '%心理健康%' THEN '心理健康'
    WHEN tags LIKE '%学习方法%' THEN '学习方法'
    WHEN tags LIKE '%情感%' THEN '情感'
    WHEN tags LIKE '%压力管理%' THEN '压力管理'
    WHEN tags LIKE '%社交%' THEN '社交'
    WHEN tags LIKE '%职业发展%' THEN '职业发展'
    WHEN tags LIKE '%健康%' THEN '健康'
    WHEN tags LIKE '%生活%' THEN '生活'
    ELSE '其他'
END;

-- 创建索引以提高查询性能
CREATE INDEX idx_post_category ON post(category);

-- 更新帖子表，确保新帖子默认有分类
ALTER TABLE post MODIFY COLUMN category VARCHAR(50) NOT NULL DEFAULT '其他' COMMENT '帖子分类'; 