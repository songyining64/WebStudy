package com.cupk.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cupk.entity.Music;
import com.cupk.service.MusicService;
import com.cupk.service.MusicFavoriteService;
import com.cupk.service.MusicSearchHistoryService;
import com.cupk.service.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Date;
import java.util.UUID;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import org.springframework.util.ResourceUtils;

@RestController
@RequestMapping("/api/music")
public class MusicController {
    @Autowired
    private MusicService musicService;
    @Autowired
    private MusicFavoriteService musicFavoriteService;
    @Autowired
    private MusicSearchHistoryService musicSearchHistoryService;

    @Value("${upload.dir}")
    private String uploadDir;

    // 获取所有音乐（用户和管理员共用）
    @GetMapping("/list")
    public Map<String, Object> listAll() {
        List<Music> list = musicService.list();
        // 转换为前端需要的字段格式
        List<Map<String, Object>> data = list.stream().map(music -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", music.getId());
            m.put("name", music.getTitle());
            m.put("artist", music.getArtist());
            m.put("coverUrl", music.getCover());
            m.put("url", music.getSrc());
            return m;
        }).toList();
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("code", 200);
        result.put("msg", "success");
        return result;
    }

    // 按分类获取音乐
    @GetMapping("/category/{category}")
    public List<Music> listByCategory(@PathVariable String category) {
        return musicService.list(new QueryWrapper<Music>().eq("category", category));
    }

    // 搜索音乐（仅按歌名模糊搜索）
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam(required = false, defaultValue = "") String name) {
        QueryWrapper<Music> wrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like("title", name);
        }
        List<Music> list = musicService.list(wrapper);
        List<Map<String, Object>> data = list.stream().map(music -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", music.getId());
            m.put("name", music.getTitle());
            m.put("artist", music.getArtist());
            m.put("coverUrl", music.getCover());
            m.put("url", music.getSrc());
            return m;
        }).toList();
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("code", 200);
        result.put("msg", "success");
        return result;
    }

    // 收藏音乐
    @PostMapping("/favorite/{musicId}")
    public String favorite(@PathVariable Long musicId, @RequestParam Long userId) {
        // TODO: 收藏逻辑
        return "success";
    }

    // 取消收藏
    @DeleteMapping("/favorite/{musicId}")
    public String unfavorite(@PathVariable Long musicId, @RequestParam Long userId) {
        // TODO: 取消收藏逻辑
        return "success";
    }

    // 获取用户收藏
    @GetMapping("/favorite/list")
    public List<Music> favoriteList(@RequestParam Long userId) {
        // TODO: 查询用户收藏
        return null;
    }

    // 获取用户搜索历史
    @GetMapping("/search/history")
    public List<String> searchHistory(@RequestParam Long userId) {
        // TODO: 查询搜索历史
        return null;
    }

    // 清空搜索历史
    @DeleteMapping("/search/history")
    public String clearSearchHistory(@RequestParam Long userId) {
        // TODO: 清空历史
        return "success";
    }

    // 管理员上传音乐（含文件）
    @PostMapping("/upload")
    public Result<?> uploadMusic(
            @RequestParam String title,
            @RequestParam String artist,
            @RequestParam(required = false) String album,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) MultipartFile cover,
            @RequestParam MultipartFile audio) throws IOException {
        
        // 打印上传目录
        System.out.println("音乐上传目录: " + uploadDir);

        if (audio.isEmpty()) {
            return Result.error("音频文件为空");
        }

        // 创建音乐子目录
        String musicDir = uploadDir + "music/";
        File musicDirectory = new File(musicDir);
        if (!musicDirectory.exists()) {
            musicDirectory.mkdirs();
        }

        // 保存音频文件
        String audioOriginalFilename = audio.getOriginalFilename();
        String audioExt = audioOriginalFilename != null && audioOriginalFilename.contains(".")
                ? audioOriginalFilename.substring(audioOriginalFilename.lastIndexOf("."))
                : "";
        String audioFileName = UUID.randomUUID().toString() + audioExt;
        File audioFile = new File(musicDirectory, audioFileName);
        
        System.out.println("音频文件实际保存路径: " + audioFile.getAbsolutePath());
        audio.transferTo(audioFile);
        
        if (!audioFile.exists()) {
            return Result.error("音频文件保存失败");
        }
        
        String audioUrl = "/static/upload/music/" + audioFileName;

        // 保存封面文件
        String coverUrl = null;
        if (cover != null && !cover.isEmpty()) {
            String coverOriginalFilename = cover.getOriginalFilename();
            String coverExt = coverOriginalFilename != null && coverOriginalFilename.contains(".")
                    ? coverOriginalFilename.substring(coverOriginalFilename.lastIndexOf("."))
                    : "";
            String coverFileName = UUID.randomUUID().toString() + coverExt;
            File coverFile = new File(musicDirectory, coverFileName);
            
            System.out.println("封面文件实际保存路径: " + coverFile.getAbsolutePath());
            cover.transferTo(coverFile);
            
            if (coverFile.exists()) {
                coverUrl = "/static/upload/music/" + coverFileName;
            }
        }

        // 保存到数据库
        Music music = new Music();
        music.setTitle(title);
        music.setArtist(artist);
        music.setAlbum(album);
        music.setSrc(audioUrl);
        music.setCover(coverUrl);
        music.setCategory(category);
        boolean saved = musicService.save(music);

        System.out.println("音乐保存结果: " + saved + ", music=" + music);
        return saved ? Result.success("上传成功") : Result.error("保存失败");
    }

    // 管理员删除音乐
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteMusic(@PathVariable Long id) {
        Music music = musicService.getById(id);
        if (music != null) {
            // 删除音频文件
            if (music.getSrc() != null) {
                String audioFileName = music.getSrc().substring(music.getSrc().lastIndexOf('/') + 1);
                String audioFilePath = uploadDir + "music/" + audioFileName;
                File audioFile = new File(audioFilePath);
                if (audioFile.exists()) {
                    boolean audioDeleted = audioFile.delete();
                    System.out.println("音频文件删除结果: " + audioDeleted + ", 路径: " + audioFilePath);
                }
            }
            
            // 删除封面文件
            if (music.getCover() != null) {
                String coverFileName = music.getCover().substring(music.getCover().lastIndexOf('/') + 1);
                String coverFilePath = uploadDir + "music/" + coverFileName;
                File coverFile = new File(coverFilePath);
                if (coverFile.exists()) {
                    boolean coverDeleted = coverFile.delete();
                    System.out.println("封面文件删除结果: " + coverDeleted + ", 路径: " + coverFilePath);
                }
            }
        }
        
        boolean removed = musicService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }
} 