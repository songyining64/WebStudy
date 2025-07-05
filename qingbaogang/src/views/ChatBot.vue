<template>
  <div class="chatbot-layout">
    <div class="session-list">
      <button class="new-session-btn" @click="newSession">+ 新建会话</button>
      <button class="emotion-eval-btn" @click="startEmotionAssessment">情绪评估</button>
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
    <EmotionAssessmentDialog
      :visible="showAssessmentDialog"
      :questions="questions"
      @close="showAssessmentDialog = false"
    />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import * as logic from './ChatBot.logic.js';

import { useRouter } from 'vue-router';

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
  fetchSessionList,
} = logic;

const showAssessmentDialog = ref(false);
const questions = ref([]);
const router = useRouter();

onMounted(async () => {
  await fetchSessionList();
  if (sessionList.value.length > 0) {
    await switchSession(sessionList.value[0].id);
  } else {
    await newSession();
  }
});

function startEmotionAssessment() {
  router.push('/emotion-assessment');
}
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
  padding: 0 0 0 0;
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
.emotion-eval-btn {
  position: absolute;
  left: 20px;
  bottom: 30px;
  width: 200px;
  background: linear-gradient(90deg, #ffb347 60%, #ffd580 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  padding: 12px 0;
  font-size: 16px;
  cursor: pointer;
  z-index: 10;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(255,179,71,0.10);
  transition: background 0.2s;
}
.emotion-eval-btn:hover {
  background: linear-gradient(90deg, #ffa500 60%, #ffb347 100%);
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
  transition: background 0.2s, color 0.2s, box-shadow 0.2s;
  border-radius: 0;
  margin: 0 8px;
}
.session-list li.active,
.session-list li:hover {
  background: #eaf2fb;
  color: #217dbb;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(52,152,219,0.08);
  border-radius: 10px;
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
}
</style>