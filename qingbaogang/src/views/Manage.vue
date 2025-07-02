<template>
  <div class="manage-container">
    <h2>帖子管理</h2>
    
    <!-- 发帖表单 -->
    <div class="card post-form-card">
      <h3>发布新帖子</h3>
      <div class="post-form">
        <input v-model="title" placeholder="帖子标题" class="input-field" />
        <textarea v-model="content" placeholder="帖子内容" class="textarea-field" rows="5"></textarea>
        <button @click="handlePost" class="btn primary-btn">发布帖子</button>
      </div>
    </div>

    <!-- 帖子列表 -->
    <div class="card post-list-card">
      <h3>帖子列表</h3>
      
      <div v-if="posts.length === 0" class="empty-state">
        暂无帖子，请发布第一篇帖子
      </div>

      <div v-else class="post-list">
        <div v-for="post in posts" :key="post.id" class="post-item">
          <div class="post-header">
            <h4 class="post-title" @click="showDetail(post)">{{ post.title }}</h4>
            <div class="post-actions">
              <button @click="showDetail(post)" class="btn small-btn">查看</button>
              <button @click="deletePostById(post.id)" class="btn small-btn danger-btn">删除</button>
            </div>
          </div>
          <div class="post-meta">
            <span>作者: {{ post.userName || '匿名' }}</span>
            <span>发布时间: {{ post.createTime || '未知' }}</span>
            <span v-if="post.likeCount !== undefined">点赞: {{ post.likeCount }}</span>
          </div>
          <div class="post-preview">{{ post.content ? (post.content.length > 100 ? post.content.substring(0, 100) + '...' : post.content) : '无内容' }}</div>
        </div>
      </div>
      
      <!-- 分页 -->
      <div class="pagination">
        <button :disabled="page === 1" @click="prevPage" class="btn page-btn">上一页</button>
        <span class="page-info">第 {{ page }} 页</span>
        <button :disabled="!hasMore" @click="nextPage" class="btn page-btn">下一页</button>
      </div>
    </div>

    <!-- 帖子详情弹窗 -->
    <div v-if="detailVisible" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ detailPost?.title }}</h3>
          <button @click="detailVisible = false" class="close-btn">&times;</button>
          </div>
        <div class="post-meta detail-meta">
          <span>作者: {{ detailPost?.userName || '匿名' }}</span>
          <span>发布时间: {{ detailPost?.createTime || '未知' }}</span>
          </div>
        <div class="post-content detail-content">
          {{ detailPost?.content || '无内容' }}
          </div>
        <div class="modal-footer">
          <button @click="detailVisible = false" class="btn">关闭</button>
          <button @click="deleteAndClose(detailPost?.id)" class="btn danger-btn">删除帖子</button>
          </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { createPost, getPostList, getPostDetail, deletePost } from '@/api/community'
import { useUserStore } from '@/stores/user'

const title = ref('')
const content = ref('')
const posts = ref([])
const page = ref(1)
const size = ref(5)
const hasMore = ref(true)
const userStore = useUserStore()

const detailVisible = ref(false)
const detailPost = ref(null)

const fetchPosts = async () => {
  try {
    const res = await getPostList({ page: page.value, size: size.value })
    if (res.data && Array.isArray(res.data.records)) {
      posts.value = res.data.records
      hasMore.value = res.data.records.length === size.value
    }
  } catch (error) {
    console.error('获取帖子列表失败:', error)
    // 如果API调用失败，使用示例数据
    posts.value = [
      {
        id: 1,
        title: '示例帖子 1',
        content: '这是一个示例帖子内容，API调用失败时显示',
        userName: '测试用户',
        createTime: '2024-01-01 12:00:00'
      },
      {
        id: 2,
        title: '示例帖子 2',
        content: '这是另一个示例帖子内容，用于测试展示',
        userName: '测试用户',
        createTime: '2024-01-02 12:00:00'
      }
    ]
  }
}

const handlePost = async () => {
  if (!title.value || !content.value) {
    alert('标题和内容不能为空')
    return
  }
  
  try {
    const res = await createPost({
      title: title.value,
      content: content.value,
      userId: userStore.userId
    })
    
    if (res.data && res.data.id) {
      alert('发帖成功')
      title.value = ''
      content.value = ''
      fetchPosts()
    }
  } catch (error) {
    console.error('发帖失败:', error)
    alert('发帖失败，请稍后重试')
  }
}

const deletePostById = async (id) => {
  if (!confirm('确定要删除该帖子吗？此操作不可恢复')) return
  
  try {
    const res = await deletePost(id)
    if (res.data && res.data === true) {
      alert('删除成功')
      fetchPosts()
    } else {
      alert('删除失败')
    }
  } catch (error) {
    console.error('删除帖子失败:', error)
    alert('删除失败，请稍后重试')
  }
}

const showDetail = async (post) => {
  try {
    const res = await getPostDetail(post.id)
    if (res.data) {
      detailPost.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    console.error('获取帖子详情失败:', error)
    // 如果获取详情失败，直接显示列表中的数据
    detailPost.value = post
    detailVisible.value = true
  }
}

const deleteAndClose = async (id) => {
  if (!id) return
  await deletePostById(id)
  detailVisible.value = false
}

const prevPage = () => {
  if (page.value > 1) {
    page.value--
    fetchPosts()
  }
}

const nextPage = () => {
  if (hasMore.value) {
    page.value++
    fetchPosts()
  }
}

onMounted(fetchPosts)
</script>

<style scoped>
.manage-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.post-form-card h3, .post-list-card h3 {
  margin-top: 0;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 15px;
}

.post-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.input-field, .textarea-field {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  box-sizing: border-box;
}

.textarea-field {
  resize: vertical;
}

.btn {
  padding: 8px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.primary-btn {
  background-color: #409eff;
  color: white;
  padding: 10px 20px;
}

.primary-btn:hover {
  background-color: #3a8ee6;
}

.small-btn {
  padding: 5px 10px;
  font-size: 0.8rem;
}

.danger-btn {
  background-color: #f56c6c;
  color: white;
}

.danger-btn:hover {
  background-color: #e64242;
}

.page-btn {
  background-color: #f2f6fc;
  color: #606266;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.post-item {
  border: 1px solid #eee;
  border-radius: 6px;
  padding: 15px;
  background: #f9f9f9;
  transition: transform 0.2s;
}

.post-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.post-title {
  margin: 0;
  cursor: pointer;
  color: #409eff;
}

.post-title:hover {
  text-decoration: underline;
}

.post-actions {
  display: flex;
  gap: 10px;
}

.post-meta {
  display: flex;
  gap: 15px;
  color: #909399;
  font-size: 0.8rem;
  margin-bottom: 10px;
}

.post-preview {
  color: #606266;
  font-size: 0.9rem;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
}

.page-info {
  color: #606266;
}

.empty-state {
  text-align: center;
  padding: 30px;
  color: #909399;
  font-style: italic;
}

.modal {
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.modal-content {
  background: #fff;
  padding: 0;
  border-radius: 8px;
  min-width: 300px;
  width: 70%;
  max-width: 700px;
  max-height: 80vh;
  overflow: auto;
  box-shadow: 0 5px 25px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.2rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #909399;
}

.detail-meta {
  padding: 10px 20px;
  border-bottom: 1px solid #f0f0f0;
  }
  
.detail-content {
  padding: 20px;
  line-height: 1.6;
  white-space: pre-line;
  min-height: 150px;
  }
  
.modal-footer {
  padding: 15px 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>