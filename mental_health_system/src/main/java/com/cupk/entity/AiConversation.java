package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_conversation")
public class AiConversation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId; // 暂时先写死为 1，未来可扩展为多用户

    private String sessionId; // 会话标识（可选，留作分组分页）

    private String userMessage;

    private String aiResponse;

    private String emotionAnalysis;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
