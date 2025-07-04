package com.cupk.controller;

import com.cupk.entity.EmotionRecord;
import com.cupk.service.EmotionRecordService;
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
import java.util.HashMap;

/**
 * 情绪记录控制器
 */
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173" }, allowCredentials = "true")
@RestController
@RequestMapping("/api/emotion-record")
public class EmotionRecordController {
    private static final Logger logger = LoggerFactory.getLogger(EmotionRecordController.class);

    @Autowired
    private EmotionRecordMapper emotionRecordMapper;

    @Autowired
    private com.cupk.client.DeepSeekClient deepSeekClient;

    @Autowired
    private EmotionRecordService emotionRecordService;

    /**
     * 查询所有情绪记录
     */
    @GetMapping
    public Result<List<EmotionRecord>> list() {
        try {
            // 使用分页查询第一页，获取较小数据量
            return Result.success(emotionRecordService.pageEmotionRecords(1, 50, null).getRecords());
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
            return Result.success(emotionRecordService.getUserRecords(userId, 1, 50).getRecords());
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
            EmotionRecord record = new EmotionRecord();
            record.setUserId(Long.valueOf(payload.get("userId").toString()));
            // record.setContent(payload.get("answers").toString()); // 移除不存在的setContent方法

            // 读取 persona prompt
            String persona = new String(
                    getClass().getClassLoader().getResourceAsStream("prompt/deepseek-persona.txt").readAllBytes(),
                    java.nio.charset.StandardCharsets.UTF_8);
            String assessmentContext = payload.get("answers").toString();
            String prompt = persona.replace("{{ assessment_context }}", assessmentContext);

            // 调用 DeepSeek AI
            String aiReport = deepSeekClient.chat(prompt);
            System.out.println("AI返回内容: " + aiReport);

            // 解析AI返回内容，假设格式为：主情绪：xxx\n分析报告：yyy
            String mainEmotion = "AI分析";
            String analysisReport = aiReport;
            if (aiReport != null && aiReport.contains("主情绪：")) {
                int start = aiReport.indexOf("主情绪：") + 4;
                int end = aiReport.indexOf("\n", start);
                if (end == -1) end = aiReport.length();
                mainEmotion = aiReport.substring(start, end).trim();
                int reportIdx = aiReport.indexOf("分析报告：");
                if (reportIdx != -1) {
                    analysisReport = aiReport.substring(reportIdx + 5).trim();
                }
            }

            // 存AI报告到数据库
            record.setAnalysisReport(analysisReport);
            record.setRecordTime(java.time.LocalDateTime.now());
            // 设置主情绪类型
            record.setEmotion(mainEmotion);
            record.setRemark("AI情绪分析结果");
            // 使用 service 添加记录
            emotionRecordService.addRecord(record);

            // 返回AI报告给前端
            Map<String, Object> result = new HashMap<>();
            result.put("report", aiReport);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存情绪评估记录失败");
        }
    }
}