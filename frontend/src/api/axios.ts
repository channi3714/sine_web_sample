// 경로: frontend/src/api/axios.ts

import axios from 'axios'

/**
 * Axios 커스텀 인스턴스
 *
 * 왜 기본 axios를 직접 쓰지 않고 인스턴스를 만드나요?
 * - baseURL을 한 곳에서만 관리할 수 있음
 * - 모든 요청에 공통 헤더(JWT 토큰 등)를 자동으로 붙일 수 있음
 * - 나중에 URL이 바뀌어도 이 파일 하나만 수정하면 됨
 */
const api = axios.create({
  // vite.config.ts의 proxy 설정 덕분에
  // 개발 환경: /api → http://localhost:8080/api 로 자동 전달
  // 운영 환경: Nginx가 /api → 백엔드 컨테이너로 전달
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

/**
 * 요청 인터셉터 (Request Interceptor)
 *
 * 모든 API 요청이 나가기 직전에 실행됨
 * localStorage에 토큰이 있으면 Authorization 헤더에 자동으로 붙여줌
 *
 * 흐름: 내 코드에서 api.get('/users/me') 호출
 *       → 인터셉터가 헤더에 "Bearer eyJhb..." 추가
 *       → 실제 네트워크 요청 전송
 */
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

/**
 * 응답 인터셉터 (Response Interceptor)
 *
 * 모든 API 응답이 내 코드에 도달하기 전에 실행됨
 * 401(인증 만료) 응답이 오면 토큰 삭제 후 로그인 페이지로 리다이렉트
 */
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // 토큰 만료 또는 유효하지 않은 토큰 → 자동 로그아웃
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  },
)

export default api
