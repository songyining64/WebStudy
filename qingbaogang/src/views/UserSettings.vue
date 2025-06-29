<template>
  <div class="card">
    <h2 class="card-title">
      <span class="icon"></span> 用户设置
    </h2>

    <!-- 用户信息表单 -->
    <div class="user-profile">
      <!-- 头像区域 -->
      <div class="avatar-section">
        <div class="avatar-preview">
          <img :src="formUser.avatar || defaultAvatar" alt="用户头像" class="avatar-image">
        </div>

        <!-- 专门的头像上传按钮容器 -->
        <div class="avatar-button-container">
          <label for="avatar-upload" class="avatar-upload-btn">
            <span class="upload-icon"></span> 更换头像
          </label>
          <input
              type="file"
              id="avatar-upload"
              accept="image/*"
              @change="handleAvatarUpload"
              class="avatar-input"
          >
        </div>
      </div>

      <!-- 用户信息表单 -->
      <div class="info-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input
              type="text"
              v-model="formUser.name"
              class="form-input"
              placeholder="输入您的用户名"
          >
        </div>

        <div class="form-group">
          <label class="form-label">电子邮箱</label>
          <input
              type="email"
              v-model="formUser.email"
              class="form-input"
              placeholder="输入您的邮箱"
          >
        </div>

        <div class="form-group">
          <label class="form-label">个人简介</label>
          <textarea
              v-model="formUser.bio"
              class="form-textarea"
              placeholder="介绍一下自己..."
              rows="3"
          ></textarea>
        </div>

        <!-- 居中显示的保存按钮 -->
        <div class="button-container">
          <button class="save-btn" @click="saveSettings">保存设置</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();

// 默认头像（Base64占位图）
const defaultAvatar = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%23cccccc'%3E%3Cpath d='M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z'/%3E%3C/svg%3E";

// 表单数据 - 从store初始化
const formUser = reactive({
  name: userStore.name,
  email: userStore.email,
  bio: userStore.bio,
  avatar: userStore.avatar
});

// 处理头像上传
const handleAvatarUpload = (event) => {
  const file = event.target.files[0];
  if (file && file.type.match('image.*')) {
    const reader = new FileReader();
    reader.onload = (e) => {
      formUser.avatar = e.target.result;
    };
    reader.readAsDataURL(file);
  }
};

// 保存设置
const saveSettings = () => {
  // 更新store中的所有用户信息
  userStore.updateUser({
    name: formUser.name,
    email: formUser.email,
    bio: formUser.bio,
    avatar: formUser.avatar
  });

  alert('用户信息已成功保存！');
};
</script>


<style scoped>
.card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 6px 32px 0 rgba(52,152,219,0.10), 0 1.5px 6px 0 rgba(44,62,80,0.06);
  padding: 48px 56px;
  min-width: 340px;
  max-width: 420px;
  margin: 48px auto 0 auto;
}
.card-title {
  font-size: 1.7rem;
  color: #2980b9;
  font-weight: 700;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
}
.icon {
  font-size: 1.5em;
  margin-right: 12px;
}
</style>
<style scoped>
.card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 6px 32px 0 rgba(52,152,219,0.10), 0 1.5px 6px 0 rgba(44,62,80,0.06);
  padding: 32px 40px;
  min-width: 340px;
  max-width: 520px;
  margin: 48px auto;
}

.card-title {
  font-size: 1.7rem;
  color: #2980b9;
  font-weight: 700;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
}

.icon {
  font-size: 1.5em;
  margin-right: 12px;
}

.user-profile {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.avatar-preview {
  position: relative;
  text-align: center;
}

.avatar-image {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #eaf6ff;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.15);
}

.avatar-upload-btn {
  display: inline-block;
  margin-top: 12px;
  padding: 8px 16px;
  background: #3498db;
  color: white;
  border-radius: 20px;
  cursor: pointer;
  transition: background 0.3s;
  font-size: 0.9rem;
}

.avatar-upload-btn:hover {
  background: #2980b9;
}

.upload-icon {
  margin-right: 6px;
}

.avatar-input {
  display: none;
}

.info-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
  font-size: 0.95rem;
}

.form-input, .form-textarea {
  padding: 12px 16px;
  border: 1px solid #e0e7ff;
  border-radius: 10px;
  font-size: 1rem;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.form-input:focus, .form-textarea:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.2);
  outline: none;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

/* 新增的居中按钮容器 */
.button-container {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

.save-btn {
  background: linear-gradient(to right, #3498db, #2980b9);
  color: white;
  border: none;
  padding: 12px 32px;
  border-radius: 30px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  min-width: 160px;
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}
</style>