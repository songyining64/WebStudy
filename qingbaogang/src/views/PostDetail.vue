<template>
    <div class="post-detail-container">
        <div v-if="post" class="post-detail">
            <!-- 帖子内容 -->
            <div class="post-card">
                <div class="post-header">
                    <h1 class="post-title">{{ post.title }}</h1>
                    <div class="post-meta">
                        <span class="author">{{ post.username || post.userName || '匿名' }}</span>
                        <span class="date">{{ formatDate(post.createTime) }}</span>
                    </div>
                </div>
                <div class="post-content">{{ post.content }}</div>
                
                <!-- 帖子图片展示 -->
                <div v-if="post.images" class="post-images">
                    <div class="images-grid" :class="getImagesGridClass(post.images)">
                        <div 
                            v-for="(image, index) in getImagesArray(post.images)" 
                            :key="index" 
                            class="image-item"
                            @click="showFullImage(image)"
                        >
                            <img :src="image" :alt="`帖子图片${index + 1}`" class="post-image" @error="handleImageError($event, index)" />
                        </div>
                    </div>
                </div>
                
                <div class="post-tags" v-if="post.tags">
                    <span v-for="tag in post.tags.split(',')" :key="tag" class="tag">
                        #{{ tag.trim() }}
                    </span>
                </div>
                <div class="post-actions">
                    <div class="action-item" @click="toggleLike">
                        <i :class="['icon', liked ? 'icon-liked' : 'icon-like']">{{ liked ? '❤️' : '🤍' }}</i>
                        <span>{{ likeCount }}</span>
                    </div>
                    <div class="action-item" @click="toggleFavorite">
                        <i :class="['icon', favorited ? 'icon-favorited' : 'icon-favorite']">{{ favorited ? '⭐' : '☆' }}</i>
                        <span>{{ favorited ? '已收藏' : '收藏' }}</span>
                    </div>
                    <div class="action-item" @click="scrollToComments">
                        <i class="icon icon-comment">💬</i>
                        <span>评论</span>
                    </div>
                </div>
            </div>

            <!-- 评论区 -->
            <div id="comments" class="comments-section">
                <h3 class="section-title">评论区</h3>
                <CommentSection :postId="post.id" />
            </div>
        </div>

        <div v-else class="loading-state">
            <p>加载中...</p>
        </div>

        <!-- 返回按钮 -->
        <div class="back-button" @click="goBack">
            <i class="icon-back">←</i> 返回社区
        </div>
        
        <!-- 图片查看弹窗 -->
        <div v-if="showImageViewer" class="image-viewer-overlay" @click="closeImageViewer">
            <div class="image-viewer-content" @click.stop>
                <img :src="currentViewingImage" alt="图片查看" />
                <button @click="closeImageViewer" class="close-btn">关闭</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import CommentSection from '@/components/CommentSection.vue'
import {
    getPostDetail,
    likePost,
    unlikePost,
    isPostLiked,
    getPostLikeCount,
    favoritePost,
    unfavoritePost,
    isPostFavorited
} from '@/api/communityApi'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 响应式状态
const post = ref(null)
const liked = ref(false)
const likeCount = ref(0)
const favorited = ref(false)
// 图片查看相关
const currentViewingImage = ref('')
const showImageViewer = ref(false)

// 获取帖子详情
const fetchPostDetail = async () => {
    try {
        console.log(`开始获取帖子详情，ID:`, route.params.id)
        const res = await getPostDetail(route.params.id)
        if (res.data) {
            console.log('获取到的帖子详情数据:', res.data)
            post.value = res.data
            
            // 获取点赞状态
            if (userStore.isLoggedIn) {
                console.log(`开始获取帖子点赞状态，帖子ID=${post.value.id}(${typeof post.value.id})，用户ID=${userStore.userId}`)
                try {
                    const likedRes = await isPostLiked(post.value.id, userStore.userId)
                    liked.value = likedRes.data || false
                    console.log(`帖子点赞状态获取结果:`, likedRes)
                } catch (error) {
                    console.error('获取点赞状态失败:', error)
                    liked.value = false
                }
            } else {
                console.log('用户未登录，不获取点赞状态')
                liked.value = false
            }
            
            // 获取点赞数
            try {
                const likeCountRes = await getPostLikeCount(post.value.id)
                likeCount.value = likeCountRes.data || 0
                console.log(`帖子点赞数:`, likeCount.value)
            } catch (error) {
                console.error('获取点赞数失败:', error)
                likeCount.value = post.value.likeCount || 0
            }
            
            // 获取收藏状态
            if (userStore.isLoggedIn) {
                try {
                    const favoritedRes = await isPostFavorited(post.value.id)
                    favorited.value = favoritedRes.data || false
                    console.log(`帖子收藏状态:`, favorited.value)
                } catch (error) {
                    console.error('获取收藏状态失败:', error)
                    favorited.value = false
                }
            } else {
                favorited.value = false
            }
        } else {
            console.error('获取帖子详情失败，响应中没有数据', res)
            alert('帖子不存在或已被删除')
            router.push('/community')
        }
    } catch (error) {
        console.error('获取帖子详情失败:', error)
        alert('加载失败，请稍后重试')
        router.push('/community')
    }
}

// 点赞/取消点赞
const toggleLike = async () => {
    if (!userStore.isLoggedIn) {
        alert('请先登录后再点赞')
        router.push('/login')
        return
    }

    try {
        console.log(`处理点赞操作：帖子ID=${post.value.id}(${typeof post.value.id})，用户ID=${userStore.userId}, 当前点赞状态=${liked.value}`)
        
        // 先更新UI状态
        const originalStatus = liked.value
        liked.value = !originalStatus
        likeCount.value = originalStatus ? (likeCount.value - 1) : (likeCount.value + 1)
        
        // 发送API请求
        let response
        if (originalStatus) {
            // 取消点赞
            console.log(`准备发送取消点赞请求，帖子ID=${post.value.id}，用户ID=${userStore.userId}`)
            response = await unlikePost(post.value.id, userStore.userId)
            console.log('取消点赞响应:', response)
            
            if (response && response.code === 200) {
                console.log('取消点赞成功')
            } else if (response && response.msg && (response.msg.includes('未点赞') || response.msg.includes('没有点赞'))) {
                console.warn('该帖子未被点赞，无需取消')
            } else {
                throw new Error(response?.msg || '取消点赞失败')
            }
        } else {
            // 点赞
            console.log(`准备发送点赞请求，帖子ID=${post.value.id}，用户ID=${userStore.userId}`)
            response = await likePost(post.value.id, userStore.userId)
            console.log('点赞响应:', response)
            
            if (response && response.code === 200) {
                console.log('点赞成功')
            } else if (response && response.msg && response.msg.includes('已经点赞')) {
                console.warn('已经点赞过该帖子')
            } else {
                console.error('点赞请求异常响应:', response)
                throw new Error(response?.msg || '点赞失败')
            }
        }
        
        // 延时 1 秒后再次检查点赞状态，确认数据库状态
        setTimeout(async () => {
            try {
                const checkResponse = await isPostLiked(post.value.id, userStore.userId)
                console.log(`点赞操作后状态检查：帖子ID=${post.value.id}，数据库中的点赞状态=${checkResponse.data}`)
                
                // 如果状态不一致，需要向用户提示
                if (checkResponse.data !== liked.value) {
                    console.warn('点赞状态与数据库不一致，可能需要刷新页面')
                }
            } catch (err) {
                console.error('检查点赞状态失败:', err)
            }
        }, 1000)
    } catch (error) {
        // 如果操作失败，恢复原始状态
        console.error('点赞操作失败:', error)
        liked.value = !liked.value
        likeCount.value = liked.value ? (likeCount.value + 1) : (likeCount.value - 1)
        
        // 显示错误信息
        alert(error.message || '点赞失败，请稍后再试')
    }
}

// 收藏/取消收藏
const toggleFavorite = async () => {
    if (!userStore.isLoggedIn) {
        alert('请先登录后再收藏')
        router.push('/login')
        return
    }
    
    try {
        if (favorited.value) {
            await unfavoritePost(post.value.id)
            favorited.value = false
            
            // 更新本地存储
            const favoriteKey = `post_favorite_${post.value.id}_${userStore.userId}`
            localStorage.setItem(favoriteKey, JSON.stringify({
                favorited: false,
                timestamp: Date.now()
            }))
        } else {
            await favoritePost(post.value.id)
            favorited.value = true
            
            // 更新本地存储
            const favoriteKey = `post_favorite_${post.value.id}_${userStore.userId}`
            localStorage.setItem(favoriteKey, JSON.stringify({
                favorited: true,
                timestamp: Date.now()
            }))
        }
    } catch (error) {
        console.error('收藏操作失败:', error)
        // 如果操作失败，恢复原始状态
        favorited.value = !favorited.value
        
        // 显示错误消息，根据错误类型提供友好提示
        if (error.response && (error.response.status === 404 || error.response.status === 400)) {
            alert('收藏功能暂未开放，请稍后再试')
        } else {
            alert(error.message || '操作失败，请稍后重试')
        }
    }
}

// 返回上一页
const goBack = () => {
    router.back()
}

// 滚动到评论区
const scrollToComments = () => {
    document.getElementById('comments').scrollIntoView({ behavior: 'smooth' })
}

// 格式化日期
const formatDate = (dateString) => {
    if (!dateString) return ''
    const date = new Date(dateString)
    return date.toLocaleDateString('zh-CN')
}

// 辅助函数：将字符串转换为数组并确保URL完整
const getImagesArray = (images) => {
    if (!images) return []
    
    console.log('帖子详情页处理图片数据:', images, '类型:', typeof images)
    
    if (typeof images === 'string') {
        // 过滤掉空字符串
        const imagesArray = images.split(',').filter(img => img.trim() !== '')
        console.log('帖子详情页解析后的图片数组:', imagesArray)
        
        // 处理每个URL，确保路径正确
        return imagesArray.map((url, index) => {
            console.log(`帖子详情页处理图片[${index}]的URL: ${url}`)
            
            // 如果已经是完整URL，直接返回
            if (url.startsWith('http')) {
                return url
            }
            
            // 提取文件名
            const parts = url.split('/')
            const filename = parts[parts.length - 1]
            console.log(`帖子详情页提取的文件名: ${filename}`)
            
            // 使用正确的路径格式 - 添加应用上下文路径
            return `/mental/upload/${filename}`
        })
    } else if (Array.isArray(images)) {
        console.log('帖子详情页已是数组格式的图片:', images)
        return images
    }
    
    console.warn('帖子详情页无法处理的图片格式:', images)
    return []
}

// 添加图片加载错误处理函数
const handleImageError = (event, index) => {
    console.error(`帖子详情页图片加载失败, 索引: ${index}`)
    
    if (!post.value || !post.value.images) return
    
    // 尝试其他URL格式
    const imagesArray = getImagesArray(post.value.images)
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

// 根据图片数量获取网格类名
const getImagesGridClass = (images) => {
    const imagesArray = getImagesArray(images)
    const count = imagesArray.length
    console.log('图片网格类计算，图片数量:', count)
    if (count === 0) return ''
    return `grid-${Math.min(count, 4)}`
}

// 显示图片查看弹窗
const showFullImage = (imageUrl) => {
    currentViewingImage.value = imageUrl
    showImageViewer.value = true
}

// 关闭图片查看弹窗
const closeImageViewer = () => {
    showImageViewer.value = false
}

// 初始化
onMounted(() => {
    fetchPostDetail()
})
</script>

<style scoped>
.post-detail-container {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
    position: relative;
}

.post-card {
    border: 1px solid #eee;
    border-radius: 8px;
    padding: 24px;
    background-color: #fff;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
    margin-bottom: 20px;
}

.post-header {
    margin-bottom: 20px;
}

.post-title {
    margin: 0 0 10px 0;
    color: #333;
    font-size: 24px;
}

.post-meta {
    display: flex;
    gap: 15px;
    color: #888;
    font-size: 14px;
}

.post-content {
    color: #333;
    line-height: 1.6;
    margin-bottom: 20px;
    white-space: pre-line;
}

.post-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 20px;
}

.tag {
    color: #1890ff;
    font-size: 14px;
}

.post-actions {
    display: flex;
    gap: 25px;
    border-top: 1px solid #f0f0f0;
    padding-top: 15px;
}

.action-item {
    display: flex;
    align-items: center;
    gap: 6px;
    cursor: pointer;
    color: #666;
    transition: color 0.3s;
}

.action-item:hover {
    color: #1890ff;
}

.icon {
    font-size: 18px;
}

.icon-liked {
    color: #f56c6c;
}

.icon-favorited {
    color: #faad14;
}

.comments-section {
    margin-top: 30px;
}

.section-title {
    margin-bottom: 20px;
    color: #333;
}

.loading-state {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 200px;
    color: #888;
}

.back-button {
    position: fixed;
    bottom: 30px;
    right: 30px;
    background-color: #1890ff;
    color: white;
    padding: 10px 15px;
    border-radius: 20px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 5px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.2);
    z-index: 10;
}

.icon-back {
    font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .post-title {
        font-size: 20px;
    }
    
    .post-actions {
        flex-wrap: wrap;
        gap: 15px;
    }
    
    .back-button {
        bottom: 20px;
        right: 20px;
        padding: 8px 12px;
        font-size: 14px;
    }
}

/* 图片展示样式 */
.post-images {
    margin: 20px 0;
}

.images-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
}

.images-grid.grid-1 .image-item {
    width: 100%;
    max-width: 500px;
    height: auto;
    max-height: 400px;
}

.images-grid.grid-2 .image-item {
    width: calc(50% - 5px);
    height: 250px;
}

.images-grid.grid-3 .image-item, 
.images-grid.grid-4 .image-item {
    width: calc(50% - 5px);
    height: 200px;
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

/* 图片查看弹窗 */
.image-viewer-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0,0,0,0.8);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.image-viewer-content {
    position: relative;
    max-width: 90%;
    max-height: 90%;
}

.image-viewer-content img {
    max-width: 100%;
    max-height: 90vh;
    object-fit: contain;
}

.image-viewer-content .close-btn {
    position: absolute;
    top: 10px;
    right: 10px;
    background-color: rgba(0,0,0,0.5);
    color: white;
    border: none;
    border-radius: 4px;
    padding: 8px 16px;
    cursor: pointer;
}
</style>