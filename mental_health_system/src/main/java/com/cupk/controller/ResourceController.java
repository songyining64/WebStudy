package com.cupk.controller;

import com.cupk.service.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ResourceController {

    @Value("${upload.dir:src/main/resources/static/upload/}")
    private String uploadDir;

    @PostMapping("/resource/upload")
    public Result<String> uploadResource(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }
        String originalFilename = file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(originalFilename);
        String newFileName = UUID.randomUUID().toString() + "." + ext;
        File destDir = new File(uploadDir);
        if (!destDir.exists()) {
            destDir.mkdirs(); // 自动创建目录
        }
        File dest = new File(destDir, newFileName);
        file.transferTo(dest);

        // 构建完整的URL路径
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String fileUrl = baseUrl + "/upload/" + newFileName;

        return Result.success(fileUrl);
    }

    @PostMapping("/upload/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只支持上传图片文件");
        }

        // 验证文件大小（限制为5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("图片大小不能超过5MB");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(originalFilename);
        String newFileName = UUID.randomUUID().toString() + "." + ext;

        // 确保上传目录存在
        File destDir = new File(uploadDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        // 保存文件
        File dest = new File(destDir, newFileName);
        file.transferTo(dest);

        // 构建完整的URL路径
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String fileUrl = baseUrl + "/upload/" + newFileName;

        // 返回文件URL
        Map<String, String> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("filename", newFileName);

        // 打印日志，便于调试
        System.out.println("上传图片成功，URL: " + fileUrl);

        return Result.success(result);
    }
}