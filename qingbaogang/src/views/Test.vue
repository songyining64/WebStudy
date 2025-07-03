<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ref, computed } from 'vue'
import { request } from '@/utils/request'

const userStore = useUserStore()
const router = useRouter()

const selectedFile = ref(null)
const uploadedImageUrl = ref('')
const debugInfo = ref({})

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

// 计算修正后的URL
const fixedImageUrl = computed(() => {
  if (!uploadedImageUrl.value) return ''
  
  // 如果URL不是以http开头，添加基础URL
  if (!uploadedImageUrl.value.startsWith('http')) {
    const baseUrl = window.location.origin
    return baseUrl + (uploadedImageUrl.value.startsWith('/') ? '' : '/') + uploadedImageUrl.value
  }
  
  return uploadedImageUrl.value
})

// 处理文件选择
const handleFileUpload = (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  selectedFile.value = file
  debugInfo.value = {
    ...debugInfo.value,
    selectedFile: {
      name: file.name,
      type: file.type,
      size: file.size + ' bytes'
    }
  }
}

// 上传图片
const uploadImage = async () => {
  if (!selectedFile.value) return
  
  try {
    debugInfo.value = {
      ...debugInfo.value,
      uploadStarted: new Date().toISOString()
    }
    
    // 调用上传API
    const response = await request.upload('/api/upload/image', selectedFile.value)
    
    debugInfo.value = {
      ...debugInfo.value,
      uploadResponse: response,
      uploadCompleted: new Date().toISOString()
    }
    
    // 获取上传的图片URL
    uploadedImageUrl.value = response.data?.url || ''
    
    // 添加到调试信息
    debugInfo.value = {
      ...debugInfo.value,
      imageUrl: uploadedImageUrl.value,
      fixedImageUrl: fixedImageUrl.value
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    debugInfo.value = {
      ...debugInfo.value,
      uploadError: error.message || '上传失败'
    }
    alert('图片上传失败: ' + (error.message || '未知错误'))
  }
}

// 复制URL到剪贴板
const copyUrl = () => {
  navigator.clipboard.writeText(uploadedImageUrl.value)
    .then(() => alert('URL已复制到剪贴板'))
    .catch(err => console.error('复制失败:', err))
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

    <div class="upload-section">
      <h2>1. 选择图片上传</h2>
      <input type="file" accept="image/*" @change="handleFileUpload" />
      <button @click="uploadImage" :disabled="!selectedFile">上传图片</button>
    </div>

    <div class="result-section" v-if="uploadedImageUrl">
      <h2>2. 上传结果</h2>
      <div>
        <p>图片URL: <code>{{ uploadedImageUrl }}</code></p>
        <button @click="copyUrl">复制URL</button>
      </div>
    </div>

    <div class="display-section" v-if="uploadedImageUrl">
      <h2>3. 图片显示测试</h2>
      <div>
        <h3>直接使用URL</h3>
        <img :src="uploadedImageUrl" alt="上传的图片" class="test-image" />
      </div>
      
      <div v-if="uploadedImageUrl">
        <h3>修正后的URL (如果需要)</h3>
        <p>修正URL: <code>{{ fixedImageUrl }}</code></p>
        <img :src="fixedImageUrl" alt="修正URL的图片" class="test-image" />
      </div>
    </div>
    
    <div class="debug-section">
      <h2>4. 调试信息</h2>
      <pre>{{ debugInfo }}</pre>
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

.upload-section, .result-section, .display-section, .debug-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  background-color: #fff;
}

button {
  margin: 10px 0;
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.test-image {
  max-width: 100%;
  max-height: 300px;
  border: 1px solid #eee;
  margin-top: 10px;
}

code {
  display: block;
  padding: 8px;
  background-color: #f5f5f5;
  border-radius: 4px;
  word-break: break-all;
  margin: 10px 0;
}

pre {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  white-space: pre-wrap;
  word-break: break-word;
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