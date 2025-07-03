package com.cupk.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cupk.entity.Post;

public interface PostService extends IService<Post> {
    /**
     * 点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否点赞成功
     */
    boolean likePost(Long postId, Long userId);

    /**
     * 取消点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否取消成功
     */
    boolean unlikePost(Long postId, Long userId);

    /**
     * 检查用户是否已点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean hasLiked(Long postId, Long userId);

    /**
     * 获取帖子点赞数
     * 
     * @param postId 帖子ID
     * @return 点赞数
     */
    Long getLikeCount(Long postId);

    /**
     * 获取帖子列表，包含用户信息
     * 
     * @param page 页码
     * @param size 每页数量
     * @return 帖子分页数据（包含用户名和头像）
     */
    IPage<Post> getPostListWithUserInfo(int page, int size);

    /**
     * 获取带用户信息的帖子详情
     * 
     * @param postId 帖子ID
     * @return 带用户信息的帖子详情
     */
    Post getPostWithUserInfo(Long postId);

    /**
     * 根据关键词搜索帖子
     * 
     * @param page    页码
     * @param size    每页数量
     * @param keyword 搜索关键词
     * @return 符合条件的帖子分页数据
     */
    IPage<Post> searchPostsByKeyword(int page, int size, String keyword);

    /**
     * 根据分类筛选帖子
     * 
     * @param page     页码
     * @param size     每页数量
     * @param category 分类名称
     * @return 符合条件的帖子分页数据
     */
    IPage<Post> filterPostsByCategory(int page, int size, String category);

    /**
     * 高级搜索：组合条件查询帖子
     * 
     * @param page      页码
     * @param size      每页数量
     * @param keyword   搜索关键词（可选）
     * @param category  分类名称（可选）
     * @param sortBy    排序字段（create_time, like_count, comment_count）
     * @param sortOrder 排序方式（ASC, DESC）
     * @return 符合条件的帖子分页数据
     */
    IPage<Post> advancedSearch(
            int page,
            int size,
            String keyword,
            String category,
            String sortBy,
            String sortOrder);

    /**
     * 获取用户发布的帖子
     * 
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页数量
     * @return 用户发布的帖子分页数据
     */
    IPage<Post> getPostsByUserId(Long userId, int page, int size);
    
    /**
     * 获取用户收藏的帖子
     * 
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页数量
     * @return 用户收藏的帖子分页数据
     */
    IPage<Post> getFavoritePostsByUserId(Long userId, int page, int size);
}