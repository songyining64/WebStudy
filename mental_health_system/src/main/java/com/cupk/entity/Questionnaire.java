package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

@Data
@TableName("questionnaire")
public class Questionnaire {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    // JSON string containing questions
    private String questions;
}