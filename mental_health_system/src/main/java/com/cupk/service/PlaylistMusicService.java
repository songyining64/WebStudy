package com.cupk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cupk.entity.PlaylistMusic;
 
public interface PlaylistMusicService extends IService<PlaylistMusic> {
    /**
     * 根据歌单ID删除所有关联的音乐
     * @param playlistId 歌单ID
     * @return 是否删除成功
     */
    boolean removeByPlaylistId(Long playlistId);
    
    /**
     * 根据歌单ID和音乐ID删除关联
     * @param playlistId 歌单ID
     * @param musicId 音乐ID
     * @return 是否删除成功
     */
    boolean removeByPlaylistIdAndMusicId(Long playlistId, Long musicId);
    
    /**
     * 获取歌单中最大的排序值
     * @param playlistId 歌单ID
     * @return 最大排序值，如果没有则返回null
     */
    Integer getMaxSortByPlaylistId(Long playlistId);
} 