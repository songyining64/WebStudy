package com.cupk.controller;

import com.cupk.entity.TextResource;
import com.cupk.entity.VideoResource;
import com.cupk.entity.UserAssessment;
import com.cupk.service.TextResourceService;
import com.cupk.service.VideoResourceService;
import com.cupk.service.UserAssessmentService;
import com.cupk.service.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import jakarta.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

@RestController
@RequestMapping("/api/resource")
public class ResourceController {

    @Autowired
    private VideoResourceService videoResourceService;

    @Autowired
    private TextResourceService textResourceService;

    @Autowired
    private UserAssessmentService userAssessmentService;
    // 推荐资源接口

    @GetMapping("/recommend")
        // 1. 获取用户的最新评估结果
    public Result<?> recommendResources(@RequestParam Long userId) {
        UserAssessment latestAssessment = userAssessmentService.getLatestAssessment(userId);

        List<VideoResource> videoResources = new ArrayList<>();
        List<TextResource> textResources = new ArrayList<>();

            // 2. 根据评估结果推荐资源
        if (latestAssessment != null) {
            String emotionTag = extractEmotionTagFromAssessment(latestAssessment.getReport());
            videoResources = videoResourceService.list(new QueryWrapper<VideoResource>().eq("emotion_tag", emotionTag));
            textResources = textResourceService.list(new QueryWrapper<TextResource>().eq("emotion_tag", emotionTag));
            // 3. 使用默认资源
            // 从数据库获取默认资源，emotion_tag 为 "default"
        } else {
            videoResources = videoResourceService.list(new QueryWrapper<VideoResource>().eq("emotion_tag", "default"));
            textResources = textResourceService.list(new QueryWrapper<TextResource>().eq("emotion_tag", "default"));
        }
        // 4. 返回结果

        return Result.success(new ResourceRecommendation(videoResources, textResources));
    }
    // 内部类，用于封装推荐结果

    private static class ResourceRecommendation {
        private List<VideoResource> videos;
        private List<TextResource> texts;

        public ResourceRecommendation(List<VideoResource> videos, List<TextResource> texts) {
            this.videos = videos;
            this.texts = texts;
        }

        public List<VideoResource> getVideos() {
            return videos;
        }

        public List<TextResource> getTexts() {
            return texts;
        }
    }
    // 工具方法：从评估报告中提取情绪标签 (需要实现)
    private String extractEmotionTagFromAssessment(String report) {
        if (report == null || report.isEmpty()) {
            return "default";
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(report);

            // 提取 risk_level 字段
            JsonNode riskLevelNode = root.get("risk_level");
            if (riskLevelNode != null && riskLevelNode.isTextual()) {
                String riskLevel = riskLevelNode.asText();
                // 根据 risk_level 返回不同的情绪标签
                switch (riskLevel) {
                    case "绿色": // 低危机
                        return "low_risk";
                    case "黄色": // 中危机
                        return "medium_risk";
                    case "红色": // 高危机
                        return "high_risk";
                    default:
            return "default"; // 默认标签
        }
            } else {
                return "default"; // risk_level 字段不存在或类型不正确
    }

        } catch (IOException e) {
            e.printStackTrace();
            return "default"; // 解析 JSON 失败，返回默认标签
        }
    }
    // ========================= 管理接口 =========================

    // 获取所有视频资源（管理员权限）

    @GetMapping("/videos")
    public Result<List<VideoResource>> listVideos(HttpServletRequest request) {
        if (!isAdmin(request))
            return Result.error("无权限");
        return Result.success(videoResourceService.list());
    }
    // 添加视频资源（管理员权限）

    @PostMapping("/videos")
    public Result<?> addVideo(@RequestBody VideoResource videoResource, HttpServletRequest request) {
        if (!isAdmin(request))
            return Result.error("无权限");
        videoResource.setUploadTime(LocalDateTime.now());
        videoResourceService.save(videoResource);
        return Result.success();
    }
    // 删除视频资源（管理员权限）

    @DeleteMapping("/videos/{id}")
    public Result<?> deleteVideo(@PathVariable Long id, HttpServletRequest request) {
         if (!isAdmin(request))
             return Result.error("无权限");
        boolean success = videoResourceService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }
    // 更新视频资源（管理员权限）

    @PutMapping("/videos")
    public Result<?> updateVideo(@RequestBody VideoResource videoResource, HttpServletRequest request) {
         if (!isAdmin(request))
             return Result.error("无权限");
        boolean success = videoResourceService.updateById(videoResource);
        return success ? Result.success() : Result.error("更新失败");
    }
    // 获取所有文案资源（管理员权限）

    @GetMapping("/texts")
    public Result<List<TextResource>> listTexts(HttpServletRequest request) {
        if (!isAdmin(request))
            return Result.error("无权限");
        return Result.success(textResourceService.list());
    }
    // 添加文案资源（管理员权限）

    @PostMapping("/texts")
    public Result<?> addText(@RequestBody TextResource textResource, HttpServletRequest request) {
        if (!isAdmin(request))
            return Result.error("无权限");
        textResource.setCreateTime(LocalDateTime.now());
        textResourceService.save(textResource);
        return Result.success();
}
    // 删除文案资源（管理员权限）

    @DeleteMapping("/texts/{id}")
    public Result<?> deleteText(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request))
            return Result.error("无权限");
        boolean success = textResourceService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }
    // 更新文案资源（管理员权限）

    @PutMapping("/texts")
    public Result<?> updateText(@RequestBody TextResource textResource, HttpServletRequest request) {
        if (!isAdmin(request))
            return Result.error("无权限");
        textResource.setUpdateTime(LocalDateTime.now());
        boolean success = textResourceService.updateById(textResource);
        return success ? Result.success() : Result.error("更新失败");
    }
    // 工具方法：判断是否为管理员

    private boolean isAdmin(HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        return "admin".equals(role);
    }
}
