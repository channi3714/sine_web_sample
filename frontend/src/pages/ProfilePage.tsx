// 경로: frontend/src/pages/ProfilePage.tsx

import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import api from '../api/axios'

interface UserProfile {
  id: number
  email: string
  username: string
  createdAt: string
}

export default function ProfilePage() {
  const navigate = useNavigate()
  const { logout } = useAuth()

  const [profile, setProfile] = useState<UserProfile | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')

  /**
   * 컴포넌트 마운트 시 프로필 정보 API 호출
   *
   * useEffect: 컴포넌트가 화면에 처음 그려진 후 실행
   * 빈 배열([])을 두 번째 인자로 주면 딱 한 번만 실행
   */
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        /**
         * GET /api/users/me 요청
         * Axios 인터셉터가 자동으로 Authorization: Bearer <token> 헤더 추가
         * → 백엔드에서 JWT 검증 후 사용자 정보 반환
         */
        const response = await api.get('/users/me')
        setProfile(response.data)
      } catch (err: any) {
        setError('프로필을 불러오는 데 실패했습니다.')
      } finally {
        setIsLoading(false)
      }
    }

    fetchProfile()
  }, [])

  const handleLogout = () => {
    logout()            // 상태 + localStorage 초기화
    navigate('/login')  // 로그인 페이지로 이동
  }

  // 날짜 포맷 헬퍼
  const formatDate = (dateStr: string) => {
    return new Date(dateStr).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    })
  }

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-gray-500">불러오는 중...</div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 px-4 py-12">
      <div className="max-w-lg mx-auto">

        {/* 헤더 */}
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-2xl font-bold text-gray-900">내 프로필</h1>
          <button
            onClick={handleLogout}
            className="px-4 py-2 text-sm text-gray-600 border border-gray-300 rounded-lg
                       hover:bg-gray-100 transition"
          >
            로그아웃
          </button>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg px-4 py-3 mb-6">
            {error}
          </div>
        )}

        {profile && (
          <div className="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">

            {/* 아바타 영역 */}
            <div className="bg-blue-600 px-8 py-10 text-center">
              <div className="w-20 h-20 bg-white rounded-full mx-auto flex items-center justify-center">
                <span className="text-blue-600 text-3xl font-bold">
                  {profile.username.charAt(0).toUpperCase()}
                </span>
              </div>
              <h2 className="mt-4 text-xl font-semibold text-white">{profile.username}</h2>
              <p className="text-blue-200 text-sm mt-1">{profile.email}</p>
            </div>

            {/* 상세 정보 */}
            <div className="px-8 py-6 space-y-4">
              <InfoRow label="사용자 ID" value={`#${profile.id}`} />
              <InfoRow label="이메일" value={profile.email} />
              <InfoRow label="사용자명" value={profile.username} />
              <InfoRow label="가입일" value={formatDate(profile.createdAt)} />
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

// 정보 행 컴포넌트
function InfoRow({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex items-center justify-between py-3 border-b border-gray-100 last:border-0">
      <span className="text-sm text-gray-500">{label}</span>
      <span className="text-sm font-medium text-gray-900">{value}</span>
    </div>
  )
}
