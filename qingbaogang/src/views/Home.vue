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
              :class="{ active: idx === hoverIndex }"
              @mouseenter="handleCarouselMouseEnter(idx)"
              @mouseleave="handleCarouselMouseLeave"
              @click="showDetail(idx)"
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
      hoverIndex: null,
      showModal: false,
      currentDetailIndex: 0,
      autoCarouselTimer: null,
      musicList: []
    }
  },
  methods: {
    getImgUrl(img) {
      return new URL(`../assets/${img}`, import.meta.url).href;
    },
    handleCarouselMouseEnter(idx) {
      this.hoverIndex = idx;
      this.clearAutoCarousel();
    },
    handleCarouselMouseLeave() {
      this.startAutoCarousel();
    },
    showDetail(index) {
      this.currentDetailIndex = index;
      this.showModal = true;
      document.body.classList.add('modal-open');
    },
    startAutoCarousel() {
      this.clearAutoCarousel();
      this.autoCarouselTimer = setInterval(() => {
        if (this.hoverIndex === null) {
          this.hoverIndex = 0;
        } else {
          this.hoverIndex = (this.hoverIndex + 1) % this.images.length;
        }
      }, 3000);
    },
    clearAutoCarousel() {
      if (this.autoCarouselTimer) {
        clearInterval(this.autoCarouselTimer);
        this.autoCarouselTimer = null;
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
  width: 100%;
  height: 100%;
  background-image: url('../assets/harbor-background.png');
  background-size: cover;
  background-position: center;
  opacity: 0.15;
  z-index: -1;
}

.home-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.overlap-carousel-area {
  margin-bottom: 40px;
}

.overlap-carousel-wrapper {
  position: relative;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.overlap-carousel-track {
  display: flex;
  height: 400px;
}

.overlap-carousel-img {
  flex: 1;
  transition: flex 0.7s ease;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.overlap-carousel-img img {
  height: 100%;
  width: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.overlap-carousel-img:hover img,
.overlap-carousel-img.active img {
  transform: scale(1.05);
}

.overlap-carousel-img.active {
  flex: 3;
}

/* 音乐播放器容器样式 */
.audio-player-container {
  margin-bottom: 40px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.post-area {
  margin-bottom: 40px;
}

.post-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.post-pre {
  white-space: pre-wrap;
  font-family: inherit;
  line-height: 1.6;
  margin: 0;
}
</style>

