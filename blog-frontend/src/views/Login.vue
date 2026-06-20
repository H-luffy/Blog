<template>
  <div style="display: flex; justify-content: center; align-items: center; height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
    <el-card style="width: 400px; box-shadow: 0 8px 32px rgba(0,0,0,0.15);">
      <template #header>
        <h2 style="text-align: center; margin: 0; color: #333;">🔐 博客后台登录</h2>
      </template>

      <el-form :model="form" label-width="60px">
        <el-form-item label="用户名">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            @click="handleLogin"
            :loading="loading"
            style="width: 100%;"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <p v-if="errorMsg" style="color: #f56c6c; text-align: center; font-size: 14px;">{{ errorMsg }}</p>

      <div style="text-align: center; margin-top: 16px;">
        <router-link to="/" style="color: #409eff; text-decoration: none; font-size: 14px;">← 返回首页</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus';

const router = useRouter();
const loading = ref(false);
const errorMsg = ref('');

const form = reactive({
  username: '',
  password: ''
});

const handleLogin = async () => {
  if (!form.username || !form.password) {
    errorMsg.value = '用户名和密码不能为空';
    return;
  }

  loading.value = true;
  errorMsg.value = '';

  try {
    const res = await axios.post('http://localhost:8081/login', form);
    if (res.data.code === 200) {
      ElMessage.success('登录成功！');
      localStorage.setItem('token', res.data.data.token);
      localStorage.setItem('username', res.data.data.username);
      router.push('/admin');
    } else {
      errorMsg.value = res.data.message || '登录失败';
    }
  } catch (err) {
    console.error('登录出错:', err);
    errorMsg.value = '网络连接失败，请检查后端是否启动';
  } finally {
    loading.value = false;
  }
};
</script>