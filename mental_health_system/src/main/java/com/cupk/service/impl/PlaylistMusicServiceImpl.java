package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.entity.PlaylistMusic;
import com.cupk.mapper.PlaylistMusicMapper;
import com.cupk.service.PlaylistMusicService;
import org.springframework.stereotype.Service;

@Service
public class PlaylistMusicServiceImpl extends ServiceImpl<PlaylistMusicMapper, PlaylistMusic> implements PlaylistMusicService {
    @Override
    public boolean removeByPlaylistId(Long playlistId) {
        QueryWrapper<PlaylistMusic> wrapper = new QueryWrapper<>();
        wrapper.eq("playlist_id", playlistId);
        return remove(wrapper);
    }

    @Override
    public boolean removeByPlaylistIdAndMusicId(Long playlistId, Long musicId) {
        QueryWrapper<PlaylistMusic> wrapper = new QueryWrapper<>();
        wrapper.eq("playlist_id", playlistId).eq("music_id", musicId);
        return remove(wrapper);
    }

    @Override
    public Integer getMaxSortByPlaylistId(Long playlistId) {
        QueryWrapper<PlaylistMusic> wrapper = new QueryWrapper<>();
        wrapper.eq("playlist_id", playlistId).orderByDesc("sort").last("LIMIT 1");
        PlaylistMusic playlistMusic = getOne(wrapper);
        return playlistMusic != null ? playlistMusic.getSort() : null;
    }
} 