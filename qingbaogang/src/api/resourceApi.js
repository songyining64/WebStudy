import request from '@/utils/request'

const BASE_URL = '/api/resource'

// 获取推荐资源
export function getRecommendedResources(userId) {
  return request({
    url: `${BASE_URL}/recommend`,
    method: 'get',
    params: { userId }
  })
}

// 获取所有视频资源（管理员）
export function getAllVideos() {
  return request({
    url: `${BASE_URL}/videos`,
    method: 'get'
  })
}

// 添加视频资源（管理员）
export function addVideo(videoResource) {
  return request({
    url: `${BASE_URL}/videos`,
    method: 'post',
    data: videoResource
  })
}

// 更新视频资源（管理员）
export function updateVideo(videoResource) {
  return request({
    url: `${BASE_URL}/videos`,
    method: 'put',
    data: videoResource
  })
}

// 删除视频资源（管理员）
export function deleteVideo(id) {
  return request({
    url: `${BASE_URL}/videos/${id}`,
    method: 'delete'
  })
}

// 获取所有文案资源（管理员）
export function getAllTexts() {
  return request({
    url: `${BASE_URL}/texts`,
    method: 'get'
  })
}

// 添加文案资源（管理员）
export function addText(textResource) {
  return request({
    url: `${BASE_URL}/texts`,
    method: 'post',
    data: textResource
  })
}

// 更新文案资源（管理员）
export function updateText(textResource) {
  return request({
    url: `${BASE_URL}/texts`,
    method: 'put',
    data: textResource
  })
}

// 删除文案资源（管理员）
export function deleteText(id) {
  return request({
    url: `${BASE_URL}/texts/${id}`,
    method: 'delete'
  })
} 