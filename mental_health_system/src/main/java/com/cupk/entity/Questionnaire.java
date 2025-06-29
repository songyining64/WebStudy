package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("questionnaire")
public class Questionnaire {
    private Long id;
    private String title;
    private String description;
    // JSON string containing questions
    private String questions; 
}