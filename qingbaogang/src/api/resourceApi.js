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

export function addMusicToPlaylist(playlistId, musicId) {
  return request({ url: `/api/playlist/${playlistId}/add`, method: 'post', params: { musicId } });
}

export function addSearchHistory(userId, keyword) {
  return request({ url: `/api/music/search/history`, method: 'post', params: { userId, keyword } });
}

// 音乐相关API补全
export function searchMusic(keyword) {
  return request({ url: `/api/music/search`, method: 'get', params: { keyword } });
}

export function getMusicCategories() {
  // 假设后端有分类接口，否则返回静态分类
  return Promise.resolve({ data: ['心情', '场景', '风格'] });
}

export function getMusicByCategory(category) {
  return request({ url: `/api/music/category/${category}`, method: 'get' });
}

export function getPlaylists(userId) {
  return request({ url: `/api/playlist/list`, method: 'get', params: { userId } });
}

export function createPlaylist(playlist) {
  return request({ url: `/api/playlist/create`, method: 'post', data: playlist });
}

export function deletePlaylist(playlistId) {
  return request({ url: `/api/playlist/${playlistId}`, method: 'delete' });
}

export function getPlaylistMusic(playlistId) {
  return request({ url: `/api/playlist/${playlistId}/music`, method: 'get' });
}

export function removeMusicFromPlaylist(playlistId, musicId) {
  return request({ url: `/api/playlist/${playlistId}/remove`, method: 'delete', params: { musicId } });
}

export function favoriteMusic(userId, musicId, playlistId) {
  return request({ url: `/api/music/favorite/${musicId}`, method: 'post', params: { userId, playlistId } });
}

export function unfavoriteMusic(userId, musicId, playlistId) {
  return request({ url: `/api/music/favorite/${musicId}`, method: 'delete', params: { userId, playlistId } });
}

export function getSearchHistory(userId) {
  return request({ url: `/api/music/search/history`, method: 'get', params: { userId } });
}

export function getMusicList() {
  return request({
    url: '/api/music/list',
    method: 'get',
  });
}

export function uploadMusic(formData) {
  return request({ 
    url: '/api/music/upload', 
    method: 'post', 
    data: formData, 
    headers: { 'Content-Type': 'multipart/form-data' } 
  });
}

export function deleteMusic(id) {
  return request({ url: `/api/music/delete/${id}`, method: 'delete' });
}

// 按歌名搜索音乐
export function searchMusicByName(name) {
  return request({
    url: '/api/music/search',
    method: 'get',
    params: { name },
  });
} 