package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.entity.EmotionRecord;
import com.cupk.mapper.EmotionRecordMapper;
import com.cupk.service.EmotionRecordService;
import org.springframework.stereotype.Service;

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
}