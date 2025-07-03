package com.cupk.service;

public interface CommentLikeService {
    boolean likeComment(Long commentId, Long userId);

    boolean unlikeComment(Long commentId, Long userId);

    boolean hasLiked(Long commentId, Long userId);

    Long getLikeCount(Long commentId);

    /**
     * 删除评论相关的所有点赞记录
     *
     * @param commentId 评论ID
     * @return 是否删除成功
     */
    boolean deleteByCommentId(Long commentId);
}