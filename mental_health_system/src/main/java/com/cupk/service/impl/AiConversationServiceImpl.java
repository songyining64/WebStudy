package com.cupk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.client.DeepSeekClient;
import com.cupk.entity.AiConversation;
import com.cupk.mapper.AiConversationMapper;
import com.cupk.service.AiConversationService;
import com.cupk.util.PromptLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiConversationServiceImpl
        extends ServiceImpl<AiConversationMapper, AiConversation>  // ✅ 注意：继承的是 MyBatis Plus 提供的默认实现
        implements AiConversationService {

    @Autowired
    private DeepSeekClient deepSeekClient;

    private static final String PROMPT = PromptLoader.load("prompt/deepseek-persona.txt");

    @Override
    public String chat(String userMessage) {
        List<DeepSeekClient.Message> messages = new ArrayList<>();
        messages.add(new DeepSeekClient.Message("system", PROMPT));
        messages.add(new DeepSeekClient.Message("user", userMessage));
        return deepSeekClient.ask(messages);
    }
}
