<template>
  <nav class="navbar">
    <div class="navbar-left">
      <div class="logo-box">
        <img src="@/assets/logo.jpg" alt="logo" class="logo" @click="$router.push('/')" />
        <span class="site-name">情波港</span>
      </div>
    </div>
    <div class="navbar-center">
      <el-button link @click="$router.push('/home')">首页</el-button>
      <el-button link @click="$router.push('/chatbot')">聊天机器人</el-button>
      <el-button link @click="$router.push('/resource')">资源推荐</el-button>
      <el-button link @click="$router.push('/community')">社区</el-button>
      <!-- 管理员专用链接 -->
      <el-button v-if="isAdmin" link @click="$router.push('/manage')">管理控制台</el-button>
    </div>
    <div class="navbar-right">
      <el-dropdown v-if="isLoggedIn" trigger="click" :hide-on-click="false">
        <span class="el-dropdown-link">
          <el-avatar :size="32" :src="userAvatar" />
          <span class="username">{{ userName }}</span>
          <el-icon class="el-icon--right">
            <arrow-down />
          </el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <!-- 用户信息预览 -->
            <div class="user-preview">
              <el-avatar :size="50" :src="userAvatar" class="preview-avatar" />
              <div class="user-details">
                <strong>{{ userName }}</strong>
                <div class="user-email">{{ userStore.email }}</div>
                <div class="user-bio">{{ userStore.bio || '这个用户很懒，还没有填写简介' }}</div>
                <div class="user-role">
                  <el-tag :type="userStore.role === 'admin' ? 'danger' : 'success'" size="small">
                    {{ userStore.role === 'admin' ? '管理员' : '普通用户' }}
                  </el-tag>
                </div>
              </div>
            </div>
            <el-dropdown-item @click="handleNavigation('/profile')">
              <el-icon><user /></el-icon> 个人主页
            </el-dropdown-item>
            <el-dropdown-item @click="handleNavigation('/history')">
              <el-icon><timer /></el-icon> 历史记录
            </el-dropdown-item>
            <el-dropdown-item @click="handleNavigation('/settings')">
              <el-icon><setting /></el-icon> 用户设置
            </el-dropdown-item>
            <el-dropdown-item v-if="isAdmin" divided @click="handleNavigation('/manage')">
              <el-icon><management /></el-icon> 管理控制台
            </el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">
              <el-icon><switch-button /></el-icon> 退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <template v-else>
        <el-button @click="handleNavigation('/login')">登录</el-button>
        <el-button type="primary" @click="handleNavigation('/register')">注册</el-button>
      </template>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { 
  ArrowDown,
  Timer,
  Setting,
  SwitchButton,
  Management,
  User
} from '@element-plus/icons-vue'
import defaultAvatarUrl from '@/assets/default-avatar.png'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.name !== '用户名' && userStore.email !== '')
const userName = computed(() => userStore.name)
const userAvatar = computed(() => userStore.avatar || defaultAvatarUrl)
const isAdmin = computed(() => userStore.isAdmin)

const handleNavigation = (path) => {
  router.push(path)
}

const handleLogout = async () => {
  userStore.clearUserData()
  localStorage.removeItem('authToken')
  await router.push('/')
}
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 64px;
  background: #eaf4fb; /* 柔和浅蓝色 */
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  position: relative;
  z-index: 100;
}

.navbar-left {
  display: flex;
  align-items: center;
}

.logo-box {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.logo {
  height: 40px;
  margin-right: 12px;
}

.site-name {
  font-size: 1.4rem;
  font-weight: bold;
  color: #2c3e50;
}

.navbar-center {
  flex: 1;
  display: flex;
  justify-content: center;
  gap: 24px;
  align-items: center;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.el-dropdown-link:hover {
  background-color: #f5f7fa;
}

.username {
  margin: 0 8px;
  font-size: 14px;
  color: #606266;
}

.user-preview {
  padding: 16px;
  border-bottom: 1px solid #eee;
  background: #f9f9f9;
  min-width: 240px;
}

.user-details {
  margin-top: 12px;
}

.user-details strong {
  font-size: 16px;
  display: block;
  margin-bottom: 4px;
  color: #303133;
}

.user-email {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.user-bio {
  font-size: 13px;
  color: #606266;
  margin-bottom: 12px;
  line-height: 1.4;
}

.user-role {
  margin-top: 8px;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
}

:deep(.el-dropdown-menu__item i) {
  margin-right: 4px;
  font-size: 16px;
}
</style>