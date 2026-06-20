<template>
  <div style="max-width: 1200px; margin: 0 auto; padding: 20px;">
    <!-- 顶部导航 -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; padding: 16px 0; border-bottom: 2px solid #409eff;">
      <h1 style="margin: 0; color: #333;">📋 文章管理后台</h1>
      <div style="display: flex; gap: 12px;">
        <span style="color: #999; font-size: 14px;">欢迎, {{ username }}</span>
        <el-button type="danger" text @click="handleLogout">退出</el-button>
        <el-button type="info" @click="$router.push('/')">← 返回首页</el-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div style="display: flex; gap: 12px; margin-bottom: 20px;">
      <el-input
        v-model="keyword"
        placeholder="搜索文章标题..."
        clearable
        style="flex: 1;"
        @keyup.enter="handleSearch"
      />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
      <el-button type="success" @click="openAddDialog">＋ 新增文章</el-button>
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
    <div style="display: flex; justify-content: center; margin-top: 24px;">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchArticles"
      />
    </div>

    <p v-if="!tableLoading && articles.length === 0" style="text-align: center; color: #999; margin-top: 40px;">
      暂无文章，点击"新增文章"开始吧！
    </p>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑文章' : '新增文章'"
      width="700px"
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { MdEditor } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

const router = useRouter();
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

// 加载所有标签
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

// 加载文章列表
const fetchArticles = async () => {
  tableLoading.value = true;
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
    ElMessage.error('加载文章失败');
  } finally {
    tableLoading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchArticles();
};

// 重置搜索
const resetSearch = () => {
  keyword.value = '';
  currentPage.value = 1;
  fetchArticles();
};

// 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false;
  editId.value = null;
  form.value = {
    title: '',
    summary: '',
    content: '',
    coverImage: '',
    status: 1,
    tagIds: []
  };
  dialogVisible.value = true;
};

// 打开编辑弹窗
const openEditDialog = (row) => {
  isEdit.value = true;
  editId.value = row.id;
  form.value = {
    title: row.title,
    summary: row.summary,
    content: row.content,
    coverImage: row.coverImage || '',
    status: row.status,
    tagIds: row.tags ? row.tags.map(t => t.id) : []
  };
  dialogVisible.value = true;
};

// 保存文章（新增或编辑）
const saveArticle = async () => {
  if (!form.value.title) {
    ElMessage.warning('标题不能为空');
    return;
  }

  saving.value = true;
  try {
    const data = { ...form.value };
    // 把 tagIds 转成 tag 对象数组
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
  } catch (error) {
    ElMessage.error('保存失败');
  } finally {
    saving.value = false;
  }
};

// 删除文章
const deleteArticle = async (id) => {
  try {
    await axios.delete(`http://localhost:8081/api/articles/${id}`);
    ElMessage.success('删除成功');
    fetchArticles();
  } catch (error) {
    ElMessage.error('删除失败');
  }
};

// 退出登录
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
});
</script>
