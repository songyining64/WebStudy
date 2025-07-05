package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("playlist_music")
public class PlaylistMusic {
    @TableId
    private Long id;
    private Long playlistId;
    private Long musicId;
    private Integer sort;  // 歌曲在歌单中的排序
} 