package com.cupk.client;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class DeepSeekClient {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 使用多个消息调用DeepSeek API
     * 
     * @param messages 消息列表
     * @return AI回复内容
     */
    public String ask(List<Message> messages) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "deepseek-chat");
        payload.put("messages", messages);
        payload.put("temperature", 0.7);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

        return response.getBody();
    }

    /**
     * 使用单个提示词调用DeepSeek API
     * 
     * @param prompt 提示词内容
     * @return AI回复内容
     */
    public String chat(String prompt) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", "你是一位专业的心理咨询助手，负责提供专业、有帮助的回复。"));
        messages.add(new Message("user", prompt));
        return ask(messages);
    }

    @Data
    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
