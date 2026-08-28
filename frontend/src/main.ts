import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { setupHttpErrorToast } from '@/utils/httpToast'
import './style.css'

setupHttpErrorToast()

createApp(App).use(createPinia()).use(router).mount('#app')
