<template>
  <div class="card">
    <h2 class="card-title">
      <span class="icon">🕓</span> 历史记录
    </h2>
    <div class="history-section">
      <h3>情绪监测历史</h3>
      <ul class="history-list">
        <li>2024-05-01 09:30  您的情绪状态为：平稳</li>
        <li>2024-05-02 14:10  检测到轻度焦虑，已推荐呼吸放松训练</li>
        <li>2024-05-03 20:00  情绪状态为：积极</li>
      </ul>
      <h3>心理急救记录</h3>
      <ul class="history-list">
        <li>2024-05-02 14:12  使用了"心理急救"服务，获得了专业建议</li>
      </ul>
      <h3>资源推荐浏览</h3>
      <ul class="history-list">
        <li>2024-05-02 14:15  浏览了《自我情绪调节指南》</li>
        <li>2024-05-03 21:00  浏览了《压力管理技巧》</li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: 'History'
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
.history-section {
  margin-top: 18px;
}
.history-section h3 {
  font-size: 1.1rem;
  color: #2980b9;
  margin: 18px 0 8px 0;
}
.history-list {
  padding-left: 18px;
  margin-bottom: 10px;
  color: #444;
  font-size: 1rem;
}
.history-list li {
  margin-bottom: 4px;
}
</style> 