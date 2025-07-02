package com.cupk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cupk.entity.AiConversation;
import java.util.List;

public interface AiConversationService extends IService<AiConversation> {
    
    /**
     * 与 AI 对话，获取返回结果
     */
    String chat(String sessionId, String userMessage, Long userId);

    List<String> getUserSessionIds(Long userId);

}
