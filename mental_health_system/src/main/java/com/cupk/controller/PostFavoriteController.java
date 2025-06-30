package com.cupk.controller;

import com.cupk.service.PostFavoriteService;
import com.cupk.service.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class PostFavoriteController {

    @Autowired
    private PostFavoriteService postFavoriteService;

    // TODO: 替换为你自己的token解析逻辑
    private Long getUserIdFromToken(String token) {
        return 1L;
    }

    @PostMapping("/{postId}/favorite")
    public Result favorite(@PathVariable Long postId, @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        boolean success = postFavoriteService.favorite(userId, postId);
        return Result.success(success);
    }

    @DeleteMapping("/{postId}/favorite")
    public Result unfavorite(@PathVariable Long postId, @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        boolean success = postFavoriteService.unfavorite(userId, postId);
        return Result.success(success);
    }

    @GetMapping("/{postId}/favorite")
    public Result isFavorite(@PathVariable Long postId, @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        boolean isFav = postFavoriteService.isFavorite(userId, postId);
        return Result.success(isFav);
    }
}