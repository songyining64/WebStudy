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

import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*")
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
        Long userId = null;
        try {
            userId = Long.valueOf(payload.get("userId"));
        } catch (Exception e) {
            return Result.error("用户ID格式错误，请重新登录");
        }
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
        String aiReply = aiService.chat(sessionId, userInput, userId);
        return Result.success(aiReply);
    }

    @GetMapping("/history")
    public Result<IPage<AiConversation>> getHistory(
            @RequestParam Long userId,
            @RequestParam String sessionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
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
