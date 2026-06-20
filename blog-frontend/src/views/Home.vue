<template>
  <div style="display: grid; grid-template-columns: 200px 1fr; gap: 48px; max-width: 920px; margin: 0 auto; padding: 48px 32px; min-height: 100vh;">

    <!-- 左侧侧边栏 -->
    <aside style="position: sticky; top: 48px; height: fit-content;">
      <div style="font-weight: 700; font-size: 14px; letter-spacing: 3px; margin-bottom: 32px;">
        STUDY<span style="color: #0ea5e9;">.</span>BLOG
      </div>
      <ul style="list-style: none;">
        <li
          @click="activeTag = ''; fetchArticles()"
          :style="{
            padding: '8px 0', fontSize: '13px', cursor: 'pointer',
            color: activeTag === '' ? '#111' : '#999',
            fontWeight: activeTag === '' ? '600' : '400',
            borderBottom: activeTag === '' ? '2px solid #0ea5e9' : 'none'
          }"
        >全部文章</li>
        <li
          v-for="tag in allTags"
          :key="tag.id"
          @click="activeTag = tag.name; fetchArticlesByTag(tag.name)"
          :style="{
            padding: '8px 0', fontSize: '13px', cursor: 'pointer',
            color: activeTag === tag.name ? '#111' : '#999',
            fontWeight: activeTag === tag.name ? '600' : '400',
            borderBottom: activeTag === tag.name ? '2px solid #0ea5e9' : 'none'
          }"
        >{{ tag.name }}</li>
      </ul>
      <div style="height: 1px; background: #e5e5e5; margin: 16px 0;"></div>
      <ul style="list-style: none;">
        <li @click="$router.push('/login')" style="padding: 8px 0; font-size: 13px; color: #999; cursor: pointer;">后台管理</li>
      </ul>
    </aside>

    <!-- 右侧主内容 -->
    <main style="min-width: 0;">
      <!-- Hero -->
      <div style="margin-bottom: 40px;">
        <h1 style="font-size: 28px; font-weight: 700; margin-bottom: 8px;">我的文章</h1>
        <p style="font-size: 13px; color: #999;">共 {{ total }} 篇文章</p>
      </div>

      <!-- 搜索 -->
      <div style="display: flex; gap: 0; margin-bottom: 32px;">
        <input
          v-model="keyword"
          type="text"
          placeholder="搜索文章..."
          @keyup.enter="handleSearch"
          style="flex: 1; padding: 10px 14px; border: 1px solid #e5e5e5; border-right: none; font-family: 'Inter', sans-serif; font-size: 13px; outline: none;"
        />
        <button @click="handleSearch" style="padding: 10px 20px; background: #111; color: #fff; border: none; font-family: 'Inter', sans-serif; font-size: 12px; font-weight: 600; cursor: pointer;">搜索</button>
      </div>

      <!-- 文章列表 -->
      <div v-loading="loading">
        <div
          v-for="article in articles"
          :key="article.id"
          @click="$router.push(`/article/${article.id}`)"
          style="background: #fff; padding: 24px; margin-bottom: 12px; border: 1px solid #eee; border-radius: 8px; transition: border-color 0.2s; cursor: pointer;"
          @mouseenter="$event.currentTarget.style.borderColor = '#0ea5e9'"
          @mouseleave="$event.currentTarget.style.borderColor = '#eee'"
        >
          <h3 style="font-size: 16px; font-weight: 600; margin-bottom: 6px;">{{ article.title }}</h3>
          <div style="font-size: 11px; color: #bbb; margin-bottom: 8px; display: flex; gap: 12px;">
            <span>{{ article.createTime }}</span>
            <span>{{ article.viewCount }} 阅读</span>
          </div>
          <p style="font-size: 13px; color: #666; line-height: 1.6; margin: 0;">{{ article.summary }}</p>
          <div style="margin-top: 10px; display: flex; gap: 6px; flex-wrap: wrap;">
            <span
              v-for="tag in article.tags"
              :key="tag.id"
              style="font-size: 9px; font-weight: 700; letter-spacing: 1px; text-transform: uppercase; padding: 2px 8px; background: #f0f9ff; color: #0ea5e9; border-radius: 4px;"
            >{{ tag.name }}</span>
          </div>
        </div>

        <p v-if="!loading && articles.length === 0" style="text-align: center; color: #999; padding: 40px 0;">暂无文章</p>
      </div>

      <!-- 分页 -->
      <div v-if="total > 0" style="display: flex; justify-content: center; gap: 6px; margin-top: 32px;">
        <span
          v-for="p in totalPages"
          :key="p"
          @click="currentPage = p; fetchArticles()"
          :style="{
            width: '28px', height: '28px', display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: '11px', borderRadius: '6px', cursor: 'pointer',
            background: p === currentPage ? '#0ea5e9' : '#fff',
            color: p === currentPage ? '#fff' : '#999',
            border: '1px solid ' + (p === currentPage ? '#0ea5e9' : '#eee')
          }"
        >{{ p }}</span>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';

const articles = ref([]);
const loading = ref(true);
const keyword = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const allTags = ref([]);
const activeTag = ref('');

const totalPages = computed(() => {
  const count = Math.ceil(total.value / pageSize.value);
  return Array.from({ length: count }, (_, i) => i + 1);
});

// 加载标签
const fetchTags = async () => {
  try {
    const res = await axios.get('http://localhost:8081/api/articles/tags');
    if (res.data.code === 200) {
      allTags.value = res.data.data;
    }
  } catch (error) {
    console.error('获取标签失败:', error);
  }
};

// 加载文章
const fetchArticles = async () => {
  loading.value = true;
  try {
    const params = { page: currentPage.value, pageSize: pageSize.value };
    if (keyword.value) params.title = keyword.value;
    const res = await axios.get('http://localhost:8081/api/articles', { params });
    if (res.data.code === 200) {
      articles.value = res.data.data.rows;
      total.value = res.data.data.total;
    }
  } catch (error) {
    console.error('获取文章失败:', error);
  } finally {
    loading.value = false;
  }
};

// 按标签筛选
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
  } catch (error) {
    console.error('按标签筛选失败:', error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  activeTag.value = '';
  fetchArticles();
};

onMounted(() => {
  fetchTags();
  fetchArticles();
});
</script>
