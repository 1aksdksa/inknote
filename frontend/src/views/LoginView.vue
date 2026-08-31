<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { Button } from '@/components/ui/button'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const username = ref('')
const password = ref('')
const loading = ref(false)

async function onSubmit() {
  loading.value = true
  try {
    await auth.login({ username: username.value.trim(), password: password.value })
    await router.replace({ name: 'notes' })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="mx-auto flex min-h-screen max-w-md flex-col justify-center px-5 py-10">
    <h1 class="mb-2 text-3xl font-semibold tracking-tight">InkNote</h1>
    <p class="mb-8 text-muted-foreground">登录后开始记录</p>

    <form class="space-y-4" @submit.prevent="onSubmit">
      <label class="block space-y-2">
        <span class="text-sm text-muted-foreground">用户名</span>
        <input
          v-model="username"
          required
          autocomplete="username"
          class="h-11 w-full rounded-md border border-input bg-background px-3 outline-none focus-visible:ring-2 focus-visible:ring-ring"
        />
      </label>
      <label class="block space-y-2">
        <span class="text-sm text-muted-foreground">密码</span>
        <input
          v-model="password"
          type="password"
          required
          autocomplete="current-password"
          class="h-11 w-full rounded-md border border-input bg-background px-3 outline-none focus-visible:ring-2 focus-visible:ring-ring"
        />
      </label>
      <Button class="h-11 w-full" type="submit" :disabled="loading">
        {{ loading ? '登录中…' : '登录' }}
      </Button>
    </form>

    <p class="mt-6 text-sm text-muted-foreground">
      还没有账号？
      <RouterLink class="text-foreground underline-offset-4 hover:underline" :to="{ name: 'register' }">
        去注册
      </RouterLink>
    </p>
  </main>
</template>