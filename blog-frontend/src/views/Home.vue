<template>
  <div style="max-width: 800px; margin: 0 auto; padding: 20px;">
    <!-- 顶部导航 -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; padding: 20px 0; border-bottom: 2px solid #409eff;">
      <h1 style="margin: 0; color: #333;">📝 我的博客</h1>
      <el-button type="primary" text @click="$router.push('/login')">后台管理</el-button>
    </div>

    <!-- 搜索框 -->
    <el-input
      v-model="keyword"
      placeholder="搜索文章标题..."
      clearable
      style="margin-bottom: 24px;"
      @keyup.enter="handleSearch"
    >
      <template #append>
        <el-button @click="handleSearch">搜索</el-button>
      </template>
    </el-input>

    <!-- 文章列表 -->
    <div v-loading="loading">
      <div
        v-for="article in articles"
        :key="article.id"
        style="background: #fff; padding: 24px; margin-bottom: 16px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.06); transition: transform 0.2s;"
        @mouseenter="$event.currentTarget.style.transform = 'translateY(-2px)'"
        @mouseleave="$event.currentTarget.style.transform = 'translateY(0)'"
      >
        <h2 style="margin: 0 0 8px 0; color: #333;">
          <router-link :to="`/article/${article.id}`" style="color: inherit; text-decoration: none;">
            {{ article.title }}
          </router-link>
        </h2>
        <p style="color: #999; font-size: 13px; margin: 0 0 8px 0;">
          {{ article.createTime }} · 阅读 {{ article.viewCount }}
        </p>
        <div style="margin-bottom: 8px;">
          <el-tag
            v-for="tag in article.tags"
            :key="tag.id"
            size="small"
            style="margin-right: 6px; margin-bottom: 4px;"
          >
            {{ tag.name }}
          </el-tag>
        </div>
        <p style="color: #666; line-height: 1.6; margin: 0;">{{ article.summary }}</p>
        <el-button type="primary" text style="padding: 8px 0;">
          <router-link :to="`/article/${article.id}`" style="color: inherit; text-decoration: none;">
            阅读全文 →
          </router-link>
        </el-button>
      </div>

      <p v-if="!loading && articles.length === 0" style="text-align: center; color: #999; padding: 40px 0;">
        暂无文章
      </p>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" style="display: flex; justify-content: center; margin-top: 30px;">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchArticles"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const articles = ref([]);
const loading = ref(true);
const keyword = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const fetchArticles = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    };
    if (keyword.value) {
      params.title = keyword.value;
    }
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

const handleSearch = () => {
  currentPage.value = 1;
  fetchArticles();
};

onMounted(() => {
  fetchArticles();
});
</script>