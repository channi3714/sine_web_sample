// 경로: frontend/src/pages/LoginPage.tsx

import { useState, type FormEvent } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import api from '../api/axios'

export default function LoginPage() {
  const navigate = useNavigate()
  const { login } = useAuth()

  // 폼 입력값 상태
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')

  // UI 상태
  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const handleSubmit = async (e: FormEvent) => {
    // 폼 기본 동작(페이지 새로고침) 방지
    e.preventDefault()
    setError('')
    setIsLoading(true)

    try {
      /**
       * POST /api/auth/login 요청
       * 성공 응답 예시:
       * {
       *   "token": "eyJhbGci...",
       *   "tokenType": "Bearer",
       *   "email": "user@example.com",
       *   "username": "홍길동"
       * }
       */
      const response = await api.post('/auth/login', { email, password })
      const { token, email: userEmail, username } = response.data

      // AuthContext의 login 호출 → 상태 + localStorage 동시 저장
      login(token, { email: userEmail, username })

      // 로그인 성공 → 프로필 페이지로 이동
      navigate('/profile')
    } catch (err: any) {
      // 서버에서 보낸 에러 메시지 표시, 없으면 기본 메시지
      setError(err.response?.data?.message ?? '로그인 중 오류가 발생했습니다.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
      <div className="w-full max-w-md">

        {/* 헤더 */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">SINE</h1>
          <p className="mt-2 text-gray-600">로그인</p>
        </div>

        {/* 카드 */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-8">
          <form onSubmit={handleSubmit} className="space-y-5">

            {/* 에러 메시지 */}
            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg px-4 py-3">
                {error}
              </div>
            )}

            {/* 이메일 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                이메일
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="example@email.com"
                required
                className="w-full px-4 py-2.5 border border-gray-300 rounded-lg text-sm
                           focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                           transition"
              />
            </div>

            {/* 비밀번호 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                비밀번호
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="비밀번호를 입력하세요"
                required
                className="w-full px-4 py-2.5 border border-gray-300 rounded-lg text-sm
                           focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                           transition"
              />
            </div>

            {/* 로그인 버튼 */}
            <button
              type="submit"
              disabled={isLoading}
              className="w-full py-2.5 bg-blue-600 text-white font-medium rounded-lg text-sm
                         hover:bg-blue-700 disabled:opacity-60 disabled:cursor-not-allowed
                         transition"
            >
              {isLoading ? '로그인 중...' : '로그인'}
            </button>
          </form>

          {/* 회원가입 링크 */}
          <p className="mt-6 text-center text-sm text-gray-500">
            계정이 없으신가요?{' '}
            <Link to="/register" className="text-blue-600 hover:underline font-medium">
              회원가입
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}
