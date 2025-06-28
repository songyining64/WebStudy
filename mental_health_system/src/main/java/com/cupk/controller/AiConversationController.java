package com.cupk.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cupk.entity.AiConversation;
import com.cupk.service.AiConversationService;
import com.cupk.service.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
public class AiConversationController {

    @Autowired
    private AiConversationService aiService;

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, String> payload) {
        String userInput = payload.get("message");
        String sessionId = payload.get("sessionId");
        Long userId = Long.valueOf(payload.get("userId")); // ⚠️ 前端需传

        if (userInput == null || userInput.trim().isEmpty()) {
            return Result.error("消息不能为空");
        }
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return Result.error("sessionId 不能为空");
        }

        String aiReply = aiService.chat(sessionId, userInput, userId);
        return Result.success(aiReply);
    }


    @GetMapping("/history")
    public Result<IPage<AiConversation>> getHistory(
        @RequestParam Long userId,
        @RequestParam String sessionId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
        ) {
        LambdaQueryWrapper<AiConversation> query = new LambdaQueryWrapper<AiConversation>()
            .eq(AiConversation::getUserId, userId)
            .eq(AiConversation::getSessionId, sessionId)
            .orderByAsc(AiConversation::getCreatedAt);

        IPage<AiConversation> result = aiService.page(new Page<>(page, size), query);
        return Result.success(result);
    }

    @GetMapping("/session/new")
    public Result<String> newSession() {
        String sessionId = UUID.randomUUID().toString();
        return Result.success(sessionId);
    }

}
