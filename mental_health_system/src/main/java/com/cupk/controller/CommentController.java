package com.cupk.controller;

import com.cupk.entity.Comment;
import com.cupk.service.CommentService;
import com.cupk.service.CommentLikeService;
import com.cupk.service.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/comments")
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    public CommentController(CommentService commentService, CommentLikeService commentLikeService) {
        this.commentService = commentService;
        this.commentLikeService = commentLikeService;
    }

    /**
     * 发表评论
     */
    @PostMapping
    public Result<Boolean> addComment(@RequestBody Comment comment) {
        logger.info("收到发表评论请求: {}", comment);
        try {
            boolean success = commentService.addComment(comment);
            logger.info("评论发表结果: {}", success);
            return success ? Result.success(true) : Result.error("发表评论失败");
        } catch (Exception e) {
            logger.error("发表评论时发生错误", e);
            return Result.error("发表评论失败: " + e.getMessage());
        }
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}/users/{userId}")
    public Result<Boolean> deleteComment(@PathVariable Long commentId, @PathVariable Long userId) {
        logger.info("收到删除评论请求: commentId={}, userId={}", commentId, userId);
        try {
            boolean success = commentService.deleteComment(commentId, userId);
            logger.info("删除评论结果: {}", success);
            return success ? Result.success(true) : Result.error("删除评论失败");
        } catch (Exception e) {
            logger.error("删除评论时发生错误", e);
            return Result.error("删除评论失败: " + e.getMessage());
        }
    }

    /**
     * 获取帖子的评论列表
     */
    @GetMapping("/posts/{postId}")
    public Result<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        logger.info("收到获取帖子评论列表请求: postId={}", postId);
        try {
            List<Comment> comments = commentService.getCommentsByPostId(postId);
            logger.info("获取到{}条评论", comments.size());
            return Result.success(comments);
        } catch (Exception e) {
            logger.error("获取帖子评论列表时发生错误", e);
            return Result.error("获取评论列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取评论的回复列表
     */
    @GetMapping("/{commentId}/replies")
    public Result<List<Comment>> getRepliesByParentId(@PathVariable Long commentId) {
        logger.info("收到获取评论回复列表请求: commentId={}", commentId);
        try {
            if (commentId == null) {
                logger.error("评论ID为空");
                return Result.error("评论ID不能为空");
            }
            List<Comment> replies = commentService.getRepliesByParentId(commentId);
            logger.info("获取到{}条回复", replies.size());
            return Result.success(replies);
        } catch (Exception e) {
            logger.error("获取评论回复列表时发生错误", e);
            return Result.error("获取回复列表失败: " + e.getMessage());
        }
    }

    /**
     * 测试接口 - 获取所有评论
     */
    @GetMapping("/test")
    public Result<String> testComments() {
        logger.info("测试评论接口被调用");
        try {
            return Result.success("测试成功");
        } catch (Exception e) {
            logger.error("测试接口发生错误", e);
            return Result.error("测试失败: " + e.getMessage());
        }
    }

    // 点赞评论
    @PostMapping("/{commentId}/like/users/{userId}")
    public Result<Boolean> likeComment(@PathVariable Long commentId, @PathVariable Long userId) {
        boolean success = commentLikeService.likeComment(commentId, userId);
        return success ? Result.success(true) : Result.error("已点赞或评论不存在");
    }

    // 取消点赞
    @DeleteMapping("/{commentId}/like/users/{userId}")
    public Result<Boolean> unlikeComment(@PathVariable Long commentId, @PathVariable Long userId) {
        boolean success = commentLikeService.unlikeComment(commentId, userId);
        return success ? Result.success(true) : Result.error("未点赞或评论不存在");
    }

    // 查询点赞状态
    @GetMapping("/{commentId}/like/users/{userId}/status")
    public Result<Boolean> hasLiked(@PathVariable Long commentId, @PathVariable Long userId) {
        boolean liked = commentLikeService.hasLiked(commentId, userId);
        return Result.success(liked);
    }

    // 获取评论点赞数
    @GetMapping("/{commentId}/like/count")
    public Result<Long> getLikeCount(@PathVariable Long commentId) {
        Long count = commentLikeService.getLikeCount(commentId);
        return Result.success(count);
    }
}