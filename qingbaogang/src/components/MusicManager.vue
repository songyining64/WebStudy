<template>
  <div class="music-manager">
    <div class="manager-header">
      <h2>音乐管理</h2>
      <button class="add-btn" @click="showAddModal = true">上传音乐</button>
    </div>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="musicList.length === 0" class="empty-state">暂无音乐，请上传</div>
    <div v-else class="music-table">
      <table>
        <thead>
          <tr>
            <th>歌名</th>
            <th>ID</th>
            <th>歌手</th>
            <th>封面</th>
            <th>音频</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="music in musicList" :key="music.id">
            <td>{{ music.name }}</td>
            <td>{{ music.id }}</td>
            <td>{{ music.artist }}</td>
            <td>
              <img v-if="music.coverUrl" :src="getCoverUrl(music.coverUrl)" alt="封面" style="max-width: 60px;" />
            </td>
            <td>
              <audio v-if="music.url" :src="getAudioUrl(music.url)" controls style="width: 120px;"></audio>
            </td>
            <td>
              <button class="action-btn delete" @click="deleteMusic(music.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <!-- 上传音乐弹窗 -->
    <div v-if="showAddModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>上传音乐</h3>
          <button class="close-btn" @click="showAddModal = false">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>歌名</label>
            <input v-model="form.title" type="text" placeholder="请输入歌名" />
          </div>
          <div class="form-group">
            <label>歌手</label>
            <input v-model="form.artist" type="text" placeholder="请输入歌手" />
          </div>
          <div class="form-group">
            <label>封面图片</label>
            <input type="file" @change="handleCoverChange" accept="image/*" />
            <div v-if="form.coverPreview">
              <img :src="form.coverPreview" alt="封面预览" style="max-width: 100px; margin-top: 8px;" />
            </div>
          </div>
          <div class="form-group">
            <label>音频文件</label>
            <input type="file" @change="handleAudioChange" accept="audio/*" />
            <div v-if="form.audioPreview">
              <audio :src="form.audioPreview" controls style="width: 180px; margin-top: 8px;"></audio>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showAddModal = false">取消</button>
          <button class="save-btn" @click="uploadMusic">上传</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getMusicList, uploadMusic, deleteMusic } from '@/api/resourceApi';

const backendBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/mental';

function getCoverUrl(coverPath) {
  if (!coverPath) return '';
  if (coverPath.startsWith('http')) return coverPath;
  // 如果是相对路径，添加后端基础URL
  if (coverPath.startsWith('/')) {
    return backendBaseUrl + coverPath;
  }
  return coverPath;
}

function getAudioUrl(audioPath) {
  if (!audioPath) return '';
  if (audioPath.startsWith('http')) return audioPath;
  // 如果是相对路径，添加后端基础URL
  if (audioPath.startsWith('/')) {
    return backendBaseUrl + audioPath;
  }
  return audioPath;
}

export default {
  name: 'MusicManager',
  data() {
    return {
      loading: false,
      musicList: [],
      showAddModal: false,
      form: {
        title: '',
        artist: '',
        cover: null,
        coverPreview: '',
        audio: null,
        audioPreview: ''
      }
    }
  },
  methods: {
    async fetchMusic() {
      this.loading = true;
      try {
        const res = await getMusicList();
        this.musicList = Array.isArray(res.data) ? res.data : [];
      } finally {
        this.loading = false;
      }
    },
    handleCoverChange(e) {
      const file = e.target.files[0];
      this.form.cover = file;
      this.form.coverPreview = file ? URL.createObjectURL(file) : '';
    },
    handleAudioChange(e) {
      const file = e.target.files[0];
      this.form.audio = file;
      this.form.audioPreview = file ? URL.createObjectURL(file) : '';
    },
    async uploadMusic() {
      if (!this.form.title || !this.form.artist || !this.form.audio) {
        alert('歌名、歌手、音频文件为必填项');
        return;
      }
      const formData = new FormData();
      formData.append('title', this.form.title);
      formData.append('artist', this.form.artist);
      if (this.form.cover) formData.append('cover', this.form.cover);
      formData.append('audio', this.form.audio);
      await uploadMusic(formData);
      this.showAddModal = false;
      this.form = { title: '', artist: '', cover: null, coverPreview: '', audio: null, audioPreview: '' };
      this.fetchMusic();
    },
    async deleteMusic(id) {
      if (!confirm('确定要删除该音乐吗？')) return;
      await deleteMusic(id);
      this.fetchMusic();
    },
    getCoverUrl,
    getAudioUrl
  },
  mounted() {
    this.fetchMusic();
  }
}
</script>

<style scoped>
.music-manager { padding: 18px; }
.manager-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 18px; }
.add-btn { background: #3498db; color: #fff; border: none; border-radius: 4px; padding: 8px 18px; cursor: pointer; font-size: 1rem; }
.add-btn:hover { background: #217dbb; }
.music-table { margin-top: 18px; }
table { width: 100%; border-collapse: collapse; }
th, td { border: 1px solid #e0e6ed; padding: 8px 12px; text-align: center; }
th { background: #f7fafd; }
.action-btn { background: #e74c3c; color: #fff; border: none; border-radius: 4px; padding: 4px 12px; cursor: pointer; font-size: 0.95rem; }
.action-btn:hover { background: #c0392b; }
.loading { text-align: center; color: #888; margin: 32px 0; }
.empty-state { text-align: center; color: #aaa; margin: 32px 0; }
.modal { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-content { background: #fff; border-radius: 10px; width: 400px; max-width: 95vw; padding: 24px; position: relative; }
.modal-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #888; }
.close-btn:hover { color: #e74c3c; }
.form-group { margin-bottom: 16px; display: flex; flex-direction: column; align-items: flex-start; }
.form-group label { font-weight: 500; margin-bottom: 6px; }
.form-group input[type="text"], .form-group textarea { width: 100%; padding: 6px 10px; border: 1px solid #e0e6ed; border-radius: 4px; font-size: 1rem; }
.form-group textarea { resize: vertical; min-height: 60px; }
.cancel-btn, .save-btn { background: #3498db; color: #fff; border: none; border-radius: 4px; padding: 7px 18px; cursor: pointer; font-size: 1rem; margin-right: 8px; }
.cancel-btn:hover, .save-btn:hover { background: #217dbb; }
</style> 