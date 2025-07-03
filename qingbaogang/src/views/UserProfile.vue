<template>
  <div class="profile-container">
    <div class="profile-header">
      <img :src="avatarUrl" class="profile-avatar" />
      <div class="profile-info">
        <h2>{{ userStore.name }}</h2>
        <p class="profile-bio">{{ userStore.bio || '这个用户很懒，还没有写简介' }}</p>
      </div>
    </div>

    <div class="profile-content">
      <div class="tab-header">
        <div 
          v-for="tab in tabs" 
          :key="tab.key"
          :class="['tab-item', { active: currentTab === tab.key }]"
          @click="switchTab(tab.key)"
        >
          {{ tab.label }}
          <span class="count">({{ tabCounts[tab.key] || 0 }})</span>
        </div>
      </div>

      <div class="post-list">
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
        <div v-else-if="currentTabPosts.length === 0" class="empty-state">
          暂无内容
        </div>
        <div v-else v-for="post in currentTabPosts" :key="post.id" class="post-card" @click="viewPost(post)">
          <img v-if="post.images" :src="post.images.split(',')[0]" class="post-img" />
          <div class="post-info">
            <div class="post-title">{{ post.title }}</div>
            <div class="post-content">{{ post.content.slice(0, 48) }}{{ post.content.length>48?'...':'' }}</div>
            <div class="post-meta">
              <span class="post-author">{{ post.username || post.author }}</span>
              <span class="post-tag" v-for="tag in getPostTags(post)" :key="tag">#{{ tag }}</span>
            </div>
            <div class="post-actions">
              <span @click.stop="likePost(post)"><i :class="['icon-like', post.liked?'liked':'']"></i> {{ post.likeCount || post.likes || 0 }}</span>
              <span @click.stop="collectPost(post)"><i :class="['icon-star', post.collected?'collected':'']"></i> {{ post.favoriteCount || post.collects || 0 }}</span>
              <span><i class="icon-comment"></i> {{ post.commentCount || (post.comments && post.comments.length) || 0 }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 分页控件 -->
      <div class="pagination" v-if="currentTabPosts.length > 0">
        <button 
          :disabled="currentPage <= 1" 
          @click="changePage(currentPage - 1)"
          class="page-btn"
        >
          上一页
        </button>
        <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
        <button 
          :disabled="currentPage >= totalPages" 
          @click="changePage(currentPage + 1)"
          class="page-btn"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 帖子详情弹窗 -->
    <div v-if="showDetailDialog" class="dialog-bg">
      <div class="dialog-box detail-box">
        <h3>{{ detailPost.title }}</h3>
        <img v-if="detailPost.images" :src="detailPost.images.split(',')[0]" class="detail-img" />
        <div class="detail-content">{{ detailPost.content }}</div>
        <div class="detail-meta">
          <span>作者：{{ detailPost.username || detailPost.author }}</span>
          <span>标签：<span v-for="tag in getPostTags(detailPost)" :key="tag">#{{ tag }} </span></span>
        </div>
        <div class="detail-actions">
          <span @click="likePost(detailPost)"><i :class="['icon-like', detailPost.liked?'liked':'']"></i> {{ detailPost.likeCount || detailPost.likes || 0 }}</span>
          <span @click="collectPost(detailPost)"><i :class="['icon-star', detailPost.collected?'collected':'']"></i> {{ detailPost.favoriteCount || detailPost.collects || 0 }}</span>
        </div>
        <div class="comment-section">
          <h4>评论</h4>
          <div v-if="detailPost.comments && detailPost.comments.length > 0">
            <div v-for="c in detailPost.comments" :key="c.id" class="comment-item">
              <span class="comment-author">{{ c.author || c.username }}：</span>
              <span>{{ c.content }}</span>
              <span v-if="canDeleteComment(c)" class="delete-comment" @click="deleteComment(c)">
                <i class="icon-delete"></i>
              </span>
            </div>
          </div>
          <div v-else class="empty-comment">暂无评论</div>
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
import { ref, computed, onMounted, reactive } from 'vue'
import { useUserStore } from '../stores/user'
import { usePostStore } from '../stores/posts'
import defaultAvatarUrl from '@/assets/default-avatar.png'
import { getUserPosts, getUserLikedPosts, getUserFavoritePosts, 
         getPostDetail, likePost as apiLikePost, unlikePost as apiUnlikePost,
         favoritePost, unfavoritePost, createComment as apiAddComment, 
         deleteComment as apiDeleteComment } from '../api/communityApi'

const userStore = useUserStore()
const postStore = usePostStore()

const currentTab = ref('myPosts')
const tabs = [
  { key: 'myPosts', label: '我的帖子' },
  { key: 'likedPosts', label: '点赞的帖子' },
  { key: 'collectedPosts', label: '收藏的帖子' }
]
const showDetailDialog = ref(false)
const detailPost = ref({})
const commentInput = ref('')
const loading = ref(false)
const currentTabPosts = ref([])
const currentPage = ref(1)
const pageSize = 10
const totalItems = ref(0)
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize))
const tabCounts = reactive({
  myPosts: 0,
  likedPosts: 0,
  collectedPosts: 0
})

const avatarUrl = computed(() => userStore.avatar || defaultAvatarUrl)

// 获取帖子的标签数组
const getPostTags = (post) => {
  if (!post.tags) return []
  if (typeof post.tags === 'string') {
    return post.tags.split(',').filter(tag => tag.trim())
  }
  return post.tags
}

// 切换标签页
const switchTab = async (tabKey) => {
  if (currentTab.value === tabKey) return
  
  currentTab.value = tabKey
  currentPage.value = 1
  await fetchTabPosts()
}

// 获取当前标签页的帖子
const fetchTabPosts = async () => {
  if (!userStore.isLoggedIn || !userStore.userId) {
    console.warn('用户未登录，无法获取帖子')
    currentTabPosts.value = []
    return
  }
  
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize
    }
    
    let response
    
    switch(currentTab.value) {
      case 'myPosts':
        console.log(`正在获取用户发布的帖子，userId=${userStore.userId}`)
        response = await getUserPosts(userStore.userId, params)
        console.log('获取用户发布的帖子响应:', response)
        break
      case 'likedPosts':
        console.log(`正在获取用户点赞的帖子，userId=${userStore.userId}`)
        response = await getUserLikedPosts(userStore.userId, params)
        console.log('获取用户点赞的帖子响应:', response)
        break
      case 'collectedPosts':
        console.log(`正在获取用户收藏的帖子，userId=${userStore.userId}`)
        response = await getUserFavoritePosts(userStore.userId, params)
        console.log('获取用户收藏的帖子响应:', response)
        break
    }
    
    if (response && response.code === 200 && response.data) {
      currentTabPosts.value = response.data.records || []
      totalItems.value = response.data.total || 0
      
      // 更新计数
      tabCounts[currentTab.value] = totalItems.value
      console.log(`成功获取${currentTab.value}，共${totalItems.value}条记录`)
    } else {
      console.error('获取帖子失败:', response)
      if (response && response.msg) {
        console.error('错误消息:', response.msg)
      }
      currentTabPosts.value = []
      // 使用模拟数据（仅用于开发测试）
      if (process.env.NODE_ENV === 'development') {
        console.log('使用模拟数据')
        currentTabPosts.value = getMockPosts(currentTab.value)
        totalItems.value = currentTabPosts.value.length
        tabCounts[currentTab.value] = totalItems.value
      }
    }
  } catch (error) {
    console.error(`获取${currentTab.value}失败:`, error)
    currentTabPosts.value = []
    // 使用模拟数据（仅用于开发测试）
    if (process.env.NODE_ENV === 'development') {
      console.log('使用模拟数据')
      currentTabPosts.value = getMockPosts(currentTab.value)
      totalItems.value = currentTabPosts.value.length
      tabCounts[currentTab.value] = totalItems.value
    }
  } finally {
    loading.value = false
  }
}

// 获取模拟数据（仅用于开发测试）
const getMockPosts = (tabKey) => {
  const mockPosts = [
    {id:1,title:'今天心情不错',content:'阳光明媚，心情舒畅，推荐大家多晒太阳！',images:'',tags:['情绪'],username:'小明',likeCount:2,liked:false,favoriteCount:1,collected:false,comments:[]},
    {id:2,title:'焦虑时怎么办',content:'最近压力大，大家有什么缓解焦虑的方法吗？',images:'',tags:['互助','心理急救'],username:'小红',likeCount:3,liked:true,favoriteCount:0,collected:false,comments:[]},
    {id:3,title:'我的成长故事',content:'从自卑到自信，这一路走来收获很多...',images:'',tags:['成长'],username:'小刚',likeCount:1,liked:false,favoriteCount:2,collected:true,comments:[]}
  ]
  
  switch(tabKey) {
    case 'myPosts':
      return mockPosts.filter(post => post.username === userStore.name)
    case 'likedPosts':
      return mockPosts.filter(post => post.liked)
    case 'collectedPosts':
      return mockPosts.filter(post => post.collected)
    default:
      return []
  }
}

// 切换页码
const changePage = async (page) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  await fetchTabPosts()
}

// 初始化获取数据
const initFetchData = async () => {
  // 获取当前标签页数据
  await fetchTabPosts()
  
  // 获取其他标签页的计数
  if (userStore.isLoggedIn && userStore.userId) {
    try {
      // 获取我的帖子计数
      if (currentTab.value !== 'myPosts') {
        const myPostsRes = await getUserPosts(userStore.userId, { page: 1, size: 1 })
        if (myPostsRes && myPostsRes.code === 200 && myPostsRes.data) {
          tabCounts.myPosts = myPostsRes.data.total || 0
        }
      }
      
      // 获取点赞帖子计数
      if (currentTab.value !== 'likedPosts') {
        const likedPostsRes = await getUserLikedPosts(userStore.userId, { page: 1, size: 1 })
        if (likedPostsRes && likedPostsRes.code === 200 && likedPostsRes.data) {
          tabCounts.likedPosts = likedPostsRes.data.total || 0
        }
      }
      
      // 获取收藏帖子计数
      if (currentTab.value !== 'collectedPosts') {
        const favPostsRes = await getUserFavoritePosts(userStore.userId, { page: 1, size: 1 })
        if (favPostsRes && favPostsRes.code === 200 && favPostsRes.data) {
          tabCounts.collectedPosts = favPostsRes.data.total || 0
        }
      }
    } catch (error) {
      console.error('获取帖子计数失败:', error)
    }
  }
}

const viewPost = async (post) => {
  try {
    // 获取最新的帖子详情
    const response = await getPostDetail(post.id)
    if (response && response.code === 200 && response.data) {
      detailPost.value = response.data
    } else {
      detailPost.value = JSON.parse(JSON.stringify(post))
    }
    showDetailDialog.value = true
    commentInput.value = ''
  } catch (error) {
    console.error('获取帖子详情失败:', error)
    detailPost.value = JSON.parse(JSON.stringify(post))
    showDetailDialog.value = true
    commentInput.value = ''
  }
}

const likePost = async (post) => {
  if (!userStore.isLoggedIn) {
    alert('请先登录')
    return
  }
  
  try {
    // 更新UI状态
    const isLiked = post.liked
    post.liked = !isLiked
    post.likeCount = (post.likeCount || post.likes || 0) + (isLiked ? -1 : 1)
    
    // 调用API
    if (isLiked) {
      await apiUnlikePost(post.id, userStore.userId)
    } else {
      await apiLikePost(post.id, userStore.userId)
    }
    
    // 如果当前是"点赞的帖子"标签，并且取消了点赞，需要重新加载列表
    if (currentTab.value === 'likedPosts' && isLiked) {
      await fetchTabPosts()
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    // 恢复UI状态
    post.liked = !post.liked
    post.likeCount = (post.likeCount || post.likes || 0) + (post.liked ? 1 : -1)
    alert('操作失败，请稍后重试')
  }
}

const collectPost = async (post) => {
  if (!userStore.isLoggedIn) {
    alert('请先登录')
    return
  }
  
  try {
    // 更新UI状态
    const isCollected = post.collected
    post.collected = !isCollected
    post.favoriteCount = (post.favoriteCount || post.collects || 0) + (isCollected ? -1 : 1)
    
    // 调用API
    if (isCollected) {
      await unfavoritePost(post.id)
    } else {
      await favoritePost(post.id)
    }
    
    // 如果当前是"收藏的帖子"标签，并且取消了收藏，需要重新加载列表
    if (currentTab.value === 'collectedPosts' && isCollected) {
      await fetchTabPosts()
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    // 恢复UI状态
    post.collected = !post.collected
    post.favoriteCount = (post.favoriteCount || post.collects || 0) + (post.collected ? 1 : -1)
    alert('操作失败，请稍后重试')
  }
}

const canDeleteComment = (comment) => {
  return userStore.isLoggedIn && (
    comment.userId === userStore.userId || 
    detailPost.value.userId === userStore.userId
  )
}

const deleteComment = async (comment) => {
  if (!confirm('确定删除这条评论？')) return
  
  try {
    await apiDeleteComment(comment.id)
    detailPost.value.comments = detailPost.value.comments.filter(c => c.id !== comment.id)
  } catch (error) {
    console.error('删除评论失败:', error)
    alert('删除评论失败，请稍后重试')
  }
}

const addComment = async () => {
  if (!commentInput.value.trim()) return
  if (!userStore.isLoggedIn) {
    alert('请先登录')
    return
  }
  
  try {
    const commentData = {
      postId: detailPost.value.id,
      content: commentInput.value,
      userId: userStore.userId
    }
    
    const response = await apiAddComment(commentData)
    if (response && response.code === 200) {
      // 添加新评论到列表
      const newComment = {
        id: response.data,
        userId: userStore.userId,
        username: userStore.name,
        content: commentInput.value,
        createTime: new Date().toISOString()
      }
      
      if (!detailPost.value.comments) {
        detailPost.value.comments = []
      }
      
      detailPost.value.comments.push(newComment)
      commentInput.value = ''
    } else {
      throw new Error(response?.msg || '添加评论失败')
    }
  } catch (error) {
    console.error('添加评论失败:', error)
    alert('添加评论失败，请稍后重试')
  }
}

onMounted(() => {
  initFetchData()
})
</script>

<style scoped>
.profile-container {
  padding: 32px;
  background: #f8fafc;
  min-height: 92vh;
}

.profile-header {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(52,152,219,0.08);
}

.profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-right: 24px;
  object-fit: cover;
}

.profile-info h2 {
  margin: 0 0 8px 0;
  color: #2c3e50;
}

.profile-bio {
  margin: 0;
  color: #7f8c8d;
  font-size: 0.95rem;
}

.profile-content {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(52,152,219,0.08);
}

.tab-header {
  display: flex;
  border-bottom: 2px solid #eee;
  margin-bottom: 24px;
}

.tab-item {
  padding: 12px 24px;
  cursor: pointer;
  color: #7f8c8d;
  font-weight: 500;
  position: relative;
  transition: color 0.3s;
}

.tab-item.active {
  color: #3498db;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 2px;
  background-color: #3498db;
}

.tab-item .count {
  font-size: 0.85rem;
  opacity: 0.7;
}

.post-list {
  min-height: 300px;
}

.post-card {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s;
}

.post-card:hover {
  background-color: #f8fafc;
}

.post-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 16px;
}

.post-info {
  flex: 1;
}

.post-title {
  font-weight: 600;
  margin-bottom: 8px;
  color: #2c3e50;
}

.post-content {
  color: #7f8c8d;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.post-meta {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 0.85rem;
}

.post-author {
  color: #3498db;
  margin-right: 12px;
}

.post-tag {
  background-color: #f1f5f9;
  color: #64748b;
  padding: 2px 8px;
  border-radius: 12px;
  margin-right: 8px;
  font-size: 0.8rem;
}

.post-actions {
  display: flex;
  align-items: center;
  font-size: 0.9rem;
  color: #94a3b8;
}

.post-actions span {
  display: flex;
  align-items: center;
  margin-right: 16px;
  cursor: pointer;
}

.post-actions i {
  margin-right: 4px;
  font-size: 1rem;
}

.icon-like::before {
  content: '♡';
}

.icon-like.liked::before {
  content: '♥';
  color: #ef4444;
}

.icon-star::before {
  content: '☆';
}

.icon-star.collected::before {
  content: '★';
  color: #f59e0b;
}

.icon-comment::before {
  content: '💬';
}

.icon-delete::before {
  content: '🗑️';
}

.dialog-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog-box {
  background-color: white;
  border-radius: 16px;
  padding: 24px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
}

.detail-img {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  border-radius: 8px;
  margin: 16px 0;
}

.detail-content {
  margin: 16px 0;
  line-height: 1.6;
  white-space: pre-wrap;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin: 16px 0;
  color: #64748b;
  font-size: 0.9rem;
}

.detail-actions {
  display: flex;
  margin: 16px 0;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.comment-section {
  margin: 16px 0;
}

.comment-item {
  padding: 12px 0;
  border-bottom: 1px solid #eee;
  position: relative;
}

.comment-author {
  font-weight: 500;
  color: #3498db;
}

.delete-comment {
  position: absolute;
  right: 0;
  top: 12px;
  cursor: pointer;
  color: #ef4444;
}

.form-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  margin: 16px 0 8px;
}

.submit-btn {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
}

.dialog-actions {
  margin-top: 24px;
  text-align: right;
}

.cancel-btn {
  background-color: #e2e8f0;
  color: #64748b;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
}

.empty-state {
  text-align: center;
  padding: 48px 0;
  color: #94a3b8;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 0;
  color: #94a3b8;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e2e8f0;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

.empty-comment {
  text-align: center;
  padding: 16px 0;
  color: #94a3b8;
  font-size: 0.9rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 24px;
}

.page-btn {
  background-color: #f1f5f9;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  color: #64748b;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  margin: 0 16px;
  color: #64748b;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style> 