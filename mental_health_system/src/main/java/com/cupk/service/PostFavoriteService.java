package com.cupk.service;

public interface PostFavoriteService {
    /**
     * 收藏帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否成功
     */
    boolean favorite(Long userId, Long postId);

    /**
     * 取消收藏帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否成功
     */
    boolean unfavorite(Long userId, Long postId);

    /**
     * 检查用户是否已收藏帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否已收藏
     */
    boolean isFavorite(Long userId, Long postId);

    /**
     * 获取帖子的收藏数
     * 
     * @param postId 帖子ID
     * @return 收藏数
     */
    long getFavoriteCount(Long postId);

    /**
     * 删除帖子的所有收藏记录
     *
     * @param postId 帖子ID
     * @return 是否成功
     */
    boolean deleteByPostId(Long postId);
}