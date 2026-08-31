<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { Button } from '@/components/ui/button'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const username = ref('')
const password = ref('')
const displayName = ref('')
const loading = ref(false)

async function onSubmit() {
  loading.value = true
  try {
    await auth.register({
      username: username.value.trim(),
      password: password.value,
      displayName: displayName.value.trim() || undefined,
    })
    await router.replace({ name: 'notes' })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="mx-auto flex min-h-screen max-w-md flex-col justify-center px-5 py-10">
    <h1 class="mb-2 text-3xl font-semibold tracking-tight">创建账号</h1>
    <p class="mb-8 text-muted-foreground">注册 InkNote，笔记自动同步到你的账号</p>

    <form class="space-y-4" @submit.prevent="onSubmit">
      <label class="block space-y-2">
        <span class="text-sm text-muted-foreground">用户名</span>
        <input
          v-model="username"
          required
          minlength="3"
          maxlength="32"
          autocomplete="username"
          class="h-11 w-full rounded-md border border-input bg-background px-3 outline-none focus-visible:ring-2 focus-visible:ring-ring"
        />
      </label>
      <label class="block space-y-2">
        <span class="text-sm text-muted-foreground">昵称（可选）</span>
        <input
          v-model="displayName"
          maxlength="64"
          class="h-11 w-full rounded-md border border-input bg-background px-3 outline-none focus-visible:ring-2 focus-visible:ring-ring"
        />
      </label>
      <label class="block space-y-2">
        <span class="text-sm text-muted-foreground">密码</span>
        <input
          v-model="password"
          type="password"
          required
          minlength="6"
          autocomplete="new-password"
          class="h-11 w-full rounded-md border border-input bg-background px-3 outline-none focus-visible:ring-2 focus-visible:ring-ring"
        />
      </label>
      <Button class="h-11 w-full" type="submit" :disabled="loading">
        {{ loading ? '注册中…' : '注册并进入' }}
      </Button>
    </form>

    <p class="mt-6 text-sm text-muted-foreground">
      已有账号？
      <RouterLink class="text-foreground underline-offset-4 hover:underline" :to="{ name: 'login' }">
        去登录
      </RouterLink>
    </p>
  </main>
</template>