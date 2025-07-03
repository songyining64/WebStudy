import { fileURLToPath, URL } from 'node:url'
import path from 'path'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src')
        }
    },
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080/mental', // 注意这里包含 /mental
                changeOrigin: true,
                rewrite: path => path.replace(/^\/api/, '/api')
            },
            '/upload': {
                target: 'http://localhost:8080/mental', // 代理上传文件的请求
                changeOrigin: true
            },
            '/mental/upload': {
                target: 'http://localhost:8080', // 代理带应用上下文的静态资源请求
                changeOrigin: true
            },
            '/mental/static/upload': {
                target: 'http://localhost:8080', // 代理带应用上下文的静态资源请求
                changeOrigin: true
            }
        },
        cors: true
    }
})
