import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(), // Tailwind CSS v4 Vite 플러그인
  ],
  server: {
    // 개발 서버에서 /api 요청을 백엔드로 프록시
    // 덕분에 CORS 에러 없이 백엔드와 통신 가능 (개발 환경 한정)
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
