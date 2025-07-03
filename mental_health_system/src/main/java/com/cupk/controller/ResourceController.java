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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/resource")
public class ResourceController {

    @Autowired
    private VideoResourceService videoResourceService;

    @Autowired
    private TextResourceService textResourceService;

    @Autowired
    private UserAssessmentService userAssessmentService;

    @Value("${upload.dir:src/main/resources/static/upload/}")
    private String uploadDir;

    // 推荐资源接口

    @GetMapping("/recommend")
    public Result<?> recommendResources(@RequestParam Long userId) {
        UserAssessment latestAssessment = userAssessmentService.getLatestAssessment(userId);

        List<VideoResource> videoResources = new ArrayList<>();
        List<TextResource> textResources = new ArrayList<>();

        if (latestAssessment != null) {
            String emotionTag = extractEmotionTagFromAssessment(latestAssessment.getReport());
            videoResources = videoResourceService.list(new QueryWrapper<VideoResource>().eq("emotion_tag", emotionTag));
            textResources = textResourceService.list(new QueryWrapper<TextResource>().eq("emotion_tag", emotionTag));
        } else {
            videoResources = videoResourceService.list(new QueryWrapper<VideoResource>().eq("emotion_tag", "default"));
            textResources = textResourceService.list(new QueryWrapper<TextResource>().eq("emotion_tag", "default"));
        }

        return Result.success(new ResourceRecommendation(videoResources, textResources));
    }

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

    private String extractEmotionTagFromAssessment(String report) {
        if (report == null || report.isEmpty()) {
            return "default";
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(report);

            JsonNode riskLevelNode = root.get("risk_level");
            if (riskLevelNode != null && riskLevelNode.isTextual()) {
                String riskLevel = riskLevelNode.asText();
                switch (riskLevel) {
                    case "绿色":
                        return "low_risk";
                    case "黄色":
                        return "medium_risk";
                    case "红色":
                        return "high_risk";
                    default:
                        return "default";
                }
            } else {
                return "default";
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "default";
        }
    }

    // ========================= 管理接口 =========================

    @GetMapping("/videos")
    public Result<List<VideoResource>> listVideos() {
        return Result.success(videoResourceService.list());
    }

    @PostMapping("/videos")
    public Result<?> addVideo(@RequestBody VideoResource videoResource) {
        videoResource.setUploadTime(LocalDateTime.now());
        videoResourceService.save(videoResource);
        return Result.success();
    }

    @DeleteMapping("/videos/{id}")
    public Result<?> deleteVideo(@PathVariable Long id) {
        boolean success = videoResourceService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping("/videos")
    public Result<?> updateVideo(@RequestBody VideoResource videoResource) {
        boolean success = videoResourceService.updateById(videoResource);
        return success ? Result.success() : Result.error("更新失败");
    }

    @GetMapping("/texts")
    public Result<List<TextResource>> listTexts() {
        return Result.success(textResourceService.list());
    }

    @PostMapping("/texts")
    public Result<?> addText(@RequestBody TextResource textResource) {
        textResource.setCreateTime(LocalDateTime.now());
        textResourceService.save(textResource);
        return Result.success();
    }

    @DeleteMapping("/texts/{id}")
    public Result<?> deleteText(@PathVariable Long id) {
        boolean success = textResourceService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping("/texts")
    public Result<?> updateText(@RequestBody TextResource textResource) {
        textResource.setUpdateTime(LocalDateTime.now());
        boolean success = textResourceService.updateById(textResource);
        return success ? Result.success() : Result.error("更新失败");
    }

    @GetMapping("/image-info/{filename:.+}")
    public Result<?> getImageInfo(@PathVariable String filename) {
        File uploadDirectory = new File(uploadDir);
        File imageFile = new File(uploadDirectory, filename);

        Map<String, Object> info = new HashMap<>();
        info.put("filename", filename);
        info.put("exists", imageFile.exists());
        info.put("size", imageFile.exists() ? imageFile.length() : -1);
        info.put("path", imageFile.getAbsolutePath());
        info.put("canRead", imageFile.canRead());
        info.put("lastModified", imageFile.exists() ? new java.util.Date(imageFile.lastModified()).toString() : null);

        String[] possibleUrls = new String[] {
                "/static/upload/" + filename,
                "/mental/static/upload/" + filename,
                "/upload/" + filename,
                "/mental/upload/" + filename
        };
        info.put("possibleUrls", possibleUrls);
        info.put("uploadDirSetting", uploadDir);
        info.put("uploadDirExists", uploadDirectory.exists());

        return Result.success(info);
    }

    @GetMapping("/direct-image/{filename:.+}")
    public ResponseEntity<Resource> getImageDirect(@PathVariable String filename) throws IOException {
        File uploadDirectory = new File(uploadDir);
        Path imagePath = Paths.get(uploadDirectory.getAbsolutePath(), filename);

        if (!Files.exists(imagePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(imagePath.toFile());
        String contentType = Files.probeContentType(imagePath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + imagePath.getFileName().toString() + "\"")
                .body(resource);
    }

    @GetMapping("/list-uploads")
    public Result<?> listUploadDirectory() {
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists() || !uploadDirectory.isDirectory()) {
            return Result.error("上传目录不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("directory", uploadDirectory.getAbsolutePath());
        result.put("exists", uploadDirectory.exists());

        if (uploadDirectory.exists()) {
            File[] files = uploadDirectory.listFiles();
            String[] fileNames = new String[files != null ? files.length : 0];
            Map<String, Long> fileSizes = new HashMap<>();

            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    fileNames[i] = files[i].getName();
                    fileSizes.put(files[i].getName(), files[i].length());
                }
            }

            result.put("files", fileNames);
            result.put("fileSizes", fileSizes);
            result.put("fileCount", fileNames.length);
        }

        return Result.success(result);
    }
}
