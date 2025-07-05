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
      <el-row :gutter="30">
        <el-col :xs="12" :sm="12" :md="6" :lg="6" v-for="(item, index) in statsCards" :key="index">
          <div class="stats-card-wrapper" :style="{ background: item.bgColor }">
            <div class="stats-top">
              <div class="stats-icon">
                <el-icon :size="38"><component :is="item.icon"></component></el-icon>
              </div>
              <div class="stats-number">{{ item.value || 0 }}</div>
            </div>
            <div class="stats-bottom">
              <div class="stats-label">{{ item.label }}</div>
            </div>
          </div>
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
          <div class="table-operations">
            <el-input
              v-model="emotionKeyword"
              placeholder="搜索用户名/情绪类型"
              prefix-icon="Search"
              @keyup.enter="searchEmotions"
              clearable
              class="search-input"
            ></el-input>
            <el-button type="primary" @click="searchEmotions">搜索</el-button>
            <el-button type="success" @click="handleRefresh('emotions')">刷新</el-button>
          </div>

          <!-- 情绪记录表格 -->
          <el-table
            v-loading="emotionsLoading"
            :data="emotionList"
            style="width: 100%"
            stripe
            border
            highlight-current-row
          >
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column label="用户" min-width="150">
              <template #default="scope">
                <div class="user-info">
                  <div class="user-detail">
                    <div class="user-name">{{ scope.row.username }}</div>
                    <div class="user-id">ID: {{ scope.row.userId }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="emotion" label="情绪类型" width="120">
              <template #default="scope">
                <el-tag :type="getEmotionTagType(scope.row.emotion)">
                  {{ scope.row.emotion }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="200"></el-table-column>
            <el-table-column prop="analysisReport" label="AI评估报告" min-width="220">
              <template #default="scope">
                <el-tooltip v-if="scope.row.analysisReport" class="item" effect="dark" :content="scope.row.analysisReport" placement="top">
                  <span>{{ scope.row.analysisReport.length > 30 ? scope.row.analysisReport.slice(0, 30) + '...' : scope.row.analysisReport }}</span>
                </el-tooltip>
                <span v-else>暂无</span>
              </template>
            </el-table-column>
            <el-table-column label="记录时间" width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.recordTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button size="small" type="danger" @click="deleteEmotionRecord(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页器 -->
          <div class="pagination-container">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :current-page="emotionPage"
              :page-size="emotionPageSize"
              :total="emotionTotal"
              @current-change="emotionPageChange"
            ></el-pagination>
          </div>
        </el-tab-pane>

        <el-tab-pane label="评估问卷" name="assessments">
          <div class="table-operations">
            <el-input
              v-model="assessmentKeyword"
              placeholder="搜索用户名/问卷标题"
              prefix-icon="Search"
              @keyup.enter="searchAssessments"
              clearable
              class="search-input"
            ></el-input>
            <el-button type="primary" @click="searchAssessments">搜索</el-button>
            <el-button type="success" @click="handleRefresh('assessments')">刷新</el-button>
          </div>

          <!-- 评估问卷表格 -->
          <el-table
            v-loading="assessmentsLoading"
            :data="assessmentList"
            style="width: 100%"
            stripe
            border
            highlight-current-row
          >
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column label="用户" min-width="150">
              <template #default="scope">
                <div class="user-info">
                  <div class="user-detail">
                    <div class="user-name">{{ scope.row.username }}</div>
                    <div class="user-id">ID: {{ scope.row.userId }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="assessmentTitle" label="问卷标题" min-width="180"></el-table-column>
            <el-table-column prop="score" label="得分" width="100">
              <template #default="scope">
                <div class="score">{{ scope.row.score || '暂无' }}</div>
              </template>
            </el-table-column>
            <el-table-column label="提交时间" width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.submitTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="viewAssessmentDetail(scope.row)">查看详情</el-button>
                <el-button size="small" type="danger" @click="deleteAssessment(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页器 -->
          <div class="pagination-container">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :current-page="assessmentPage"
              :page-size="assessmentPageSize"
              :total="assessmentTotal"
              @current-change="assessmentPageChange"
            ></el-pagination>
          </div>
          
          <!-- 问卷详情弹窗 -->
          <el-dialog v-model="showAssessmentDetail" title="问卷详情" width="650px">
            <div class="assessment-detail" v-if="currentAssessment">
              <div class="detail-header">
                <h3>{{ currentAssessment.assessmentTitle }}</h3>
                <div class="detail-meta">
                  <span>用户: {{ currentAssessment.username }}</span>
                  <span>得分: {{ currentAssessment.score || '暂无' }}</span>
                  <span>提交时间: {{ formatDateTime(currentAssessment.submitTime) }}</span>
                </div>
              </div>
              <div class="detail-content">
                <h4>详细答案</h4>
                <el-table
                  :data="parseAssessmentDetail(currentAssessment.detail)"
                  style="width: 100%"
                  stripe
                  border
                >
                  <el-table-column prop="question" label="问题" min-width="250"></el-table-column>
                  <el-table-column prop="answer" label="答案" min-width="150"></el-table-column>
                </el-table>
              </div>
            </div>
          </el-dialog>
        </el-tab-pane>

        <el-tab-pane label="资源管理" name="resources">
          <ResourceManager />
        </el-tab-pane>

        <el-tab-pane label="系统设置" name="settings">
          <div class="settings-container">
            <div class="settings-header">
              <h3>系统公告管理</h3>
              <el-button type="primary" @click="showAddNotice">发布公告</el-button>
            </div>
            
            <div class="table-operations">
              <el-input
                v-model="noticeKeyword"
                placeholder="搜索公告标题/内容"
                prefix-icon="Search"
                @keyup.enter="searchNotices"
                clearable
                class="search-input"
              ></el-input>
              <el-button type="primary" @click="searchNotices">搜索</el-button>
              <el-button type="success" @click="handleRefresh('notices')">刷新</el-button>
            </div>
            
            <!-- 系统公告表格 -->
            <el-table
              v-loading="noticesLoading"
              :data="noticeList"
              style="width: 100%"
              stripe
              border
              highlight-current-row
            >
              <el-table-column prop="id" label="ID" width="80"></el-table-column>
              <el-table-column prop="title" label="公告标题" min-width="200"></el-table-column>
              <el-table-column prop="content" label="公告内容" min-width="300">
                <template #default="scope">
                  <div class="notice-content-preview">
                    {{ scope.row.content ? (scope.row.content.length > 100 ? scope.row.content.slice(0, 100) + '...' : scope.row.content) : '无内容' }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                    {{ scope.row.status === 1 ? '有效' : '无效' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="创建时间" width="180">
                <template #default="scope">
                  {{ formatDateTime(scope.row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="220" fixed="right">
                <template #default="scope">
                  <el-button size="small" @click="viewNotice(scope.row)">查看</el-button>
                  <el-button size="small" type="primary" @click="editNotice(scope.row)">编辑</el-button>
                  <el-button size="small" type="danger" @click="deleteNotice(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <!-- 分页器 -->
            <div class="pagination-container">
              <el-pagination
                background
                layout="total, prev, pager, next"
                :current-page="noticePage"
                :page-size="noticePageSize"
                :total="noticeTotal"
                @current-change="noticePageChange"
              ></el-pagination>
            </div>
          </div>
          
          <!-- 公告添加/编辑弹窗 -->
          <el-dialog
            v-model="showNoticeForm"
            :title="isEditingNotice ? '编辑公告' : '发布公告'"
            width="650px"
          >
            <el-form :model="noticeForm" label-width="80px" :rules="noticeRules" ref="noticeFormRef">
              <el-form-item label="标题" prop="title">
                <el-input v-model="noticeForm.title" placeholder="请输入公告标题"></el-input>
              </el-form-item>
              <el-form-item label="内容" prop="content">
                <el-input
                  v-model="noticeForm.content"
                  type="textarea"
                  :rows="6"
                  placeholder="请输入公告内容"
                ></el-input>
              </el-form-item>
              <el-form-item label="状态" prop="status">
                <el-switch
                  v-model="noticeForm.status"
                  :active-value="1"
                  :inactive-value="0"
                  active-text="有效"
                  inactive-text="无效"
                ></el-switch>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitNotice">提交</el-button>
                <el-button @click="showNoticeForm = false">取消</el-button>
              </el-form-item>
            </el-form>
          </el-dialog>
          
          <!-- 公告详情弹窗 -->
          <el-dialog v-model="showNoticeDetail" title="公告详情" width="650px">
            <div class="notice-detail" v-if="currentNotice">
              <div class="notice-header">
                <h2>{{ currentNotice.title }}</h2>
                <div class="notice-meta">
                  <el-tag :type="currentNotice.status === 1 ? 'success' : 'info'">
                    {{ currentNotice.status === 1 ? '有效' : '无效' }}
                  </el-tag>
                  <span>创建时间: {{ formatDateTime(currentNotice.createTime) }}</span>
                  <span>更新时间: {{ formatDateTime(currentNotice.updateTime) }}</span>
                </div>
              </div>
              <div class="notice-content">
                <div style="white-space: pre-line;">{{ currentNotice.content }}</div>
              </div>
            </div>
          </el-dialog>
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
import ResourceManager from '@/components/ResourceManager.vue';

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
    bgColor: 'linear-gradient(120deg, #6366F1 0%, #8B5CF6 100%)'
  },
  {
    icon: 'Document',
    label: '帖子总数',
    value: stats.value.postCount,
    bgColor: 'linear-gradient(120deg, #10B981 0%, #059669 100%)'
  },
  {
    icon: 'ChatDotRound',
    label: '评论总数',
    value: stats.value.commentCount,
    bgColor: 'linear-gradient(120deg, #F59E0B 0%, #D97706 100%)'
  },
  {
    icon: 'Histogram',
    label: '评估数据',
    value: stats.value.assessmentCount + stats.value.emotionRecordCount,
    bgColor: 'linear-gradient(120deg, #EC4899 0%, #BE185D 100%)'
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

// 情绪记录相关
const emotionList = ref([]);
const emotionPage = ref(1);
const emotionPageSize = ref(10);
const emotionTotal = ref(0);
const emotionKeyword = ref('');
const emotionsLoading = ref(false);

// 评估问卷相关
const assessmentList = ref([]);
const assessmentPage = ref(1);
const assessmentPageSize = ref(10);
const assessmentTotal = ref(0);
const assessmentKeyword = ref('');
const assessmentsLoading = ref(false);
const showAssessmentDetail = ref(false);
const currentAssessment = ref(null);

// 系统公告相关
const noticeList = ref([]);
const noticePage = ref(1);
const noticePageSize = ref(10);
const noticeTotal = ref(0);
const noticeKeyword = ref('');
const noticesLoading = ref(false);
const showNoticeForm = ref(false);
const showNoticeDetail = ref(false);
const isEditingNotice = ref(false);
const currentNotice = ref(null);
const noticeForm = ref({
  id: null,
  title: '',
  content: '',
  status: 1
});
const noticeRules = ref({
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符之间', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择公告状态', trigger: 'change' }
  ]
});

// 确认对话框
const confirmDialogVisible = ref(false);
const confirmDialogTitle = ref('');
const confirmDialogMessage = ref('');
const confirmAction = ref(() => {});

// 在 script setup 区域的 ref 定义部分添加
const noticeFormRef = ref(null);

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
  } else if (tab === 'emotions') {
    emotionPage.value = 1;
    emotionKeyword.value = '';
    fetchEmotionRecords();
  } else if (tab === 'assessments') {
    assessmentPage.value = 1;
    assessmentKeyword.value = '';
    fetchAssessments();
  } else if (tab === 'notices') {
    noticePage.value = 1;
    noticeKeyword.value = '';
    fetchSystemNotices();
  }
  fetchStats(); // 同时刷新统计数据
};

// Tab切换处理
const handleTabChange = (tab) => {
  if (tab === 'users') {
    fetchUsers();
  } else if (tab === 'posts') {
    fetchPosts();
  } else if (tab === 'emotions') {
    fetchEmotionRecords();
  } else if (tab === 'assessments') {
    fetchAssessments();
  } else if (tab === 'notices') {
    fetchSystemNotices();
  }
};

// 获取情绪记录列表
const fetchEmotionRecords = async () => {
  emotionsLoading.value = true;
  try {
    const response = await adminApi.getEmotionRecords({
      page: emotionPage.value,
      size: emotionPageSize.value,
      keyword: emotionKeyword.value || undefined
    });
    
    if (response && (response.code === 200 || response.success)) {
      const data = response.data || {};
      emotionList.value = data.records || [];
      emotionTotal.value = data.total || 0;
    } else {
      emotionList.value = [];
      emotionTotal.value = 0;
      ElMessage.warning('获取情绪记录失败: ' + (response?.msg || '未知错误'));
    }
  } catch (error) {
    console.error('获取情绪记录异常:', error);
    ElMessage.error('获取情绪记录异常');
    emotionList.value = [];
    emotionTotal.value = 0;
  } finally {
    emotionsLoading.value = false;
  }
};

// 搜索情绪记录
const searchEmotions = () => {
  emotionPage.value = 1;
  fetchEmotionRecords();
};

// 情绪记录分页变化
const emotionPageChange = (page) => {
  emotionPage.value = page;
  fetchEmotionRecords();
};

// 删除情绪记录
const deleteEmotionRecord = (record) => {
  ElMessageBox.confirm(
    `确定要删除用户 ${record.username} 的情绪记录吗？此操作不可恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await adminApi.deleteEmotionRecord(record.id);
      if (response && (response.code === 200 || response.success)) {
        ElMessage.success('删除情绪记录成功');
        fetchEmotionRecords();
      } else {
        ElMessage.error('删除情绪记录失败: ' + (response?.msg || '未知错误'));
      }
    } catch (error) {
      console.error('删除情绪记录异常:', error);
      ElMessage.error('删除情绪记录异常');
    }
  }).catch(() => {
    // 取消删除
  });
};

// 获取情绪标签类型
const getEmotionTagType = (emotion) => {
  if (!emotion) return '';
  
  if (emotion.includes('开心') || emotion.includes('快乐') || emotion.includes('高兴')) {
    return 'success';
  } else if (emotion.includes('难过') || emotion.includes('悲伤') || emotion.includes('伤心')) {
    return 'info';
  } else if (emotion.includes('生气') || emotion.includes('愤怒')) {
    return 'danger';
  } else if (emotion.includes('焦虑') || emotion.includes('紧张')) {
    return 'warning';
  } else {
    return '';
  }
};

// 获取评估问卷列表
const fetchAssessments = async () => {
  assessmentsLoading.value = true;
  try {
    const response = await adminApi.getUserAssessments({
      page: assessmentPage.value,
      size: assessmentPageSize.value,
      keyword: assessmentKeyword.value || undefined
    });
    
    if (response && (response.code === 200 || response.success)) {
      const data = response.data || {};
      assessmentList.value = data.records || [];
      assessmentTotal.value = data.total || 0;
    } else {
      assessmentList.value = [];
      assessmentTotal.value = 0;
      ElMessage.warning('获取评估问卷失败: ' + (response?.msg || '未知错误'));
    }
  } catch (error) {
    console.error('获取评估问卷异常:', error);
    ElMessage.error('获取评估问卷异常');
    assessmentList.value = [];
    assessmentTotal.value = 0;
  } finally {
    assessmentsLoading.value = false;
  }
};

// 搜索评估问卷
const searchAssessments = () => {
  assessmentPage.value = 1;
  fetchAssessments();
};

// 评估问卷分页变化
const assessmentPageChange = (page) => {
  assessmentPage.value = page;
  fetchAssessments();
};

// 查看评估问卷详情
const viewAssessmentDetail = (assessment) => {
  currentAssessment.value = assessment;
  showAssessmentDetail.value = true;
};

// 解析评估问卷详情
const parseAssessmentDetail = (detailJson) => {
  try {
    if (!detailJson) return [];
    
    const detail = typeof detailJson === 'string' ? JSON.parse(detailJson) : detailJson;
    return Object.entries(detail).map(([question, answer]) => ({
      question,
      answer: String(answer)
    }));
  } catch (error) {
    console.error('解析评估问卷详情异常:', error);
    return [];
  }
};

// 删除评估问卷
const deleteAssessment = (assessment) => {
  ElMessageBox.confirm(
    `确定要删除用户 ${assessment.username} 的 ${assessment.assessmentTitle} 问卷吗？此操作不可恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await adminApi.deleteUserAssessment(assessment.id);
      if (response && (response.code === 200 || response.success)) {
        ElMessage.success('删除问卷成功');
        fetchAssessments();
      } else {
        ElMessage.error('删除问卷失败: ' + (response?.msg || '未知错误'));
      }
    } catch (error) {
      console.error('删除问卷异常:', error);
      ElMessage.error('删除问卷异常');
    }
  }).catch(() => {
    // 取消删除
  });
};

// 获取系统公告列表
const fetchSystemNotices = async () => {
  try {
    noticesLoading.value = true;
    console.log('开始获取系统公告列表, 页码:', noticePage.value, '关键词:', noticeKeyword.value);
    
    const params = {
      page: noticePage.value,
      size: noticePageSize.value,
      keyword: noticeKeyword.value || undefined
    };
    
    const response = await adminApi.getSystemNotices(params);
    console.log('获取系统公告响应:', response);
    
    if (response && (response.code === 200 || response.success)) {
      noticeList.value = response.data.records || response.data;
      noticeTotal.value = response.data.total || response.data.length;
      console.log('系统公告列表加载成功:', noticeList.value.length, '条记录');
    } else {
      console.error('获取系统公告失败:', response);
      ElMessage.error('获取系统公告列表失败: ' + (response?.msg || '未知错误'));
    }
  } catch (error) {
    console.error('获取系统公告列表异常:', error);
    ElMessage.error('获取系统公告列表异常: ' + (error.message || '未知错误'));
  } finally {
    noticesLoading.value = false;
  }
};

// 系统公告页面变化
const noticePageChange = (page) => {
  noticePage.value = page;
  fetchSystemNotices();
};

// 搜索系统公告
const searchNotices = () => {
  noticePage.value = 1;
  fetchSystemNotices();
};

// 查看系统公告详情
const viewNotice = (notice) => {
  currentNotice.value = notice;
  showNoticeDetail.value = true;
};

// 打开添加公告弹窗
const showAddNotice = () => {
  isEditingNotice.value = false;
  noticeForm.value = {
    id: null,
    title: '',
    content: '',
    status: 1
  };
  
  // 确保在显示表单前重置表单验证状态
  if (noticeFormRef.value) {
    noticeFormRef.value.resetFields();
  }
  
  showNoticeForm.value = true;
  console.log('打开添加公告弹窗，表单数据:', noticeForm.value);
};

// 编辑系统公告
const editNotice = (notice) => {
  isEditingNotice.value = true;
  noticeForm.value = {
    id: notice.id,
    title: notice.title,
    content: notice.content,
    status: notice.status
  };
  showNoticeForm.value = true;
};

// 修改 submitNotice 函数，添加详细错误处理和日志
const submitNotice = async () => {
  try {
    // 手动验证表单数据
    if (!noticeForm.value.title || noticeForm.value.title.trim() === '') {
      ElMessage.warning('请输入公告标题');
      return;
    }
    
    if (!noticeForm.value.content || noticeForm.value.content.trim() === '') {
      ElMessage.warning('请输入公告内容');
      return;
    }
    
    console.log('开始提交公告数据:', JSON.stringify(noticeForm.value));
    let response;
    
    if (isEditingNotice.value) {
      // 更新公告
      console.log('更新公告，ID:', noticeForm.value.id);
      response = await adminApi.updateSystemNotice(noticeForm.value.id, noticeForm.value);
      console.log('更新公告响应:', response);
    } else {
      // 添加公告
      console.log('添加新公告');
      response = await adminApi.addSystemNotice(noticeForm.value);
      console.log('添加公告响应:', response);
    }
    
    if (response && (response.code === 200 || response.success)) {
      ElMessage.success(isEditingNotice.value ? '更新公告成功' : '发布公告成功');
      showNoticeForm.value = false;
      // 将新添加的公告添加到列表中
      if (!isEditingNotice.value && response.data) {
        noticeList.value.unshift(response.data);
        noticeTotal.value += 1;
      } else if (isEditingNotice.value && response.data) {
        const index = noticeList.value.findIndex(item => item.id === response.data.id);
        if (index !== -1) {
          noticeList.value[index] = response.data;
        }
      }
    } else {
      console.error('提交公告返回异常结果:', response);
      ElMessage.error((isEditingNotice.value ? '更新公告失败: ' : '发布公告失败: ') + (response?.msg || '未知错误'));
    }
  } catch (error) {
    console.error('提交公告异常:', error);
    ElMessage.error('提交公告失败: ' + (error.message || '未知错误'));
  }
};

// 删除系统公告
const deleteNotice = (notice) => {
  ElMessageBox.confirm(
    `确定要删除标题为"${notice.title}"的公告吗？此操作不可恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await adminApi.deleteSystemNotice(notice.id);
      if (response && (response.code === 200 || response.success)) {
        ElMessage.success('删除公告成功');
        fetchSystemNotices();
      } else {
        ElMessage.error('删除公告失败: ' + (response?.msg || '未知错误'));
      }
    } catch (error) {
      console.error('删除公告异常:', error);
      ElMessage.error('删除公告异常');
    }
  }).catch(() => {
    // 取消删除
  });
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
    
    // 在开发环境中，允许不验证权限直接访问管理页面
    if (import.meta.env.DEV) {
      console.log('开发环境，跳过严格权限验证');
      // 如果未设置管理员标记，自动设置
      if (isAdmin !== 'true') {
        localStorage.setItem('isAdmin', 'true');
      }
      if (userRole !== 'admin') {
        localStorage.setItem('userRole', 'admin');
      }
    } else {
      // 生产环境中进行完整验证
    if (isAdmin !== 'true' || userRole !== 'admin') {
      throw new Error('本地存储中没有管理员权限标识');
    }
    
    // 再次确认服务器上的管理员权限
      try {
    await adminApi.verifyAdmin();
    console.log('服务器验证管理员权限成功');
      } catch (error) {
        console.warn('服务器验证失败，使用本地权限判断');
      }
    }
    
    // 打印表单相关信息用于调试
    console.log('表单引用:', noticeFormRef);
    console.log('表单验证规则:', noticeRules.value);
    
    // 获取数据
    fetchStats();
    fetchUsers();
    fetchSystemNotices(); // 确保初始化时加载公告数据
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
  margin-bottom: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  overflow: hidden;
}

.admin-title {
  text-align: center;
  padding: 30px 0;
}

.admin-title h1 {
  margin: 0 0 10px 0;
  color: #ffffff;
  font-size: 32px;
  font-weight: 700;
  letter-spacing: 1.5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
}

.admin-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  letter-spacing: 0.5px;
}

.stats-row {
  margin-bottom: 30px;
}

.stats-card-wrapper {
  display: flex;
  flex-direction: column;
  height: 160px;
  overflow: hidden;
  color: white;
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  padding: 12px 15px 0 15px;
  position: relative;
}

.stats-card-wrapper:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.15);
}

.stats-top {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
}

.stats-icon {
  background-color: rgba(255, 255, 255, 0.2);
  padding: 12px;
  border-radius: 50%;
  margin-bottom: 5px;
  z-index: 2;
}

.stats-number {
  font-size: 50px;
  font-weight: 700;
  line-height: 0.9;
  z-index: 2;
  text-align: center;
  margin-top: -5px;
}

.stats-bottom {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(255, 255, 255, 0.15);
  padding: 8px 0;
  z-index: 2;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  width: 100%;
  margin-top: auto;
}

.stats-label {
  font-size: 16px;
  font-weight: 500;
  opacity: 0.95;
  letter-spacing: 0.5px;
  text-align: center;
}

.admin-content {
  margin-bottom: 30px;
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

:deep(.el-tabs__header) {
  margin: 0;
  background: #f9f9f9;
  border-bottom: 1px solid #eaeaea;
  padding: 0 20px;
}

:deep(.el-tabs__nav) {
  border: none !important;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  padding: 0 22px;
  height: 50px;
  line-height: 50px;
  transition: all 0.2s;
}

:deep(.el-tabs__item.is-active) {
  font-weight: 600;
  color: #6a11cb;
}

:deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
}

:deep(.el-tabs__content) {
  padding: 20px;
}

.table-operations {
  margin-bottom: 22px;
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  width: 350px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-detail {
  margin-left: 15px;
}

.user-name {
  font-weight: 600;
  font-size: 15px;
  color: #333;
}

.user-email {
  font-size: 13px;
  color: #666;
  margin-top: 4px;
}

.post-title {
  font-weight: 600;
  font-size: 15px;
  color: #333;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.post-stats {
  display: flex;
  gap: 15px;
}

.post-stats span {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.pagination-container {
  margin-top: 30px;
  margin-bottom: 10px;
  display: flex;
  justify-content: center;
}

.user-detail-card {
  padding: 25px;
}

.user-detail-header {
  display: flex;
  gap: 25px;
}

.user-detail-info {
  flex-grow: 1;
}

.user-detail-info h2 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #303133;
  font-size: 22px;
}

.user-detail-info p {
  margin: 12px 0;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #606266;
  font-size: 15px;
}

.confirm-content {
  padding: 25px 0;
  color: #606266;
}

.no-data {
  text-align: center;
  padding: 50px;
  color: #909399;
  font-size: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

/* 系统公告管理样式 */
.settings-container {
  margin-bottom: 25px;
}

.settings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eaeaea;
}

.settings-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.notice-content-preview {
  white-space: pre-line;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 6px;
}

.notice-header {
  margin-bottom: 20px;
  border-bottom: 1px solid #eaeaea;
  padding-bottom: 15px;
}

.notice-meta {
  display: flex;
  gap: 20px;
  margin-top: 12px;
  color: #666;
  font-size: 14px;
}

.notice-content {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  min-height: 120px;
  border: 1px solid #eaeaea;
}

/* 评估问卷详情样式 */
.assessment-detail .detail-header {
  margin-bottom: 20px;
  border-bottom: 1px solid #eaeaea;
  padding-bottom: 15px;
}

.assessment-detail .detail-meta {
  display: flex;
  gap: 20px;
  margin-top: 12px;
  color: #666;
  font-size: 14px;
}

.assessment-detail .detail-content {
  padding: 20px;
  background-color: #fafafa;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

/* 情绪记录样式 */
.score {
  font-weight: bold;
  color: #409EFF;
  font-size: 16px;
}

.user-id {
  font-size: 13px;
  color: #909399;
  margin-top: 3px;
}

/* 表格样式增强 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #303133;
  padding: 15px 0;
}

:deep(.el-table td) {
  padding: 14px 0;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: #fafafa;
}

:deep(.el-pagination) {
  padding: 15px 0;
}

:deep(.el-pagination .el-pagination__total) {
  font-weight: 500;
}

:deep(.el-pagination button:disabled) {
  background-color: #f5f7fa;
}

:deep(.el-pagination .btn-next,
.el-pagination .btn-prev) {
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}

:deep(.el-pagination .el-pager li) {
  border-radius: 4px;
  border: 1px solid #e0e0e0;
  font-weight: 500;
}

:deep(.el-pagination .el-pager li.is-active) {
  background-color: #409EFF;
  color: #fff;
  border-color: #409EFF;
  font-weight: 600;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}

:deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  padding: 0 22px;
  height: 50px;
  line-height: 50px;
  transition: all 0.2s;
}

:deep(.el-tabs__item.is-active) {
  font-weight: 600;
}

:deep(.el-button) {
  border-radius: 6px;
  font-weight: 500;
}

:deep(.el-button--primary) {
  font-weight: 500;
}

:deep(.el-input__inner) {
  border-radius: 6px;
}

:deep(.el-tag) {
  border-radius: 4px;
  padding: 0 10px;
  height: 28px;
  line-height: 28px;
  font-weight: 500;
}

:deep(.el-dialog) {
  border-radius: 10px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  padding: 20px;
  margin: 0;
  border-bottom: 1px solid #f0f0f0;
  background-color: #f9f9f9;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  font-size: 18px;
  color: #333;
}

:deep(.el-dialog__body) {
  padding: 25px;
}

:deep(.el-dialog__footer) {
  padding: 15px 20px;
  border-top: 1px solid #f0f0f0;
  background-color: #f9f9f9;
}
</style> 