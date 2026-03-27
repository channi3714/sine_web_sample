// 경로: frontend/src/App.tsx

import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import PrivateRoute from './components/PrivateRoute'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ProfilePage from './pages/ProfilePage'

/**
 * 앱 라우팅 구조
 *
 * BrowserRouter : URL 기반 라우팅 활성화 (/login, /register, /profile)
 * AuthProvider  : 전체 앱을 감싸 인증 상태를 어디서든 사용 가능하게 함
 * Routes        : 현재 URL과 매칭되는 Route 하나만 렌더링
 * PrivateRoute  : 로그인 안 했으면 /login으로 강제 이동
 */
export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* 루트 경로 → 로그인 페이지로 리다이렉트 */}
          <Route path="/" element={<Navigate to="/login" replace />} />

          {/* 인증 불필요 페이지 */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* 인증 필요 페이지 - PrivateRoute로 보호 */}
          <Route
            path="/profile"
            element={
              <PrivateRoute>
                <ProfilePage />
              </PrivateRoute>
            }
          />

          {/* 없는 경로 → 로그인 페이지로 */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
