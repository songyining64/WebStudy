<template>
  <div class="audio-player">
    <div class="music-info">
      <div class="cover-wrapper">
        <img :src="currentCover" class="cover-img" alt="音乐封面" />
      </div>
      <div class="music-meta">
        <div class="music-title">{{ musicList[currentIndex].title }}</div>
        <div class="music-artist">{{ musicList[currentIndex].artist || '未知艺术家' }}</div>
        <div class="time-display">
          <span class="current-time">{{ formatTime(currentTime) }}</span>
          <span class="total-time">{{ formatTime(duration) }}</span>
        </div>
        <div class="progress-bar-bg" @click="seekTo">
          <div class="progress-bar" :style="{ width: progressPercent + '%' }"></div>
          <div class="progress-handle" :style="{ left: progressPercent + '%' }"></div>
        </div>
      </div>
    </div>
    
    <div class="music-controls">
      <button @click="prevMusic" class="music-btn" title="上一首">
        <span>&#9664;&#9664;</span>
      </button>
      <button @click="togglePlay" class="music-btn play-btn" title="播放/暂停">
        <span v-if="!isPlaying">&#9654;</span>
        <span v-else>||</span>
      </button>
      <button @click="nextMusic" class="music-btn" title="下一首">
        <span>&#9654;&#9654;</span>
      </button>
    </div>
    
    <div class="music-extra-controls">
      <div class="play-mode-control">
        <button @click="togglePlayMode" class="mode-btn" :title="playModeText">
          <span v-if="playMode === 'loop'">🔁</span>
          <span v-else-if="playMode === 'shuffle'">🔀</span>
          <span v-else>🔂</span>
        </button>
      </div>
      
      <div class="volume-control">
        <button @click="toggleMute" class="volume-btn" :title="isMuted ? '取消静音' : '静音'">
          <span v-if="isMuted">🔇</span>
          <span v-else-if="volume < 0.5">🔉</span>
          <span v-else>🔊</span>
        </button>
        <div class="volume-slider" @click="setVolume">
          <div class="volume-bar">
            <div class="volume-fill" :style="{ width: (isMuted ? 0 : volume * 100) + '%' }"></div>
          </div>
        </div>
      </div>
      
      <div class="playlist-control">
        <button @click="togglePlaylist" class="playlist-btn" title="播放列表">
          <span>📋</span>
        </button>
      </div>
    </div>
    
    <audio 
      ref="audio" 
      :src="musicList[currentIndex].src" 
      @timeupdate="onTimeUpdate" 
      @ended="onEnded"
      @loadedmetadata="onLoadedMetadata"
      @error="onAudioError"
      preload="metadata"
    />
    
    <!-- 播放列表弹窗 -->
    <div class="playlist-modal" v-if="showPlaylist" @click.self="togglePlaylist">
      <div class="playlist-content">
        <div class="playlist-header">
          <h3>播放列表 ({{ musicList.length }})</h3>
          <button @click="togglePlaylist" class="close-playlist-btn">&times;</button>
        </div>
        <div class="playlist-items">
          <div 
            v-for="(song, index) in musicList" 
            :key="index"
            :class="['playlist-item', { active: index === currentIndex }]"
            @click="playSong(index)"
          >
            <div class="playlist-item-info">
              <img :src="song.cover" class="playlist-item-cover" alt="歌曲封面" />
              <div class="playlist-item-details">
                <div class="playlist-item-title">{{ song.title }}</div>
                <div class="playlist-item-artist">{{ song.artist || '未知艺术家' }}</div>
              </div>
            </div>
            <div class="playlist-item-status">
              <span v-if="index === currentIndex && isPlaying" class="playing-indicator">▶</span>
              <span v-else-if="index === currentIndex" class="paused-indicator">⏸</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AudioPlayer',
  props: {
    musicList: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      currentIndex: 0,
      isPlaying: false,
      progressPercent: 0,
      currentTime: 0,
      duration: 0,
      playMode: 'loop', // 'loop', 'shuffle', 'single'
      isMuted: false,
      volume: 0.5,
      showPlaylist: false
    }
  },
  computed: {
    currentCover() {
      return this.musicList[this.currentIndex]?.cover || '';
    },
    playModeText() {
      return this.playMode === 'loop' ? '循环播放' : this.playMode === 'shuffle' ? '随机播放' : '单曲循环';
    }
  },
  watch: {
    currentIndex(newVal, oldVal) {
      // 切换歌曲后，等待src变更后再播放，避免AbortError
      this.$nextTick(() => {
        const audio = this.$refs.audio;
        if (audio) {
          audio.currentTime = 0;
          const playPromise = audio.play();
          if (playPromise && playPromise.catch) {
            playPromise.catch(() => {});
          }
          this.isPlaying = true;
        }
      });
    }
  },
  methods: {
    formatTime(seconds) {
      if (isNaN(seconds)) return '0:00';
      const mins = Math.floor(seconds / 60);
      const secs = Math.floor(seconds % 60);
      return `${mins}:${secs.toString().padStart(2, '0')}`;
    },
    prevMusic() {
      if (this.playMode === 'shuffle') {
        this.currentIndex = Math.floor(Math.random() * this.musicList.length);
      } else {
        this.currentIndex = (this.currentIndex - 1 + this.musicList.length) % this.musicList.length;
      }
    },
    nextMusic() {
      if (this.playMode === 'shuffle') {
        this.currentIndex = Math.floor(Math.random() * this.musicList.length);
      } else {
        this.currentIndex = (this.currentIndex + 1) % this.musicList.length;
      }
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
    seekTo(e) {
      const audio = this.$refs.audio;
      const rect = e.target.getBoundingClientRect();
      const clickX = e.clientX - rect.left;
      const percentage = clickX / rect.width;
      audio.currentTime = percentage * audio.duration;
    },
    onTimeUpdate(e) {
      const audio = e.target;
      this.progressPercent = (audio.currentTime / audio.duration) * 100;
      this.currentTime = audio.currentTime;
      this.duration = audio.duration;
    },
    onEnded() {
      if (this.playMode === 'single') {
        // 单曲循环，重新播放当前歌曲
        this.$nextTick(() => {
          const audio = this.$refs.audio;
          audio.currentTime = 0;
          audio.play();
        });
      } else {
        // 循环播放或随机播放，播放下一首
        this.nextMusic();
      }
    },
    onLoadedMetadata(e) {
      this.duration = e.target.duration;
      // 设置音量
      e.target.volume = this.volume;
    },
    onAudioError(e) {
      console.error('音频加载错误:', e);
    },
    togglePlayMode() {
      const modes = ['loop', 'shuffle', 'single'];
      const currentIndex = modes.indexOf(this.playMode);
      this.playMode = modes[(currentIndex + 1) % modes.length];
    },
    toggleMute() {
      this.isMuted = !this.isMuted;
      const audio = this.$refs.audio;
      if (this.isMuted) {
        audio.volume = 0;
      } else {
        audio.volume = this.volume;
      }
    },
    setVolume(e) {
      const rect = e.target.getBoundingClientRect();
      const clickX = e.clientX - rect.left;
      const percentage = clickX / rect.width;
      this.volume = Math.max(0, Math.min(1, percentage));
      this.isMuted = false;
      
      const audio = this.$refs.audio;
      if (audio) {
        audio.volume = this.volume;
      }
    },
    togglePlaylist() {
      this.showPlaylist = !this.showPlaylist;
    },
    playSong(index) {
      this.currentIndex = index;
      this.showPlaylist = false;
    }
  }
}
</script>

<style scoped>
.audio-player {
  max-width: 1300px;
  margin: 0 auto;
  padding: 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.music-info {
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 400px;
}

.cover-wrapper {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 18px;
  box-shadow: 0 2px 8px rgba(52,152,219,0.10);
  flex-shrink: 0;
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
  margin-bottom: 4px;
}

.music-artist {
  font-size: 0.9rem;
  color: #777;
  margin-bottom: 8px;
}

.time-display {
  display: flex;
  justify-content: space-between;
  width: 100%;
  font-size: 0.8rem;
  color: #777;
  margin-bottom: 8px;
}

.progress-bar-bg {
  width: 100%;
  height: 6px;
  background: #e0e7ef;
  border-radius: 3px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #4fc3f7, #2980b9);
  border-radius: 3px;
  transition: width 0.3s;
}

.progress-handle {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #fff;
  position: absolute;
  top: 50%;
  left: 0;
  transform: translate(-50%, -50%);
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
}

.music-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 32px;
  width: 100%;
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
  display: flex;
  align-items: center;
  justify-content: center;
}

.music-btn:hover {
  background: #3498db;
  color: #fff;
}

.play-btn {
  width: 52px;
  height: 52px;
  font-size: 1.8rem;
}

.music-extra-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  width: 100%;
}

.play-mode-control,
.volume-control,
.playlist-control {
  display: flex;
  align-items: center;
}

.mode-btn,
.volume-btn,
.playlist-btn {
  background: #f2f6fa;
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  font-size: 1.2rem;
  color: #2980b9;
  box-shadow: 0 2px 8px rgba(52,152,219,0.10);
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mode-btn:hover,
.volume-btn:hover,
.playlist-btn:hover {
  background: #3498db;
  color: #fff;
}

.volume-control {
  gap: 8px;
}

.volume-slider {
  width: 60px;
  height: 6px;
  background: #e0e7ef;
  border-radius: 3px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
}

.volume-bar {
  height: 100%;
  width: 100%;
  position: relative;
}

.volume-fill {
  height: 100%;
  background: linear-gradient(90deg, #4fc3f7, #2980b9);
  border-radius: 3px;
  transition: width 0.3s;
}

/* 播放列表弹窗样式 */
.playlist-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.75);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.playlist-content {
  background: #fff;
  border-radius: 20px;
  max-width: 500px;
  width: 90%;
  height: 70vh;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
}

.playlist-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
  flex-shrink: 0;
}

.playlist-header h3 {
  font-size: 1.3rem;
  color: #2980b9;
  font-weight: 700;
  margin: 0;
}

.close-playlist-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #2c3e50;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.playlist-items {
  flex: 1;
  overflow-y: auto;
  padding: 0;
}

.playlist-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  cursor: pointer;
  border-bottom: 1px solid #f5f5f5;
  transition: background-color 0.2s;
}

.playlist-item:hover {
  background: #f8f9fa;
}

.playlist-item.active {
  background: #e3f2fd;
  border-left: 4px solid #2980b9;
}

.playlist-item-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.playlist-item-cover {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 15px;
}

.playlist-item-details {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.playlist-item-title {
  font-size: 1rem;
  color: #2c3e50;
  font-weight: 600;
  margin-bottom: 4px;
}

.playlist-item-artist {
  font-size: 0.85rem;
  color: #777;
}

.playlist-item-status {
  font-size: 1.2rem;
  color: #2980b9;
  margin-left: 10px;
}

.playing-indicator {
  color: #4fc3f7;
  animation: pulse 1.5s infinite;
}

.paused-indicator {
  color: #777;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .audio-player {
    padding: 16px;
    gap: 12px;
  }
  
  .music-info {
    max-width: 100%;
  }
  
  .music-controls {
    gap: 24px;
  }
  
  .music-extra-controls {
    gap: 12px;
  }
  
  .volume-slider {
    width: 40px;
  }
}
</style> 