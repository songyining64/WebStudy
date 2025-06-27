package com.cupk.controller;

import com.cupk.service.AiConversationService;
import com.cupk.service.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiConversationController {

    @Autowired
    private AiConversationService aiService;

    @PostMapping("/chat")
        public Result<String> chat(@RequestBody Map<String, String> payload) {
        String userInput = payload.get("message");
        if (userInput == null || userInput.trim().isEmpty()) {
            return Result.error("消息不能为空");
        }
        String aiReply = aiService.chat(userInput);
        return Result.success(aiReply);
    }
}
