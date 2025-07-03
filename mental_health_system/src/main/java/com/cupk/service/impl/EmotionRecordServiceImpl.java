package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.entity.EmotionRecord;
import com.cupk.mapper.EmotionRecordMapper;
import com.cupk.service.EmotionRecordService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class EmotionRecordServiceImpl extends ServiceImpl<EmotionRecordMapper, EmotionRecord>
        implements EmotionRecordService {

    @Override
    public boolean addRecord(EmotionRecord record) {
        record.setRecordTime(LocalDateTime.now());
        return save(record);
    }

    @Override
    public IPage<EmotionRecord> getUserRecords(Long userId, int page, int size) {
        Page<EmotionRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<EmotionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmotionRecord::getUserId, userId)
                .orderByDesc(EmotionRecord::getRecordTime);
        return page(pageParam, wrapper);
    }

    @Override
    public long count() {
        return count(null);
    }

    @Override
    public IPage<EmotionRecord> pageEmotionRecords(int page, int size, String keyword) {
        Page<EmotionRecord> pageParam = new Page<>(page, size);

        try {
            if (StringUtils.hasText(keyword)) {
                // 使用自定义方法搜索
                return this.baseMapper.pageEmotionRecords(pageParam, keyword);
            } else {
                // 使用自定义方法但不带关键词
                return this.baseMapper.pageEmotionRecords(pageParam, null);
            }
        } catch (Exception e) {
            throw new RuntimeException("查询情绪记录失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteEmotionRecord(Long id) {
        if (id == null) {
            return false;
        }
        return this.removeById(id);
    }
}