import { request } from '@/utils/request'

/**
 * 管理员API接口
 */
export const adminApi = {
    /**
     * 验证管理员权限
     * @returns {Promise}
     */
    verifyAdmin() {
        return request.get('/api/admin/verify')
    },

    /**
     * 获取系统统计数据
     * @returns {Promise}
     */
    getStats() {
        return request.get('/api/admin/stats')
    },

    /**
     * 获取用户列表
     * @param {Object} params 查询参数
     * @returns {Promise}
     */
    getUsers(params) {
        return request.get('/api/admin/users', { params })
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
        return request.delete(`/api/admin/posts/${id}`)
    }
} 