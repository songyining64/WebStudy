<template>
  <div class="settings-container">
    <h2>用户设置</h2>
    
    <!-- 头像上传部分 -->
    <div class="avatar-section">
      <el-upload
        class="avatar-uploader"
        :show-file-list="false"
        :before-upload="beforeAvatarUpload"
        :http-request="handleCustomUpload"
        :auto-upload="true"
        accept="image/*"
      >
        <div class="avatar-upload-area">
          <img v-if="userStore.avatar" :src="userStore.avatar" class="avatar-preview" alt="用户头像" @error="handleAvatarError" />
          <div v-else class="upload-placeholder">
            <el-icon class="upload-icon"><Plus /></el-icon>
          </div>
          <div class="upload-text">点击更换头像</div>
        </div>
      </el-upload>
    </div>

    <!-- 用户信息表单 -->
    <el-form :model="userForm" label-width="120px" class="settings-form">
      <el-form-item label="用户名">
        <el-input v-model="userForm.name" />
      </el-form-item>
      <el-form-item label="电子邮箱">
        <el-input v-model="userForm.email" type="email" />
      </el-form-item>
      <el-form-item label="出生日期">
        <el-date-picker v-model="userForm.birthDate" type="date" placeholder="选择日期" />
      </el-form-item>
      <el-form-item label="个人简介">
        <el-input v-model="userForm.bio" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveSettings">保存设置</el-button>
        <el-button @click="重置">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useUserStore } from '@/stores/user';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import defaultAvatarUrl from '@/assets/default-avatar.png';

const userStore = useUserStore();

const userForm = ref({
  name: userStore.name,
  email: userStore.email,
  bio: userStore.bio,
  birthDate: userStore.birthDate,
});

// 头像上传前的验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error('头像必须是图片格式!');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!');
    return false;
  }

  return true;
};

// 自定义上传处理
const handleCustomUpload = async ({ file }) => {
  try {
    const reader = new FileReader();
    reader.onload = async (e) => {
      const base64Data = e.target.result;
      try {
        await userStore.updateUser({ avatar: base64Data });
        ElMessage.success('头像上传成功');
      } catch (error) {
        console.error('Failed to update avatar:', error);
        ElMessage.error('头像上传失败');
      }
    };
    reader.onerror = (error) => {
      console.error('Error reading file:', error);
      ElMessage.error('读取文件失败');
    };
    reader.readAsDataURL(file);
  } catch (error) {
    console.error('Upload failed:', error);
    ElMessage.error('上传失败');
  }
};

// 头像加载错误的处理
const handleAvatarError = () => {
  console.error('Avatar failed to load, falling back to default');
  userStore.updateUser({ avatar: defaultAvatarUrl });
};

// 保存设置
const saveSettings = async () => {
  try {
    await userStore.updateUser({
      name: userForm.value.name,
      email: userForm.value.email,
      bio: userForm.value.bio,
      birthDate: userForm.value.birthDate,
    });
    ElMessage.success('设置保存成功');
  } catch (error) {
    console.error('Failed to save settings:', error);
    ElMessage.error('设置保存失败');
  }
};

// 重置表单
const 重置 = () => {
  userForm.value = {
    name: userStore.name,
    email: userStore.email,
    bio: userStore.bio,
    birthDate: userStore.birthDate,
  };
};

onMounted(() => {
  console.log('Component mounted, current avatar:', userStore.avatar);
});
</script>

<style scoped>
.settings-container {
  max-width: 480px;
  margin: 40px auto;
  padding: 32px 24px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(52,152,219,0.10);
  border: 1.5px solid #e0e6ed;
}
.settings-container h2 {
  text-align: center;
  color: #217dbb;
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 32px;
  border-left: 6px solid #3498db;
  padding-left: 12px;
  background: linear-gradient(90deg, #eaf2fb 60%, transparent);
  border-radius: 6px;
}
.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}
.avatar-uploader {
  text-align: center;
}
.avatar-upload-area {
  width: 128px;
  height: 128px;
  border: 2px dashed #d0e6fa;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  background: #f4f8fb;
  box-shadow: 0 2px 8px rgba(52,152,219,0.06);
}
.avatar-upload-area:hover {
  border-color: #3498db;
}
.avatar-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.upload-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}
.upload-icon {
  font-size: 32px;
  color: #8c939d;
}
.upload-text {
  position: absolute;
  bottom: 12px;
  left: 0;
  right: 0;
  text-align: center;
  color: #8c939d;
  font-size: 13px;
}
.settings-form {
  margin-top: 20px;
  background: #f8fafc;
  border-radius: 16px;
  padding: 24px 18px 12px 18px;
  box-shadow: 0 2px 8px rgba(52,152,219,0.04);
  border: 1.5px solid #e0e6ed;
}
.el-form-item {
  margin-bottom: 18px;
}
.el-input, .el-textarea__inner, .el-date-editor.el-input {
  border-radius: 999px !important;
  background: #fff !important;
  border: 1.5px solid #e0e6ed !important;
  font-size: 15px;
  padding: 8px 16px;
  transition: border 0.2s, box-shadow 0.2s;
}
.el-input:focus, .el-textarea__inner:focus, .el-date-editor.el-input:focus {
  border: 1.5px solid #3498db !important;
  box-shadow: 0 2px 8px rgba(52,152,219,0.08);
}
.el-button--primary, .el-button[type="primary"] {
  background: linear-gradient(90deg, #3498db 60%, #6dd5fa 100%);
  color: #fff;
  border-radius: 999px;
  border: none;
  font-weight: bold;
  padding: 8px 32px;
  font-size: 15px;
  box-shadow: 0 2px 8px rgba(52,152,219,0.08);
  transition: background 0.2s, box-shadow 0.2s;
}
.el-button--primary:hover, .el-button[type="primary"]:hover {
  background: #217dbb;
  color: #fff;
}
.el-button {
  border-radius: 999px;
  font-size: 15px;
  padding: 8px 24px;
}
</style>