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
              @mouseenter="hoverIndex = idx"
              @mouseleave="hoverIndex = null"
            >
              <img :src="getImgUrl(img)" :alt="`轮播图${idx+1}`" />
            </div>
          </div>
        </div>
      </div>
      <!-- 音乐播放器在推文上方 -->
      <div class="music-player music-player-top">
        <div class="music-info">
          <div class="cover-wrapper">
            <img :src="currentCover" class="cover-img" />
          </div>
          <div class="music-meta">
            <div class="music-title">{{ musicList[currentIndex].title }}</div>
            <div class="progress-bar-bg">
              <div class="progress-bar" :style="{ width: progressPercent + '%' }"></div>
            </div>
          </div>
        </div>
        <div class="music-controls">
          <button @click="prevMusic" class="music-btn"><span>&#9664;&#9664;</span></button>
          <button @click="togglePlay" class="music-btn">
            <span v-if="!isPlaying">&#9654;</span>
            <span v-else>||</span>
          </button>
          <button @click="nextMusic" class="music-btn"><span>&#9654;&#9654;</span></button>
        </div>
        <audio ref="audio" :src="musicList[currentIndex].src" @timeupdate="onTimeUpdate" @ended="nextMusic" />
      </div>
      <!-- 推文区域 -->
      <div class="post-area post-area-top">
        <div class="post-card">
          <h2>简介</h2>
          <pre class="post-pre">
情波港：情绪舒缓的爱心港湾
引言：情绪舒缓的时代需求
      在快节奏的现代生活中，焦虑、孤独、压力成为普遍的社会情绪症结。据世界卫生组织统计，全球超3亿人受情绪问题困扰，而传统心理咨询资源稀缺、成本高昂。情波港（Emotion Harbor）应运而生——一个专注于情绪舒缓的公益网页平台，通过"聊天机器人即时支持 + 精准资源推荐 + 社区互助交流"三维体系，为大众提供温暖、高效的"情绪急救站"。它不仅是工具，更是一座传递爱心的桥梁。
一、聊天机器人：全天候的"情绪急救员"
       情波港的智能聊天机器人（昵称"小情"）是平台核心，运用情感计算与自然语言处理技术，实现人性化情绪疏导：
1.即时倾听与共情响应
       场景示例：用户深夜输入"最近工作压力大到失眠"，小情会先回应："听起来你正承受很大的负担，失眠一定很煎熬吧？"（共情），而非机械式提问。
2.技术支撑：
       通过语义分析识别情绪关键词（如"压力""孤独"），触发预设的舒缓策略库，如引导呼吸练习（"跟我一起深呼吸三次好吗？"）。
3.个性化疏导方案
根据对话深度动态调整：
初级焦虑：推荐5分钟正念音频；
长期抑郁：生成阶段性疏导计划（如"每日情绪日记+微目标打卡"）。
4.爱心救助案例：
        一位产后抑郁妈妈通过21天机器人陪伴，逐步重建生活节奏，最终在社区分享："小情像深夜的一盏灯"。
5.危机干预转接机制
       当识别自杀倾向等高风险语句时，自动连接公益心理热线，并推送本地援助资源（如"xx市24小时心理救援中心"），实现"AI-人工"无缝接力。
聊天机器人以零门槛、零评判的特性，打破时空限制，成为触手可及的"情绪避风港"。
二、资源推荐系统：定制化的"情绪药箱"
       情波港整合千余项舒缓资源，通过算法匹配用户需求，实现"精准治愈"：
1.智能匹配逻辑
短期需求：若用户描述"考试前心慌"，系统优先推送"考前放松呼吸指南"短视频；
长期建设：对频繁登录的孤独用户，逐步推荐"社交技能提升"课程与线下活动。
数据反馈：用户评分高的资源自动加权推荐，形成"群体智慧"筛选机制。
2.爱心传递设计
资源共创：用户可上传自创内容（如一首安慰诗），经审核后标注"来自@匿名伙伴的礼物"；
紧急包功能：针对灾害事件（如地震后），自动推送"集体创伤应对指南"并开通定向捐赠通道。
       资源系统像一位"懂你的药剂师"，将碎片化信息转化为结构化治愈力。
三、社区交流：温情的"情绪共生圈"
      情波港建立"树洞社区"，以严格规则守护安全空间，激发用户间的情感互助：
1.分层交流机制
匿名树洞区：完全加密发帖，用户倾诉压力获得共鸣回应（如"我也曾失业，3个月后找到转机"）；
主题互助组：按需求细分群组（"单亲父母情绪角""抗癌者联盟"），降低孤独感；
专家互动日：每月邀请心理咨询师在线答疑，公益科普情绪知识。
2.爱心救助的社区化实践
故事银行：用户记录情绪康复历程，如"从恐慌症到登山者"的自述，给予他人希望；
微行动倡议：发起"今天夸1个人"挑战，参与者上传对话截图，累计完成超10万次正向互动；
紧急支援网：用户可标记"急需倾听"，志愿者团队（认证用户）15分钟内响应，形成"人机协作"救援网。
3.安全守护与正向引导
AI+人工审核：屏蔽攻击性言论，对负面帖子触发关怀私信（"需要和小情聊聊吗？"）；
能量值体系：积极互动获"爱心积分"，兑换公益周边（印有用户留言的治愈手账本）。
      案例：一名大学生在社区发帖"想休学但不敢说"，收到32条经验分享后决定与家人沟通，后续回帖："这里让我知道，脆弱也是力量。"
      情波港——从舒缓到共生的爱心革命，情波港的创新在于将科技温度与人性关怀深度融合：
聊天机器人提供"即时安全感"，资源系统赋予"自我疗愈力"，社区培育"集体希望感"，三者形成情绪救助闭环；其公益属性打破经济壁垒——所有功能免费开放，运营依靠社会捐赠与志愿者网络，真正践行"情绪平权"。
在情波港，每一次对话、一次资源打开、一条社区回复，都是爱心的具体形态。正如一位用户留言：
"这里没有神药，但有无数双手在深渊边拉住你。"情绪舒缓之路，终因共行而不孤独。
          </pre>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      images: [
        'lunhuan1.jpg',
        'lunhuan2.jpg',
        'lunhuan3.jpg',
        'lunhuan4.jpg',
        'lunhuan5.jpg',
        'lunhuan6.jpg'
      ],
      hoverIndex: null,
      musicList: [
        {
          title: 'Moon Night',
          src: new URL('../assets/moonnight.mp3', import.meta.url).href,
          cover: new URL('../assets/lunhuan1.jpg', import.meta.url).href
        },
        {
          title: '雨的印记',
          src: new URL('../assets/雨的印记.mp3', import.meta.url).href,
          cover: new URL('../assets/lunhuan2.jpg', import.meta.url).href
        }
      ],
      currentIndex: 0,
      isPlaying: false,
      progressPercent: 0
    }
  },
  computed: {
    currentCover() {
      return this.musicList[this.currentIndex].cover;
    }
  },
  watch: {
    currentIndex(newVal, oldVal) {
      // 切换歌曲后，等待src变更后再播放，避免AbortError
      this.$nextTick(() => {
        const audio = this.$refs.audio;
        audio.currentTime = 0;
        const playPromise = audio.play();
        if (playPromise && playPromise.catch) {
          playPromise.catch(() => {});
        }
        this.isPlaying = true;
      });
    }
  },
  methods: {
    getImgUrl(img) {
      return new URL(`../assets/${img}`, import.meta.url).href;
    },
    prevMusic() {
      this.currentIndex = (this.currentIndex - 1 + this.musicList.length) % this.musicList.length;
    },
    nextMusic() {
      this.currentIndex = (this.currentIndex + 1) % this.musicList.length;
    },
    togglePlay() {
      const audio = this.$refs.audio;
      if (this.isPlaying) {
        audio.pause();
      } else {
        audio.play();
      }
      this.isPlaying = !this.isPlaying;
    },
    onTimeUpdate(e) {
      const audio = e.target;
      this.progressPercent = (audio.currentTime / audio.duration) * 100;
    }
  }
}
</script>

<style scoped>
.home-bg {
  min-height: 100vh;
  width: 100vw;
  position: fixed;
  top: 0;
  left: 0;
  z-index: -1;
  background: url('@/assets/shouye.png') no-repeat center center fixed;
  background-size: cover;
}
.home-main {
  padding: 40px 0 0 0;
  min-height: 92vh;
  background: transparent;
}
.overlap-carousel-area {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 420px;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 6px 32px 0 rgba(52,152,219,0.10), 0 1.5px 6px 0 rgba(44,62,80,0.06);
  margin: 0 auto 40px auto;
  max-width: 1300px;
  position: relative;
}
.overlap-carousel-wrapper {
  width: 100%;
  overflow: visible;
  position: relative;
  height: 420px;
  display: flex;
  justify-content: center;
}
.overlap-carousel-track {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
  position: relative;
  width: auto;
}
.overlap-carousel-img {
  position: relative;
  z-index: 1;
  margin-left: -80px;
  transition: 
    filter 0.4s,
    transform 0.4s,
    z-index 0.2s;
  filter: blur(1.2px) grayscale(30%) brightness(0.92);
  transform: scale(0.82);
  box-shadow: 0 2px 12px rgba(0,0,0,0.10);
  border-radius: 18px;
  cursor: pointer;
}
.overlap-carousel-img:first-child {
  margin-left: 0;
}
.overlap-carousel-img img {
  width: 320px;
  height: 400px;
  object-fit: cover;
  border-radius: 18px;
  display: block;
}
.overlap-carousel-img.active {
  filter: none;
  transform: scale(1.08) translateY(-16px);
  z-index: 2;
  box-shadow: 0 8px 32px 0 rgba(52,152,219,0.18), 0 2px 8px 0 rgba(44,62,80,0.10);
}
/* 鼠标悬停时，未激活图片更虚化 */
.overlap-carousel-track:hover .overlap-carousel-img:not(.active) {
  filter: blur(2.5px) grayscale(50%) brightness(0.8);
  transform: scale(0.78);
  z-index: 1;
}

.music-player-top {
  margin: 40px auto 0 auto;
  max-width: 340px;
  min-width: 260px;
  position: static;
  box-shadow: 0 6px 32px 0 rgba(52,152,219,0.10), 0 1.5px 6px 0 rgba(44,62,80,0.06);
  border-radius: 18px;
  background: #fff;
  padding: 18px 18px 22px 18px;
}
.post-area-top {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}
.post-card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 6px 32px 0 rgba(52,152,219,0.10), 0 1.5px 6px 0 rgba(44,62,80,0.06);
  padding: 40px 48px;
  max-width: 900px;
  width: 100%;
}
.post-card h2 {
  font-size: 1.7rem;
  color: #2980b9;
  font-weight: 700;
  margin-bottom: 18px;
}
.post-card p {
  font-size: 1.1rem;
  color: #444;
  line-height: 1.8;
}
.music-info {
  display: flex;
  align-items: center;
  width: 100%;
  margin-bottom: 12px;
}
.cover-wrapper {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 18px;
  box-shadow: 0 2px 8px rgba(52,152,219,0.10);
}
.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}
.music-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.music-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #222;
  margin-bottom: 6px;
}
.progress-bar-bg {
  width: 180px;
  height: 6px;
  background: #e0e7ef;
  border-radius: 3px;
  overflow: hidden;
}
.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #4fc3f7, #2980b9);
  border-radius: 3px;
  transition: width 0.3s;
}
.music-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 32px;
  margin-top: 8px;
}
.music-btn {
  background: #f2f6fa;
  border: none;
  border-radius: 50%;
  width: 44px;
  height: 44px;
  font-size: 1.5rem;
  color: #2980b9;
  box-shadow: 0 2px 8px rgba(52,152,219,0.10);
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}
.music-btn:hover {
  background: #3498db;
  color: #fff;
}
.post-pre {
  font-size: 1.08rem;
  color: #444;
  background: none;
  border: none;
  padding: 0;
  margin: 0;
  line-height: 1.9;
  white-space: pre-wrap;
  word-break: break-all;
  font-family: inherit;
}
@media (max-width: 900px) {
  .content-flex-area {
    flex-direction: column;
    gap: 24px;
  }
  .music-player-side {
    max-width: 100%;
    min-width: 0;
  }
}
</style>

