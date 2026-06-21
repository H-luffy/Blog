<template>
  <div style="display: grid; grid-template-columns: 1fr; min-height: 100vh; background: #e2e2e8; align-items: center; justify-items: center;">
    <div style="background: #fff; padding: 48px; border-radius: 12px; box-shadow: 0 4px 24px rgba(0,0,0,0.08); width: 380px;">
      <h2 style="font-size: 24px; font-weight: 700; margin-bottom: 32px; text-align: center; letter-spacing: 2px;">后台登录</h2>

      <div style="margin-bottom: 20px;">
        <label style="display: block; font-size: 13px; color: #666; margin-bottom: 8px;">用户名</label>
        <input v-model="form.username" type="text" placeholder="请输入用户名"
          style="width: 100%; padding: 12px 16px; border: 1px solid #e5e5e5; border-radius: 8px; font-size: 14px; outline: none; transition: border-color 0.2s;"
          @focus="$event.target.style.borderColor = '#0ea5e9'"
          @blur="$event.target.style.borderColor = '#e5e5e5'"
          @keyup.enter="handleLogin"
        />
      </div>

      <div style="margin-bottom: 28px;">
        <label style="display: block; font-size: 13px; color: #666; margin-bottom: 8px;">密码</label>
        <input v-model="form.password" type="password" placeholder="请输入密码"
          style="width: 100%; padding: 12px 16px; border: 1px solid #e5e5e5; border-radius: 8px; font-size: 14px; outline: none; transition: border-color 0.2s;"
          @focus="$event.target.style.borderColor = '#0ea5e9'"
          @blur="$event.target.style.borderColor = '#e5e5e5'"
          @keyup.enter="handleLogin"
        />
      </div>

      <button @click="handleLogin" :disabled="loading"
        style="width: 100%; padding: 12px; background: #111; color: #fff; border: none; border-radius: 8px; font-size: 15px; font-weight: 600; cursor: pointer; letter-spacing: 4px; transition: background 0.2s;"
        @mouseenter="$event.target.style.background = '#333'"
        @mouseleave="$event.target.style.background = '#111'"
      >{{ loading ? '登录中...' : '登 录' }}</button>

      <p v-if="errorMsg" style="color: #f56c6c; text-align: center; margin-top: 20px; font-size: 13px;">{{ errorMsg }}</p>

      <div style="text-align: center; margin-top: 24px;">
        <router-link to="/" style="color: #999; text-decoration: none; font-size: 13px;">← 返回首页</router-link>
      </div>
    </div>
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
