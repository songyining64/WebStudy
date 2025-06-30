<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 响应式数据
const users = ref([])
const searchQuery = ref('')
const showEditModal = ref(false)
const editingUser = ref({})
const settings = ref({
  allowRegistration: true,
  requireEmailVerification: false,
  autoModerateContent: true,
  allowFileUpload: true,
  emailNotifications: true,
  pushNotifications: false
})

// 计算属性
const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value
  return users.value.filter(user => 
    user.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    user.email.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

const adminUsers = computed(() => {
  return users.value.filter(user => user.role === 'admin')
})

const activeUsers = computed(() => {
  return users.value.filter(user => user.status === 'active')
})

const newUsersThisMonth = computed(() => {
  const now = new Date()
  const thisMonth = new Date(now.getFullYear(), now.getMonth(), 1)
  return users.value.filter(user => new Date(user.createdAt) >= thisMonth)
})

// 方法
const loadUsers = () => {
  // 模拟从API加载用户数据
  users.value = [
    {
      id: 1,
      name: '管理员',
      email: 'admin@example.com',
      avatar: '/src/assets/default-avatar.png',
      role: 'admin',
      status: 'active',
      createdAt: '2024-01-01'
    },
    {
      id: 2,
      name: '张三',
      email: 'zhangsan@example.com',
      avatar: '/src/assets/default-avatar.png',
      role: 'user',
      status: 'active',
      createdAt: '2024-01-15'
    },
    {
      id: 3,
      name: '李四',
      email: 'lisi@example.com',
      avatar: '/src/assets/default-avatar.png',
      role: 'user',
      status: 'inactive',
      createdAt: '2024-01-20'
    },
    {
      id: 4,
      name: '王五',
      email: 'wangwu@example.com',
      avatar: '/src/assets/default-avatar.png',
      role: 'admin',
      status: 'active',
      createdAt: '2024-02-01'
    }
  ]
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const editUser = (user) => {
  editingUser.value = { ...user }
  showEditModal.value = true
}

const closeEditModal = () => {
  showEditModal.value = false
  editingUser.value = {}
}

const saveUserEdit = () => {
  const index = users.value.findIndex(u => u.id === editingUser.value.id)
  if (index !== -1) {
    users.value[index] = { ...editingUser.value }
  }
  closeEditModal()
}

const toggleUserRole = (user) => {
  const newRole = user.role === 'admin' ? 'user' : 'admin'
  const index = users.value.findIndex(u => u.id === user.id)
  if (index !== -1) {
    users.value[index].role = newRole
  }
}

const deleteUser = (user) => {
  if (confirm(`确定要删除用户 "${user.name}" 吗？`)) {
    const index = users.value.findIndex(u => u.id === user.id)
    if (index !== -1) {
      users.value.splice(index, 1)
    }
  }
}

const saveSettings = () => {
  // 保存设置到localStorage或API
  localStorage.setItem('adminSettings', JSON.stringify(settings.value))
  alert('设置已保存')
}

const loadSettings = () => {
  const savedSettings = localStorage.getItem('adminSettings')
  if (savedSettings) {
    settings.value = { ...settings.value, ...JSON.parse(savedSettings) }
  }
}

// 生命周期
onMounted(() => {
  loadUsers()
  loadSettings()
})
</script>

<template>
  <div class="admin-container">
    <div class="admin-header">
      <h1>管理员控制台</h1>
      <div class="admin-info">
        <span>欢迎，{{ userStore.name }}</span>
        <span class="role-badge">管理员</span>
      </div>
    </div>

    <div class="admin-content">
      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">👥</div>
          <div class="stat-info">
            <h3>{{ users.length }}</h3>
            <p>总用户数</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">👑</div>
          <div class="stat-info">
            <h3>{{ adminUsers.length }}</h3>
            <p>管理员</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📊</div>
          <div class="stat-info">
            <h3>{{ activeUsers.length }}</h3>
            <p>活跃用户</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🆕</div>
          <div class="stat-info">
            <h3>{{ newUsersThisMonth.length }}</h3>
            <p>本月新增</p>
          </div>
        </div>
      </div>

      <!-- 用户管理表格 -->
      <div class="user-management">
        <div class="section-header">
          <h2>用户管理</h2>
          <div class="search-box">
            <input 
              v-model="searchQuery" 
              type="text" 
              placeholder="搜索用户..."
              class="search-input"
            />
            <button class="search-btn">🔍</button>
          </div>
        </div>

        <div class="table-container">
          <table class="user-table">
            <thead>
              <tr>
                <th>头像</th>
                <th>用户名</th>
                <th>邮箱</th>
                <th>角色</th>
                <th>状态</th>
                <th>注册时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in filteredUsers" :key="user.id" class="user-row">
                <td>
                  <img :src="user.avatar" :alt="user.name" class="user-avatar" />
                </td>
                <td>{{ user.name }}</td>
                <td>{{ user.email }}</td>
                <td>
                  <span :class="['role-tag', user.role]">
                    {{ user.role === 'admin' ? '管理员' : '普通用户' }}
                  </span>
                </td>
                <td>
                  <span :class="['status-tag', user.status]">
                    {{ user.status === 'active' ? '活跃' : '非活跃' }}
                  </span>
                </td>
                <td>{{ formatDate(user.createdAt) }}</td>
                <td class="actions">
                  <button 
                    @click="editUser(user)" 
                    class="action-btn edit"
                    title="编辑用户"
                  >
                    ✏️
                  </button>
                  <button 
                    @click="toggleUserRole(user)" 
                    :class="['action-btn', user.role === 'admin' ? 'demote' : 'promote']"
                    :title="user.role === 'admin' ? '取消管理员' : '设为管理员'"
                  >
                    {{ user.role === 'admin' ? '👤' : '👑' }}
                  </button>
                  <button 
                    @click="deleteUser(user)" 
                    class="action-btn delete"
                    title="删除用户"
                  >
                    🗑️
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 系统设置 -->
      <div class="system-settings">
        <h2>系统设置</h2>
        <div class="settings-grid">
          <div class="setting-card">
            <h3>用户注册设置</h3>
            <div class="setting-item">
              <label>
                <input type="checkbox" v-model="settings.allowRegistration" />
                允许新用户注册
              </label>
            </div>
            <div class="setting-item">
              <label>
                <input type="checkbox" v-model="settings.requireEmailVerification" />
                需要邮箱验证
              </label>
            </div>
          </div>

          <div class="setting-card">
            <h3>内容管理</h3>
            <div class="setting-item">
              <label>
                <input type="checkbox" v-model="settings.autoModerateContent" />
                自动内容审核
              </label>
            </div>
            <div class="setting-item">
              <label>
                <input type="checkbox" v-model="settings.allowFileUpload" />
                允许文件上传
              </label>
            </div>
          </div>

          <div class="setting-card">
            <h3>通知设置</h3>
            <div class="setting-item">
              <label>
                <input type="checkbox" v-model="settings.emailNotifications" />
                邮件通知
              </label>
            </div>
            <div class="setting-item">
              <label>
                <input type="checkbox" v-model="settings.pushNotifications" />
                推送通知
              </label>
            </div>
          </div>
        </div>
        <button @click="saveSettings" class="save-btn">保存设置</button>
      </div>
    </div>

    <!-- 编辑用户模态框 -->
    <div v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
      <div class="modal-content" @click.stop>
        <h3>编辑用户</h3>
        <form @submit.prevent="saveUserEdit">
          <div class="form-group">
            <label>用户名</label>
            <input v-model="editingUser.name" type="text" required />
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input v-model="editingUser.email" type="email" required />
          </div>
          <div class="form-group">
            <label>角色</label>
            <select v-model="editingUser.role">
              <option value="user">普通用户</option>
              <option value="admin">管理员</option>
            </select>
          </div>
          <div class="form-group">
            <label>状态</label>
            <select v-model="editingUser.status">
              <option value="active">活跃</option>
              <option value="inactive">非活跃</option>
            </select>
          </div>
          <div class="modal-actions">
            <button type="button" @click="closeEditModal" class="cancel-btn">取消</button>
            <button type="submit" class="save-btn">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.admin-header {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  padding: 20px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.admin-header h1 {
  margin: 0;
  color: #333;
  font-size: 2rem;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.role-badge {
  background: linear-gradient(45deg, #ff6b6b, #ee5a24);
  color: white;
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: bold;
}

.admin-content {
  max-width: 1200px;
  margin: 0 auto;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  padding: 25px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  font-size: 2.5rem;
  background: linear-gradient(45deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-info h3 {
  margin: 0;
  font-size: 2rem;
  color: #333;
}

.stat-info p {
  margin: 5px 0 0 0;
  color: #666;
}

.user-management {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  color: #333;
}

.search-box {
  display: flex;
  gap: 10px;
}

.search-input {
  padding: 10px 15px;
  border: 2px solid #e1e5e9;
  border-radius: 25px;
  outline: none;
  width: 250px;
  transition: border-color 0.3s ease;
}

.search-input:focus {
  border-color: #667eea;
}

.search-btn {
  background: #667eea;
  color: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.search-btn:hover {
  background: #5a6fd8;
}

.table-container {
  overflow-x: auto;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.user-table th,
.user-table td {
  padding: 15px;
  text-align: left;
  border-bottom: 1px solid #e1e5e9;
}

.user-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.user-row:hover {
  background: #f8f9fa;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.role-tag {
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 0.8rem;
  font-weight: 500;
}

.role-tag.admin {
  background: #ff6b6b;
  color: white;
}

.role-tag.user {
  background: #51cf66;
  color: white;
}

.status-tag {
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 0.8rem;
  font-weight: 500;
}

.status-tag.active {
  background: #51cf66;
  color: white;
}

.status-tag.inactive {
  background: #868e96;
  color: white;
}

.actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 5px;
  border-radius: 5px;
  transition: background 0.3s ease;
}

.action-btn:hover {
  background: #e9ecef;
}

.action-btn.edit:hover {
  background: #fff3cd;
}

.action-btn.promote:hover {
  background: #d1ecf1;
}

.action-btn.demote:hover {
  background: #f8d7da;
}

.action-btn.delete:hover {
  background: #f8d7da;
}

.system-settings {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.system-settings h2 {
  margin: 0 0 20px 0;
  color: #333;
}

.settings-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.setting-card {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 20px;
}

.setting-card h3 {
  margin: 0 0 15px 0;
  color: #333;
}

.setting-item {
  margin-bottom: 15px;
}

.setting-item label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  color: #555;
}

.setting-item input[type="checkbox"] {
  width: 18px;
  height: 18px;
  accent-color: #667eea;
}

.save-btn {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: transform 0.3s ease;
}

.save-btn:hover {
  transform: translateY(-2px);
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 15px;
  padding: 30px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-content h3 {
  margin: 0 0 20px 0;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #555;
  font-weight: 500;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  outline: none;
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group select:focus {
  border-color: #667eea;
}

.modal-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 20px;
}

.cancel-btn {
  background: #6c757d;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.cancel-btn:hover {
  background: #5a6268;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .section-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .search-box {
    width: 100%;
  }
  
  .search-input {
    width: 100%;
  }
  
  .settings-grid {
    grid-template-columns: 1fr;
  }
  
  .user-table {
    font-size: 0.9rem;
  }
  
  .user-table th,
  .user-table td {
    padding: 10px 5px;
  }
}
</style>