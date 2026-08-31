<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { showHttpToast } from '@/utils/httpToast'

const WIDTH = 78
const HEIGHT = 92
const EDGE_GAP = -8
const STORAGE_KEY = 'inknote-ai-kitty-pos'
const DRAG_THRESHOLD = 6

const greeting = ref(false)
const dragging = ref(false)
const snapping = ref(false)
const pos = ref({ x: 0, y: 0 })

let pointerId: number | null = null
let startClient = { x: 0, y: 0 }
let startPos = { x: 0, y: 0 }
let moved = false

const style = computed(() => ({
  left: `${pos.value.x}px`,
  top: `${pos.value.y}px`,
  width: `${WIDTH}px`,
  height: `${HEIGHT}px`,
}))

function clamp(n: number, min: number, max: number) {
  return Math.min(max, Math.max(min, n))
}

function maxX() {
  return Math.max(EDGE_GAP, window.innerWidth - WIDTH - EDGE_GAP)
}

function maxY() {
  return Math.max(8, window.innerHeight - HEIGHT - 8)
}

function defaultPos() {
  return {
    x: window.innerWidth - WIDTH - EDGE_GAP,
    y: window.innerHeight - HEIGHT - 108,
  }
}

function loadPos() {
  try {
    const raw = sessionStorage.getItem(STORAGE_KEY)
    if (!raw) return defaultPos()
    const parsed = JSON.parse(raw) as { x?: number; y?: number }
    if (typeof parsed.x !== 'number' || typeof parsed.y !== 'number') return defaultPos()
    return {
      x: clamp(parsed.x, EDGE_GAP, maxX()),
      y: clamp(parsed.y, 8, maxY()),
    }
  } catch {
    return defaultPos()
  }
}

function savePos() {
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(pos.value))
}

function snapToEdge() {
  const mid = pos.value.x + WIDTH / 2
  const targetX = mid < window.innerWidth / 2 ? EDGE_GAP : maxX()
  snapping.value = true
  pos.value = {
    x: targetX,
    y: clamp(pos.value.y, 8, maxY()),
  }
  savePos()
  window.setTimeout(() => {
    snapping.value = false
  }, 220)
}

function onPointerDown(event: PointerEvent) {
  if (event.button !== 0 && event.pointerType === 'mouse') return
  greeting.value = false
  pointerId = event.pointerId
  moved = false
  dragging.value = true
  startClient = { x: event.clientX, y: event.clientY }
  startPos = { ...pos.value }
  ;(event.currentTarget as HTMLElement).setPointerCapture(event.pointerId)
}

function onPointerMove(event: PointerEvent) {
  if (!dragging.value || event.pointerId !== pointerId) return
  const dx = event.clientX - startClient.x
  const dy = event.clientY - startClient.y
  if (!moved && Math.hypot(dx, dy) >= DRAG_THRESHOLD) moved = true
  if (!moved) return
  pos.value = {
    x: clamp(startPos.x + dx, EDGE_GAP, maxX()),
    y: clamp(startPos.y + dy, 8, maxY()),
  }
}

function onPointerUp(event: PointerEvent) {
  if (event.pointerId !== pointerId) return
  const wasDrag = moved
  dragging.value = false
  pointerId = null
  try {
    ;(event.currentTarget as HTMLElement).releasePointerCapture(event.pointerId)
  } catch {
    /* already released */
  }
  if (wasDrag) {
    snapToEdge()
    return
  }
  showHttpToast('Kitty AI 助手即将上线，敬请期待')
}

function onResize() {
  pos.value = {
    x: clamp(pos.value.x, EDGE_GAP, maxX()),
    y: clamp(pos.value.y, 8, maxY()),
  }
}

onMounted(() => {
  pos.value = loadPos()
  requestAnimationFrame(() => {
    greeting.value = true
  })
  window.addEventListener('resize', onResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
})
</script>

<template>
  <button
    type="button"
    class="ai-kitty"
    :class="{
      'ai-kitty--greet': greeting,
      'ai-kitty--dragging': dragging,
      'ai-kitty--snapping': snapping,
    }"
    :style="style"
    aria-label="AI 助手 Kitty"
    title="AI 助手（可拖动）"
    @pointerdown="onPointerDown"
    @pointermove="onPointerMove"
    @pointerup="onPointerUp"
    @pointercancel="onPointerUp"
    @animationend.self="greeting = false"
  >
    <span class="ai-kitty__shadow" aria-hidden="true" />
    <svg
      class="ai-kitty__svg"
      viewBox="0 0 96 110"
      width="72"
      height="82"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
    >
      <path
        d="M18 42 L28 8 L44 36 Z"
        fill="#fff7f2"
        stroke="#2a2a2a"
        stroke-width="2.2"
        stroke-linejoin="round"
      />
      <path
        d="M52 36 L68 8 L78 42 Z"
        fill="#fff7f2"
        stroke="#2a2a2a"
        stroke-width="2.2"
        stroke-linejoin="round"
      />
      <path d="M24 36 L30 16 L40 34 Z" fill="#ffb7c5" />
      <path d="M56 34 L66 16 L72 36 Z" fill="#ffb7c5" />

      <ellipse
        cx="48"
        cy="58"
        rx="34"
        ry="32"
        fill="#fff7f2"
        stroke="#2a2a2a"
        stroke-width="2.2"
      />

      <ellipse cx="28" cy="66" rx="7" ry="4.5" fill="#ffc0cb" opacity="0.85" />
      <ellipse cx="68" cy="66" rx="7" ry="4.5" fill="#ffc0cb" opacity="0.85" />

      <g class="ai-kitty__eyes">
        <ellipse cx="36" cy="56" rx="4.2" ry="5.2" fill="#2a2a2a" />
        <ellipse cx="60" cy="56" rx="4.2" ry="5.2" fill="#2a2a2a" />
        <circle cx="37.4" cy="54.4" r="1.3" fill="#fff" />
        <circle cx="61.4" cy="54.4" r="1.3" fill="#fff" />
      </g>

      <ellipse cx="48" cy="66" rx="3.2" ry="2.4" fill="#ff8fab" />
      <path
        d="M48 68.5 Q42 74 37 71.5"
        fill="none"
        stroke="#2a2a2a"
        stroke-width="1.6"
        stroke-linecap="round"
      />
      <path
        d="M48 68.5 Q54 74 59 71.5"
        fill="none"
        stroke="#2a2a2a"
        stroke-width="1.6"
        stroke-linecap="round"
      />

      <path d="M12 60 H26" stroke="#2a2a2a" stroke-width="1.3" stroke-linecap="round" />
      <path d="M11 66 H26" stroke="#2a2a2a" stroke-width="1.3" stroke-linecap="round" />
      <path d="M70 60 H84" stroke="#2a2a2a" stroke-width="1.3" stroke-linecap="round" />
      <path d="M70 66 H85" stroke="#2a2a2a" stroke-width="1.3" stroke-linecap="round" />

      <g class="ai-kitty__bow">
        <ellipse cx="66" cy="40" rx="8" ry="6" fill="#ff5a7a" stroke="#2a2a2a" stroke-width="1.5" />
        <ellipse cx="78" cy="40" rx="8" ry="6" fill="#ff5a7a" stroke="#2a2a2a" stroke-width="1.5" />
        <circle cx="72" cy="40" r="3.2" fill="#ff2e55" stroke="#2a2a2a" stroke-width="1.4" />
      </g>

      <ellipse
        cx="34"
        cy="98"
        rx="10"
        ry="7"
        fill="#fff7f2"
        stroke="#2a2a2a"
        stroke-width="2"
      />
      <circle cx="30" cy="96" r="1.4" fill="#ff8fab" />
      <circle cx="34.5" cy="94.5" r="1.4" fill="#ff8fab" />
      <circle cx="39" cy="96" r="1.4" fill="#ff8fab" />
    </svg>
    <span class="ai-kitty__badge">AI</span>
  </button>
</template>

<style scoped>
.ai-kitty {
  position: fixed;
  z-index: 40;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding: 0;
  border: 0;
  background: transparent;
  cursor: grab;
  touch-action: none;
  user-select: none;
  transform-origin: center center;
  filter: drop-shadow(0 6px 10px rgba(40, 20, 30, 0.18));
  -webkit-tap-highlight-color: transparent;
}

.ai-kitty--snapping {
  transition: left 0.22s ease, top 0.22s ease;
}

.ai-kitty--dragging {
  cursor: grabbing;
  transition: none;
  filter: drop-shadow(0 10px 16px rgba(40, 20, 30, 0.28));
  scale: 1.04;
}

.ai-kitty:focus-visible {
  outline: 2px solid #111;
  outline-offset: 2px;
}

.ai-kitty__svg {
  display: block;
  overflow: visible;
  pointer-events: none;
}

.ai-kitty__shadow {
  position: absolute;
  left: 18px;
  bottom: 2px;
  width: 42px;
  height: 10px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.12);
  filter: blur(2px);
  pointer-events: none;
}

.ai-kitty__badge {
  position: absolute;
  top: 6px;
  left: 4px;
  min-width: 22px;
  height: 18px;
  padding: 0 5px;
  border-radius: 999px;
  background: #111;
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  line-height: 18px;
  letter-spacing: 0.02em;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  pointer-events: none;
}

.ai-kitty--greet {
  animation: kitty-greet 1.15s cubic-bezier(0.34, 1.45, 0.64, 1) both;
}

.ai-kitty--greet .ai-kitty__bow {
  animation: kitty-bow 1.15s ease both;
}

.ai-kitty--greet .ai-kitty__eyes {
  animation: kitty-blink 1.15s ease both;
}

.ai-kitty__eyes {
  transform-origin: 48px 56px;
  animation: kitty-idle-blink 4.8s ease-in-out infinite;
}

@keyframes kitty-greet {
  0% {
    transform: translateX(28px) rotate(8deg) scale(0.92);
    opacity: 0.2;
  }
  35% {
    transform: translateX(-12px) rotate(-6deg) scale(1.06);
    opacity: 1;
  }
  55% {
    transform: translateX(2px) rotate(4deg) scale(1);
  }
  75% {
    transform: translateX(-6px) rotate(-2deg);
  }
  100% {
    transform: translateX(0) rotate(0);
  }
}

@keyframes kitty-bow {
  0%,
  40% {
    transform: rotate(0deg);
  }
  55% {
    transform: rotate(-18deg);
  }
  70% {
    transform: rotate(12deg);
  }
  100% {
    transform: rotate(0deg);
  }
}

@keyframes kitty-blink {
  0%,
  48%,
  62%,
  100% {
    transform: scaleY(1);
  }
  55% {
    transform: scaleY(0.15);
  }
}

@keyframes kitty-idle-blink {
  0%,
  42%,
  50%,
  100% {
    transform: scaleY(1);
  }
  46% {
    transform: scaleY(0.12);
  }
}

@media (prefers-reduced-motion: reduce) {
  .ai-kitty--greet,
  .ai-kitty--greet .ai-kitty__bow,
  .ai-kitty--greet .ai-kitty__eyes,
  .ai-kitty__eyes,
  .ai-kitty--snapping {
    animation: none;
    transition: none;
  }
}
</style>