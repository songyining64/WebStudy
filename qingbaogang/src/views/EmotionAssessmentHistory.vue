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
.history-layout { max-width: 700px; margin: 40px auto; background: #fff; padding: 32px; border-radius: 8px; }
.record-card { border: 1px solid #e0e6ed; border-radius: 8px; padding: 16px; margin-bottom: 18px; }
.ai-report { background: #f6faff; padding: 10px; border-radius: 6px; margin-top: 6px; }
</style>
