package com.cupk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.dir:src/main/resources/static/upload/}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Disposition")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保上传目录存在
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }

        // 获取上传目录的绝对路径
        String uploadPath = uploadDirectory.getAbsolutePath();

        // 添加资源处理器，将/upload/**的请求映射到文件系统中的uploadPath
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadPath + "/");

        // 添加资源处理器，将/mental/upload/**的请求映射到文件系统中的uploadPath
        registry.addResourceHandler("/mental/upload/**")
                .addResourceLocations("file:" + uploadPath + "/");

        // 日志输出配置的路径
        System.out.println("配置的上传目录路径: " + uploadPath);
        System.out.println("已配置的资源映射:");
        System.out.println("  /upload/** -> " + uploadPath + "/");
        System.out.println("  /mental/upload/** -> " + uploadPath + "/");
    }
}