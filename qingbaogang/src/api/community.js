import request from '../utils/request'
import { getAuthToken } from '../utils/auth'

// 帖子相关
export const createPost = (data) => request.post('/api/post', data)
export const getPostList = (params) => request.get('/api/post', { params })
export const getPostDetail = (id) => request.get(`/api/post/${id}`)
export const deletePost = (id) => request.delete(`/api/post/${id}`)

// 评论相关
export const createComment = (data) => request.post('/api/comments', data)
export const deleteComment = (commentId, userId) => request.delete(`/api/comments/${commentId}/users/${userId}`)
export const getPostComments = (postId) => request.get(`/api/comments/post/${postId}`)
export const getCommentReplies = (commentId) => request.get(`/api/comments/${commentId}/replies`)

// 帖子点赞
export const likePost = (postId, userId) => {
    // 确保ID参数是有效的数字
    if (!postId || !userId) {
        console.error('点赞帖子失败：无效的参数', { postId, userId });
        return Promise.reject(new Error('无效的参数'));
    }

    // 尝试将ID转换为数字类型（如果是字符串的话）
    const numericPostId = typeof postId === 'string' ? parseInt(postId, 10) : postId;
    const numericUserId = typeof userId === 'string' ? parseInt(userId, 10) : userId;

    console.log(`发送点赞请求：postId=${numericPostId}, userId=${numericUserId}`);
    return request.post(`/api/post-likes/${numericPostId}/users/${numericUserId}`);
}

export const unlikePost = (postId, userId) => {
    // 确保ID参数是有效的数字
    if (!postId || !userId) {
        console.error('取消点赞帖子失败：无效的参数', { postId, userId });
        return Promise.reject(new Error('无效的参数'));
    }

    // 尝试将ID转换为数字类型（如果是字符串的话）
    const numericPostId = typeof postId === 'string' ? parseInt(postId, 10) : postId;
    const numericUserId = typeof userId === 'string' ? parseInt(userId, 10) : userId;

    console.log(`发送取消点赞请求：postId=${numericPostId}, userId=${numericUserId}`);
    return request.delete(`/api/post-likes/${numericPostId}/users/${numericUserId}`);
}

export const isPostLiked = (postId, userId) => {
    // 确保ID参数是有效的数字
    if (!postId || !userId) {
        console.error('检查帖子点赞状态失败：无效的参数', { postId, userId });
        return Promise.reject(new Error('无效的参数'));
    }

    // 尝试将ID转换为数字类型（如果是字符串的话）
    const numericPostId = typeof postId === 'string' ? parseInt(postId, 10) : postId;
    const numericUserId = typeof userId === 'string' ? parseInt(userId, 10) : userId;

    return request.get(`/api/post-likes/${numericPostId}/users/${numericUserId}/status`);
}
export const getPostLikeCount = (postId) => request.get(`/api/post-likes/${postId}/count`)

// 评论点赞
export const likeComment = (commentId) => request.post(`/api/comments/${commentId}/like`)
export const unlikeComment = (commentId) => request.delete(`/api/comments/${commentId}/like`)
export const isCommentLiked = (commentId) => request.get(`/api/comments/${commentId}/like/status`)
export const getCommentLikeCount = (commentId) => request.get(`/api/comments/${commentId}/like/count`)

// 帖子收藏（需带token）
export const favoritePost = (postId) => request.post(`/api/post/${postId}/favorite`)
export const unfavoritePost = (postId) => request.delete(`/api/post/${postId}/favorite`)
export const isPostFavorited = (postId) => request.get(`/api/post/${postId}/favorite/simple`)
export const getPostFavoriteCount = (postId) => request.get(`/api/post/${postId}/favorite-count`) 