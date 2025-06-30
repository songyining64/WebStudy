import { defineStore } from 'pinia';
import { ref } from 'vue';

export const usePostStore = defineStore('posts', () => {
  const posts = ref([
    // 示例数据
    {id:1,title:'今天心情不错',content:'阳光明媚，心情舒畅，推荐大家多晒太阳！',image:'',tags:['情绪'],author:'小明',likes:2,liked:false,collects:1,collected:false,comments:[{id:1,author:'小红',content:'赞同！'}]},
    {id:2,title:'焦虑时怎么办',content:'最近压力大，大家有什么缓解焦虑的方法吗？',image:'',tags:['互助','心理急救'],author:'小红',likes:3,liked:true,collects:0,collected:false,comments:[]},
    {id:3,title:'我的成长故事',content:'从自卑到自信，这一路走来收获很多...',image:'',tags:['成长'],author:'小刚',likes:1,liked:false,collects:2,collected:true,comments:[]},
    {id:4,title:'心理急救经验',content:'遇到突发事件时，深呼吸和自我暗示很有用。',image:'',tags:['心理急救'],author:'小美',likes:0,liked:false,collects:0,collected:false,comments:[]},
    {id:5,title:'生活小确幸',content:'今天喝到一杯好喝的咖啡，简单的幸福。',image:'',tags:['生活'],author:'小李',likes:2,liked:false,collects:1,collected:false,comments:[]}
  ]);

  function addPost(post) {
    const newId = Math.max(...posts.value.map(p => p.id)) + 1;
    posts.value.unshift({
      ...post,
      id: newId,
      likes: 0,
      liked: false,
      collects: 0,
      collected: false,
      comments: []
    });
  }

  function updatePost(updatedPost) {
    const index = posts.value.findIndex(p => p.id === updatedPost.id);
    if (index > -1) {
      posts.value[index] = updatedPost;
    }
  }

  function deletePost(postId) {
    posts.value = posts.value.filter(p => p.id !== postId);
  }

  function addComment(postId, comment) {
    const post = posts.value.find(p => p.id === postId);
    if (post) {
      post.comments.push(comment);
    }
  }

  function deleteComment(postId, commentId) {
    const post = posts.value.find(p => p.id === postId);
    if (post) {
      post.comments = post.comments.filter(c => c.id !== commentId);
    }
  }

  function toggleLike(postId) {
    const post = posts.value.find(p => p.id === postId);
    if (post) {
      post.liked = !post.liked;
      post.likes += post.liked ? 1 : -1;
    }
  }

  function toggleCollect(postId) {
    const post = posts.value.find(p => p.id === postId);
    if (post) {
      post.collected = !post.collected;
      post.collects += post.collected ? 1 : -1;
    }
  }

  return {
    posts,
    addPost,
    updatePost,
    deletePost,
    addComment,
    deleteComment,
    toggleLike,
    toggleCollect
  };
}); 