package com.cupk.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/resource")
public class ResourceController {

    @Value("${upload.dir:src/main/resources/static/upload/}")
    private String uploadDir;

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return "文件为空";
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
        return "/upload/" + newFileName;
    }
}