package com.cupk.service;

public interface PostFavoriteService {
    boolean favorite(Long userId, Long postId);

    boolean unfavorite(Long userId, Long postId);

    boolean isFavorite(Long userId, Long postId);
}