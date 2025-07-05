package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.entity.Post;
import com.cupk.entity.Comment;
import com.cupk.mapper.PostMapper;
import com.cupk.mapper.CommentMapper;
import com.cupk.service.PostService;
import com.cupk.service.PostLikeService;
import com.cupk.service.CommentLikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostMapper postMapper;
    private final PostLikeService postLikeService;
    private final CommentMapper commentMapper;
    private final CommentLikeService commentLikeService;

    public PostServiceImpl(PostMapper postMapper, PostLikeService postLikeService,
            CommentMapper commentMapper, CommentLikeService commentLikeService) {
        this.postMapper = postMapper;
        this.postLikeService = postLikeService;
        this.commentMapper = commentMapper;
        this.commentLikeService = commentLikeService;
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
            postMapper.increaseLikeCount(postId);
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
            postMapper.decreaseLikeCount(postId);
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

    /**
     * 重写removeById方法实现级联删除
     * 删除帖子时同时删除相关的评论、点赞等数据
     *
     * @param id 帖子ID
     * @return 是否删除成功
     */
    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        if (id == null) {
            return false;
        }

        Long postId = (Long) id;
        logger.info("开始删除帖子: ID = {}", postId);

        try {
            // 1. 查询该帖子下的所有评论
            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.eq("post_id", postId);

            // 2. 删除评论的点赞记录
            commentMapper.selectList(commentQueryWrapper).forEach(comment -> {
                try {
                    // 删除评论点赞
                    commentLikeService.deleteByCommentId(comment.getId());
                    logger.info("已删除评论ID={}的点赞记录", comment.getId());
                } catch (Exception e) {
                    logger.error("删除评论点赞记录失败，评论ID={}", comment.getId(), e);
                }
            });

            // 3. 删除该帖子的所有评论
            commentMapper.delete(commentQueryWrapper);
            logger.info("已删除帖子ID={}的所有评论", postId);

            // 4. 删除帖子的点赞记录
            postLikeService.deleteByPostId(postId);
            logger.info("已删除帖子ID={}的所有点赞记录", postId);

            // 5. 最后删除帖子
            boolean result = super.removeById(id);
            logger.info("删除帖子完成: ID = {}, 结果 = {}", postId, result);
            return result;
        } catch (Exception e) {
            logger.error("删除帖子过程中发生异常: ID = {}", postId, e);
            throw e; // 重新抛出异常，让事务回滚
        }
    }

    @Override
    @Transactional
    public boolean deletePostByUser(Long postId, Long userId) {
        if (postId == null || userId == null) {
            logger.warn("删除帖子失败，postId或userId为空");
            return false;
        }
        Post post = this.getById(postId);
        if (post == null) {
            logger.warn("删除帖子失败，帖子不存在: {}", postId);
            return false;
        }
        if (!userId.equals(post.getUserId())) {
            logger.warn("删除帖子失败，用户{}不是帖子{}的作者", userId, postId);
            return false;
        }
        // 复用已有的removeById方法，自动级联删除相关数据
        return this.removeById(postId);
    }
}