<template>
  <div class="resource-manager">
    <div class="manager-header">
      <h2>资源推荐管理</h2>
    </div>

    <div class="resource-tabs">
      <div class="tab-buttons">
        <button 
          :class="['tab-btn', { active: activeTab === 'videos' }]" 
          @click="activeTab = 'videos'"
        >
          视频资源
        </button>
        <button 
          :class="['tab-btn', { active: activeTab === 'texts' }]" 
          @click="activeTab = 'texts'"
        >
          文案资源
        </button>
      </div>
    </div>

    <!-- 视频资源管理 -->
    <div v-if="activeTab === 'videos'" class="tab-content">
      <div class="action-bar">
        <button class="add-btn" @click="openVideoModal()">添加视频资源</button>
      </div>

      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="videos.length === 0" class="empty-state">
        暂无视频资源，请点击"添加视频资源"按钮添加
      </div>
      
      <div v-else class="resource-table">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>标题</th>
              <th>缩略图</th>
              <th>情绪标签</th>
              <th>上传时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="video in videos" :key="video.id">
              <td>{{ video.id }}</td>
              <td>{{ video.title }}</td>
              <td>
                <img 
                  :src="video.thumbnailUrl || defaultThumbnail" 
                  alt="缩略图" 
                  class="thumbnail"
                />
              </td>
              <td>
                <span :class="['tag', getTagClass(video.emotionTag)]">
                  {{ getEmotionTagText(video.emotionTag) }}
                </span>
              </td>
              <td>{{ formatDate(video.uploadTime) }}</td>
              <td>
                <button class="action-btn edit" @click="openVideoModal(video)">编辑</button>
                <button class="action-btn delete" @click="confirmDelete('video', video.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 文案资源管理 -->
    <div v-if="activeTab === 'texts'" class="tab-content">
      <div class="action-bar">
        <button class="add-btn" @click="openTextModal()">添加文案资源</button>
      </div>

      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="texts.length === 0" class="empty-state">
        暂无文案资源，请点击"添加文案资源"按钮添加
      </div>
      
      <div v-else class="resource-table">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>标题</th>
              <th>图片</th>
              <th>情绪标签</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="text in texts" :key="text.id">
              <td>{{ text.id }}</td>
              <td>{{ text.title }}</td>
              <td>
                <img v-if="text.imageUrl" :src="text.imageUrl" alt="图片" style="max-width: 60px;" />
              </td>
              <td>
                <span :class="['tag', getTagClass(text.emotionTag)]">
                  {{ getEmotionTagText(text.emotionTag) }}
                </span>
              </td>
              <td>{{ formatDate(text.createTime) }}</td>
              <td>
                <button class="action-btn edit" @click="openTextModal(text)">编辑</button>
                <button class="action-btn delete" @click="confirmDelete('text', text.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 视频编辑弹窗 -->
    <div v-if="showVideoModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingVideo.id ? '编辑视频资源' : '添加视频资源' }}</h3>
          <button class="close-btn" @click="closeVideoModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>标题</label>
            <input v-model="editingVideo.title" type="text" placeholder="请输入视频标题" />
          </div>
          <div class="form-group">
            <label>描述</label>
            <textarea v-model="editingVideo.description" placeholder="请输入视频描述"></textarea>
          </div>
          <div class="form-group">
            <label>视频URL</label>
            <input v-model="editingVideo.url" type="text" placeholder="请输入视频URL" />
          </div>
          <div class="form-group">
            <label>缩略图URL</label>
            <input v-model="editingVideo.thumbnailUrl" type="text" placeholder="请输入缩略图URL" />
          </div>
          <div class="form-group">
            <label>情绪标签</label>
            <select v-model="editingVideo.emotionTag">
              <option value="low_risk">轻松放松</option>
              <option value="medium_risk">情绪调节</option>
              <option value="high_risk">深度关怀</option>
              <option value="default">通用资源</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeVideoModal">取消</button>
          <button class="save-btn" @click="saveVideo">保存</button>
        </div>
      </div>
    </div>

    <!-- 文案编辑弹窗 -->
    <div v-if="showTextModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingText.id ? '编辑文案资源' : '添加文案资源' }}</h3>
          <button class="close-btn" @click="closeTextModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>标题</label>
            <input v-model="editingText.title" type="text" placeholder="请输入文案标题" />
          </div>
          <div class="form-group">
            <label>内容</label>
            <textarea v-model="editingText.content" placeholder="请输入文案内容" rows="6"></textarea>
          </div>
          <div class="form-group">
            <label>情绪标签</label>
            <select v-model="editingText.emotionTag">
              <option value="low_risk">轻松放松</option>
              <option value="medium_risk">情绪调节</option>
              <option value="high_risk">深度关怀</option>
              <option value="default">通用资源</option>
            </select>
          </div>
          <div class="form-group">
            <label>图片</label>
            <input type="file" @change="handleTextImageUpload" />
            <div v-if="editingText.imageUrl">
              <img :src="editingText.imageUrl" alt="图片预览" style="max-width: 100px; margin-top: 8px;" />
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeTextModal">取消</button>
          <button class="save-btn" @click="saveText">保存</button>
        </div>
      </div>
    </div>

    <!-- 确认删除弹窗 -->
    <div v-if="showDeleteConfirm" class="modal">
      <div class="modal-content confirm-modal">
        <div class="modal-header">
          <h3>确认删除</h3>
          <button class="close-btn" @click="showDeleteConfirm = false">&times;</button>
        </div>
        <div class="modal-body">
          <p>确定要删除这个资源吗？此操作不可撤销。</p>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showDeleteConfirm = false">取消</button>
          <button class="delete-btn" @click="executeDelete">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { 
  getAllVideos, addVideo, updateVideo, deleteVideo,
  getAllTexts, addText, updateText, deleteText
} from '@/api/resourceApi';
import { useUserStore } from '../stores/user';
import { ElMessage } from 'element-plus';
import { Search, Plus, Edit, Delete, Close, Check, Warning } from '@element-plus/icons-vue';

export default {
  name: 'ResourceManager',
  components: {
    Search,
    Plus,
    Edit,
    Delete,
    Close,
    Check,
    Warning
  },
  data() {
    return {
      activeTab: 'videos',
      loading: false,
      videos: [],
      texts: [],
      showVideoModal: false,
      showTextModal: false,
      showDeleteConfirm: false,
      editingVideo: {
        title: '',
        description: '',
        url: '',
        thumbnailUrl: '',
        emotionTag: 'default'
      },
      editingText: {
        title: '',
        content: '',
        emotionTag: 'default'
      },
      deleteType: '',
      deleteId: null,
      defaultThumbnail: 'https://via.placeholder.com/150'
    };
  },
  created() {
    this.fetchResources();
  },
  methods: {
    async fetchResources() {
      this.loading = true;
      try {
        console.log('开始获取资源...');
        
        // 获取视频资源
        try {
          console.log('获取视频资源...');
          const videoResponse = await getAllVideos();
          console.log('视频资源响应:', videoResponse);
          
          if (videoResponse && videoResponse.code === 200) {
            this.videos = videoResponse.data || [];
            console.log('获取到视频资源:', this.videos.length);
          } else {
            console.error('获取视频资源失败:', videoResponse);
            ElMessage.error('获取视频资源失败');
          }
        } catch (videoError) {
          console.error('获取视频资源出错:', videoError);
          ElMessage.error(`获取视频资源出错: ${videoError.message || '未知错误'}`);
        }
        
        // 获取文案资源
        try {
          console.log('获取文案资源...');
          const textResponse = await getAllTexts();
          console.log('文案资源响应:', textResponse);
          
          if (textResponse && textResponse.code === 200) {
            this.texts = textResponse.data || [];
            console.log('获取到文案资源:', this.texts.length);
          } else {
            console.error('获取文案资源失败:', textResponse);
            ElMessage.error('获取文案资源失败');
          }
        } catch (textError) {
          console.error('获取文案资源出错:', textError);
          ElMessage.error(`获取文案资源出错: ${textError.message || '未知错误'}`);
        }
      } catch (error) {
        console.error('获取资源出错:', error);
        ElMessage.error('获取资源出错');
      } finally {
        this.loading = false;
      }
    },
    
    openVideoModal(video = null) {
      if (video) {
        // 编辑现有视频
        this.editingVideo = { ...video };
      } else {
        // 添加新视频
        this.editingVideo = {
          title: '',
          description: '',
          url: '',
          thumbnailUrl: '',
          emotionTag: 'default'
        };
      }
      this.showVideoModal = true;
    },
    
    closeVideoModal() {
      this.showVideoModal = false;
    },
    
    openTextModal(text = null) {
      if (text) {
        // 编辑现有文案
        this.editingText = { ...text };
      } else {
        // 添加新文案
        this.editingText = {
          title: '',
          content: '',
          emotionTag: 'default'
        };
      }
      this.showTextModal = true;
    },
    
    closeTextModal() {
      this.showTextModal = false;
    },
    
    async saveVideo() {
      try {
        const userStore = useUserStore();
        // 验证必填字段
        if (!this.editingVideo.title || !this.editingVideo.url) {
          ElMessage.error('标题和视频URL为必填项');
          return;
        }
        // 构造请求体，字段名严格按API文档，保留uploaderId
        const payload = {
          title: this.editingVideo.title,
          description: this.editingVideo.description,
          url: this.editingVideo.url,
          thumbnailUrl: this.editingVideo.thumbnailUrl,
          emotionTag: this.editingVideo.emotionTag || 'default',
          uploaderId: userStore.userId || null
        };
        // 如果是编辑，带上id
        if (this.editingVideo.id) {
          payload.id = this.editingVideo.id;
        }
        console.log('准备保存的视频数据:', JSON.stringify(payload));
        let response;
        try {
          if (this.editingVideo.id) {
            response = await updateVideo(payload);
          } else {
            response = await addVideo(payload);
          }
          console.log('保存视频响应:', response);
          // 判断 success 字段或 code 字段
          if ((response && response.success) || response.code === 200) {
            ElMessage.success(this.editingVideo.id ? '更新成功' : '添加成功');
            this.closeVideoModal();
            this.fetchResources();
          } else {
            ElMessage.error(this.editingVideo.id ? '更新失败' : '添加失败');
            console.error('保存视频失败，响应:', response);
          }
        } catch (apiError) {
          console.error('API调用出错:', apiError);
          ElMessage.error(`操作失败: ${apiError.message || '未知错误'}`);
        }
      } catch (error) {
        console.error('保存视频出错:', error);
        ElMessage.error('操作失败');
      }
    },
    
    async handleTextImageUpload(event) {
      const file = event.target.files[0];
      if (!file) return;
      const formData = new FormData();
      formData.append('file', file);
      try {
        const res = await fetch('/api/resource/upload', {
          method: 'POST',
          body: formData
        });
        const result = await res.json();
        if (result.code === 200 && result.data) {
          this.editingText.imageUrl = result.data;
          this.$message.success('图片上传成功');
        } else {
          this.$message.error('图片上传失败');
        }
      } catch (e) {
        this.$message.error('图片上传失败');
      }
    },
    
    async saveText() {
      try {
        const userStore = useUserStore();
        if (!this.editingText.title || !this.editingText.content) {
          ElMessage.error('标题和内容为必填项');
          return;
        }
        const payload = {
          title: this.editingText.title,
          content: this.editingText.content,
          emotionTag: this.editingText.emotionTag || 'default',
          authorId: userStore.userId || null,
          imageUrl: this.editingText.imageUrl || ''
        };
        if (this.editingText.id) {
          payload.id = this.editingText.id;
        }
        console.log('准备保存的文案数据:', JSON.stringify(payload));
        let response;
        try {
          if (this.editingText.id) {
            response = await updateText(payload);
          } else {
            response = await addText(payload);
          }
          console.log('保存文案响应:', response);
          if (response && response.code === 200) {
            ElMessage.success(this.editingText.id ? '更新成功' : '添加成功');
            this.closeTextModal();
            this.fetchResources();
          } else {
            ElMessage.error(this.editingText.id ? '更新失败' : '添加失败');
            console.error('保存文案失败，响应:', response);
          }
        } catch (apiError) {
          console.error('API调用出错:', apiError);
          ElMessage.error(`操作失败: ${apiError.message || '未知错误'}`);
        }
      } catch (error) {
        console.error('保存文案出错:', error);
        ElMessage.error('操作失败');
      }
    },
    
    confirmDelete(type, id) {
      this.deleteType = type;
      this.deleteId = id;
      this.showDeleteConfirm = true;
    },
    
    async executeDelete() {
      try {
        let response;
        
        if (this.deleteType === 'video') {
          response = await deleteVideo(this.deleteId);
        } else if (this.deleteType === 'text') {
          response = await deleteText(this.deleteId);
        }
        
        if (response && response.code === 200) {
          ElMessage.success('删除成功');
          this.fetchResources();
        } else {
          ElMessage.error('删除失败');
        }
      } catch (error) {
        console.error('删除资源出错:', error);
        ElMessage.error('删除失败');
      } finally {
        this.showDeleteConfirm = false;
      }
    },
    
    getTagClass(tag) {
      switch (tag) {
        case 'low_risk':
          return 'tag-low';
        case 'medium_risk':
          return 'tag-medium';
        case 'high_risk':
          return 'tag-high';
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
    
    formatDate(dateString) {
      if (!dateString) return '未知时间';
      
      try {
        const date = new Date(dateString);
        return date.toLocaleDateString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        });
      } catch (e) {
        return dateString;
      }
    }
  }
};
</script>

<style scoped>
.resource-manager {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.manager-header {
  margin-bottom: 20px;
}

.manager-header h2 {
  font-size: 1.8rem;
  color: #2c3e50;
  margin-bottom: 15px;
}

.resource-tabs {
  margin-bottom: 20px;
}

.tab-buttons {
  display: flex;
  border-bottom: 1px solid #e0e0e0;
  margin-bottom: 20px;
}

.tab-btn {
  padding: 10px 20px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  color: #606266;
  position: relative;
  transition: all 0.3s;
}

.tab-btn.active {
  color: #409eff;
  font-weight: 500;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #409eff;
}

.action-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.add-btn {
  background-color: #67c23a;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.3s;
  display: flex;
  align-items: center;
}

.add-btn::before {
  content: "+";
  margin-right: 5px;
  font-size: 1.2rem;
  font-weight: bold;
}

.add-btn:hover {
  background-color: #85ce61;
}

.resource-table {
  width: 100%;
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #ebeef5;
}

th, td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 500;
}

.thumbnail {
  width: 80px;
  height: 45px;
  object-fit: cover;
  border-radius: 4px;
}

.action-btn {
  padding: 5px 10px;
  margin-right: 5px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
}

.edit {
  background-color: #409eff;
  color: white;
}

.edit:hover {
  background-color: #66b1ff;
}

.delete {
  background-color: #f56c6c;
  color: white;
}

.delete:hover {
  background-color: #f78989;
}

.tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
}

.tag-low {
  background-color: #e1f3d8;
  color: #67c23a;
}

.tag-medium {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.tag-high {
  background-color: #fef0f0;
  color: #f56c6c;
}

.tag-default {
  background-color: #f4f4f5;
  color: #909399;
}

.loading, .empty-state {
  text-align: center;
  padding: 30px;
  color: #909399;
  background-color: #f8f8f9;
  border-radius: 4px;
}

/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 500px;
  max-width: 90%;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.confirm-modal {
  width: 400px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #303133;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #909399;
  cursor: pointer;
}

.modal-body {
  padding: 20px;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #606266;
  font-weight: 500;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 0.9rem;
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.modal-footer {
  padding: 10px 20px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: flex-end;
}

.cancel-btn,
.save-btn,
.delete-btn {
  padding: 8px 20px;
  margin-left: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn {
  background-color: #f4f4f5;
  color: #606266;
}

.save-btn {
  background-color: #409eff;
  color: white;
}

.delete-btn {
  background-color: #f56c6c;
  color: white;
}

@media (max-width: 768px) {
  .modal-content {
    width: 95%;
  }
  
  th, td {
    padding: 8px;
  }
  
  .thumbnail {
    width: 60px;
    height: 34px;
  }
}
</style> 