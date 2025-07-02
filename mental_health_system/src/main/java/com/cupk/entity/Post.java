package com.cupk.entity;

import java.time.LocalDateTime;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

@Data
@TableName("post")
public class Post {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("title")
    private String title;
    @TableField("content")
    private String content;
    @TableField("images")
    private String images;
    @TableField("tags")
    private String tags;
    @TableField("category")
    private String category;
    @TableField("like_count")
    private Integer likeCount;
    @TableField("comment_count")
    private Integer commentCount;
    @TableField("status")
    private Integer status;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;

    // 非数据库字段，用于前端显示
    @TableField(exist = false)
    private String username;

    // 非数据库字段，用于前端显示用户头像
    @TableField(exist = false)
    private String avatar;
}