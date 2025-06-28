package com.cupk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.client.DeepSeekClient;
import com.cupk.entity.AiConversation;
import com.cupk.mapper.AiConversationMapper;
import com.cupk.service.AiConversationService;
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

    private static final String PROMPT = PromptLoader.load("prompt/deepseek-persona.txt");

    @Override
        public String chat(String sessionId, String userMessage, Long userId) {
                List<DeepSeekClient.Message> messages = new ArrayList<>();

                boolean isFirstMessage = lambdaQuery()
                        .eq(AiConversation::getSessionId, sessionId)
                        .eq(AiConversation::getUserId, userId)
                        .count() == 0;

                    if (isFirstMessage) {
                        messages.add(new DeepSeekClient.Message("system", PROMPT));
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



    /**
     * 提取 AI 回复中的 JSON 情绪分析部分（🌡️{...}）
     */
    private String extractEmotionJson(String aiResponse) {
        if (aiResponse == null) return null;

        // 使用正则提取 🌡️ 开头 + JSON 格式字符串
        Pattern pattern = Pattern.compile("🌡️\\s*(\\{.*?})", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(aiResponse);

        if (matcher.find()) {
            return matcher.group(1); // 仅返回 {...}
        }

        return null;
    }
}
