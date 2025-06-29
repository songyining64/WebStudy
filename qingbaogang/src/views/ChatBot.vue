<template>
  <div class="chatbot-container">
    <div class="chat-header">情波港·AI聊天机器人</div>
    <div class="chat-messages" ref="messagesRef">
      <div v-for="(msg, idx) in messages" :key="idx" :class="['chat-msg', msg.role]">
        <div class="avatar">
          <img v-if="msg.role==='ai'" src="/src/assets/logo.jpg" alt="AI" />
          <img v-else src="/src/assets/default-avatar.png" alt="用户" />
        </div>
        <div class="bubble">
          {{ msg.content }}
        </div>
      </div>
    </div>
    <div class="chat-input-area">
      <input
        v-model="input"
        @keyup.enter="sendMsg"
        class="chat-input"
        placeholder="请输入您的问题..."
        maxlength="200"
      />
      <button @click="sendMsg" class="send-btn">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue';

const input = ref('');
const messages = ref([
  { role: 'ai', content: '您好，我是情波港AI助手，有什么可以帮您？' }
]);
const messagesRef = ref(null);

function sendMsg() {
  const text = input.value.trim();
  if (!text) return;
  messages.value.push({ role: 'user', content: text });
  input.value = '';
  // 模拟AI回复
  setTimeout(() => {
    const aiReplies = [
      '我明白您的感受，想聊聊具体发生了什么吗？',
      '请深呼吸，慢慢说，我一直在这里。',
      '遇到困难时，倾诉是很好的方式。',
      '您可以试试写下自己的情绪，或者听一段舒缓的音乐。',
      '感谢您的信任，有什么都可以告诉我。'
    ];
    const reply = aiReplies[Math.floor(Math.random() * aiReplies.length)];
    messages.value.push({ role: 'ai', content: reply });
    scrollToBottom();
  }, 800);
  scrollToBottom();
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight;
    }
  });
}
</script>

<style scoped>
.chatbot-container {
  max-width: 420px;
  margin: 40px auto;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 6px 32px 0 rgba(52,152,219,0.10), 0 1.5px 6px 0 rgba(44,62,80,0.06);
  display: flex;
  flex-direction: column;
  height: 540px;
}
.chat-header {
  font-size: 20px;
  font-weight: bold;
  padding: 18px 0 12px 0;
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
  letter-spacing: 2px;
}
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 18px 16px;
  background: #f8fafc;
}
.chat-msg {
  display: flex;
  align-items: flex-end;
  margin-bottom: 16px;
}
.chat-msg.user {
  flex-direction: row-reverse;
}
.chat-msg .avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 8px;
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
  max-width: 220px;
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 15px;
  line-height: 1.7;
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
  padding: 14px 16px 18px 16px;
  border-top: 1px solid #f0f0f0;
  background: #fff;
}
.chat-input {
  flex: 1;
  height: 36px;
  border: 1px solid #d0e6fa;
  border-radius: 18px;
  padding: 0 14px;
  font-size: 15px;
  outline: none;
  margin-right: 10px;
  background: #f8fafc;
}
.send-btn {
  background: #3498db;
  color: #fff;
  border: none;
  border-radius: 18px;
  padding: 0 18px;
  height: 36px;
  font-size: 15px;
  cursor: pointer;
  transition: background 0.2s;
}
.send-btn:hover {
  background: #217dbb;
}
</style> 