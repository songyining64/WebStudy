<template>
  <div class="community-container">
    <div class="community-header">
      <div class="search-box">
        <input type="text" v-model="searchQuery" placeholder="搜索帖子/作者/标签..." @keyup.enter="searchPosts" />
        <button class="search-btn" @click="searchPosts">搜索</button>
        <button class="advanced-search-btn" @click="showAdvancedSearch = !showAdvancedSearch">
          {{ showAdvancedSearch ? '收起' : '高级搜索' }}
        </button>
      </div>
      <button class="publish-btn" @click="showPostForm = true">发布</button>
    </div>

    <!-- 分类和筛选 -->
    <div class="filter-container">
      <div class="categories">
        <span 
          v-for="category in categories" 
          :key="category.value" 
          :class="['category-item', { active: activeCategory === category.value }]"
          @click="setCategory(category.value)"
        >
          {{ category.label }}
        </span>
      </div>
      <div class="sort-options">
        <span>排序:</span>
        <select v-model="sortBy" @change="fetchPosts" title="排序字段">
          <option value="create_time">最新发布</option>
          <option value="like_count">点赞数</option>
          <option value="comment_count">评论数</option>
        </select>
        <select v-model="sortOrder" @change="fetchPosts" title="排序方式">
          <option value="DESC">降序</option>
          <option value="ASC">升序</option>
        </select>
      </div>
    </div>

    <!-- 帖子列表 -->
    <div class="post-list">
      <div v-for="post in posts" :key="post.id" class="post-card">
        <div class="post-header">
          <h3 class="post-title" @click="goToPostDetail(post.id)">{{ post.title }}</h3>
          <div class="post-meta">
            <span class="author">{{ post.username || post.userName || '匿名' }}</span>
            <span class="date">{{ formatDate(post.createTime) }}</span>
          </div>
        </div>
        <div class="post-content" @click="goToPostDetail(post.id)">
          {{ post.content && post.content.length > 120 ? post.content.substring(0, 120) + '...' : post.content }}
        </div>
        <!-- 帖子图片展示 -->
        <div v-if="post.images" class="post-images">
          <div class="images-grid" :class="'grid-' + Math.min(getImagesArray(post.images).length, 4)">
            <div 
              v-for="(image, index) in getImagesArray(post.images).slice(0, 4)" 
              :key="index" 
              class="image-item"
              @click.stop="showFullImage(image)"
            >
              <img :src="image" :alt="`${post.title}图片${index + 1}`" class="post-image" @error="handleImageError($event, post, index)" />
              <!-- 如果有更多图片，显示+N -->
              <div v-if="index === 3 && getImagesArray(post.images).length > 4" class="more-images-overlay">
                <span>+{{ getImagesArray(post.images).length - 4 }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="post-tags">
          <template v-if="post.tags && typeof post.tags === 'string'">
            <span v-for="tag in post.tags.split(',')" :key="tag" class="tag">
              #{{ tag.trim() }}
            </span>
          </template>
          <template v-else-if="post.tags && Array.isArray(post.tags)">
            <span v-for="tag in post.tags" :key="tag" class="tag">
              #{{ typeof tag === 'string' ? tag.trim() : tag }}
            </span>
          </template>
        </div>
        <div class="post-actions">
          <div class="action-item" @click="toggleLike(post)" :class="{ 'active': post.liked }">
            <i :class="['icon', post.liked ? 'icon-liked' : 'icon-like']">{{ post.liked ? '❤️' : '🤍' }}</i>
            <span>{{ post.likeCount || 0 }}</span>
          </div>
          <div class="action-item" @click="goToPostDetail(post.id)">
            <i class="icon icon-comment">💬</i>
            <span>{{ post.commentCount || 0 }}</span>
          </div>
          <div class="action-item" @click="toggleFavorite(post)" :class="{ 'active': post.favorited }">
            <i :class="['icon', post.favorited ? 'icon-favorited' : 'icon-favorite']">{{ post.favorited ? '⭐' : '☆' }}</i>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="posts.length === 0" class="empty-state">
        <p>暂无帖子</p>
        <button @click="showPostForm = true" class="empty-btn">发布第一篇帖子</button>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <button :disabled="currentPage === 1" @click="prevPage" class="page-btn">上一页</button>
      <span class="page-indicator">第 {{ currentPage }} 页</span>
      <button :disabled="!hasMorePages" @click="nextPage" class="page-btn">下一页</button>
    </div>

    <!-- 发帖表单弹窗 -->
    <div v-if="showPostForm" class="modal-overlay">
      <div class="modal-content post-form-modal">
        <div class="modal-header">
          <h3>发布帖子</h3>
          <button @click="showPostForm = false" class="close-btn">&times;</button>
        </div>
        <div class="post-form">
          <input v-model="newPost.title" placeholder="标题" class="form-control" />
          <select v-model="newPost.category" class="form-control" required title="帖子分类">
            <option value="" disabled>请选择分类</option>
            <option v-for="item in categories" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <textarea v-model="newPost.content" placeholder="分享你的故事..." class="form-control" rows="6"></textarea>
          
          <!-- 图片上传区域 -->
          <div class="image-upload-container">
            <div class="upload-header">
              <h4>添加图片</h4>
              <span class="image-count">{{ uploadedImages.length }}/{{ maxImageCount }}</span>
            </div>
            <div class="image-preview-container">
              <!-- 已上传的图片预览 -->
              <div v-for="(image, index) in uploadedImages" :key="index" class="image-preview">
                <img :src="image.url" alt="预览图片" />
                <div class="image-actions">
                  <button type="button" class="delete-btn" @click="removeImage(index)">×</button>
                </div>
              </div>
              <!-- 上传按钮 -->
              <div v-if="uploadedImages.length < maxImageCount" class="upload-btn-wrapper">
                <div class="upload-btn" @click="triggerFileInput">
                  <i class="upload-icon">+</i>
                  <span>上传图片</span>
                </div>
                <input 
                  ref="fileInput" 
                  type="file" 
                  accept="image/*" 
                  multiple 
                  @change="handleFileUpload" 
                  style="display: none;"
                />
              </div>
            </div>
            <div class="upload-tips">支持JPG、PNG格式，最多上传{{ maxImageCount }}张</div>
          </div>
          
          <input v-model="newPost.tags" placeholder="添加标签，用逗号分隔" class="form-control" />
          <div class="form-footer">
            <button @click="showPostForm = false" class="cancel-btn">取消</button>
            <button 
              @click="createNewPost" 
              class="submit-btn" 
              :disabled="!newPost.title || !newPost.content || !newPost.category || isSubmitting"
            >
              {{ isSubmitting ? '发布中...' : '发布' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 高级搜索面板 -->
    <div v-if="showAdvancedSearch" class="advanced-search-panel">
      <div class="advanced-search-form">
        <div class="form-group">
          <label>关键词</label>
          <input type="text" v-model="advancedSearch.keyword" placeholder="搜索标题、内容和标签" />
        </div>
        <div class="form-group">
          <label>分类</label>
          <select v-model="advancedSearch.category" title="筛选分类">
            <option value="">全部分类</option>
            <option v-for="category in categories.slice(1)" :key="category.value" :value="category.value">
              {{ category.label }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>排序方式</label>
          <select v-model="advancedSearch.sortBy" title="高级搜索排序字段">
            <option value="create_time">发布时间</option>
            <option value="like_count">点赞数</option>
            <option value="comment_count">评论数</option>
          </select>
          <select v-model="advancedSearch.sortOrder" title="高级搜索排序方式">
            <option value="DESC">降序</option>
            <option value="ASC">升序</option>
          </select>
        </div>
        <button class="search-btn" @click="applyAdvancedSearch">搜索</button>
      </div>
    </div>

    <!-- 图片查看弹窗 -->
    <div v-if="showImageViewer" class="image-viewer-overlay">
      <div class="image-viewer-content">
        <img :src="currentViewingImage" alt="图片查看" />
        <button @click="showImageViewer = false" class="close-btn">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { request } from '@/utils/request'
import { 
  getPostList, 
  createPost, 
  likePost, 
  unlikePost, 
  isPostLiked, 
  getPostLikeCount,
  favoritePost,
  unfavoritePost,
  isPostFavorited,
  searchPosts as apiSearchPosts,
  getPostsByCategory,
  advancedSearchPosts
} from '@/api/communityApi'

const router = useRouter()
const userStore = useUserStore()

// 响应式状态
const posts = ref([])
const currentPage = ref(1)
const pageSize = ref(5)
const hasMorePages = ref(true)
const searchQuery = ref('')
const showPostForm = ref(false)
const activeCategory = ref('all')
const sortBy = ref('create_time')
const sortOrder = ref('DESC')
const isLoading = ref(false)
const apiError = ref(null)
const searchMode = ref('basic') // 'basic', 'keyword', 'category', 'advanced'
const showAdvancedSearch = ref(false)
const advancedSearch = reactive({
  keyword: '',
  category: '',
  sortBy: 'create_time',
  sortOrder: 'DESC'
})
// 图片上传相关
const fileInput = ref(null)
const uploadedImages = ref([])
const maxImageCount = 6 // 最大上传图片数量
const isSubmitting = ref(false) // 提交状态
// 图片查看
const currentViewingImage = ref('')
const showImageViewer = ref(false)
const mockPosts = ref([
  {
    id: 'mock1',
    title: '欢迎来到心理健康社区',
    content: '这是一个模拟帖子，当API未正常工作时会显示。在这里，我们可以分享心理健康相关的话题和经验。',
    createTime: new Date().toISOString(),
    updateTime: new Date().toISOString(),
    userId: 1,
    username: '系统管理员',
    avatar: '/src/assets/default-avatar.png',
    tags: '心理健康,社区',
    category: '公告',
    likeCount: 15,
    commentCount: 5
  },
  {
    id: 'mock2',
    title: '如何缓解学习压力',
    content: '分享一些缓解学习压力的小技巧...',
    createTime: new Date(Date.now() - 86400000).toISOString(),
    updateTime: new Date(Date.now() - 86400000).toISOString(),
    userId: 2,
    username: '心理咨询师',
    avatar: '/src/assets/default-avatar.png',
    tags: '压力管理,学习方法',
    category: '学习',
    likeCount: 8,
    commentCount: 3
  }
])
const useMockData = ref(false)

// 用户信息
const userAvatar = computed(() => userStore.avatar || '/src/assets/default-avatar.png')

// 发帖表单
const newPost = reactive({
  title: '',
  content: '',
  tags: '',
  category: ''
})

// 分类
const categories = [
  { label: '全部', value: 'all' },
  { label: '心理健康', value: '心理健康' },
  { label: '学习方法', value: '学习方法' },
  { label: '情感', value: '情感' },
  { label: '压力管理', value: '压力管理' },
  { label: '社交', value: '社交' },
  { label: '职业发展', value: '职业发展' },
  { label: '健康', value: '健康' },
  { label: '生活', value: '生活' },
  { label: '其他', value: '其他' }
]

// 获取帖子列表
const fetchPosts = async () => {
  console.log('开始获取帖子列表，参数:', {
    current: currentPage.value,
    size: pageSize.value,
    category: activeCategory.value,
    keyword: searchQuery.value,
    sortBy: sortBy.value,
    sortOrder: sortOrder.value,
    searchMode: searchMode.value
  })
  
  isLoading.value = true
  apiError.value = null
  
  try {
    // 构建基础参数
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    
    let response
    
    // 根据搜索模式选择不同的API
    switch (searchMode.value) {
      case 'keyword':
        // 关键词搜索
        console.log('执行关键词搜索，关键词:', searchQuery.value);
        params.keyword = searchQuery.value
        console.log('准备调用搜索API，参数:', params);
        try {
          response = await apiSearchPosts(params)
          console.log('搜索API响应:', response);
        } catch (error) {
          console.error('搜索API调用失败:', error);
        }
        break
        
      case 'category':
        // 分类筛选
        response = await getPostsByCategory(activeCategory.value, params)
        break
        
      case 'advanced':
        // 高级搜索
        params.keyword = searchQuery.value
        params.category = activeCategory.value !== 'all' ? activeCategory.value : undefined
        params.sortBy = sortBy.value
        params.sortOrder = sortOrder.value
        response = await advancedSearchPosts(params)
        break
        
      default:
        // 基础搜索
        if (activeCategory.value && activeCategory.value !== 'all') {
          params.category = activeCategory.value
        }
        if (searchQuery.value) {
          params.keyword = searchQuery.value
        }
        params.sortBy = sortBy.value
        params.sortOrder = sortOrder.value
        response = await getPostList(params)
    }
    
    if (response && response.data) {
      console.log('获取帖子列表成功:', response.data)
      
      let postList = []
      
      // 处理返回的分页对象
      if (response.data.records) {
        postList = response.data.records.map(post => {
          // 确保tags格式一致
          if (post.tags && Array.isArray(post.tags)) {
            post.tags = post.tags.join(',');
          }
          return post;
        });
        hasMorePages.value = currentPage.value * pageSize.value < response.data.total
      } else if (Array.isArray(response.data)) {
        postList = response.data.map(post => {
          // 确保tags格式一致
          if (post.tags && Array.isArray(post.tags)) {
            post.tags = post.tags.join(',');
          }
          return post;
        });
        hasMorePages.value = response.data.length >= pageSize.value
      } else {
        console.warn('未知的响应数据格式:', response.data)
        useMockData.value = true
      }
      
      // 如果获取到了帖子列表，给每个帖子获取点赞和收藏状态
      if (postList.length > 0 && !useMockData.value) {
        const userId = userStore.userId
        
        // 打印第一个帖子的数据结构
        if (postList[0]) {
          console.log('帖子列表中的单个帖子数据结构:', postList[0])
        }
        
        // 使用 Promise.all 并行获取所有帖子的点赞和收藏状态
        const postsWithStatus = await Promise.all(
          postList.map(async (post) => {
            try {
              let liked = false
              let likeCount = post.likeCount || 0
              let favorited = false
              
              // 尝试从本地存储获取点赞和收藏状态
              const likeKey = `post_like_${post.id}_${userId}`
              const favoriteKey = `post_favorite_${post.id}_${userId}`
              const likeData = localStorage.getItem(likeKey)
              const favoriteData = localStorage.getItem(favoriteKey)
              
              // 如果本地存储有数据，先使用本地数据
              if (likeData) {
                try {
                  const parsedLike = JSON.parse(likeData)
                  if (parsedLike && parsedLike.timestamp) {
                    // 如果缓存时间不超过1小时，使用本地数据
                    if (Date.now() - parsedLike.timestamp < 3600000) {
                      liked = parsedLike.liked
                      console.log(`从本地缓存获取帖子${post.id}的点赞状态:`, liked)
                    }
                  }
                } catch (e) {
                  console.error('解析本地点赞数据失败:', e)
                }
              }
              
              if (favoriteData) {
                try {
                  const parsedFavorite = JSON.parse(favoriteData)
                  if (parsedFavorite && parsedFavorite.timestamp) {
                    // 如果缓存时间不超过1小时，使用本地数据
                    if (Date.now() - parsedFavorite.timestamp < 3600000) {
                      favorited = parsedFavorite.favorited
                      console.log(`从本地缓存获取帖子${post.id}的收藏状态:`, favorited)
                    }
                  }
                } catch (e) {
                  console.error('解析本地收藏数据失败:', e)
                }
              }
              
              // 无论是否有本地缓存，都尝试从服务器获取最新状态
              const results = await Promise.allSettled([
                isPostLiked(post.id, userId),
                getPostLikeCount(post.id),
                isPostFavorited(post.id)
              ])
              
              // 处理每个结果
              const likedResult = results[0].status === 'fulfilled' ? results[0].value : null
              const likeCountResult = results[1].status === 'fulfilled' ? results[1].value : null
              const favoritedResult = results[2].status === 'fulfilled' ? results[2].value : null
              
              // 如果服务器请求成功，使用服务器数据并更新本地缓存
              if (likedResult) {
                liked = likedResult.data || false
                localStorage.setItem(likeKey, JSON.stringify({
                  liked,
                  timestamp: Date.now()
                }))
              }
              
              if (likeCountResult) {
                likeCount = likeCountResult.data || 0
              }
              
              if (favoritedResult) {
                favorited = favoritedResult.data || false
                localStorage.setItem(favoriteKey, JSON.stringify({
                  favorited,
                  timestamp: Date.now()
                }))
              }
              
              // 更新帖子数据
              return {
                ...post,
                liked,
                likeCount,
                favorited
              }
            } catch (err) {
              console.warn(`获取帖子 ${post.id} 的状态失败:`, err)
              // 如果获取失败，使用默认值
              return {
                ...post,
                liked: false,
                likeCount: post.likeCount || 0,
                favorited: false
              }
            }
          })
        )
        
        posts.value = postsWithStatus
      } else {
        posts.value = postList
      }
    } else {
      console.warn('响应数据为空')
      useMockData.value = true
    }
  } catch (error) {
    console.error('获取帖子列表失败:', error)
    apiError.value = error
    useMockData.value = true
  } finally {
    isLoading.value = false
    
    // 如果需要使用模拟数据
    if (useMockData.value) {
      console.log('使用模拟数据')
      posts.value = mockPosts.value
      hasMorePages.value = false
    }
  }
}

// 创建新帖子
const createNewPost = async () => {
  if (!newPost.title || !newPost.content || !newPost.category) {
    alert('标题、内容和分类不能为空')
    return
  }
  
  // 设置提交状态
  isSubmitting.value = true
  
  try {
    // 首先上传图片
    const imageUrls = await uploadImagesToServer()
    
    // 构造帖子数据
    const postData = {
      title: newPost.title,
      content: newPost.content,
      tags: newPost.tags,
      userId: userStore.userId || '1',
      username: userStore.username || '用户' + (userStore.userId || '1'),
      category: newPost.category,
      images: imageUrls.filter(url => url).join(',') // 将图片URL数组转换为逗号分隔的字符串
    }
    
    console.log('创建帖子，带用户名和图片:', postData)
    
    // 发送创建帖子请求
    const response = await createPost(postData)
    
    console.log('发帖成功，服务器返回:', response)
    
    // 重置表单
    newPost.title = ''
    newPost.content = ''
    newPost.tags = ''
    newPost.category = ''
    
    // 清空已上传图片
    uploadedImages.value.forEach(image => {
      if (image.url && image.url.startsWith('blob:')) {
        URL.revokeObjectURL(image.url)
      }
    })
    uploadedImages.value = []
    
    showPostForm.value = false
    
    // 显示成功消息
    alert('发帖成功！')
    
    // 重新加载帖子列表 - 回到第一页
    currentPage.value = 1
    // 延迟刷新以确保后端数据更新
    setTimeout(() => {
      fetchPosts()
    }, 1000)
  } catch (error) {
    console.error('发帖失败:', error)
    alert('发帖失败，请稍后重试')
  } finally {
    // 重置提交状态
    isSubmitting.value = false
  }
}

// 点赞/取消点赞
const toggleLike = async (post) => {
  // 如果未登录，提示用户
  if (!userStore.isLoggedIn) {
    alert('请先登录后再点赞')
    router.push('/login')
    return
  }

  const userId = userStore.userId
  if (!userId) {
    console.error('未找到用户ID')
    return
  }
  
  try {
    // 确保postId是有效的
    if (!post.id) {
      console.error('无效的帖子ID', post)
      throw new Error('无效的帖子数据')
    }
    
    // 添加调试日志
    console.log(`处理点赞操作：帖子ID=${post.id}(${typeof post.id})，帖子标题="${post.title}"，用户ID=${userId}, 当前点赞状态=${post.liked}`)
    
    // 先在UI上立即反馈，再发送请求
    const originalStatus = post.liked
    const originalCount = post.likeCount || 0
    
    // 更新UI状态
    post.liked = !originalStatus
    post.likeCount = originalStatus ? (originalCount - 1) : (originalCount + 1)
    
    // 发送API请求
    let response
    if (originalStatus) {
      // 取消点赞
      console.log(`准备发送取消点赞请求，帖子ID=${post.id}，用户ID=${userId}`)
      response = await unlikePost(post.id, userId)
      console.log('取消点赞响应:', response)
      
      if (response && response.code === 200) {
        console.log('取消点赞成功')
      } else if (response && response.msg && (response.msg.includes('未点赞') || response.msg.includes('没有点赞'))) {
        console.warn('该帖子未被点赞，无需取消')
        // 不需要恢复UI状态
      } else {
        throw new Error(response?.msg || '取消点赞失败')
      }
    } else {
      // 点赞
      console.log(`准备发送点赞请求，帖子ID=${post.id}，用户ID=${userId}`)
      response = await likePost(post.id, userId)
      console.log('点赞响应:', response)
      
      if (response && response.code === 200) {
        console.log('点赞成功')
      } else if (response && response.msg && response.msg.includes('已经点赞')) {
        console.warn('已经点赞过该帖子')
        // 不需要恢复UI状态，因为帖子实际上已经是点赞状态
      } else {
        console.error('点赞请求异常响应:', response)
        throw new Error(response?.msg || '点赞失败')
      }
    }
    
    // 延时 1 秒后再次检查点赞状态，确认数据库状态
    setTimeout(async () => {
      try {
        const checkResponse = await isPostLiked(post.id, userId)
        console.log(`点赞操作后状态检查：帖子ID=${post.id}，数据库中的点赞状态=${checkResponse.data}`)
        
        // 如果状态不一致，需要向用户提示
        if (checkResponse.data !== post.liked) {
          console.warn('点赞状态与数据库不一致，可能需要刷新页面')
        }
      } catch (err) {
        console.error('检查点赞状态失败:', err)
      }
    }, 1000)
    
  } catch (error) {
    // 如果API请求失败，恢复UI状态
    console.error('点赞操作失败:', error)
    post.liked = !post.liked
    post.likeCount = post.liked ? (post.likeCount + 1) : (post.likeCount - 1)
    
    // 显示错误信息
    alert(error.message || '操作失败，请稍后重试')
  }
}

// 收藏/取消收藏
const toggleFavorite = async (post) => {
  // 检查用户是否登录
  if (!userStore.isLoggedIn) {
    alert('请先登录后再收藏')
    router.push('/login')
    return
  }
  
  const userId = userStore.userId
  if (!userId) {
    console.error('未找到用户ID')
    return
  }
  
  try {
    // 确保帖子ID有效
    if (!post.id) {
      throw new Error('无效的帖子数据')
    }
    
    console.log(`处理收藏操作：帖子ID=${post.id}, 当前收藏状态=${post.favorited}`)
    
    // 先更新UI状态
    const originalStatus = post.favorited
    post.favorited = !originalStatus
    
    // 更新本地存储
    const favoriteKey = `post_favorite_${post.id}_${userId}`
    localStorage.setItem(favoriteKey, JSON.stringify({
      favorited: post.favorited,
      timestamp: Date.now()
    }))
    
    // 发送API请求
    if (originalStatus) {
      // 取消收藏
      await unfavoritePost(post.id)
    } else {
      // 收藏
      await favoritePost(post.id)
    }
    
    console.log(originalStatus ? '取消收藏成功' : '收藏成功')
  } catch (error) {
    console.error('收藏操作失败:', error)
    
    // 恢复UI状态
    post.favorited = !post.favorited
    
    // 恢复本地存储
    const favoriteKey = `post_favorite_${post.id}_${userId}`
    localStorage.setItem(favoriteKey, JSON.stringify({
      favorited: post.favorited,
      timestamp: Date.now()
    }))
    
    // 显示错误提示
    alert('收藏操作失败，请稍后重试')
  }
}

// 翻页
const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    fetchPosts()
  }
}

const nextPage = () => {
  if (hasMorePages.value) {
    currentPage.value++
    fetchPosts()
  }
}

// 搜索帖子
const searchPosts = () => {
  console.log('搜索按钮被点击，当前搜索关键词:', searchQuery.value);
  
  if (searchQuery.value) {
    console.log('设置搜索模式为 keyword');
    searchMode.value = 'keyword'
  } else {
    console.log('设置搜索模式为 basic');
    searchMode.value = 'basic'
  }
  
  console.log('重置页码为 1');
  currentPage.value = 1
  
  console.log('调用 fetchPosts 函数获取帖子');
  fetchPosts()
}

// 设置分类
const setCategory = (category) => {
  activeCategory.value = category
  if (category !== 'all') {
    searchMode.value = 'category'
  } else {
    searchMode.value = 'basic'
  }
  currentPage.value = 1
  fetchPosts()
}

// 执行高级搜索
const doAdvancedSearch = () => {
  searchMode.value = 'advanced'
  currentPage.value = 1
  fetchPosts()
}

// 应用高级搜索
const applyAdvancedSearch = () => {
  // 将高级搜索的值应用到主搜索
  searchQuery.value = advancedSearch.keyword
  
  if (advancedSearch.category) {
    activeCategory.value = advancedSearch.category
  } else {
    activeCategory.value = 'all'
  }
  
  sortBy.value = advancedSearch.sortBy
  sortOrder.value = advancedSearch.sortOrder
  
  // 设置搜索模式为高级搜索
  searchMode.value = 'advanced'
  
  // 重置页码
  currentPage.value = 1
  
  // 关闭高级搜索面板
  showAdvancedSearch.value = false
  
  // 执行搜索
  fetchPosts()
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 跳转到帖子详情
const goToPostDetail = (postId) => {
  router.push(`/post/${postId}`)
}

// 跳转到个人主页
const goToProfile = () => {
  router.push('/user-profile')
}

// 组件挂载后获取帖子列表
onMounted(() => {
  // 初始化获取帖子列表
  fetchPosts()
})

// 触发文件上传
const triggerFileInput = () => {
  fileInput.value.click()
}

// 处理文件上传
const handleFileUpload = (event) => {
  const files = event.target.files
  if (!files || files.length === 0) return
  
  // 计算可以上传的图片数量
  const remainingSlots = maxImageCount - uploadedImages.value.length
  const filesToProcess = Array.from(files).slice(0, remainingSlots)
  
  // 处理每个文件
  filesToProcess.forEach(file => {
    // 验证文件类型
    if (!file.type.startsWith('image/')) {
      alert('只支持上传图片文件')
      return
    }
    
    // 验证文件大小（限制为5MB）
    if (file.size > 5 * 1024 * 1024) {
      alert('图片大小不能超过5MB')
      return
    }
    
    // 创建临时URL用于预览
    const imageUrl = URL.createObjectURL(file)
    
    // 添加到已上传图片列表
    uploadedImages.value.push({
      file: file,
      url: imageUrl,
      uploaded: false
    })
  })
  
  // 重置文件输入，以便相同文件可以再次触发change事件
  event.target.value = null
}

// 移除已上传的图片
const removeImage = (index) => {
  // 如果有临时URL，释放它
  if (uploadedImages.value[index].url && uploadedImages.value[index].url.startsWith('blob:')) {
    URL.revokeObjectURL(uploadedImages.value[index].url)
  }
  
  // 从列表中移除
  uploadedImages.value.splice(index, 1)
}

// 上传图片到服务器
const uploadImagesToServer = async () => {
  // 收集所有未上传的图片文件
  const imagesToUpload = uploadedImages.value.filter(img => !img.uploaded)
  if (imagesToUpload.length === 0) return []
  
  console.log('准备上传图片，数量:', imagesToUpload.length)
  
  // 上传所有图片
  const uploadPromises = imagesToUpload.map(async (image) => {
    try {
      const formData = new FormData()
      formData.append('file', image.file)
      
      console.log('上传图片:', image.file.name, '类型:', image.file.type, '大小:', image.file.size)
      
      // 调用上传API
      const response = await request.upload('/api/upload/image', image.file)
      
      console.log('图片上传响应:', response)
      
      // 标记为已上传
      image.uploaded = true
      
      // 返回服务器返回的图片URL
      const imageUrl = response.data?.url || ''
      console.log('获取到的图片URL:', imageUrl)
      return imageUrl
    } catch (error) {
      console.error('图片上传失败:', error)
      return ''
    }
  })
  
  // 等待所有图片上传完成
  const urls = await Promise.all(uploadPromises)
  console.log('所有图片上传完成，URLs:', urls)
  return urls
}

// 显示图片查看弹窗
const showFullImage = (imageUrl) => {
  currentViewingImage.value = imageUrl
  showImageViewer.value = true
}

// 辅助函数：将字符串转换为数组并确保URL完整
const getImagesArray = (images) => {
  if (!images) return []
  
  console.log('处理图片数据:', images, '类型:', typeof images)
  
  if (typeof images === 'string') {
    // 过滤掉空字符串
    const imagesArray = images.split(',').filter(img => img.trim() !== '')
    console.log('解析后的图片数组:', imagesArray)
    
    // 处理每个URL，确保路径正确
    return imagesArray.map((url, index) => {
      console.log(`处理图片[${index}]的URL: ${url}`)
      
      // 如果已经是完整URL，直接返回
      if (url.startsWith('http')) {
        return url
      }
      
      // 提取文件名
      const parts = url.split('/')
      const filename = parts[parts.length - 1]
      console.log(`提取的文件名: ${filename}`)
      
      // 使用正确的路径格式 - 添加应用上下文路径
      return `/mental/upload/${filename}`
    })
  } else if (Array.isArray(images)) {
    console.log('已是数组格式的图片:', images)
    return images
  }
  
  console.warn('无法处理的图片格式:', images)
  return []
}

// 图片错误处理
const handleImageError = (event, post, index) => {
  console.error(`图片加载失败: 帖子ID=${post.id}, 图片索引=${index}`)
  
  // 尝试其他URL格式
  const imagesArray = getImagesArray(post.images)
  const url = imagesArray[index]
  
  if (url) {
    const parts = url.split('/')
    const filename = parts[parts.length - 1]
    
    // 尝试其他URL格式
    const alternativeUrls = [
      `/mental/upload/${filename}`,
      `/upload/${filename}`,
      `http://localhost:8080/mental/upload/${filename}`,
      `http://localhost:8080/upload/${filename}`
    ]
    
    // 找到当前URL在替代URL列表中的位置
    const currentIndex = alternativeUrls.findIndex(alt => alt === url)
    
    // 如果有下一个替代URL，尝试使用它
    if (currentIndex < alternativeUrls.length - 1) {
      console.log(`尝试替代URL: ${alternativeUrls[currentIndex + 1]}`)
      event.target.src = alternativeUrls[currentIndex + 1]
    } else {
      // 所有URL都尝试过了，显示默认图片
      event.target.src = '/src/assets/default-avatar.png'
      event.target.alt = '图片加载失败'
    }
  }
}
</script>

<style scoped>
.community-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.community-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 10px;
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  max-width: 600px;
}

.search-box input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-btn {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.advanced-search-btn {
  padding: 8px 16px;
  background-color: transparent;
  color: #1890ff;
  border: 1px solid #1890ff;
  border-radius: 4px;
  cursor: pointer;
}

.publish-btn {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.filter-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.categories {
  display: flex;
  gap: 10px;
}

.category-item {
  padding: 8px 16px;
  background-color: #f0f0f0;
  border-radius: 4px;
  cursor: pointer;
}

.category-item.active {
  background-color: #1890ff;
  color: white;
}

.sort-options {
  display: flex;
  gap: 10px;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.post-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 16px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.post-title {
  margin: 0 0 8px 0;
  cursor: pointer;
  color: #333;
}

.post-meta {
  display: flex;
  gap: 10px;
  color: #888;
  font-size: 13px;
  margin-bottom: 10px;
}

.post-content {
  color: #333;
  margin-bottom: 12px;
  line-height: 1.5;
  cursor: pointer;
}

.post-images {
  margin-bottom: 12px;
}

.images-grid {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.images-grid.grid-1 .image-item {
  width: 200px;
  height: 200px;
}

.images-grid.grid-2 .image-item {
  width: calc(50% - 5px);
  height: 150px;
}

.images-grid.grid-3 .image-item, 
.images-grid.grid-4 .image-item {
  width: calc(50% - 5px);
  height: 120px;
}

.image-item {
  position: relative;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.more-images-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 13px;
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.tag {
  color: #1890ff;
  font-size: 13px;
}

.post-actions {
  display: flex;
  gap: 20px;
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  color: #888;
}

.action-item.active {
  color: #1890ff;
}

.action-item:hover {
  color: #1890ff;
}

.icon-liked {
  color: #f56c6c;
}

.icon-favorited {
  color: #faad14;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
}

.page-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background-color: #fff;
  border-radius: 4px;
  cursor: pointer;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-indicator {
  color: #666;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #fff;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow: auto;
  box-shadow: 0 4px 16px rgba(0,0,0,0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #888;
}

.post-form {
  padding: 16px;
}

.form-control {
  width: 100%;
  margin-bottom: 16px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

textarea.form-control {
  resize: vertical;
  min-height: 120px;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background-color: #fff;
  border-radius: 4px;
  cursor: pointer;
}

.submit-btn {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
  color: #888;
}

.empty-btn {
  margin-top: 16px;
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/* 高级搜索面板样式 */
.advanced-search-panel {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1001;
}

.advanced-search-form {
  background-color: #fff;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow: auto;
  padding: 16px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-btn {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/* 图片上传区域样式 */
.image-upload-container {
  margin-bottom: 16px;
}

.upload-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.upload-header h4 {
  margin: 0;
  color: #333;
}

.image-count {
  color: #888;
  font-size: 13px;
}

.image-preview-container {
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
}

.image-preview {
  position: relative;
  width: 100px;
  height: 100px;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-actions {
  position: absolute;
  top: 0;
  right: 0;
  display: flex;
  gap: 5px;
  padding: 5px;
}

.delete-btn {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  color: #888;
}

.upload-btn-wrapper {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 5px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.upload-btn {
  display: flex;
  align-items: center;
  gap: 5px;
}

.upload-icon {
  color: #888;
  font-size: 16px;
}

.upload-tips {
  color: #888;
  font-size: 13px;
}

/* 图片查看弹窗样式 */
.image-viewer-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1002;
}

.image-viewer-content {
  background-color: #fff;
  border-radius: 8px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow: auto;
  padding: 16px;
}

.image-viewer-content img {
  width: 100%;
  height: auto;
  max-height: 100%;
  object-fit: contain;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #888;
  float: right;
}
</style> 