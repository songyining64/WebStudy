// src/utils/request.js

import axios from 'axios';
import { getAuthToken } from './auth';

// 从环境变量中获取 API 基础路径
// 使用相对路径，让Vite代理处理CORS
const baseURL = import.meta.env.VITE_API_BASE_URL || '';

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

        // 添加用户角色到请求头
        const role = localStorage.getItem('userRole');
        if (role) {
            config.headers['X-User-Role'] = role;
            // 记录请求发送的角色信息，用于调试
            console.log('请求头携带角色信息:', role);
        }

        // 添加Content-Type头
        if (config.method === 'patch' || config.method === 'post' || config.method === 'put') {
            if (!config.headers['Content-Type'] && !config.headers.get('Content-Type')) {
                config.headers['Content-Type'] = 'application/json';
            }
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
                    break;
                case 401:
                    errorMessage = '未授权，请重新登录';
                    // 清除无效的token和角色信息
                    localStorage.removeItem('authToken');
                    localStorage.removeItem('userRole');
                    localStorage.removeItem('isAdmin');
                    // 触发全局未授权事件
                    window.dispatchEvent(new CustomEvent('unauthorized'));
                    break;
                case 403:
                    errorMessage = '禁止访问，权限不足';
                    // 检查是否需要管理员权限
                    if (error.config.url.includes('/api/admin')) {
                        // 可能是用户无管理员权限但尝试访问管理页面
                        console.error('权限不足，尝试访问管理员接口:', error.config.url);
                    }
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
    get: (url, config = {}) => instance.get(url, config),
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