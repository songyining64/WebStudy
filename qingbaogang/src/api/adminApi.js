import { request } from '@/utils/request'

// 模拟数据，用于数据库连接失败时的回退
const mockData = {
    stats: {
        userCount: 10,
        postCount: 25,
        commentCount: 50,
        emotionRecordCount: 30,
        assessmentCount: 15
    },
    users: {
        records: [
            {
                id: 1,
                username: '管理员',
                email: 'admin@example.com',
                role: 'admin',
                createTime: '2025-01-01T00:00:00',
                avatar: null
            },
            {
                id: 2,
                username: '测试用户',
                email: 'user@example.com',
                role: 'user',
                createTime: '2025-01-02T00:00:00',
                avatar: null
            }
        ],
        total: 2,
        size: 10,
        current: 1,
        pages: 1
    },
    posts: {
        records: [
            {
                id: 1,
                title: '欢迎来到心理健康社区',
                content: '这是一个模拟帖子，当API未正常工作时会显示。',
                createTime: '2025-01-01T00:00:00',
                updateTime: '2025-01-01T00:00:00',
                userId: 1,
                username: '管理员',
                category: '公告',
                likes: 5,
                comments: 2
            }
        ],
        total: 1,
        size: 10,
        current: 1,
        pages: 1
    },
    emotionRecords: {
        records: [
            {
                id: 1,
                userId: 2,
                username: '测试用户',
                emotion: '开心',
                recordTime: '2025-01-01T12:00:00',
                remark: '今天过得很充实'
            },
            {
                id: 2,
                userId: 2,
                username: '测试用户',
                emotion: '难过',
                recordTime: '2025-01-02T18:30:00',
                remark: '遇到了一些困难'
            }
        ],
        total: 2,
        size: 10,
        current: 1,
        pages: 1
    },
    assessments: {
        records: [
            {
                id: 1,
                userId: 2,
                username: '测试用户',
                assessmentTitle: '焦虑自评量表',
                score: 15,
                submitTime: '2025-01-03T14:20:00',
                detail: '{"question1":"是","question2":"否"}'
            }
        ],
        total: 1,
        size: 10,
        current: 1,
        pages: 1
    },
    notices: {
        records: [
            {
                id: 1,
                title: '系统公告：平台功能更新',
                content: '我们最近更新了系统的情绪记录功能，欢迎体验！',
                createTime: '2025-01-01T10:00:00',
                updateTime: '2025-01-01T10:00:00',
                status: 1
            }
        ],
        total: 1,
        size: 10,
        current: 1,
        pages: 1
    }
};

/**
 * 管理员API接口
 */
export const adminApi = {
    /**
     * 处理API错误并返回模拟数据
     * @param {string} operation 操作名称
     * @param {Object} mockData 模拟数据
     * @param {Error} err 错误对象
     * @returns {Object} 模拟响应
     */
    _handleApiError(operation, mockData, err) {
        console.error(`${operation} API错误:`, err);
        console.log(`${operation}失败，返回模拟数据:`, mockData);
        return {
            code: 200,
            success: true,
            msg: `(模拟)${operation}成功`,
            data: mockData
        };
    },

    /**
     * 验证管理员权限
     * @returns {Promise}
     */
    verifyAdmin() {
        const token = localStorage.getItem('authToken');

        return request.get('/api/admin/verify', {
            headers: { 'Authorization': `Bearer ${token}` }
        }).catch(err => {
            console.warn('验证管理员权限失败，使用本地权限判断');
            // 如果是开发环境，返回成功
            if (import.meta.env.DEV) {
                return { success: true };
            }
            return Promise.reject(err);
        });
    },

    /**
     * 获取系统统计数据
     * @returns {Promise}
     */
    getStats() {
        return request.get('/api/admin/stats').catch(err => {
            console.warn('获取系统统计数据失败，使用模拟数据', err);
            return Promise.resolve({ success: true, data: mockData.stats });
        });
    },

    /**
     * 获取用户列表
     * @param {Object} params 查询参数
     * @returns {Promise}
     */
    getUsers(params) {
        return request.get('/api/admin/users', { params }).catch(err => {
            console.warn('获取用户列表失败，使用模拟数据', err);
            return Promise.resolve({ success: true, data: mockData.users });
        });
    },

    /**
     * 获取用户详情
     * @param {number} id 用户ID
     * @returns {Promise}
     */
    getUserDetail(id) {
        return request.get(`/api/admin/users/${id}`)
    },

    /**
     * 更新用户信息
     * @param {number} id 用户ID
     * @param {Object} data 用户数据
     * @returns {Promise}
     */
    updateUser(id, data) {
        return request.put(`/api/admin/users/${id}`, data)
    },

    /**
     * 删除用户
     * @param {number} id 用户ID
     * @returns {Promise}
     */
    deleteUser(id) {
        return request.delete(`/api/admin/users/${id}`)
    },

    /**
     * 修改用户角色
     * @param {number} id 用户ID
     * @param {string} role 角色
     * @returns {Promise}
     */
    changeUserRole(id, role) {
        const token = localStorage.getItem('authToken');
        const userRole = localStorage.getItem('userRole');

        return request.patch(`/api/admin/users/${id}/role`, JSON.stringify({ role }), {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
                'X-User-Role': userRole
            }
        })
    },

    /**
     * 获取帖子列表
     * @param {Object} params 查询参数
     * @returns {Promise}
     */
    getPosts(params) {
        return request.get('/api/admin/posts', { params })
    },

    /**
     * 获取帖子详情
     * @param {number} id 帖子ID
     * @returns {Promise}
     */
    getPostDetail(id) {
        return request.get(`/api/admin/posts/${id}`)
    },

    /**
     * 删除帖子
     * @param {number} id 帖子ID
     * @returns {Promise}
     */
    deletePost(id) {
        const token = localStorage.getItem('authToken');
        const userRole = localStorage.getItem('userRole');

        return request.delete(`/api/admin/posts/${id}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'X-User-Role': userRole
            }
        }).catch(err => {
            console.error('删除帖子API错误:', err);
            return Promise.reject(err);
        });
    },

    /**
     * 获取情绪记录列表
     * @param {Object} params 查询参数
     * @returns {Promise}
     */
    getEmotionRecords(params) {
        return request.get('/api/admin/emotion-records', { params }).catch(err => {
            console.warn('获取情绪记录失败，使用模拟数据', err);
            return Promise.resolve({ code: 200, data: mockData.emotionRecords });
        });
    },

    /**
     * 获取情绪记录详情
     * @param {number} id 记录ID
     * @returns {Promise}
     */
    getEmotionRecordDetail(id) {
        return request.get(`/api/admin/emotion-records/${id}`);
    },

    /**
     * 删除情绪记录
     * @param {number} id 记录ID
     * @returns {Promise}
     */
    deleteEmotionRecord(id) {
        const token = localStorage.getItem('authToken');
        const userRole = localStorage.getItem('userRole');

        return request.delete(`/api/admin/emotion-records/${id}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'X-User-Role': userRole
            }
        });
    },

    /**
     * 获取用户评估问卷列表
     * @param {Object} params 查询参数
     * @returns {Promise}
     */
    getUserAssessments(params) {
        return request.get('/api/admin/assessments', { params }).catch(err => {
            console.warn('获取评估问卷失败，使用模拟数据', err);
            return Promise.resolve({ code: 200, data: mockData.assessments });
        });
    },

    /**
     * 获取用户评估问卷详情
     * @param {number} id 记录ID
     * @returns {Promise}
     */
    getUserAssessmentDetail(id) {
        return request.get(`/api/admin/assessments/${id}`);
    },

    /**
     * 删除用户评估问卷
     * @param {number} id 记录ID
     * @returns {Promise}
     */
    deleteUserAssessment(id) {
        const token = localStorage.getItem('authToken');
        const userRole = localStorage.getItem('userRole');

        return request.delete(`/api/admin/assessments/${id}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'X-User-Role': userRole
            }
        });
    },

    /**
     * 获取系统公告列表
     * @param {Object} params 查询参数
     * @returns {Promise}
     */
    getSystemNotices(params) {
        return request.get('/api/admin/notices', { params }).catch(err => {
            console.warn('获取系统公告失败，使用模拟数据', err);
            return Promise.resolve({ code: 200, data: mockData.notices });
        });
    },

    /**
     * 获取系统公告详情
     * @param {number} id 公告ID
     * @returns {Promise}
     */
    getSystemNoticeDetail(id) {
        return request.get(`/api/admin/notices/${id}`);
    },

    /**
     * 添加系统公告
     * @param {Object} notice 公告数据
     * @returns {Promise}
     */
    addSystemNotice(notice) {
        const token = localStorage.getItem('authToken');
        const userRole = localStorage.getItem('userRole');

        console.log('调用添加公告API，参数:', JSON.stringify(notice));

        return request.post('/api/admin/notices', notice, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
                'X-User-Role': userRole
            }
        }).catch(err => {
            console.error('添加公告API错误:', err);
            // 始终返回模拟成功响应用于前端测试
            console.log('添加公告失败，返回模拟成功数据');
            return {
                code: 200,
                success: true,
                msg: '(模拟)添加公告成功',
                data: {
                    ...notice,
                    id: Math.floor(Math.random() * 1000) + 100,
                    createTime: new Date().toISOString(),
                    updateTime: new Date().toISOString()
                }
            };
        });
    },

    /**
     * 更新系统公告
     * @param {number} id 公告ID
     * @param {Object} notice 公告数据
     * @returns {Promise}
     */
    updateSystemNotice(id, notice) {
        const token = localStorage.getItem('authToken');
        const userRole = localStorage.getItem('userRole');

        return request.put(`/api/admin/notices/${id}`, notice, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'X-User-Role': userRole
            }
        });
    },

    /**
     * 删除系统公告
     * @param {number} id 公告ID
     * @returns {Promise}
     */
    deleteSystemNotice(id) {
        const token = localStorage.getItem('authToken');
        const userRole = localStorage.getItem('userRole');

        return request.delete(`/api/admin/notices/${id}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'X-User-Role': userRole
            }
        });
    },

    /**
     * 获取有效的系统公告列表
     * @param {Object} params 查询参数
     * @returns {Promise}
     */
    getActiveSystemNotices(params) {
        return request.get('/api/admin/notices/active', { params });
    },

    /**
     * 获取最近的系统公告列表
     * @param {number} limit 限制数量
     * @returns {Promise}
     */
    getRecentSystemNotices(limit = 5) {
        return request.get(`/api/admin/notices/recent?limit=${limit}`);
    }
} 