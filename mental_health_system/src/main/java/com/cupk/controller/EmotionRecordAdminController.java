package com.cupk.controller;

import com.cupk.entity.EmotionRecord;
import com.cupk.service.EmotionRecordService;
import com.cupk.service.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/emotion-records")
public class EmotionRecordAdminController {

    @Autowired
    private EmotionRecordService emotionRecordService;

    /**
     * 分页查询情绪记录
     * 
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词（用户名或情绪类型）
     * @return 分页结果
     */
    @GetMapping
    public Result<IPage<EmotionRecord>> pageEmotionRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        try {
            IPage<EmotionRecord> records = emotionRecordService.pageEmotionRecords(page, size, keyword);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error("查询情绪记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取情绪记录详情
     * 
     * @param id 记录ID
     * @return 记录详情
     */
    @GetMapping("/{id}")
    public Result<EmotionRecord> getEmotionRecord(@PathVariable Long id) {
        try {
            EmotionRecord record = emotionRecordService.getById(id);
            if (record == null) {
                return Result.error("情绪记录不存在");
            }
            return Result.success(record);
        } catch (Exception e) {
            return Result.error("获取情绪记录失败: " + e.getMessage());
        }
    }

    /**
     * 删除情绪记录
     * 
     * @param id 记录ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteEmotionRecord(@PathVariable Long id) {
        try {
            boolean success = emotionRecordService.deleteEmotionRecord(id);
            if (success) {
                return Result.success("删除情绪记录成功", true);
            } else {
                return Result.error("删除情绪记录失败");
            }
        } catch (Exception e) {
            return Result.error("删除情绪记录失败: " + e.getMessage());
        }
    }
}