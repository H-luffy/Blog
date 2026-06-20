<template>
  <div style="max-width: 800px; margin: 0 auto; padding: 20px;">
    <!-- 顶部导航 -->
    <div style="display: flex; justify-content: space-between; align-items: center; padding: 20px 0; border-bottom: 2px solid #409eff; margin-bottom: 30px;">
      <router-link to="/" style="color: #409eff; text-decoration: none; font-size: 15px;">&larr; 返回首页</router-link>
      <span style="color: #999; font-size: 13px;">博客详情</span>
    </div>

    <div v-loading="loading">
      <div v-if="article" style="background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.06);">
        <h1 style="margin: 0 0 16px 0; color: #333; font-size: 28px;">{{ article.title }}</h1>
        <p style="color: #999; font-size: 13px; margin: 0 0 16px 0; display: flex; gap: 16px; flex-wrap: wrap; align-items: center;">
          <span>📅 {{ article.createTime }}</span>
          <span>👁️ 阅读 {{ article.viewCount }}</span>
          <span v-if="article.tags && article.tags.length">
            <el-tag
              v-for="tag in article.tags"
              :key="tag.id"
              size="small"
              style="margin-right: 6px;"
            >
              {{ tag.name }}
            </el-tag>
          </span>
        </p>

        <MdPreview :editorId="`preview-${article.id}`" :model-value="article.content" style="line-height: 2; color: #333;" />
      </div>

      <div v-else-if="!loading" style="text-align: center; padding: 60px 0; color: #999;">
        文章不存在或已被删除
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';

const route = useRoute();
const article = ref(null);
const loading = ref(true);

onMounted(async () => {
  const id = route.params.id;
  try {
    const res = await axios.get(`http://localhost:8081/api/articles/${id}`);
    if (res.data.code === 200) {
      article.value = res.data.data;
    }
  } catch (error) {
    console.error('获取文章失败:', error);
  } finally {
    loading.value = false;
  }
});
</script>