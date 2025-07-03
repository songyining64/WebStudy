<template>
  <div class="admin-panel-container">
    <el-card class="admin-header">
      <div class="admin-title">
        <h1>系统管理控制台</h1>
        <div class="admin-subtitle">情波港心理健康系统</div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="12" :md="6" :lg="6" v-for="(item, index) in statsCards" :key="index">
          <el-card shadow="hover" class="stats-card" :style="{ background: item.bgColor }">
            <div class="stats-icon">
              <el-icon :size="36"><component :is="item.icon"></component></el-icon>
            </div>
            <div class="stats-info">
              <h2 class="stats-number">{{ item.value || 0 }}</h2>
              <div class="stats-label">{{ item.label }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 主要内容区 -->
    <el-card class="admin-content">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="用户管理" name="users">
          <div class="table-operations">
            <el-input
              v-model="userKeyword"
              placeholder="搜索用户名/邮箱"
              prefix-icon="Search"
              @keyup.enter="searchUsers"
              clearable
              class="search-input"
            ></el-input>
            <el-button type="primary" @click="searchUsers">搜索</el-button>
            <el-button type="success" @click="handleRefresh('users')">刷新</el-button>
          </div>

          <!-- 用户表格 -->
          <el-table
            v-loading="usersLoading"
            :data="userList"
            style="width: 100%"
            stripe
            border
            highlight-current-row
          >
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column label="用户信息" min-width="250">
              <template #default="scope">
                <div class="user-info">
                  <el-avatar :src="scope.row.avatar || defaultAvatar" :size="40"></el-avatar>
                  <div class="user-detail">
                    <div class="user-name">{{ scope.row.username }}</div>
                    <div class="user-email">{{ scope.row.email }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="role" label="角色" width="120">
              <template #default="scope">
                <el-tag :type="scope.row.role === 'admin' ? 'danger' : 'success'">
                  {{ scope.row.role === 'admin' ? '管理员' : '普通用户' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="创建时间" width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="230" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="viewUserDetail(scope.row)">查看</el-button>
                <el-dropdown trigger="click" @command="(cmd) => handleUserCommand(cmd, scope.row)">
                  <el-button size="small" type="primary">
                    管理
                    <el-icon class="el-icon--right"><arrow-down /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="role">
                        {{ scope.row.role === 'admin' ? '取消管理员' : '设为管理员' }}
                      </el-dropdown-item>
                      <el-dropdown-item command="delete" divided>删除账号</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页器 -->
          <div class="pagination-container">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :current-page="userPage"
              :page-size="userPageSize"
              :total="userTotal"
              @current-change="userPageChange"
            ></el-pagination>
          </div>
        </el-tab-pane>

        <el-tab-pane label="帖子管理" name="posts">
          <div class="table-operations">
            <el-input
              v-model="postKeyword"
              placeholder="搜索帖子标题/内容"
              prefix-icon="Search"
              @keyup.enter="searchPosts"
              clearable
              class="search-input"
            ></el-input>
            <el-button type="primary" @click="searchPosts">搜索</el-button>
            <el-button type="success" @click="handleRefresh('posts')">刷新</el-button>
          </div>

          <!-- 帖子表格 -->
          <el-table
            v-loading="postsLoading"
            :data="postList"
            style="width: 100%"
            stripe
            border
            highlight-current-row
          >
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column label="标题" min-width="180">
              <template #default="scope">
                <div class="post-title">{{ scope.row.title }}</div>
              </template>
            </el-table-column>
            <el-table-column label="作者" width="150">
              <template #default="scope">
                <div class="post-author">
                  <el-avatar :src="scope.row.avatar || defaultAvatar" :size="32"></el-avatar>
                  <span>{{ scope.row.username || '未知用户' }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="category" label="分类" width="120">
              <template #default="scope">
                <el-tag>{{ scope.row.category || '未分类' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="统计" width="120">
              <template #default="scope">
                <div class="post-stats">
                  <span><el-icon><star /></el-icon> {{ scope.row.likeCount || 0 }}</span>
                  <span><el-icon><chat-round /></el-icon> {{ scope.row.commentCount || 0 }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="发布时间" width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="viewPostDetail(scope.row)">查看</el-button>
                <el-button size="small" type="danger" @click="deletePost(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页器 -->
          <div class="pagination-container">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :current-page="postPage"
              :page-size="postPageSize"
              :total="postTotal"
              @current-change="postPageChange"
            ></el-pagination>
          </div>
        </el-tab-pane>

        <el-tab-pane label="情绪记录" name="emotions">
          <el-empty description="情绪记录管理功能开发中..."></el-empty>
        </el-tab-pane>

        <el-tab-pane label="评估问卷" name="assessments">
          <el-empty description="评估问卷管理功能开发中..."></el-empty>
        </el-tab-pane>

        <el-tab-pane label="系统设置" name="settings">
          <el-empty description="系统设置功能开发中..."></el-empty>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 用户详情对话框 -->
    <el-dialog v-model="userDetailVisible" title="用户详情" width="600px">
      <div v-loading="userDetailLoading">
        <div v-if="selectedUser" class="user-detail-card">
          <div class="user-detail-header">
            <el-avatar :src="selectedUser.avatar || defaultAvatar" :size="80"></el-avatar>
            <div class="user-detail-info">
              <h2>{{ selectedUser.username }}</h2>
              <p><el-icon><message /></el-icon> {{ selectedUser.email }}</p>
              <p><el-icon><timer /></el-icon> 注册于：{{ formatDateTime(selectedUser.createTime) }}</p>
              <el-tag :type="selectedUser.role === 'admin' ? 'danger' : 'success'">
                {{ selectedUser.role === 'admin' ? '管理员' : '普通用户' }}
              </el-tag>
            </div>
          </div>
    </div>
        <div v-else class="no-data">未找到用户信息</div>
    </div>
    </el-dialog>

    <!-- 确认对话框 -->
    <el-dialog v-model="confirmDialogVisible" :title="confirmDialogTitle" width="400px">
      <div class="confirm-content">{{ confirmDialogMessage }}</div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="confirmDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAction">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  User, UserFilled, Document, ChatDotRound, Histogram, Setting,
  Search, ArrowDown, Star, ChatRound, Message, Timer, Refresh
} from '@element-plus/icons-vue';
import { adminApi } from '@/api/adminApi';
import defaultAvatar from '@/assets/default-avatar.png';

const router = useRouter();
const activeTab = ref('users');

// 统计数据
const stats = ref({
  userCount: 0,
  postCount: 0,
  commentCount: 0,
  emotionRecordCount: 0,
  assessmentCount: 0
});

// 统计卡片配置
const statsCards = computed(() => [
  {
    icon: 'UserFilled',
    label: '用户总数',
    value: stats.value.userCount,
    bgColor: 'linear-gradient(135deg, #6B73FF 10%, #6476FF 100%)'
  },
  {
    icon: 'Document',
    label: '帖子总数',
    value: stats.value.postCount,
    bgColor: 'linear-gradient(135deg, #36D1DC 10%, #5B86E5 100%)'
  },
  {
    icon: 'ChatDotRound',
    label: '评论总数',
    value: stats.value.commentCount,
    bgColor: 'linear-gradient(135deg, #FF9966 10%, #FF5E62 100%)'
  },
  {
    icon: 'Histogram',
    label: '评估数据',
    value: stats.value.assessmentCount + stats.value.emotionRecordCount,
    bgColor: 'linear-gradient(135deg, #43C6AC 10%, #4B83C0 100%)'
  }
]);

// 用户管理相关
const userList = ref([]);
const userPage = ref(1);
const userPageSize = ref(10);
const userTotal = ref(0);
const userKeyword = ref('');
const usersLoading = ref(false);
const selectedUser = ref(null);
const userDetailVisible = ref(false);
const userDetailLoading = ref(false);

// 帖子管理相关
const postList = ref([]);
const postPage = ref(1);
const postPageSize = ref(10);
const postTotal = ref(0);
const postKeyword = ref('');
const postsLoading = ref(false);

// 确认对话框
const confirmDialogVisible = ref(false);
const confirmDialogTitle = ref('');
const confirmDialogMessage = ref('');
const confirmAction = ref(() => {});

// 获取统计数据
const fetchStats = async () => {
  try {
    const res = await adminApi.getStats();
    stats.value = res.data || {
      userCount: 0,
      postCount: 0,
      commentCount: 0,
      emotionRecordCount: 0,
      assessmentCount: 0
    };
    console.log('统计数据:', stats.value);
  } catch (error) {
    console.error('获取统计数据失败', error);
    ElMessage.error('获取统计数据失败');
  }
};

// 获取用户列表
const fetchUsers = async () => {
  usersLoading.value = true;
  try {
    const res = await adminApi.getUsers({
      page: userPage.value,
      size: userPageSize.value,
      keyword: userKeyword.value || undefined
    });
    userList.value = res.data.records || [];
    userTotal.value = res.data.total || 0;
    console.log('用户列表:', userList.value);
  } catch (error) {
    console.error('获取用户列表失败', error);
    ElMessage.error('获取用户列表失败');
  } finally {
    usersLoading.value = false;
  }
};

// 获取帖子列表
const fetchPosts = async () => {
  postsLoading.value = true;
  try {
    const res = await adminApi.getPosts({
      page: postPage.value,
      size: postPageSize.value,
      keyword: postKeyword.value || undefined
    });
    postList.value = res.data.records || [];
    postTotal.value = res.data.total || 0;
    console.log('帖子列表:', postList.value);
  } catch (error) {
    console.error('获取帖子列表失败', error);
    ElMessage.error('获取帖子列表失败');
  } finally {
    postsLoading.value = false;
  }
};

// 搜索用户
const searchUsers = () => {
  userPage.value = 1;
  fetchUsers();
};

// 搜索帖子
const searchPosts = () => {
  postPage.value = 1;
  fetchPosts();
};

// 用户分页变化
const userPageChange = (page) => {
  userPage.value = page;
  fetchUsers();
};

// 帖子分页变化
const postPageChange = (page) => {
  postPage.value = page;
  fetchPosts();
};

// 查看用户详情
const viewUserDetail = async (user) => {
  selectedUser.value = user;
  userDetailVisible.value = true;
  userDetailLoading.value = true;
  
  try {
    const res = await adminApi.getUserDetail(user.id);
    selectedUser.value = res.data;
  } catch (error) {
    console.error('获取用户详情失败', error);
    ElMessage.error('获取用户详情失败');
  } finally {
    userDetailLoading.value = false;
  }
};

// 查看帖子详情
const viewPostDetail = (post) => {
  router.push(`/community/post/${post.id}`);
};

// 用户操作处理
const handleUserCommand = (command, user) => {
  switch (command) {
    case 'role':
      confirmChangeUserRole(user);
      break;
    case 'delete':
      confirmDeleteUser(user);
      break;
  }
};

// 确认更改用户角色
const confirmChangeUserRole = (user) => {
  const newRole = user.role === 'admin' ? 'user' : 'admin';
  confirmDialogTitle.value = user.role === 'admin' ? '取消管理员权限' : '设置为管理员';
  confirmDialogMessage.value = user.role === 'admin' 
    ? `确定要取消用户 "${user.username}" 的管理员权限吗？` 
    : `确定要将用户 "${user.username}" 设置为管理员吗？`;
  
  confirmAction.value = async () => {
    try {
      await adminApi.changeUserRole(user.id, newRole);
      ElMessage.success(`已${user.role === 'admin' ? '取消' : '设置'}管理员权限`);
      fetchUsers(); // 刷新用户列表
    } catch (error) {
      console.error('更改用户角色失败', error);
      ElMessage.error('操作失败: ' + (error.message || '未知错误'));
    }
    confirmDialogVisible.value = false;
  };
  
  confirmDialogVisible.value = true;
};

// 确认删除用户
const confirmDeleteUser = (user) => {
  confirmDialogTitle.value = '删除用户';
  confirmDialogMessage.value = `确定要删除用户 "${user.username}" 吗？此操作不可恢复！`;
  
  confirmAction.value = async () => {
    try {
      await adminApi.deleteUser(user.id);
      ElMessage.success('用户已删除');
      fetchUsers(); // 刷新用户列表
      fetchStats(); // 刷新统计数据
    } catch (error) {
      console.error('删除用户失败', error);
      ElMessage.error('删除失败: ' + (error.message || '未知错误'));
    }
    confirmDialogVisible.value = false;
  };
  
  confirmDialogVisible.value = true;
};

// 删除帖子
const deletePost = (post) => {
  ElMessageBox.confirm(
    `确定要删除标题为 "${post.title}" 的帖子吗？此操作不可恢复！`,
    '删除帖子',
    {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        console.log('正在删除帖子ID:', post.id);
        const res = await adminApi.deletePost(post.id);
        console.log('删除帖子响应:', res);
        
        // 判断响应是否表示成功（兼容不同格式的响应）
        if (res.success === true || res.code === 200 || res.code === 0) {
        ElMessage.success('帖子已删除');
        fetchPosts(); // 刷新帖子列表
        fetchStats(); // 刷新统计数据
        } else {
          // 尽管显示错误，但仍然刷新列表，以防后端删除成功但响应格式不符合预期
          ElMessage({
            message: '删除操作可能已成功，正在刷新数据...',
            type: 'warning'
          });
          setTimeout(() => {
            fetchPosts(); // 刷新帖子列表
            fetchStats(); // 刷新统计数据
          }, 1000);
          
          // 如果确实有错误信息，再抛出
          if (res.msg || res.message) {
            throw new Error(res.msg || res.message);
          }
        }
      } catch (error) {
        console.error('删除帖子失败', error);
        ElMessage.error('删除失败: ' + (error.message || '未知错误'));
        
        // 尝试刷新列表，确认是否删除成功
        setTimeout(() => {
          fetchPosts();
        }, 1500);
      }
    })
    .catch(() => {
      ElMessage.info('已取消删除');
    });
};

// 刷新数据
const handleRefresh = (tab) => {
  if (tab === 'users') {
    fetchUsers();
  } else if (tab === 'posts') {
    fetchPosts();
  }
  fetchStats(); // 同时刷新统计数据
};

// Tab切换处理
const handleTabChange = (tab) => {
  if (tab === 'users') {
    fetchUsers();
  } else if (tab === 'posts') {
    fetchPosts();
  }
};

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '未知';
  
  try {
    const date = new Date(dateTime);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (e) {
    return dateTime;
  }
};

// 初始化 - 更强的权限验证
onMounted(async () => {
  try {
    // 检查本地存储是否有管理员标识
    const isAdmin = localStorage.getItem('isAdmin');
    const userRole = localStorage.getItem('userRole');
    
    console.log('当前用户角色:', userRole, '是否管理员:', isAdmin);
    
    if (isAdmin !== 'true' || userRole !== 'admin') {
      throw new Error('本地存储中没有管理员权限标识');
    }
    
    // 再次确认服务器上的管理员权限
    await adminApi.verifyAdmin();
    console.log('服务器验证管理员权限成功');
    
    // 获取数据
    fetchStats();
    fetchUsers();
  } catch (error) {
    console.error('验证管理员权限失败', error);
    ElMessage.error('无管理员权限，将返回首页');
    router.push('/home');
  }
});
</script>

<style scoped>
.admin-panel-container {
  max-width: 1400px;
  margin: 20px auto;
  padding: 0 20px;
}

.admin-header {
  margin-bottom: 20px;
}

.admin-title {
  text-align: center;
}

.admin-title h1 {
  margin: 10px 0;
  color: #303133;
}

.admin-subtitle {
  color: #909399;
  font-size: 14px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  display: flex;
  align-items: center;
  height: 100px;
  overflow: hidden;
  color: white;
}

.stats-icon {
  margin-right: 15px;
}

.stats-info {
  flex-grow: 1;
}

.stats-number {
  font-size: 28px;
  font-weight: bold;
  margin: 0 0 5px 0;
}

.stats-label {
  font-size: 14px;
  opacity: 0.8;
}

.admin-content {
  margin-bottom: 20px;
}

.table-operations {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.search-input {
  width: 300px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-detail {
  margin-left: 10px;
}

.user-name {
  font-weight: bold;
}

.user-email {
  font-size: 12px;
  color: #909399;
}

.post-title {
  font-weight: bold;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 8px;
}

.post-stats {
  display: flex;
  gap: 10px;
}

.post-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.user-detail-card {
  padding: 20px;
}

.user-detail-header {
  display: flex;
  gap: 20px;
}

.user-detail-info {
  flex-grow: 1;
}

.user-detail-info h2 {
  margin-top: 0;
  margin-bottom: 10px;
}

.user-detail-info p {
  margin: 8px 0;
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
}

.confirm-content {
  padding: 20px 0;
  color: #606266;
}

.no-data {
  text-align: center;
  padding: 30px;
  color: #909399;
}
</style> 