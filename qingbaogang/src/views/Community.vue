<template>
  <div class="community-main">
    <aside class="community-sidebar">
      <div class="sidebar-title">分类</div>
      <ul class="sidebar-list">
        <li :class="{active: filterTag==='全部'}" @click="filterTag='全部'">全部</li>
        <li v-for="tag in tags" :key="tag" :class="{active: filterTag===tag}" @click="filterTag=tag">{{ tag }}</li>
      </ul>
    </aside>
    <section class="community-content">
      <div class="community-header">
        <input v-model="search" class="search-input" placeholder="搜索帖子/作者/标签..." @keyup.enter="handleSearch" />
        <button class="post-btn" @click="showPostDialog=true">发布</button>
      </div>
      <div class="post-list">
        <div v-for="post in filteredPosts" :key="post.id" class="post-card" @click="viewPost(post)">
          <img v-if="post.image" :src="post.image" class="post-img" />
          <div class="post-info">
            <div class="post-title">{{ post.title }}</div>
            <div class="post-content">{{ post.content.slice(0, 48) }}{{ post.content.length>48?'...':'' }}</div>
            <div class="post-meta">
              <span class="post-author">{{ post.author }}</span>
              <span class="post-tag" v-for="tag in post.tags" :key="tag">#{{ tag }}</span>
            </div>
            <div class="post-actions">
              <span @click.stop="likePost(post)"><i :class="['icon-like', post.liked?'liked':'']"></i> {{ post.likes }}</span>
              <span @click.stop="collectPost(post)"><i :class="['icon-star', post.collected?'collected':'']"></i> {{ post.collects }}</span>
              <span><i class="icon-comment"></i> {{ post.comments.length }}</span>
              <span v-if="canEdit(post)" @click.stop="editPost(post)"><i class="icon-edit"></i></span>
              <span v-if="canEdit(post)" @click.stop="deletePost(post)"><i class="icon-delete"></i></span>
            </div>
          </div>
        </div>
      </div>
      <div class="pagination">
        <button :disabled="page===1" @click="page--">上一页</button>
        <span>第{{page}}页</span>
        <button :disabled="page===maxPage" @click="page++">下一页</button>
      </div>
    </section>
    <!-- 发帖弹窗 -->
    <div v-if="showPostDialog" class="dialog-bg">
      <div class="dialog-box">
        <h3>{{ editMode?'编辑帖子':'发布新帖' }}</h3>
        <form @submit.prevent="submitPost">
          <input v-model="postForm.title" placeholder="标题" required class="form-input" />
          <textarea v-model="postForm.content" placeholder="内容" required class="form-textarea"></textarea>
          <input v-model="postForm.image" placeholder="图片URL（可选）" class="form-input" />
          <input v-model="postForm.tags" placeholder="标签（逗号分隔）" class="form-input" />
          <div class="dialog-actions">
            <button type="submit" class="submit-btn">{{ editMode?'保存':'发布' }}</button>
            <button type="button" class="cancel-btn" @click="closeDialog">取消</button>
          </div>
        </form>
      </div>
    </div>
    <!-- 帖子详情弹窗 -->
    <div v-if="showDetailDialog" class="dialog-bg">
      <div class="dialog-box detail-box">
        <h3>{{ detailPost.title }}</h3>
        <img v-if="detailPost.image" :src="detailPost.image" class="detail-img" />
        <div class="detail-content">{{ detailPost.content }}</div>
        <div class="detail-meta">
          <span>作者：{{ detailPost.author }}</span>
          <span>标签：<span v-for="tag in detailPost.tags" :key="tag">#{{ tag }} </span></span>
        </div>
        <div class="detail-actions">
          <span @click="likePost(detailPost)"><i :class="['icon-like', detailPost.liked?'liked':'']"></i> {{ detailPost.likes }}</span>
          <span @click="collectPost(detailPost)"><i :class="['icon-star', detailPost.collected?'collected':'']"></i> {{ detailPost.collects }}</span>
        </div>
        <div class="comment-section">
          <h4>评论</h4>
          <div v-for="c in detailPost.comments" :key="c.id" class="comment-item">
            <span class="comment-author">{{ c.author }}：</span>
            <span>{{ c.content }}</span>
          </div>
          <form @submit.prevent="addComment">
            <input v-model="commentInput" placeholder="写下你的评论..." class="form-input" />
            <button type="submit" class="submit-btn">评论</button>
          </form>
        </div>
        <div class="dialog-actions">
          <button class="cancel-btn" @click="showDetailDialog=false">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Community',
  data() {
    return {
      tags: ['情绪', '互助', '成长', '心理急救', '生活'],
      filterTag: '全部',
      search: '',
      page: 1,
      pageSize: 8,
      posts: [
        // 示例数据
        {id:1,title:'今天心情不错',content:'阳光明媚，心情舒畅，推荐大家多晒太阳！',image:'',tags:['情绪'],author:'小明',likes:2,liked:false,collects:1,collected:false,comments:[{id:1,author:'小红',content:'赞同！'}]},
        {id:2,title:'焦虑时怎么办',content:'最近压力大，大家有什么缓解焦虑的方法吗？',image:'',tags:['互助','心理急救'],author:'小红',likes:3,liked:true,collects:0,collected:false,comments:[]},
        {id:3,title:'我的成长故事',content:'从自卑到自信，这一路走来收获很多...',image:'',tags:['成长'],author:'小刚',likes:1,liked:false,collects:2,collected:true,comments:[]},
        {id:4,title:'心理急救经验',content:'遇到突发事件时，深呼吸和自我暗示很有用。',image:'',tags:['心理急救'],author:'小美',likes:0,liked:false,collects:0,collected:false,comments:[]},
        {id:5,title:'生活小确幸',content:'今天喝到一杯好喝的咖啡，简单的幸福。',image:'',tags:['生活'],author:'小李',likes:2,liked:false,collects:1,collected:false,comments:[]},
        // ...可扩展
      ],
      showPostDialog: false,
      showDetailDialog: false,
      editMode: false,
      postForm: {id:null,title:'',content:'',image:'',tags:'',author:'',},
      detailPost: {},
      commentInput: ''
    }
  },
  computed: {
    filteredPosts() {
      let arr = this.posts.filter(p =>
        (this.filterTag==='全部'||p.tags.includes(this.filterTag)) &&
        (this.search===''||p.title.includes(this.search)||p.content.includes(this.search)||p.author.includes(this.search)||p.tags.join(',').includes(this.search))
      );
      // 分页
      return arr.slice((this.page-1)*this.pageSize, this.page*this.pageSize);
    },
    maxPage() {
      let arr = this.posts.filter(p =>
        (this.filterTag==='全部'||p.tags.includes(this.filterTag)) &&
        (this.search===''||p.title.includes(this.search)||p.content.includes(this.search)||p.author.includes(this.search)||p.tags.join(',').includes(this.search))
      );
      return Math.max(1, Math.ceil(arr.length/this.pageSize));
    }
  },
  methods: {
    handleSearch() { this.page=1; },
    likePost(post) { post.liked=!post.liked; post.likes+=post.liked?1:-1; },
    collectPost(post) { post.collected=!post.collected; post.collects+=post.collected?1:-1; },
    canEdit(post) { return post.author==='小明'; }, // 示例：仅小明可编辑
    editPost(post) {
      this.editMode=true;
      this.showPostDialog=true;
      this.postForm={...post, tags:post.tags.join(',')};
    },
    deletePost(post) {
      if(confirm('确定删除该帖子？')) this.posts=this.posts.filter(p=>p.id!==post.id);
    },
    closeDialog() {
      this.showPostDialog=false; this.editMode=false;
      this.postForm={id:null,title:'',content:'',image:'',tags:'',author:'',};
    },
    submitPost() {
      if(this.editMode) {
        let idx=this.posts.findIndex(p=>p.id===this.postForm.id);
        if(idx>-1) this.posts[idx]={...this.postForm, tags:this.postForm.tags.split(',').map(t=>t.trim()), author:'小明'};
      } else {
        let newId=Math.max(...this.posts.map(p=>p.id))+1;
        this.posts.unshift({...this.postForm, id:newId, tags:this.postForm.tags.split(',').map(t=>t.trim()), author:'小明', likes:0, liked:false, collects:0, collected:false, comments:[]});
      }
      this.closeDialog();
    },
    viewPost(post) {
      this.detailPost=JSON.parse(JSON.stringify(post));
      this.showDetailDialog=true;
      this.commentInput='';
    },
    addComment() {
      if(!this.commentInput.trim()) return;
      this.detailPost.comments.push({id:Date.now(), author:'小明', content:this.commentInput});
      let idx=this.posts.findIndex(p=>p.id===this.detailPost.id);
      if(idx>-1) this.posts[idx].comments.push({id:Date.now(), author:'小明', content:this.commentInput});
      this.commentInput='';
    }
  }
}
</script>

<style scoped>
.community-main {
  display: flex;
  min-height: 92vh;
  background: #f8fafc;
}
.community-sidebar {
  width: 120px;
  background: #fff;
  border-right: 1px solid #eaeaea;
  padding: 32px 0 0 0;
  min-height: 100%;
}
.sidebar-title {
  font-size: 1.1rem;
  font-weight: bold;
  color: #2980b9;
  margin-left: 18px;
  margin-bottom: 18px;
}
.sidebar-list {
  list-style: none;
  padding: 0 0 0 18px;
  margin: 0;
}
.sidebar-list li {
  padding: 8px 0;
  cursor: pointer;
  color: #444;
  border-radius: 6px 0 0 6px;
  transition: background 0.2s, color 0.2s;
}
.sidebar-list li.active, .sidebar-list li:hover {
  background: #eaf6ff;
  color: #2980b9;
}
.community-content {
  flex: 1;
  padding: 32px 32px 0 32px;
  min-width: 0;
}
.community-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.search-input {
  width: 320px;
  height: 38px;
  border-radius: 8px;
  border: 1.5px solid #e0e7ef;
  padding: 0 16px;
  font-size: 16px;
  margin-right: 18px;
}
.post-btn {
  background: #3498db;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}
.post-btn:hover {
  background: #2980b9;
}
.post-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 22px;
}
.post-card {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(52,152,219,0.08);
  overflow: hidden;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.2s;
  position: relative;
}
.post-card:hover {
  box-shadow: 0 6px 24px rgba(52,152,219,0.16);
}
.post-img {
  width: 100%;
  height: 140px;
  object-fit: cover;
  background: #eee;
}
.post-info {
  padding: 16px 16px 10px 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}
.post-title {
  font-size: 1.1rem;
  font-weight: bold;
  color: #222;
  margin-bottom: 6px;
}
.post-content {
  color: #444;
  font-size: 0.98rem;
  margin-bottom: 8px;
  flex: 1;
}
.post-meta {
  font-size: 0.92rem;
  color: #888;
  margin-bottom: 8px;
}
.post-tag {
  background: #eaf6ff;
  color: #3498db;
  border-radius: 6px;
  padding: 2px 8px;
  margin-left: 6px;
  font-size: 0.9em;
}
.post-actions {
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 1rem;
  color: #888;
  margin-top: 2px;
}
.icon-like, .icon-star, .icon-comment, .icon-edit, .icon-delete {
  font-style: normal;
  margin-right: 3px;
}
.icon-like:before { content: '👍'; }
.icon-like.liked:before { content: '💙'; }
.icon-star:before { content: '☆'; }
.icon-star.collected:before { content: '★'; }
.icon-comment:before { content: '💬'; }
.icon-edit:before { content: '✏️'; }
.icon-delete:before { content: '🗑️'; }
.icon-like.liked, .icon-star.collected { color: #3498db; }
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 18px;
  margin: 32px 0 0 0;
}
.pagination button {
  background: #f2f6fa;
  border: none;
  border-radius: 8px;
  padding: 8px 18px;
  font-size: 1rem;
  color: #2980b9;
  cursor: pointer;
  transition: background 0.2s;
}
.pagination button:disabled {
  background: #e0e7ef;
  color: #aaa;
  cursor: not-allowed;
}
.dialog-bg {
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.13);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}
.dialog-box {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 4px 32px rgba(52,152,219,0.18);
  padding: 32px 28px 18px 28px;
  min-width: 340px;
  max-width: 95vw;
  position: relative;
}
.dialog-box h3 {
  text-align: center;
  margin-bottom: 18px;
  color: #2980b9;
  font-size: 1.2rem;
}
.form-input {
  width: 100%;
  height: 38px;
  border-radius: 8px;
  border: 1.5px solid #e0e7ef;
  padding: 0 12px;
  font-size: 15px;
  margin-bottom: 12px;
}
.form-textarea {
  width: 100%;
  min-height: 70px;
  border-radius: 8px;
  border: 1.5px solid #e0e7ef;
  padding: 8px 12px;
  font-size: 15px;
  margin-bottom: 12px;
  resize: vertical;
}
.dialog-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 18px;
  gap: 16px;
}
.submit-btn {
  background: #3498db;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}
.submit-btn:hover {
  background: #2980b9;
}
.cancel-btn {
  background: #eee;
  color: #444;
  border: none;
  border-radius: 8px;
  padding: 10px 18px;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s;
}
.cancel-btn:hover {
  background: #e0e7ef;
}
.detail-box {
  min-width: 420px;
  max-width: 600px;
}
.detail-img {
  width: 100%;
  max-height: 220px;
  object-fit: cover;
  border-radius: 10px;
  margin-bottom: 12px;
}
.detail-content {
  font-size: 1.05rem;
  color: #444;
  margin-bottom: 10px;
}
.detail-meta {
  font-size: 0.95rem;
  color: #888;
  margin-bottom: 8px;
}
.detail-actions {
  display: flex;
  align-items: center;
  gap: 18px;
  font-size: 1rem;
  color: #888;
  margin-bottom: 10px;
}
.comment-section {
  margin-top: 18px;
}
.comment-section h4 {
  font-size: 1.05rem;
  color: #2980b9;
  margin-bottom: 8px;
}
.comment-item {
  background: #f8fafc;
  border-radius: 8px;
  padding: 6px 12px;
  margin-bottom: 6px;
  font-size: 0.98rem;
}
.comment-author {
  color: #3498db;
  font-weight: 600;
}
</style> 