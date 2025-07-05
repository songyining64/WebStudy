package com.cupk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cupk.entity.Music;
import java.util.List;

public interface MusicService extends IService<Music> {
    /**
     * 根据歌单ID获取音乐列表
     * @param playlistId 歌单ID
     * @return 音乐列表
     */
    List<Music> getByPlaylistId(Long playlistId);
} 