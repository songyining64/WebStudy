package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cupk.entity.Post;
import com.cupk.entity.PostLike;
import com.cupk.mapper.PostLikeMapper;
import com.cupk.mapper.PostMapper;
import com.cupk.service.PostLikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PostLikeServiceImpl implements PostLikeService {
    private static final Logger logger = LoggerFactory.getLogger(PostLikeServiceImpl.class);

    private final PostLikeMapper postLikeMapper;
    private final PostMapper postMapper;

    public PostLikeServiceImpl(PostLikeMapper postLikeMapper, PostMapper postMapper) {
        this.postLikeMapper = postLikeMapper;
        this.postMapper = postMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likePost(Long postId, Long userId) {
        logger.info("尝试点赞帖子，帖子ID: {}, 用户ID: {}", postId, userId);

        try {
            // 参数验证
            if (postId == null || userId == null) {
                logger.error("点赞失败：帖子ID或用户ID为空");
                return false;
            }

            // 检查是否已经点赞
            if (hasLiked(postId, userId)) {
                logger.warn("用户 {} 已经点赞过帖子 {}", userId, postId);
                return false;
            }

            // 检查帖子是否存在
            Post post = postMapper.selectById(postId);
            if (post == null) {
                logger.error("点赞失败：帖子ID {} 不存在", postId);
                return false;
            }

            logger.info("点赞前帖子信息: {}", post);

            // 创建新的点赞记录
            PostLike postLike = new PostLike();
            postLike.setPostId(postId);
            postLike.setUserId(userId);
            postLike.setCreateTime(LocalDateTime.now());

            // 更新帖子点赞数
            int currentLikes = post.getLikeCount() == null ? 0 : post.getLikeCount();
            post.setLikeCount(currentLikes + 1);
            int updateResult = postMapper.updateById(post);
            logger.info("更新帖子点赞数结果: {}, 更新后的点赞数: {}", updateResult, post.getLikeCount());

            int insertResult = postLikeMapper.insert(postLike);
            logger.info("插入点赞记录结果: {}", insertResult);

            return true;
        } catch (Exception e) {
            logger.error("点赞帖子时发生异常，帖子ID: {}, 用户ID: {}", postId, userId, e);
            throw e; // 重新抛出异常以便事务回滚
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlikePost(Long postId, Long userId) {
        logger.info("尝试取消点赞帖子，帖子ID: {}, 用户ID: {}", postId, userId);

        try {
            // 参数验证
            if (postId == null || userId == null) {
                logger.error("取消点赞失败：帖子ID或用户ID为空");
                return false;
            }

            // 检查帖子是否存在
            Post post = postMapper.selectById(postId);
            if (post == null) {
                logger.error("取消点赞失败：帖子ID {} 不存在", postId);
                return false;
            }

            // 检查是否已经点赞
            if (!hasLiked(postId, userId)) {
                logger.warn("用户 {} 尚未点赞帖子 {}", userId, postId);
                return false;
            }

            logger.info("取消点赞前帖子信息: {}", post);

            // 更新帖子点赞数
            if (post.getLikeCount() != null && post.getLikeCount() > 0) {
                post.setLikeCount(post.getLikeCount() - 1);
                int updateResult = postMapper.updateById(post);
                logger.info("更新帖子点赞数结果: {}, 更新后的点赞数: {}", updateResult, post.getLikeCount());
            } else {
                logger.warn("帖子 {} 的点赞数已为0或为null", postId);
            }

            LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PostLike::getPostId, postId)
                    .eq(PostLike::getUserId, userId);

            int deleteResult = postLikeMapper.delete(wrapper);
            logger.info("删除点赞记录结果: {}", deleteResult);

            return deleteResult > 0;
        } catch (Exception e) {
            logger.error("取消点赞帖子时发生异常，帖子ID: {}, 用户ID: {}", postId, userId, e);
            throw e; // 重新抛出异常以便事务回滚
        }
    }

    @Override
    public boolean hasLiked(Long postId, Long userId) {
        try {
            if (postId == null || userId == null) {
                logger.warn("检查点赞状态时参数无效: postId={}, userId={}", postId, userId);
                return false;
            }

            LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PostLike::getPostId, postId)
                    .eq(PostLike::getUserId, userId);

            long count = postLikeMapper.selectCount(wrapper);
            logger.info("检查点赞状态: postId={}, userId={}, 结果={}", postId, userId, count > 0);
            return count > 0;
        } catch (Exception e) {
            logger.error("检查点赞状态时发生异常，帖子ID: {}, 用户ID: {}", postId, userId, e);
            return false;
        }
    }

    @Override
    public Long getLikeCount(Long postId) {
        try {
            if (postId == null) {
                return 0L;
            }

            LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PostLike::getPostId, postId);

            return postLikeMapper.selectCount(wrapper);
        } catch (Exception e) {
            logger.error("获取点赞数量时发生异常，帖子ID: {}", postId, e);
            return 0L;
        }
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<Post> getLikedPostsByUserId(Long userId, int page, int size) {
        logger.info("获取用户点赞的帖子列表，用户ID: {}, 页码: {}, 每页大小: {}", userId, page, size);
        
        try {
            if (userId == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }
            
            // 创建分页参数
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<Post> pageParams = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
            
            // 调用Mapper查询用户点赞的帖子
            return postLikeMapper.selectLikedPostsByUserId(pageParams, userId);
        } catch (Exception e) {
            logger.error("获取用户点赞的帖子列表时发生异常，用户ID: {}", userId, e);
            throw e;
        }
    }
}