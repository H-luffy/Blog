<template>
  <div class="page-wrapper">

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

    <!-- 中间登录表单 -->
    <main class="content login-main">
      <!-- 背景氛围光 -->
      <div class="ambient-light light-1"></div>
      <div class="ambient-light light-2"></div>
      <div class="ambient-light light-3"></div>

      <div class="login-card" :class="{ 'fade-in': !initialLoad }">
        <h2 class="login-title">后台登录</h2>

        <div class="form-group">
          <label>用户名</label>
          <input v-model="form.username" type="text" placeholder="请输入用户名"
            class="form-input" @keyup.enter="handleLogin"
          />
        </div>

        <div class="form-group">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码"
            class="form-input" @keyup.enter="handleLogin"
          />
        </div>

        <button @click="handleLogin" :disabled="loading" class="login-btn">
          {{ loading ? '登录中...' : '登 录' }}
        </button>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>

        <div class="back-link">
          <router-link to="/">← 返回首页</router-link>
        </div>
      </div>
    </main>

    <!-- 右侧留白区 -->
    <aside class="sidebar right-sidebar"></aside>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const initialLoad = ref(false);
const loading = ref(false);
const errorMsg = ref('');

const form = reactive({ username: '', password: '' });

const handleLogin = async () => {
  if (!form.username || !form.password) { errorMsg.value = '用户名和密码不能为空'; return; }
  loading.value = true; errorMsg.value = '';
  try {
    const res = await axios.post('http://localhost:8081/login', form);
    if (res.data.code === 200) {
      localStorage.setItem('token', res.data.data.token);
      localStorage.setItem('username', res.data.data.username);
      router.push('/admin');
    } else {
      errorMsg.value = res.data.message || '登录失败';
    }
  } catch (err) {
    errorMsg.value = '网络连接失败，请检查后端是否启动';
  } finally {
    loading.value = false;
  }
};

setTimeout(() => { initialLoad.value = true; }, 100);
</script>
