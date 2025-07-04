<template>
  <div class="resource-main">
    <div class="arc-carousel-area">
      <div class="arc-carousel-wrapper">
        <div class="arc-carousel-track">
          <div
            v-for="(img, idx) in images"
            :key="img"
            class="arc-carousel-img"
            :class="{ center: idx === centerIndex }"
            :style="getImgStyle(idx)"
          >
            <img :src="getImgUrl(img)" :alt="`轮播图${idx+1}`" />
          </div>
        </div>
        <div class="arc-carousel-controls">
          <button class="arrow-btn" @click="prev"><span>&larr;</span></button>
          <button class="arrow-btn" @click="next"><span>&rarr;</span></button>
        </div>
      </div>
    </div>
    <div class="resource-content">
      <div class="section-title">
        <h1>推荐资源</h1>
        <p>根据您的情绪状态，我们为您推荐以下资源</p>
      </div>

      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>正在加载资源...</p>
      </div>

      <div v-if="!loading && recommendedResources.videos && recommendedResources.videos.length > 0" class="resource-section">
        <h2>视频资源</h2>
        <div class="video-grid">
          <div 
            v-for="video in recommendedResources.videos" 
            :key="video.id" 
            class="video-card"
            @click="openVideoModal(video)"
          >
            <div class="video-thumbnail">
              <img :src="video.thumbnailUrl || getDefaultThumbnail()" alt="视频缩略图" />
              <div class="play-icon">▶</div>
            </div>
            <div class="video-info">
              <h3>{{ video.title }}</h3>
              <p>{{ truncateText(video.description, 60) }}</p>
              <div class="video-tag" :class="getEmotionTagClass(video.emotionTag)">
                {{ getEmotionTagText(video.emotionTag) }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!loading && recommendedResources.texts && recommendedResources.texts.length > 0" class="resource-section">
        <h2>推荐资源</h2>
        <div class="recommend-list">
          <div
            v-for="(text, i) in recommendedResources.texts"
            :key="text.id"
            class="recommend-item"
          >
            <div class="img-box">
              <img
                :src="text.imageUrl"
                alt="文案图片"
                style="cursor: pointer"
                v-if="recommendedResources.videos && recommendedResources.videos[i]"
                @click="openVideoModal(recommendedResources.videos[i])"
              />
              <img
                v-else
                :src="text.imageUrl"
                alt="文案图片"
                style="opacity:0.7;cursor:not-allowed"
              />
            </div>
            <div class="text-box">
              <h3>{{ text.title }}</h3>
              <p>{{ text.content }}</p>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!loading && (!recommendedResources.videos || recommendedResources.videos.length === 0) && (!recommendedResources.texts || recommendedResources.texts.length === 0)" class="no-resources">
        <p>暂无推荐资源，请稍后再试</p>
      </div>

      <div v-if="showVideoModal" class="modal-overlay" @click.self="closeVideoModal">
        <div class="video-modal">
          <div class="modal-header">
            <h3>{{ currentVideo.title }}</h3>
            <button class="close-btn" @click="closeVideoModal">&times;</button>
          </div>
          <div class="video-container">
            <video 
              v-if="isMp4(currentVideo.url)"
              ref="videoPlayer" 
              controls 
              autoplay
              class="video-player"
            >
              <source :src="currentVideo.url" type="video/mp4">
              您的浏览器不支持视频播放
            </video>
            <iframe
              v-else
              :src="getIframeUrl(currentVideo.url)"
              frameborder="0"
              allowfullscreen
              style="width: 100%; height: 400px;"
            ></iframe>
          </div>
          <div class="video-description">
            <p>{{ currentVideo.description }}</p>
          </div>
        </div>
      </div>

      <div v-if="showTextModal" class="modal-overlay" @click.self="closeTextModal">
        <div class="text-modal">
          <div class="modal-header">
            <h3>{{ currentText.title }}</h3>
            <button class="close-btn" @click="closeTextModal">&times;</button>
          </div>
          <div class="text-content-modal">
            <p v-html="formatTextContent(currentText.content)"></p>
          </div>
        </div>
      </div>

      <div v-if="showStaticContent" class="static-content">
        <div class="content-section">
          <div class="image-text-block">
            <div class="image-container">
              <img src="../assets/ziyuan1.jpg" alt="宁静窗景" class="content-image" />
            </div>
            <div class="text-content">
              <h2>心灵的窗台</h2>
              <div class="text-block">
                <p class="highlight">【心理空间】：创造一个属于自己的心理安全空间，就像这个明亮的窗台，让阳光照进内心。</p>
                <p class="highlight">【情绪觉察】：学会像观察窗外风景一样，静静观察自己的情绪变化，接纳每一个真实的感受。</p>
                <p class="highlight">【放松练习】：找一个安静的角落，看着窗外，深呼吸，让紧张和压力随着呼吸慢慢释放。</p>
                <p class="highlight">【日常建议】：每天留出10分钟的独处时光，整理思绪，梳理情感，让心灵得到休憩。</p>
              </div>
            </div>
          </div>
        </div>

        <div class="content-section">
          <div class="image-text-block reverse">
            <div class="image-container">
              <img src="../assets/ziyuan2.jpg" alt="温暖阳光" class="content-image" />
            </div>
            <div class="text-content">
              <h2>情绪日记</h2>
              <p class="elegant-text">当阳光温柔地洒在窗台，不妨拿起笔记本，记录下当下的心情。情绪日记不仅是倾诉的出口，更是自我认知的开始。</p>
              <p class="elegant-text">通过持续记录，你会发现情绪的规律，理解自己的需求，这是情绪管理的第一步。记住，每个情绪都值得被倾听和理解。</p>
            </div>
          </div>
        </div>

        <div class="content-section">
          <div class="image-text-block">
            <div class="image-container">
              <img src="../assets/ziyuan3.jpg" alt="心理咨询" class="content-image" />
            </div>
            <div class="text-content">
              <h2>专业心理支持</h2>
              <p class="elegant-text">当你感到困惑或压力，请记住寻求专业帮助是明智和勇敢的选择。我们的心理咨询师团队始终准备倾听和陪伴。</p>
              <p class="elegant-text">在线咨询、面对面交流，或是团体辅导，选择最适合你的方式。每一次倾诉都是通向心理健康的重要一步。</p>
            </div>
          </div>
        </div>

        <div class="content-section">
          <div class="message-block">
            <div class="icon-message">
              <div class="plant-icon"></div>
              <div class="message-text">
                <h3>情绪急救包</h3>
                <div class="image-container">
                  <img src="../assets/ziyuan4.jpg" alt="急救资源" class="content-image" />
                </div>
                <p>为你准备随时可用的情绪急救资源：</p>
                <ul>
                  <li>24小时心理援助热线</li>
                  <li>冥想和放松音频指导</li>
                  <li>情绪管理小技巧手册</li>
                  <li>压力纾解运动指南</li>
                </ul>
              </div>
            </div>
          </div>
        </div>

        <div class="content-section">
          <div class="image-text-block reverse">
            <div class="image-container">
              <img src="../assets/ziyuan5.jpg" alt="团体活动" class="content-image" />
            </div>
            <div class="text-content">
              <h2>心理成长工作坊</h2>
              <p class="elegant-text">在安全温暖的环境中，与志同道合的朋友一起探索内心，分享故事，互相支持。定期举办的工作坊包括：</p>
              <ul class="workshop-list">
                <li>情绪管理训练营</li>
                <li>压力应对技巧分享</li>
                <li>人际关系提升小组</li>
                <li>自我认知探索之旅</li>
              </ul>
            </div>
          </div>
        </div>

        <div class="content-section">
          <div class="image-text-block">
            <div class="image-container">
              <img src="../assets/ziyuan6.jpg" alt="自我关怀" class="content-image" />
            </div>
            <div class="text-content">
              <h2>自我关怀指南</h2>
              <p class="elegant-text">学会关爱自己，是心理健康的基础。每天花一些时间做自己喜欢的事，保持规律的作息，培养健康的生活方式。</p>
              <p class="elegant-text">记住，给自己一些独处的时光，享受宁静，聆听内心的声音，这些都是自我关怀的重要部分。</p>
            </div>
          </div>
        </div>

        <div class="content-section">
          <div class="image-text-block reverse">
            <div class="image-container">
              <img src="../assets/ziyuan7.jpg" alt="正念练习" class="content-image" />
            </div>
            <div class="text-content">
              <h2>正念练习</h2>
              <p class="elegant-text">通过正念练习，培养专注当下的能力，减少焦虑和压力。每天进行简单的呼吸练习，觉察身体感受，让心灵回到平静。</p>
              <div class="mindfulness-tips">
                <h4>每日正念小练习：</h4>
                <ul>
                  <li>专注呼吸3分钟</li>
                  <li>身体扫描5分钟</li>
                  <li>正念步行10分钟</li>
                  <li>感恩日记书写</li>
                </ul>
              </div>
            </div>
          </div>
        </div>

        <div class="content-section">
          <div class="image-text-block">
            <div class="image-container">
              <img src="../assets/ziyuan8.jpg" alt="成长之路" class="content-image" />
            </div>
            <div class="text-content">
              <h2>心理健康资源库</h2>
              <p class="elegant-text">为你精选优质的心理健康资源，包括专业书籍推荐、心理健康APP、在线课程等，助你在心理成长的道路上不断前进。</p>
              <div class="resource-links">
                <h4>推荐资源：</h4>
                <ul>
                  <li>心理健康测评工具</li>
                  <li>情绪管理主题书单</li>
                  <li>冥想放松引导音频</li>
                  <li>心理健康公开课</li>
                </ul>
              </div>
            </div>
          </div>
        </div>

        <div class="content-section">
          <div class="footer-quote">
            <p>愿你在人生的旅途中，始终保持心灵的澄明</p>
            <p>让我们一起守护心理健康，遇见更好的自己</p>
            <p class="english-quote">May you maintain inner peace on your life journey</p>
            <p class="english-quote">Let's protect mental health and meet a better self</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getRecommendedResources } from '@/api/resourceApi';
import { useUserStore } from '../stores/user';

export default {
  name: 'Resource',
  data() {
    return {
      images: [
        'lunbotu1.jpg',
        'lunbotu2.jpg',
        'lunbotu3.jpg',
        'lunbotu4.jpg',
        'lunbotu5.jpg',
        'lunbotu6.jpg',
        'lunbotu7.jpg'
      ],
      centerIndex: 3, // 默认中间图片
      loading: true,
      recommendedResources: {
        videos: [],
        texts: []
      },
      showVideoModal: false,
      showTextModal: false,
      currentVideo: {},
      currentText: {},
      showStaticContent: false // 是否显示静态内容
    }
  },
  created() {
    this.fetchRecommendedResources();
  },
  methods: {
    getImgUrl(img) {
      return new URL(`../assets/${img}`, import.meta.url).href;
    },
    getImgStyle(idx) {
      const total = this.images.length;
      let offset = idx - this.centerIndex;
      if (offset < -Math.floor(total / 2)) offset += total;
      if (offset > Math.floor(total / 2)) offset -= total;
      // 只显示中心及两侧各2张
      const maxOffset = 2;
      if (offset > maxOffset || offset < -maxOffset) {
        return {
          opacity: 0,
          pointerEvents: 'none',
          transform: 'scale(0.7)'
        };
      }
      // 中心图片正面、居中
      if (offset === 0) {
        return {
          transform: 'translateX(0) scale(1) rotateY(0deg)',
          zIndex: 100,
          opacity: 1,
          pointerEvents: 'auto',
          transition: 'all 0.6s cubic-bezier(.4,2,.6,1)'
        };
      }
      // 两侧图片
      const baseTranslate = 340; // 水平间距
      const baseRotate = 40; // 旋转角度
      const baseScale = 0.8; // 最小缩放
      const absOffset = Math.abs(offset);
      const translateX = offset * baseTranslate;
      const rotateY = offset * baseRotate;
      const scale = 1 - absOffset * 0.13;
      return {
        transform: `translateX(${translateX}px) scale(${scale}) rotateY(${rotateY}deg)` ,
        zIndex: 100 - absOffset,
        opacity: 1,
        pointerEvents: 'auto',
        transition: 'all 0.6s cubic-bezier(.4,2,.6,1)'
      };
    },
    prev() {
      this.centerIndex = (this.centerIndex - 1 + this.images.length) % this.images.length;
    },
    next() {
      this.centerIndex = (this.centerIndex + 1) % this.images.length;
    },
    async fetchRecommendedResources() {
      this.loading = true;
      try {
        const userStore = useUserStore();
        const userId = userStore.userId;
        
        if (!userId) {
          console.warn('用户未登录，无法获取个性化推荐');
          this.showStaticContent = true;
          this.loading = false;
          return;
        }
        const response = await getRecommendedResources(userId);
        // 修正判断逻辑：只要有 data.videos 或 data.texts 就展示
        if (
          response &&
          ((response.success === true) || response.code === 200 || response.status === 200) &&
          response.data &&
          (Array.isArray(response.data.videos) || Array.isArray(response.data.texts))
        ) {
          this.recommendedResources = response.data;
          if ((!(this.recommendedResources.videos && this.recommendedResources.videos.length > 0)) && 
              (!(this.recommendedResources.texts && this.recommendedResources.texts.length > 0))) {
            this.showStaticContent = true;
          }
        } else {
          console.error('获取推荐资源失败:', response);
          this.showStaticContent = true;
        }
      } catch (error) {
        console.error('获取推荐资源出错:', error);
        this.showStaticContent = true;
      } finally {
        this.loading = false;
      }
    },
    openVideoModal(video) {
      this.currentVideo = video;
      this.showVideoModal = true;
      // 等待DOM更新后设置视频
      this.$nextTick(() => {
        if (this.$refs.videoPlayer) {
          this.$refs.videoPlayer.load();
        }
      });
    },
    closeVideoModal() {
      // 停止视频播放
      if (this.$refs.videoPlayer) {
        this.$refs.videoPlayer.pause();
      }
      this.showVideoModal = false;
    },
    openTextModal(text) {
      this.currentText = text;
      this.showTextModal = true;
    },
    closeTextModal() {
      this.showTextModal = false;
    },
    truncateText(text, maxLength) {
      if (!text) return '';
      return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
    },
    getDefaultThumbnail() {
      return new URL('../assets/default-thumbnail.png', import.meta.url).href;
    },
    getEmotionTagClass(tag) {
      switch (tag) {
        case 'low_risk':
          return 'tag-low-risk';
        case 'medium_risk':
          return 'tag-medium-risk';
        case 'high_risk':
          return 'tag-high-risk';
        default:
          return 'tag-default';
      }
    },
    getEmotionTagText(tag) {
      switch (tag) {
        case 'low_risk':
          return '轻松放松';
        case 'medium_risk':
          return '情绪调节';
        case 'high_risk':
          return '深度关怀';
        default:
          return '通用资源';
      }
    },
    formatTextContent(content) {
      if (!content) return '';
      // 将换行符转换为<br>标签
      return content.replace(/\n/g, '<br>');
    },
    isMp4(url) {
      return url && url.endsWith('.mp4');
    },
    getIframeUrl(url) {
      // 可根据实际需要适配B站、腾讯等外链
      return url;
    }
  }
}
</script>

<style scoped>
.resource-main {
  padding: 40px 0 0 0;
  min-height: 92vh;
  position: relative;
  background-color: #f8fafc;
}

/* 使用伪元素添加背景 */
.resource-main::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  background-image: url('../assets/ziyuanbeijing.jpg');
  background-position: center top;
  background-repeat: repeat-y;
  background-size: 100% auto;
  opacity: 0.95;
  z-index: 0;
}

/* 确保内容在背景之上 */
.arc-carousel-area,
.resource-content {
  position: relative;
  z-index: 1;
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
  border-radius: 0;
  box-shadow: none;
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
  border-radius: 22px;
  cursor: pointer;
  transition: all 0.6s cubic-bezier(.4,2,.6,1);
}
.arc-carousel-img img {
  width: 380px;
  height: 480px;
  object-fit: cover;
  border-radius: 22px;
  display: block;
  background: #eee;
}
.arc-carousel-img.center {
  /* 让中心图片更突出 */
  box-shadow: 0 8px 32px 0 rgba(52,152,219,0.18), 0 2px 8px 0 rgba(44,62,80,0.10);
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
  background: #f2f6fa;
  color: #2980b9;
  font-size: 2.2rem;
  box-shadow: 0 2px 8px rgba(52,152,219,0.10);
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}
.arrow-btn:hover {
  background: #3498db;
  color: #fff;
}
.resource-content {
  margin: 48px auto 0 auto;
  max-width: 1200px;
  padding: 40px;
  background: rgba(245, 240, 230, 0.9);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.section-title {
  text-align: center;
  margin: 40px 0;
}

.section-title h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 10px;
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
  padding-left: 15px;
}

.resource-section h2::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 5px;
  height: 25px;
  background-color: #3498db;
  border-radius: 3px;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
}

.video-card {
  background-color: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
}

.video-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
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
  transform: scale(1.05);
}

.play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 50px;
  height: 50px;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #e74c3c;
  opacity: 0;
  transition: opacity 0.3s;
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
  color: #2c3e50;
  height: 2.4rem;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.video-info p {
  font-size: 0.9rem;
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
  padding: 3px 10px;
  border-radius: 15px;
  font-size: 0.8rem;
  font-weight: 500;
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
  background: #fffbe6;
  border-radius: 12px;
  box-shadow: 0 2px 8px #eee;
  padding: 24px;
}
.img-box {
  flex: 0 0 180px;
  margin-right: 32px;
}
.img-box img {
  width: 180px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.img-box img:hover {
  box-shadow: 0 0 8px #409eff;
}
.text-box {
  flex: 1;
}
.text-box h3 {
  margin: 0 0 12px 0;
  font-size: 1.2rem;
  color: #333;
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
  border-top: 5px solid #3498db;
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
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.video-modal {
  width: 80%;
  max-width: 900px;
  background-color: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.modal-header h3 {
  font-size: 1.5rem;
  color: #2c3e50;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.8rem;
  color: #7f8c8d;
  cursor: pointer;
  transition: color 0.3s;
}

.close-btn:hover {
  color: #e74c3c;
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

.text-modal {
  width: 80%;
  max-width: 800px;
  max-height: 90vh;
  background-color: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
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
}

.static-content {
  margin-top: 50px;
}
</style> 