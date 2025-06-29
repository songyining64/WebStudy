export const saveAuthToken = (token) => {
    localStorage.setItem('authToken', token);
};

export const getAuthToken = () => {
    return localStorage.getItem('authToken');
};

export const clearAuthToken = () => {
    localStorage.removeItem('authToken');
};

// 检查用户是否已登录
export const isAuthenticated = () => {
    return !!getAuthToken();
};

// 在路由守卫中使用
export const authGuard = (to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!isAuthenticated()) {
            next({
                path: '/login',
                query: { redirect: to.fullPath }
            });
        } else {
            next();
        }
    } else {
        next();
    }
};