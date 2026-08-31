<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Button } from '@/components/ui/button'
import { deleteNote, fetchNotePage, type NoteListItem } from '@/api/note'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const keyword = ref('')
const notes = ref<NoteListItem[]>([])
const total = ref(0)

function formatTime(ms: number) {
  const d = new Date(ms)
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  const hh = String(d.getHours()).padStart(2, '0')
  const mi = String(d.getMinutes()).padStart(2, '0')
  return `${mm}-${dd} ${hh}:${mi}`
}

async function loadNotes() {
  loading.value = true
  try {
    const page = await fetchNotePage({
      current: 1,
      size: 50,
      keyword: keyword.value.trim() || undefined,
    })
    notes.value = page.records
    total.value = page.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  router.push({ name: 'note-edit' })
}

function openEdit(noteId: string) {
  router.push({ name: 'note-edit', query: { noteId } })
}

async function onDelete(note: NoteListItem) {
  if (!window.confirm(`删除笔记「${note.title}」？`)) return
  await deleteNote(note.noteId)
  await loadNotes()
}

function onLogout() {
  auth.logout()
  router.replace({ name: 'login' })
}

onMounted(loadNotes)
</script>

<template>
  <main class="mx-auto min-h-screen max-w-2xl px-4 pb-24 pt-6">
    <header class="mb-6 flex items-start justify-between gap-3">
      <div>
        <h1 class="text-2xl font-semibold tracking-tight">InkNote</h1>
        <p class="mt-1 text-sm text-muted-foreground">
          {{ auth.profile?.displayName || auth.profile?.username || '我的笔记' }}
          · {{ total }} 篇
        </p>
      </div>
      <Button variant="ghost" size="sm" type="button" @click="onLogout">退出</Button>
    </header>

    <form class="mb-4 flex gap-2" @submit.prevent="loadNotes">
      <input
        v-model="keyword"
        placeholder="搜索标题"
        class="h-11 flex-1 rounded-md border border-input bg-background px-3 outline-none focus-visible:ring-2 focus-visible:ring-ring"
      />
      <Button type="submit" variant="secondary" :disabled="loading">搜索</Button>
    </form>

    <p v-if="loading" class="py-10 text-center text-muted-foreground">加载中…</p>
    <p v-else-if="notes.length === 0" class="py-10 text-center text-muted-foreground">
      还没有笔记，点右下角新建
    </p>
    <ul v-else class="divide-y divide-border rounded-xl border border-border bg-card">
      <li
        v-for="note in notes"
        :key="note.noteId"
        class="flex items-center gap-3 px-4 py-3"
      >
        <button
          type="button"
          class="min-w-0 flex-1 text-left"
          @click="openEdit(note.noteId)"
        >
          <div class="truncate font-medium">{{ note.title }}</div>
          <div class="mt-1 text-xs text-muted-foreground">{{ formatTime(note.updatedAt) }}</div>
        </button>
        <Button variant="ghost" size="sm" type="button" @click="onDelete(note)">删除</Button>
      </li>
    </ul>

    <button
      type="button"
      class="fixed bottom-6 right-6 flex h-14 w-14 items-center justify-center rounded-full bg-primary text-2xl text-primary-foreground shadow-lg"
      aria-label="新建笔记"
      @click="openCreate"
    >
      +
    </button>
  </main>
</template>