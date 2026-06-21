<template>
  <div class="page-wrapper">

    <!-- 左侧侧边栏 -->
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-bar"></div>
        STUDY<span class="dot">.</span>BLOG
      </div>
      <ul class="nav-list">
        <li
          class="nav-item"
          :class="{ active: activeTag === '' }"
          @click="activeTag = ''; fetchArticles()"
        >全部文章</li>
        <li
          v-for="tag in allTags"
          :key="tag.id"
          class="nav-item"
          :class="{ active: activeTag === tag.name }"
          @click="activeTag = tag.name; fetchArticlesByTag(tag.name)"
        >{{ tag.name }}</li>
      </ul>
      <div class="divider"></div>
      <ul class="nav-list">
        <li class="nav-item secondary" @click="$router.push('/login')">后台管理</li>
      </ul>
    </aside>

    <!-- 中间主内容 -->
    <main class="content" :class="{ 'fade-in': !initialLoad }">
      <!-- Hero -->
      <div class="hero">
        <h1>我的文章</h1>
        <p>共 {{ total }} 篇文章</p>
      </div>

      <!-- 搜索 -->
      <div class="search-row">
        <span class="search-icon">🔍</span>
        <input
          v-model="keyword"
          type="text"
          placeholder="搜索文章..."
          @keyup.enter="handleSearch"
          class="search-input"
        />
        <button @click="handleSearch" class="search-btn">搜索</button>
      </div>

      <!-- 文章列表 -->
      <div v-loading="loading" element-loading-background="rgba(255,255,255,0.8)">
        <div
          v-for="(article, index) in articles"
          :key="article.id"
          class="article-card"
          :class="['card-' + index, 'status-' + (article.status || 1)]"
          @click="$router.push(`/article/${article.id}`)"
        >
          <div class="card-header">
            <span class="card-icon" :class="article.status === 1 ? 'icon-published' : 'icon-draft'">
              {{ article.status === 1 ? '📄' : '📝' }}
            </span>
            <h3 class="card-title">{{ article.title }}</h3>
          </div>
          <div class="card-meta">
            <span>{{ article.createTime }}</span>
            <span class="dot"></span>
            <span>{{ article.viewCount }} 阅读</span>
            <span class="dot"></span>
            <span class="read-time">📖 {{ estimateReadTime(article.content) }} 分钟</span>
          </div>
          <p class="card-summary">{{ article.summary }}</p>
          <div class="card-tags">
            <span
              v-for="tag in article.tags"
              :key="tag.id"
              class="card-tag"
              :class="'tag-' + tag.id"
            >{{ tag.name }}</span>
          </div>
        </div>

        <p v-if="!loading && articles.length === 0" class="empty-state">暂无文章</p>
      </div>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination">
        <span
          v-for="p in totalPages"
          :key="p"
          class="page-btn"
          :class="{ active: p === currentPage }"
          @click="currentPage = p; fetchArticles()"
        >{{ p }}</span>
      </div>

      <!-- 页脚 -->
      <footer class="site-footer">
        © 2026 Study Blog · 由 <span>Vue 3</span> + <span>Spring Boot</span> 驱动
      </footer>
    </main>

    <!-- 右侧面板 -->
    <aside class="sidebar right-sidebar">
      <!-- 关于博主 -->
      <div class="panel about-panel">
        <div class="avatar">
          <img src="https://api.dicebear.com/7.x/notionists/svg?seed=Felix" alt="avatar" />
        </div>
        <h3 class="about-name">Study</h3>
        <p class="about-desc">学习博客，热爱编程，用代码记录成长。</p>
        <div class="about-links">
          <a href="https://github.com/H-luffy" class="about-link">GitHub</a>
          
          <a href="mailto:3218688828@qq.com" class="about-link">邮箱</a>
        </div>
      </div>

      <!-- 站点统计 -->
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

      <!-- 热门标签 -->
      <div class="panel tags-panel">
        <h4 class="panel-title">热门标签</h4>
        <div class="hot-tags">
          <span
            v-for="tag in allTags.slice(0, 8)"
            :key="tag.id"
            class="hot-tag"
            @click="activeTag = tag.name; fetchArticlesByTag(tag.name); $router.push('/')"
          >{{ tag.name }}</span>
        </div>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';

const initialLoad = ref(false);
const articles = ref([]);
const loading = ref(true);
const keyword = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const allTags = ref([]);
const activeTag = ref('');

const stats = ref({
  totalArticles: 0,
  totalViews: 0,
  totalTags: 0
});

const totalPages = computed(() => {
  const count = Math.ceil(total.value / pageSize.value);
  return Array.from({ length: count }, (_, i) => i + 1);
});

const fetchTags = async () => {
  try {
    const res = await axios.get('http://localhost:8081/api/articles/tags');
    if (res.data.code === 200) {
      allTags.value = res.data.data;
      stats.value.totalTags = res.data.data.length;
    }
  } catch (error) { console.error('获取标签失败:', error); }
};

const fetchArticles = async () => {
  loading.value = true;
  try {
    const params = { page: currentPage.value, pageSize: pageSize.value };
    if (keyword.value) params.title = keyword.value;
    const res = await axios.get('http://localhost:8081/api/articles', { params });
    if (res.data.code === 200) {
      articles.value = res.data.data.rows;
      total.value = res.data.data.total;
      // 计算总阅读量
      const views = res.data.data.rows.reduce((sum, a) => sum + (a.viewCount || 0), 0);
      stats.value.totalArticles = res.data.data.total;
      stats.value.totalViews += views;
    }
  } catch (error) { console.error('获取文章失败:', error); }
  finally { loading.value = false; }
};

const fetchArticlesByTag = async (tagName) => {
  activeTag.value = tagName;
  currentPage.value = 1;
  keyword.value = '';
  loading.value = true;
  try {
    const res = await axios.get(`http://localhost:8081/api/articles/by-tag?tag=${encodeURIComponent(tagName)}`);
    if (res.data.code === 200) {
      articles.value = res.data.data.rows;
      total.value = res.data.data.total;
    }
  } catch (error) { console.error('按标签筛选失败:', error); }
  finally { loading.value = false; }
};

const handleSearch = () => { currentPage.value = 1; activeTag.value = ''; fetchArticles(); };

const estimateReadTime = (content) => {
  if (!content) return 1;
  return Math.max(1, Math.ceil(content.length / 200));
};

onMounted(() => {
  fetchTags();
  fetchArticles();
  setTimeout(() => { initialLoad.value = true; }, 100);
});
</script>

<style>
/* 文章卡片交错入场 */
.card-0 { animation: slideIn 0.35s ease 0s both; }
.card-1 { animation: slideIn 0.35s ease 0.05s both; }
.card-2 { animation: slideIn 0.35s ease 0.1s both; }
.card-3 { animation: slideIn 0.35s ease 0.15s both; }
.card-4 { animation: slideIn 0.35s ease 0.2s both; }
.card-5 { animation: slideIn 0.35s ease 0.25s both; }
.card-6 { animation: slideIn 0.35s ease 0.3s both; }
.card-7 { animation: slideIn 0.35s ease 0.35s both; }
.card-8 { animation: slideIn 0.35s ease 0.4s both; }
.card-9 { animation: slideIn 0.35s ease 0.45s both; }

@keyframes slideIn {
  from { opacity: 0; transform: translateY(8px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* ===== 右侧面板 ===== */
.panel {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 16px;
}

/* 关于博主 */
.about-panel {
  text-align: center;
}

.avatar {
  width: 64px;
  height: 64px;
  margin: 0 auto 12px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #f0f9ff;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.about-name {
  font-size: 16px;
  font-weight: 700;
  margin: 0 0 6px;
  color: #111;
}

.about-desc {
  font-size: 12px;
  color: #999;
  line-height: 1.6;
  margin: 0 0 14px;
}

.about-links {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.about-link {
  font-size: 11px;
  color: #0ea5e9;
  text-decoration: none;
  padding: 4px 12px;
  border: 1px solid #e0f0ff;
  border-radius: 20px;
  transition: all 0.2s;
}

.about-link:hover {
  background: #0ea5e9;
  color: #fff;
  border-color: #0ea5e9;
}

/* 站点统计 */
.stats-panel {
  display: flex;
  justify-content: space-around;
  padding: 16px 20px;
}

.stat-item {
  text-align: center;
  cursor: default;
}

.stat-num {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #111;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 11px;
  color: #bbb;
  letter-spacing: 1px;
}

/* 热门标签 */
.tags-panel {
  padding: 16px 20px;
}

.panel-title {
  font-size: 12px;
  font-weight: 600;
  color: #666;
  margin: 0 0 12px;
  letter-spacing: 1px;
}

.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.hot-tag {
  font-size: 11px;
  color: #888;
  padding: 4px 10px;
  border: 1px solid #eee;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.hot-tag:hover {
  color: #0ea5e9;
  border-color: #0ea5e9;
  background: #f0f9ff;
}
</style>
