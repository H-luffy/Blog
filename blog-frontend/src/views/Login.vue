<template>
  <div style="display: grid; grid-template-columns: 200px 1fr; gap: 48px; max-width: 920px; margin: 0 auto; padding: 48px 32px; min-height: 100vh; align-items: center;">

    <!-- 左侧侧边栏 -->
    <aside style="position: sticky; top: 48px;">
      <div style="font-weight: 700; font-size: 14px; letter-spacing: 3px; margin-bottom: 32px;">
        STUDY<span style="color: #0ea5e9;">.</span>BLOG
      </div>
      <ul style="list-style: none;">
        <li @click="$router.push('/')" style="padding: 8px 0; font-size: 13px; color: #999; cursor: pointer;">返回首页</li>
      </ul>
    </aside>

    <!-- 右侧登录表单 -->
    <main style="max-width: 360px;">
      <div style="background: #fff; padding: 32px; border-radius: 8px; border: 1px solid #eee; box-shadow: 0 2px 12px rgba(0,0,0,0.04);">
        <h2 style="font-size: 20px; font-weight: 700; margin-bottom: 24px; text-align: center;">后台登录</h2>

        <div style="margin-bottom: 16px;">
          <label style="display: block; font-size: 12px; color: #999; margin-bottom: 6px;">用户名</label>
          <input v-model="form.username" type="text" placeholder="请输入用户名"
            style="width: 100%; padding: 10px 14px; border: 1px solid #e5e5e5; border-radius: 6px; font-size: 13px; outline: none;"
            @keyup.enter="handleLogin"
          />
        </div>

        <div style="margin-bottom: 24px;">
          <label style="display: block; font-size: 12px; color: #999; margin-bottom: 6px;">密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码"
            style="width: 100%; padding: 10px 14px; border: 1px solid #e5e5e5; border-radius: 6px; font-size: 13px; outline: none;"
            @keyup.enter="handleLogin"
          />
        </div>

        <button @click="handleLogin" :disabled="loading"
          style="width: 100%; padding: 10px; background: #111; color: #fff; border: none; border-radius: 6px; font-size: 13px; font-weight: 600; cursor: pointer;"
        >{{ loading ? '登录中...' : '登 录' }}</button>

        <p v-if="errorMsg" style="color: #f56c6c; text-align: center; margin-top: 16px; font-size: 13px;">{{ errorMsg }}</p>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
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
</script>
