// 경로: frontend/src/pages/RegisterPage.tsx

import { useState, type FormEvent } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import api from '../api/axios'

export default function RegisterPage() {
  const navigate = useNavigate()

  const [email, setEmail] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [passwordConfirm, setPasswordConfirm] = useState('')

  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')

    // 프론트엔드 비밀번호 확인 검증 (백엔드에도 @Size 검증 있음)
    if (password !== passwordConfirm) {
      setError('비밀번호가 일치하지 않습니다.')
      return
    }

    setIsLoading(true)

    try {
      /**
       * POST /api/auth/register 요청
       * 성공 응답 (201 Created):
       * {
       *   "id": 1,
       *   "email": "user@example.com",
       *   "username": "홍길동",
       *   "createdAt": "2025-01-01T00:00:00"
       * }
       */
      await api.post('/auth/register', { email, username, password })

      // 회원가입 성공 → 로그인 페이지로 이동
      // state로 성공 메시지 전달
      navigate('/login', { state: { message: '회원가입이 완료되었습니다. 로그인해주세요.' } })
    } catch (err: any) {
      setError(err.response?.data?.message ?? '회원가입 중 오류가 발생했습니다.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
      <div className="w-full max-w-md">

        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">SINE</h1>
          <p className="mt-2 text-gray-600">회원가입</p>
        </div>

        <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-8">
          <form onSubmit={handleSubmit} className="space-y-5">

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

            {/* 사용자명 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                사용자명
              </label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="2~20자 사이"
                required
                minLength={2}
                maxLength={20}
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
                placeholder="최소 8자 이상"
                required
                minLength={8}
                className="w-full px-4 py-2.5 border border-gray-300 rounded-lg text-sm
                           focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                           transition"
              />
            </div>

            {/* 비밀번호 확인 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                비밀번호 확인
              </label>
              <input
                type="password"
                value={passwordConfirm}
                onChange={(e) => setPasswordConfirm(e.target.value)}
                placeholder="비밀번호를 다시 입력하세요"
                required
                className={`w-full px-4 py-2.5 border rounded-lg text-sm
                            focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                            transition
                            ${passwordConfirm && password !== passwordConfirm
                              ? 'border-red-400 bg-red-50'
                              : 'border-gray-300'
                            }`}
              />
              {passwordConfirm && password !== passwordConfirm && (
                <p className="mt-1 text-xs text-red-500">비밀번호가 일치하지 않습니다.</p>
              )}
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full py-2.5 bg-blue-600 text-white font-medium rounded-lg text-sm
                         hover:bg-blue-700 disabled:opacity-60 disabled:cursor-not-allowed
                         transition"
            >
              {isLoading ? '가입 중...' : '회원가입'}
            </button>
          </form>

          <p className="mt-6 text-center text-sm text-gray-500">
            이미 계정이 있으신가요?{' '}
            <Link to="/login" className="text-blue-600 hover:underline font-medium">
              로그인
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}
