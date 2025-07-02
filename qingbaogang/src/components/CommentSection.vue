<template>
    <div class="comment-section">
        <!-- 发表评论 -->
        <div class="comment-form">
            <textarea 
                v-model="commentContent" 
                placeholder="写下你的评论..." 
                class="comment-input"
                :class="{ 'focused': isCommentFocused }"
                @focus="isCommentFocused = true"
                @blur="isCommentFocused = false"
            ></textarea>
            <div class="form-actions" v-show="isCommentFocused || commentContent">
                <button @click="resetComment" class="cancel-btn">取消</button>
                <button @click="submitComment" class="submit-btn" :disabled="!commentContent.trim()">发表评论</button>
            </div>
        </div>
        
        <!-- 评论列表 -->
        <div class="comments-list">
            <div v-if="comments.length === 0" class="empty-comments">
                还没有评论，快来发表第一条评论吧！
            </div>
            
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-header">
                    <span class="comment-author">{{ comment.userName }}</span>
                    <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
                </div>
                <div class="comment-content">{{ comment.content }}</div>
                <div class="comment-actions">
                    <div class="action-item" @click="toggleCommentLike(comment)">
                        <i :class="['icon', comment.liked ? 'icon-liked' : 'icon-like']">{{ comment.liked ? '❤️' : '🤍' }}</i>
                        <span>{{ comment.likeCount || 0 }}</span>
                    </div>
                    <div class="action-item" @click="replyToComment(comment)">
                        <i class="icon icon-reply">↩️</i>
                        <span>回复</span>
                    </div>
                    <div v-if="canDeleteComment(comment)" class="action-item delete" @click="deleteCommentItem(comment)">
                        <i class="icon icon-delete">🗑️</i>
                        <span>删除</span>
                    </div>
                </div>
                
                <!-- 回复区域 -->
                <div v-if="comment.showReplyForm" class="reply-form">
                    <textarea 
                        v-model="comment.replyContent" 
                        placeholder="回复..." 
                        class="reply-input"
                    ></textarea>
                    <div class="form-actions">
                        <button @click="cancelReply(comment)" class="cancel-btn">取消</button>
                        <button 
                            @click="submitReply(comment)" 
                            class="submit-btn" 
                            :disabled="!comment.replyContent || !comment.replyContent.trim()"
                        >
                            回复
                        </button>
                    </div>
                </div>
                
                <!-- 展示回复 -->
                <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
                    <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                        <div class="reply-header">
                            <span class="reply-author">{{ reply.userName }}</span>
                            <span class="reply-time">{{ formatDate(reply.createTime) }}</span>
                        </div>
                        <div class="reply-content">{{ reply.content }}</div>
                        <div class="reply-actions">
                            <div v-if="canDeleteComment(reply)" class="action-item delete" @click="deleteReply(reply, comment)">
                                <i class="icon icon-delete">🗑️</i>
                                <span>删除</span>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- 查看更多回复 -->
                <div v-if="comment.hasMoreReplies" class="view-more-replies" @click="loadMoreReplies(comment)">
                    查看更多回复
                </div>
            </div>
        </div>
        
        <!-- 加载更多评论 -->
        <div v-if="hasMoreComments" class="load-more" @click="loadMoreComments">
            加载更多评论
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import {
    createComment,
    getPostComments,
    getCommentReplies,
    deleteComment,
    likeComment,
    unlikeComment,
    isCommentLiked,
    getCommentLikeCount,
    replyToComment as sendCommentReply,
    getCommentDetail
} from '@/api/communityApi'

const props = defineProps({
    postId: {
        type: [Number, String],
        required: true
    }
})

const userStore = useUserStore()

// 响应式状态
const comments = ref([])
const commentContent = ref('')
const isCommentFocused = ref(false)
const commentPage = ref(1)
const commentSize = ref(10)
const hasMoreComments = ref(false)

// 获取评论列表
const fetchComments = async (page = 1) => {
    try {
        console.log(`获取评论列表，帖子ID=${props.postId}(${typeof props.postId})，页码=${page}`)
        const res = await getPostComments(props.postId)
        
        if (res.data && Array.isArray(res.data)) {
            console.log(`获取到${res.data.length}条评论`)
            const commentList = page === 1 ? [] : [...comments.value]
            
            // 处理每条评论，添加点赞状态、回复等信息
            const newComments = await Promise.all(res.data.map(async (comment) => {
                try {
                    console.log(`处理评论数据，评论ID=${comment.id}(${typeof comment.id})，内容=${comment.content?.substring(0, 20)}...`)
                    // 获取点赞状态和数量
                    const likedRes = await isCommentLiked(comment.id)
                    const likeCountRes = await getCommentLikeCount(comment.id)
                    
                    console.log(`评论${comment.id}点赞状态:`, likedRes.data, '点赞数:', likeCountRes.data)
                    
                    // 添加UI相关属性
                    return {
                        ...comment,
                        // 确保评论有用户名，如果没有则显示"匿名"
                        userName: comment.userName || comment.username || '匿名',
                        liked: likedRes.data || false,
                        likeCount: likeCountRes.data || 0,
                        showReplyForm: false,
                        replyContent: '',
                        replies: [],
                        hasMoreReplies: false,
                        replyPage: 1
                    }
                } catch (err) {
                    console.warn(`获取评论${comment.id}的点赞状态失败:`, err)
                    return {
                        ...comment,
                        // 确保评论有用户名，如果没有则显示"匿名"
                        userName: comment.userName || comment.username || '匿名',
                        liked: false,
                        likeCount: 0,
                        showReplyForm: false,
                        replyContent: '',
                        replies: [],
                        hasMoreReplies: false,
                        replyPage: 1
                    }
                }
            }))
            
            comments.value = [...commentList, ...newComments]
            hasMoreComments.value = newComments.length === commentSize.value
            commentPage.value = page
            
            // 默认加载每个评论的前几条回复
            comments.value.forEach(comment => {
                loadReplies(comment)
            })
        }
    } catch (error) {
        console.error('获取评论失败:', error)
    }
}

// 加载更多评论
const loadMoreComments = () => {
    fetchComments(commentPage.value + 1)
}

// 加载评论回复
const loadReplies = async (comment, page = 1) => {
    try {
        // 处理评论ID
        let commentId = comment.id.toString();
        
        // 检查是否是雪花算法ID (长度通常为18-19位)
        if (commentId.length >= 18) {
            console.log(`评论ID看起来是雪花ID: ${commentId}，尝试多种格式获取回复`)
            
            // 尝试用不同格式获取回复
            try {
                // 先尝试以数字格式获取回复
                const numericId = parseInt(commentId, 10)
                console.log(`尝试用数字格式获取回复: ${numericId}`)
                
                const numericResponse = await getCommentReplies(numericId)
                if (numericResponse.code === 200 && Array.isArray(numericResponse.data)) {
                    console.log(`使用数字格式成功获取回复: ${numericResponse.data.length}条`)
                    processReplies(comment, numericResponse.data, page)
                    return
                } else {
                    console.log(`数字格式获取失败，尝试字符串格式`)
                }
            } catch (err) {
                console.error(`数字格式获取回复失败:`, err)
            }
        } else {
            // 对于短ID，尝试转换为数字
            try {
                commentId = parseInt(commentId, 10)
                console.log(`短评论ID转为数字: ${commentId}`)
            } catch (err) {
                console.error(`转换短评论ID为数字时出错:`, err)
                // 保持原始格式
            }
        }
            
        console.log(`加载评论回复，评论ID=${commentId}(${typeof commentId})，页码=${page}`)
        
        const res = await getCommentReplies(commentId)
        if (res.code === 200 && Array.isArray(res.data)) {
            console.log(`获取到${res.data.length}条回复`)
            processReplies(comment, res.data, page)
        } else {
            console.warn(`获取评论${comment.id}的回复失败或回复为空`, res)
        }
    } catch (error) {
        console.error(`获取评论ID=${comment.id}的回复失败:`, error)
        // 不显示错误提示，避免影响用户体验
    }
}

// 处理回复数据
const processReplies = (comment, repliesData, page) => {
            // 处理每条回复，添加用户信息和UI相关属性
    const replies = repliesData.map(reply => {
        // 处理回复ID，防止长ID格式问题
        const replyId = reply.id.toString()
            
        console.log(`处理回复数据，回复ID=${reply.id}(${typeof reply.id})，处理后ID=${replyId}(${typeof replyId})，内容=${reply.content?.substring(0, 20)}...`)
        
                return {
                    ...reply,
            // 使用处理后的ID
            id: replyId,
                    // 确保回复有用户名，如果没有则显示"匿名"
                    userName: reply.userName || reply.username || '匿名'
                }
            })
            
            const replyList = page === 1 ? [] : [...(comment.replies || [])]
            comment.replies = [...replyList, ...replies]
            comment.hasMoreReplies = replies.length === 5 // 假设每页5条回复
            comment.replyPage = page
    
    console.log(`成功加载评论${comment.id}的回复，当前共有${comment.replies.length}条回复`)
}

// 加载更多回复
const loadMoreReplies = (comment) => {
    loadReplies(comment, comment.replyPage + 1)
}

// 提交评论
const submitComment = async () => {
    if (!commentContent.value.trim()) return
    
    try {
        // 确保postId使用正确的格式
        const processedPostId = typeof props.postId === 'number' && props.postId.toString().length > 10 
            ? props.postId.toString() 
            : props.postId
            
        console.log(`准备提交评论，帖子ID=${props.postId}(${typeof props.postId}) 转换后=${processedPostId}(${typeof processedPostId})`)
        
        // 构建评论数据，只包含必要字段
        const commentData = {
            postId: processedPostId,
            content: commentContent.value,
            userId: userStore.userId
        }
        
        console.log('发表评论，数据:', commentData)
        const res = await createComment(commentData)
        console.log('评论响应:', res)
        
        if (res.code === 200 && res.data) {
            // 重置输入框
            resetComment()
            
            // 刷新评论列表
            fetchComments()
            
            // 通知用户
            alert('评论发表成功！')
        } else {
            throw new Error(res.msg || '评论发表失败')
        }
    } catch (error) {
        console.error('发表评论失败:', error)
        if (error.response) {
            console.error('错误响应:', error.response)
        }
        alert(error.message || '评论发表失败，请稍后重试')
    }
}

// 重置评论输入框
const resetComment = () => {
    commentContent.value = ''
    isCommentFocused.value = false
}

// 回复评论
const replyToComment = (comment) => {
    // 先关闭所有其他回复框
    comments.value.forEach(c => {
        if (c.id !== comment.id) {
            c.showReplyForm = false
        }
    })
    
    // 切换当前评论的回复框
    comment.showReplyForm = !comment.showReplyForm
    if (comment.showReplyForm) {
        comment.replyContent = ''
    }
}

// 提交回复
const submitReply = async (comment) => {
    if (!comment.replyContent.trim()) return
    
    try {
        // 确保parentId使用正确的格式 (长ID保持字符串格式)
        let parentId = comment.id.toString()
        
        // 检查是否是雪花算法ID (长度通常为18-19位)
        if (parentId.length >= 18) {
            console.log(`父评论ID看起来是雪花ID: ${parentId}`)
            
            // 尝试转换为数字类型 (有些后端期望数字类型)
            try {
                // 尝试获取评论详情以验证评论是否存在
                const checkRes = await getCommentDetail(parentId)
                console.log(`评论${parentId}详情验证结果:`, checkRes)
                
                // 如果评论存在但API接受的是数字ID
                if (checkRes.code !== 200) {
                    console.log(`尝试以数字形式验证评论:`, parseInt(parentId, 10))
                    const numericCheckRes = await getCommentDetail(parseInt(parentId, 10))
                    console.log(`数字格式评论ID验证结果:`, numericCheckRes)
                    
                    // 如果数字格式能成功获取，则使用数字格式
                    if (numericCheckRes.code === 200) {
                        parentId = parseInt(parentId, 10)
                        console.log(`使用数字格式的父评论ID: ${parentId}`)
                    }
                }
            } catch (err) {
                console.error(`验证评论${parentId}是否存在时出错:`, err)
            }
        } else {
            // 对于短ID，尝试转换为数字
            try {
                parentId = parseInt(parentId, 10)
                console.log(`短评论ID转为数字: ${parentId}`)
            } catch (err) {
                console.error(`转换短评论ID为数字时出错:`, err)
                // 保持原始格式
            }
        }
        
        console.log(`准备提交回复，父评论ID=${comment.id}(${typeof comment.id}) 转换后=${parentId}(${typeof parentId})`)
        
        // 构建回复数据，只包含必要字段
        const replyData = {
            postId: props.postId,
            content: comment.replyContent,
            parentId: parentId, // 使用转换后的ID
            userId: userStore.userId
        }
        
        console.log(`回复评论，评论ID=${comment.id}(${typeof comment.id})，数据:`, replyData)
        const res = await createComment(replyData)
        console.log('回复响应:', res)
        
        if (res.code === 200 && res.data) {
            // 隐藏回复框并清空回复内容
            comment.showReplyForm = false
            comment.replyContent = ''
            
            // 重新加载回复
            loadReplies(comment)
            
            // 通知用户
            alert('回复成功！')
        } else {
            throw new Error(res.msg || '回复失败')
        }
    } catch (error) {
        console.error('回复评论失败:', error)
        if (error.response) {
            console.error('错误响应:', error.response)
        }
        alert(error.message || '回复失败，请稍后重试')
    }
}

// 取消回复
const cancelReply = (comment) => {
    comment.showReplyForm = false
    comment.replyContent = ''
}

// 删除评论
const deleteCommentItem = async (comment) => {
    if (!confirm('确定要删除这条评论吗？')) return
    
    try {
        // 确保commentId使用正确的格式
        const commentId = typeof comment.id === 'number' && comment.id.toString().length > 10
            ? comment.id.toString()
            : comment.id
            
        console.log(`删除评论，原始评论ID=${comment.id}(${typeof comment.id})，处理后ID=${commentId}(${typeof commentId})，用户ID=${userStore.userId}`)
        
        const res = await deleteComment(commentId, userStore.userId)
        console.log('删除评论响应:', res)
        
        if (res && res.code === 200) {
        // 从列表中移除
        const index = comments.value.findIndex(c => c.id === comment.id)
        if (index !== -1) {
            comments.value.splice(index, 1)
                alert('评论已删除')
            }
        } else {
            throw new Error(res?.msg || '删除失败')
        }
    } catch (error) {
        console.error('删除评论失败:', error)
        alert(error.message || '删除失败，请稍后重试')
    }
}

// 删除回复
const deleteReply = async (reply, parentComment) => {
    if (!confirm('确定要删除这条回复吗？')) return
    
    try {
        // 确保replyId使用正确的格式
        const replyId = typeof reply.id === 'number' && reply.id.toString().length > 10
            ? reply.id.toString()
            : reply.id
            
        console.log(`删除回复，原始回复ID=${reply.id}(${typeof reply.id})，处理后ID=${replyId}(${typeof replyId})，父评论ID=${parentComment.id}，用户ID=${userStore.userId}`)
        
        const res = await deleteComment(replyId, userStore.userId)
        console.log('删除回复响应:', res)
        
        if (res && res.code === 200) {
        // 从回复列表中移除
        if (parentComment.replies) {
            const index = parentComment.replies.findIndex(r => r.id === reply.id)
            if (index !== -1) {
                parentComment.replies.splice(index, 1)
                    alert('回复已删除')
                }
            }
        } else {
            throw new Error(res?.msg || '删除失败')
        }
    } catch (error) {
        console.error('删除回复失败:', error)
        alert(error.message || '删除失败，请稍后重试')
    }
}

// 点赞/取消点赞评论
const toggleCommentLike = async (comment) => {
    if (!userStore.isLoggedIn) {
        alert('请先登录后再点赞')
        return
    }
    
    try {
        const commentId = comment.id
        console.log(`处理评论点赞操作，评论ID=${commentId}(${typeof commentId})，当前点赞状态=${comment.liked}`)
        
        const originalLiked = comment.liked
        const originalCount = comment.likeCount || 0
        
        // 先更新UI
        comment.liked = !originalLiked
        comment.likeCount = originalLiked ? Math.max(0, originalCount - 1) : originalCount + 1
        
        // 发送请求
        let response
        try {
            // 检查评论是否存在并可访问
            try {
                await getCommentDetail(commentId)
                console.log(`评论${commentId}存在，可以操作`)
            } catch (error) {
                console.warn(`无法验证评论${commentId}是否存在:`, error)
                // 继续操作，因为后端会做最终检查
            }
            
            // 尝试转换ID格式 (对于长ID)
            let processedId = commentId
            if (typeof commentId === 'string' || typeof commentId === 'number') {
                const idStr = commentId.toString()
                if (idStr.length >= 18) {
                    console.log(`评论ID看起来是雪花ID: ${idStr}，尝试用数字格式`)
                    try {
                        processedId = parseInt(idStr, 10)
                    } catch (e) {
                        console.error(`转换评论ID${idStr}为数字出错:`, e)
                    }
                } else {
                    // 短ID尝试数字格式
                    try {
                        processedId = parseInt(idStr, 10)
                        console.log(`短评论ID转为数字: ${processedId}`)
                    } catch (e) {
                        console.error(`转换短评论ID为数字出错:`, e)
                    }
                }
            }
            
            if (originalLiked) {
                console.log(`准备发送取消评论点赞请求，评论ID=${processedId}(${typeof processedId})`)
                response = await unlikeComment(processedId)
                console.log('取消评论点赞响应:', response)
            } else {
                console.log(`准备发送评论点赞请求，评论ID=${processedId}(${typeof processedId})`)
                response = await likeComment(processedId)
                console.log('评论点赞响应:', response)
            }
            
            // 如果API返回错误，可能需要使用另一种ID格式
            if (response.code !== 200 && typeof commentId === 'string' || typeof commentId === 'number') {
                const idStr = commentId.toString()
                if (idStr.length >= 18) {
                    console.log(`API失败，尝试使用字符串格式ID重试`)
                    
                    // 如果数字格式失败，尝试字符串格式
                    if (typeof processedId === 'number') {
        if (originalLiked) {
                            response = await unlikeComment(idStr)
                            console.log('(字符串ID)取消评论点赞响应:', response)
        } else {
                            response = await likeComment(idStr)
                            console.log('(字符串ID)评论点赞响应:', response)
                        }
                    }
                }
            }
            
            // 延时检查评论点赞状态
            setTimeout(async () => {
                try {
                    const statusRes = await isCommentLiked(commentId)
                    console.log(`点赞后评论状态检查，评论ID=${commentId}，数据库中的状态=${statusRes.data}`)
                    
                    // 如果状态不一致，需要提醒并同步
                    if (statusRes.data !== comment.liked) {
                        console.warn('评论点赞状态与数据库不一致，可能需要刷新页面')
                        // 同步状态
                        comment.liked = statusRes.data
                        
                        // 重新获取点赞数
                        try {
                            const countRes = await getCommentLikeCount(commentId)
                            if (countRes.code === 200) {
                                comment.likeCount = countRes.data
                                console.log(`评论${commentId}点赞数更新为:`, countRes.data)
                            }
                        } catch (err) {
                            console.error('获取评论点赞数失败:', err)
                        }
                    }
                } catch (err) {
                    console.error('检查评论点赞状态失败:', err)
                }
            }, 500)
        } catch (apiError) {
            console.error('评论点赞API请求失败:', apiError)
            // 特殊处理重复点赞的情况
            if (apiError.message && apiError.message.includes('已点赞')) {
                console.warn('已经点赞过该评论')
                return
            }
            throw apiError
        }
    } catch (error) {
        console.error('评论点赞操作失败:', error)
        
        // 恢复原始状态
        comment.liked = !comment.liked
        comment.likeCount = comment.liked ? comment.likeCount + 1 : Math.max(0, comment.likeCount - 1)
        
        // 错误提示
        if (error.message && (error.message.includes('已点赞') || error.message.includes('未点赞'))) {
            // 已点赞/未点赞的情况，不显示错误提示
            return
        } else if (error.response && (error.response.status === 404 || error.response.status === 400)) {
            alert('评论点赞功能暂未开放，请稍后再试')
        } else {
            alert('操作失败，请稍后重试')
        }
    }
}

// 判断用户是否可以删除评论
const canDeleteComment = (comment) => {
    // 用户只能删除自己的评论，或者管理员可以删除任何评论
    return userStore.isLoggedIn && (
        userStore.userId === comment.userId || 
        userStore.isAdmin
    )
}

// 格式化日期
const formatDate = (dateString) => {
    if (!dateString) return ''
    
    const now = new Date()
    const commentDate = new Date(dateString)
    const diffMs = now - commentDate
    const diffSec = Math.floor(diffMs / 1000)
    const diffMin = Math.floor(diffSec / 60)
    const diffHour = Math.floor(diffMin / 60)
    const diffDay = Math.floor(diffHour / 24)
    
    if (diffSec < 60) return '刚刚'
    if (diffMin < 60) return `${diffMin}分钟前`
    if (diffHour < 24) return `${diffHour}小时前`
    if (diffDay < 30) return `${diffDay}天前`
    
    return commentDate.toLocaleDateString('zh-CN')
}

// 监听postId变化
watch(() => props.postId, () => {
    comments.value = []
    commentPage.value = 1
    fetchComments()
})

// 初始化
onMounted(() => {
    fetchComments()
})
</script>

<style scoped>
.comment-section {
    margin-top: 10px;
}

.comment-form {
    margin-bottom: 20px;
}

.comment-input {
    width: 100%;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 6px;
    resize: vertical;
    min-height: 80px;
    transition: all 0.3s;
    box-sizing: border-box;
}

.comment-input.focused {
    border-color: #1890ff;
    box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 10px;
}

.cancel-btn {
    padding: 6px 12px;
    background-color: #f0f0f0;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    color: #666;
}

.submit-btn {
    padding: 6px 12px;
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

.comments-list {
    margin-top: 20px;
}

.comment-item {
    padding: 15px 0;
    border-bottom: 1px solid #f0f0f0;
}

.comment-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
}

.comment-author {
    font-weight: 500;
    color: #333;
}

.comment-time {
    color: #999;
    font-size: 12px;
}

.comment-content {
    margin-bottom: 10px;
    line-height: 1.5;
    color: #333;
    white-space: pre-line;
}

.comment-actions {
    display: flex;
    gap: 15px;
}

.action-item {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #999;
    cursor: pointer;
    font-size: 13px;
}

.action-item:hover {
    color: #1890ff;
}

.action-item.delete:hover {
    color: #f5222d;
}

.icon {
    font-size: 14px;
}

.icon-liked {
    color: #f56c6c;
}

.reply-form {
    margin: 10px 0 10px 20px;
    padding-left: 10px;
    border-left: 2px solid #f0f0f0;
}

.reply-input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    resize: vertical;
    min-height: 60px;
    box-sizing: border-box;
}

.replies-list {
    margin: 10px 0 0 20px;
    padding-left: 10px;
    border-left: 2px solid #f0f0f0;
}

.reply-item {
    padding: 10px 0;
    border-bottom: 1px dashed #f0f0f0;
}

.reply-item:last-child {
    border-bottom: none;
}

.reply-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 5px;
}

.reply-author {
    font-weight: 500;
    color: #333;
    font-size: 13px;
}

.reply-time {
    color: #999;
    font-size: 12px;
}

.reply-content {
    margin-bottom: 5px;
    line-height: 1.4;
    color: #333;
    font-size: 13px;
}

.reply-actions {
    display: flex;
    justify-content: flex-end;
}

.view-more-replies {
    margin: 5px 0 0 20px;
    padding-left: 10px;
    color: #1890ff;
    cursor: pointer;
    font-size: 13px;
}

.view-more-replies:hover {
    text-decoration: underline;
}

.load-more {
    text-align: center;
    margin-top: 20px;
    color: #1890ff;
    cursor: pointer;
    padding: 10px;
}

.load-more:hover {
    background-color: #f0f8ff;
    border-radius: 4px;
}

.empty-comments {
    text-align: center;
    padding: 20px 0;
    color: #999;
    font-style: italic;
}
</style>