<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { http, HttpError, type HealthResponse } from '@/api/http'

const loading = ref(false)
const health = ref<HealthResponse | null>(null)
const error = ref('')

async function loadHealth() {
  loading.value = true
  error.value = ''
  try {
    health.value = await http.get<HealthResponse>('/health', { silent: true })
  } catch (e) {
    health.value = null
    error.value = e instanceof HttpError || e instanceof Error ? e.message : '请求失败，请确认后端已启动'
  } finally {
    loading.value = false
  }
}

onMounted(loadHealth)
</script>

<template>
  <main class="page">
    <h1>InkNote</h1>
    <p class="desc">Vue 3.5 前端 + Java Spring Boot 后端</p>

    <section class="card">
      <div class="row">
        <h2>后端连通性</h2>
        <button type="button" :disabled="loading" @click="loadHealth">
          {{ loading ? '检测中…' : '重新检测' }}
        </button>
      </div>

      <p v-if="error" class="error">{{ error }}</p>
      <ul v-else-if="health" class="meta">
        <li><span>状态</span>{{ health.status }}</li>
        <li><span>服务</span>{{ health.service }}</li>
        <li><span>时间</span>{{ health.time }}</li>
      </ul>
      <p v-else class="muted">等待检测结果…</p>
    </section>
  </main>
</template>

<style scoped>
.page {
  max-width: 720px;
  margin: 0 auto;
  padding: 48px 20px;
  text-align: left;
}

h1 {
  margin: 0 0 8px;
  font-size: 32px;
}

.desc {
  margin: 0 0 28px;
  color: #666;
}

.card {
  border: 1px solid #e5e5e5;
  border-radius: 12px;
  padding: 20px;
  background: #fff;
}

.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.row h2 {
  margin: 0;
  font-size: 18px;
}

button {
  border: 1px solid #222;
  background: #222;
  color: #fff;
  border-radius: 8px;
  padding: 8px 14px;
  cursor: pointer;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.meta {
  list-style: none;
  margin: 0;
  padding: 0;
}

.meta li {
  display: flex;
  gap: 12px;
  padding: 8px 0;
  border-top: 1px solid #f0f0f0;
}

.meta li span {
  width: 48px;
  color: #888;
}

.error {
  color: #c0392b;
  margin: 0;
}

.muted {
  color: #999;
  margin: 0;
}
</style>
