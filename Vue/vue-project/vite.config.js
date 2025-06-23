import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    host: '192.168.205.81',
    // 또는 실제 서버가 192.168.205.86에서 실행 중이고, 해당 IP에만 바인딩하려면 아래처럼 사용
    // host: '192.168.205.86'
  },
})
