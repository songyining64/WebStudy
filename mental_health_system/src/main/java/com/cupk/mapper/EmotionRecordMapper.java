package com.cupk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cupk.entity.EmotionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmotionRecordMapper extends BaseMapper<EmotionRecord> {
    /**
     * 分页查询情绪记录，支持按用户名和情绪类型搜索
     * 
     * @param page    分页参数
     * @param keyword 关键词（用户名或情绪类型）
     * @return 分页结果
     */
    IPage<EmotionRecord> pageEmotionRecords(Page<EmotionRecord> page, @Param("keyword") String keyword);
}