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
</template>

<script>
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
      centerIndex: 3 // 默认中间图片
    }
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

.content-section {
  background: rgba(245, 240, 230, 0.95);
  border-radius: 18px;
  box-shadow: 0 6px 32px 0 rgba(52,152,219,0.10);
  padding: 40px;
  margin-bottom: 40px;
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
}

.image-text-block {
  display: flex;
  gap: 40px;
  align-items: center;
}

.image-text-block.reverse {
  flex-direction: row-reverse;
}

.image-container {
  flex: 0 0 45%;
}

.content-image {
  width: 100%;
  border-radius: 12px;
  object-fit: cover;
}

.text-content {
  flex: 1;
}

.text-content h2 {
  font-size: 2rem;
  color: #2c3e50;
  margin-bottom: 20px;
}

.highlight {
  color: #34495e;
  margin-bottom: 15px;
  line-height: 1.8;
}

.poetry-block {
  text-align: center;
  padding: 20px;
}

.poetry-block h3 {
  font-size: 1.8rem;
  color: #2c3e50;
  margin-bottom: 30px;
}

.poetry-content {
  font-size: 1.1rem;
  line-height: 2;
  color: #34495e;
}

.quote-block {
  text-align: center;
  padding: 40px 20px;
}

.quote-text {
  font-size: 1.2rem;
  line-height: 1.8;
}

.english-quote {
  color: #7f8c8d;
  margin-bottom: 10px;
}

.chinese-quote {
  color: #34495e;
  font-size: 1.3rem;
  margin-top: 20px;
}

.elegant-text {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #34495e;
  margin-bottom: 20px;
}

.message-block {
  background: rgba(245, 240, 230, 0.95);
  border-radius: 15px;
  padding: 30px;
}

.icon-message {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.plant-icon {
  width: 60px;
  height: 60px;
  background: #e8f4f8;
  border-radius: 50%;
}

.message-text {
  flex: 1;
  font-size: 1.1rem;
  line-height: 1.8;
  color: #34495e;
}

.signature {
  color: #7f8c8d;
  font-size: 0.9rem;
  margin-top: 15px;
}

.tips-block {
  padding: 20px;
}

.tips-block h3 {
  font-size: 1.4rem;
  color: #2c3e50;
  margin-bottom: 25px;
  text-align: center;
}

.tips-content {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #34495e;
}

.tips-content ol {
  margin: 15px 0;
  padding-left: 20px;
}

.footer-quote {
  text-align: center;
  padding: 40px 20px;
  font-size: 1.2rem;
  line-height: 1.8;
}

.footer-quote p {
  margin-bottom: 10px;
}

.footer-quote .english-quote {
  color: #7f8c8d;
  font-size: 1rem;
}

.workshop-list, .mindfulness-tips ul, .resource-links ul {
  list-style: none;
  padding-left: 20px;
  margin: 15px 0;
}

.workshop-list li, .mindfulness-tips li, .resource-links li {
  margin: 10px 0;
  color: #34495e;
  position: relative;
  padding-left: 25px;
}

.workshop-list li:before, .mindfulness-tips li:before, .resource-links li:before {
  content: "•";
  color: #3498db;
  font-size: 1.5em;
  position: absolute;
  left: 0;
  top: -3px;
}

.mindfulness-tips h4, .resource-links h4 {
  color: #2c3e50;
  margin: 20px 0 15px;
  font-size: 1.2rem;
}

.message-block .image-container {
  margin: 20px 0;
  border-radius: 12px;
  overflow: hidden;
}

.message-block ul {
  list-style: none;
  padding-left: 20px;
  margin: 15px 0;
}

.message-block li {
  margin: 10px 0;
  color: #34495e;
  position: relative;
  padding-left: 25px;
}

.message-block li:before {
  content: "✓";
  color: #3498db;
  position: absolute;
  left: 0;
}

.message-block h3 {
  color: #2c3e50;
  margin-bottom: 15px;
  font-size: 1.4rem;
}
</style> 