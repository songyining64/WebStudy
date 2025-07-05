package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("music")
public class Music {
    @TableId
    private Long id;
    private String title;
    private String artist;
    private String album;
    private String src;
    private String cover;
    private String category;
} 