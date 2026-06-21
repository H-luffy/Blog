<template>
  <div class="page-wrapper">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-bar"></div>
        STUDY<span class="dot">.</span>BLOG
      </div>
      <ul class="nav-list">
        <li class="nav-item secondary" @click="$router.push('/')">返回首页</li>
      </ul>
    </aside>

    <main class="content" style="display: flex; align-items: center; justify-content: center;">
      <div class="not-found-card" :class="{ 'fade-in': !initialLoad }">
        <div class="not-found-num">404</div>
        <p class="not-found-text">页面走丢了</p>
        <p class="not-found-desc">你访问的页面似乎去了另一个维度</p>
        <router-link to="/" class="not-found-btn">返回首页</router-link>
      </div>
    </main>

    <aside class="sidebar right-sidebar"></aside>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const initialLoad = ref(false);

onMounted(() => {
  setTimeout(() => { initialLoad.value = true; }, 100);
});
</script>
