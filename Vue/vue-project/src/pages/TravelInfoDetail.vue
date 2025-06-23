<template>
  <div class="travel-info-page">
    <div class="page-header">
      <BackButton />
      <h1>여행 정보</h1>
    </div>

    <!-- 게시글 상세 -->
    <div v-if="post" class="forum-container">
      <div class="post-card">
        <div class="post-header">
          <div class="post-author">
            <div class="author-avatar">
              {{ post.author ? post.author.charAt(0).toUpperCase() : '?' }}
            </div>
            <div class="author-name">{{ post.author }}</div>
          </div>
          <div class="post-date">{{ formatDate(post.date) }}</div>
        </div>

        <h3 class="post-title">{{ post.title }}</h3>

        <div class="post-tags">
          <span v-for="tag in post.tags" :key="tag" class="tag">{{ tag }}</span>
        </div>

        <div class="post-content">{{ post.content }}</div>

        <div class="post-actions">
          <button class="like-button" @click="toggleLike(post)">
            {{ post.liked ? '❤️' : '🤍' }} {{ post.likes }}
          </button>
          <button class="comment-button" @click="toggleComments">💬 {{ post.commentCount }}</button>
          <button class="share-button" @click="sharePost(post)">🔗 Share</button>
          <button
            v-if="auth.user && (post.author === auth.user.id || post.userId === auth.user.mno)"
            class="delete-button"
            @click="deletePost(post.id)"
          >
            🗑️ Delete
          </button>
        </div>

        <!-- 댓글 섹션 -->
        <div v-if="showComments" class="comments-section">
          <h4>Comments</h4>
          <div v-for="comment in post.comments" :key="comment.id" class="comment">
            <div class="comment-header">
              <div class="comment-author">{{ comment.author }}</div>
              <div class="comment-date">{{ formatDate(comment.date) }}</div>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <button class="like-button" @click="toggleCommentLike(comment)">
                {{ comment.liked ? '❤️' : '🤍' }} {{ comment.likes }}
              </button>
              <button
                v-if="auth.user && comment.author === auth.user.name"
                class="delete-button"
                @click="deleteComment(comment.id)"
              >
                🗑️ Delete
              </button>
            </div>
          </div>

          <div class="new-comment-form">
            <textarea v-model="newComment" placeholder="댓글 달기..." rows="2"></textarea>
            <button class="submit-comment-button" @click="addComment" :disabled="!newComment">
              댓글 쓰기
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="forum-container">
      <p>게시글을 불러오는 중입니다...</p>
    </div>

    <!-- Toast 메시지 -->
    <div v-show="toastMessage" :class="['toast-message', { show: toastVisible }]">
      {{ toastMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import BackButton from '../components/common/BackButton.vue'
import { useAuthStore } from '@/stores/auth'

axios.defaults.baseURL = 'http://192.168.205.81:8080/api/v1/'

const auth = useAuthStore()
const token = computed(() => auth.token || localStorage.getItem('accessToken'))
axios.interceptors.request.use((config) => {
  const t = token.value
  if (t) config.headers.Authorization = `Bearer ${t}`
  return config
})

const route = useRoute()
const router = useRouter()

const post = ref(null)
const showComments = ref(false)
const newComment = ref('')

// — 토스트용 상태 & 함수 —
const toastMessage = ref('')
const toastVisible = ref(false)
function showToast(msg) {
  toastMessage.value = msg
  toastVisible.value = true
  setTimeout(() => (toastVisible.value = false), 1500)
  setTimeout(() => (toastMessage.value = ''), 2000)
}

function getConfig(params = {}) {
  return { params }
}

function formatDate(date) {
  if (!date) return ''
  const d = typeof date === 'string' ? new Date(date) : date
  const now = new Date()
  const diff = Math.floor((now - d) / (1000 * 60 * 60 * 24))
  if (diff === 0) return 'Today'
  if (diff === 1) return 'Yesterday'
  if (diff < 7) return `${diff} days ago`
  return d.toLocaleDateString()
}

async function fetchPost() {
  try {
    const postId = route.params.postId
    const res = await axios.get(`/posts/${postId}`, getConfig())
    const p = res.data.post
    let liked = false
    if (auth.user?.mno) {
      try {
        const likeRes = await axios.get(
          `/posts/${p.postId}/isliked`,
          getConfig({ userId: auth.user.mno }),
        )
        liked = likeRes.data.isLiked || likeRes.data === 1
      } catch {}
    }
    post.value = {
      id: p.postId,
      title: p.title,
      content: p.content,
      author: p.userid || p.authorId,
      userId: p.userid || p.authorId,
      date: new Date(p.created),
      tags: p.tags ? p.tags.split(',') : [],
      likes: p.likes,
      commentCount: p.commentCount,
      liked,
      comments: [],
    }
  } catch {
    showToast('존재하지 않는 게시글입니다.')
    router.replace('/')
  }
}

async function deletePost(id) {
  if (!confirm('정말 삭제하시겠습니까?')) return
  try {
    await axios.delete(`/posts/${id}`, getConfig())
    showToast('삭제되었습니다.')
    router.replace('/')
  } catch {
    showToast('삭제에 실패했습니다.')
  }
}

async function toggleLike(postObj) {
  if (!auth.user?.mno) {
    showToast('로그인이 필요합니다.')
    return
  }
  try {
    if (!postObj.liked) {
      await axios.post(`/posts/${postObj.id}/like`, null, getConfig({ userId: auth.user.mno }))
    } else {
      await axios.delete(`/posts/${postObj.id}/like`, getConfig({ userId: auth.user.mno }))
    }
    const likeRes = await axios.get(
      `/posts/${postObj.id}/isliked`,
      getConfig({ userId: auth.user.mno }),
    )
    postObj.liked = likeRes.data.isLiked || likeRes.data === 1
    const { data } = await axios.get(`/posts/${postObj.id}`, getConfig())
    postObj.likes = data.post.likes
    postObj.commentCount = data.post.commentCount
  } catch {
    showToast('좋아요 처리에 실패했습니다.')
  }
}

async function toggleComments() {
  showComments.value = !showComments.value
  if (showComments.value && post.value.comments.length === 0) {
    try {
      const res = await axios.get(`/comments/post/${post.value.id}`, getConfig())
      const userId = auth.user?.mno ?? null
      const withLikes = await Promise.all(
        res.data.map(async (c) => {
          let liked = false
          if (userId) {
            try {
              const lr = await axios.get(`/comments/${c.commentId}/isliked`, getConfig({ userId }))
              liked = lr.data.isLiked || lr.data === 1
            } catch {}
          }
          return {
            id: c.commentId,
            author: c.authorName,
            content: c.content,
            date: new Date(c.created),
            likes: c.likes,
            liked,
          }
        }),
      )
      post.value.comments = withLikes
    } catch {
      showToast('댓글을 불러오지 못했습니다.')
    }
  }
}

async function addComment() {
  if (!auth.user?.mno) {
    showToast('로그인이 필요합니다.')
    return
  }
  if (!newComment.value.trim()) return
  try {
    await axios.post(
      '/comments',
      { postId: post.value.id, userId: auth.user.mno, content: newComment.value },
      getConfig(),
    )
    await toggleComments() // 댓글 새로고침
    showComments.value = true
    newComment.value = ''
    showToast('댓글이 등록되었습니다!')
  } catch {
    showToast('댓글 등록에 실패했습니다.')
  }
}

async function deleteComment(commentId) {
  if (!confirm('정말 삭제하시겠습니까?')) return
  try {
    await axios.delete(`/comments/${commentId}`, getConfig())
    post.value.comments = post.value.comments.filter((c) => c.id !== commentId)
    post.value.commentCount = post.value.comments.length
    showToast('댓글이 삭제되었습니다.')
  } catch {
    showToast('댓글 삭제에 실패했습니다.')
  }
}

async function toggleCommentLike(comment) {
  if (!auth.user?.mno) {
    showToast('로그인이 필요합니다.')
    return
  }
  try {
    if (!comment.liked) {
      await axios.post(`/comments/${comment.id}/like`, null, getConfig({ userId: auth.user.mno }))
    } else {
      await axios.delete(`/comments/${comment.id}/like`, getConfig({ userId: auth.user.mno }))
    }
    const lr = await axios.get(
      `/comments/${comment.id}/isliked`,
      getConfig({ userId: auth.user.mno }),
    )
    comment.liked = lr.data.isLiked || lr.data === 1
    const { data } = await axios.get(`/comments/${comment.id}`, getConfig())
    comment.likes = data.likes
    post.value.commentCount = post.value.comments.length
  } catch {
    showToast('댓글 좋아요 처리에 실패했습니다.')
  }
}

function getPostDetailUrl(postId) {
  return `http://192.168.205.81:5173/travel-info/${postId}`
}

function sharePost(postObj) {
  const url = getPostDetailUrl(postObj.id)
  if (navigator.clipboard) {
    navigator.clipboard
      .writeText(url)
      .then(() => showToast('게시글 링크가 복사되었습니다!'))
      .catch(() => showToast('복사에 실패했습니다.'))
  } else {
    const ta = document.createElement('textarea')
    ta.value = url
    document.body.appendChild(ta)
    ta.select()
    document.execCommand('copy')
    document.body.removeChild(ta)
    showToast('게시글 링크가 복사되었습니다!')
  }
}

onMounted(fetchPost)
</script>

<style scoped>
.delete-button {
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  color: var(--text-light);
  transition: color 0.2s ease;
}
.delete-button:hover {
  color: var(--text-light);
}
.travel-info-page {
  padding: 20px;
  height: calc(100vh - var(--topbar-height));
  overflow-y: auto;
  background-color: rgba(255, 255, 255, 0.9);
  position: relative;
  z-index: 2;
}
.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h1 {
  margin-left: 15px;
  color: var(--primary-color);
}
.forum-container {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: var(--shadow);
}
.post-card {
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 20px;
}
.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.post-author {
  display: flex;
  align-items: center;
}
.author-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 10px;
}
.post-date {
  color: var(--text-light);
  font-size: 0.9rem;
}
.post-title {
  margin: 0 0 10px 0;
  color: var(--primary-color);
}
.post-content {
  margin-bottom: 15px;
}
.post-actions {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}
.like-button,
.comment-button,
.share-button {
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  color: var(--text-light);
  transition: color 0.2s ease;
}
.like-button:hover,
.comment-button:hover,
.share-button:hover {
  color: var(--primary-color);
}
.comments-section {
  border-top: 1px solid var(--border-color);
  padding-top: 15px;
}
.comments-section h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: var(--text-color);
}
.comment {
  padding: 10px;
  border-bottom: 1px solid var(--border-color);
}
.comment:last-child {
  border-bottom: none;
}
.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}
.comment-author {
  font-weight: bold;
}
.comment-date {
  font-size: 0.8rem;
  color: var(--text-light);
}
.new-comment-form {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}
.new-comment-form textarea {
  flex: 1;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  resize: vertical;
}
.submit-comment-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 0 15px;
  font-weight: bold;
  cursor: pointer;
}
.submit-comment-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
.post-tags {
  margin: 8px 0;
}
.post-tags .tag {
  display: inline-block;
  background-color: #e0e0e0;
  color: #333;
  font-size: 0.8rem;
  padding: 2px 6px;
  border-radius: 4px;
  margin-right: 4px;
}
.comment-actions {
  margin-top: 5px;
  display: flex;
  gap: 10px;
  align-items: center;
}

/* 토스트 스타일 */
.toast-message {
  position: fixed;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  background: #333;
  color: #fff;
  padding: 13px 30px;
  border-radius: 30px;
  font-size: 1rem;
  z-index: 9999;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.5s;
}
.toast-message.show {
  opacity: 0.93;
}
</style>
