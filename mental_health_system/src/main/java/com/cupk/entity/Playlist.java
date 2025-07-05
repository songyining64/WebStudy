package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("playlist")
public class Playlist {
    @TableId
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String cover;
    private Integer musicCount;
    private String createTime;
    private String updateTime;
} 