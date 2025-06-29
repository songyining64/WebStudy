import { createRouter, createWebHistory } from 'vue-router'

import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Home from '@/views/Home.vue'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/register',
        name: 'Register',
        component: Register
    },
    {
        path: '/home',
        name: 'Home',
        component: Home,
        meta: { requiresAuth: true }
    },
    {
        path: '/user-settings',
        name: 'UserSettings',
        component: () => import('@/views/UserSettings.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/manage',
        name: 'Manage',
        component: () => import('@/views/Manage.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
        path: '/test',
        name: 'Test',
        component: () => import('@/views/Test.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/chatbot',
        name: 'ChatBot',
        component: () => import('@/views/ChatBot.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/resource',
        name: 'Resource',
        component: () => import('@/views/Resource.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/community',
        name: 'Community',
        component: () => import('@/views/Community.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/history',
        name: 'History',
        component: () => import('@/views/History.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/:pathMatch(.*)*',
        component: () => import('@/views/404.vue')
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const isAuthenticated = localStorage.getItem('userName') // 简单的认证检查
    const isAdmin = localStorage.getItem('isAdmin') === 'true'
    
    if (to.meta.requiresAuth && !isAuthenticated) {
        next('/login')
    } else if (to.meta.requiresAdmin && !isAdmin) {
        next('/home') // 非管理员重定向到首页
    } else {
        next()
    }
})

export default router