<template>
  <div class="chatbot-layout">
    <div class="session-list">
      <button class="new-session-btn" @click="back">返回聊天</button>
      <button class="new-session-btn" @click="router.push('/emotion-assessment-history')">历史记录</button>
      <div class="session-title">情绪评估问卷</div>
    </div>
    <div class="chatbot-container">
      <div class="chat-header">情波港·AI柴郡</div>
      <div class="chat-messages" ref="messagesRef">
        <div v-for="(msg, idx) in messages" :key="idx" :class="['chat-msg', msg.role]">
          <div class="avatar">
            <img v-if="msg.role==='ai'" src="/src/assets/cheshire.png" alt="AI" />
            <img v-else src="/src/assets/default-avatar.png" alt="用户" />
          </div>
          <div class="bubble" v-if="msg.role==='ai'" v-html="msg.content"></div>
          <div class="bubble" v-else>{{ msg.content }}</div>
        </div>
      </div>
      <div class="chat-input-area" v-if="!finished">
        <input
          v-model="input"
          @keyup.enter="send"
          class="chat-input"
          placeholder="请输入您的回答..."
          maxlength="200"
          :disabled="waiting"
        />
        <button @click="send" class="send-btn" :disabled="waiting">发送</button>
      </div>
      <div v-if="finished && report" class="report-area">
        <div class="chat-header">AI评估报告</div>
        <div class="bubble" v-html="report"></div>
        <button class="send-btn" @click="back">返回聊天</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';

const questions = ref([]);
const answers = ref([]);
const current = ref(0);
const input = ref('');
const messages = ref([]);
const finished = ref(false);
const report = ref('');
const waiting = ref(false);
const messagesRef = ref(null);
const router = useRouter();

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight;
    }
  });
}

onMounted(async () => {
  // 获取问卷
  const res = await fetch('/api/questionnaire');
  const data = await res.json();
  questions.value = data || [];
  if (questions.value.length > 0) {
    messages.value.push({ role: 'ai', content: questions.value[0].text });
    scrollToBottom();
  } else {
    messages.value.push({ role: 'ai', content: '问卷加载失败，请稍后重试。' });
  }
});

async function send() {
  if (!input.value.trim() || finished.value) return;
  // 用户回答
  messages.value.push({ role: 'user', content: input.value });
  answers.value.push(input.value);
  input.value = '';
  scrollToBottom();
  // 下一个问题
  current.value++;
  if (current.value < questions.value.length) {
    // AI发下一个问题
    setTimeout(() => {
      messages.value.push({ role: 'ai', content: questions.value[current.value].text });
      scrollToBottom();
    }, 400); // 模拟AI思考
  } else {
    // 全部答完，提交后端
    finished.value = true;
    waiting.value = true;
    messages.value.push({ role: 'ai', content: '正在生成评估报告，请稍候...' });
    scrollToBottom();
    const res = await fetch('/api/emotion-record/record', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: 1, // 实际项目用真实用户ID
        answers: answers.value,
      }),
    });
    const data = await res.json();
    console.log('后端返回:', data);
    report.value = data.data?.report || data.data || '评估完成，未返回报告。';
    waiting.value = false;
    scrollToBottom();
  }
}

function back() {
  router.push('/chatbot');
}
</script>

<style scoped>
/* 复用 ChatBot.vue 的样式 */
:global(body) {
  margin: 0;
  padding: 0;
  overflow: hidden;
  background: #f8fafc;
}
.chatbot-layout {
  display: flex;
  width: 100vw;
  height: 100vh;
}
.session-list {
  width: 260px;
  background: #f4f8fb;
  border-right: 1.5px solid #e0e6ed;
  padding: 0;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  position: relative;
}
.session-title {
  margin: 24px 16px 12px 16px;
  font-size: 18px;
  font-weight: bold;
  color: #217dbb;
  text-align: center;
}
.new-session-btn {
  margin: 24px 16px 12px 16px;
  padding: 10px 0;
  font-size: 16px;
  background: #3498db;
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
.chatbot-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #fff;
}
.chat-header {
  font-size: 26px;
  font-weight: bold;
  padding: 28px 0 18px 0;
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
  letter-spacing: 2px;
}
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 28px 32px;
  background: #f8fafc;
}
.chat-msg {
  display: flex;
  align-items: flex-end;
  margin-bottom: 22px;
}
.chat-msg.user {
  flex-direction: row-reverse;
}
.chat-msg .avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 12px;
  background: #eaf2fb;
  display: flex;
  align-items: center;
  justify-content: center;
}
.chat-msg .avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.bubble {
  max-width: 480px;
  padding: 16px 22px;
  border-radius: 18px;
  font-size: 18px;
  line-height: 1.8;
  word-break: break-all;
  background: #eaf2fb;
  color: #222;
}
.chat-msg.user .bubble {
  background: #d0e6fa;
  color: #222;
}
.chat-input-area {
  display: flex;
  align-items: center;
  padding: 20px 32px 28px 32px;
  border-top: 1px solid #f0f0f0;
  background: #fff;
}
.chat-input {
  flex: 1;
  height: 48px;
  border: 1.5px solid #d0e6fa;
  border-radius: 24px;
  padding: 0 20px;
  font-size: 18px;
  outline: none;
  margin-right: 16px;
  background: #f8fafc;
}
.send-btn {
  background: #3498db;
  color: #fff;
  border: none;
  border-radius: 24px;
  padding: 0 32px;
  height: 48px;
  font-size: 18px;
  cursor: pointer;
  transition: background 0.2s;
}
.send-btn:hover {
  background: #217dbb;
}
.report-area {
  padding: 32px;
}
</style>
