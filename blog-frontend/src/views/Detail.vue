<template>
  <div style="display: grid; grid-template-columns: 200px 1fr; gap: 48px; max-width: 920px; margin: 0 auto; padding: 48px 32px; min-height: 100vh;">

    <!-- 左侧侧边栏 -->
    <aside style="position: sticky; top: 48px; height: fit-content;">
      <div style="font-weight: 700; font-size: 14px; letter-spacing: 3px; margin-bottom: 32px;">
        STUDY<span style="color: #0ea5e9;">.</span>BLOG
      </div>
      <ul style="list-style: none;">
        <li @click="$router.push('/')" style="padding: 8px 0; font-size: 13px; color: #999; cursor: pointer;">返回首页</li>
      </ul>
    </aside>

    <!-- 右侧文章内容 -->
    <main style="min-width: 0;">
      <div v-loading="loading">
        <div v-if="article" style="background: #fff; padding: 32px; border-radius: 8px; border: 1px solid #eee;">
          <h1 style="font-size: 28px; font-weight: 700; margin-bottom: 12px;">{{ article.title }}</h1>
          <p style="font-size: 12px; color: #bbb; margin-bottom: 24px; display: flex; gap: 16px; flex-wrap: wrap;">
            <span>{{ article.createTime }}</span>
            <span>{{ article.viewCount }} 阅读</span>
            <span v-if="article.tags && article.tags.length">
              <span
                v-for="tag in article.tags"
                :key="tag.id"
                style="font-size: 9px; font-weight: 700; letter-spacing: 1px; text-transform: uppercase; padding: 2px 8px; background: #f0f9ff; color: #0ea5e9; border-radius: 4px;"
              >{{ tag.name }}</span>
            </span>
          </p>
          <MdPreview :editorId="`preview-${article.id}`" :model-value="article.content" style="line-height: 2; color: #333;" />
        </div>
        <div v-else-if="!loading" style="text-align: center; padding: 60px 0; color: #999;">文章不存在或已被删除</div>
      </div>
    </main>
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
