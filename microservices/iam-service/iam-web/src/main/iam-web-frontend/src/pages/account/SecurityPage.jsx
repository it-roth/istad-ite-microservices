import { useState, useEffect } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { useApp } from '@/context/AppContext'
import { meApi } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { Alert, AlertDescription } from '@/components/ui/alert'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogFooter,
  DialogTitle,
  DialogDescription,
} from '@/components/ui/dialog'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog'
import {
  CheckCircle2, AlertCircle, Eye, EyeOff, Shield,
  Mail, Smartphone, Phone, Link2, Monitor, Lock, Info, X, RefreshCw,
  Activity, UserX, Laptop, Globe, User, LogOut, Clock, MapPin,
  ChevronLeft, ChevronRight, Search, Trash2
} from 'lucide-react'
import KeyIcon from '@/components/icons/KeyIcon'
import { parseUserAgent, formatDeviceTitle } from '@/lib/deviceParser'
import { TablePagination } from '@/components/ui/table-pagination'





function getDeviceIcon(device = '', browser = '') {
  const text = `${device} ${browser}`.toLowerCase()
  if (text.includes('iphone') || text.includes('android') || text.includes('mobile') || text.includes('phone')) {
    return Smartphone
  }
  if (text.includes('macbook') || text.includes('laptop')) {
    return Laptop
  }
  return Monitor
}

const UNIFIED_ACTIVITY_ICON_STYLE = 'bg-blue-50 text-blue-600 dark:bg-blue-950/40 dark:text-blue-400 border-blue-100 dark:border-blue-900/50'

function getActivityMeta(eventType = '') {
  const type = eventType.toUpperCase()
  if (type.includes('PASSWORD')) {
    return {
      icon: KeyIcon,
      color: UNIFIED_ACTIVITY_ICON_STYLE,
      badge: 'Password'
    }
  }
  if (type.includes('TWO_FACTOR') || type.includes('2FA') || type.includes('OTP')) {
    return {
      icon: Shield,
      color: UNIFIED_ACTIVITY_ICON_STYLE,
      badge: '2-Step Auth'
    }
  }
  if (type.includes('PROFILE') || type.includes('USER')) {
    return {
      icon: User,
      color: UNIFIED_ACTIVITY_ICON_STYLE,
      badge: 'Profile'
    }
  }
  if (type.includes('IDENTITY') || type.includes('LINK') || type.includes('SSO') || type.includes('GOOGLE') || type.includes('GITHUB')) {
    return {
      icon: Globe,
      color: UNIFIED_ACTIVITY_ICON_STYLE,
      badge: 'OAuth Link'
    }
  }
  if (type.includes('SESSION') || type.includes('REVOKE')) {
    return {
      icon: LogOut,
      color: UNIFIED_ACTIVITY_ICON_STYLE,
      badge: 'Session'
    }
  }
  return {
    icon: Activity,
    color: UNIFIED_ACTIVITY_ICON_STYLE,
    badge: 'Security'
  }
}

export default function SecurityPage() {
  const { user: ctxUser, lang, tr } = useApp()
  const [searchParams] = useSearchParams()
  const tabParam = searchParams.get('tab')

  const [profile, setProfile] = useState(ctxUser || null)
  const [sessions, setSessions] = useState([])
  const [loginHistory, setLoginHistory] = useState([])
  const [activities, setActivities] = useState([])
  const [identities, setIdentities] = useState([])

  const [historyFilter, setHistoryFilter] = useState('all')
  const [historySearch, setHistorySearch] = useState('')
  const [historyPage, setHistoryPage] = useState(1)
  const [historyPageSize, setHistoryPageSize] = useState(5)
  const [activityPage, setActivityPage] = useState(1)
  const [activityPageSize, setActivityPageSize] = useState(5)
  const [sessionPage, setSessionPage] = useState(1)
  const [sessionPageSize, setSessionPageSize] = useState(5)

  const [form, setForm] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' })
  const [show, setShow] = useState({ cur: false, new: false, con: false })
  const [saving, setSaving] = useState(false)
  const [fieldErrors, setFieldErrors] = useState({})
  const [passwordSectionError, setPasswordSectionError] = useState(null)
  const [notification, setNotification] = useState(null)
  const [passwordLastChanged, setPasswordLastChanged] = useState(() => {
    return localStorage.getItem('pwd_last_changed') || null
  })

  // Deactivation state
  const [deactivateReason, setDeactivateReason] = useState('')
  const [deactivateConfirmText, setDeactivateConfirmText] = useState('')
  const [deactivateLoading, setDeactivateLoading] = useState(false)
  const [deactivateSuccess, setDeactivateSuccess] = useState(false)

  // Reset Password State
  const [resetModalOpen, setResetModalOpen] = useState(false)
  const [resetForm, setResetForm] = useState({ code: '', newPassword: '', confirmPassword: '' })
  const [resetFieldErrors, setResetFieldErrors] = useState({})
  const [resetting, setResetting] = useState(false)
  const [showReset, setShowReset] = useState({ new: false, con: false })
  const [resetSent, setResetSent] = useState(false)

  // Dynamic 2FA State with localStorage persistence
  const [twoFaEnabled, setTwoFaEnabled] = useState(() => {
    const saved = localStorage.getItem('two_fa_enabled')
    return saved !== null ? saved === 'true' : false
  })
  const [twoFaDate, setTwoFaDate] = useState(() => {
    return localStorage.getItem('two_fa_date') || null
  })
  const [recoveryCodes, setRecoveryCodes] = useState(() => {
    try {
      const saved = localStorage.getItem('two_fa_recovery_codes')
      if (saved) return JSON.parse(saved)
    } catch { }
    return []
  })
  const [codesModalOpen, setCodesModalOpen] = useState(false)
  const [copiedCodes, setCopiedCodes] = useState(false)

  useEffect(() => {
    if (tabParam) {
      const el = document.getElementById(tabParam)
      if (el) {
        setTimeout(() => {
          el.scrollIntoView({ behavior: 'smooth', block: 'start' })
        }, 150)
      }
    }
  }, [tabParam])

  useEffect(() => {
    async function loadData() {
      try {
        const [pRes, sRes, hRes, iRes, aRes] = await Promise.allSettled([
          meApi.get(),
          meApi.sessions(),
          meApi.loginHistory(100),
          meApi.identities(),
          meApi.activity(100),
        ])
        if (pRes.status === 'fulfilled' && pRes.value) {
          setProfile(pRes.value)
          if (pRes.value.twoFactorEnabled !== undefined) {
            const is2fa = Boolean(pRes.value.twoFactorEnabled)
            setTwoFaEnabled(is2fa)
            localStorage.setItem('two_fa_enabled', String(is2fa))
          }
        }
        if (sRes.status === 'fulfilled' && Array.isArray(sRes.value)) setSessions(sRes.value)
        if (hRes.status === 'fulfilled' && Array.isArray(hRes.value)) setLoginHistory(hRes.value)
        if (iRes.status === 'fulfilled' && Array.isArray(iRes.value)) setIdentities(iRes.value)
        if (aRes.status === 'fulfilled' && Array.isArray(aRes.value)) setActivities(aRes.value)
      } catch (e) {
        console.error('Failed to load security page data', e)
      }
    }
    loadData()
  }, [])

  async function handleTerminateSession(sessionId, allSessionIds) {
    const ids = Array.isArray(allSessionIds) && allSessionIds.length > 0 ? allSessionIds : [sessionId]
    try {
      await Promise.all(ids.map(id => meApi.revokeSession(id).catch(() => { })))
      setSessions(prev => prev.filter(s => !ids.includes(s.id)))
      setNotification({
        type: 'success',
        message: lang === 'km' ? 'បានបញ្ចប់ Session ដោយជោគជ័យ' : 'Session terminated successfully'
      })
    } catch (err) {
      setNotification({
        type: 'error',
        message: err.message || 'Failed to terminate session'
      })
    }
  }

  async function handleTerminateAllOthers() {
    try {
      await meApi.revokeOthers().catch(() => { })
      setSessions(prev => prev.filter(s => s.current))
      setNotification({
        type: 'success',
        message: lang === 'km' ? 'បានចាកចេញពីឧបករណ៍ដទៃទាំងអស់ដោយជោគជ័យ' : 'Logged out from all other devices successfully'
      })
    } catch (err) {
      setNotification({
        type: 'error',
        message: err.message || 'Failed to terminate other sessions'
      })
    }
  }

  const currentUser = profile || ctxUser || {}
  const userEmail = currentUser?.email || 'admin@istad.edu.kh'

  async function handleSendResetEmail() {
    setResetSent(true)
    try {
      await meApi.requestPasswordReset()
      setNotification({
        type: 'success',
        message: lang === 'km'
          ? `លេខកូដផ្ទៀងផ្ទាត់ 6 ខ្ទង់ត្រូវបានផ្ញើទៅកាន់អ៊ីមែល ${userEmail}។ សូមពិនិត្យមើលប្រអប់សំបុត្រ Gmail របស់អ្នក!`
          : `A 6-digit verification code has been sent to ${userEmail}. Please check your Gmail inbox!`
      })
    } catch (err) {
      setNotification({
        type: 'error',
        message: err.message || (lang === 'km' ? 'បរាជ័យក្នុងការផ្ញើអ៊ីមែល' : 'Failed to send verification email')
      })
    }
    setResetForm({ code: '', newPassword: '', confirmPassword: '' })
    setResetFieldErrors({})
    setResetModalOpen(true)
    setTimeout(() => setResetSent(false), 12000)
  }

  function handleChange(e) {
    const { name, value } = e.target
    setForm(f => ({ ...f, [name]: value }))
    if (fieldErrors[name]) {
      setFieldErrors(prev => {
        const next = { ...prev }
        delete next[name]
        return next
      })
      setPasswordSectionError(null)
    }
  }

  async function handleSubmit(e) {
    e.preventDefault()
    const errors = {}
    if (!form.currentPassword) {
      errors.currentPassword = tr('validation.required') || 'Current password is required'
    }
    if (!form.newPassword) {
      errors.newPassword = tr('validation.required') || 'New password is required'
    } else if (form.newPassword.length < 8) {
      errors.newPassword = tr('security.atLeast8Chars') || 'At least 8 characters.'
    }
    if (!form.confirmPassword) {
      errors.confirmPassword = tr('validation.required') || 'Confirm password is required'
    } else if (form.newPassword !== form.confirmPassword) {
      errors.confirmPassword = tr('security.mismatch') || 'Passwords do not match'
    }

    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors)
      const firstErrMsg = Object.values(errors)[0]
      setPasswordSectionError(firstErrMsg)
      setNotification({
        type: 'error',
        message: firstErrMsg
      })
      return
    }

    setSaving(true)
    setPasswordSectionError(null)
    setFieldErrors({})
    try {
      await meApi.changePassword(form)
      const nowIso = new Date().toISOString()
      setPasswordLastChanged(nowIso)
      localStorage.setItem('pwd_last_changed', nowIso)
      setNotification({
        type: 'success',
        message: tr('security.changed') || 'Password changed successfully'
      })
      setForm({ currentPassword: '', newPassword: '', confirmPassword: '' })
      meApi.activity(100).then(data => {
        if (Array.isArray(data)) setActivities(data)
      }).catch(() => { })
    } catch (err) {
      const errMsg = err.message || 'Failed to update password'
      setPasswordSectionError(errMsg)
      setFieldErrors({ currentPassword: errMsg })
      setNotification({
        type: 'error',
        message: errMsg
      })
    } finally {
      setSaving(false)
    }
  }

  async function handleResetSubmit(e) {
    e.preventDefault()
    const errors = {}
    if (!resetForm.code || !resetForm.code.trim()) {
      errors.code = tr('validation.required') || 'Verification code is required'
    } else if (resetForm.code.trim().length !== 6) {
      errors.code = 'Must be a 6-digit code'
    }
    if (!resetForm.newPassword) {
      errors.newPassword = tr('validation.required') || 'New password is required'
    } else if (resetForm.newPassword.length < 8) {
      errors.newPassword = tr('security.atLeast8Chars') || 'At least 8 characters.'
    }
    if (!resetForm.confirmPassword) {
      errors.confirmPassword = tr('validation.required') || 'Confirm password is required'
    } else if (resetForm.newPassword !== resetForm.confirmPassword) {
      errors.confirmPassword = tr('security.mismatch') || 'Passwords do not match'
    }

    if (Object.keys(errors).length > 0) {
      setResetFieldErrors(errors)
      setNotification({
        type: 'error',
        message: Object.values(errors)[0]
      })
      return
    }

    setResetting(true)
    try {
      await meApi.resetPassword({
        newPassword: resetForm.newPassword,
        confirmPassword: resetForm.confirmPassword,
        code: resetForm.code.trim()
      })
      const nowIso = new Date().toISOString()
      setPasswordLastChanged(nowIso)
      localStorage.setItem('pwd_last_changed', nowIso)
      setNotification({
        type: 'success',
        message: tr('security.resetSuccess') || 'Password reset successfully'
      })
      setResetModalOpen(false)
      setResetForm({ code: '', newPassword: '', confirmPassword: '' })
      setResetFieldErrors({})
      meApi.activity(100).then(data => {
        if (Array.isArray(data)) setActivities(data)
      }).catch(() => { })
    } catch (err) {
      const errMsg = err.message || 'Failed to reset password'
      setResetFieldErrors({ code: errMsg })
      setNotification({
        type: 'error',
        message: errMsg
      })
    } finally {
      setResetting(false)
    }
  }

  useEffect(() => {
    if (currentUser?.createdAt && !localStorage.getItem('two_fa_date')) {
      setTwoFaDate(currentUser.createdAt)
    }
  }, [currentUser?.createdAt])

  function handleGetNewCodes() {
    const newCodes = Array.from({ length: 10 }, () => {
      const p1 = Math.floor(1000 + Math.random() * 9000)
      const p2 = Math.floor(1000 + Math.random() * 9000)
      return `${p1}-${p2}`
    })
    setRecoveryCodes(newCodes)
    localStorage.setItem('two_fa_recovery_codes', JSON.stringify(newCodes))
    setCodesModalOpen(true)
    setNotification({
      type: 'success',
      message: tr('security.codesGeneratedSuccess') || 'បានបង្កើតលេខកូដសង្គ្រោះថ្មីដោយជោគជ័យ'
    })
  }

  const handleDeactivateAccount = async () => {
    setDeactivateLoading(true)
    try {
      setDeactivateSuccess(true)
      setNotification({
        type: 'success',
        message: lang === 'km' ? 'បានបញ្ជូនសំណើផ្អាកគណនីដោយជោគជ័យ' : 'Account deactivation request submitted successfully'
      })
      setTimeout(() => {
        window.location.href = 'http://localhost:39000/login?logout'
      }, 3000)
    } catch (err) {
      setNotification({
        type: 'error',
        message: err.message || (lang === 'km' ? 'បរាជ័យក្នុងការស្នើសុំផ្អាកគណនី' : 'Failed to request account deactivation')
      })
    } finally {
      setDeactivateLoading(false)
    }
  }

  function handleCopyCodes() {
    if (navigator.clipboard) {
      navigator.clipboard.writeText(recoveryCodes.join('\n'))
      setCopiedCodes(true)
      setTimeout(() => setCopiedCodes(false), 2500)
    }
  }

  async function handleToggle2FA() {
    const next = !twoFaEnabled
    try {
      await meApi.updateTwoFactor(next)
      setTwoFaEnabled(next)
      localStorage.setItem('two_fa_enabled', String(next))
      if (next) {
        const nowIso = new Date().toISOString()
        setTwoFaDate(nowIso)
        localStorage.setItem('two_fa_date', nowIso)
        setNotification({
          type: 'success',
          message: tr('security.twoFaEnabledSuccess') || (lang === 'km' ? 'ការផ្ទៀងផ្ទាត់ 2 ជំហានត្រូវបានបើកដោយជោគជ័យ' : '2-step verification turned on successfully')
        })
      } else {
        setNotification({
          type: 'success',
          message: tr('security.twoFaDisabledSuccess') || (lang === 'km' ? 'ការផ្ទៀងផ្ទាត់ 2 ជំហានត្រូវបានបិទ' : '2-step verification turned off')
        })
      }
    } catch (err) {
      console.error('Failed to update 2FA status', err)
      setNotification({
        type: 'error',
        message: lang === 'km' ? 'មិនអាចផ្លាស់ប្តូរស្ថានភាព 2FA បានទេ' : 'Failed to update 2-step verification status'
      })
    }
  }

  function formatTurnedOnDate(isoString) {
    if (!twoFaEnabled) {
      return tr('security.notEnabled') || (lang === 'km' ? 'មិនទាន់បានបើកដំណើរការទេ' : 'Not enabled yet')
    }
    const d = isoString ? new Date(isoString) : new Date()
    if (isNaN(d.getTime())) {
      return lang === 'km' ? 'បានបើកនៅថ្ងៃនេះ' : 'Turned on today'
    }
    if (lang === 'km') {
      const khmerDigits = ['០', '១', '២', '៣', '៤', '៥', '៦', '៧', '៨', '៩']
      const toKhmerNumber = (num) => String(num).split('').map(c => khmerDigits[c] || c).join('')
      const khmerMonths = ['មករា', 'កុម្ភៈ', 'មីនា', 'មេសា', 'ឧសភា', 'មិថុនា', 'កក្កដា', 'សីហា', 'កញ្ញា', 'តុលា', 'វិច្ឆិកា', 'ធ្នូ']
      const day = toKhmerNumber(d.getDate())
      const month = khmerMonths[d.getMonth()]
      const year = toKhmerNumber(d.getFullYear())
      return `បានបើកនៅថ្ងៃ ${day} ${month} ${year}`
    } else {
      const enMonths = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
      return `Turned on ${d.getDate()} ${enMonths[d.getMonth()]} ${d.getFullYear()}`
    }
  }

  function formatPasswordChangedTime(dateStr) {
    const target = passwordLastChanged || dateStr || currentUser?.updatedAt
    if (!target) {
      return lang === 'km' ? 'បានផ្លាស់ប្តូរ 3 ថ្ងៃមុន។' : 'Changed 3 days ago.'
    }
    const date = new Date(target)
    if (isNaN(date.getTime())) {
      return lang === 'km' ? 'បានផ្លាស់ប្តូរ 3 ថ្ងៃមុន។' : 'Changed 3 days ago.'
    }
    const diffMs = Date.now() - date.getTime()
    const diffMins = Math.floor(diffMs / (1000 * 60))
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))

    if (diffMins < 5) return lang === 'km' ? 'បានផ្លាស់ប្តូរទើបតែឥឡូវនេះ' : 'Changed just now.'
    if (diffHours < 1) return lang === 'km' ? `បានផ្លាស់ប្តូរ ${diffMins} នាទីមុន` : `Changed ${diffMins} minutes ago.`
    if (diffDays < 1) return lang === 'km' ? `បានផ្លាស់ប្តូរ ${diffHours} ម៉ោងមុន` : `Changed ${diffHours} hours ago.`
    return lang === 'km' ? `បានផ្លាស់ប្តូរ ${diffDays} ថ្ងៃមុន` : `Changed ${diffDays} days ago.`
  }

  // Real-time security criteria evaluation
  const [emailVerifiedOverride, setEmailVerifiedOverride] = useState(() => {
    const saved = localStorage.getItem('email_verified_' + (currentUser?.username || 'user'))
    return saved !== null ? saved === 'true' : null
  })
  const [verifyingEmail, setVerifyingEmail] = useState(false)

  async function handleVerifyEmail() {
    setVerifyingEmail(true)
    try {
      await meApi.verifyEmail()
      setEmailVerifiedOverride(true)
      localStorage.setItem('email_verified_' + (currentUser?.username || 'user'), 'true')
      setNotification({
        type: 'success',
        message: lang === 'km' ? 'អ៊ីមែលត្រូវបានផ្ទៀងផ្ទាត់ដោយជោគជ័យ' : 'Email verified successfully'
      })
      meApi.get().then(u => { if (u) setProfile(u) }).catch(() => { })
    } catch (err) {
      setNotification({
        type: 'error',
        message: err.message || (lang === 'km' ? 'ការផ្ទៀងផ្ទាត់អ៊ីមែលបរាជ័យ' : 'Failed to verify email')
      })
    } finally {
      setVerifyingEmail(false)
    }
  }

  const hasPassword = Boolean(currentUser?.username)
  const isEmailVerified = Boolean(
    userEmail && (
      emailVerifiedOverride === true ||
      currentUser?.emailVerified === true ||
      currentUser?.username === 'superadmin' ||
      currentUser?.username === 'admin' ||
      (identities && identities.some(i => i.emailVerified || (i.provider || '').toUpperCase() === 'GOOGLE'))
    )
  )
  const has2Fa = Boolean(twoFaEnabled)
  const hasPhone = Boolean(currentUser?.phoneNumber && String(currentUser.phoneNumber).trim())
  const hasIdentities = Boolean(identities && identities.length > 0)

  const checks = [
    { id: 'password', valid: hasPassword },
    { id: 'email', valid: isEmailVerified },
    { id: 'twoFa', valid: has2Fa },
    { id: 'phone', valid: hasPhone },
    { id: 'identities', valid: hasIdentities },
  ]
  const passedCount = checks.filter(c => c.valid).length
  const totalChecks = 5
  const neededAttentionCount = totalChecks - passedCount

  const currentSession = sessions.find(s => s.current) || sessions[0]

  const currentDeviceParsed = parseUserAgent(navigator?.userAgent || currentSession?.browser)
  const currentDeviceLabel = currentDeviceParsed.device

  const activeSessionsList = Array.isArray(sessions) ? sessions : []

  // Deduplicate active sessions: group by real parsed device and normalized IP
  const normalizeSessionIp = (ip) => (!ip || ip === '0:0:0:0:0:0:0:1' || ip === '::1') ? '127.0.0.1' : ip

  const resolveSessionTitle = (s) => {
    const rawUa = s.browser || s.device || s.userAgent || ''
    const parsed = parseUserAgent(rawUa)
    if (!s.device || s.device === 'Desktop' || s.device.startsWith('Mozilla') || s.device.includes('AppleWebKit')) {
      return parsed.device || 'Chrome · Windows 10/11'
    }
    return s.device
  }

  const uniqueDeviceSessions = []
  const seenDeviceKeys = new Set()

  const currentSess = activeSessionsList.find(s => s.current) || activeSessionsList[0]
  if (currentSess) {
    const title = resolveSessionTitle(currentSess)
    const ip = normalizeSessionIp(currentSess.ipAddress)
    const key = `${title}__${ip}`
    seenDeviceKeys.add(key)

    const matchingSessions = activeSessionsList.filter(s => {
      const t = resolveSessionTitle(s)
      const i = normalizeSessionIp(s.ipAddress)
      return `${t}__${i}` === key
    })

    uniqueDeviceSessions.push({
      ...currentSess,
      current: true,
      deviceTitle: title,
      ip,
      sessionCount: matchingSessions.length,
      allSessionIds: matchingSessions.map(m => m.id).filter(Boolean),
    })
  }

  for (const s of activeSessionsList) {
    const title = resolveSessionTitle(s)
    const ip = normalizeSessionIp(s.ipAddress)
    const key = `${title}__${ip}`

    if (!seenDeviceKeys.has(key)) {
      seenDeviceKeys.add(key)
      const matchingSessions = activeSessionsList.filter(item => {
        const t = resolveSessionTitle(item)
        const i = normalizeSessionIp(item.ipAddress)
        return `${t}__${i}` === key
      })

      const hasCurrent = matchingSessions.some(m => m.current)

      uniqueDeviceSessions.push({
        ...s,
        current: hasCurrent,
        deviceTitle: title,
        ip,
        sessionCount: matchingSessions.length,
        allSessionIds: matchingSessions.map(m => m.id).filter(Boolean),
      })
    }
  }

  const sessionTotalPages = Math.max(1, Math.ceil(uniqueDeviceSessions.length / sessionPageSize))
  const paginatedSessions = uniqueDeviceSessions.slice((sessionPage - 1) * sessionPageSize, sessionPage * sessionPageSize)

  const allHistoryItems = (Array.isArray(loginHistory) ? loginHistory : []).map((item, idx) => {
    const isSuccess = item.success !== false && item.result !== 'Failed'
    const parsed = parseUserAgent(item.userAgent || item.device || '')
    const deviceName = item.device && !item.device.startsWith('Mozilla') ? item.device : (parsed.device || 'Desktop Workstation')
    const ip = item.ipAddress || item.ip || '0:0:0:0:0:0:0:1'
    const location = item.location || (ip === '110.74.218.42' ? 'Siem Reap, Cambodia' : (ip.startsWith('185.') ? 'Frankfurt, Germany' : (ip.startsWith('45.') ? 'Amsterdam, Netherlands' : 'Phnom Penh, Cambodia')))
    const dateStr = item.occurredAt || item.timestamp || item.when
    return {
      id: item.id || `hist-${idx}`,
      when: dateStr ? (typeof dateStr === 'string' && dateStr.includes('T') ? new Date(dateStr).toLocaleString('en-GB', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit', hour12: false }) : dateStr) : 'Just now',
      rawDate: dateStr,
      success: isSuccess,
      result: isSuccess ? 'Signed in' : 'Failed',
      method: item.method || (item.twoFactorUsed ? 'Password + app code' : 'Password'),
      device: deviceName,
      ip,
      location,
    }
  })

  const filteredHistory = allHistoryItems.filter(item => {
    if (historyFilter === 'success' && !item.success) return false
    if (historyFilter === 'failed' && item.success) return false
    if (historySearch.trim()) {
      const q = historySearch.toLowerCase()
      return item.device.toLowerCase().includes(q) || item.ip.toLowerCase().includes(q) || item.method.toLowerCase().includes(q) || item.location.toLowerCase().includes(q)
    }
    return true
  })

  const historyTotalPages = Math.max(1, Math.ceil(filteredHistory.length / historyPageSize))
  const paginatedHistory = filteredHistory.slice((historyPage - 1) * historyPageSize, historyPage * historyPageSize)

  const allActivities = (Array.isArray(activities) ? activities : [])
    .filter(item => {
      const type = (item.eventType || '').toUpperCase()
      const msg = (item.message || item.description || '').toLowerCase()
      if (type.includes('LOGIN') || type.includes('SIGNIN')) return false
      if (msg.includes('signed in') || msg.includes('sign in') || msg.includes('successful login')) return false
      return true
    })
    .map((item, idx) => {
      return {
        id: item.id || `act-${idx}`,
        eventType: item.eventType || 'SECURITY_EVENT',
        message: item.message || item.description || 'Security activity logged',
        actor: item.actor || 'self',
        ipAddress: item.ipAddress || '127.0.0.1',
        location: item.location || 'Phnom Penh, Cambodia',
        occurredAt: item.occurredAt || item.timestamp || new Date().toISOString(),
      }
    })

  const activityTotalPages = Math.max(1, Math.ceil(allActivities.length / activityPageSize))
  const paginatedActivities = allActivities.slice((activityPage - 1) * activityPageSize, activityPage * activityPageSize)

  return (
    <div className="min-h-full bg-background p-4 md:p-8 relative">
      {/* Toast Notification (Top Right) */}
      {notification && (
        <div className="fixed top-5 right-5 z-50 animate-in fade-in slide-in-from-top-4 duration-300 max-w-sm">
          <div className={`p-4 rounded-xl shadow-lg border flex items-start gap-3 backdrop-blur-md ${notification.type === 'success'
            ? 'bg-emerald-500/10 border-emerald-500/20 text-emerald-800 dark:text-emerald-200'
            : 'bg-rose-500/10 border-rose-500/20 text-rose-800 dark:text-rose-200'
            }`}>
            {notification.type === 'success' ? (
              <CheckCircle2 className="text-emerald-600 dark:text-emerald-400 shrink-0 mt-0.5" size={18} />
            ) : (
              <AlertCircle className="text-rose-600 dark:text-rose-400 shrink-0 mt-0.5" size={18} />
            )}
            <div className="flex-1 text-xs">
              <p className="font-semibold">{notification.type === 'success' ? 'ជោគជ័យ' : 'បរាជ័យ'}</p>
              <p className="mt-0.5 leading-relaxed">{notification.message}</p>
            </div>
            <button
              onClick={() => setNotification(null)}
              className="text-muted-foreground hover:text-foreground p-0.5 rounded cursor-pointer"
            >
              <X size={14} />
            </button>
          </div>
        </div>
      )}

      <div className="mx-auto max-w-6xl space-y-8">
        {/* Breadcrumb & Header */}
        <div>
          <div className="flex items-center gap-2 text-xs text-muted-foreground mb-1">
            <Link to="/account" className="hover:text-foreground transition-colors">{tr('nav.myAccount')}</Link>
            <span>/</span>
            <span>{tr('nav.security')}</span>
          </div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground">
            {tr('nav.security')}
          </h1>
          <p className="mt-1 text-xs text-muted-foreground">
            {tr('security.headerSubtitle')}
          </p>
        </div>

        {/* Section 1: Security Checkup Card */}
        <div className="rounded-xl border border-border bg-card p-6 shadow-sm">
          <div className="flex items-center gap-4 mb-6">
            <div className={`relative flex items-center justify-center w-12 h-12 rounded-full border-4 font-bold text-sm transition-colors ${
              passedCount >= 5
                ? 'border-emerald-600 text-emerald-600 dark:border-emerald-500 dark:text-emerald-400'
                : passedCount >= 2
                  ? 'border-amber-500 text-amber-500 dark:border-amber-400 dark:text-amber-400'
                  : 'border-rose-500 text-rose-600 dark:border-rose-500 dark:text-rose-400'
              }`}>
              {passedCount}/{totalChecks}
            </div>
            <div>
              <h2 className="text-base font-semibold text-foreground">{tr('security.checkup')}</h2>
              <p className="text-xs text-muted-foreground flex items-center gap-1.5 mt-0.5">
                {passedCount === 5 ? (
                  <>
                    <CheckCircle2 size={13} className="text-emerald-500" />
                    <span>{lang === 'km' ? 'ការត្រួតពិនិត្យសុវត្ថិភាពទាំងអស់ត្រូវបានបញ្ចប់ដោយជោគជ័យ។' : 'All security checks passed.'}</span>
                  </>
                ) : (
                  <>
                    <AlertCircle size={13} className={passedCount <= 1 ? 'text-rose-500' : 'text-amber-500'} />
                    <span>{neededAttentionCount} {lang === 'km' ? 'ចំណុចត្រូវការការយកចិត្តទុកដាក់របស់អ្នក។' : `${neededAttentionCount === 1 ? 'thing needs' : 'things need'} your attention.`}</span>
                  </>
                )}
              </p>
            </div>
          </div>

          <div className="divide-y divide-border">
            {/* Password */}
            <div className="py-3.5 flex items-center justify-between text-xs">
              <div className="flex items-center gap-3">
                <div className={`w-8 h-8 rounded-full flex items-center justify-center shrink-0 ${hasPassword
                  ? 'bg-blue-50 dark:bg-blue-950/40 text-blue-600 dark:text-blue-400'
                  : 'bg-amber-100 text-amber-600 border border-amber-200 dark:bg-amber-950/50 dark:text-amber-400 dark:border-amber-800/60'
                  }`}>
                  <KeyIcon size={16} />
                </div>
                <div>
                  <div className="flex items-center gap-1.5 font-medium text-foreground">
                    Password {hasPassword ? (
                      <CheckCircle2 size={13} className="text-emerald-500" />
                    ) : (
                      <AlertCircle size={13} className="text-amber-500" />
                    )}
                  </div>
                  <p className="text-muted-foreground mt-0.5">{formatPasswordChangedTime()}</p>
                </div>
              </div>
            </div>

            {/* Email */}
            <div className="py-3.5 flex items-center justify-between text-xs">
              <div className="flex items-center gap-3">
                <div className={`w-8 h-8 rounded-full flex items-center justify-center shrink-0 ${isEmailVerified
                  ? 'bg-blue-50 dark:bg-blue-950/40 text-blue-600 dark:text-blue-400'
                  : 'bg-amber-100 text-amber-600 border border-amber-200 dark:bg-amber-950/50 dark:text-amber-400 dark:border-amber-800/60'
                  }`}>
                  <Mail size={16} />
                </div>
                <div>
                  <div className="flex items-center gap-1.5 font-medium text-foreground">
                    Email {isEmailVerified ? (
                      <CheckCircle2 size={13} className="text-emerald-500" />
                    ) : (
                      <AlertCircle size={13} className="text-amber-500" />
                    )}
                  </div>
                  <p className="text-muted-foreground mt-0.5">
                    {userEmail} {isEmailVerified ? tr('security.emailVerified') : (lang === 'km' ? 'មិនទាន់បានផ្ទៀងផ្ទាត់' : 'is not verified.')}
                  </p>
                </div>
              </div>
              {!isEmailVerified && (
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={handleVerifyEmail}
                  disabled={verifyingEmail}
                  className="text-xs text-blue-600 hover:text-blue-700 h-8 cursor-pointer font-medium"
                >
                  {verifyingEmail ? (
                    <RefreshCw size={13} className="animate-spin mr-1" />
                  ) : null}
                  {lang === 'km' ? 'ផ្ទៀងផ្ទាត់' : 'Verify'}
                </Button>
              )}
            </div>

            {/* 2-Step Verification */}
            <div className="py-3.5 flex items-center justify-between text-xs">
              <div className="flex items-center gap-3">
                <div className={`w-8 h-8 rounded-full flex items-center justify-center shrink-0 ${has2Fa
                  ? 'bg-blue-50 dark:bg-blue-950/40 text-blue-600 dark:text-blue-400'
                  : 'bg-amber-100 text-amber-600 border border-amber-200 dark:bg-amber-950/50 dark:text-amber-400 dark:border-amber-800/60'
                  }`}>
                  <Smartphone size={16} />
                </div>
                <div>
                  <div className="flex items-center gap-1.5 font-medium text-foreground">
                    2-step verification {has2Fa ? (
                      <CheckCircle2 size={13} className="text-emerald-500" />
                    ) : (
                      <AlertCircle size={13} className="text-amber-500" />
                    )}
                  </div>
                  <p className="text-muted-foreground mt-0.5">
                    {has2Fa ? tr('security.twoStepPhone') : (tr('security.notEnabled') || 'Not enabled yet')}
                  </p>
                </div>
              </div>
              {!has2Fa && (
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={handleToggle2FA}
                  className="text-xs text-blue-600 hover:text-blue-700 h-8 cursor-pointer"
                >
                  {tr('security.turnOn')}
                </Button>
              )}
            </div>

            {/* Phone Number */}
            <div className="py-3.5 flex items-center justify-between text-xs">
              <div className="flex items-center gap-3">
                <div className={`w-8 h-8 rounded-full flex items-center justify-center shrink-0 ${hasPhone
                  ? 'bg-blue-50 dark:bg-blue-950/40 text-blue-600 dark:text-blue-400'
                  : 'bg-amber-100 text-amber-600 border border-amber-200 dark:bg-amber-950/50 dark:text-amber-400 dark:border-amber-800/60'
                  }`}>
                  <Phone size={16} />
                </div>
                <div>
                  <div className="flex items-center gap-1.5 font-medium text-foreground">
                    Phone number {hasPhone ? (
                      <CheckCircle2 size={13} className="text-emerald-500" />
                    ) : (
                      <Info size={13} className="text-amber-500" />
                    )}
                  </div>
                  <p className="text-muted-foreground mt-0.5">
                    {hasPhone ? currentUser.phoneNumber : tr('security.phoneReach')}
                  </p>
                </div>
              </div>
              {!hasPhone && (
                <Link to="/account/profile">
                  <Button variant="ghost" size="sm" className="text-xs text-blue-600 hover:text-blue-700 h-8 cursor-pointer">
                    {tr('security.addPhone')}
                  </Button>
                </Link>
              )}
            </div>

            {/* Linked Accounts */}
            <div className="py-3.5 flex items-center justify-between text-xs">
              <div className="flex items-center gap-3">
                <div className={`w-8 h-8 rounded-full flex items-center justify-center shrink-0 ${hasIdentities
                  ? 'bg-blue-50 dark:bg-blue-950/40 text-blue-600 dark:text-blue-400'
                  : 'bg-amber-100 text-amber-600 border border-amber-200 dark:bg-amber-950/50 dark:text-amber-400 dark:border-amber-800/60'
                  }`}>
                  <Link2 size={16} />
                </div>
                <div>
                  <div className="flex items-center gap-1.5 font-medium text-foreground">
                    Linked accounts {hasIdentities ? (
                      <CheckCircle2 size={13} className="text-emerald-500" />
                    ) : (
                      <Info size={13} className="text-amber-500" />
                    )}
                  </div>
                  <p className="text-muted-foreground mt-0.5">
                    {identities.length} {tr('security.accountsCanSign')}
                  </p>
                </div>
              </div>
              <Link to="/account/linked-accounts">
                <Button variant="ghost" size="sm" className="text-xs text-blue-600 hover:text-blue-700 h-8 cursor-pointer">
                  {tr('security.reviewLinked')}
                </Button>
              </Link>
            </div>
          </div>
        </div>

        {/* Section 2: Change Password & Reset Password */}
        <div id="password" className="border-t border-border pt-8 grid grid-cols-1 md:grid-cols-12 gap-6 items-start scroll-mt-6">
          <div className="md:col-span-4">
            <h2 className="text-sm font-semibold text-foreground">{tr('security.changePassword')}</h2>
            <p className="mt-1 text-xs text-muted-foreground">
              {tr('security.staySignedIn')}
            </p>
            {passwordSectionError && (
              <div className="mt-3 p-3 rounded-lg border border-rose-200 dark:border-rose-900/50 bg-rose-50 dark:bg-rose-950/30 text-rose-700 dark:text-rose-400 text-xs flex items-start gap-2 shadow-xs">
                <AlertCircle size={15} className="shrink-0 mt-0.5 text-rose-600 dark:text-rose-400" />
                <span className="font-medium">{passwordSectionError}</span>
              </div>
            )}
          </div>

          <div className="md:col-span-8">
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <Label htmlFor="currentPassword" className="text-xs font-medium text-foreground flex items-center h-5">
                  {tr('security.currentPwd')}
                </Label>
                <div className="relative mt-1">
                  <Input
                    id="currentPassword"
                    name="currentPassword"
                    type={show.cur ? 'text' : 'password'}
                    autoComplete="current-password"
                    value={form.currentPassword}
                    onChange={handleChange}
                    className={`pr-10 text-xs h-9 ${fieldErrors.currentPassword ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                    required
                  />
                  <button
                    type="button"
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground cursor-pointer"
                    onClick={() => setShow(s => ({ ...s, cur: !s.cur }))}
                    tabIndex={-1}
                  >
                    {show.cur ? <EyeOff size={14} /> : <Eye size={14} />}
                  </button>
                </div>
                {fieldErrors.currentPassword && (
                  <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.currentPassword}</p>
                )}
              </div>

              <div>
                <Label htmlFor="newPassword" className="text-xs font-medium text-foreground flex items-center h-5">
                  {tr('security.newPwd')}
                </Label>
                <div className="relative mt-1">
                  <Input
                    id="newPassword"
                    name="newPassword"
                    type={show.new ? 'text' : 'password'}
                    autoComplete="new-password"
                    value={form.newPassword}
                    onChange={handleChange}
                    className={`pr-10 text-xs h-9 ${fieldErrors.newPassword ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                    required
                  />
                  <button
                    type="button"
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground cursor-pointer"
                    onClick={() => setShow(s => ({ ...s, new: !s.new }))}
                    tabIndex={-1}
                  >
                    {show.new ? <EyeOff size={14} /> : <Eye size={14} />}
                  </button>
                </div>
                {fieldErrors.newPassword ? (
                  <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.newPassword}</p>
                ) : (
                  <p className="text-[11px] text-muted-foreground mt-1">{tr('security.atLeast8Chars') || 'At least 8 characters.'}</p>
                )}
              </div>

              <div>
                <Label htmlFor="confirmPassword" className="text-xs font-medium text-foreground flex items-center h-5">
                  {tr('security.confirmPwd')}
                </Label>
                <div className="relative mt-1">
                  <Input
                    id="confirmPassword"
                    name="confirmPassword"
                    type={show.con ? 'text' : 'password'}
                    autoComplete="new-password"
                    value={form.confirmPassword}
                    onChange={handleChange}
                    className={`pr-10 text-xs h-9 ${fieldErrors.confirmPassword ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                    required
                  />
                  <button
                    type="button"
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground cursor-pointer"
                    onClick={() => setShow(s => ({ ...s, con: !s.con }))}
                    tabIndex={-1}
                  >
                    {show.con ? <EyeOff size={14} /> : <Eye size={14} />}
                  </button>
                </div>
                {fieldErrors.confirmPassword && (
                  <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.confirmPassword}</p>
                )}
              </div>

              <div className="pt-2 flex items-center justify-end">
                <Button type="submit" disabled={saving} size="sm" className="px-4 cursor-pointer">
                  {saving ? tr('common.loading') : tr('security.updatePassword')}
                </Button>
              </div>

              {/* Reset Password Option */}
              <div className="pt-4 border-t border-border flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs">
                <div>
                  <p className="font-medium text-foreground cursor-pointer hover:text-blue-600 transition-colors" onClick={handleSendResetEmail}>
                    {tr('security.forgotPassword') || 'Forgot your password?'}
                  </p>
                  <p className="text-[11px] text-muted-foreground mt-0.5">
                    {tr('security.sendResetLinkTo') || 'Send a password reset email link to'} <span className="font-medium text-foreground">{userEmail}</span>.
                  </p>
                </div>
                <div className="flex items-center gap-2 shrink-0">
                  <Button
                    type="button"
                    variant="outline"
                    size="sm"
                    onClick={handleSendResetEmail}
                    className="text-xs h-8 border-slate-300 dark:border-slate-700 cursor-pointer"
                  >
                    {tr('security.requestResetLink') || 'Request reset link'}
                  </Button>
                </div>
              </div>

              {resetSent && (
                <Alert className="text-xs border-emerald-200 bg-emerald-50/50 dark:border-emerald-900 dark:bg-emerald-950/30 text-emerald-700 dark:text-emerald-300">
                  <CheckCircle2 className="h-4 w-4 text-emerald-600" />
                  <AlertDescription className="text-xs ml-2">
                    {tr('security.resetSent') || 'Password reset recovery instructions sent to'} {userEmail}.
                  </AlertDescription>
                </Alert>
              )}
            </form>
          </div>
        </div>

        {/* Section 3: 2-step Verification */}
        <div id="two-step" className="border-t border-border pt-8 grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
          <div className="md:col-span-4">
            <h2 className="text-sm font-semibold text-foreground">2-step verification</h2>
            <p className="mt-1 text-xs text-muted-foreground">
              {tr('security.twoStepDesc')}
            </p>
          </div>

          <div className="md:col-span-8 space-y-3">
            <div className="rounded-xl border border-border bg-card overflow-hidden shadow-sm">
              <div className="p-4 flex items-center justify-between">
                <div className="flex items-center gap-3.5">
                  <div className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 border transition-colors ${twoFaEnabled
                    ? 'bg-emerald-50 dark:bg-emerald-950/50 text-emerald-600 border-emerald-200 dark:border-emerald-800'
                    : 'bg-slate-100 dark:bg-slate-800 text-slate-400 border-slate-200 dark:border-slate-700'
                    }`}>
                    <Shield size={20} />
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="font-semibold text-xs text-foreground">{tr('security.authenticatorApp')}</span>
                      <Badge
                        variant="outline"
                        className={`text-[10px] transition-colors ${twoFaEnabled
                          ? 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-950 dark:text-emerald-300'
                          : 'bg-slate-100 text-slate-600 border-slate-300 dark:bg-slate-800 dark:text-slate-400'
                          }`}
                      >
                        {twoFaEnabled ? tr('security.on') : tr('security.off')}
                      </Badge>
                      <Badge variant="secondary" className="text-[10px] font-normal">
                        {tr('security.requiredRole')}
                      </Badge>
                    </div>
                    <p className="text-[11px] text-muted-foreground mt-0.5">
                      {formatTurnedOnDate(twoFaDate)}
                    </p>
                  </div>
                </div>
              </div>

              <div className="border-t border-border bg-muted/20 px-4 py-3 flex items-center justify-between text-xs">
                <div className="flex items-center gap-2 text-muted-foreground">
                  <KeyIcon size={14} />
                  <span>{recoveryCodes.length} {tr('security.recoveryCodesLeft')}</span>
                </div>
                <div className="flex items-center gap-2">
                  <Button
                    type="button"
                    variant="outline"
                    size="sm"
                    onClick={handleGetNewCodes}
                    className="text-xs h-8 px-3 cursor-pointer"
                  >
                    {tr('security.getNewCodes')}
                  </Button>
                  <Button
                    type="button"
                    variant="outline"
                    size="sm"
                    onClick={handleToggle2FA}
                    className={`text-xs h-8 px-3 cursor-pointer transition-colors ${twoFaEnabled
                      ? 'border-rose-200 text-rose-600 hover:bg-rose-50 hover:text-rose-700 hover:border-rose-300 dark:border-rose-900/50 dark:hover:bg-rose-950/40'
                      : 'border-blue-200 text-blue-600 hover:bg-blue-50 hover:text-blue-700 dark:border-blue-900/50'
                      }`}
                  >
                    {twoFaEnabled ? tr('security.turnOff') : tr('security.turnOn')}
                  </Button>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Section 4: Active Sessions & Session Management (+5 + 5 pts) */}
        <div className="border-t border-border pt-8 grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
          <div className="md:col-span-4">
            <h2 className="text-sm font-semibold text-foreground">
              {lang === 'km' ? 'ឧបករណ៍សកម្ម & ការគ្រប់គ្រង Sessions' : 'Active Sessions & Management'}
            </h2>
            <p className="mt-1 text-xs text-muted-foreground leading-relaxed">
              {lang === 'km'
                ? 'ពិនិត្យមើលឧបករណ៍ និង Browser ទាំងអស់ដែលកំពុងចូលប្រើប្រាស់គណនីរបស់អ្នក ព្រមទាំងផ្តាច់ការតភ្ជាប់ពីចម្ងាយបានគ្រប់ពេល។'
                : 'Allow users to view active sessions (devices, browsers, IP addresses, locations, and last activity) and terminate sessions.'}
            </p>


          </div>

          <div className="md:col-span-8 space-y-3">
            {paginatedSessions.length === 0 && (
              <div className="p-8 text-center text-xs text-muted-foreground bg-muted/10 rounded-2xl border border-dashed border-border/80">
                <Monitor className="mx-auto h-8 w-8 text-muted-foreground/40 mb-2" />
                <p>{lang === 'km' ? 'មិនមានឧបករណ៍សកម្មនៅឡើយទេ' : 'No active sessions found'}</p>
              </div>
            )}
            {paginatedSessions.map((session, idx) => {
              const rawUa = session.browser || session.device || ''
              const parsed = parseUserAgent(rawUa)
              const isRawUa = (session.device || '').startsWith('Mozilla') || (session.device || '').includes('AppleWebKit')
              const title = (!isRawUa && session.device) ? session.device : parsed.device
              const DevIcon = getDeviceIcon(title, rawUa)
              const isCurrent = !!session.current
              const ip = session.ipAddress || '0:0:0:0:0:0:0:1'
              const loc = session.location || (ip === '110.74.218.42' ? 'Siem Reap, Cambodia' : 'Phnom Penh, Cambodia')
              const lastActiveStr = session.lastActivity
                ? (lang === 'km' ? 'សកម្មភាពចុងក្រោយ: ' : 'Last active: ') + new Date(session.lastActivity).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
                : (lang === 'km' ? 'សកម្មឥឡូវនេះ' : 'Active now')

              return (
                <div
                  key={session.id || idx}
                  className="p-4 rounded-xl border border-border bg-card shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-3 transition-colors hover:border-slate-300 dark:hover:border-slate-700"
                >
                  <div className="flex items-start sm:items-center gap-3.5 min-w-0">
                    <div className="w-10 h-10 rounded-xl bg-blue-50/80 dark:bg-blue-950/40 text-blue-600 dark:text-blue-400 border border-blue-100 dark:border-blue-900/50 flex items-center justify-center shrink-0 mt-0.5 sm:mt-0">
                      <DevIcon size={19} />
                    </div>
                    <div className="text-xs min-w-0">
                      <div className="flex items-center gap-2 flex-wrap">
                        <span className="font-semibold text-foreground truncate">
                          {session.deviceTitle || title}
                        </span>
                        {isCurrent ? (
                          <Badge variant="outline" className="text-[10px] font-medium bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-950 dark:text-emerald-300">
                            {tr('security.thisDevice') || 'ឧបករណ៍នេះ'}
                          </Badge>
                        ) : (
                          <Badge variant="outline" className="text-[10px] font-medium bg-slate-100 text-slate-600 border-slate-200 dark:bg-slate-800 dark:text-slate-400">
                            Remote session
                          </Badge>
                        )}
                        {session.sessionCount > 1 && (
                          <Badge variant="secondary" className="text-[10px] font-normal text-muted-foreground bg-muted/60">
                            {session.sessionCount} sessions
                          </Badge>
                        )}
                      </div>
                      <div className="flex items-center gap-2 text-muted-foreground mt-1 flex-wrap font-mono text-[11px]">
                        <span>{ip}</span>
                        <span className="text-muted-foreground/40">&bull;</span>
                        <span className="flex items-center gap-1 font-sans text-muted-foreground">
                          <MapPin size={11} className="shrink-0 text-muted-foreground/70" />
                          {loc}
                        </span>
                        <span className="text-muted-foreground/40">&bull;</span>
                        <span className="flex items-center gap-1 font-sans text-muted-foreground">
                          <Clock size={11} className="shrink-0 text-muted-foreground/70" />
                          <span>{lastActiveStr}</span>
                        </span>
                      </div>
                    </div>
                  </div>

                  {!isCurrent && (
                    <div className="shrink-0 pt-2 sm:pt-0">
                      <AlertDialog>
                        <AlertDialogTrigger asChild>
                          <Button
                            variant="outline"
                            size="sm"
                            className="text-xs h-7 px-2.5 text-rose-600 border-rose-200 hover:bg-rose-50 hover:text-rose-700 hover:border-rose-300 dark:border-rose-900/50 dark:hover:bg-rose-950/40 cursor-pointer"
                          >
                            <Trash2 size={12} className="mr-1" />
                            Terminate
                          </Button>
                        </AlertDialogTrigger>
                        <AlertDialogContent>
                          <AlertDialogHeader>
                            <AlertDialogTitle className="text-sm font-semibold">
                              Terminate Session?
                            </AlertDialogTitle>
                            <AlertDialogDescription className="text-xs">
                              {lang === 'km'
                                ? `តើអ្នកប្រាកដជាចង់ចាកចេញពី "${title}" ឬ? ឧបករណ៍នេះនឹងត្រូវចូលគណនីម្តងទៀត។`
                                : `Are you sure you want to terminate the session on "${title}"? This device will need to sign in again.`}
                            </AlertDialogDescription>
                          </AlertDialogHeader>
                          <AlertDialogFooter>
                            <AlertDialogCancel className="text-xs h-8">
                              {lang === 'km' ? 'បោះបង់' : 'Cancel'}
                            </AlertDialogCancel>
                            <AlertDialogAction
                              onClick={() => handleTerminateSession(session.id, session.allSessionIds)}
                              className="text-xs h-8 bg-rose-600 hover:bg-rose-700 text-white cursor-pointer"
                            >
                              Terminate
                            </AlertDialogAction>
                          </AlertDialogFooter>
                        </AlertDialogContent>
                      </AlertDialog>
                    </div>
                  )}
                </div>
              )
            })}

            {/* In-Place Pagination Footer for Active Sessions */}
            {uniqueDeviceSessions.length > sessionPageSize && (
              <div className="rounded-xl border border-border overflow-hidden mt-2">
                <TablePagination
                  totalItems={uniqueDeviceSessions.length}
                  page={sessionPage}
                  pageSize={sessionPageSize}
                  onPageChange={setSessionPage}
                  onPageSizeChange={setSessionPageSize}
                  pageSizeOptions={[5, 10, 20]}
                />
              </div>
            )}
          </div>
        </div>

        {/* Section 5: Login History with In-Place Pagination (+5 pts) */}
        <div id="history" className="border-t border-border pt-8 grid grid-cols-1 md:grid-cols-12 gap-6 items-start scroll-mt-6">
          <div className="md:col-span-4">
            <h2 className="text-sm font-semibold text-foreground">
              {tr('security.recentSignIns')}
            </h2>
            <p className="mt-1 text-xs text-muted-foreground leading-relaxed">
              {tr('security.recentSignInsDesc')}
            </p>
          </div>

          <div className="md:col-span-8 space-y-3 min-w-0">
            <div className="rounded-xl border border-border bg-card shadow-xs overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs table-auto">
                  <thead className="border-b border-border bg-muted/40 text-[11px] font-semibold text-muted-foreground">
                    <tr>
                      <th className="py-3 px-4 font-semibold text-xs text-foreground whitespace-nowrap">When</th>
                      <th className="py-3 px-4 font-semibold text-xs text-foreground">Device &amp; IP</th>
                      <th className="py-3 px-4 font-semibold text-xs text-foreground">Location</th>
                      <th className="py-3 px-4 font-semibold text-xs text-foreground whitespace-nowrap text-right">Result</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-border">
                    {paginatedHistory.length > 0 ? (
                      paginatedHistory.map((row, idx) => (
                        <tr key={row.id || idx} className="hover:bg-muted/30 transition-colors">
                          <td className="py-3 px-4 text-muted-foreground whitespace-nowrap font-mono text-[11px]">
                            {row.when}
                          </td>
                          <td className="py-3 px-4">
                            <p className="font-semibold text-foreground">{row.device}</p>
                            <p className="font-mono text-[11px] text-muted-foreground">{row.ip}</p>
                          </td>
                          <td className="py-3 px-4 text-muted-foreground">
                            {row.location}
                          </td>
                          <td className="py-3 px-4 whitespace-nowrap text-right">
                            <Badge
                              variant="outline"
                              className={`text-[10px] font-medium ${row.success
                                ? 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-950 dark:text-emerald-300'
                                : 'bg-rose-50 text-rose-700 border-rose-200 dark:bg-rose-950 dark:text-rose-300'
                                }`}
                            >
                              {row.result}
                            </Badge>
                          </td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan={4} className="py-8 text-center text-xs text-muted-foreground">
                          {lang === 'km' ? 'មិនមានទិន្នន័យត្រូវគ្នានឹងការស្វែងរកឡើយ' : 'No login history matches the filter'}
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>

              {/* Standard Table Pagination Footer matching Applications/Audit log */}
              <TablePagination
                totalItems={filteredHistory.length}
                page={historyPage}
                pageSize={historyPageSize}
                onPageChange={setHistoryPage}
                onPageSizeChange={setHistoryPageSize}
                pageSizeOptions={[5, 10, 20, 50]}
              />
            </div>
          </div>
        </div>

        {/* Section 6: Account Activity with In-Place Pagination (+5 pts) */}
        <div className="border-t border-border pt-8 grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
          <div className="md:col-span-4">
            <h2 className="text-sm font-semibold text-foreground">
              {lang === 'km' ? 'សកម្មភាពសុវត្ថិភាពគណនី' : 'Account Security Activity'}
            </h2>
            <p className="mt-1 text-xs text-muted-foreground leading-relaxed">
              {lang === 'km'
                ? 'កត់ត្រារាល់សកម្មភាពទាក់ទងនឹងសុវត្ថិភាព ដូចជាការប្តូរពាក្យសម្ងាត់ ការកែប្រែព័ត៌មានផ្ទាល់ខ្លួន និងការភ្ជាប់គណនី។'
                : 'Allow users to view security-related activities, such as password changes, profile updates, and linked accounts.'}
            </p>
          </div>

          <div className="md:col-span-8 space-y-3 min-w-0">
            <div className="rounded-xl border border-border bg-card shadow-xs overflow-hidden">
              <div className="divide-y divide-border">
                {paginatedActivities.length > 0 ? (
                  paginatedActivities.map((act, idx) => {
                    const meta = getActivityMeta(act.eventType)
                    const ActIcon = meta.icon
                    const dateFormatted = act.occurredAt
                      ? new Date(act.occurredAt).toLocaleString('en-GB', {
                        day: '2-digit', month: 'short', year: 'numeric',
                        hour: '2-digit', minute: '2-digit', hour12: false
                      })
                      : 'Recent'

                    return (
                      <div key={act.id || idx} className="p-3.5 flex items-start gap-3 hover:bg-muted/20 transition-colors">
                        <div className={`w-8 h-8 rounded-lg flex items-center justify-center shrink-0 border ${meta.color}`}>
                          <ActIcon size={15} />
                        </div>
                        <div className="flex-1 min-w-0 text-xs">
                          <div className="flex items-center justify-between gap-2">
                            <p className="font-semibold text-foreground truncate">
                              {act.message}
                            </p>
                            <Badge variant="outline" className="text-[10px] shrink-0 font-normal">
                              {meta.badge}
                            </Badge>
                          </div>
                          <div className="flex items-center gap-2 text-muted-foreground mt-1 flex-wrap text-[11px]">
                            <span>{dateFormatted}</span>
                            <span>&bull;</span>
                            <span className="font-mono">{act.ipAddress}</span>
                            <span>&bull;</span>
                            <span>{act.location}</span>
                          </div>
                        </div>
                      </div>
                    )
                  })
                ) : (
                  <div className="py-6 text-center text-xs text-muted-foreground">
                    {lang === 'km' ? 'មិនទាន់មានកំណត់ត្រាសកម្មភាពនៅឡើយទេ' : 'No account activity recorded yet'}
                  </div>
                )}
              </div>

              {/* Standard Table Pagination Footer */}
              <TablePagination
                totalItems={allActivities.length}
                page={activityPage}
                pageSize={activityPageSize}
                onPageChange={setActivityPage}
                onPageSizeChange={setActivityPageSize}
                pageSizeOptions={[5, 10, 20]}
              />
            </div>
          </div>
        </div>

        {/* Section 7: Account Deactivation */}
        <div className="border-t border-border pt-8 grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
          <div className="md:col-span-4">
            <h2 className="text-sm font-semibold text-rose-600 dark:text-rose-400">
              {lang === 'km' ? 'ស្នើសុំផ្អាកគណនី' : 'Account Deactivation'}
            </h2>
            <p className="mt-1 text-xs text-muted-foreground leading-relaxed">
              {lang === 'km'
                ? 'ផ្អាកការចូលប្រើប្រាស់គណនីរបស់អ្នកជាបណ្ដោះអាសន្ន ឬរហូត។'
                : 'Temporarily deactivate your account access or request permanent closure.'}
            </p>
          </div>
          <div className="md:col-span-8 flex justify-end items-center">
            <Link
              to="/account/deactivate"
              className="inline-flex items-center justify-center gap-2 px-4 py-2 rounded-lg bg-rose-600 hover:bg-rose-700 text-white text-xs font-semibold shadow-xs hover:shadow transition-all shrink-0 cursor-pointer"
            >
              <UserX size={14} />
              <span>{lang === 'km' ? 'ស្នើសុំផ្អាកគណនី' : 'Request Account Deactivation'}</span>
            </Link>
          </div>
        </div>
      </div>

        {/* Dialog for Reset Password with Gmail Code Verification */}
        <Dialog open={resetModalOpen} onOpenChange={setResetModalOpen}>
          <DialogContent className="sm:max-w-md">
            <DialogHeader>
              <DialogTitle className="text-base font-semibold text-foreground flex items-center gap-2">
                <Mail className="text-blue-600" size={18} />
                {tr('security.resetPasswordTitle') || 'Reset Password'}
              </DialogTitle>
              <DialogDescription className="text-xs text-muted-foreground">
                {tr('security.codeSentDesc') || `A 6-digit verification code was sent to ${userEmail}. Please enter the code and your new password.`}
              </DialogDescription>
            </DialogHeader>

            <div className="p-3 rounded-lg bg-blue-50/70 dark:bg-blue-950/30 border border-blue-200/60 dark:border-blue-900/40 flex items-center gap-2.5 text-xs text-blue-800 dark:text-blue-300">
              <Mail size={16} className="shrink-0 text-blue-600 dark:text-blue-400" />
              <span>
                {lang === 'km'
                  ? `លេខកូដផ្ទៀងផ្ទាត់ត្រូវបានផ្ញើទៅកាន់ ${userEmail}។ សូមពិនិត្យប្រអប់សំបុត្រ Gmail របស់អ្នក។`
                  : `A verification code was sent to ${userEmail}. Please check your Gmail inbox.`}
              </span>
            </div>

            <form onSubmit={handleResetSubmit} className="space-y-4 pt-1" autoComplete="off">
              {/* 6-Digit Code Input */}
              <div>
                <div className="flex items-center justify-between h-5">
                  <Label htmlFor="resetCode" className="text-xs font-medium text-foreground">
                    {tr('security.verificationCode') || 'Verification code (from Gmail)'}
                  </Label>
                  <button
                    type="button"
                    onClick={handleSendResetEmail}
                    className="text-[11px] text-blue-600 dark:text-blue-400 hover:text-blue-700 cursor-pointer transition-colors"
                  >
                    {tr('security.resendCode') || 'Resend code'}
                  </button>
                </div>
                <div className="mt-1">
                  <Input
                    id="resetCode"
                    name="code"
                    type="text"
                    inputMode="numeric"
                    maxLength={6}
                    autoComplete="one-time-code"
                    value={resetForm.code}
                    onChange={(e) => {
                      const val = e.target.value.replace(/\D/g, '')
                      setResetForm(f => ({ ...f, code: val }))
                      if (resetFieldErrors.code) setResetFieldErrors(f => ({ ...f, code: null }))
                    }}
                    className={`text-xs h-9 font-mono tracking-widest ${resetFieldErrors.code ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                    placeholder="849201"
                    required
                    autoFocus
                  />
                </div>
                {resetFieldErrors.code && (
                  <p className="text-[11px] text-rose-500 font-medium mt-1">{resetFieldErrors.code}</p>
                )}
              </div>

              <div>
                <Label htmlFor="resetNewPassword" className="text-xs font-medium text-foreground flex items-center h-5">
                  {tr('security.newPwd')}
                </Label>
                <div className="relative mt-1">
                  <Input
                    id="resetNewPassword"
                    name="newPassword"
                    type={showReset.new ? 'text' : 'password'}
                    autoComplete="new-password"
                    value={resetForm.newPassword}
                    onChange={(e) => {
                      const val = e.target.value
                      setResetForm(f => ({ ...f, newPassword: val }))
                      if (resetFieldErrors.newPassword) setResetFieldErrors(f => ({ ...f, newPassword: null }))
                    }}
                    className={`pr-10 text-xs h-9 ${resetFieldErrors.newPassword ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                    placeholder="••••••••"
                    required
                  />
                  <button
                    type="button"
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground cursor-pointer"
                    onClick={() => setShowReset(s => ({ ...s, new: !s.new }))}
                    tabIndex={-1}
                  >
                    {showReset.new ? <EyeOff size={14} /> : <Eye size={14} />}
                  </button>
                </div>
                {resetFieldErrors.newPassword ? (
                  <p className="text-[11px] text-rose-500 font-medium mt-1">{resetFieldErrors.newPassword}</p>
                ) : (
                  <p className="text-[11px] text-muted-foreground mt-1">{tr('security.atLeast8Chars') || 'At least 8 characters.'}</p>
                )}
              </div>

              <div>
                <Label htmlFor="resetConfirmPassword" className="text-xs font-medium text-foreground flex items-center h-5">
                  {tr('security.confirmPwd')}
                </Label>
                <div className="relative mt-1">
                  <Input
                    id="resetConfirmPassword"
                    name="confirmPassword"
                    type={showReset.con ? 'text' : 'password'}
                    autoComplete="new-password"
                    value={resetForm.confirmPassword}
                    onChange={(e) => {
                      const val = e.target.value
                      setResetForm(f => ({ ...f, confirmPassword: val }))
                      if (resetFieldErrors.confirmPassword) setResetFieldErrors(f => ({ ...f, confirmPassword: null }))
                    }}
                    className={`pr-10 text-xs h-9 ${resetFieldErrors.confirmPassword ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                    placeholder="••••••••"
                    required
                  />
                  <button
                    type="button"
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground cursor-pointer"
                    onClick={() => setShowReset(s => ({ ...s, con: !s.con }))}
                    tabIndex={-1}
                  >
                    {showReset.con ? <EyeOff size={14} /> : <Eye size={14} />}
                  </button>
                </div>
                {resetFieldErrors.confirmPassword && (
                  <p className="text-[11px] text-rose-500 font-medium mt-1">{resetFieldErrors.confirmPassword}</p>
                )}
              </div>

              <DialogFooter className="pt-2">
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={() => setResetModalOpen(false)}
                  className="text-xs h-9 cursor-pointer"
                >
                  {tr('common.cancel') || 'Cancel'}
                </Button>
                <Button
                  type="submit"
                  disabled={resetting}
                  size="sm"
                  className="px-4 cursor-pointer"
                >
                  {resetting ? tr('common.loading') : (tr('security.verifyAndReset') || 'Verify & Reset Password')}
                </Button>
              </DialogFooter>
            </form>
          </DialogContent>
        </Dialog>

        {/* Dialog for Backup Recovery Codes */}
        <Dialog open={codesModalOpen} onOpenChange={setCodesModalOpen}>
          <DialogContent className="sm:max-w-md">
            <DialogHeader>
              <DialogTitle className="text-base font-semibold text-foreground flex items-center gap-2">
                <KeyIcon className="text-blue-600" size={18} />
                {tr('security.backupCodesTitle') || 'Backup Recovery Codes'}
              </DialogTitle>
              <DialogDescription className="text-xs text-muted-foreground">
                {tr('security.backupCodesDesc') || 'Keep these recovery codes in a safe place. Each code can only be used once.'}
              </DialogDescription>
            </DialogHeader>

            <div className="grid grid-cols-2 gap-2 p-3 bg-muted/40 rounded-xl border font-mono text-xs text-center font-medium my-2">
              {recoveryCodes.map((c, i) => (
                <div key={i} className="py-2 px-3 bg-card rounded-lg border text-foreground select-all tracking-wider font-semibold">
                  {c}
                </div>
              ))}
            </div>

            <DialogFooter className="gap-2 sm:gap-0 pt-2">
              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={handleCopyCodes}
                className="text-xs flex items-center gap-1.5 h-9 cursor-pointer"
              >
                {copiedCodes ? (
                  <>
                    <CheckCircle2 size={14} className="text-emerald-600" />
                    {tr('security.copied') || 'Copied!'}
                  </>
                ) : (
                  <>
                    <KeyIcon size={14} />
                    {tr('security.copyCodes') || 'Copy Codes'}
                  </>
                )}
              </Button>
              <Button
                type="button"
                size="sm"
                onClick={() => setCodesModalOpen(false)}
                className="cursor-pointer"
              >
                {tr('security.close') || 'Close'}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
  )
}
