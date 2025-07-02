package com.cupk.controller;

import com.cupk.entity.EmotionRecord;
import com.cupk.service.Result;
import com.cupk.mapper.EmotionRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 情绪记录控制器
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/emotion-record")
public class EmotionRecordController {
    private static final Logger logger = LoggerFactory.getLogger(EmotionRecordController.class);

    @Autowired
    private EmotionRecordMapper emotionRecordMapper;

    /**
     * 查询所有情绪记录
     */
    @GetMapping
    public Result<List<EmotionRecord>> list() {
        try {
            List<EmotionRecord> records = emotionRecordMapper.selectList(null);
            return Result.success(records);
        } catch (Exception e) {
            logger.error("获取情绪记录列表失败", e);
            return Result.error("获取情绪记录列表失败");
        }
    }

    /**
     * 根据用户ID获取情绪记录
     */
    @GetMapping("/user/{userId}")
    public Result<List<EmotionRecord>> getByUserId(@PathVariable Long userId) {
        try {
            List<EmotionRecord> records = emotionRecordMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<EmotionRecord>()
                            .eq("user_id", userId)
                            .orderByDesc("record_time")
            );
            return Result.success(records);
        } catch (Exception e) {
            logger.error("获取用户情绪记录列表失败: userId={}", userId, e);
            return Result.error("获取用户情绪记录列表失败");
        }
    }

    /**
     * 保存情绪评估记录（问卷评估专用）
     */
    @PostMapping("/record")
    public Result<?> saveRecord(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("收到情绪评估保存请求: " + payload);
            EmotionRecord record = new EmotionRecord();
            // 用户ID
            record.setUserId(Long.valueOf(payload.get("userId").toString()));
            // content 存问卷答案
            record.setContent(payload.get("answers").toString());
            // emotionType 存评估结果类型（如良好/一般/波动等）
            record.setEmotionType(payload.getOrDefault("emotionType", payload.getOrDefault("result", "")).toString());
            // confidence 存分数
            if (payload.get("score") != null) {
                try {
                    record.setConfidence(new BigDecimal(payload.get("score").toString()));
                } catch (Exception e) {
                    record.setConfidence(BigDecimal.ZERO);
                }
            }
            // recordTime 存当前时间
            record.setRecordTime(LocalDateTime.now());
            // suggestions 存评语
            record.setSuggestions(payload.getOrDefault("result", "").toString());
            int rows = emotionRecordMapper.insert(record);
            System.out.println("插入结果: " + rows + ", 记录: " + record);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存情绪评估记录失败", e);
            return Result.error("保存情绪评估记录失败");
        }
    }
}