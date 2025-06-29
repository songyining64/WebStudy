<template>
  <nav class="navbar">
    <div class="navbar-left">
      <div class="logo-box">
        <img src="@/assets/logo.jpg" alt="logo" class="logo" />
        <span class="site-name">情波港</span>
      </div>
    </div>
    <div class="navbar-center">
      <el-button type="text" @click="$router.push('/home')">首页</el-button>
      <el-dropdown>
        <el-button type="text" class="user-settings-btn">
          用户设置 <i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="$router.push('/user-settings')">用户设置</el-dropdown-item>
            <el-dropdown-item @click="$router.push('/history')">历史记录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <el-button type="text" @click="$router.push('/chatbot')">聊天机器人</el-button>
      <el-button type="text" @click="$router.push('/resource')">资源推荐</el-button>
      <el-button type="text" @click="$router.push('/community')">社区</el-button>
      <!-- 管理员专用链接（只保留文字） -->
      <el-button 
        v-if="userStore.isAdmin" 
        type="text" 
        @click="$router.push('/manage')"
      >
        管理控制台
      </el-button>
      <!-- 测试页面链接（仅管理员可见） -->
      <el-button 
        v-if="userStore.isAdmin"
        type="text" 
        @click="$router.push('/test')"
      >
        测试页面
      </el-button>
    </div>
    <div class="navbar-right">
      <el-dropdown>
        <span class="el-dropdown-link">
          <!-- 使用store中的头像和用户名 -->
          <img :src="userStore.avatar" class="avatar" />
          {{ userStore.name }}
          <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <!-- 添加用户信息预览 -->
            <div class="user-preview">
              <img :src="userStore.avatar" class="preview-avatar" />
              <div class="user-details">
                <strong>{{ userStore.name }}</strong>
                <div>{{ userStore.email }}</div>
                <div class="user-bio">{{ userStore.bio }}</div>
                <div class="user-role">
                  <span :class="['role-badge', userStore.role]">
                    {{ userStore.role === 'admin' ? '管理员' : '普通用户' }}
                  </span>
                </div>
              </div>
            </div>

            <el-dropdown-item @click="$router.push('/history')">
              <i class="el-icon-time"></i> 历史记录
            </el-dropdown-item>
            <el-dropdown-item divided @click="$router.push('/user-settings')">
              <i class="el-icon-setting"></i> 用户设置
            </el-dropdown-item>
            <!-- 管理员专用菜单项（只保留文字） -->
            <el-dropdown-item 
              v-if="userStore.isAdmin" 
              divided 
              @click="$router.push('/manage')"
            >
              管理控制台
            </el-dropdown-item>
            <el-dropdown-item 
              v-if="userStore.isAdmin"
              divided 
              @click="$router.push('/test')"
            >
              测试页面
            </el-dropdown-item>
            <el-dropdown-item divided @click="logout">
              <i class="el-icon-switch-button"></i> 退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </nav>
</template>

<script>
import { useUserStore } from '@/stores/user';

export default {
  name: 'NavBar',
  setup() {
    const userStore = useUserStore();
    return { userStore };
  },
  methods: {
    logout() {
      // 清除token
      localStorage.removeItem('authToken');

      // 清除用户数据
      this.userStore.clearUserData();

      // 跳转到登录页
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  height: 70px;
  background: #fff;
  border-bottom: 1px solid #eaeaea;
}

.logo-box {
  display: flex;
  align-items: center;
  margin-right: 30px;
}

.logo {
  height: 40px;
  margin-right: 10px;
  border-radius: 8px;
}

.site-name {
  font-size: 24px;
  font-weight: bold;
  color: #3498db;
}

.navbar-center {
  display: flex;
  flex: 1;
  justify-content: center;
  align-items: center;
}

.navbar-center > * {
  margin: 0 16px;
  font-size: 20px !important;
  font-weight: 600;
}

/* 移除特殊按钮样式，全部按钮只保留文字 */
.admin-btn,
.test-btn {
  background: none !important;
  color: inherit !important;
  border: none !important;
  font-weight: 600 !important;
  box-shadow: none !important;
  border-radius: 0 !important;
  transition: none !important;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  margin-right: 8px;
  vertical-align: middle;
  object-fit: cover;
}

.user-settings-btn {
  font-size: 20px !important;
  font-weight: 600;
}

.el-dropdown-link {
  font-size: 18px;
  font-weight: 600;
}

/* 用户预览样式 */
.user-preview {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid #eee;
  background: #f9f9f9;
}

.preview-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-right: 12px;
  object-fit: cover;
}

.user-details {
  flex: 1;
}

.user-details strong {
  font-size: 1.25rem;
}

.user-details div {
  font-size: 1rem;
}

.user-bio {
  font-size: 0.95rem;
}

.user-role {
  margin-top: 8px;
}

.role-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.role-badge.admin {
  background: linear-gradient(45deg, #ff6b6b, #ee5a24);
  color: white;
}

.role-badge.user {
  background: #51cf66;
  color: white;
}

/* 下拉菜单项样式 */
.el-dropdown-menu__item {
  font-size: 18px;
  font-weight: 500;
}

.el-dropdown-menu__item i {
  margin-right: 8px;
}
</style>