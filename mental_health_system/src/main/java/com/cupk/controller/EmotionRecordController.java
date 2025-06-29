package com.cupk.controller;

import com.cupk.entity.EmotionRecord;
import com.cupk.service.EmotionRecordService;
import com.cupk.service.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 情绪记录控制器
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/emotion-records")
public class EmotionRecordController {
    private static final Logger logger = LoggerFactory.getLogger(EmotionRecordController.class);

    @Autowired
    private EmotionRecordService emotionRecordService;

    /**
     * 获取所有情绪记录
     */
    @GetMapping
    public Result<List<EmotionRecord>> list() {
        try {
            List<EmotionRecord> records = emotionRecordService.list();
            return Result.success(records);
        } catch (Exception e) {
            logger.error("获取情绪记录列表失败", e);
            return Result.error("获取情绪记录列表失败");
        }
    }

    /**
     * 根据ID获取情绪记录
     */
    @GetMapping("/{id}")
    public Result<EmotionRecord> get(@PathVariable Long id) {
        try {
            EmotionRecord record = emotionRecordService.getById(id);
            if (record != null) {
                return Result.success(record);
            } else {
                return Result.error("未找到对应的情绪记录");
            }
        } catch (Exception e) {
            logger.error("获取情绪记录失败: id={}", id, e);
            return Result.error("获取情绪记录失败");
        }
    }

    /**
     * 添加情绪记录
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody EmotionRecord record) {
        try {
            boolean success = emotionRecordService.save(record);
            return success ? Result.success(true) : Result.error("保存情绪记录失败");
        } catch (Exception e) {
            logger.error("保存情绪记录失败", e);
            return Result.error("保存情绪记录失败");
        }
    }

    /**
     * 更新情绪记录
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody EmotionRecord record) {
        try {
            if (record.getId() == null) {
                return Result.validateFailed("ID不能为空");
            }
            boolean success = emotionRecordService.updateById(record);
            return success ? Result.success(true) : Result.error("更新情绪记录失败");
        } catch (Exception e) {
            logger.error("更新情绪记录失败", e);
            return Result.error("更新情绪记录失败");
        }
    }

    /**
     * 删除情绪记录
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        try {
            boolean success = emotionRecordService.removeById(id);
            return success ? Result.success(true) : Result.error("删除情绪记录失败");
        } catch (Exception e) {
            logger.error("删除情绪记录失败: id={}", id, e);
            return Result.error("删除情绪记录失败");
        }
    }

    /**
     * 根据用户ID获取情绪记录
     */
    @GetMapping("/user/{userId}")
    public Result<List<EmotionRecord>> getByUserId(@PathVariable Long userId) {
        try {
            List<EmotionRecord> records = emotionRecordService.lambdaQuery()
                    .eq(EmotionRecord::getUserId, userId)
                    .orderByDesc(EmotionRecord::getRecordTime)
                    .list();
            return Result.success(records);
        } catch (Exception e) {
            logger.error("获取用户情绪记录列表失败: userId={}", userId, e);
            return Result.error("获取用户情绪记录列表失败");
        }
    }
}