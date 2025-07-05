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
  background: #f8fafc;
  gap: 32px;
  padding: 32px 0;
  box-sizing: border-box;
}
.session-list {
  width: 270px;
  min-width: 220px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(52,152,219,0.10);
  border: 1.5px solid #e0e6ed;
  padding: 0;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  position: relative;
  margin-left: 32px;
  margin-top: 0;
  margin-bottom: 0;
  margin-right: 0;
  overflow: hidden;
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
  background: linear-gradient(90deg, #3498db 60%, #4fc3f7 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(52,152,219,0.10);
  font-weight: 600;
  transition: background 0.2s;
}
.new-session-btn:hover {
  background: linear-gradient(90deg, #217dbb 60%, #3498db 100%);
}
.chatbot-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(52,152,219,0.10);
  border: 1.5px solid #e0e6ed;
  margin-right: 32px;
  overflow: hidden;
}
.chat-header {
  font-size: 26px;
  font-weight: bold;
  padding: 28px 0 18px 0;
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
  letter-spacing: 2px;
  background: linear-gradient(90deg, #eaf2fb 60%, transparent);
  color: #217dbb;
}
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 28px 32px 18px 32px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.chat-msg {
  display: flex;
  align-items: flex-end;
  margin-bottom: 0;
  gap: 8px;
}
.chat-msg.user {
  flex-direction: row-reverse;
}
.chat-msg .avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 8px;
  background: #eaf2fb;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(52,152,219,0.10);
}
.chat-msg .avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.bubble {
  max-width: 480px;
  padding: 14px 20px;
  border-radius: 18px 18px 18px 6px;
  font-size: 17px;
  line-height: 1.8;
  word-break: break-all;
  background: linear-gradient(90deg, #eaf2fb 60%, #fff 100%);
  color: #222;
  box-shadow: 0 2px 12px rgba(52,152,219,0.08);
  border: 1.5px solid #d0e6fa;
  margin-bottom: 2px;
  position: relative;
}
.chat-msg.user .bubble {
  background: linear-gradient(90deg, #d0e6fa 60%, #fff 100%);
  color: #222;
  border-radius: 18px 18px 6px 18px;
  border: 1.5px solid #b3d8f6;
}
.chat-input-area {
  display: flex;
  align-items: center;
  padding: 18px 32px 22px 32px;
  border-top: 1px solid #f0f0f0;
  background: #fff;
  gap: 12px;
}
.chat-input {
  flex: 1;
  height: 44px;
  border: 1.5px solid #d0e6fa;
  border-radius: 22px;
  padding: 0 18px;
  font-size: 17px;
  outline: none;
  margin-right: 0;
  background: #f8fafc;
  box-shadow: 0 2px 8px rgba(52,152,219,0.04);
  transition: border 0.2s;
}
.chat-input:focus {
  border: 1.5px solid #3498db;
}
.send-btn {
  background: linear-gradient(90deg, #3498db 60%, #4fc3f7 100%);
  color: #fff;
  border: none;
  border-radius: 22px;
  padding: 0 28px;
  height: 44px;
  font-size: 17px;
  cursor: pointer;
  transition: background 0.2s;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(52,152,219,0.10);
}
.send-btn:hover {
  background: linear-gradient(90deg, #217dbb 60%, #3498db 100%);
}
.report-area {
  padding: 32px;
  background: #f8fafc;
  border-radius: 18px;
  box-shadow: 0 2px 12px rgba(52,152,219,0.08);
  margin: 32px auto 0 auto;
  max-width: 600px;
  border: 1.5px solid #d0e6fa;
}
@media (max-width: 900px) {
  .chatbot-layout {
    flex-direction: column;
    gap: 12px;
    padding: 8px 0;
    height: auto;
  }
  .session-list {
    width: 100vw;
    min-width: 0;
    margin: 0 0 8px 0;
    border-radius: 16px;
    box-shadow: 0 2px 8px rgba(52,152,219,0.08);
  }
  .chatbot-container {
    margin: 0;
    border-radius: 16px;
    box-shadow: 0 2px 8px rgba(52,152,219,0.08);
    height: auto;
  }
  .chat-header {
    font-size: 20px;
    padding: 18px 0 10px 0;
  }
  .chat-messages {
    padding: 12px 8px 8px 8px;
  }
  .chat-input-area {
    padding: 10px 8px 12px 8px;
  }
  .report-area {
    padding: 12px;
    margin: 12px auto 0 auto;
    border-radius: 12px;
  }
}
</style>
