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
          @click="currentTab = tab.key"
        >
          {{ tab.label }}
          <span class="count">({{ getTabCount(tab.key) }})</span>
        </div>
      </div>

      <div class="post-list">
        <div v-if="currentTabPosts.length === 0" class="empty-state">
          暂无内容
        </div>
        <div v-else v-for="post in currentTabPosts" :key="post.id" class="post-card" @click="viewPost(post)">
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
            </div>
          </div>
        </div>
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
import defaultAvatarUrl from '@/assets/default-avatar.png'

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

const avatarUrl = computed(() => userStore.avatar || defaultAvatarUrl)

const currentTabPosts = computed(() => {
  switch(currentTab.value) {
    case 'myPosts':
      return postStore.posts.filter(post => post.author === userStore.name)
    case 'likedPosts':
      return postStore.posts.filter(post => post.liked)
    case 'collectedPosts':
      return postStore.posts.filter(post => post.collected)
    default:
      return []
  }
})

const getTabCount = (tabKey) => {
  switch(tabKey) {
    case 'myPosts':
      return postStore.posts.filter(post => post.author === userStore.name).length
    case 'likedPosts':
      return postStore.posts.filter(post => post.liked).length
    case 'collectedPosts':
      return postStore.posts.filter(post => post.collected).length
    default:
      return 0
  }
}

const viewPost = (post) => {
  detailPost.value = JSON.parse(JSON.stringify(post))
  showDetailDialog.value = true
  commentInput.value = ''
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

const canDeleteComment = (comment) => {
  return comment.author === userStore.name || detailPost.value.author === userStore.name
}

const deleteComment = (comment) => {
  if (!confirm('确定删除这条评论？')) return
  
  postStore.deleteComment(detailPost.value.id, comment.id)
  detailPost.value.comments = detailPost.value.comments.filter(c => c.id !== comment.id)
}

const addComment = () => {
  if (!commentInput.value.trim()) return
  const newComment = {
    id: Date.now(),
    author: userStore.name,
    content: commentInput.value
  }
  
  postStore.addComment(detailPost.value.id, newComment)
  detailPost.value.comments.push(newComment)
  commentInput.value = ''
}
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
  background: #3498db;
}

.count {
  font-size: 0.9em;
  color: #95a5a6;
  margin-left: 4px;
}

.empty-state {
  text-align: center;
  padding: 48px;
  color: #95a5a6;
  font-size: 1.1rem;
}

.post-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.post-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(52,152,219,0.08);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.post-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(52,152,219,0.12);
}

/* 复用Community.vue中的其他样式 */
.post-img, .post-info, .post-title, .post-content, .post-meta, .post-tag,
.post-actions, .dialog-bg, .dialog-box, .detail-box, .detail-img,
.detail-content, .detail-meta, .detail-actions, .comment-section,
.comment-item, .comment-author, .delete-comment, .form-input,
.submit-btn, .cancel-btn {
  composes: from '../views/Community.vue';
}
</style> 