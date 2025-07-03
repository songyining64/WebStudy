package com.cupk.controller;

import com.cupk.service.PostLikeService;
import com.cupk.service.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post-likes")
public class PostLikeController {
    private static final Logger logger = LoggerFactory.getLogger(PostLikeController.class);
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
        logger.info("收到点赞请求: postId={}, userId={}", postId, userId);

        // 参数验证
        if (postId == null || userId == null) {
            logger.warn("点赞失败: 帖子ID或用户ID为空");
            return Result.error("帖子ID或用户ID不能为空");
        }

        try {
            boolean success = postLikeService.likePost(postId, userId);
            if (success) {
                return Result.success(true);
            } else {
                return Result.error("您已经点赞过该帖子");
            }
        } catch (Exception e) {
            logger.error("点赞帖子时发生异常", e);
            return Result.error("点赞失败，请稍后重试");
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
        logger.info("收到取消点赞请求: postId={}, userId={}", postId, userId);

        // 参数验证
        if (postId == null || userId == null) {
            logger.warn("取消点赞失败: 帖子ID或用户ID为空");
            return Result.error("帖子ID或用户ID不能为空");
        }

        try {
            boolean success = postLikeService.unlikePost(postId, userId);
            if (success) {
                return Result.success(true);
            } else {
                return Result.error("您还没有点赞该帖子");
            }
        } catch (Exception e) {
            logger.error("取消点赞帖子时发生异常", e);
            return Result.error("取消点赞失败，请稍后重试");
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
        logger.info("检查点赞状态: postId={}, userId={}", postId, userId);

        // 参数验证
        if (postId == null || userId == null) {
            return Result.error("帖子ID或用户ID不能为空");
        }

        try {
            boolean hasLiked = postLikeService.hasLiked(postId, userId);
            return Result.success(hasLiked);
        } catch (Exception e) {
            logger.error("检查点赞状态时发生异常", e);
            return Result.error("获取点赞状态失败，请稍后重试");
        }
    }

    /**
     * 获取帖子点赞数
     *
     * @param postId 帖子ID
     * @return 点赞数
     */
    @GetMapping("/{postId}/count")
    public Result<Long> getLikeCount(@PathVariable Long postId) {
        logger.info("获取点赞数量: postId={}", postId);

        // 参数验证
        if (postId == null) {
            return Result.error("帖子ID不能为空");
        }

        try {
            Long count = postLikeService.getLikeCount(postId);
            return Result.success(count);
        } catch (Exception e) {
            logger.error("获取点赞数量时发生异常", e);
            return Result.error("获取点赞数量失败，请稍后重试");
        }
    }

    /**
     * 获取用户点赞的帖子列表
     *
     * @param userId 用户ID
     * @param current 当前页码
     * @param size 每页大小
     * @return 用户点赞的帖子列表
     */
    @GetMapping("/users/{userId}/posts")
    public Result<?> getUserLikedPosts(
            @PathVariable Long userId,
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        logger.info("获取用户点赞帖子列表请求: userId={}, current={}, size={}", userId, current, size);
        
        try {
            return Result.success(postLikeService.getLikedPostsByUserId(userId, current, size));
        } catch (Exception e) {
            logger.error("获取用户点赞帖子列表失败", e);
            return Result.error("获取用户点赞帖子列表失败: " + e.getMessage());
        }
    }
}