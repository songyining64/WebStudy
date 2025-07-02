import { ref, nextTick } from 'vue';
import MarkdownIt from 'markdown-it';

const md = new MarkdownIt();

export const userId = ref(1); // 实际项目应从登录信息获取
export const sessionList = ref([]); // [{id, title}]
export const currentSessionId = ref('');
export const messages = ref([]);
export const input = ref('');

export async function fetchSessionList() {
  const res = await fetch(`/api/ai/sessions?userId=${userId.value}`);
  const data = await res.json();
  if (data.code === 200) {
    // 获取每个会话的第一条消息
    const sessions = await Promise.all(
      data.data.map(async (sid) => {
        // 获取该会话的历史
        const res2 = await fetch(`/api/ai/history?userId=${userId.value}&sessionId=${sid}`);
        const data2 = await res2.json();
        let title = sid.slice(0, 8) + '...';
        if (data2.code === 200 && data2.data.records.length > 0) {
          const firstUserMsg = data2.data.records.find(r => r.userMessage);
          if (firstUserMsg && firstUserMsg.userMessage) {
            title = firstUserMsg.userMessage.slice(0, 8) + (firstUserMsg.userMessage.length > 8 ? '...' : '');
          }
        }
        return { id: sid, title };
      })
    );
    sessionList.value = sessions;
  }
}

export async function newSession() {
  const res = await fetch('/api/ai/session/new');
  const data = await res.json();
  if (data.code === 200) {
    currentSessionId.value = data.data;
    messages.value = [];
    await fetchSessionList();
  }
}

export async function switchSession(sessionId) {
  currentSessionId.value = sessionId;
  await fetchHistory(sessionId);
}

export async function fetchHistory(sessionId) {
  const res = await fetch(`/api/ai/history?userId=${userId.value}&sessionId=${sessionId}`);
  const data = await res.json();
  if (data.code === 200) {
    messages.value = [];
    data.data.records.forEach(item => {
      if (item.userMessage) {
        messages.value.push({ role: 'user', content: item.userMessage });
      }
      if (item.aiResponse) {
        const aiText = extractAIContent(item.aiResponse);
        messages.value.push({ role: 'ai', content: formatAIContent(aiText) });
      }
    });
    scrollToBottom();
  }
}

export async function sendMsg() {
  const text = input.value.trim();
  if (!text || !currentSessionId.value) return;
  messages.value.push({ role: 'user', content: text });
  input.value = '';
  scrollToBottom();

  try {
    const res = await fetch('/api/ai/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: text, userId: userId.value, sessionId: currentSessionId.value })
    });
    const data = await res.json();
    if (data.code === 200) {
      const aiText = extractAIContent(data.data);
      messages.value.push({ role: 'ai', content: formatAIContent(aiText) });
    } else {
      messages.value.push({ role: 'ai', content: data.msg || 'AI服务异常' });
    }
    scrollToBottom();
  } catch (e) {
    messages.value.push({ role: 'ai', content: 'AI服务暂时不可用，请稍后再试。' });
    scrollToBottom();
  }
}

export function scrollToBottom() {
  nextTick(() => {
    const el = document.querySelector('.chat-messages');
    if (el) el.scrollTop = el.scrollHeight;
  });
}

function formatAIContent(raw) {
  // 提取情绪 JSON
  const match = raw.match(/🌡️\\s*(\\{.*?\\})/s) || raw.match(/🌡️\\s*({.*?})/s);
  let emotionHtml = '';
  let text = raw;
  if (match) {
    try {
      const emotionObj = JSON.parse(match[1]);
      emotionHtml = `
        <div class="emotion-card">
          <span>情绪：<b>${emotionObj.emotion}</b></span>
          <span>强度：${emotionObj.intensity}</span>
          <span>触发因素：${(emotionObj.triggers || []).join('、')}</span>
          <span>危机等级：<b style="color:${emotionObj.risk_level === '红色' ? 'red' : emotionObj.risk_level === '黄色' ? '#e6a23c' : 'green'}">${emotionObj.risk_level}</b></span>
        </div>
      `;
    } catch (e) {
      // 解析失败，直接显示原始
      emotionHtml = `<div class="emotion-card">${match[0]}</div>`;
    }
    text = raw.replace(match[0], '').trim();
  }
  // markdown-it 渲染正文
  return emotionHtml + md.render(text);
}

function extractAIContent(raw) {
  // 如果是对象，直接取 content
  if (typeof raw === 'object' && raw.choices) {
    return raw.choices[0]?.message?.content || '';
  }
  // 如果是字符串，尝试解析为 JSON
  try {
    const obj = typeof raw === 'string' ? JSON.parse(raw) : raw;
    if (obj && obj.choices) {
      return obj.choices[0]?.message?.content || '';
    }
  } catch (e) {
    // 不是 JSON，直接返回原文
    return raw;
  }
  return raw;
}
