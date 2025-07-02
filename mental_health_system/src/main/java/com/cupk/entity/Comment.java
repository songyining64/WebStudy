package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("post_id")
    private Long postId;

    @TableField("user_id")
    private Long userId;

    @TableField("content")
    private String content;

    @TableField("parent_id")
    private Long parentId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("status")
    private Integer status;

    // 非数据库字段，用于前端显示
    @TableField(exist = false)
    private String username;

    // 非数据库字段，用于前端显示用户头像
    @TableField(exist = false)
    private String avatar;

    // 非数据库字段，评论点赞数
    @TableField(exist = false)
    private Long likeCount;

    // 非数据库字段，是否已点赞
    @TableField(exist = false)
    private Boolean liked;
}