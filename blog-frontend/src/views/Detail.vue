<template>
  <div class="page-wrapper">
    <!-- 阅读进度条 -->
    <div class="reading-progress" :style="{ width: progressWidth + '%' }"></div>

    <!-- 左侧侧边栏 -->
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-bar"></div>
        STUDY<span class="dot">.</span>BLOG
      </div>
      <ul class="nav-list">
        <li class="nav-item secondary" @click="$router.push('/')">返回首页</li>
      </ul>
    </aside>

    <!-- 中间文章内容 -->
    <main class="content" :class="{ 'fade-in': !initialLoad }">
      <div v-loading="loading" element-loading-background="rgba(255,255,255,0.8)">
        <div v-if="article" class="article-detail">
          <h1 class="detail-title">{{ article.title }}</h1>
          <p class="detail-meta">
            <span>{{ article.createTime }}</span>
            <span class="dot"></span>
            <span>{{ article.viewCount }} 阅读</span>
            <span class="dot"></span>
            <span class="read-time">📖 预计 {{ readTime }} 分钟</span>
            <span v-if="article.tags && article.tags.length" class="detail-tags">
              <span
                v-for="tag in article.tags"
                :key="tag.id"
                class="detail-tag"
                :class="'tag-' + tag.id"
              >{{ tag.name }}</span>
            </span>
          </p>
          <MdPreview :editorId="`preview-${article.id}`" :model-value="article.content" style="line-height: 2; color: #333;" />
        </div>
        <div v-else-if="!loading" class="empty-state">文章不存在或已被删除</div>
      </div>

      <!-- 页脚 -->
      <footer class="site-footer">
        © 2026 Study Blog · 由 <span>Vue 3</span> + <span>Spring Boot</span> 驱动
      </footer>
    </main>

    <!-- 右侧面板 -->
    <aside class="sidebar right-sidebar">
      <div class="panel stats-panel">
        <div class="stat-item">
          <span class="stat-num">{{ stats.totalArticles }}</span>
          <span class="stat-label">文章</span>
        </div>
        <div class="stat-item">
          <span class="stat-num">{{ stats.totalViews }}</span>
          <span class="stat-label">阅读</span>
        </div>
        <div class="stat-item">
          <span class="stat-num">{{ stats.totalTags }}</span>
          <span class="stat-label">标签</span>
        </div>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';

const route = useRoute();
const initialLoad = ref(false);
const article = ref(null);
const loading = ref(true);
const progressWidth = ref(0);

const stats = ref({
  totalArticles: 0,
  totalViews: 0,
  totalTags: 0
});

const readTime = ref(0);

const handleScroll = () => {
  const scrollTop = window.scrollY;
  const docHeight = document.documentElement.scrollHeight - window.innerHeight;
  if (docHeight > 0) {
    progressWidth.value = (scrollTop / docHeight) * 100;
  }
};

onMounted(async () => {
  const id = route.params.id;
  try {
    const res = await axios.get(`http://localhost:8081/api/articles/${id}`);
    if (res.data.code === 200) {
      article.value = res.data.data;
      // 计算预计阅读时间（字数 / 200 字每分钟）
      const charCount = (article.value.content || '').length;
      readTime.value = Math.max(1, Math.ceil(charCount / 200));
      // 获取统计
      const tagsRes = await axios.get('http://localhost:8081/api/articles/tags');
      if (tagsRes.data.code === 200) stats.value.totalTags = tagsRes.data.data.length;
      const listRes = await axios.get('http://localhost:8081/api/articles', { params: { page: 1, pageSize: 1 } });
      if (listRes.data.code === 200) stats.value.totalArticles = listRes.data.data.total;
    }
  } catch (error) { console.error('获取文章失败:', error); }
  finally { loading.value = false; }
  setTimeout(() => { initialLoad.value = true; }, 100);
  window.addEventListener('scroll', handleScroll);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>
