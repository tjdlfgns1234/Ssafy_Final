<template>
  <div>
    <div v-if="!auth.isAuthenticated" class="login-prompt">
      <div class="empty-icon">🔒</div>
      <h2>로그인이 필요합니다</h2>
      <p>여행 게시판을 보려면 로그인해주세요.</p>
      <button class="login-button" @click="modalStore.openLoginModal">로그인하기</button>
    </div>
    <div v-else class="travel-info-page" ref="scrollArea">
      <div class="page-header">
        <BackButton />
        <h1>여행 정보 공유</h1>
      </div>

      <div class="forum-container">
        <div class="forum-header">
          <h2>여행 포럼</h2>
          <button class="new-post-button" @click="showNewPostForm = true">글 작성하기</button>
        </div>

        <!-- New Post Form -->
        <div v-if="showNewPostForm" class="new-post-form">
          <h3>글 작성하기</h3>
          <div class="form-group">
            <label for="post-title">제목</label>
            <input
              type="text"
              id="post-title"
              v-model="newPost.title"
              placeholder="제목을 입력하세요"
            />
          </div>
          <div class="form-group">
            <label for="post-content">내용</label>
            <textarea
              id="post-content"
              v-model="newPost.content"
              placeholder="여행 경험을 공유하거나 질문하세요!"
            ></textarea>
          </div>
          <div class="form-group">
            <label for="post-tags">태그</label>
            <input
              type="text"
              id="post-tags"
              v-model="newPost.tags"
              placeholder="예시) 역사, 서울, 여행"
            />
          </div>
          <div class="form-actions">
            <button class="cancel-button" @click="showNewPostForm = false">취소</button>
            <button class="submit-button" @click="submitPost">제출</button>
          </div>
        </div>

        <!-- Posts List -->
        <div class="posts-list">
          <div v-for="post in posts" :key="post.id" class="post-card" style="cursor: pointer">
            <div class="post-header">
              <div class="post-author">
                <div class="author-avatar">
                  {{ post.author.charAt(0).toUpperCase() }}
                </div>
                <div class="author-name">{{ post.author }}</div>
              </div>
              <div class="post-date">{{ formatDate(post.date) }}</div>
            </div>
            <h3 class="post-title" @click="goToDetail(post.id)">
              {{ post.title }}
            </h3>
            <div class="post-tags">
              <span v-for="tag in post.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
            <div class="post-content">{{ post.content }}</div>
            <div class="post-actions" @click.stop>
              <button class="like-button" @click="toggleLike(post)">
                {{ post.liked ? '❤️' : '🤍' }} {{ post.likes }}
              </button>
              <button class="comment-button" @click="toggleComments(post)">
                💬 {{ post.commentCount }}
              </button>
              <button class="share-button" @click="copyLink(post.id)">🔗 Share</button>
              <button
                v-if="auth.user && (post.author === auth.user.id || post.userId === auth.user.mno)"
                class="delete-button"
                @click="deletePost(post.id)"
              >
                🗑️ Delete
              </button>
            </div>
            <!-- Comments Section -->
            <div v-if="post.showComments" class="comments-section">
              <h4>댓글</h4>
              <div v-for="comment in post.comments" :key="comment.id" class="comment">
                <div class="comment-header">
                  <div class="comment-author">{{ comment.author }}</div>
                  <div class="comment-date">{{ formatDate(comment.date) }}</div>
                </div>
                <div class="comment-content">{{ comment.content }}</div>
                <div class="comment-actions">
                  <button class="like-button" @click="toggleCommentLike(post, comment)">
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
              <!-- New Comment Form -->
              <div class="new-comment-form">
                <textarea
                  v-model="newComments[post.id]"
                  placeholder="댓글 달기..."
                  rows="2"
                ></textarea>
                <button
                  class="submit-comment-button"
                  @click="addComment(post)"
                  :disabled="!newComments[post.id]"
                >
                  댓글 쓰기
                </button>
              </div>
            </div>
          </div>
        </div>
        <!-- 무한 스크롤 로딩 표시 -->
        <div v-if="isLoadingMore" style="text-align: center; padding: 20px">불러오는 중...</div>
      </div>

      <!-- 토스트 메시지 -->
      <div v-show="toastMessage" :class="['toast-message', { show: toastVisible }]">
        {{ toastMessage }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import BackButton from '../components/common/BackButton.vue'
import { useAuthStore } from '@/stores/auth'
import { useModalStore } from '@/stores/modal'

axios.defaults.baseURL = 'http://192.168.205.81:8080/api/v1/'
//const baseURL = 'http://192.168.205.86:8080/api/v1'

const auth = useAuthStore()
const token = computed(() => auth.token || localStorage.getItem('accessToken'))
axios.interceptors.request.use((config) => {
  const t = token.value
  if (t) config.headers.Authorization = `Bearer ${t}`
  return config
})
const modalStore = useModalStore()

const allPosts = ref([]) // 전체 데이터
const posts = ref([]) // 화면에 보이는 데이터
const pageSize = 10
const page = ref(1)
const isLoadingMore = ref(false)

const showNewPostForm = ref(false)
const newPost = ref({ title: '', content: '', tags: '' })
const newComments = ref({})
const router = useRouter()

const toastMessage = ref('')
const toastVisible = ref(false)

const scrollArea = ref(null)

// 로그인 상태 변화를 감시
watch(
  () => auth.isAuthenticated,
  (isLoggedIn) => {
    if (isLoggedIn) {
      window.location.href = '/travel-info'
    }
  },
)

function showToast(msg) {
  toastMessage.value = msg
  toastVisible.value = true
  setTimeout(() => {
    toastVisible.value = false
  }, 1500)
  setTimeout(() => {
    toastMessage.value = ''
  }, 2000)
}

onMounted(() => {
  if (auth.isAuthenticated) fetchPosts()
  // 무한 스크롤 이벤트 등록
  if (scrollArea.value) {
    scrollArea.value.addEventListener('scroll', handleScroll)
  } else {
    // fallback: window 스크롤
    window.addEventListener('scroll', handleScroll)
  }
})

function getConfig(params = {}) {
  return { params }
}

function goToDetail(postId) {
  router.push({ name: 'TravelInfoDetail', params: { postId } })
}

// 공유(링크 복사) 함수
function copyLink(postId) {
  const url = `http://192.168.205.81:5173/travel-info/${postId}`
  if (navigator.clipboard) {
    navigator.clipboard
      .writeText(url)
      .then(() => showToast('링크가 복사되었습니다!'))
      .catch(() => showToast('복사에 실패했습니다.'))
  } else {
    const textarea = document.createElement('textarea')
    textarea.value = url
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    showToast('링크가 복사되었습니다!')
  }
}

async function deletePost(postId) {
  if (!confirm('정말 삭제하시겠습니까?')) return
  try {
    await axios.delete(`posts/${postId}`, getConfig())
    await fetchPosts()
  } catch (err) {
    showToast('삭제에 실패했습니다.')
  }
}

async function deleteComment(commentId) {
  if (!confirm('정말 삭제하시겠습니까?')) return
  try {
    await axios.delete(`comments/${commentId}`, getConfig())
    const post = posts.value.find((p) => p.comments.some((c) => c.id === commentId))
    if (post) {
      post.comments = post.comments.filter((c) => c.id !== commentId)
      post.commentCount = post.comments.length
    }
  } catch (err) {
    showToast('삭제에 실패했습니다.')
  }
}

function formatDate(date) {
  const now = new Date()
  const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24))
  if (diffDays === 0) return 'Today'
  if (diffDays === 1) return 'Yesterday'
  if (diffDays < 7) return `${diffDays} days ago`
  return date.toLocaleDateString()
}

async function fetchPosts() {
  try {
    const res = await axios.get('/posts', getConfig())
    const userId = auth.user.mno
    const postsWithLikeStatus = await Promise.all(
      res.data.posts.map(async (p) => {
        try {
          const likeRes = await axios.get(`/posts/${p.postId}/isliked`, getConfig({ userId }))
          return {
            id: p.postId,
            title: p.title,
            content: p.content,
            author: p.userid || p.authorId,
            date: new Date(p.created),
            tags: p.tags ? p.tags.split(',') : [],
            likes: p.likes,
            commentCount: p.commentCount,
            liked: likeRes.data.isLiked || likeRes.data === 1,
            showComments: false,
            comments: [],
          }
        } catch (err) {
          return {
            ...p,
            id: p.postId,
            author: p.userid || p.authorId,
            date: new Date(p.created),
            tags: p.tags ? p.tags.split(',') : [],
            likes: p.likes,
            commentCount: p.commentCount,
            liked: false,
            showComments: false,
            comments: [],
          }
        }
      }),
    )
    allPosts.value = postsWithLikeStatus
    posts.value = allPosts.value.slice(0, pageSize)
    page.value = 1
    await nextTick()
  } catch (err) {
    showToast('게시글을 불러오지 못했습니다.')
  }
}

// 무한 스크롤 이벤트 핸들러
function handleScroll() {
  let scrollTarget, scrollTop, scrollHeight, clientHeight
  if (scrollArea.value) {
    scrollTarget = scrollArea.value
    scrollTop = scrollTarget.scrollTop
    scrollHeight = scrollTarget.scrollHeight
    clientHeight = scrollTarget.clientHeight
  } else {
    scrollTop = window.scrollY
    scrollHeight = document.body.scrollHeight
    clientHeight = window.innerHeight
  }
  if (
    scrollTop + clientHeight >= scrollHeight - 10 &&
    posts.value.length < allPosts.value.length &&
    !isLoadingMore.value
  ) {
    loadMorePosts()
  }
}

function loadMorePosts() {
  isLoadingMore.value = true
  setTimeout(() => {
    const next = allPosts.value.slice(posts.value.length, posts.value.length + pageSize)
    posts.value.push(...next)
    page.value += 1
    isLoadingMore.value = false
  }, 500) // 로딩 표시를 위해 약간의 딜레이
}

async function submitPost() {
  if (!newPost.value.title.trim() || !newPost.value.content.trim()) return
  try {
    const tagsArray = newPost.value.tags
      .split(',')
      .map((t) => t.trim())
      .filter((t) => t)
    const payload = {
      title: newPost.value.title,
      content: newPost.value.content,
      tags: tagsArray.join(','),
      id: auth.user.mno,
    }
    const res = await axios.post('/posts', payload, getConfig())
    const created = res.data.post
    const nowIso = new Date().toISOString()
    const newItem = {
      id: created.postId,
      title: created.title,
      content: created.content,
      author: auth.user.id,
      date: new Date(nowIso),
      tags: created.tags.split(',') || [],
      likes: created.likes || 0,
      commentCount: 0,
      liked: false,
      showComments: false,
      comments: [],
    }
    allPosts.value.unshift(newItem)
    posts.value.unshift(newItem)
    newPost.value = { title: '', content: '', tags: '' }
    showNewPostForm.value = false
    showToast('게시글이 등록되었습니다!')
  } catch (err) {
    showToast('게시글 등록에 실패했습니다.')
  }
}

async function toggleLike(post) {
  try {
    if (!post.liked) {
      await axios.post(`/posts/${post.id}/like`, null, getConfig({ userId: auth.user.mno }))
    } else {
      await axios.delete(`/posts/${post.id}/like`, getConfig({ userId: auth.user.mno }))
    }
    const likeRes = await axios.get(
      `/posts/${post.id}/isliked`,
      getConfig({ userId: auth.user.mno }),
    )
    post.liked = likeRes.data.isLiked || likeRes.data === 1
    const res = await axios.get(`/posts/${post.id}`, getConfig())
    post.likes = res.data.post.likes
    post.commentCount = res.data.post.commentCount
  } catch (err) {
    showToast('좋아요 처리에 실패했습니다.')
  }
}

async function toggleComments(post) {
  post.showComments = !post.showComments
  if (post.showComments && post.comments.length === 0) {
    try {
      const res = await axios.get(`/comments/post/${post.id}`, getConfig())
      const userId = auth.user.mno
      const commentsWithLikeStatus = await Promise.all(
        res.data.map(async (c) => {
          try {
            const likeRes = await axios.get(
              `/comments/${c.commentId}/isliked`,
              getConfig({ userId }),
            )
            return {
              id: c.commentId,
              author: c.authorName,
              content: c.content,
              date: new Date(c.created),
              likes: c.likes,
              liked: likeRes.data.isLiked || likeRes.data === 1,
            }
          } catch (err) {
            return {
              ...c,
              id: c.commentId,
              author: c.authorName,
              content: c.content,
              date: new Date(c.created),
              likes: c.likes,
              liked: false,
            }
          }
        }),
      )
      post.comments = commentsWithLikeStatus
    } catch (err) {
      showToast('댓글을 불러오지 못했습니다.')
    }
  }
}

async function addComment(post) {
  try {
    const text = newComments.value[post.id]
    if (!text.trim()) return
    await axios.post(
      '/comments',
      { postId: post.id, userId: auth.user.mno, content: text },
      getConfig(),
    )
    const res = await axios.get(`/comments/post/${post.id}`, getConfig())
    const userId = auth.user.mno
    const commentsWithLikeStatus = await Promise.all(
      res.data.map(async (c) => {
        try {
          const likeRes = await axios.get(`/comments/${c.commentId}/isliked`, getConfig({ userId }))
          return {
            id: c.commentId,
            author: c.authorName,
            content: c.content,
            date: new Date(c.created),
            likes: c.likes,
            liked: likeRes.data.isLiked || likeRes.data === 1,
          }
        } catch (err) {
          return {
            ...c,
            id: c.commentId,
            author: c.authorName,
            content: c.content,
            date: new Date(c.created),
            likes: c.likes,
            liked: false,
          }
        }
      }),
    )
    post.comments = commentsWithLikeStatus
    post.commentCount = post.comments.length
    newComments.value[post.id] = ''
    showToast('댓글이 등록되었습니다!')
  } catch (err) {
    showToast('댓글 등록에 실패했습니다.')
  }
}

async function toggleCommentLike(post, comment) {
  try {
    if (!comment.liked) {
      await axios.post(`/comments/${comment.id}/like`, null, getConfig({ userId: auth.user.mno }))
    } else {
      await axios.delete(`/comments/${comment.id}/like`, getConfig({ userId: auth.user.mno }))
    }
    const likeRes = await axios.get(
      `/comments/${comment.id}/isliked`,
      getConfig({ userId: auth.user.mno }),
    )
    comment.liked = likeRes.data.isLiked || likeRes.data === 1
    const res = await axios.get(`/comments/${comment.id}`, getConfig())
    comment.likes = res.data.likes
    post.commentCount = post.comments.length
  } catch (err) {
    showToast('댓글 좋아요 처리에 실패했습니다.')
  }
}
</script>

<style scoped>
.login-prompt {
  height: calc(100vh - var(--topbar-height));
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
  background: none;
}

.empty-icon {
  font-size: 5rem;
  margin-bottom: 20px;
  color: var(--primary-color);
}

.login-prompt h2 {
  color: var(--primary-color);
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 10px;
}

.login-prompt p {
  color: var(--text-light);
  font-size: 1rem;
  margin-bottom: 30px;
  max-width: 360px;
  line-height: 1.5;
}

.login-button {
  background-color: var(--primary-color);
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 12px 24px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  width: 160px;
  transition: background-color 0.2s ease;
}

.login-button:hover {
  background-color: #004c8e;
}

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

.forum-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.forum-header h2 {
  color: var(--primary-color);
  margin: 0;
}

.new-post-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 15px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.new-post-button:hover {
  background-color: #004c8e;
}

.new-post-form {
  background-color: var(--background-light);
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.new-post-form h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: var(--primary-color);
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
}

.form-group textarea {
  min-height: 100px;
  resize: vertical;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-button,
.submit-button {
  padding: 8px 15px;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
}

.cancel-button {
  background-color: transparent;
  border: 1px solid var(--border-color);
  color: var(--text-color);
}

.submit-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
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
</style>
