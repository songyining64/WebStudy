<!-- ResourceManagement.vue -->
<template>
  <div class="resource-management">
    <h1>资源管理</h1>

    <!-- Tabs for switching between videos and texts -->
    <el-tabs v-model="activeTab" class="resource-tabs">
      <el-tab-pane label="视频资源" name="videos">
        <!-- Video Resources Management -->
        <div class="resource-section">
          <div class="section-header">
            <h2>视频资源列表</h2>
            <el-button type="primary" @click="showVideoDialog('add')">添加视频</el-button>
          </div>

          <el-table :data="videos" style="width: 100%" v-loading="loading">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="description" label="描述" show-overflow-tooltip />
            <el-table-column prop="emotionTag" label="情绪标签">
              <template #default="{ row }">
                <el-tag :type="getEmotionTagType(row.emotionTag)">
                  {{ getEmotionTagLabel(row.emotionTag) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button-group>
                  <el-button type="primary" @click="showVideoDialog('edit', row)">编辑</el-button>
                  <el-button type="danger" @click="handleDeleteVideo(row)">删除</el-button>
                </el-button-group>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="文案资源" name="texts">
        <!-- Text Resources Management -->
        <div class="resource-section">
          <div class="section-header">
            <h2>文案资源列表</h2>
            <el-button type="primary" @click="showTextDialog('add')">添加文案</el-button>
          </div>

          <el-table :data="texts" style="width: 100%" v-loading="loading">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column prop="emotionTag" label="情绪标签">
              <template #default="{ row }">
                <el-tag :type="getEmotionTagType(row.emotionTag)">
                  {{ getEmotionTagLabel(row.emotionTag) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button-group>
                  <el-button type="primary" @click="showTextDialog('edit', row)">编辑</el-button>
                  <el-button type="danger" @click="handleDeleteText(row)">删除</el-button>
                </el-button-group>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- Video Dialog -->
    <el-dialog
      :title="dialogMode === 'add' ? '添加视频资源' : '编辑视频资源'"
      v-model="videoDialogVisible"
      width="50%"
    >
      <el-form :model="videoForm" label-width="100px" ref="videoFormRef">
        <el-form-item label="标题" prop="title" :rules="[{ required: true, message: '请输入标题' }]">
          <el-input v-model="videoForm.title" />
        </el-form-item>
        <el-form-item label="描述" prop="description" :rules="[{ required: true, message: '请输入描述' }]">
          <el-input type="textarea" v-model="videoForm.description" rows="3" />
        </el-form-item>
        <el-form-item label="视频URL" prop="url" :rules="[{ required: true, message: '请输入视频URL' }]">
          <el-input v-model="videoForm.url" />
        </el-form-item>
        <el-form-item label="缩略图URL" prop="thumbnailUrl" :rules="[{ required: true, message: '请输入缩略图URL' }]">
          <el-input v-model="videoForm.thumbnailUrl" />
        </el-form-item>
        <el-form-item label="情绪标签" prop="emotionTag" :rules="[{ required: true, message: '请选择情绪标签' }]">
          <el-select v-model="videoForm.emotionTag">
            <el-option label="低风险" value="low_risk" />
            <el-option label="中等风险" value="medium_risk" />
            <el-option label="高风险" value="high_risk" />
            <el-option label="默认" value="default" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="videoDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleVideoSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- Text Dialog -->
    <el-dialog
      :title="dialogMode === 'add' ? '添加文案资源' : '编辑文案资源'"
      v-model="textDialogVisible"
      width="50%"
    >
      <el-form :model="textForm" label-width="100px" ref="textFormRef">
        <el-form-item label="标题" prop="title" :rules="[{ required: true, message: '请输入标题' }]">
          <el-input v-model="textForm.title" />
        </el-form-item>
        <el-form-item label="内容" prop="content" :rules="[{ required: true, message: '请输入内容' }]">
          <el-input type="textarea" v-model="textForm.content" rows="5" />
        </el-form-item>
        <el-form-item label="情绪标签" prop="emotionTag" :rules="[{ required: true, message: '请选择情绪标签' }]">
          <el-select v-model="textForm.emotionTag">
            <el-option label="低风险" value="low_risk" />
            <el-option label="中等风险" value="medium_risk" />
            <el-option label="高风险" value="high_risk" />
            <el-option label="默认" value="default" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="textDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleTextSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAllVideos,
  getAllTexts,
  addVideo,
  updateVideo,
  deleteVideo,
  addText,
  updateText,
  deleteText
} from '@/api/resourceApi'

export default {
  name: 'ResourceManagement',
  setup() {
    const activeTab = ref('videos')
    const loading = ref(false)
    const videos = ref([])
    const texts = ref([])
    const videoDialogVisible = ref(false)
    const textDialogVisible = ref(false)
    const dialogMode = ref('add')
    const videoFormRef = ref(null)
    const textFormRef = ref(null)

    const videoForm = ref({
      title: '',
      description: '',
      url: '',
      thumbnailUrl: '',
      emotionTag: 'default'
    })

    const textForm = ref({
      title: '',
      content: '',
      emotionTag: 'default'
    })

    // 获取所有资源
    const fetchResources = async () => {
      loading.value = true
      try {
        const [videoRes, textRes] = await Promise.all([getAllVideos(), getAllTexts()])
        if (videoRes.success) {
          videos.value = videoRes.data
        }
        if (textRes.success) {
          texts.value = textRes.data
        }
      } catch (error) {
        ElMessage.error('获取资源列表失败')
        console.error('Error fetching resources:', error)
      } finally {
        loading.value = false
      }
    }

    // 显示视频对话框
    const showVideoDialog = (mode, row = null) => {
      dialogMode.value = mode
      if (mode === 'edit' && row) {
        videoForm.value = { ...row }
      } else {
        videoForm.value = {
          title: '',
          description: '',
          url: '',
          thumbnailUrl: '',
          emotionTag: 'default'
        }
      }
      videoDialogVisible.value = true
    }

    // 显示文案对话框
    const showTextDialog = (mode, row = null) => {
      dialogMode.value = mode
      if (mode === 'edit' && row) {
        textForm.value = { ...row }
      } else {
        textForm.value = {
          title: '',
          content: '',
          emotionTag: 'default'
        }
      }
      textDialogVisible.value = true
    }

    // 处理视频表单提交
    const handleVideoSubmit = async () => {
      if (!videoFormRef.value) return
      await videoFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            const action = dialogMode.value === 'add' ? addVideo : updateVideo
            const res = await action(videoForm.value)
            if (res.success) {
              ElMessage.success(dialogMode.value === 'add' ? '添加成功' : '更新成功')
              videoDialogVisible.value = false
              fetchResources()
            }
          } catch (error) {
            ElMessage.error('操作失败')
            console.error('Error submitting video:', error)
          }
        }
      })
    }

    // 处理文案表单提交
    const handleTextSubmit = async () => {
      if (!textFormRef.value) return
      await textFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            const action = dialogMode.value === 'add' ? addText : updateText
            const res = await action(textForm.value)
            if (res.success) {
              ElMessage.success(dialogMode.value === 'add' ? '添加成功' : '更新成功')
              textDialogVisible.value = false
              fetchResources()
            }
          } catch (error) {
            ElMessage.error('操作失败')
            console.error('Error submitting text:', error)
          }
        }
      })
    }

    // 处理删除视频
    const handleDeleteVideo = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除这个视频资源吗？', '警告', {
          type: 'warning'
        })
        const res = await deleteVideo(row.id)
        if (res.success) {
          ElMessage.success('删除成功')
          fetchResources()
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
          console.error('Error deleting video:', error)
        }
      }
    }

    // 处理删除文案
    const handleDeleteText = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除这个文案资源吗？', '警告', {
          type: 'warning'
        })
        const res = await deleteText(row.id)
        if (res.success) {
          ElMessage.success('删除成功')
          fetchResources()
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
          console.error('Error deleting text:', error)
        }
      }
    }

    // 获取情绪标签类型
    const getEmotionTagType = (tag) => {
      const types = {
        low_risk: 'success',
        medium_risk: 'warning',
        high_risk: 'danger',
        default: 'info'
      }
      return types[tag] || 'info'
    }

    // 获取情绪标签显示文本
    const getEmotionTagLabel = (tag) => {
      const labels = {
        low_risk: '低风险',
        medium_risk: '中等风险',
        high_risk: '高风险',
        default: '默认'
      }
      return labels[tag] || '未知'
    }

    onMounted(() => {
      fetchResources()
    })

    return {
      activeTab,
      loading,
      videos,
      texts,
      videoDialogVisible,
      textDialogVisible,
      dialogMode,
      videoForm,
      textForm,
      videoFormRef,
      textFormRef,
      showVideoDialog,
      showTextDialog,
      handleVideoSubmit,
      handleTextSubmit,
      handleDeleteVideo,
      handleDeleteText,
      getEmotionTagType,
      getEmotionTagLabel
    }
  }
}
</script>

<style scoped>
.resource-management {
  padding: 2rem;
}

.resource-tabs {
  margin-top: 2rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.resource-section {
  margin-top: 1rem;
}

h1 {
  color: #2c3e50;
  margin-bottom: 2rem;
}

h2 {
  color: #2c3e50;
  margin: 0;
}
</style> 