<template>
  <div class="page-wrapper">

    <!-- 左侧侧边栏 -->
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-bar"></div>
        STUDY<span class="dot">.</span>BLOG
      </div>
      <ul class="nav-list">
        <li class="nav-item active">文章管理</li>
        <li class="nav-item secondary" @click="$router.push('/')">返回首页</li>
      </ul>
      <div class="divider"></div>
      <ul class="nav-list">
        <li class="nav-item secondary">{{ username }}</li>
      </ul>
    </aside>

    <!-- 中间主内容 -->
    <main class="content" :class="{ 'fade-in': !initialLoad }">
      <!-- 顶部操作栏 -->
      <div class="toolbar">
        <h1>文章管理后台</h1>
        <el-button type="danger" text size="small" @click="handleLogout">退出登录</el-button>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card stat-green">
          <span class="stat-card-num">{{ stats.totalArticles }}</span>
          <span class="stat-card-label">总文章</span>
        </div>
        <div class="stat-card stat-orange">
          <span class="stat-card-num">{{ stats.totalDrafts }}</span>
          <span class="stat-card-label">草稿</span>
        </div>
        <div class="stat-card stat-blue">
          <span class="stat-card-num">{{ stats.publishedCount }}</span>
          <span class="stat-card-label">已发布</span>
        </div>
        <div class="stat-card stat-purple">
          <span class="stat-card-num">{{ stats.totalTags }}</span>
          <span class="stat-card-label">标签</span>
        </div>
      </div>

      <!-- 搜索栏 -->
      <div class="search-row">
        <span class="search-icon">🔍</span>
        <input
          v-model="keyword"
          type="text"
          placeholder="搜索文章标题..."
          @keyup.enter="handleSearch"
          class="search-input"
        />
        <button @click="handleSearch" class="btn btn-dark">搜索</button>
        <button @click="resetSearch" class="btn btn-outline">重置</button>
        <button @click="openAddDialog" class="btn btn-primary">＋ 新增</button>
      </div>

      <!-- 文章列表表格 -->
      <el-table :data="articles" stripe style="width: 100%" v-loading="tableLoading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="summary" label="摘要" min-width="250" show-overflow-tooltip />
        <el-table-column label="标签" min-width="150">
          <template #default="{ row }">
            <el-tag
              v-for="tag in row.tags"
              :key="tag.id"
              size="small"
              style="margin-right: 4px; margin-bottom: 2px;"
            >
              {{ tag.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-popconfirm title="确定要删除这篇文章吗？" @confirm="deleteArticle(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

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

      <p v-if="!tableLoading && articles.length === 0" class="empty-state">
        暂无文章，点击"新增文章"开始吧！
      </p>

      <!-- 页脚 -->
      <footer class="site-footer">
        © 2026 Study Blog · 由 <span>Vue 3</span> + <span>Spring Boot</span> 驱动
      </footer>

      <!-- 新增/编辑对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑文章' : '新增文章'"
        width="800px"
      >
        <el-form :model="form" label-width="80px">
          <el-form-item label="标题">
            <el-input v-model="form.title" placeholder="请输入文章标题" />
          </el-form-item>
          <el-form-item label="摘要">
            <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="请输入文章摘要" />
          </el-form-item>
          <el-form-item label="内容">
            <MdEditor v-model="form.content" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="封面图">
            <el-input v-model="form.coverImage" placeholder="请输入封面图 URL" />
          </el-form-item>
          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio :value="0">草稿</el-radio>
              <el-radio :value="1">已发布</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="标签">
            <el-checkbox-group v-model="form.tagIds">
              <el-checkbox
                v-for="tag in allTags"
                :key="tag.id"
                :label="tag.id"
                :value="tag.id"
              >
                {{ tag.name }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-form>

        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveArticle" :loading="saving">保存</el-button>
        </template>
      </el-dialog>
    </main>

    <!-- 右侧面板 -->
    <aside class="sidebar right-sidebar">
      <div class="panel stats-panel">
        <div class="stat-item">
          <span class="stat-num">{{ stats.totalArticles }}</span>
          <span class="stat-label">文章</span>
        </div>
        <div class="stat-item">
          <span class="stat-num">{{ stats.totalDrafts }}</span>
          <span class="stat-label">草稿</span>
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
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { MdEditor } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

const router = useRouter();
const initialLoad = ref(false);
const username = ref(localStorage.getItem('username') || '');

const articles = ref([]);
const tableLoading = ref(false);
const dialogVisible = ref(false);
const saving = ref(false);
const keyword = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const isEdit = ref(false);
const editId = ref(null);

const form = ref({
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  status: 1,
  tagIds: []
});

const allTags = ref([]);

const stats = ref({
  totalArticles: 0,
  totalDrafts: 0,
  publishedCount: 0,
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
  tableLoading.value = true;
  try {
    const params = { page: currentPage.value, pageSize: pageSize.value };
    if (keyword.value) params.title = keyword.value;
    const res = await axios.get('http://localhost:8081/api/articles', { params });
    if (res.data.code === 200) {
      articles.value = res.data.data.rows;
      total.value = res.data.data.total;
      // 统计草稿数和已发布数
      const drafts = res.data.data.rows.filter(a => a.status === 0).length;
      const published = res.data.data.rows.filter(a => a.status === 1).length;
      stats.value.totalDrafts = drafts;
      stats.value.publishedCount = published;
      stats.value.totalArticles = res.data.data.total;
    }
  } catch (error) { ElMessage.error('加载文章失败'); }
  finally { tableLoading.value = false; }
};

const handleSearch = () => { currentPage.value = 1; fetchArticles(); };
const resetSearch = () => { keyword.value = ''; currentPage.value = 1; fetchArticles(); };

const openAddDialog = () => {
  isEdit.value = false; editId.value = null;
  form.value = { title: '', summary: '', content: '', coverImage: '', status: 1, tagIds: [] };
  dialogVisible.value = true;
};

const openEditDialog = (row) => {
  isEdit.value = true; editId.value = row.id;
  form.value = {
    title: row.title, summary: row.summary, content: row.content,
    coverImage: row.coverImage || '', status: row.status,
    tagIds: row.tags ? row.tags.map(t => t.id) : []
  };
  dialogVisible.value = true;
};

const saveArticle = async () => {
  if (!form.value.title) { ElMessage.warning('标题不能为空'); return; }
  saving.value = true;
  try {
    const data = { ...form.value };
    data.tags = data.tagIds.map(id => ({ id }));
    delete data.tagIds;
    if (isEdit.value) {
      data.id = editId.value;
      await axios.put('http://localhost:8081/api/articles', data);
      ElMessage.success('更新成功');
    } else {
      await axios.post('http://localhost:8081/api/articles', data);
      ElMessage.success('新增成功');
    }
    dialogVisible.value = false;
    fetchArticles();
  } catch (error) { ElMessage.error('保存失败'); }
  finally { saving.value = false; }
};

const deleteArticle = async (id) => {
  try {
    await axios.delete(`http://localhost:8081/api/articles/${id}`);
    ElMessage.success('删除成功');
    fetchArticles();
  } catch (error) { ElMessage.error('删除失败'); }
};

const handleLogout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('username');
  username.value = '';
  router.push('/login');
  ElMessage.success('已退出登录');
};

onMounted(() => {
  fetchTags();
  fetchArticles();
  setTimeout(() => { initialLoad.value = true; }, 100);
});
</script>
