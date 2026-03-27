// 경로: frontend/src/pages/ProfilePage.tsx

import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import api from '../api/axios'

interface UserProfile {
  email: string
  username: string
  bio: string | null
  birthday: string | null   // "YYYY-MM-DD" 형식
  createdAt: string
  updatedAt: string | null
}

export default function ProfilePage() {
  const navigate = useNavigate()
  const { logout } = useAuth()

  const [profile, setProfile] = useState<UserProfile | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')

  const [isEditing, setIsEditing] = useState(false)
  const [editUsername, setEditUsername] = useState('')
  const [editBio, setEditBio] = useState('')
  const [editBirthday, setEditBirthday] = useState('')
  const [isSaving, setIsSaving] = useState(false)
  const [saveError, setSaveError] = useState('')

  useEffect(() => {
    api.get('/users/me')
      .then(res => setProfile(res.data))
      .catch(() => setError('프로필을 불러오는 데 실패했습니다.'))
      .finally(() => setIsLoading(false))
  }, [])

  const handleEditStart = () => {
    if (!profile) return
    setEditUsername(profile.username)
    setEditBio(profile.bio ?? '')
    setEditBirthday(profile.birthday ?? '')
    setSaveError('')
    setIsEditing(true)
  }

  const handleEditCancel = () => {
    setIsEditing(false)
    setSaveError('')
  }

  const handleSave = async () => {
    if (!editUsername.trim()) {
      setSaveError('사용자명은 필수입니다.')
      return
    }
    setIsSaving(true)
    setSaveError('')
    try {
      const response = await api.put('/users/me', {
        username: editUsername,
        bio: editBio || null,
        birthday: editBirthday || null,
      })
      setProfile(response.data)
      setIsEditing(false)
    } catch (err: any) {
      setSaveError(err.response?.data?.message ?? '저장 중 오류가 발생했습니다.')
    } finally {
      setIsSaving(false)
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  // 날짜 포맷 헬퍼
  const formatDateTime = (str: string | null) => {
    if (!str) return '-'
    return new Date(str).toLocaleDateString('ko-KR', {
      year: 'numeric', month: 'long', day: 'numeric',
      hour: '2-digit', minute: '2-digit',
    })
  }

  const formatDate = (str: string | null) => {
    if (!str) return '-'
    const [y, m, d] = str.split('-')
    return `${y}년 ${m}월 ${d}일`
  }

  // 나이 계산
  const calcAge = (birthday: string | null) => {
    if (!birthday) return null
    const today = new Date()
    const birth = new Date(birthday)
    let age = today.getFullYear() - birth.getFullYear()
    const hasHadBirthday =
      today.getMonth() > birth.getMonth() ||
      (today.getMonth() === birth.getMonth() && today.getDate() >= birth.getDate())
    if (!hasHadBirthday) age--
    return age
  }

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-gray-400 text-sm">불러오는 중...</div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-red-500 text-sm">{error}</div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 px-4 py-12">
      <div className="max-w-lg mx-auto space-y-4">

        {/* 헤더 */}
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold text-gray-900">내 프로필</h1>
          <button
            onClick={handleLogout}
            className="px-4 py-2 text-sm text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-100 transition"
          >
            로그아웃
          </button>
        </div>

        {profile && (
          <>
            {/* 아바타 카드 */}
            <div className="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
              <div className="bg-blue-600 px-8 py-8 text-center">
                <div className="w-20 h-20 bg-white rounded-full mx-auto flex items-center justify-center">
                  <span className="text-blue-600 text-3xl font-bold">
                    {profile.username.charAt(0).toUpperCase()}
                  </span>
                </div>
                <h2 className="mt-3 text-xl font-semibold text-white">{profile.username}</h2>
                <p className="text-blue-200 text-sm mt-1">{profile.email}</p>
                {profile.bio && (
                  <p className="text-blue-100 text-sm mt-2 max-w-xs mx-auto">{profile.bio}</p>
                )}
              </div>

              {/* 상세 정보 */}
              <div className="px-6 py-4 space-y-1">
                <InfoRow label="이메일" value={profile.email} />
                <InfoRow label="사용자명" value={profile.username} />
                <InfoRow
                  label="생년월일"
                  value={
                    profile.birthday
                      ? `${formatDate(profile.birthday)}${calcAge(profile.birthday) !== null ? ` (만 ${calcAge(profile.birthday)}세)` : ''}`
                      : '미입력'
                  }
                  muted={!profile.birthday}
                />
                <InfoRow label="자기소개" value={profile.bio ?? '미작성'} muted={!profile.bio} />
                <InfoRow label="가입일" value={formatDateTime(profile.createdAt)} />
                <InfoRow label="최근 수정" value={formatDateTime(profile.updatedAt)} />
              </div>

              {!isEditing && (
                <div className="px-6 pb-6">
                  <button
                    onClick={handleEditStart}
                    className="w-full py-2.5 border border-blue-600 text-blue-600 font-medium rounded-lg text-sm hover:bg-blue-50 transition"
                  >
                    프로필 수정
                  </button>
                </div>
              )}
            </div>

            {/* 편집 폼 */}
            {isEditing && (
              <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 space-y-4">
                <h3 className="font-semibold text-gray-900">프로필 수정</h3>

                {saveError && (
                  <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg px-4 py-3">
                    {saveError}
                  </div>
                )}

                {/* 사용자명 */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    사용자명 <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    value={editUsername}
                    onChange={(e) => setEditUsername(e.target.value)}
                    maxLength={20}
                    className="w-full px-4 py-2.5 border border-gray-300 rounded-lg text-sm
                               focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                  />
                  <p className="mt-1 text-xs text-gray-400 text-right">{editUsername.length}/20</p>
                </div>

                {/* 생년월일 */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    생년월일
                  </label>
                  <input
                    type="date"
                    value={editBirthday}
                    onChange={(e) => setEditBirthday(e.target.value)}
                    max={new Date().toISOString().split('T')[0]}  // 오늘 이후 날짜 선택 불가
                    className="w-full px-4 py-2.5 border border-gray-300 rounded-lg text-sm
                               focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                  />
                  {editBirthday && (
                    <p className="mt-1 text-xs text-gray-400">
                      만 {calcAge(editBirthday)}세
                    </p>
                  )}
                </div>

                {/* 자기소개 */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    자기소개
                  </label>
                  <textarea
                    value={editBio}
                    onChange={(e) => setEditBio(e.target.value)}
                    placeholder="자신을 소개해보세요 (선택)"
                    maxLength={200}
                    rows={3}
                    className="w-full px-4 py-2.5 border border-gray-300 rounded-lg text-sm resize-none
                               focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                  />
                  <p className="mt-1 text-xs text-gray-400 text-right">{editBio.length}/200</p>
                </div>

                <div className="flex gap-3 pt-1">
                  <button
                    onClick={handleEditCancel}
                    className="flex-1 py-2.5 border border-gray-300 text-gray-700 font-medium rounded-lg text-sm hover:bg-gray-50 transition"
                  >
                    취소
                  </button>
                  <button
                    onClick={handleSave}
                    disabled={isSaving}
                    className="flex-1 py-2.5 bg-blue-600 text-white font-medium rounded-lg text-sm hover:bg-blue-700 disabled:opacity-60 transition"
                  >
                    {isSaving ? '저장 중...' : '저장'}
                  </button>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}

function InfoRow({ label, value, muted = false }: { label: string; value: string; muted?: boolean }) {
  return (
    <div className="flex items-start justify-between py-3 border-b border-gray-100 last:border-0 gap-4">
      <span className="text-sm text-gray-500 shrink-0">{label}</span>
      <span className={`text-sm text-right ${muted ? 'text-gray-400' : 'font-medium text-gray-900'}`}>
        {value}
      </span>
    </div>
  )
}
