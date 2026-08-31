<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref, type Component } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import {
  Bold,
  Code,
  Heading1,
  Heading2,
  Heading3,
  Image as ImageIcon,
  Italic,
  Link2,
  List,
  ListOrdered,
  ListTodo,
  Minus,
  Quote,
  SquareCode,
  Strikethrough,
} from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { fetchNoteDetail, saveNote, uploadNoteImage } from '@/api/note'

const route = useRoute()
const router = useRouter()
const noteId = ref<string | undefined>(
  typeof route.query.noteId === 'string' ? route.query.noteId : undefined,
)
const title = ref('')
const contentMd = ref('')
const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const updatedAt = ref<number | null>(null)
const editorHost = ref<HTMLDivElement | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)
let vditor: Vditor | null = null

type ToolbarItem =
  | { type: 'sep'; key: string }
  | {
      type: 'btn'
      key: string
      label: string
      icon: Component
      disabled?: () => boolean
      run: () => void
    }

function formatSaveTime(ms: number) {
  const d = new Date(ms)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hh = String(d.getHours()).padStart(2, '0')
  const mi = String(d.getMinutes()).padStart(2, '0')
  const ss = String(d.getSeconds()).padStart(2, '0')
  return `${y}-${m}-${day} ${hh}:${mi}:${ss}`
}

function destroyEditor() {
  if (vditor) {
    vditor.destroy()
    vditor = null
  }
}

async function uploadFiles(files: File[]): Promise<string> {
  const file = files[0]
  if (!file) return ''
  const result = await uploadNoteImage(file)
  const alt = file.name.replace(/\.[^.]+$/, '') || '图片'
  vditor?.insertValue(`\n![${alt}](${result.url})\n`)
  return ''
}

function wrapSelection(before: string, after = before, placeholder = '文本') {
  if (!vditor) return
  vditor.focus()
  const selected = vditor.getSelection()
  const text = selected && selected.length > 0 ? selected : placeholder
  vditor.insertValue(`${before}${text}${after}`)
}

function insertHeading(level: 1 | 2 | 3) {
  if (!vditor) return
  vditor.focus()
  const prefix = `${'#'.repeat(level)} `
  const selected = vditor.getSelection()
  const text = selected && selected.length > 0 ? selected.replace(/^#{1,6}\s+/, '') : '标题'
  vditor.insertValue(`\n${prefix}${text}\n`)
}

function insertLinePrefix(prefix: string, placeholder = '列表项') {
  if (!vditor) return
  vditor.focus()
  const selected = vditor.getSelection()
  if (selected && selected.length > 0) {
    const lines = selected.split(/\r?\n/).map((line) => {
      const bare = line.replace(/^(\s*)([-*+] |\d+\. |>\s?|#{1,6}\s+|-\s\[[ xX]\]\s+)/, '$1')
      return `${prefix}${bare || placeholder}`
    })
    vditor.insertValue(lines.join('\n'))
    return
  }
  vditor.insertValue(`\n${prefix}${placeholder}\n`)
}

function insertCodeBlock() {
  if (!vditor) return
  vditor.focus()
  const selected = vditor.getSelection()
  const text = selected && selected.length > 0 ? selected : 'code'
  vditor.insertValue(`\n\`\`\`\n${text}\n\`\`\`\n`)
}

function insertLink() {
  if (!vditor) return
  vditor.focus()
  const selected = vditor.getSelection()
  const text = selected && selected.length > 0 ? selected : '链接文字'
  vditor.insertValue(`[${text}](https://)`)
}

function insertDivider() {
  if (!vditor) return
  vditor.focus()
  vditor.insertValue('\n\n---\n\n')
}

function onPickImage() {
  fileInputRef.value?.click()
}

async function onImageSelected(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  input.value = ''
  if (!file) return
  uploading.value = true
  try {
    await uploadFiles([file])
  } finally {
    uploading.value = false
  }
}

const toolbarItems: ToolbarItem[] = [
  { type: 'btn', key: 'h1', label: '一级标题', icon: Heading1, run: () => insertHeading(1) },
  { type: 'btn', key: 'h2', label: '二级标题', icon: Heading2, run: () => insertHeading(2) },
  { type: 'btn', key: 'h3', label: '三级标题', icon: Heading3, run: () => insertHeading(3) },
  { type: 'sep', key: 'sep-1' },
  { type: 'btn', key: 'bold', label: '加粗', icon: Bold, run: () => wrapSelection('**', '**', '加粗文本') },
  { type: 'btn', key: 'italic', label: '斜体', icon: Italic, run: () => wrapSelection('*', '*', '斜体文本') },
  { type: 'btn', key: 'strike', label: '删除线', icon: Strikethrough, run: () => wrapSelection('~~', '~~', '删除文本') },
  { type: 'sep', key: 'sep-2' },
  { type: 'btn', key: 'code', label: '行内代码', icon: Code, run: () => wrapSelection('`', '`', 'code') },
  { type: 'btn', key: 'codeblock', label: '代码块', icon: SquareCode, run: () => insertCodeBlock() },
  { type: 'btn', key: 'quote', label: '引用', icon: Quote, run: () => insertLinePrefix('> ', '引用内容') },
  { type: 'sep', key: 'sep-3' },
  { type: 'btn', key: 'ul', label: '无序列表', icon: List, run: () => insertLinePrefix('- ', '列表项') },
  { type: 'btn', key: 'ol', label: '有序列表', icon: ListOrdered, run: () => insertLinePrefix('1. ', '列表项') },
  { type: 'btn', key: 'todo', label: '待办', icon: ListTodo, run: () => insertLinePrefix('- [ ] ', '待办事项') },
  { type: 'sep', key: 'sep-4' },
  { type: 'btn', key: 'link', label: '链接', icon: Link2, run: () => insertLink() },
  {
    type: 'btn',
    key: 'image',
    label: '插入图片',
    icon: ImageIcon,
    disabled: () => uploading.value,
    run: () => onPickImage(),
  },
  { type: 'btn', key: 'hr', label: '分割线', icon: Minus, run: () => insertDivider() },
]

async function initEditor(value: string) {
  await nextTick()
  if (!editorHost.value) return
  destroyEditor()
  editorHost.value.innerHTML = ''
  vditor = new Vditor(editorHost.value, {
    value,
    mode: 'ir',
    lang: 'zh_CN',
    cdn: '/vditor',
    cache: { enable: false },
    height: Math.max(480, window.innerHeight - 200),
    placeholder: '开始写作… 点击某一行才会显示 Markdown 符号',
    toolbar: [],
    upload: {
      accept: 'image/png, image/jpeg, image/gif, image/webp',
      multiple: false,
      handler: uploadFiles,
    },
    preview: {
      actions: [],
    },
    input(val: string) {
      contentMd.value = val
    },
    after() {
      contentMd.value = vditor?.getValue() || value
    },
  })
}

async function loadDetail() {
  loading.value = true
  destroyEditor()
  try {
    if (!noteId.value) {
      title.value = '未命名笔记'
      contentMd.value = ''
      updatedAt.value = null
      await initEditor('')
      return
    }
    const detail = await fetchNoteDetail(noteId.value)
    title.value = detail.title
    contentMd.value = detail.contentMd
    updatedAt.value = detail.updatedAt
    await initEditor(detail.contentMd || '')
  } finally {
    loading.value = false
  }
}

async function onSave() {
  saving.value = true
  try {
    const markdown = vditor?.getValue() ?? contentMd.value
    contentMd.value = markdown
    const saved = await saveNote({
      noteId: noteId.value,
      title: title.value.trim() || '未命名笔记',
      contentMd: markdown,
    })
    noteId.value = saved.noteId
    updatedAt.value = saved.updatedAt
    await router.replace({ name: 'note-edit', query: { noteId: saved.noteId } })
  } finally {
    saving.value = false
  }
}

function goBack() {
  router.push({ name: 'notes' })
}

onMounted(loadDetail)
onBeforeUnmount(destroyEditor)
</script>

<template>
  <main class="mx-auto flex min-h-screen max-w-2xl flex-col bg-background">
    <div class="sticky top-0 z-30 border-b border-border bg-background/95 backdrop-blur">
      <header class="flex items-center justify-between gap-2 px-4 py-3">
        <Button variant="ghost" size="sm" type="button" @click="goBack">返回</Button>
        <div class="min-w-0 flex-1 text-center text-xs text-muted-foreground">
          <span v-if="updatedAt">已保存 {{ formatSaveTime(updatedAt) }}</span>
          <span v-else>尚未保存</span>
        </div>
        <Button size="sm" type="button" :disabled="saving || loading" @click="onSave">
          {{ saving ? '保存中…' : '保存' }}
        </Button>
      </header>

      <div class="toolbar-scroll flex items-center gap-0.5 overflow-x-auto px-3 pb-2.5">
        <template v-for="item in toolbarItems" :key="item.key">
          <span
            v-if="item.type === 'sep'"
            class="mx-1 h-4 w-px shrink-0 bg-border"
            aria-hidden="true"
          />
          <Button
            v-else
            variant="ghost"
            size="icon-sm"
            type="button"
            class="shrink-0 text-muted-foreground hover:text-foreground"
            :title="item.label"
            :aria-label="item.label"
            :disabled="loading || item.disabled?.()"
            @click="item.run()"
          >
            <component :is="item.icon" class="size-4" />
          </Button>
        </template>
        <input
          ref="fileInputRef"
          type="file"
          accept="image/png,image/jpeg,image/gif,image/webp"
          class="hidden"
          @change="onImageSelected"
        />
      </div>
    </div>

    <div class="flex flex-1 flex-col px-4 pt-3 pb-6">
      <input
        v-model="title"
        placeholder="标题"
        class="mb-2 h-11 w-full border-0 bg-transparent text-2xl font-semibold outline-none placeholder:text-muted-foreground"
      />

      <p v-if="loading" class="py-10 text-center text-muted-foreground">加载中…</p>
      <div
        v-show="!loading"
        ref="editorHost"
        class="inknote-vditor min-h-[480px] flex-1 overflow-hidden"
      />
    </div>
  </main>
</template>

<style scoped>
.toolbar-scroll {
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
}
.toolbar-scroll::-webkit-scrollbar {
  display: none;
}
.inknote-vditor,
.inknote-vditor.vditor {
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
  background: transparent !important;
}
.inknote-vditor :deep(.vditor-content),
.inknote-vditor :deep(.vditor-ir),
.inknote-vditor :deep(.vditor-sv),
.inknote-vditor :deep(.vditor-wysiwyg),
.inknote-vditor :deep(.vditor-preview),
.inknote-vditor :deep(.vditor-ir pre.vditor-reset) {
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
  background: transparent !important;
}
.inknote-vditor :deep(.vditor-ir),
.inknote-vditor :deep(.vditor-wysiwyg),
.inknote-vditor :deep(.vditor-sv) {
  padding-left: 0 !important;
  padding-right: 0 !important;
  padding-top: 0 !important;
}
.inknote-vditor :deep(.vditor-toolbar) {
  display: none !important;
}
.inknote-vditor :deep(.vditor-ir pre.vditor-reset) {
  font-size: 15px;
  line-height: 1.75;
  padding: 0 0 64px !important;
  min-height: 420px;
}
</style>