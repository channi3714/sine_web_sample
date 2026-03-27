// 경로: frontend/src/components/PrivateRoute.tsx

import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

/**
 * PrivateRoute: 로그인한 사용자만 접근 가능한 페이지 보호
 *
 * 사용법: App.tsx에서 보호할 페이지를 <PrivateRoute>로 감싸기
 *   <Route path="/profile" element={<PrivateRoute><ProfilePage /></PrivateRoute>} />
 *
 * 동작:
 * - 로그인 O → 자식 컴포넌트(ProfilePage 등) 그대로 렌더링
 * - 로그인 X → /login 으로 자동 리다이렉트
 */
export default function PrivateRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuth()

  if (!isAuthenticated) {
    // replace: 브라우저 히스토리에 /profile이 남지 않음
    // (뒤로 가기 눌렀을 때 다시 /profile로 돌아오는 것 방지)
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
}
