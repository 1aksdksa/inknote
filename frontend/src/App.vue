<script setup lang="ts">
import { computed } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import AiKittyBuddy from '@/components/AiKittyBuddy.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const route = useRoute()

const showKitty = computed(() => {
  if (!auth.token) return false
  const name = route.name
  return name === 'notes' || name === 'note-edit'
})
</script>

<template>
  <div class="app">
    <RouterView />
    <AiKittyBuddy v-if="showKitty" />
  </div>
</template>