package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.entity.Music;
import com.cupk.entity.PlaylistMusic;
import com.cupk.mapper.MusicMapper;
import com.cupk.mapper.PlaylistMusicMapper;
import com.cupk.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {
    
    @Autowired
    private PlaylistMusicMapper playlistMusicMapper;
    
    @Override
    public List<Music> getByPlaylistId(Long playlistId) {
        // 先查询歌单中的音乐ID列表
        QueryWrapper<PlaylistMusic> wrapper = new QueryWrapper<>();
        wrapper.eq("playlist_id", playlistId).orderByAsc("sort");
        List<PlaylistMusic> playlistMusics = playlistMusicMapper.selectList(wrapper);
        
        if (playlistMusics.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取音乐ID列表
        List<Long> musicIds = playlistMusics.stream()
                .map(PlaylistMusic::getMusicId)
                .collect(Collectors.toList());
        
        // 查询音乐详情
        return listByIds(musicIds);
    }
} 