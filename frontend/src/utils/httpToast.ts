import { setHttpErrorHandler, type HttpError } from '@/api/http'

const TOAST_ID = 'inknote-http-toast'

function ensureToastEl(): HTMLDivElement {
  let el = document.getElementById(TOAST_ID) as HTMLDivElement | null
  if (el) return el

  el = document.createElement('div')
  el.id = TOAST_ID
  el.style.cssText = [
    'position:fixed',
    'left:50%',
    'top:24px',
    'transform:translateX(-50%)',
    'z-index:9999',
    'max-width:min(90vw,420px)',
    'padding:10px 14px',
    'border-radius:8px',
    'background:#2b2b2b',
    'color:#fff',
    'font-size:14px',
    'line-height:1.4',
    'box-shadow:0 8px 24px rgba(0,0,0,.18)',
    'opacity:0',
    'pointer-events:none',
    'transition:opacity .2s ease',
  ].join(';')
  document.body.appendChild(el)
  return el
}

let hideTimer = 0

export function showHttpToast(message: string, duration = 2800) {
  const el = ensureToastEl()
  el.textContent = message
  el.style.opacity = '1'
  window.clearTimeout(hideTimer)
  hideTimer = window.setTimeout(() => {
    el.style.opacity = '0'
  }, duration)
}

export function setupHttpErrorToast() {
  setHttpErrorHandler((error: HttpError) => {
    showHttpToast(error.message || '请求失败')
  })
}
