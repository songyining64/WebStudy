package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("video_resource")
public class VideoResource {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("title")
    private String title;
    @TableField("description")
    private String description;
    @TableField("url")
    private String url;
    @TableField("thumbnail_url")
    private String thumbnailUrl;
    @TableField("upload_time")
    private LocalDateTime uploadTime;
    @TableField("uploader_id")
    private Long uploaderId; //上传者ID,关联用户表
}