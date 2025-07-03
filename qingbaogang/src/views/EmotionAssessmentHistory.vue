<template>
  <div class="history-layout">
    <h2>历史情绪评估记录</h2>
    <div v-if="records.length === 0">暂无历史记录</div>
    <div v-for="rec in records" :key="rec.id" class="record-card">
      <div><b>评估时间：</b>{{ rec.recordTime }}</div>
      <div><b>问卷答案：</b>{{ rec.content }}</div>
      <div><b>AI评估报告：</b>
        <div class="ai-report" v-html="rec.suggestions"></div>
      </div>
    </div>
    <button @click="back">返回</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const records = ref([]);
const router = useRouter();

onMounted(async () => {
  // 假设你有 userId
  const userId = 1; // 实际项目用真实用户ID
  const res = await fetch(`/api/emotion-record/user/${userId}`);
  const data = await res.json();
  records.value = data.data || [];
});

function back() {
  router.push('/emotion-assessment');
}
</script>

<style scoped>
.history-layout {
  max-width: 700px;
  margin: 40px auto;
  background: #fff;
  padding: 32px;
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(52,152,219,0.10);
  border: 1.5px solid #e0e6ed;
}
.record-card {
  border: 1.5px solid #d0e6fa;
  border-radius: 14px;
  padding: 18px 20px;
  margin-bottom: 18px;
  background: linear-gradient(90deg, #f8fafc 60%, #fff 100%);
  box-shadow: 0 2px 12px rgba(52,152,219,0.08);
}
.ai-report {
  background: #f6faff;
  padding: 10px 16px;
  border-radius: 8px;
  margin-top: 6px;
  border-left: 5px solid #3498db;
  color: #217dbb;
}
button {
  background: linear-gradient(90deg, #3498db 60%, #4fc3f7 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  padding: 10px 32px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 18px;
  box-shadow: 0 2px 8px rgba(52,152,219,0.10);
  transition: background 0.2s;
}
button:hover {
  background: linear-gradient(90deg, #217dbb 60%, #3498db 100%);
}
h2 {
  color: #217dbb;
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 24px;
  border-left: 7px solid #3498db;
  padding-left: 14px;
  background: linear-gradient(90deg, #eaf2fb 60%, transparent);
  border-radius: 8px;
}
@media (max-width: 700px) {
  .history-layout {
    padding: 12px;
    border-radius: 12px;
  }
  .record-card {
    padding: 10px 6px;
    border-radius: 8px;
  }
  h2 {
    font-size: 1.1rem;
    padding-left: 6px;
    border-radius: 4px;
  }
  button {
    padding: 8px 12px;
    font-size: 15px;
    border-radius: 8px;
  }
}
</style>
