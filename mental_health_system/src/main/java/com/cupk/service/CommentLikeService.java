package com.cupk.service;

public interface CommentLikeService {
    boolean likeComment(Long commentId, Long userId);

    boolean unlikeComment(Long commentId, Long userId);

    boolean hasLiked(Long commentId, Long userId);

    Long getLikeCount(Long commentId);
}