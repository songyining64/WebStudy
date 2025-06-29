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
      <h2>资源推荐内容区</h2>
      <p>这里可以展示资源推荐的详细内容。</p>
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
  background: #f8fafc;
  min-height: 92vh;
}
.arc-carousel-area {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 600px;
  background: #fff;
  margin: 0 auto 0 auto;
  max-width: 1600px;
  position: relative;
  border-radius: 0;
  box-shadow: none;
  border-bottom: none;
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
  max-width: 900px;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 6px 32px 0 rgba(52,152,219,0.10), 0 1.5px 6px 0 rgba(44,62,80,0.06);
  padding: 40px 48px;
}
.resource-content h2 {
  font-size: 1.7rem;
  color: #2980b9;
  font-weight: 700;
  margin-bottom: 18px;
}
.resource-content p {
  font-size: 1.1rem;
  color: #444;
  line-height: 1.8;
}
</style> 