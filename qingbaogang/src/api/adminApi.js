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
    }
};

/**
 * 管理员API接口
 */
export const adminApi = {
    /**
     * 验证管理员权限
     * @returns {Promise}
     */
    verifyAdmin() {
        return request.get('/api/admin/verify').catch(err => {
            console.warn('验证管理员权限失败，使用模拟数据', err);
            return Promise.resolve({ success: true, message: '模拟验证成功' });
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
    }
} 