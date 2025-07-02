<template>
  <div class="chatbot-container">
    <div class="chat-header">情波港·AI柴郡</div>
    <div class="chat-messages" ref="messagesRef">
      <div v-for="(msg, idx) in messages" :key="idx" :class="['chat-msg', msg.role]">
        <div class="avatar">
          <img v-if="msg.role==='ai'" src="/src/assets/cheshire.png" alt="AI" />
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
const isEvaluating = ref(false); // 是否在情绪评估流程
const questions = ref([]); // 问卷题目
const currentQuestionIndex = ref(0);
const answers = ref([]);
const evaluationResult = ref(null); // 评估结果
const input = ref('');
const messages = ref([
  { role: 'ai', content: '您好，我是情波港AI柴郡，有什么可以帮您？' }
]);
const messagesRef = ref(null);

async function startEvaluation() {
  isEvaluating.value = true;
  // 获取题目（从后端）
  const res = await fetch('/api/questionnaire');
  questions.value = await res.json();
  currentQuestionIndex.value = 0;
  answers.value = [];
  askNextQuestion();
}

function askNextQuestion() {
  if (currentQuestionIndex.value < questions.value.length) {
    const q = questions.value[currentQuestionIndex.value];
    messages.value.push({ role: 'ai', content: q.text + (q.options ? '\n' + q.options.join(' / ') : '') });
  } else {
    evaluateEmotion();
  }
}

async function sendMsg() {
  const text = input.value.trim();
  if (!text) return;
  messages.value.push({ role: 'user', content: text });
  input.value = '';

  // 进入问卷流程
  if (!isEvaluating.value && text === '可以') {
    startEvaluation();
    return;
  }

  // 问卷流程中
  if (isEvaluating.value) {
    answers.value.push(text);
    currentQuestionIndex.value++;
    askNextQuestion();
    return;
  }

  // 普通AI聊天
  try {
    const res = await fetch('/api/ai/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: text, userId: 1, sessionId: 'default' }) // userId/sessionId按实际传
    });
    const data = await res.json();
    if (data.code === 200) {
      messages.value.push({ role: 'ai', content: data.data });
    } else {
      messages.value.push({ role: 'ai', content: data.msg || 'AI服务异常' });
    }
    scrollToBottom();
  } catch (e) {
    messages.value.push({ role: 'ai', content: 'AI服务暂时不可用，请稍后再试。' });
    scrollToBottom();
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight;
    }
  });
}

async function evaluateEmotion() {
  isEvaluating.value = false;
  // 提交到后端评估
  const res = await fetch('/api/questionnaire/evaluate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ answers: answers.value })
  });
  const data = await res.json();
  evaluationResult.value = data.result;
  messages.value.push({ role: 'ai', content: data.result });

  // 可选：记录到后端
  fetch('/api/emotion-record/record', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      userId: 1, // 必须有且为数字
      answers: answers.value, // 建议用 JSON.stringify(answers.value)
      score: data.score,
      result: data.result,
      emotionType: data.result // 或自定义类型
    })
  });
}

messages.value.push({
  role: 'ai',
  content: '您好，我想问您几个问题，帮您了解下当前的情绪状态，可以吗？（回复"可以"开始）'
});
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