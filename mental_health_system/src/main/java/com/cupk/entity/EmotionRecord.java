package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("emotion_record")
public class EmotionRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private String emotion;

    private LocalDateTime recordTime;

    private String remark;

    // 新增字段 - 这些字段可能在数据库中不存在
    // 在执行完 emotion_record_table.sql 脚本后才能使用这些字段
    // 暂时设为 transient，不参与数据库操作
    @TableField(exist = false)
    private transient String content; // 内容

    @TableField(exist = false)
    private transient String suggestions; // 建议
}