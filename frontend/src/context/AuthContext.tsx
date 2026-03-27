// 경로: frontend/src/context/AuthContext.tsx

import { createContext, useContext, useState, type ReactNode } from 'react'

/**
 * 인증 상태를 전역으로 관리하는 Context
 *
 * 왜 Context가 필요한가요?
 * - 로그인 여부를 LoginPage, ProfilePage, Header 등 여러 컴포넌트가 알아야 함
 * - props로 전달하면 컴포넌트가 깊어질수록 "props drilling" 문제 발생
 * - Context를 쓰면 어느 컴포넌트에서든 바로 꺼내 쓸 수 있음
 *
 * 로그인 상태 유지 방법:
 * - 토큰을 localStorage에 저장 → 브라우저를 닫았다 열어도 유지
 * - 앱 시작 시 localStorage에서 토큰을 꺼내 상태 초기화
 */

interface User {
  email: string
  username: string
}

interface AuthContextType {
  user: User | null         // 현재 로그인한 사용자 (null = 비로그인)
  token: string | null      // JWT 토큰
  login: (token: string, user: User) => void
  logout: () => void
  isAuthenticated: boolean  // 로그인 여부 (편의용)
}

// Context 생성 (초기값은 undefined, 아래 Provider에서 실제값 제공)
const AuthContext = createContext<AuthContextType | undefined>(undefined)

/**
 * AuthProvider: 앱 전체를 감싸는 인증 상태 공급자
 * App.tsx에서 <AuthProvider>로 전체를 감싸면 하위 모든 컴포넌트에서 사용 가능
 */
export function AuthProvider({ children }: { children: ReactNode }) {
  // localStorage에서 초기값 로드 (새로고침해도 로그인 유지)
  const [token, setToken] = useState<string | null>(
    () => localStorage.getItem('token')
  )
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('user')
    return saved ? JSON.parse(saved) : null
  })

  /**
   * 로그인 처리
   * 1. 상태(state) 업데이트 → 컴포넌트 리렌더링
   * 2. localStorage 저장 → 새로고침 후에도 유지
   */
  const login = (newToken: string, newUser: User) => {
    setToken(newToken)
    setUser(newUser)
    localStorage.setItem('token', newToken)
    localStorage.setItem('user', JSON.stringify(newUser))
  }

  /**
   * 로그아웃 처리
   * 상태와 localStorage 모두 초기화
   */
  const logout = () => {
    setToken(null)
    setUser(null)
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const value: AuthContextType = {
    user,
    token,
    login,
    logout,
    isAuthenticated: !!token,  // token이 있으면 true
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

/**
 * useAuth: AuthContext를 쉽게 꺼내 쓰는 커스텀 훅
 * 사용법: const { user, login, logout, isAuthenticated } = useAuth()
 */
export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth는 AuthProvider 안에서만 사용할 수 있습니다.')
  }
  return context
}
