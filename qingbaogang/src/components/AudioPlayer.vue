<template>
  <div class="audio-player">
    <div class="search-bar">
      <input v-model="searchQuery" placeholder="搜索歌名" @keyup.enter="searchMusic" />
      <button @click="searchMusic">搜索</button>
    </div>
    <div class="player-container">
      <div class="player-main" v-if="currentMusic">
        <img v-if="coverExists(currentMusic.coverUrl)" :src="getCoverUrl(currentMusic.coverUrl)" class="cover" alt="封面" @error="onCoverError" />
        <div class="info">
          <div class="title">{{ currentMusic.name }}</div>
          <div class="artist">{{ currentMusic.artist }}</div>
        </div>
        <audio ref="audio" :src="currentMusic.url" @ended="playNext" @timeupdate="updateTime" @loadedmetadata="updateDuration" />
        <div class="controls">
          <button @click="playPrev">⏮️</button>
          <button @click="togglePlay">{{ isPlaying ? '⏸️' : '▶️' }}</button>
          <button @click="playNext">⏭️</button>
        </div>
        <div class="progress">
          <span>{{ formatTime(currentTime) }}</span>
          <input type="range" min="0" :max="duration" step="0.1" v-model="currentTime" @input="seek" />
          <span>{{ formatTime(duration) }}</span>
        </div>
      </div>
      <div class="music-list">
        <div v-for="(music, idx) in filteredMusicList" :key="music.id" :class="['music-item', {active: idx === currentIndex}]" @click="selectMusic(idx)">
          <img v-if="coverExists(music.coverUrl)" :src="getCoverUrl(music.coverUrl)" class="list-cover" alt="封面" @error="onCoverError" />
          <div class="list-info">
            <span class="list-title">{{ music.name }}</span>
            <span class="list-artist">{{ music.artist }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed, watch } from 'vue';
import { getMusicList, searchMusicByName } from '../api/resourceApi';

const backendBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/mental';

function getCoverUrl(coverPath) {
  if (!coverPath) return '';
  if (coverPath.startsWith('http')) return coverPath;
  if (coverPath.startsWith('/')) {
    return backendBaseUrl + coverPath;
  }
  return coverPath;
}

function getAudioUrl(audioPath) {
  if (!audioPath) return '';
  if (audioPath.startsWith('http')) return audioPath;
  if (audioPath.startsWith('/')) {
    return backendBaseUrl + audioPath;
  }
  return audioPath;
}

export default {
  name: 'AudioPlayer',
  setup() {
    const musicList = ref([]);
    const filteredMusicList = ref([]);
    const currentIndex = ref(0);
    const isPlaying = ref(false);
    const currentTime = ref(0);
    const duration = ref(0);
    const searchQuery = ref('');
    const audio = ref(null);
    const erroredCovers = ref(new Set());

    const currentMusic = computed(() => {
      const music = filteredMusicList.value[currentIndex.value] || null;
      if (music) {
        return {
          ...music,
          url: getAudioUrl(music.url)
        };
      }
      return null;
    });

    const fetchMusicList = async () => {
      try {
        const res = await getMusicList();
        console.log('获取音乐列表结果:', res);
        if (res && res.data) {
          musicList.value = res.data;
          filteredMusicList.value = res.data;
        } else {
          console.error('获取音乐列表失败:', res);
        }
      } catch (error) {
        console.error('获取音乐列表出错:', error);
      }
    };

    const searchMusic = async () => {
      try {
        if (!searchQuery.value) {
          filteredMusicList.value = musicList.value;
          currentIndex.value = 0;
          return;
        }
        const res = await searchMusicByName(searchQuery.value);
        console.log('搜索音乐结果:', res);
        if (res && res.data) {
          filteredMusicList.value = res.data;
          currentIndex.value = 0;
        } else {
          console.error('搜索音乐失败:', res);
        }
      } catch (error) {
        console.error('搜索音乐出错:', error);
      }
    };

    const selectMusic = (idx) => {
      currentIndex.value = idx;
      play();
    };

    const play = () => {
      if (audio.value) {
        audio.value.play();
        isPlaying.value = true;
      }
    };
    const pause = () => {
      if (audio.value) {
        audio.value.pause();
        isPlaying.value = false;
      }
    };
    const togglePlay = () => {
      if (isPlaying.value) {
        pause();
      } else {
        play();
      }
    };
    const playNext = () => {
      if (currentIndex.value < filteredMusicList.value.length - 1) {
        currentIndex.value++;
      } else {
        currentIndex.value = 0;
      }
      play();
    };
    const playPrev = () => {
      if (currentIndex.value > 0) {
        currentIndex.value--;
      } else {
        currentIndex.value = filteredMusicList.value.length - 1;
      }
      play();
    };
    const updateTime = () => {
      if (audio.value) {
        currentTime.value = audio.value.currentTime;
      }
    };
    const updateDuration = () => {
      if (audio.value) {
        duration.value = audio.value.duration;
      }
    };
    const seek = () => {
      if (audio.value) {
        audio.value.currentTime = currentTime.value;
      }
    };
    const formatTime = (t) => {
      const m = Math.floor(t / 60);
      const s = Math.floor(t % 60);
      return `${m}:${s.toString().padStart(2, '0')}`;
    };

    function coverExists(coverUrl) {
      return coverUrl && !erroredCovers.value.has(coverUrl);
    }
    function onCoverError(e) {
      erroredCovers.value.add(e.target.src.replace(backendBaseUrl, ''));
      e.target.style.display = 'none';
    }

    watch(currentIndex, () => {
      if (audio.value) {
        audio.value.load();
        play();
      }
    });

    onMounted(() => {
      fetchMusicList();
    });

    return {
      musicList,
      filteredMusicList,
      currentIndex,
      isPlaying,
      currentTime,
      duration,
      searchQuery,
      audio,
      currentMusic,
      selectMusic,
      play,
      pause,
      togglePlay,
      playNext,
      playPrev,
      updateTime,
      updateDuration,
      seek,
      formatTime,
      searchMusic,
      getCoverUrl,
      getAudioUrl,
      coverExists,
      onCoverError,
    };
  },
};
</script>

<style scoped>
.audio-player {
  width: 100%;
  max-width: 800px;
  margin: 20px auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  padding: 24px;
}

.player-container {
  display: flex;
  gap: 24px;
}

.player-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.search-bar {
  margin-bottom: 24px;
  display: flex;
  gap: 10px;
}

.search-bar input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-bar button {
  padding: 8px 16px;
  background: #4a90e2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cover {
  width: 200px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.info {
  width: 100%;
  text-align: center;
  margin-bottom: 20px;
}

.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.artist {
  font-size: 16px;
  color: #666;
}

.controls {
  display: flex;
  gap: 20px;
  margin: 20px 0;
}

.controls button {
  font-size: 24px;
  padding: 10px;
  background: none;
  border: none;
  cursor: pointer;
  transition: transform 0.2s;
}

.controls button:hover {
  transform: scale(1.1);
}

.progress {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress input[type="range"] {
  flex: 1;
  height: 4px;
}

.progress span {
  font-size: 14px;
  color: #666;
  min-width: 45px;
}

.music-list {
  flex: 1;
  max-height: 500px;
  overflow-y: auto;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
}

.music-item {
  display: flex;
  align-items: center;
  padding: 12px;
  cursor: pointer;
  border-radius: 6px;
  transition: background-color 0.2s;
  margin-bottom: 8px;
}

.music-item:hover {
  background: #eee;
}

.music-item.active {
  background: #e3f2fd;
}

.list-cover {
  width: 48px;
  height: 48px;
  border-radius: 4px;
  margin-right: 12px;
}

.list-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.list-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.list-artist {
  font-size: 12px;
  color: #666;
}

/* 自定义滚动条样式 */
.music-list::-webkit-scrollbar {
  width: 6px;
}

.music-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.music-list::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

.music-list::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style> 