<template>
  <div class="card">
    <h2 class="card-title">
      <span class="icon">🤖</span> 聊天机器人
    </h2>
    <div class="chatbot-section">
      <div class="chat-history">
        <div v-for="(msg, idx) in messages" :key="idx" :class="['chatbot-bubble', msg.role]">
          {{ msg.content }}
        </div>
      </div>
      <div class="chat-input-row">
        <input
            v-model="userInput"
            @keyup.enter="sendMessage"
            placeholder="请输入你的问题..."
            class="chat-input"
            :disabled="loading"
        />
        <button @click="sendMessage" :disabled="loading || !userInput.trim()" class="send-btn">
          {{ loading ? '发送中...' : '发送' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { request } from '@/utils/request'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'

const userStore = useUserStore()
const refs = storeToRefs(userStore)
console.log('userStore keys:', Object.keys(userStore))
console.log('storeToRefs(userStore):', refs)
console.log('userId:', refs.userId)

const sessionId = ref('')
const userInput = ref('')
const messages = ref([
  { role: 'bot', content: '欢迎来到心理健康助手，有什么可以帮您？' }
])
const loading = ref(false)

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    const history = document.querySelector('.chat-history')
    if (history) history.scrollTop = history.scrollHeight
  })
}

// 获取新会话ID
const getSessionId = async () => {
  try {
    const res = await request.get('/api/ai/session/new')
    if (res.code === 200) sessionId.value = res.data
  } catch (err) {
    messages.value.push({ role: 'bot', content: '无法获取会话ID，请刷新页面重试' })
  }
}

onMounted(async () => {
  await getSessionId()
  scrollToBottom()
})

const sendMessage = async () => {
  const content = userInput.value.trim()
  if (!content || !sessionId.value) return
  if (!refs.userId || !refs.userId.value) {
    messages.value.push({ role: 'bot', content: '未检测到用户ID，请重新登录。' })
    loading.value = false
    return
  }
  messages.value.push({ role: 'user', content })
  userInput.value = ''
  loading.value = true
  scrollToBottom()
  try {
    console.log('chat request params:', {
      message: content,
      sessionId: sessionId.value,
      userId: refs.userId.value
    })
    const res = await request.post('/api/ai/chat', {
      message: content,
      sessionId: sessionId.value,
      userId: refs.userId.value
    })
    let aiReply = ''
    if (res.code === 200) {
      // 1. 如果后端返回的 data 是对象（不是字符串），直接取 content
      if (res.data && typeof res.data === 'object' && res.data.choices) {
        aiReply = res.data.choices[0]?.message?.content || '[AI无回复]'
      }
      // 2. 如果 data 是 JSON 字符串，先解析再取 content
      else if (typeof res.data === 'string' && res.data.trim().startsWith('{')) {
        try {
          const parsed = JSON.parse(res.data)
          aiReply = parsed.choices?.[0]?.message?.content || res.data
        } catch (e) {
          aiReply = res.data
        }
      }
      // 3. 否则直接显示
      else {
        aiReply = res.data
      }
      // 去掉 content 前面的 JSON 情绪描述
      if (typeof aiReply === 'string' && aiReply.startsWith('🌡️')) {
        aiReply = aiReply.replace(/^🌡️[^{]*\{.*?\}\s*/s, '').trim()
      }
      messages.value.push({ role: 'bot', content: aiReply })
    } else {
      messages.value.push({ role: 'bot', content: res.msg || 'AI服务异常' })
    }
  } catch (err) {
    // 优先显示后端返回的msg或message字段
    const msg = err?.message || err?.msg || err?.data?.msg || 'AI服务异常';
    messages.value.push({ role: 'bot', content: msg });
  } finally {
    loading.value = false
    scrollToBottom()
  }
}
</script>

<style scoped>
.card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 6px 32px 0 rgba(52,152,219,0.10), 0 1.5px 6px 0 rgba(44,62,80,0.06);
  padding: 48px 56px;
  min-width: 340px;
  max-width: 420px;
  margin: 48px auto 0 auto;
}
.card-title {
  font-size: 1.7rem;
  color: #2980b9;
  font-weight: 700;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
}
.icon {
  font-size: 1.5em;
  margin-right: 12px;
}
.chatbot-section {
  margin-top: 18px;
  font-size: 1.05rem;
}
.chat-history {
  min-height: 180px;
  max-height: 320px;
  overflow-y: auto;
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.chatbot-bubble {
  display: inline-block;
  padding: 10px 16px;
  border-radius: 16px;
  max-width: 90%;
  word-break: break-all;
}
.chatbot-bubble.user {
  background: #eaf6ff;
  color: #2980b9;
  align-self: flex-end;
}
.chatbot-bubble.bot {
  background: #f3f9f6;
  color: #444;
  align-self: flex-start;
}
.chat-input-row {
  display: flex;
  gap: 8px;
}
.chat-input {
  flex: 1;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #d0d7de;
  font-size: 1rem;
}
.send-btn {
  padding: 0 18px;
  border-radius: 8px;
  background: #2980b9;
  color: #fff;
  border: none;
  font-weight: 600;
  cursor: pointer;
}
.send-btn:disabled {
  background: #b2bec3;
  cursor: not-allowed;
}
</style>