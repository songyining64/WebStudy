package com.cupk.controller;

import com.cupk.service.PostLikeService;
import com.cupk.service.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post-likes")
public class PostLikeController {

    private final PostLikeService postLikeService;

    public PostLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    /**
     * 点赞帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 点赞结果
     */
    @PostMapping("/{postId}/users/{userId}")
    public Result<Boolean> likePost(@PathVariable Long postId, @PathVariable Long userId) {
        boolean success = postLikeService.likePost(postId, userId);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error("您已经点赞过该帖子");
        }
    }

    /**
     * 取消点赞帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 取消点赞结果
     */
    @DeleteMapping("/{postId}/users/{userId}")
    public Result<Boolean> unlikePost(@PathVariable Long postId, @PathVariable Long userId) {
        boolean success = postLikeService.unlikePost(postId, userId);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error("您还没有点赞该帖子");
        }
    }

    /**
     * 检查用户是否已点赞帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    @GetMapping("/{postId}/users/{userId}/status")
    public Result<Boolean> checkLikeStatus(@PathVariable Long postId, @PathVariable Long userId) {
        boolean hasLiked = postLikeService.hasLiked(postId, userId);
        return Result.success(hasLiked);
    }

    /**
     * 获取帖子点赞数
     *
     * @param postId 帖子ID
     * @return 点赞数
     */
    @GetMapping("/{postId}/count")
    public Result<Long> getLikeCount(@PathVariable Long postId) {
        Long count = postLikeService.getLikeCount(postId);
        return Result.success(count);
    }
}