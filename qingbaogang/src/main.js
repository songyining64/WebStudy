import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'

// Element Plus
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'

// 导入全局样式
import '@/assets/global.css'

// 导入请求工具
import axiosInstance from './utils/request'

import App from './App.vue'

// 创建 Vue 应用实例
const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 创建 Pinia 实例并安装
const pinia = createPinia()
app.use(pinia)

// 安装 Element Plus
app.use(ElementPlus, { size: 'default' })

// 安装路由
app.use(router)

// 设置全局 axios 实例
app.config.globalProperties.$axios = axiosInstance

// 全局认证错误处理
window.addEventListener('unauthorized', () => {
    router.push({
        path: '/login',
        query: {
            error: '会话已过期，请重新登录'
        }
    })
})

// 挂载应用
app.mount('#app')





