// src/utils/request.js

import axios from 'axios';
import { getAuthToken } from './auth';

// 从环境变量中获取 API 基础路径

// 修改这里，指向你的后端服务
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/mental';

// 创建 axios 实例
const instance = axios.create({
    baseURL,
    timeout: 15000, // 15秒超时
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
});

/**
 * 请求拦截器
 * 在发送请求之前添加认证 Token
 */
instance.interceptors.request.use(
    config => {
        // 从 localStorage 获取认证令牌
        const token = getAuthToken();

        // 如果存在 token，添加到请求头
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        // 添加请求拦截器，保证X-User-Role只为英文
        let role = localStorage.getItem('userRole');
        if (role === '管理员') role = 'admin';
        if (role === '用户') role = 'user';
        if (role === 'admin' || role === 'user') {
            config.headers['X-User-Role'] = role;
        } else {
            // 不是admin/user就不加header，防止ISO-8859-1错误
            delete config.headers['X-User-Role'];
        }

        return config;
    },
    error => {
        // 请求错误处理
        return Promise.reject(error);
    }
);

/**
 * 响应拦截器
 * 统一处理 API 响应和错误
 */
instance.interceptors.response.use(
    response => {
        // 成功响应处理
        return response.data;
    },
    error => {
        // 统一错误处理
        let errorMessage = '请求失败，请稍后重试';
        let statusCode = 500;

        if (error.response) {
            // 服务器返回错误响应（状态码非 2xx）
            statusCode = error.response.status;

            // 根据不同状态码处理
            switch (statusCode) {
                case 400:
                    errorMessage = error.response.data?.message || '请求参数错误';

                    // 特殊处理：如果是获取帖子列表的请求，返回模拟数据
                    if (error.config.url.includes('/api/post') && error.config.method === 'get') {
                        console.log('获取帖子列表失败，返回模拟数据');
                        return {
                            data: [
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
                            ]
                        };
                    }
                    break;
                case 401:
                    errorMessage = '未授权，请重新登录';
                    // 触发全局未授权事件
                    window.dispatchEvent(new CustomEvent('unauthorized'));
                    break;
                case 403:
                    errorMessage = '禁止访问，权限不足';
                    break;
                case 404:
                    errorMessage = '请求资源不存在';
                    break;
                case 500:
                    errorMessage = '服务器内部错误';
                    break;
                default:
                    errorMessage = error.response.data?.message || '请求错误';
            }
        } else if (error.request) {
            // 请求已发送但无响应（超时或网络问题）
            if (error.code === 'ECONNABORTED') {
                errorMessage = '请求超时，请检查网络连接';
            } else {
                errorMessage = '网络错误，请检查网络连接';
            }
        } else {
            // 其他错误
            errorMessage = error.message;
        }

        // 返回统一的错误对象
        return Promise.reject({
            status: statusCode,
            message: errorMessage,
            data: error.response?.data
        });
    }
);

// 导出封装后的请求方法
export const request = {
    get: (url, params = {}, config = {}) => instance.get(url, { ...config, params }),
    post: (url, data = {}, config = {}) => instance.post(url, data, config),
    put: (url, data = {}, config = {}) => instance.put(url, data, config),
    delete: (url, config = {}) => instance.delete(url, config),
    patch: (url, data = {}, config = {}) => instance.patch(url, data, config),

    // 文件上传
    upload: (url, file, config = {}) => {
        const formData = new FormData();
        formData.append('file', file);
        return instance.post(url, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
            ...config
        });
    }
};

// 导出实例用于特殊场景
export default instance;