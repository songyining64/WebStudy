<template>
  <div class="admin-panel">
    <h2>系统管理</h2>
    <div class="tabs">
      <button :class="{active: tab==='user'}" @click="tab='user'">用户管理</button>
      <button :class="{active: tab==='post'}" @click="tab='post'">社区管理</button>
    </div>
    <div v-if="tab==='user'">
      <h3>用户管理</h3>
      <table>
        <tr>
          <th>ID</th><th>用户名</th><th>邮箱</th><th>角色</th><th>操作</th>
        </tr>
        <tr v-for="user in users" :key="user.id">
          <td>{{ user.id }}</td>
          <td>{{ user.username }}</td>
          <td>{{ user.email }}</td>
          <td>{{ user.role }}</td>
          <td>
            <button @click="deleteUser(user.id)">删除</button>
          </td>
        </tr>
      </table>
    </div>
    <div v-else>
      <h3>社区管理（帖子）</h3>
      <table>
        <tr>
          <th>ID</th><th>标题</th><th>作者ID</th><th>内容</th><th>操作</th>
        </tr>
        <tr v-for="post in posts" :key="post.id">
          <td>{{ post.id }}</td>
          <td>{{ post.title }}</td>
          <td>{{ post.userId }}</td>
          <td>{{ post.content.slice(0, 20) }}...</td>
          <td>
            <button @click="deletePost(post.id)">删除</button>
          </td>
        </tr>
      </table>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { request } from '@/utils/request'
const tab = ref('user')
const users = ref([])
const posts = ref([])
const fetchUsers = async () => {
  const res = await request.get('/api/user')
  users.value = res
}
const fetchPosts = async () => {
  const res = await request.get('/api/post')
  posts.value = res
}
onMounted(() => {
  fetchUsers()
  fetchPosts()
})
const deleteUser = async (id) => {
  await request.delete(`/api/user/${id}`)
  users.value = users.value.filter(u => u.id !== id)
}
const deletePost = async (id) => {
  await request.delete(`/api/post/${id}`)
  posts.value = posts.value.filter(p => p.id !== id)
}
</script>
<style scoped>
.admin-panel { max-width: 900px; margin: 40px auto; background: #fff; padding: 32px; border-radius: 12px; box-shadow: 0 2px 12px #0001; }
.tabs { margin-bottom: 24px; }
.tabs button { margin-right: 12px; padding: 8px 24px; border: none; border-radius: 6px; background: #eee; cursor: pointer; }
.tabs .active { background: #409eff; color: #fff; }
table { width: 100%; border-collapse: collapse; margin-bottom: 24px; }
th, td { border: 1px solid #eee; padding: 8px 12px; text-align: left; }
th { background: #f5f7fa; }
button { background: #f56c6c; color: #fff; border: none; border-radius: 4px; padding: 4px 12px; cursor: pointer; }
button:hover { background: #c0392b; }
</style> 