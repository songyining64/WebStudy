<template>
  <div class="community-main">
    <aside class="community-sidebar">
      <div class="sidebar-title">分类</div>
      <ul class="sidebar-list">
        <li :class="{active: filterTag==='全部'}" @click="filterTag='全部'">全部</li>
        <li v-for="tag in tags" :key="tag" :class="{active: filterTag===tag}" @click="filterTag=tag">{{ tag }}</li>
      </ul>
    </aside>
    <section class="community-content">
      <div class="community-header">
        <input v-model="search" class="search-input" placeholder="搜索帖子/作者/标签..." @keyup.enter="handleSearch" />
        <div class="header-actions">
          <router-link to="/profile" class="profile-link">
            <img :src="headerAvatarUrl" class="header-avatar" />
            <span>我的主页</span>
          </router-link>
          <button class="post-btn" @click="showPostDialog=true">发布</button>
        </div>
      </div>
      <div class="post-list">
        <div v-for="post in filteredPosts" :key="post.id" class="post-card" @click="viewPost(post)">
          <img v-if="post.image" :src="post.image" class="post-img" />
          <div class="post-info">
            <div class="post-title">{{ post.title }}</div>
            <div class="post-content">{{ post.content.slice(0, 48) }}{{ post.content.length>48?'...':'' }}</div>
            <div class="post-meta">
              <span class="post-author">{{ post.author }}</span>
              <span class="post-tag" v-for="tag in post.tags" :key="tag">#{{ tag }}</span>
            </div>
            <div class="post-actions">
              <span @click.stop="likePost(post)"><i :class="['icon-like', post.liked?'liked':'']"></i> {{ post.likes }}</span>
              <span @click.stop="collectPost(post)"><i :class="['icon-star', post.collected?'collected':'']"></i> {{ post.collects }}</span>
              <span><i class="icon-comment"></i> {{ post.comments.length }}</span>
              <span v-if="canEdit(post)" @click.stop="editPost(post)"><i class="icon-edit"></i></span>
              <span v-if="canEdit(post)" @click.stop="deletePost(post)"><i class="icon-delete"></i></span>
            </div>
          </div>
        </div>
      </div>
      <div class="pagination">
        <button :disabled="page===1" @click="page--">上一页</button>
        <span>第{{page}}页</span>
        <button :disabled="page===maxPage" @click="page++">下一页</button>
      </div>
    </section>
    <!-- 发帖弹窗 -->
    <div v-if="showPostDialog" class="dialog-bg">
      <div class="dialog-box">
        <h3>{{ editMode?'编辑帖子':'发布新帖' }}</h3>
        <form @submit.prevent="submitPost">
          <input v-model="postForm.title" placeholder="标题" required class="form-input" />
          <textarea v-model="postForm.content" placeholder="内容" required class="form-textarea"></textarea>
          
          <!-- 图片上传区域 -->
          <div class="image-upload-section">
            <el-upload
              class="image-uploader"
              :show-file-list="false"
              :before-upload="beforeImageUpload"
              :http-request="handleImageUpload"
              :auto-upload="true"
              accept="image/*"
            >
              <div class="upload-area">
                <img v-if="postForm.image" :src="postForm.image" class="image-preview" />
                <div v-else class="upload-placeholder">
                  <el-icon class="upload-icon"><Plus /></el-icon>
                  <div class="upload-text">点击上传图片</div>
                </div>
              </div>
            </el-upload>
            <div v-if="postForm.image" class="image-actions">
              <el-button type="danger" size="small" @click="removeImage">删除图片</el-button>
            </div>
          </div>

          <input v-model="postForm.tags" placeholder="标签（逗号分隔）" class="form-input" />
          <div class="dialog-actions">
            <button type="submit" class="submit-btn">{{ editMode?'保存':'发布' }}</button>
            <button type="button" class="cancel-btn" @click="closeDialog">取消</button>
          </div>
        </form>
      </div>
    </div>
    <!-- 帖子详情弹窗 -->
    <div v-if="showDetailDialog" class="dialog-bg">
      <div class="dialog-box detail-box">
        <h3>{{ detailPost.title }}</h3>
        <img v-if="detailPost.image" :src="detailPost.image" class="detail-img" />
        <div class="detail-content">{{ detailPost.content }}</div>
        <div class="detail-meta">
          <span>作者：{{ detailPost.author }}</span>
          <span>标签：<span v-for="tag in detailPost.tags" :key="tag">#{{ tag }} </span></span>
        </div>
        <div class="detail-actions">
          <span @click="likePost(detailPost)"><i :class="['icon-like', detailPost.liked?'liked':'']"></i> {{ detailPost.likes }}</span>
          <span @click="collectPost(detailPost)"><i :class="['icon-star', detailPost.collected?'collected':'']"></i> {{ detailPost.collects }}</span>
        </div>
        <div class="comment-section">
          <h4>评论</h4>
          <div v-for="c in detailPost.comments" :key="c.id" class="comment-item">
            <span class="comment-author">{{ c.author }}：</span>
            <span>{{ c.content }}</span>
            <span v-if="canDeleteComment(c)" class="delete-comment" @click="deleteComment(c)">
              <i class="icon-delete"></i>
            </span>
          </div>
          <form @submit.prevent="addComment">
            <input v-model="commentInput" placeholder="写下你的评论..." class="form-input" />
            <button type="submit" class="submit-btn">评论</button>
          </form>
        </div>
        <div class="dialog-actions">
          <button class="cancel-btn" @click="showDetailDialog=false">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '../stores/user'
import { usePostStore } from '../stores/posts'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import defaultAvatarUrl from '@/assets/default-avatar.png'

const userStore = useUserStore()
const postStore = usePostStore()

const tags = ['情绪', '互助', '成长', '心理急救', '生活']
const filterTag = ref('全部')
const search = ref('')
const page = ref(1)
const pageSize = 8
const showPostDialog = ref(false)
const showDetailDialog = ref(false)
const editMode = ref(false)
const postForm = ref({id:null, title:'', content:'', image:'', tags:''})
const detailPost = ref({})
const commentInput = ref('')

const filteredPosts = computed(() => {
  let arr = postStore.posts.filter(p =>
    (filterTag.value === '全部' || p.tags.includes(filterTag.value)) &&
    (search.value === '' || p.title.includes(search.value) || p.content.includes(search.value) || 
     p.author.includes(search.value) || p.tags.join(',').includes(search.value))
  )
  return arr.slice((page.value-1)*pageSize, page.value*pageSize)
})

const maxPage = computed(() => {
  let arr = postStore.posts.filter(p =>
    (filterTag.value === '全部' || p.tags.includes(filterTag.value)) &&
    (search.value === '' || p.title.includes(search.value) || p.content.includes(search.value) || 
     p.author.includes(search.value) || p.tags.join(',').includes(search.value))
  )
  return Math.max(1, Math.ceil(arr.length/pageSize))
})

const handleSearch = () => { 
  page.value = 1
}

const likePost = (post) => {
  postStore.toggleLike(post.id)
  if (detailPost.value.id === post.id) {
    const updatedPost = postStore.posts.find(p => p.id === post.id)
    if (updatedPost) {
      detailPost.value.liked = updatedPost.liked
      detailPost.value.likes = updatedPost.likes
    }
  }
}

const collectPost = (post) => {
  postStore.toggleCollect(post.id)
  if (detailPost.value.id === post.id) {
    const updatedPost = postStore.posts.find(p => p.id === post.id)
    if (updatedPost) {
      detailPost.value.collected = updatedPost.collected
      detailPost.value.collects = updatedPost.collects
    }
  }
}

const canEdit = (post) => post.author === userStore.name

const editPost = (post) => {
  editMode.value = true
  showPostDialog.value = true
  postForm.value = {...post, tags: post.tags.join(',')}
}

const deletePost = (post) => {
  if(confirm('确定删除该帖子？')) {
    postStore.deletePost(post.id)
  }
}

const closeDialog = () => {
  showPostDialog.value = false
  editMode.value = false
  postForm.value = {id:null, title:'', content:'', image:'', tags:''}
}

const submitPost = () => {
  const postData = {
    ...postForm.value,
    tags: postForm.value.tags.split(',').map(t => t.trim()),
    author: userStore.name
  }

  if(editMode.value) {
    postStore.updatePost(postData)
  } else {
    postStore.addPost(postData)
  }
  closeDialog()
}

const viewPost = (post) => {
  detailPost.value = JSON.parse(JSON.stringify(post))
  showDetailDialog.value = true
  commentInput.value = ''
}

const addComment = () => {
  if(!commentInput.value.trim()) return
  const newComment = {
    id: Date.now(),
    author: userStore.name,
    content: commentInput.value
  }
  
  postStore.addComment(detailPost.value.id, newComment)
  detailPost.value.comments.push(newComment)
  commentInput.value = ''
}

const canDeleteComment = (comment) => {
  return comment.author === userStore.name || detailPost.value.author === userStore.name
}

const deleteComment = (comment) => {
  if(!confirm('确定删除这条评论？')) return
  
  postStore.deleteComment(detailPost.value.id, comment.id)
  detailPost.value.comments = detailPost.value.comments.filter(c => c.id !== comment.id)
}

// 图片上传前的验证
const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

// 处理图片上传
const handleImageUpload = async ({ file }) => {
  try {
    const reader = new FileReader()
    reader.onload = (e) => {
      postForm.value.image = e.target.result
    }
    reader.onerror = (error) => {
      console.error('Error reading file:', error)
      ElMessage.error('读取文件失败')
    }
    reader.readAsDataURL(file)
  } catch (error) {
    console.error('Upload failed:', error)
    ElMessage.error('上传失败')
  }
}

// 移除图片
const removeImage = () => {
  postForm.value.image = ''
}

const headerAvatarUrl = computed(() => userStore.avatar || defaultAvatarUrl)
</script>

<style scoped>
.community-main {
  display: flex;
  min-height: 92vh;
  background: #f8fafc;
}
.community-sidebar {
  width: 120px;
  background: #fff;
  border-right: 1px solid #eaeaea;
  padding: 32px 0 0 0;
  min-height: 100%;
}
.sidebar-title {
  font-size: 1.1rem;
  font-weight: bold;
  color: #2980b9;
  margin-left: 18px;
  margin-bottom: 18px;
}
.sidebar-list {
  list-style: none;
  padding: 0 0 0 18px;
  margin: 0;
}
.sidebar-list li {
  padding: 8px 0;
  cursor: pointer;
  color: #444;
  border-radius: 6px 0 0 6px;
  transition: background 0.2s, color 0.2s;
}
.sidebar-list li.active, .sidebar-list li:hover {
  background: #eaf6ff;
  color: #2980b9;
}
.community-content {
  flex: 1;
  padding: 32px 32px 0 32px;
  min-width: 0;
}
.community-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.search-input {
  width: 320px;
  height: 38px;
  border-radius: 8px;
  border: 1.5px solid #e0e7ef;
  padding: 0 16px;
  font-size: 16px;
  margin-right: 18px;
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}
.profile-link {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  color: #2c3e50;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 8px;
  background: #f8fafc;
  transition: background 0.2s;
}
.profile-link:hover {
  background: #edf2f7;
}
.header-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}
.post-btn {
  background: #3498db;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}
.post-btn:hover {
  background: #2980b9;
}
.post-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 22px;
}
.post-card {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(52,152,219,0.08);
  overflow: hidden;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.2s;
  position: relative;
}
.post-card:hover {
  box-shadow: 0 6px 24px rgba(52,152,219,0.16);
}
.post-img {
  width: 100%;
  height: 140px;
  object-fit: cover;
  background: #eee;
}
.post-info {
  padding: 16px 16px 10px 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}
.post-title {
  font-size: 1.1rem;
  font-weight: bold;
  color: #222;
  margin-bottom: 6px;
}
.post-content {
  color: #444;
  font-size: 0.98rem;
  margin-bottom: 8px;
  flex: 1;
}
.post-meta {
  font-size: 0.92rem;
  color: #888;
  margin-bottom: 8px;
}
.post-tag {
  background: #eaf6ff;
  color: #3498db;
  border-radius: 6px;
  padding: 2px 8px;
  margin-left: 6px;
  font-size: 0.9em;
}
.post-actions {
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 1rem;
  color: #888;
  margin-top: 2px;
}
.icon-like, .icon-star, .icon-comment, .icon-edit, .icon-delete {
  font-style: normal;
  margin-right: 3px;
}
.icon-like:before { content: '👍'; }
.icon-like.liked:before { content: '💙'; }
.icon-star:before { content: '☆'; }
.icon-star.collected:before { content: '★'; }
.icon-comment:before { content: '💬'; }
.icon-edit:before { content: '✏️'; }
.icon-delete:before { content: '🗑️'; }
.icon-like.liked, .icon-star.collected { color: #3498db; }
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 18px;
  margin: 32px 0 0 0;
}
.pagination button {
  background: #f2f6fa;
  border: none;
  border-radius: 8px;
  padding: 8px 18px;
  font-size: 1rem;
  color: #2980b9;
  cursor: pointer;
  transition: background 0.2s;
}
.pagination button:disabled {
  background: #e0e7ef;
  color: #aaa;
  cursor: not-allowed;
}
.dialog-bg {
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.13);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}
.dialog-box {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 4px 32px rgba(52,152,219,0.18);
  padding: 32px 28px 18px 28px;
  min-width: 340px;
  max-width: 95vw;
  position: relative;
  max-height: 90vh;
  overflow-y: auto;
}
.dialog-box h3 {
  text-align: center;
  margin-bottom: 18px;
  color: #2980b9;
  font-size: 1.2rem;
}
.form-input {
  width: 100%;
  height: 38px;
  border-radius: 8px;
  border: 1.5px solid #e0e7ef;
  padding: 0 12px;
  font-size: 15px;
  margin-bottom: 12px;
}
.form-textarea {
  width: 100%;
  min-height: 70px;
  border-radius: 8px;
  border: 1.5px solid #e0e7ef;
  padding: 8px 12px;
  font-size: 15px;
  margin-bottom: 12px;
  resize: vertical;
}
.dialog-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 18px;
  gap: 16px;
}
.submit-btn {
  background: #3498db;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}
.submit-btn:hover {
  background: #2980b9;
}
.cancel-btn {
  background: #eee;
  color: #444;
  border: none;
  border-radius: 8px;
  padding: 10px 18px;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s;
}
.cancel-btn:hover {
  background: #e0e7ef;
}
.detail-box {
  min-width: 420px;
  max-width: 600px;
}
.detail-img {
  width: 100%;
  max-height: 400px;
  object-fit: contain;
  margin: 16px 0;
}
.detail-content {
  font-size: 1.05rem;
  color: #444;
  margin-bottom: 10px;
}
.detail-meta {
  font-size: 0.95rem;
  color: #888;
  margin-bottom: 8px;
}
.detail-actions {
  display: flex;
  align-items: center;
  gap: 18px;
  font-size: 1rem;
  color: #888;
  margin-bottom: 10px;
}
.comment-section {
  margin-top: 18px;
}
.comment-section h4 {
  font-size: 1.05rem;
  color: #2980b9;
  margin-bottom: 8px;
}
.comment-item {
  background: #f8fafc;
  border-radius: 8px;
  padding: 6px 12px;
  margin-bottom: 6px;
  font-size: 0.98rem;
  display: flex;
  align-items: center;
}
.comment-author {
  color: #3498db;
  font-weight: 600;
}
.delete-comment {
  margin-left: auto;
  cursor: pointer;
  color: #e74c3c;
  opacity: 0.7;
  transition: opacity 0.2s;
}
.delete-comment:hover {
  opacity: 1;
}
.image-upload-section {
  margin: 16px 0;
}
.image-uploader {
  text-align: center;
}
.upload-area {
  width: 200px;
  height: 200px;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  margin: 0 auto;
}
.upload-area:hover {
  border-color: #409EFF;
}
.image-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.upload-placeholder {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
}
.upload-icon {
  font-size: 28px;
  color: #8c939d;
  margin-bottom: 8px;
}
.upload-text {
  color: #8c939d;
  font-size: 14px;
}
.image-actions {
  margin-top: 8px;
  text-align: center;
}
</style> 