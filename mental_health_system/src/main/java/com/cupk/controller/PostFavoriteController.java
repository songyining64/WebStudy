package com.cupk.controller;

import com.cupk.service.PostFavoriteService;
import com.cupk.service.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class PostFavoriteController {
    private static final Logger logger = LoggerFactory.getLogger(PostFavoriteController.class);

    @Autowired
    private PostFavoriteService postFavoriteService;

    /**
     * 收藏帖子
     * 支持多种调用方式，优先级：userId参数 > token中解析 > 默认用户1
     */
    @PostMapping("/{postId}/favorite")
    public Result<?> favorite(
            @PathVariable Long postId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("收藏帖子请求: postId={}, userId={}, token={}", postId, userId, token);
        Long userIdToUse = determineUserId(userId, token);
        boolean success = postFavoriteService.favorite(userIdToUse, postId);
        return Result.success(success);
    }

    /**
     * 取消收藏
     * 支持多种调用方式，优先级：userId参数 > token中解析 > 默认用户1
     */
    @DeleteMapping("/{postId}/favorite")
    public Result<?> unfavorite(
            @PathVariable Long postId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("取消收藏请求: postId={}, userId={}, token={}", postId, userId, token);
        Long userIdToUse = determineUserId(userId, token);
        boolean success = postFavoriteService.unfavorite(userIdToUse, postId);
        return Result.success(success);
    }

    /**
     * 查询收藏状态
     * 支持多种调用方式，优先级：userId参数 > token中解析 > 默认用户1
     */
    @GetMapping("/{postId}/favorite")
    public Result<?> isFavorite(
            @PathVariable Long postId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("查询收藏状态请求: postId={}, userId={}, token={}", postId, userId, token);

        // 如果没有任何参数，默认使用用户ID 1
        Long userIdToUse = determineUserId(userId, token);
        logger.info("使用用户ID: {}", userIdToUse);

        boolean isFav = postFavoriteService.isFavorite(userIdToUse, postId);
        return Result.success(isFav);
    }

    /**
     * 获取帖子收藏数
     */
    @GetMapping("/{postId}/favorite/count")
    public Result<Long> getFavoriteCount(@PathVariable Long postId) {
        logger.info("获取收藏数请求: postId={}", postId);
        long count = postFavoriteService.getFavoriteCount(postId);
        return Result.success(count);
    }

    /**
     * 查询收藏状态 - 简化版本
     * 直接使用默认用户ID 1，用于紧急修复
     */
    @GetMapping("/{postId}/favorite/simple")
    public Result<?> isFavoriteSimple(@PathVariable Long postId) {
        logger.info("简化版查询收藏状态请求: postId={}", postId);
        boolean isFav = postFavoriteService.isFavorite(1L, postId);
        return Result.success(isFav);
    }

    // 确定使用哪个用户ID
    private Long determineUserId(Long userId, String token) {
        // 优先使用参数中的userId
        if (userId != null) {
            return userId;
        }

        // 如果没有userId但有token，从token中获取
        if (token != null) {
            return getUserIdFromToken(token);
        }

        // 如果都没有，使用默认值1
        return 1L;
    }

    // token解析逻辑
    private Long getUserIdFromToken(String token) {
        // 简单处理，从token中提取userId
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // 在实际应用中应该进行JWT解析，这里简化处理
            return 1L;
        } catch (Exception e) {
            return 1L; // 默认返回用户ID 1
        }
    }
}