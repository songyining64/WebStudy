package com.cupk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.dir}")
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
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists())
            uploadDirectory.mkdirs();
        String uploadPath = uploadDirectory.getAbsolutePath();
        if (!uploadPath.endsWith(File.separator))
            uploadPath += File.separator;
        String fileProtocolPath = "file:" + uploadPath;

        registry.addResourceHandler("/static/upload/**")
                .addResourceLocations(fileProtocolPath);
        registry.addResourceHandler("/mental/static/upload/**")
                .addResourceLocations(fileProtocolPath);
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(fileProtocolPath);
        registry.addResourceHandler("/mental/upload/**")
                .addResourceLocations(fileProtocolPath);

        System.out.println("静态资源映射目录: " + fileProtocolPath);
    }
}