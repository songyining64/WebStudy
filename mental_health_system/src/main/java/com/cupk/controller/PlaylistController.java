package com.cupk.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cupk.entity.Music;
import com.cupk.entity.Playlist;
import com.cupk.entity.PlaylistMusic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.cupk.service.PlaylistService;
import com.cupk.service.PlaylistMusicService;
import com.cupk.service.MusicService;

@RestController
@RequestMapping("/api/playlist")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;
    
    @Autowired
    private PlaylistMusicService playlistMusicService;
    
    @Autowired
    private MusicService musicService;

    // 获取用户所有歌单
    @GetMapping("/list")
    public List<Playlist> list(@RequestParam Long userId) {
        return playlistService.getByUserId(userId);
    }
    
    // 创建歌单
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Playlist playlist) {
        boolean success = playlistService.save(playlist);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "创建成功" : "创建失败");
        result.put("data", success ? playlist : null);
        return ResponseEntity.ok(result);
    }
    
    // 删除歌单
    @DeleteMapping("/{playlistId}")
    public ResponseEntity<String> delete(@PathVariable Long playlistId) {
        // 先删除歌单中的音乐关联
        playlistMusicService.removeByPlaylistId(playlistId);
        // 再删除歌单
        boolean removed = playlistService.removeById(playlistId);
        return ResponseEntity.ok(removed ? "删除成功" : "删除失败");
    }
    
    // 获取歌单中的音乐
    @GetMapping("/{playlistId}/music")
    public List<Music> getPlaylistMusic(@PathVariable Long playlistId) {
        return musicService.getByPlaylistId(playlistId);
    }
    
    // 添加音乐到歌单
    @PostMapping("/{playlistId}/add")
    public ResponseEntity<String> addMusic(@PathVariable Long playlistId, @RequestParam Long musicId) {
        PlaylistMusic playlistMusic = new PlaylistMusic();
        playlistMusic.setPlaylistId(playlistId);
        playlistMusic.setMusicId(musicId);
        
        // 获取当前歌单中最大的排序值
        Integer maxSort = playlistMusicService.getMaxSortByPlaylistId(playlistId);
        playlistMusic.setSort(maxSort != null ? maxSort + 1 : 1);
        
        boolean success = playlistMusicService.save(playlistMusic);
        return ResponseEntity.ok(success ? "添加成功" : "添加失败");
    }
    
    // 从歌单中移除音乐
    @DeleteMapping("/{playlistId}/remove")
    public ResponseEntity<String> removeMusic(@PathVariable Long playlistId, @RequestParam Long musicId) {
        boolean removed = playlistMusicService.removeByPlaylistIdAndMusicId(playlistId, musicId);
        return ResponseEntity.ok(removed ? "移除成功" : "移除失败");
    }
} 