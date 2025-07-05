package com.cupk.controller;

import com.cupk.service.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${upload.dir}")
    private String uploadDir;

    @PostMapping("/image")
    public Result<?> uploadImage(@RequestParam("file") MultipartFile file) {
        // Print the uploadDir here, before it's used
        System.out.println("图片上传目录: " + uploadDir);

        if (file.isEmpty()) {
            return Result.error("文件为空");
        }
        
        // 创建图片子目录
        String imageDir = uploadDir + "image/";
        File imageDirectory = new File(imageDir);
        if (!imageDirectory.exists()) {
            imageDirectory.mkdirs();
        }
        
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String newFileName = UUID.randomUUID().toString() + ext;

        File dest = new File(imageDirectory, newFileName);
        System.out.println("图片实际保存路径: " + dest.getAbsolutePath());
        try {
            file.transferTo(dest);
            if (!dest.exists())
                return Result.error("文件保存失败");
            String url = "/static/upload/image/" + newFileName;
            return Result.success(url);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}