package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("music_favorite")
public class MusicFavorite {
    private Long userId;
    private Long musicId;
    private Long playlistId;
} 