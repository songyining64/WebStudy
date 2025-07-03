package com.cupk.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cupk.entity.AiConversation;
import com.cupk.entity.User;
import com.cupk.service.AiConversationService;
import com.cupk.service.Result;
import com.cupk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173" }, allowCredentials = "true")
@RestController
@RequestMapping("/api/ai")
public class AiConversationController {

    @Autowired
    private AiConversationService aiService;

    @Autowired
    private UserService userService;

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, String> payload) {
        String userInput = payload.get("message");
        String sessionId = payload.get("sessionId");
        Long userId = Long.valueOf(payload.get("userId"));
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在，请重新登录");
        }
        if (userInput == null || userInput.trim().isEmpty()) {
            return Result.error("消息不能为空");
        }
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return Result.error("sessionId 不能为空");
        }

        String aiRawResponse = aiService.chat(sessionId, userInput, userId);
        // 直接返回原始 AI 响应给前端
        return Result.success(aiRawResponse);
    }

    @GetMapping("/history")
    public Result<?> getHistory(
            @RequestParam Long userId,
            @RequestParam String sessionId) {
        // 返回所有历史消息
        List<AiConversation> records = aiService.lambdaQuery()
                .eq(AiConversation::getUserId, userId)
                .eq(AiConversation::getSessionId, sessionId)
                .orderByAsc(AiConversation::getCreatedAt)
                .list();
        return Result.success(Map.of("records", records));
    }

    @GetMapping("/session/new")
    public Result<String> newSession() {
        String sessionId = UUID.randomUUID().toString();
        return Result.success(sessionId);
    }

    @GetMapping("/sessions")
    public Result<List<String>> getUserSessions(@RequestParam Long userId) {
        List<String> sessionIds = aiService.getUserSessionIds(userId);
        return Result.success(sessionIds);
    }

}

