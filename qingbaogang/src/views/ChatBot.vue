<template>
  <div class="chatbot-layout">
    <div class="session-list">
      <button class="new-session-btn" @click="newSession">+ 新建会话</button>
      <ul>
        <li
          v-for="s in sessionList"
          :key="s.id"
          :class="{active: s.id === currentSessionId}"
          @click="switchSession(s.id)"
        >
          {{ s.title }}
        </li>
      </ul>
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
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import * as logic from './ChatBot.logic.js';

const {
  userId,
  sessionList,
  currentSessionId,
  messages,
  input,
  newSession,
  switchSession,
  sendMsg,
  scrollToBottom,
  fetchSessionList
} = logic;

onMounted(async () => {
  await fetchSessionList();
  if (sessionList.value.length > 0) {
    await switchSession(sessionList.value[0].id);
  } else {
    await newSession();
  }
});
</script>

<style scoped>
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
  padding: 0 0 0 0;
  display: flex;
  flex-direction: column;
  align-items: stretch;
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
.session-list ul {
  list-style: none;
  padding: 0 0 0 0;
  margin: 0;
  flex: 1;
  overflow-y: auto;
}
.session-list li {
  padding: 14px 18px;
  cursor: pointer;
  border-bottom: 1px solid #e0e6ed;
  font-size: 15px;
  color: #333;
  transition: background 0.2s;
}
.session-list li.active,
.session-list li:hover {
  background: #eaf2fb;
  color: #217dbb;
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
.bubble :deep(pre) {
  background: #f4f4f4;
  padding: 10px;
  border-radius: 6px;
  overflow-x: auto;
  font-size: 15px;
}
.bubble :deep(code) {
  background: #f4f4f4;
  padding: 2px 6px;
  border-radius: 4px;
  color: #c7254e;
  font-size: 95%;
}
.bubble :deep(ul), .bubble :deep(ol) {
  margin: 8px 0 8px 20px;
}
.bubble :deep(strong) {
  font-weight: bold;
}
.bubble :deep(em) {
  font-style: italic;
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
.bubble .emotion-card {
  background: #f6faff;
  border-left: 5px solid #3498db;
  border-radius: 8px;
  padding: 10px 16px;
  margin-bottom: 10px;
  font-size: 15px;
  color: #222;
  display: flex;
  flex-wrap: wrap;
  gap: 18px 24px;
}
.bubble .emotion-card span {
  margin-right: 18px;
  min-width: 90px;
  display: inline-block;
}
</style>