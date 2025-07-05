package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("music_search_history")
public class MusicSearchHistory {
    @TableId
    private Long id;
    private Long userId;
    private String keyword;
    private Date searchTime;
} 