package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.entity.Post;
import com.cupk.mapper.PostMapper;
import com.cupk.service.PostService;
import com.cupk.service.PostLikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostMapper postMapper;
    private final PostLikeService postLikeService;

    public PostServiceImpl(PostMapper postMapper, PostLikeService postLikeService) {
        this.postMapper = postMapper;
        this.postLikeService = postLikeService;
    }

    @Override
    @Transactional
    public boolean likePost(Long postId, Long userId) {
        // 检查帖子是否存在
        Post post = this.getById(postId);
        if (post == null) {
            return false;
        }

        // 尝试点赞
        boolean success = postLikeService.likePost(postId, userId);
        if (success) {
            // 更新帖子点赞数
            postMapper.incrementLikeCount(postId);
        }
        return success;
    }

    @Override
    @Transactional
    public boolean unlikePost(Long postId, Long userId) {
        // 检查帖子是否存在
        Post post = this.getById(postId);
        if (post == null) {
            return false;
        }

        // 尝试取消点赞
        boolean success = postLikeService.unlikePost(postId, userId);
        if (success) {
            // 更新帖子点赞数
            postMapper.decrementLikeCount(postId);
        }
        return success;
    }

    @Override
    public boolean hasLiked(Long postId, Long userId) {
        return postLikeService.hasLiked(postId, userId);
    }

    @Override
    public Long getLikeCount(Long postId) {
        return postLikeService.getLikeCount(postId);
    }

    @Override
    public IPage<Post> getPostListWithUserInfo(int page, int size) {
        // 创建分页参数
        Page<Post> pageParams = new Page<>(page, size);
        // 调用Mapper中的方法进行联表查询
        return postMapper.selectPostsWithUserInfo(pageParams);
    }

    @Override
    public Post getPostWithUserInfo(Long postId) {
        // 调用Mapper中的方法进行联表查询获取带用户信息的帖子详情
        return postMapper.selectPostWithUserInfo(postId);
    }

    @Override
    public IPage<Post> searchPostsByKeyword(int page, int size, String keyword) {
        logger.info("搜索帖子，关键词: {}, 页码: {}, 每页数量: {}", keyword, page, size);

        if (keyword == null || keyword.trim().isEmpty()) {
            // 如果关键词为空，返回普通帖子列表
            return getPostListWithUserInfo(page, size);
        }

        // 创建分页参数
        Page<Post> pageParams = new Page<>(page, size);
        // 调用Mapper中的搜索方法
        return postMapper.searchPostsByKeyword(pageParams, keyword.trim());
    }

    @Override
    public IPage<Post> filterPostsByCategory(int page, int size, String category) {
        logger.info("按分类筛选帖子，分类: {}, 页码: {}, 每页数量: {}", category, page, size);

        if (category == null || category.trim().isEmpty()) {
            // 如果分类为空，返回普通帖子列表
            return getPostListWithUserInfo(page, size);
        }

        // 创建分页参数
        Page<Post> pageParams = new Page<>(page, size);
        // 调用Mapper中的筛选方法
        return postMapper.filterPostsByCategory(pageParams, category.trim());
    }

    @Override
    public IPage<Post> advancedSearch(int page, int size, String keyword, String category, String sortBy,
            String sortOrder) {
        logger.info("高级搜索帖子，关键词: {}, 分类: {}, 排序字段: {}, 排序方式: {}, 页码: {}, 每页数量: {}",
                keyword, category, sortBy, sortOrder, page, size);

        // 参数处理和验证
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "create_time"; // 默认按创建时间排序
        }

        // 验证排序字段
        if (!isValidSortField(sortBy)) {
            sortBy = "create_time"; // 如果不是有效的排序字段，使用默认值
        }

        // 验证排序方式
        if (sortOrder == null || (!sortOrder.equalsIgnoreCase("ASC") && !sortOrder.equalsIgnoreCase("DESC"))) {
            sortOrder = "DESC"; // 默认降序排序
        }

        // 创建分页参数
        Page<Post> pageParams = new Page<>(page, size);

        // 调用Mapper中的高级搜索方法
        return postMapper.advancedSearch(
                pageParams,
                keyword,
                category,
                sortBy,
                sortOrder.toUpperCase());
    }

    /**
     * 验证排序字段是否有效
     */
    private boolean isValidSortField(String field) {
        if (field == null)
            return false;

        switch (field.toLowerCase()) {
            case "create_time":
            case "like_count":
            case "comment_count":
                return true;
            default:
                return false;
        }
    }

    @Override
    public IPage<Post> getPostsByUserId(Long userId, int page, int size) {
        logger.info("获取用户帖子，用户ID: {}, 页码: {}, 每页数量: {}", userId, page, size);
        
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 创建分页参数
        Page<Post> pageParams = new Page<>(page, size);
        
        // 调用Mapper中的方法查询用户发布的帖子
        return postMapper.selectPostsByUserId(pageParams, userId);
    }
    
    @Override
    public IPage<Post> getFavoritePostsByUserId(Long userId, int page, int size) {
        logger.info("获取用户收藏帖子，用户ID: {}, 页码: {}, 每页数量: {}", userId, page, size);
        
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 创建分页参数
        Page<Post> pageParams = new Page<>(page, size);
        
        // 调用Mapper中的方法查询用户收藏的帖子
        return postMapper.selectFavoritePostsByUserId(pageParams, userId);
    }
}