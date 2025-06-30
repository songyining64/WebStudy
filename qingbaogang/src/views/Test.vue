<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const testAdminAccess = () => {
  if (userStore.isAdmin) {
    alert('当前用户是管理员，可以访问管理页面')
    router.push('/manage')
  } else {
    alert('当前用户不是管理员，无法访问管理页面')
  }
}

const setAsAdmin = () => {
  userStore.updateUser({
    role: 'admin',
    isAdmin: true
  })
  alert('已设置为管理员')
}

const setAsUser = () => {
  userStore.updateUser({
    role: 'user',
    isAdmin: false
  })
  alert('已设置为普通用户')
}

const clearUserData = () => {
  userStore.clearUserData()
  alert('用户数据已清除')
}

const getLocalStorage = (key) => {
  return localStorage.getItem(key) || '无'
}
</script>

<template>
  <div class="test-container">
    <h1>管理员功能测试页面</h1>
    
    <div class="test-section">
      <h2>当前用户信息</h2>
      <div class="user-info">
        <p><strong>用户名:</strong> {{ userStore.name }}</p>
        <p><strong>邮箱:</strong> {{ userStore.email }}</p>
        <p><strong>角色:</strong> {{ userStore.role }}</p>
        <p><strong>是否管理员:</strong> {{ userStore.isAdmin ? '是' : '否' }}</p>
      </div>
    </div>

    <div class="test-section">
      <h2>功能测试</h2>
      <div class="test-buttons">
        <button @click="testAdminAccess" class="test-btn">
          测试管理员页面访问
        </button>
        <button @click="setAsAdmin" class="test-btn admin">
          设置为管理员
        </button>
        <button @click="setAsUser" class="test-btn user">
          设置为普通用户
        </button>
        <button @click="clearUserData" class="test-btn clear">
          清除用户数据
        </button>
      </div>
    </div>

    <div class="test-section">
      <h2>路由测试</h2>
      <div class="route-buttons">
        <button @click="$router.push('/home')" class="route-btn">首页</button>
        <button @click="$router.push('/manage')" class="route-btn admin">管理页面</button>
        <button @click="$router.push('/user-settings')" class="route-btn">用户设置</button>
        <button @click="$router.push('/login')" class="route-btn">登录页面</button>
      </div>
    </div>

    <div class="test-section">
      <h2>本地存储信息</h2>
      <div class="storage-info">
        <p><strong>authToken:</strong> {{ getLocalStorage('authToken') }}</p>
        <p><strong>userName:</strong> {{ getLocalStorage('userName') }}</p>
        <p><strong>userEmail:</strong> {{ getLocalStorage('userEmail') }}</p>
        <p><strong>userRole:</strong> {{ getLocalStorage('userRole') }}</p>
        <p><strong>isAdmin:</strong> {{ getLocalStorage('isAdmin') }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.test-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.test-container h1 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
}

.test-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.test-section h2 {
  color: #333;
  margin-bottom: 15px;
  border-bottom: 2px solid #3498db;
  padding-bottom: 10px;
}

.user-info p {
  margin: 8px 0;
  font-size: 16px;
}

.test-buttons, .route-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.test-btn, .route-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.test-btn {
  background: #3498db;
  color: white;
}

.test-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.test-btn.admin {
  background: #ff6b6b;
}

.test-btn.user {
  background: #51cf66;
}

.test-btn.clear {
  background: #6c757d;
}

.route-btn {
  background: #95a5a6;
  color: white;
}

.route-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(149, 165, 166, 0.3);
}

.route-btn.admin {
  background: #e74c3c;
}

.storage-info p {
  margin: 5px 0;
  font-family: monospace;
  background: #f8f9fa;
  padding: 5px 10px;
  border-radius: 4px;
  border-left: 3px solid #3498db;
}

@media (max-width: 600px) {
  .test-buttons, .route-buttons {
    flex-direction: column;
  }
  
  .test-btn, .route-btn {
    width: 100%;
  }
}
</style>