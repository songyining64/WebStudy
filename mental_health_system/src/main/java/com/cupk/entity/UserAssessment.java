package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_assessment")
public class UserAssessment {
    private Long id;
    private Long userId;
    private Long questionnaireId;
    private String answers; // JSON answers
    private String report; // Generated assessment report
}