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
    public Result<Comment> addComment(
            @RequestBody Comment comment,
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("收到发表评论请求: postId={}, userId={}, content={}, parentId={}, token={}",
                comment.getPostId(), comment.getUserId(), comment.getContent(), comment.getParentId(), token);

        try {
            // 如果评论没有用户ID，尝试从token解析
            if (comment.getUserId() == null && token != null) {
                Long userId = getUserIdFromToken(token);
                if (userId != null) {
                    comment.setUserId(userId);
                    logger.info("从token解析得到用户ID: {}", userId);
                }
            }

            // 如果仍然没有用户ID，使用默认值1
            if (comment.getUserId() == null) {
                comment.setUserId(1L);
                logger.warn("未提供用户ID，使用默认值: 1");
            }

            // 检查必要参数
            if (comment.getPostId() == null) {
                logger.error("缺少必要参数: postId");
                return Result.error("缺少必要参数: 帖子ID");
            }

            if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
                logger.error("评论内容不能为空");
                return Result.error("评论内容不能为空");
            }

            // 处理评论回复逻辑
            if (comment.getParentId() != null) {
                logger.info("这是一条回复评论，父评论ID: {}", comment.getParentId());

                // 验证父评论是否存在
                Comment parentComment = commentService.getCommentById(comment.getParentId());
                if (parentComment == null) {
                    logger.error("父评论不存在: {}", comment.getParentId());
                    return Result.error("回复的评论不存在");
                }

                // 如果父评论是回复其他评论的，则将当前评论的父ID设置为顶级评论ID
                // 这样可以避免多级嵌套，保持二级结构
                if (parentComment.getParentId() != null) {
                    logger.info("父评论也是回复评论，将当前评论的父ID设置为顶级评论ID: {}", parentComment.getParentId());
                    comment.setParentId(parentComment.getParentId());
                }

                // 确保回复的评论与父评论属于同一帖子
                if (!parentComment.getPostId().equals(comment.getPostId())) {
                    logger.error("回复的帖子ID与父评论帖子ID不匹配: parentPostId={}, commentPostId={}",
                            parentComment.getPostId(), comment.getPostId());
                    // 自动修正为父评论的帖子ID
                    comment.setPostId(parentComment.getPostId());
                    logger.info("已自动修正帖子ID为父评论的帖子ID: {}", comment.getPostId());
                }
            } else {
                logger.info("这是一条普通评论，没有父评论ID");
            }

            // 记录完整的评论对象
            logger.info("处理后的评论对象: {}", comment);

            comment.setId(null);
            boolean success = commentService.addComment(comment);
            logger.info("评论发表结果: {}, 生成的评论ID: {}", success, comment.getId());

            if (success) {
                // 获取带用户信息的评论返回给前端
                Comment newComment = null;

                try {
                    if (comment.getParentId() == null) {
                        // 如果是一级评论，从一级评论列表中查找
                        newComment = commentService.getCommentsWithUserInfo(comment.getPostId(), comment.getUserId())
                                .stream()
                                .filter(c -> c.getId().equals(comment.getId()))
                                .findFirst()
                                .orElse(null);
                    } else {
                        // 如果是回复，从回复列表中查找
                        newComment = commentService.getRepliesWithUserInfo(comment.getParentId(), comment.getUserId())
                                .stream()
                                .filter(c -> c.getId().equals(comment.getId()))
                                .findFirst()
                                .orElse(null);
                    }
                } catch (Exception e) {
                    logger.error("获取新评论详情失败", e);
                }

                // 如果找不到详情，直接返回原始评论
                if (newComment == null) {
                    newComment = comment;
                    // 设置默认值，避免前端显示问题
                    if (newComment.getUsername() == null) {
                        newComment.setUsername("用户" + comment.getUserId());
                    }
                    if (newComment.getAvatar() == null) {
                        newComment.setAvatar("/avatar/default.png");
                    }
                    newComment.setLikeCount(0L);
                    newComment.setLiked(false);
                }

                logger.info("返回新评论: {}", newComment);
                return Result.success(newComment);
            } else {
                return Result.error("发表评论失败");
            }
        } catch (RuntimeException e) {
            logger.error("发表评论时发生运行时错误", e);
            return Result.error("发表评论失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("发表评论时发生错误", e);
            return Result.error("发表评论失败: " + e.getMessage());
        }
    }

    /**
     * 回复评论的专用接口
     */
    @PostMapping("/{commentId}/reply")
    public Result<Comment> replyComment(
            @PathVariable Long commentId,
            @RequestBody Comment reply,
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("收到回复评论请求: commentId={}, reply={}, token={}", commentId, reply, token);

        try {
            // 设置父评论ID
            reply.setParentId(commentId);

            // 如果评论没有用户ID，尝试从token解析
            if (reply.getUserId() == null && token != null) {
                Long userId = getUserIdFromToken(token);
                if (userId != null) {
                    reply.setUserId(userId);
                }
            }

            // 如果仍然没有用户ID，使用默认值1
            if (reply.getUserId() == null) {
                reply.setUserId(1L);
            }

            // 验证父评论是否存在
            Comment parentComment = commentService.getCommentById(commentId);
            if (parentComment == null) {
                return Result.error("回复的评论不存在");
            }

            // 设置帖子ID与父评论一致
            reply.setPostId(parentComment.getPostId());

            // 如果父评论也是回复，则将当前回复的父ID设置为顶级评论ID
            if (parentComment.getParentId() != null) {
                reply.setParentId(parentComment.getParentId());
            }

            // 添加回复
            reply.setId(null);
            boolean success = commentService.addComment(reply);

            if (success) {
                // 获取带用户信息的回复返回给前端
                Comment newReply = commentService.getRepliesWithUserInfo(reply.getParentId(), reply.getUserId())
                        .stream()
                        .filter(c -> c.getId().equals(reply.getId()))
                        .findFirst()
                        .orElse(reply);

                return Result.success(newReply);
            } else {
                return Result.error("回复评论失败");
            }
        } catch (Exception e) {
            logger.error("回复评论时发生错误", e);
            return Result.error("回复评论失败: " + e.getMessage());
        }
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public Result<Boolean> deleteComment(
            @PathVariable Long commentId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("收到删除评论请求: commentId={}, userId={}, token={}", commentId, userId, token);

        try {
            // 确定用户ID
            Long userIdToUse = determineUserId(userId, token);

            boolean success = commentService.deleteComment(commentId, userIdToUse);
            logger.info("删除评论结果: {}", success);
            return success ? Result.success(true) : Result.error("删除评论失败");
        } catch (Exception e) {
            logger.error("删除评论时发生错误", e);
            return Result.error("删除评论失败: " + e.getMessage());
        }
    }

    /**
     * 获取帖子的评论列表 - 原始接口
     */
    @GetMapping("/posts/{postId}")
    public Result<List<Comment>> getCommentsByPostId(
            @PathVariable Long postId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("收到获取帖子评论列表请求: postId={}, userId={}, token={}", postId, userId, token);

        try {
            // 确定用户ID
            Long userIdToUse = determineUserId(userId, token);

            List<Comment> comments = commentService.getCommentsWithUserInfo(postId, userIdToUse);
            logger.info("获取到{}条评论", comments.size());
            return Result.success(comments);
        } catch (Exception e) {
            logger.error("获取帖子评论列表时发生错误", e);
            return Result.error("获取评论列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取帖子的评论列表 - 兼容前端调用
     */
    @GetMapping("/post/{postId}")
    public Result<List<Comment>> getCommentsByPostIdAlternative(
            @PathVariable Long postId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("收到获取帖子评论列表请求(兼容路径): postId={}, userId={}, token={}", postId, userId, token);
        return getCommentsByPostId(postId, userId, token);
    }

    /**
     * 获取评论的回复列表
     */
    @GetMapping("/{commentId}/replies")
    public Result<List<Comment>> getRepliesByParentId(
            @PathVariable Long commentId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("收到获取评论回复列表请求: commentId={}, userId={}, token={}", commentId, userId, token);

        try {
            if (commentId == null) {
                logger.error("评论ID为空");
                return Result.error("评论ID不能为空");
            }

            // 确定用户ID
            Long userIdToUse = determineUserId(userId, token);

            List<Comment> replies = commentService.getRepliesWithUserInfo(commentId, userIdToUse);
            logger.info("获取到{}条回复", replies.size());
            return Result.success(replies);
        } catch (Exception e) {
            logger.error("获取评论回复列表时发生错误", e);
            return Result.error("获取回复列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取评论
     */
    @GetMapping("/{commentId}")
    public Result<Comment> getCommentById(
            @PathVariable Long commentId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("收到获取评论详情请求: commentId={}, userId={}, token={}", commentId, userId, token);

        try {
            if (commentId == null) {
                logger.error("评论ID为空");
                return Result.error("评论ID不能为空");
            }

            // 确定用户ID
            Long userIdToUse = determineUserId(userId, token);

            // 获取评论详情
            Comment comment = commentService.getCommentById(commentId);
            if (comment == null) {
                logger.warn("评论不存在: commentId={}", commentId);
                return Result.error("评论不存在");
            }

            // 设置是否点赞
            if (userIdToUse != null) {
                boolean liked = commentLikeService.hasLiked(commentId, userIdToUse);
                comment.setLiked(liked);
            } else {
                comment.setLiked(false);
            }

            logger.info("获取评论详情成功: {}", comment);
            return Result.success(comment);
        } catch (Exception e) {
            logger.error("获取评论详情时发生错误", e);
            return Result.error("获取评论详情失败: " + e.getMessage());
        }
    }

    // 点赞评论
    @PostMapping("/{commentId}/like")
    public Result<Boolean> likeComment(
            @PathVariable Long commentId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("收到评论点赞请求: commentId={}, userId={}, token={}", commentId, userId, token);

        try {
            // 确定用户ID
            Long userIdToUse = determineUserId(userId, token);

            boolean success = commentLikeService.likeComment(commentId, userIdToUse);
            logger.info("评论点赞结果: {}", success);
            return success ? Result.success(true) : Result.error("已点赞或评论不存在");
        } catch (Exception e) {
            logger.error("评论点赞时发生错误", e);
            return Result.error("点赞失败: " + e.getMessage());
        }
    }

    // 取消点赞
    @DeleteMapping("/{commentId}/like")
    public Result<Boolean> unlikeComment(
            @PathVariable Long commentId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("收到取消评论点赞请求: commentId={}, userId={}, token={}", commentId, userId, token);

        try {
            // 确定用户ID
            Long userIdToUse = determineUserId(userId, token);

            boolean success = commentLikeService.unlikeComment(commentId, userIdToUse);
            logger.info("取消评论点赞结果: {}", success);
            return success ? Result.success(true) : Result.error("未点赞或评论不存在");
        } catch (Exception e) {
            logger.error("取消评论点赞时发生错误", e);
            return Result.error("取消点赞失败: " + e.getMessage());
        }
    }

    // 查询点赞状态
    @GetMapping("/{commentId}/like/status")
    public Result<Boolean> hasLiked(
            @PathVariable Long commentId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("收到查询评论点赞状态请求: commentId={}, userId={}, token={}", commentId, userId, token);

        try {
            // 确定用户ID
            Long userIdToUse = determineUserId(userId, token);

            boolean liked = commentLikeService.hasLiked(commentId, userIdToUse);
            logger.info("评论点赞状态: {}", liked);
            return Result.success(liked);
        } catch (Exception e) {
            logger.error("查询评论点赞状态时发生错误", e);
            return Result.error("查询点赞状态失败: " + e.getMessage());
        }
    }

    // 获取评论点赞数
    @GetMapping("/{commentId}/like/count")
    public Result<Long> getLikeCount(@PathVariable Long commentId) {
        logger.info("收到获取评论点赞数请求: commentId={}", commentId);

        try {
            Long count = commentLikeService.getLikeCount(commentId);
            logger.info("评论点赞数: {}", count);
            return Result.success(count);
        } catch (Exception e) {
            logger.error("获取评论点赞数时发生错误", e);
            return Result.error("获取点赞数失败: " + e.getMessage());
        }
    }

    // 兼容现有前端的点赞接口
    @PostMapping("/{commentId}/like/users/{userId}")
    public Result<Boolean> likeCommentLegacy(@PathVariable Long commentId, @PathVariable Long userId) {
        logger.info("收到评论点赞请求(旧接口): commentId={}, userId={}", commentId, userId);
        return likeComment(commentId, userId, null);
    }

    // 兼容现有前端的取消点赞接口
    @DeleteMapping("/{commentId}/like/users/{userId}")
    public Result<Boolean> unlikeCommentLegacy(@PathVariable Long commentId, @PathVariable Long userId) {
        logger.info("收到取消评论点赞请求(旧接口): commentId={}, userId={}", commentId, userId);
        return unlikeComment(commentId, userId, null);
    }

    // 兼容现有前端的查询点赞状态接口
    @GetMapping("/{commentId}/like/users/{userId}/status")
    public Result<Boolean> hasLikedLegacy(@PathVariable Long commentId, @PathVariable Long userId) {
        logger.info("收到查询评论点赞状态请求(旧接口): commentId={}, userId={}", commentId, userId);
        return hasLiked(commentId, userId, null);
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