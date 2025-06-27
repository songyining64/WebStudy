package com.cupk.util;

import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class PromptLoader {

    public static String load(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("无法加载Prompt: " + path, e);
        }
    }
}
