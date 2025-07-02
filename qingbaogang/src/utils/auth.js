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

export function setUserRole(role) {
    let r = role;
    if (role === '管理员') r = 'admin';
    if (role === '用户') r = 'user';
    localStorage.setItem('userRole', r);
}

export function getUserRole() {
    let r = localStorage.getItem('userRole');
    if (r === '管理员') r = 'admin';
    if (r === '用户') r = 'user';
    return r;
}