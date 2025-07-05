package com.cupk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cupk.client.DeepSeekClient;
import com.cupk.entity.AiConversation;
import com.cupk.entity.UserAssessment;
import com.cupk.mapper.AiConversationMapper;
import com.cupk.mapper.UserMapper;
import com.cupk.service.AiConversationService;
import com.cupk.service.UserAssessmentService;
import com.cupk.util.PromptLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiConversationServiceImpl
        extends ServiceImpl<AiConversationMapper, AiConversation>
        implements AiConversationService {

    @Autowired
    private DeepSeekClient deepSeekClient;

    @Autowired
    private AiConversationMapper conversationMapper;

    @Autowired
    private UserAssessmentService assessmentService;

    @Autowired
    private UserMapper userMapper;

    private static final String PROMPT = PromptLoader.load("prompt/deepseek-persona.txt");

    @Override
    public String chat(String sessionId, String userMessage, Long userId) {
        // 校验 userId 是否存在
        if (userMapper.selectById(userId) == null) {
            throw new RuntimeException("用户不存在");
        }

        List<DeepSeekClient.Message> messages = new ArrayList<>();

        boolean isFirstMessage = lambdaQuery()
                .eq(AiConversation::getSessionId, sessionId)
                .eq(AiConversation::getUserId, userId)
                .count() == 0;

        UserAssessment assessment = assessmentService.lambdaQuery()
                .eq(UserAssessment::getUserId, userId)
                .orderByDesc(UserAssessment::getId)
                .last("LIMIT 1")
                .one();

        String assessmentContext = assessment != null ?
            "\n用户心理评估背景:\n" + assessment.getReport() : "";

        String fullPrompt = PROMPT + assessmentContext;

        if (isFirstMessage) {
            messages.add(new DeepSeekClient.Message("system", fullPrompt));
        }

        messages.add(new DeepSeekClient.Message("user", userMessage));

        String rawResponse = deepSeekClient.ask(messages);
        String emotionJson = extractEmotionJson(rawResponse);

        AiConversation record = new AiConversation();
        record.setUserId(userId);
        record.setSessionId(sessionId);
        record.setUserMessage(userMessage);
        record.setAiResponse(rawResponse);
        record.setEmotionAnalysis(emotionJson);
        record.setCreatedAt(LocalDateTime.now());

        save(record);
        return rawResponse;
    }

    private String extractEmotionJson(String aiResponse) {
        if (aiResponse == null) return null;

        Pattern pattern = Pattern.compile("🌡️\\s*(\\{.*?})", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(aiResponse);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    @Override
    public List<String> getUserSessionIds(Long userId) {
        // 查询每个 sessionId 的最新 created_at
        List<Map<String, Object>> list = this.baseMapper.selectMaps(
            new QueryWrapper<AiConversation>()
                .select("session_id, MAX(created_at) as last_time")
                .eq("user_id", userId)
                .groupBy("session_id")
                .orderByDesc("last_time")
        );
        // 提取 session_id
        return list.stream()
            .map(m -> (String) m.get("session_id"))
            .toList();
    }
}

