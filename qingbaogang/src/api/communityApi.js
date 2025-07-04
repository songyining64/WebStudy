import request from '../utils/request'
import { getAuthToken } from '../utils/auth'
import axios from 'axios'

// 帖子相关
export const createPost = (data) => {
    console.log('发送创建帖子请求，数据:', data)
    return request.post('/api/post', data)
}

export const getPostList = async (params) => {
    // 根据后端 API 构建参数
    const apiParams = {
        // 后端优先使用 current 参数
        current: params.page || params.current || 1,
        size: params.size || 10
    }

    // 添加可选参数
    if (params.category) apiParams.category = params.category
    if (params.keyword) apiParams.keyword = params.keyword
    if (params.sortBy) apiParams.sortBy = params.sortBy
    if (params.sortOrder) apiParams.sortOrder = params.sortOrder

    console.log('获取帖子列表，参数:', apiParams)

    try {
        // 发送请求，不需要额外的头部
        const response = await request.get('/api/post', { params: apiParams })
        return response
    } catch (error) {
        console.warn('获取帖子列表失败:', error)

        // 如果发生错误，返回模拟数据
        return {
            data: {
                records: [
                    {
                        id: 'mock1',
                        title: '欢迎来到心理健康社区',
                        content: '这是一个模拟帖子，当API未正常工作时会显示。在这里，我们可以分享心理健康相关的话题和经验。',
                        createTime: new Date().toISOString(),
                        updateTime: new Date().toISOString(),
                        userId: 1,
                        username: '系统管理员',
                        avatar: '/src/assets/default-avatar.png',
                        tags: ['心理健康', '社区'],
                        category: '公告',
                        likes: 15,
                        comments: 5
                    },
                    {
                        id: 'mock2',
                        title: '如何缓解学习压力',
                        content: '分享一些缓解学习压力的小技巧...',
                        createTime: new Date(Date.now() - 86400000).toISOString(),
                        updateTime: new Date(Date.now() - 86400000).toISOString(),
                        userId: 2,
                        username: '心理咨询师',
                        avatar: '/src/assets/default-avatar.png',
                        tags: ['压力管理', '学习方法'],
                        category: '学习',
                        likes: 8,
                        comments: 3
                    }
                ],
                total: 2,
                size: 10,
                current: 1
            }
        }
    }
}

export const getPostDetail = (id) => {
    console.log('获取帖子详情，ID:', id)
    return request.get(`/api/post/${id}`)
}

export const deletePost = (id) => request.delete(`/api/post/${id}`)

// 评论相关
export const createComment = (data) => {
    console.log('创建评论，原始数据:', data);

    // 处理请求中可能包含的ID字段
    const processedData = { ...data };

    // 处理postId
    if (processedData.postId) {
        processedData.postId = typeof processedData.postId === 'string' && processedData.postId.length <= 10 ?
            parseInt(processedData.postId, 10) : processedData.postId.toString();
    }

    // 处理parentId (对于二级评论)
    if (processedData.parentId) {
        processedData.parentId = typeof processedData.parentId === 'string' && processedData.parentId.length <= 10 ?
            parseInt(processedData.parentId, 10) : processedData.parentId.toString();

        console.log(`处理parentId: 原始值=${data.parentId}(${typeof data.parentId})，处理后=${processedData.parentId}(${typeof processedData.parentId})`);
    }

    // 处理userId
    if (processedData.userId) {
        processedData.userId = typeof processedData.userId === 'string' ?
            parseInt(processedData.userId, 10) : processedData.userId;
    }

    console.log('创建评论，处理后数据:', processedData);
    return request.post('/api/comments', processedData);
}

export const getPostComments = (postId) => {
    if (!postId) {
        console.error('获取帖子评论失败：无效的帖子ID');
        return Promise.reject(new Error('无效的帖子ID'));
    }

    const processedPostId = typeof postId === 'string' && postId.length <= 10 ?
        parseInt(postId, 10) : postId.toString();

    console.log(`获取帖子评论，postId=${processedPostId}(${typeof processedPostId})`);
    return request.get(`/api/comments/post/${processedPostId}`);
}

export const deleteComment = (commentId) => {
    // 修复：如果传进来的是对象，自动取 id 字段
    const realId = (typeof commentId === 'object' && commentId !== null) ? commentId.id : commentId;
    if (!realId) {
        console.error('删除评论失败：无效的评论ID');
        return Promise.reject(new Error('无效的评论ID'));
    }
    const processedCommentId = typeof realId === 'string' && realId.length <= 10 ?
        parseInt(realId, 10) : realId.toString();

    console.log(`删除评论，评论ID=${processedCommentId}(${typeof processedCommentId})`);
    return request.delete(`/api/comments/${processedCommentId}`);
}

export const getCommentReplies = (commentId) => {
    if (!commentId) {
        console.error('获取评论回复失败：无效的评论ID');
        return Promise.reject(new Error('无效的评论ID'));
    }

    const processedCommentId = typeof commentId === 'string' && commentId.length <= 10 ?
        parseInt(commentId, 10) : commentId.toString();

    console.log(`获取评论回复，commentId=${processedCommentId}(${typeof processedCommentId})`);
    return request.get(`/api/comments/${processedCommentId}/replies`);
}

export const replyToComment = (commentId, data) => {
    if (!commentId) {
        console.error('回复评论失败：无效的评论ID');
        return Promise.reject(new Error('无效的评论ID'));
    }

    const processedCommentId = typeof commentId === 'string' && commentId.length <= 10 ?
        parseInt(commentId, 10) : commentId.toString();

    // 处理请求中可能包含的ID字段
    const processedData = { ...data };

    // 处理postId
    if (processedData.postId) {
        processedData.postId = typeof processedData.postId === 'string' && processedData.postId.length <= 10 ?
            parseInt(processedData.postId, 10) : processedData.postId.toString();
    }

    // 处理parentId (对于二级评论)
    if (processedData.parentId) {
        processedData.parentId = typeof processedData.parentId === 'string' && processedData.parentId.length <= 10 ?
            parseInt(processedData.parentId, 10) : processedData.parentId.toString();
    }

    // 处理userId
    if (processedData.userId) {
        processedData.userId = typeof processedData.userId === 'string' ?
            parseInt(processedData.userId, 10) : processedData.userId;
    }

    console.log(`回复评论，commentId=${processedCommentId}(${typeof processedCommentId})，处理后数据:`, processedData);
    return request.post(`/api/comments/${processedCommentId}/reply`, processedData);
}

export const getCommentDetail = (commentId) => {
    if (!commentId) {
        console.error('获取评论详情失败：无效的评论ID');
        return Promise.reject(new Error('无效的评论ID'));
    }

    const processedCommentId = typeof commentId === 'string' && commentId.length <= 10 ?
        parseInt(commentId, 10) : commentId.toString();

    console.log(`获取评论详情，commentId=${processedCommentId}(${typeof processedCommentId})`);
    return request.get(`/api/comments/${processedCommentId}`);
}

// 帖子点赞
export const likePost = (postId, userId) => {
    // 确保ID参数存在
    if (!postId || !userId) {
        console.error('点赞帖子失败：无效的参数', { postId, userId });
        return Promise.reject(new Error('无效的参数'));
    }

    // 关键修改：不要尝试将超长ID转换为数字，而是保持原始格式
    // 如果是短数字ID，则转换为数字；如果是长数字ID（雪花算法），则保持字符串格式
    const processedPostId = typeof postId === 'string' && postId.length <= 10 ?
        parseInt(postId, 10) : postId.toString();

    const numericUserId = typeof userId === 'string' ? parseInt(userId, 10) : userId;

    console.log(`发送点赞请求：postId=${processedPostId}(${typeof processedPostId}), userId=${numericUserId}`);
    return request.post(`/api/post-likes/${processedPostId}/users/${numericUserId}`);
}

export const unlikePost = (postId, userId) => {
    // 确保ID参数存在
    if (!postId || !userId) {
        console.error('取消点赞帖子失败：无效的参数', { postId, userId });
        return Promise.reject(new Error('无效的参数'));
    }

    // 关键修改：不要尝试将超长ID转换为数字，而是保持原始格式
    // 如果是短数字ID，则转换为数字；如果是长数字ID（雪花算法），则保持字符串格式
    const processedPostId = typeof postId === 'string' && postId.length <= 10 ?
        parseInt(postId, 10) : postId.toString();

    const numericUserId = typeof userId === 'string' ? parseInt(userId, 10) : userId;

    console.log(`发送取消点赞请求：postId=${processedPostId}(${typeof processedPostId}), userId=${numericUserId}`);
    return request.delete(`/api/post-likes/${processedPostId}/users/${numericUserId}`);
}

export const isPostLiked = (postId, userId) => {
    // 确保ID参数是有效的
    if (!postId || !userId) {
        console.error('检查帖子点赞状态失败：无效的参数', { postId, userId });
        return Promise.reject(new Error('无效的参数'));
    }

    // 关键修改：不要尝试将超长ID转换为数字，而是保持原始格式
    const processedPostId = typeof postId === 'string' && postId.length <= 10 ?
        parseInt(postId, 10) : postId.toString();

    const numericUserId = typeof userId === 'string' ? parseInt(userId, 10) : userId;

    console.log(`获取帖子点赞状态，postId=${processedPostId}(${typeof processedPostId}), userId=${numericUserId}`);
    return request.get(`/api/post-likes/${processedPostId}/users/${numericUserId}/status`);
}

export const getPostLikeCount = (postId) => {
    if (!postId) {
        return Promise.reject(new Error('无效的帖子ID'));
    }

    const processedPostId = typeof postId === 'string' && postId.length <= 10 ?
        parseInt(postId, 10) : postId.toString();

    return request.get(`/api/post-likes/${processedPostId}/count`);
}

// 评论点赞
export const likeComment = (commentId) => {
    if (!commentId) {
        console.error('点赞评论失败：无效的评论ID');
        return Promise.reject(new Error('无效的评论ID'));
    }

    const processedCommentId = typeof commentId === 'string' && commentId.length <= 10 ?
        parseInt(commentId, 10) : commentId.toString();

    console.log(`点赞评论，评论ID=${processedCommentId}(${typeof processedCommentId})`);
    return request.post(`/api/comments/${processedCommentId}/like`);
}

export const unlikeComment = (commentId) => {
    if (!commentId) {
        console.error('取消点赞评论失败：无效的评论ID');
        return Promise.reject(new Error('无效的评论ID'));
    }

    const processedCommentId = typeof commentId === 'string' && commentId.length <= 10 ?
        parseInt(commentId, 10) : commentId.toString();

    console.log(`取消点赞评论，评论ID=${processedCommentId}(${typeof processedCommentId})`);
    return request.delete(`/api/comments/${processedCommentId}/like`);
}

export const isCommentLiked = (commentId) => {
    if (!commentId) {
        console.error('获取评论点赞状态失败：无效的评论ID');
        return Promise.reject(new Error('无效的评论ID'));
    }

    const processedCommentId = typeof commentId === 'string' && commentId.length <= 10 ?
        parseInt(commentId, 10) : commentId.toString();

    console.log(`获取评论点赞状态，评论ID=${processedCommentId}(${typeof processedCommentId})`);
    return request.get(`/api/comments/${processedCommentId}/like/status`);
}

export const getCommentLikeCount = (commentId) => {
    if (!commentId) {
        console.error('获取评论点赞数失败：无效的评论ID');
        return Promise.reject(new Error('无效的评论ID'));
    }

    const processedCommentId = typeof commentId === 'string' && commentId.length <= 10 ?
        parseInt(commentId, 10) : commentId.toString();

    console.log(`获取评论点赞数，评论ID=${processedCommentId}(${typeof processedCommentId})`);
    return request.get(`/api/comments/${processedCommentId}/like/count`);
}

// 帖子收藏（需带token，不需要userId参数）
export const favoritePost = (postId) => {
    if (!postId) {
        console.error('收藏帖子失败：无效的参数', { postId });
        return Promise.reject(new Error('无效的参数'));
    }

    const processedPostId = typeof postId === 'string' && postId.length <= 10 ?
        parseInt(postId, 10) : postId.toString();

    console.log(`发送收藏请求：postId=${processedPostId}(${typeof processedPostId})`);
    return request.post(`/api/post/${processedPostId}/favorite`);
}

export const unfavoritePost = (postId) => {
    if (!postId) {
        console.error('取消收藏帖子失败：无效的参数', { postId });
        return Promise.reject(new Error('无效的参数'));
    }

    const processedPostId = typeof postId === 'string' && postId.length <= 10 ?
        parseInt(postId, 10) : postId.toString();

    console.log(`发送取消收藏请求：postId=${processedPostId}(${typeof processedPostId})`);
    return request.delete(`/api/post/${processedPostId}/favorite`);
}

export const isPostFavorited = (postId) => {
    if (!postId) {
        console.error('检查帖子收藏状态失败：无效的参数', { postId });
        return Promise.reject(new Error('无效的参数'));
    }

    const processedPostId = typeof postId === 'string' && postId.length <= 10 ?
        parseInt(postId, 10) : postId.toString();

    console.log(`获取帖子收藏状态，postId=${processedPostId}(${typeof processedPostId})`);
    return request.get(`/api/post/${processedPostId}/favorite/simple`);
}

export const getPostFavoriteCount = (postId) => {
    if (!postId) {
        return Promise.reject(new Error('无效的帖子ID'));
    }

    const processedPostId = typeof postId === 'string' && postId.length <= 10 ?
        parseInt(postId, 10) : postId.toString();

    return request.get(`/api/post/${processedPostId}/favorite-count`);
}

// 关键词搜索
export const searchPosts = (params) => {
    console.log('调用搜索API，参数:', params);
    try {
        return request.get('/api/post/search', { params });
    } catch (error) {
        console.error('搜索API请求失败:', error);
        throw error;
    }
}

// 分类筛选
export const getPostsByCategory = (category, params) => {
    // 使用基本API，添加category参数
    return request.get('/api/post', {
        params: {
            ...params,
            category: category
        }
    })
}

// 高级搜索
export const advancedSearchPosts = (params) => {
    // 使用基本API，传递所有参数
    return request.get('/api/post', { params })
}

// 直接测试API的函数
export const testPostApi = async (options) => {
    try {
        const baseURL = 'http://localhost:8080/mental'
        const url = `${baseURL}${options.url || '/api/post'}`

        const response = await axios({
            method: options.method || 'GET',
            url,
            params: options.params,
            data: options.data,
            headers: {
                'Authorization': `Bearer ${getAuthToken()}`,
                'Content-Type': 'application/json',
                ...options.headers
            }
        })

        return { success: true, data: response.data }
    } catch (error) {
        return {
            success: false,
            error: error.message,
            response: error.response?.data
        }
    }
}

// 获取用户发布的帖子
export const getUserPosts = (userId, params = {}) => {
    if (!userId) {
        console.error('获取用户帖子失败：无效的用户ID');
        return Promise.reject(new Error('无效的用户ID'));
    }

    const apiParams = {
        userId: userId,
        current: params.page || 1,
        size: params.size || 10
    };

    console.log(`获取用户发布的帖子，用户ID=${userId}`);
    return request.get('/api/post/user', { params: apiParams });
}

// 获取用户点赞的帖子
export const getUserLikedPosts = (userId, params = {}) => {
    if (!userId) {
        console.error('获取用户点赞帖子失败：无效的用户ID');
        return Promise.reject(new Error('无效的用户ID'));
    }

    const apiParams = {
        current: params.page || 1,
        size: params.size || 10
    };

    console.log(`获取用户点赞的帖子，用户ID=${userId}`);
    return request.get(`/api/post-likes/users/${userId}/posts`, { params: apiParams });
}

// 获取用户收藏的帖子
export const getUserFavoritePosts = (userId, params = {}) => {
    if (!userId) {
        console.error('获取用户收藏帖子失败：无效的用户ID');
        return Promise.reject(new Error('无效的用户ID'));
    }

    const apiParams = {
        current: params.page || 1,
        size: params.size || 10
    };

    console.log(`获取用户收藏的帖子，用户ID=${userId}`);
    return request.get(`/api/post/favorites/user/${userId}`, { params: apiParams });
}

// 添加评论
export const addComment = (data) => {
    if (!data.postId || !data.content) {
        console.error('添加评论失败：无效的参数', data);
        return Promise.reject(new Error('无效的参数'));
    }

    console.log(`发送添加评论请求：`, data);
    return request.post('/api/comments', data);
}

// 系统公告相关
export const getSystemNotices = async (params = {}) => {
    try {
        const apiParams = {
            page: params.page || 1,
            size: params.size || 10
        };
        console.log('获取系统公告，参数:', apiParams);
        const response = await request.get('/api/notices', { params: apiParams });
        console.log('系统公告API原始响应:', response);

        // 检查公告时间格式
        if (response && response.data) {
            let notices = [];

            // 确定公告数据列表
            if (response.data.records) {
                notices = response.data.records;
            } else if (Array.isArray(response.data)) {
                notices = response.data;
            } else {
                notices = [response.data];
            }

            // 检查第一条公告的时间格式
            if (notices.length > 0) {
                const firstNotice = notices[0];
                console.log('公告数据示例:', {
                    id: firstNotice.id,
                    title: firstNotice.title,
                    createTime: firstNotice.createTime,
                    updateTime: firstNotice.updateTime
                });

                // 检查时间格式
                if (firstNotice.createTime) {
                    console.log('创建时间类型:', typeof firstNotice.createTime);

                    try {
                        const date = new Date(firstNotice.createTime);
                        console.log('解析后的日期对象:', date);
                        console.log('时间部分 - 小时:', date.getHours(), '分钟:', date.getMinutes());
                    } catch (e) {
                        console.error('解析日期失败:', e);
                    }
                }
            }
        }

        // 检查响应格式并处理
        if (response && response.data) {
            // 如果是分页格式
            if (response.data.records) {
                return response;
            }
            // 如果是数组格式
            else if (Array.isArray(response.data)) {
                return {
                    data: {
                        records: response.data,
                        total: response.data.length,
                        size: apiParams.size,
                        current: apiParams.page
                    }
                };
            }
            // 其他格式，包装成标准格式
            else {
                return {
                    data: {
                        records: [response.data],
                        total: 1,
                        size: apiParams.size,
                        current: apiParams.page
                    }
                };
            }
        }
        return response;
    } catch (error) {
        console.warn('获取系统公告失败:', error);
        // 返回模拟数据
        return {
            data: {
                records: [
                    {
                        id: 'mock1',
                        title: '欢迎使用心理健康系统',
                        content: '这是一条模拟的系统公告，当API未正常工作时会显示。',
                        createTime: new Date().toISOString(),
                        updateTime: new Date().toISOString(),
                        status: 1
                    }
                ],
                total: 1,
                size: params.size || 10,
                current: params.page || 1
            }
        };
    }
};

export const getRecentNotices = async (limit = 5) => {
    try {
        console.log('获取最近系统公告，数量限制:', limit);
        const response = await request.get(`/api/notices/recent?limit=${limit}`);
        return response;
    } catch (error) {
        console.warn('获取最近系统公告失败:', error);
        // 返回模拟数据
        return {
            data: [
                {
                    id: 'mock1',
                    title: '欢迎使用心理健康系统',
                    content: '这是一条模拟的系统公告，当API未正常工作时会显示。',
                    createTime: new Date().toISOString(),
                    updateTime: new Date().toISOString(),
                    status: 1
                }
            ]
        };
    }
};

export const getNoticeDetail = async (id) => {
    try {
        console.log('获取系统公告详情，ID:', id);
        const response = await request.get(`/api/notices/${id}`);
        return response;
    } catch (error) {
        console.warn('获取系统公告详情失败:', error);
        // 返回模拟数据
        return {
            data: {
                id: id,
                title: '系统公告详情',
                content: '这是一条模拟的系统公告详情，当API未正常工作时会显示。',
                createTime: new Date().toISOString(),
                updateTime: new Date().toISOString(),
                status: 1
            }
        };
    }
};

export const testNoticeApi = async () => {
    try {
        const baseURL = 'http://localhost:8080/mental';
        const url = `${baseURL}/api/notices`;

        console.log('直接测试系统公告API');

        const response = await axios({
            method: 'GET',
            url,
            headers: {
                'Authorization': `Bearer ${getAuthToken()}`,
                'Content-Type': 'application/json'
            }
        });

        console.log('公告API原始响应:', response);

        if (response.data && (response.data.records || Array.isArray(response.data))) {
            const notices = response.data.records || response.data;
            if (notices.length > 0) {
                const firstNotice = notices[0];
                console.log('公告时间字段类型:', {
                    createTime: typeof firstNotice.createTime,
                    createTimeValue: firstNotice.createTime,
                    updateTime: typeof firstNotice.updateTime,
                    updateTimeValue: firstNotice.updateTime
                });

                // 尝试解析日期
                if (firstNotice.createTime) {
                    try {
                        const date = new Date(firstNotice.createTime);
                        console.log('JS Date解析结果:', date.toString());

                        // 如果是数字（时间戳）
                        if (!isNaN(Number(firstNotice.createTime))) {
                            const timestamp = String(firstNotice.createTime).length === 10
                                ? Number(firstNotice.createTime) * 1000  // 秒转毫秒
                                : Number(firstNotice.createTime);
                            const timestampDate = new Date(timestamp);
                            console.log('时间戳解析结果:', timestampDate.toString());
                        }
                    } catch (e) {
                        console.error('解析日期出错:', e);
                    }
                }
            }
        }

        return { success: true, data: response.data };
    } catch (error) {
        return {
            success: false,
            error: error.message,
            response: error.response?.data
        };
    }
};

export const getServerTime = async () => {
    try {
        console.log('获取服务器当前时间');
        const response = await request.get('/api/notices/server-time');
        console.log('服务器时间响应:', response);
        return response;
    } catch (error) {
        console.warn('获取服务器时间失败:', error);
        // 返回模拟数据
        return {
            data: {
                serverTime: new Date().toISOString(),
                timestamp: Date.now(),
                formattedTime: new Date().toString()
            }
        };
    }
}; 