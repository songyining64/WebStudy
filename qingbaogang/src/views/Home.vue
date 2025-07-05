<template>
  <div>
    <div class="home-bg"></div>
    <div class="home-main">
      <!-- 轮播图区域 -->
      <div class="overlap-carousel-area">
        <div class="overlap-carousel-wrapper">
          <div class="overlap-carousel-track">
            <div
              v-for="(img, idx) in images"
              :key="img"
              class="overlap-carousel-img"
              :class="{ active: idx === centerIndex }"
            >
              <img :src="getImgUrl(img)" :alt="`轮播图${idx+1}`" />
            </div>
          </div>
        </div>
      </div>
      
      <!-- 音乐播放器区域 -->
      <div class="audio-player-container">
        <AudioPlayer />
      </div>
      
      <!-- 简介区域 -->
      <div class="post-area post-area-top">
        <div class="post-card">
          <h2>简介</h2>
          <pre class="post-pre">
情波港：情绪舒缓的爱心港湾
引言：情绪舒缓的时代需求
      在快节奏的现代生活中，焦虑、孤独、压力成为普遍的社会情绪症结。据世界卫生组织统计，全球超3亿人受情绪问题困扰，而传统心理咨询资源稀缺、成本高昂。情波港（Emotion Harbor）应运而生——一个专注于情绪舒缓的公益网页平台，通过"聊天机器人即时支持 + 精准资源推荐 + 社区互助交流"三维体系，为大众提供温暖、高效的"情绪急救站"。它不仅是工具，更是一座传递爱心的桥梁。
          </pre>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AudioPlayer from '../components/AudioPlayer.vue'
import { getMusicList, searchMusic } from '@/api/resourceApi'
export default {
  name: 'Home',
  components: {
    AudioPlayer
  },
  data() {
    return {
      images: [
        'lunhuan1.jpg',
        'lunhuan2.jpg',
        'lunhuan3.jpg',
        'lunhuan4.jpg',
        'lunhuan5.jpg'
      ],
      centerIndex: 0,
      carouselTimer: null,
      musicList: []
    }
  },
  methods: {
    getImgUrl(img) {
      return new URL(`../assets/${img}`, import.meta.url).href;
    },
    startAutoCarousel() {
      this.clearAutoCarousel();
      this.carouselTimer = setInterval(() => {
        this.centerIndex = (this.centerIndex + 1) % this.images.length;
      }, 1000);
    },
    clearAutoCarousel() {
      if (this.carouselTimer) {
        clearInterval(this.carouselTimer);
        this.carouselTimer = null;
      }
    },
    async fetchMusic() {
      try {
        const res = await getMusicList();
        this.musicList = Array.isArray(res.data) ? res.data : [];
      } catch (error) {
        console.error('获取音乐列表失败:', error);
      }
    },
    async handleSearch(keyword) {
      try {
        if (!keyword) {
          this.fetchMusic();
        } else {
          const res = await searchMusic(keyword);
          this.musicList = Array.isArray(res.data) ? res.data : [];
        }
      } catch (error) {
        console.error('搜索音乐失败:', error);
      }
    }
  },
  mounted() {
    this.startAutoCarousel();
    this.fetchMusic();
  },
  beforeUnmount() {
    this.clearAutoCarousel();
  }
}
</script>

<style scoped>
.home-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-image: url('../assets/72de8e20089aa38cc67bcb6ead20e6f8.jpg');
  background-size: cover;
  background-position: center;
  opacity: 0.18;
  z-index: -1;
  pointer-events: none;
}

.home-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 16px 16px 16px;
}

.overlap-carousel-area {
  margin-bottom: 32px;
  position: relative;
}

.overlap-carousel-wrapper {
  position: relative;
  overflow: hidden;
  border-radius: 18px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
}

.overlap-carousel-track {
  display: flex;
  height: 400px;
  background: linear-gradient(90deg, #f8fafc 0%, #e0e7ef 100%);
}

.overlap-carousel-img {
  flex: 1;
  transition: flex 0.7s cubic-bezier(.4,2.3,.3,1), box-shadow 0.3s;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  border-radius: 18px;
  margin: 0 2px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.overlap-carousel-img img {
  height: 100%;
  width: 100%;
  object-fit: cover;
  transition: transform 0.5s, filter 0.5s;
  filter: brightness(0.95) saturate(1.1);
}

.overlap-carousel-img:hover img,
.overlap-carousel-img.active img {
  transform: scale(1.08);
  filter: brightness(1.05) saturate(1.2);
}

.overlap-carousel-img.active {
  flex: 3;
  box-shadow: 0 8px 32px rgba(33,150,243,0.18);
  border: 2px solid #90caf9;
}

.overlap-carousel-img::after {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg,rgba(255,255,255,0.05) 60%,rgba(33,150,243,0.08) 100%);
  pointer-events: none;
}

.carousel-indicators {
  display: flex;
  justify-content: center;
  margin-top: 12px;
  gap: 10px;
}

.carousel-indicators span {
  display: inline-block;
  width: 12px;
  height: 12px;
  background: #b3c6e0;
  border-radius: 50%;
  transition: background 0.3s, transform 0.3s;
  cursor: pointer;
  box-shadow: 0 1px 4px rgba(33,150,243,0.08);
}

.carousel-indicators span.active {
  background: linear-gradient(120deg, #42a5f5 60%, #90caf9 100%);
  transform: scale(1.25);
  box-shadow: 0 2px 8px rgba(33,150,243,0.18);
}

/* 音乐播放器容器样式 */
.audio-player-container {
  margin-bottom: 40px;
  background: rgba(255, 255, 255, 0.92);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 6px 24px rgba(33,150,243,0.08);
  animation: fadeIn 1.2s;
  backdrop-filter: blur(2px);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(30px);}
  to { opacity: 1; transform: none;}
}

.post-area {
  margin-bottom: 40px;
}

.post-card {
  background: rgba(255, 255, 255, 0.96);
  border-radius: 16px;
  padding: 32px 28px 24px 28px;
  box-shadow: 0 6px 24px rgba(33,150,243,0.08);
  position: relative;
  overflow: hidden;
}

.post-card h2 {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 18px;
  color: #1976d2;
  letter-spacing: 2px;
  border-left: 5px solid #90caf9;
  padding-left: 12px;
  background: linear-gradient(90deg, #e3f2fd 60%, #fff 100%);
  border-radius: 6px;
  display: inline-block;
}

.post-pre {
  white-space: pre-wrap;
  font-family: inherit;
  line-height: 1.7;
  margin: 0;
  color: #374151;
  text-indent: 2em;
  font-size: 1.08rem;
  letter-spacing: 0.5px;
}

.post-card::before {
  content: "";
  position: absolute;
  top: -60px;
  right: -60px;
  width: 160px;
  height: 160px;
  background: radial-gradient(circle, #90caf9 0%, transparent 70%);
  opacity: 0.18;
  z-index: 0;
  pointer-events: none;
}

.resource-main {
  padding: 40px 0 0 0;
  min-height: 92vh;
  position: relative;
  background-color: #f8fafc;
}

/* 背景加渐变遮罩 */
.resource-main::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  width: 100%; height: 100%;
  background-image: 
    linear-gradient(120deg, #a1c4fd 0%, #c2e9fb 100%),
    url('../assets/ziyuanbeijing.jpg');
  background-blend-mode: lighten;
  background-position: center top;
  background-repeat: repeat-y;
  background-size: 100% auto;
  opacity: 0.93;
  z-index: 0;
  pointer-events: none;
}

.arc-carousel-area {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 600px;
  background: rgba(255, 255, 255, 0.85);
  margin: 0 auto 0 auto;
  max-width: 1600px;
  position: relative;
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(33,150,243,0.10);
  border-bottom: none;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.arc-carousel-wrapper {
  width: 100%;
  overflow: visible;
  position: relative;
  height: 600px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.arc-carousel-track {
  display: flex;
  align-items: flex-end;
  justify-content: flex-start;
  position: relative;
  width: 100%;
  height: 520px;
  perspective: 1800px;
  will-change: transform;
}
.arc-carousel-img {
  position: absolute;
  left: 35%;
  bottom: 60px;
  transform-origin: bottom center;
  box-shadow: 0 4px 24px rgba(0,0,0,0.13);
  border-radius: 28px;
  cursor: pointer;
  transition: all 0.6s cubic-bezier(.4,2,.6,1), filter 0.4s;
  filter: blur(1.5px) brightness(0.92) grayscale(0.1);
  opacity: 0.7;
}
.arc-carousel-img img {
  width: 380px;
  height: 480px;
  object-fit: cover;
  border-radius: 28px;
  display: block;
  background: #eee;
  box-shadow: 0 2px 8px rgba(33,150,243,0.08);
  transition: box-shadow 0.4s, filter 0.4s;
}
.arc-carousel-img.center {
  box-shadow: 0 12px 48px 0 rgba(52,152,219,0.22), 0 2px 8px 0 rgba(44,62,80,0.10);
  filter: none;
  opacity: 1;
  z-index: 10;
  border: 3px solid #90caf9;
}
.arc-carousel-img.center img {
  box-shadow: 0 8px 32px 0 rgba(33,150,243,0.18);
  filter: brightness(1.05) saturate(1.1);
}
.arc-carousel-img:not(.center):hover {
  filter: blur(0.5px) brightness(1) grayscale(0.05);
  opacity: 0.85;
}

.arc-carousel-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 32px;
  gap: 48px;
}
.arrow-btn {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(120deg, #e3f2fd 60%, #bbdefb 100%);
  color: #2980b9;
  font-size: 2.2rem;
  box-shadow: 0 2px 8px rgba(52,152,219,0.10);
  cursor: pointer;
  transition: background 0.2s, color 0.2s, box-shadow 0.3s;
}
.arrow-btn:hover {
  background: linear-gradient(120deg, #42a5f5 60%, #90caf9 100%);
  color: #fff;
  box-shadow: 0 6px 24px rgba(33,150,243,0.18);
}

.resource-content {
  margin: 48px auto 0 auto;
  max-width: 1200px;
  padding: 40px;
  background: rgba(245, 240, 230, 0.93);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(33,150,243,0.10);
}

.section-title {
  text-align: center;
  margin: 40px 0;
}

.section-title h1 {
  font-size: 2.5rem;
  color: #1976d2;
  margin-bottom: 10px;
  letter-spacing: 2px;
  border-left: 6px solid #90caf9;
  padding-left: 16px;
  background: linear-gradient(90deg, #e3f2fd 60%, #fff 100%);
  border-radius: 8px;
  display: inline-block;
}

.section-title p {
  font-size: 1.2rem;
  color: #7f8c8d;
}

.resource-section {
  margin: 40px auto;
  max-width: 1200px;
  padding: 0 20px;
}

.resource-section h2 {
  font-size: 1.8rem;
  color: #34495e;
  margin-bottom: 20px;
  position: relative;
  padding-left: 18px;
  background: linear-gradient(90deg, #bbdefb 60%, #fff 100%);
  border-radius: 6px;
  display: inline-block;
  border-left: 5px solid #42a5f5;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
}

.video-card {
  background-color: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(33,150,243,0.08);
  transition: transform 0.3s, box-shadow 0.3s, filter 0.3s;
  cursor: pointer;
  border: 1.5px solid #e3f2fd;
}
.video-card:hover {
  transform: translateY(-8px) scale(1.03);
  box-shadow: 0 12px 32px rgba(33,150,243,0.18);
  filter: brightness(1.03);
}

.video-thumbnail {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.video-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.video-card:hover .video-thumbnail img {
  transform: scale(1.07);
}

.play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 50px;
  height: 50px;
  background-color: rgba(255, 255, 255, 0.85);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #42a5f5;
  opacity: 0;
  transition: opacity 0.3s;
  box-shadow: 0 2px 8px rgba(33,150,243,0.10);
}

.video-card:hover .play-icon {
  opacity: 1;
}

.video-info {
  padding: 15px;
}

.video-info h3 {
  font-size: 1.2rem;
  margin-bottom: 8px;
  color: #1976d2;
  height: 2.4rem;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.video-info p {
  font-size: 0.98rem;
  color: #7f8c8d;
  margin-bottom: 10px;
  height: 3.6rem;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.video-tag {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 15px;
  font-size: 0.85rem;
  font-weight: 500;
  background: linear-gradient(90deg, #e3f2fd 60%, #fff 100%);
  color: #1976d2;
  margin-top: 6px;
  border: 1px solid #90caf9;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 32px;
  margin: 40px auto;
  max-width: 800px;
}
.recommend-item {
  display: flex;
  align-items: center;
  background: linear-gradient(90deg, #fffbe6 60%, #f8fafc 100%);
  border-radius: 16px;
  box-shadow: 0 2px 8px #eee;
  padding: 28px;
  border: 1.5px solid #ffe082;
  transition: box-shadow 0.2s, transform 0.2s;
}
.recommend-item:hover {
  box-shadow: 0 8px 24px #ffe08288;
  transform: translateY(-4px) scale(1.01);
}
.img-box {
  flex: 0 0 180px;
  margin-right: 32px;
}
.img-box img {
  width: 180px;
  height: 120px;
  object-fit: cover;
  border-radius: 10px;
  cursor: pointer;
  transition: box-shadow 0.2s, filter 0.2s;
  border: 1.5px solid #bbdefb;
}
.img-box img:hover {
  box-shadow: 0 0 12px #42a5f5;
  filter: brightness(1.08);
}
.text-box {
  flex: 1;
}
.text-box h3 {
  margin: 0 0 12px 0;
  font-size: 1.2rem;
  color: #1976d2;
}
.text-box p {
  margin: 0;
  color: #666;
  font-size: 1rem;
  line-height: 1.7;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 0;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #42a5f5;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.no-resources {
  text-align: center;
  padding: 50px 0;
  color: #7f8c8d;
  font-size: 1.2rem;
}

.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeInModal 0.5s;
}
@keyframes fadeInModal {
  from { opacity: 0; }
  to { opacity: 1; }
}

.video-modal, .text-modal {
  width: 80%;
  max-width: 900px;
  background-color: white;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(33,150,243,0.18);
  animation: fadeInModal 0.5s;
}

.text-modal {
  max-width: 800px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #f8f9fa;
  border-bottom: 1.5px solid #e3f2fd;
}

.modal-header h3 {
  font-size: 1.5rem;
  color: #1976d2;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 2rem;
  color: #7f8c8d;
  cursor: pointer;
  transition: color 0.3s;
  border-radius: 50%;
  padding: 2px 8px;
}
.close-btn:hover {
  color: #e74c3c;
  background: #e3f2fd;
}

.video-container {
  width: 100%;
  background-color: black;
}

.video-player {
  width: 100%;
  max-height: 70vh;
  display: block;
}

.video-description {
  padding: 20px;
  font-size: 1rem;
  line-height: 1.6;
  color: #34495e;
}

.text-content-modal {
  padding: 25px;
  font-size: 1.1rem;
  line-height: 1.8;
  color: #34495e;
  overflow-y: auto;
  max-height: 70vh;
}

.text-content-modal p {
  margin-bottom: 15px;
}

@media (max-width: 768px) {
  .video-grid, .recommend-list {
    grid-template-columns: 1fr;
  }
  .video-modal, .text-modal {
    width: 95%;
  }
  .modal-header h3 {
    font-size: 1.2rem;
  }
  .arc-carousel-img img {
    width: 220px;
    height: 280px;
  }
}

.static-content {
  margin-top: 50px;
}
</style>

