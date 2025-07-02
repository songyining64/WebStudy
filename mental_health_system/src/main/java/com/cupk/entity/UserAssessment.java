package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

@Data
@TableName("user_assessment")
public class UserAssessment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long questionnaireId;
    private String answers; // JSON answers
    private String report; // Generated assessment report
}