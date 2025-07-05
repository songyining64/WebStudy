package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("user_assessment")
public class UserAssessment implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private String assessmentTitle;

    private Integer score;

    private Date submitTime;

    private String detail;

    // 该字段在数据库表中不存在，需要标记为非表字段
    @TableField(exist = false)
    private Long questionnaireId;

    // 新增字段 - 这些字段在数据库中不存在
    @TableField(exist = false)
    private String answers; // 用户的问卷答案（JSON格式）

    @TableField(exist = false)
    private String content; // 问卷内容

    @TableField(exist = false)
    private String suggestions; // 建议

    @TableField(exist = false)
    private String report; // 评估报告
}