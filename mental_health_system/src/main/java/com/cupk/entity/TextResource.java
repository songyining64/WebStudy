package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("text_resource")
public class TextResource {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("title")
    private String title;
    @TableField("content")
    private String content;
    @TableField("author_id")
    private Long authorId; // 作者ID，可以为null
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("emotion_tag") // 添加情绪标签字段
    private String emotionTag;

    @TableField("image_url")
    private String imageUrl;
}