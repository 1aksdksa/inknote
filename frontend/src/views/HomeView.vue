<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { http, HttpError, type HealthResponse } from '@/api/http'
import { Button } from '@/components/ui/button'

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
    error.value =
      e instanceof HttpError || e instanceof Error ? e.message : '请求失败，请确认后端已启动'
  } finally {
    loading.value = false
  }
}

onMounted(loadHealth)
</script>

<template>
  <main class="mx-auto max-w-xl px-5 py-12 text-left">
    <h1 class="mb-2 text-3xl font-semibold tracking-tight">InkNote</h1>
    <p class="mb-8 text-muted-foreground">Vue 3.5 前端 + Java Spring Boot 后端</p>

    <section class="rounded-xl border border-border bg-card p-5 text-card-foreground">
      <div class="mb-4 flex items-center justify-between gap-3">
        <h2 class="text-lg font-medium">后端连通性</h2>
        <Button type="button" :disabled="loading" @click="loadHealth">
          {{ loading ? '检测中…' : '重新检测' }}
        </Button>
      </div>

      <p v-if="error" class="m-0 text-destructive">{{ error }}</p>
      <ul v-else-if="health" class="m-0 list-none space-y-0 p-0">
        <li class="flex gap-3 border-t border-border py-2 first:border-t-0">
          <span class="w-12 text-muted-foreground">状态</span>{{ health.status }}
        </li>
        <li class="flex gap-3 border-t border-border py-2">
          <span class="w-12 text-muted-foreground">服务</span>{{ health.service }}
        </li>
        <li class="flex gap-3 border-t border-border py-2">
          <span class="w-12 text-muted-foreground">时间</span>{{ health.time }}
        </li>
      </ul>
      <p v-else class="m-0 text-muted-foreground">等待检测结果…</p>
    </section>
  </main>
</template>