package com.cupk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cupk.entity.Playlist;

import java.util.List;

public interface PlaylistService extends IService<Playlist> {
    
    /**
     * 根据用户ID获取歌单列表
     * @param userId 用户ID
     * @return 歌单列表
     */
    List<Playlist> getByUserId(Long userId);
} 