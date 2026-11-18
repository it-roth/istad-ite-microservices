import { useEffect, useState, useMemo } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { useApp } from '@/context/AppContext'
import { usersApi, rolesApi, permissionsApi, adminSessionsApi } from '@/lib/api'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Tabs, TabsList, TabsTrigger, TabsContent } from '@/components/ui/tabs'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  AlertDialog,
  AlertDialogContent,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogAction,
  AlertDialogCancel,
} from '@/components/ui/alert-dialog'
import {
  Shield, Plus, X, Search, CheckCircle2,
  AlertCircle, ChevronDown, RefreshCw, Mail, Lock, UserX,
  Inbox, Monitor, Clock, Link2, EyeOff
} from 'lucide-react'
import KeyIcon from '@/components/icons/KeyIcon'
import UserAvatar, { getMainNameInitials } from '@/components/common/UserAvatar'
import { parseUserAgent } from '@/lib/deviceParser'
import { TablePagination } from '@/components/ui/table-pagination'

export default function UserDetailPage() {
  const { uuid } = useParams()
  const navigate = useNavigate()
  const { setBreadcrumbSub, tr, lang } = useApp?.() || {}

  const [user, setUser] = useState(null)
  const [authorities, setAuthorities] = useState({ roles: [], permissions: [], directPermissions: [], effectivePermissions: [] })
  const [allRoles, setAllRoles] = useState([])
  const [allPerms, setAllPerms] = useState([])
  const [identities, setIdentities] = useState([])
  const [userSessions, setUserSessions] = useState([])
  const [loginHistory, setLoginHistory] = useState([])
  const [historyPage, setHistoryPage] = useState(1)
  const [historyPageSize, setHistoryPageSize] = useState(5)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [actionMsg, setActionMsg] = useState('')
  const [permFilter, setPermFilter] = useState('')
  const [confirmAction, setConfirmAction] = useState(null)

  // Modals state
  const [assignRoleOpen, setAssignRoleOpen] = useState(false)
  const [grantPermOpen, setGrantPermOpen] = useState(false)
  const [selectedRoleUuid, setSelectedRoleUuid] = useState('')
  const [selectedPermUuid, setSelectedPermUuid] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [pwdError, setPwdError] = useState('')
  const [pwdModalOpen, setPwdModalOpen] = useState(false)
  const [editProfileOpen, setEditProfileOpen] = useState(false)

  // Edit profile form state
  const [editForm, setEditForm] = useState({
    givenName: '',
    familyName: '',
    middleName: '',
    nickname: '',
    email: '',
    phoneNumber: '',
    gender: '',
    birthdate: '',
    websiteUrl: '',
    addressStreetAddress: '',
  })

  function loadUserData() {
    setLoading(true)
    setError('')
    Promise.allSettled([
      usersApi.get(uuid),
      usersApi.authorities(uuid),
      rolesApi.list(0, 100),
      permissionsApi.list(0, 100),
      usersApi.identities(uuid),
      usersApi.sessions(uuid).catch(() => adminSessionsApi.list().catch(() => [])),
      usersApi.loginHistory(uuid, 100).catch(() => [])
    ]).then(([uRes, authRes, rRes, pRes, iRes, sessRes, histRes]) => {
      if (uRes.status === 'fulfilled' && uRes.value) {
        setUser(uRes.value)
        if (setBreadcrumbSub) {
          setBreadcrumbSub(uRes.value.username ? `@${uRes.value.username}` : (uRes.value.givenName || 'User'))
        }
        setEditForm({
          givenName: uRes.value.givenName || '',
          familyName: uRes.value.familyName || '',
          middleName: uRes.value.middleName || '',
          nickname: uRes.value.nickname || '',
          email: uRes.value.email || '',
          phoneNumber: uRes.value.phoneNumber || '',
          gender: uRes.value.gender || '',
          birthdate: uRes.value.birthdate || '',
          websiteUrl: uRes.value.websiteUrl || uRes.value.website || '',
          addressStreetAddress: uRes.value.addressStreetAddress || (typeof uRes.value.address === 'string' ? uRes.value.address : uRes.value.address?.streetAddress) || '',
        })
      } else {
        setError('Failed to load user: ' + (uRes.reason?.message || 'Not found'))
      }

      if (authRes.status === 'fulfilled') {
        setAuthorities(authRes.value || { roles: [], permissions: [], directPermissions: [], effectivePermissions: [] })
      }
      if (rRes.status === 'fulfilled') {
        setAllRoles(rRes.value?.content || [])
      }
      if (pRes.status === 'fulfilled') {
        setAllPerms(pRes.value?.content || [])
      }
      if (iRes.status === 'fulfilled') {
        setIdentities(Array.isArray(iRes.value) ? iRes.value : [])
      }
      if (sessRes.status === 'fulfilled' && Array.isArray(sessRes.value)) {
        const uVal = uRes.value
        // If sessions are already user-specific from usersApi.sessions
        const list = sessRes.value
        const filtered = list.some(s => s.actor || s.userUuid)
          ? list.filter(s => s.actor === uVal?.username || s.userUuid === uuid)
          : list
        setUserSessions(filtered)
      } else {
        setUserSessions([])
      }
      if (histRes.status === 'fulfilled' && Array.isArray(histRes.value)) {
        setLoginHistory(histRes.value)
      } else {
        setLoginHistory([])
      }
    }).finally(() => setLoading(false))
  }

  useEffect(() => {
    loadUserData()
  }, [uuid])

  // Save profile edit
  async function handleSaveProfile(e) {
    e.preventDefault()
    try {
      const payload = {
        ...editForm,
        address: {
          streetAddress: editForm.addressStreetAddress?.trim() || '',
        },
        addressStreetAddress: editForm.addressStreetAddress?.trim() || '',
      }
      await usersApi.update(uuid, payload)
      setEditProfileOpen(false)
      setActionMsg('Profile updated successfully.')
      setTimeout(() => setActionMsg(''), 4000)
      loadUserData()
    } catch (err) {
      setError(err.message || 'Failed to update profile')
      setTimeout(() => setError(''), 4000)
    }
  }

  // Unassign Role
  function handleRevokeRole(roleUuid) {
    setConfirmAction({
      title: lang === 'km' ? 'តើអ្នកប្រាកដជាចង់ដកតួនាទីនេះ?' : 'Remove Role?',
      description: lang === 'km'
        ? 'តើអ្នកប្រាកដជាចង់ដកតួនាទីនេះចេញពីអ្នកប្រើប្រាស់មែនទេ?'
        : 'Are you sure you want to remove this role from the user?',
      actionLabel: lang === 'km' ? 'ដកតួនាទី' : 'Remove Role',
      onConfirm: async () => {
        try {
          await usersApi.revokeRole(uuid, roleUuid)
          setActionMsg(lang === 'km' ? 'បានដកតួនាទីដោយជោគជ័យ។' : 'Role revoked.')
          setTimeout(() => setActionMsg(''), 4000)
          loadUserData()
        } catch (err) {
          setError(err.message || 'Failed to revoke role')
          setTimeout(() => setError(''), 4000)
        }
      }
    })
  }

  // Assign Role
  async function handleAssignRole(e) {
    e.preventDefault()
    if (!selectedRoleUuid) return
    try {
      await usersApi.assignRoles(uuid, [selectedRoleUuid])
      setAssignRoleOpen(false)
      setSelectedRoleUuid('')
      setActionMsg('Role assigned successfully.')
      setTimeout(() => setActionMsg(''), 4000)
      loadUserData()
    } catch (err) {
      setError(err.message || 'Failed to assign role')
      setTimeout(() => setError(''), 4000)
    }
  }

  // Grant Direct Permission
  async function handleGrantPermission(e) {
    e.preventDefault()
    if (!selectedPermUuid) return
    try {
      await usersApi.grantPermissions(uuid, [selectedPermUuid])
      setGrantPermOpen(false)
      setSelectedPermUuid('')
      setActionMsg('Direct permission granted.')
      setTimeout(() => setActionMsg(''), 4000)
      loadUserData()
    } catch (err) {
      setError(err.message || 'Failed to grant permission')
      setTimeout(() => setError(''), 4000)
    }
  }

  // Revoke Direct Permission
  function handleRevokePermission(permUuid) {
    setConfirmAction({
      title: lang === 'km' ? 'តើអ្នកប្រាកដជាចង់ដកសិទ្ធិនេះ?' : 'Revoke Permission?',
      description: lang === 'km'
        ? 'តើអ្នកប្រាកដជាចង់ដកសិទ្ធិផ្ទាល់នេះចេញពីអ្នកប្រើប្រាស់មែនទេ?'
        : 'Are you sure you want to revoke this direct permission?',
      actionLabel: lang === 'km' ? 'ដកសិទ្ធិ' : 'Revoke Permission',
      onConfirm: async () => {
        try {
          await usersApi.revokePermission(uuid, permUuid)
          setActionMsg(lang === 'km' ? 'បានដកសិទ្ធិដោយជោគជ័យ។' : 'Permission revoked.')
          setTimeout(() => setActionMsg(''), 4000)
          loadUserData()
        } catch (err) {
          setError(err.message || 'Failed to revoke permission')
          setTimeout(() => setError(''), 4000)
        }
      }
    })
  }

  // Reset Password
  async function handleResetPassword(e) {
    e.preventDefault()
    setPwdError('')
    if (newPassword !== confirmPassword) {
      setPwdError('Passwords do not match')
      return
    }
    try {
      await usersApi.resetPassword(uuid, newPassword, confirmPassword)
      setPwdModalOpen(false)
      setNewPassword('')
      setConfirmPassword('')
      setPwdError('')
      setActionMsg('Password reset successfully.')
      setTimeout(() => setActionMsg(''), 4000)
    } catch (err) {
      setPwdError(err.message || 'Failed to reset password')
    }
  }

  // Toggle user state
  async function handleToggleStatus() {
    try {
      if (user.enabled) await usersApi.disable(uuid)
      else await usersApi.enable(uuid)
      loadUserData()
    } catch (err) {
      setError(err.message || 'Failed to change user status')
      setTimeout(() => setError(''), 4000)
    }
  }

  // Terminate device session
  async function handleTerminateSession(sessionId) {
    try {
      try {
        await usersApi.terminateSession(uuid, sessionId)
      } catch {
        await adminSessionsApi.terminate(sessionId)
      }
      setUserSessions(s => s.filter(x => x.id !== sessionId))
      setActionMsg('Device session terminated.')
      setTimeout(() => setActionMsg(''), 4000)
    } catch (err) {
      setError(err.message || 'Failed to terminate session')
      setTimeout(() => setError(''), 4000)
    }
  }

  function formatDate(isoStr) {
    if (!isoStr) return '—'
    try {
      return new Date(isoStr).toLocaleDateString(lang === 'km' ? 'km-KH' : 'en-GB', { day: 'numeric', month: 'short', year: 'numeric' })
    } catch {
      return isoStr
    }
  }

  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-7 w-7 animate-spin rounded-full border-2 border-blue-600 border-t-transparent" />
      </div>
    )
  }

  if (error || !user) {
    return (
      <div className="p-8 max-w-4xl mx-auto space-y-4">
        <div className="rounded-lg bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-900 p-4 text-sm text-red-600 dark:text-red-400">
          {error || 'User not found.'}
        </div>
        <Button variant="outline" onClick={() => navigate('/admin/users')}>
          &larr; Back to users
        </Button>
      </div>
    )
  }

  const displayName = [user.givenName, user.familyName].filter(Boolean).join(' ') || user.name || user.username
  const initials = getMainNameInitials(user, displayName)
  const assignedRolesList = authorities.roles || user.roles || []
  const directPermissionsList = authorities.directPermissions || authorities.permissions || []
  const rawEffective = authorities.effectivePermissions || []
  const effectiveList = rawEffective.length > 0
    ? rawEffective.map(p => (typeof p === 'string' ? { name: p } : p))
    : directPermissionsList

  // Filter effective permissions
  const effectivePerms = effectiveList.filter(p => {
    const name = p.name || `${p.resource}:${p.action}` || String(p)
    return name.toLowerCase().includes(permFilter.toLowerCase())
  })

  const paginatedHistory = useMemo(() => {
    const start = (historyPage - 1) * historyPageSize
    return (loginHistory || []).slice(start, start + historyPageSize)
  }, [loginHistory, historyPage, historyPageSize])

  return (
    <div className="min-h-full bg-background p-6 md:p-8">
      <div className="mx-auto max-w-6xl space-y-6">

        {/* Top Header matching screenshot 2 */}
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-2 border-b border-border/60">
          <div className="flex items-center gap-4">
            <UserAvatar
              user={user}
              src={user.pictureUrl}
              className="h-14 w-14 shrink-0 border border-border/60 shadow-xs"
              textClassName="text-base font-bold"
            />
            <div className="space-y-1">
              <div className="flex items-center gap-2.5 flex-wrap">
                <h1 className="text-xl sm:text-2xl font-bold tracking-tight text-foreground">
                  {displayName}
                </h1>
                <span className="inline-flex items-center gap-1.5 text-xs font-medium text-emerald-600 dark:text-emerald-400">
                  <span className="h-1.5 w-1.5 rounded-full bg-emerald-500"></span>
                  {user.enabled !== false ? 'Active' : 'Disabled'}
                </span>
                {user.mustChangePassword && (
                  <Badge variant="outline" className="border-amber-200 bg-amber-50 text-amber-700 dark:border-amber-800 dark:bg-amber-950/40 dark:text-amber-300 text-xs font-normal">
                    Must change password
                  </Badge>
                )}
              </div>
              <p className="text-xs text-muted-foreground font-mono">
                @{user.username} &bull; {user.email || `${user.username}@istad.local`}
              </p>
            </div>
          </div>

          {/* Manage Dropdown */}
          <div className="flex items-center gap-2">
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="outline" size="sm" className="gap-1.5 text-xs font-medium border-border cursor-pointer shadow-xs">
                  {tr ? tr('userDetail.manage') : 'Manage'} <ChevronDown size={14} className="text-muted-foreground" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-48 text-xs">
                <DropdownMenuLabel>User Actions</DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={() => setPwdModalOpen(true)} className="gap-2 cursor-pointer">
                  <KeyIcon size={14} /> {tr ? tr('userDetail.resetPassword') : 'Reset password'}
                </DropdownMenuItem>
                <DropdownMenuItem onClick={handleToggleStatus} className="gap-2 cursor-pointer">
                  <Lock size={14} /> {user.enabled !== false ? (tr ? tr('userDetail.disableUser') : 'Disable user') : (tr ? tr('userDetail.enableUser') : 'Enable user')}
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem
                  onClick={() => {
                    setConfirmAction({
                      title: lang === 'km' ? 'តើអ្នកប្រាកដជាចង់លុបអ្នកប្រើប្រាស់នេះ?' : 'Delete User?',
                      description: lang === 'km'
                        ? `តើអ្នកពិតជាចង់លុបអ្នកប្រើប្រាស់ @${user.username} នេះជាអចិន្ត្រៃយ៍មែនទេ? សកម្មភាពនេះមិនអាចត្រឡប់វិញបានទេ។`
                        : `Are you sure you want to permanently delete user @${user.username}? This action cannot be undone.`,
                      actionLabel: lang === 'km' ? 'លុបអ្នកប្រើប្រាស់' : 'Delete User',
                      onConfirm: async () => {
                        try {
                          await usersApi.delete(uuid)
                          navigate('/admin/users')
                        } catch (err) {
                          setError(err.message || 'Failed to delete user')
                        }
                      }
                    })
                  }}
                  className="gap-2 text-rose-600 focus:text-rose-600 cursor-pointer"
                >
                  <UserX size={14} /> {tr ? tr('userDetail.deleteUser') : 'Delete user'}
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>

        {actionMsg && (
          <div className="flex items-center gap-2 rounded-lg border border-emerald-200 bg-emerald-50/70 p-3 text-xs text-emerald-800 dark:border-emerald-800 dark:bg-emerald-950/30 dark:text-emerald-300">
            <CheckCircle2 size={15} /> {actionMsg}
          </div>
        )}

        {error && (
          <div className="flex items-center gap-2 rounded-lg border border-red-200 bg-red-50/70 p-3 text-xs text-red-700 dark:border-red-900 dark:bg-red-950/40 dark:text-red-300">
            <AlertCircle size={15} /> {error}
          </div>
        )}

        {/* 3 Metric Stat Tiles matching screenshot 2 (Single segmented container with divide-x) */}
        <div className="rounded-xl border border-border bg-card shadow-xs grid grid-cols-1 divide-y sm:grid-cols-3 sm:divide-y-0 sm:divide-x divide-border">
          <div className="p-5 space-y-1">
            <p className="text-xs text-muted-foreground font-medium">
              {tr ? tr('userDetail.lastSignIn') : 'Last sign-in'}
            </p>
            <p className="text-xl font-bold tracking-tight text-foreground">
              {user.lastLoginAt ? formatDate(user.lastLoginAt) : '—'}
            </p>
          </div>

          <div className="p-5 space-y-1">
            <p className="text-xs text-muted-foreground font-medium">
              {tr ? tr('userDetail.created') : 'Created'}
            </p>
            <p className="text-xl font-bold tracking-tight text-foreground">
              {formatDate(user.createdAt)}
            </p>
          </div>

          <div className="p-5 space-y-1">
            <p className="text-xs text-muted-foreground font-medium">
              {tr ? tr('userDetail.roles') : 'Roles'}
            </p>
            <p className="text-xl font-bold tracking-tight text-foreground">
              {assignedRolesList.length}
            </p>
            {assignedRolesList.length > 0 && (
              <p className="text-[11px] font-semibold text-muted-foreground uppercase tracking-wider">
                {assignedRolesList[0]?.name || assignedRolesList[0]}
              </p>
            )}
          </div>
        </div>

        {/* User Workspace Tabs matching screenshots 2, 3, 4, 5 */}
        <Tabs defaultValue="profile" className="w-full">
          <TabsList className="bg-transparent border-b border-border w-full justify-start rounded-none h-auto p-0 gap-8">
            <TabsTrigger
              value="profile"
              className="rounded-none border-b-2 border-transparent px-1 pb-3 pt-1 text-xs font-semibold data-[state=active]:border-foreground data-[state=active]:text-foreground data-[state=active]:bg-transparent shadow-none transition-none cursor-pointer"
            >
              {tr ? tr('userDetail.profileTab') : 'Profile'}
            </TabsTrigger>
            <TabsTrigger
              value="roles-permissions"
              className="rounded-none border-b-2 border-transparent px-1 pb-3 pt-1 text-xs font-semibold data-[state=active]:border-foreground data-[state=active]:text-foreground data-[state=active]:bg-transparent shadow-none transition-none cursor-pointer"
            >
              {tr ? tr('userDetail.rolesTab') : 'Roles & permissions'}
            </TabsTrigger>
            <TabsTrigger
              value="security"
              className="rounded-none border-b-2 border-transparent px-1 pb-3 pt-1 text-xs font-semibold data-[state=active]:border-foreground data-[state=active]:text-foreground data-[state=active]:bg-transparent shadow-none transition-none cursor-pointer"
            >
              {tr ? tr('userDetail.securityTab') : 'Security'}
            </TabsTrigger>
            <TabsTrigger
              value="linked-accounts"
              className="rounded-none border-b-2 border-transparent px-1 pb-3 pt-1 text-xs font-semibold data-[state=active]:border-foreground data-[state=active]:text-foreground data-[state=active]:bg-transparent shadow-none transition-none cursor-pointer"
            >
              {tr ? tr('userDetail.linkedTab') : 'Linked accounts'}
            </TabsTrigger>
          </TabsList>

          {/* TAB 1: Profile matching screenshot 2 */}
          <TabsContent value="profile" className="pt-6">
            <div className="rounded-xl border border-border bg-card shadow-xs">
              <div className="flex items-start justify-between p-6 pb-5 border-b border-border/60">
                <div>
                  <h2 className="text-sm font-semibold text-foreground">
                    {tr ? tr('userDetail.profileHeading') : 'Profile'}
                  </h2>
                  <p className="text-xs text-muted-foreground mt-0.5">
                    {tr ? tr('userDetail.profileSubheading') : 'OpenID Connect profile shared with applications.'}
                  </p>
                </div>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setEditProfileOpen(true)}
                  className="h-8 px-3 text-xs font-medium cursor-pointer"
                >
                  {tr ? tr('userDetail.editProfile') : 'Edit'}
                </Button>
              </div>

              <div className="p-6 grid grid-cols-1 sm:grid-cols-2 gap-y-6 gap-x-12 text-xs">
                {/* Column 1 */}
                <div className="space-y-6">
                  <div>
                    <label className="text-xs text-muted-foreground block">{tr ? tr('profile.firstName') : 'First name'}</label>
                    <p className="font-semibold text-foreground text-xs mt-1">{user.givenName || '—'}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">{tr ? tr('profile.middleName') : 'Middle name'}</label>
                    <p className="font-semibold text-foreground text-xs mt-1">{user.middleName || '—'}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">{tr ? tr('profile.email') : 'Email'}</label>
                    <p className="font-semibold text-foreground text-xs mt-1 font-mono">{user.email || '—'}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">{tr ? tr('profile.gender') : 'Gender'}</label>
                    <p className="font-semibold text-foreground text-xs mt-1">{user.gender || '—'}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">{tr ? tr('profile.website') : 'Website'}</label>
                    <p className="font-semibold text-foreground text-xs mt-1">{user.websiteUrl || user.website || '—'}</p>
                  </div>
                </div>

                {/* Column 2 */}
                <div className="space-y-6">
                  <div>
                    <label className="text-xs text-muted-foreground block">{tr ? tr('profile.lastName') : 'Last name'}</label>
                    <p className="font-semibold text-foreground text-xs mt-1">{user.familyName || '—'}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">{tr ? tr('profile.nickname') : 'Nickname'}</label>
                    <p className="font-semibold text-foreground text-xs mt-1">{user.nickname || '—'}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">{tr ? tr('profile.phone') : 'Phone number'}</label>
                    <p className="font-semibold text-foreground text-xs mt-1 font-mono">{user.phoneNumber || '—'}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">{tr ? tr('profile.dob') : 'Date of birth'}</label>
                    <p className="font-semibold text-foreground text-xs mt-1">{formatDate(user.birthdate)}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">{tr ? tr('profile.address') : 'Address'}</label>
                    <p className="font-semibold text-foreground text-xs mt-1">
                      {user.addressStreetAddress || (typeof user.address === 'string' ? user.address : [user.address?.streetAddress, user.address?.locality, user.address?.country].filter(Boolean).join(', ')) || '—'}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </TabsContent>

          {/* TAB 2: Roles & Permissions matching screenshot 3 */}
          <TabsContent value="roles-permissions" className="pt-6 space-y-6">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 items-start">

              {/* Left Column: Roles Card */}
              <div className="rounded-xl border border-border bg-card p-6 shadow-xs min-h-[220px] flex flex-col justify-between">
                <div>
                  <div className="flex items-start justify-between pb-4">
                    <div>
                      <h2 className="text-sm font-semibold text-foreground">
                        {tr ? tr('userDetail.assignedRoles') : 'Roles'}
                      </h2>
                      <p className="text-xs text-muted-foreground mt-0.5">
                        {tr ? tr('userDetail.assignedRolesDesc') : 'Each role gives a set of permissions.'}
                      </p>
                    </div>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setAssignRoleOpen(true)}
                      className="h-8 gap-1.5 text-xs font-medium cursor-pointer"
                    >
                      <Plus size={13} /> {tr ? tr('userDetail.assignRole') : 'Assign'}
                    </Button>
                  </div>

                  {assignedRolesList.length === 0 ? (
                    <div className="flex flex-col items-center justify-center p-8 text-center border border-dashed rounded-lg">
                      <Inbox size={26} className="text-muted-foreground/40 mb-1" />
                      <p className="text-xs text-muted-foreground">No roles assigned.</p>
                    </div>
                  ) : (
                    <div className="space-y-2">
                      {assignedRolesList.map((r, i) => {
                        const rName = r?.name || r
                        const rUuid = r?.uuid || r
                        return (
                          <div
                            key={i}
                            className="flex items-center justify-between rounded-lg border border-border bg-card p-3 shadow-xs"
                          >
                            <div className="flex items-center gap-3">
                              <Shield size={16} className="text-blue-600 dark:text-blue-400 shrink-0" />
                              <div>
                                <p className="text-xs font-semibold text-foreground uppercase">{rName}</p>
                                <p className="text-[11px] text-muted-foreground">No expiry</p>
                              </div>
                            </div>
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => handleRevokeRole(rUuid)}
                              className="h-7 w-7 text-muted-foreground hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-950/40 cursor-pointer"
                            >
                              <X size={14} />
                            </Button>
                          </div>
                        )
                      })}
                    </div>
                  )}
                </div>
              </div>

              {/* Right Column: Direct Permissions Card */}
              <div className="rounded-xl border border-border bg-card p-6 shadow-xs min-h-[220px] flex flex-col justify-between">
                <div>
                  <div className="flex items-start justify-between pb-4">
                    <div>
                      <h2 className="text-sm font-semibold text-foreground">
                        {tr ? tr('userDetail.directPerms') : 'Direct permissions'}
                      </h2>
                      <p className="text-xs text-muted-foreground mt-0.5">
                        {tr ? tr('userDetail.directPermsDesc') : 'Given to this user only, outside their roles.'}
                      </p>
                    </div>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setGrantPermOpen(true)}
                      className="h-8 gap-1.5 text-xs font-medium cursor-pointer"
                    >
                      <Plus size={13} /> {tr ? tr('userDetail.grantPermission') : 'Grant'}
                    </Button>
                  </div>

                  {directPermissionsList.length === 0 ? (
                    <div className="flex flex-col items-center justify-center p-8 text-center">
                      <div className="w-10 h-10 rounded-full border border-border flex items-center justify-center text-muted-foreground/50 mb-2">
                        <Inbox size={18} strokeWidth={1.5} />
                      </div>
                      <p className="text-xs font-medium text-muted-foreground">No direct permissions</p>
                    </div>
                  ) : (
                    <div className="space-y-2">
                      {directPermissionsList.map((p, i) => {
                        const pName = typeof p === 'string' ? p : (p?.name || (p?.resource ? `${p.resource}:${p.action}` : 'Permission'))
                        const pUuid = p?.uuid || pName
                        return (
                          <div
                            key={i}
                            className="flex items-center justify-between rounded-lg border border-border bg-card p-3 shadow-xs"
                          >
                            <div className="flex items-center gap-2.5">
                              <KeyIcon size={15} className="text-emerald-600 dark:text-emerald-400 shrink-0" />
                              <span className="text-xs font-mono font-medium">{pName}</span>
                            </div>
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => handleRevokePermission(pUuid)}
                              className="h-7 w-7 text-muted-foreground hover:text-red-600 cursor-pointer"
                            >
                              <X size={14} />
                            </Button>
                          </div>
                        )
                      })}
                    </div>
                  )}
                </div>
              </div>

            </div>

            {/* Bottom Card: Effective Permissions Filter Table */}
            <div className="rounded-xl border border-border bg-card p-6 shadow-xs space-y-4">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-3 border-b border-border/60 gap-3">
                <div>
                  <h2 className="text-sm font-semibold text-foreground">
                    {tr ? tr('userDetail.effectivePerms') : 'Effective permissions'} ({effectivePerms.length})
                  </h2>
                  <p className="text-xs text-muted-foreground mt-0.5">
                    {tr ? tr('userDetail.effectivePermsDesc') : 'What the user can do now: roles, plus grants, minus denies.'}
                  </p>
                </div>
                <div className="relative w-full sm:w-64">
                  <Input
                    placeholder="Filter..."
                    value={permFilter}
                    onChange={e => setPermFilter(e.target.value)}
                    className="h-8 text-xs bg-muted/20 border-border"
                  />
                </div>
              </div>

              {effectivePerms.length === 0 ? (
                <p className="text-sm text-muted-foreground py-2">—</p>
              ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-2">
                  {effectivePerms.map((p, i) => {
                    const name = p.name || `${p.resource}:${p.action}`
                    return (
                      <div
                        key={i}
                        className="flex items-center gap-2 rounded-md border border-border bg-muted/30 px-2.5 py-1.5 text-xs font-mono text-foreground"
                      >
                        <span className="h-1.5 w-1.5 rounded-full bg-emerald-500"></span>
                        <span className="truncate">{name}</span>
                      </div>
                    )
                  })}
                </div>
              )}
            </div>
          </TabsContent>

          {/* TAB 3: Security matching screenshot 4 */}
          <TabsContent value="security" className="pt-6 space-y-6">

            {/* Section 1: 2-step verification */}
            <div className="grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
              <div className="md:col-span-4 space-y-1">
                <h3 className="text-sm font-semibold text-foreground">
                  {tr ? tr('userDetail.twoFactor') : '2-step verification'}
                </h3>
                <p className="text-xs text-muted-foreground leading-relaxed">
                  {tr ? tr('userDetail.twoFactorDesc') : 'Reset it when the user lost their phone and recovery codes.'}
                </p>
              </div>

              <div className="md:col-span-8 rounded-xl border border-border bg-card p-5 shadow-xs flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-full border border-border flex items-center justify-center text-muted-foreground">
                    <EyeOff size={15} />
                  </div>
                  <div>
                    <span className="text-xs font-semibold px-2 py-0.5 rounded bg-muted text-muted-foreground inline-block">
                      {user.twoFactorEnabled
                        ? (tr ? tr('userDetail.on') : 'On')
                        : (tr ? tr('userDetail.off') : 'Off')}
                    </span>
                    <p className="text-xs text-muted-foreground mt-1">
                      {user.twoFactorEnabled
                        ? (tr ? tr('userDetail.twoFactorOn') : 'Enforced with two-factor verification.')
                        : (tr ? tr('userDetail.twoFactorOff') : 'The user signs in with a password only.')}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* Section 2: Signed-in devices */}
            <div className="grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
              <div className="md:col-span-4 space-y-1">
                <h3 className="text-sm font-semibold text-foreground">
                  {tr ? tr('userDetail.signedInDevices') : 'Signed-in devices'}
                </h3>
                <p className="text-xs text-muted-foreground leading-relaxed">
                  {tr ? tr('userDetail.signedInDevicesDesc') : 'Browsers where the user is signed in now.'}
                </p>
              </div>

              <div className="md:col-span-8 rounded-xl border border-border bg-card p-6 shadow-xs">
                {userSessions.length === 0 ? (
                  <div className="flex flex-col items-center justify-center text-center py-6">
                    <div className="w-10 h-10 rounded-full border border-border flex items-center justify-center text-muted-foreground/50 mb-2">
                      <Monitor size={18} strokeWidth={1.5} />
                    </div>
                    <p className="text-xs font-medium text-muted-foreground">
                      {tr ? tr('userDetail.noDevices') : 'No signed-in devices.'}
                    </p>
                  </div>
                ) : (
                  <div className="space-y-3">
                    {userSessions.map(sess => (
                      <div key={sess.id} className="flex items-center justify-between p-3.5 rounded-lg border border-border bg-card">
                        <div className="flex items-center gap-3">
                          <div className="w-8 h-8 rounded-full bg-muted flex items-center justify-center text-muted-foreground">
                            <Monitor size={16} />
                          </div>
                          <div>
                            <p className="font-semibold text-xs text-foreground">
                              {sess.browser || 'Browser'} on {sess.device || 'Desktop'}
                            </p>
                            <p className="text-[11px] text-muted-foreground font-mono mt-0.5">
                              {sess.ipAddress || '127.0.0.1'} &bull; {sess.location || 'Local'}
                            </p>
                          </div>
                        </div>
                        <Button
                          variant="outline"
                          size="sm"
                          className="text-destructive hover:bg-destructive/10 h-7 text-xs cursor-pointer"
                          onClick={() => handleTerminateSession(sess.id)}
                        >
                          {tr ? tr('userDetail.terminate') : 'Terminate'}
                        </Button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>

            {/* Section 3: Sign-in attempts */}
            <div className="grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
              <div className="md:col-span-4 space-y-1">
                <h3 className="text-sm font-semibold text-foreground">
                  {tr ? tr('userDetail.signInAttempts') : 'Sign-in attempts'}
                </h3>
                <p className="text-xs text-muted-foreground leading-relaxed">
                  {tr ? tr('userDetail.signInAttemptsDesc') : 'Successful and failed, newest first.'}
                </p>
              </div>

              <div className="md:col-span-8 rounded-xl border border-border bg-card shadow-xs overflow-hidden">
                <div className="overflow-x-auto">
                  <table className="w-full text-left text-xs">
                    <thead className="border-b border-border bg-muted/40 text-[11px] font-semibold text-muted-foreground">
                      <tr>
                        <th className="py-3 px-4 font-semibold text-xs text-foreground whitespace-nowrap">
                          {tr ? tr('userDetail.when') : 'When'}
                        </th>
                        <th className="py-3 px-4 font-semibold text-xs text-foreground">
                          {tr ? tr('userDetail.result') : 'Result'}
                        </th>
                        <th className="py-3 px-4 font-semibold text-xs text-foreground">
                          {tr ? tr('userDetail.method') : 'Method'}
                        </th>
                        <th className="py-3 px-4 font-semibold text-xs text-foreground">
                          {tr ? tr('userDetail.device') : 'Device'}
                        </th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-border">
                      {loginHistory.length === 0 ? (
                        <tr>
                          <td colSpan={4} className="py-10 text-center">
                            <div className="flex flex-col items-center justify-center">
                              <div className="w-10 h-10 rounded-full border border-border flex items-center justify-center text-muted-foreground/50 mb-2">
                                <Clock size={18} strokeWidth={1.5} />
                              </div>
                              <p className="text-xs font-medium text-muted-foreground">
                                {tr ? tr('userDetail.noSignIns') : 'No sign-ins yet.'}
                              </p>
                            </div>
                          </td>
                        </tr>
                      ) : (
                        paginatedHistory.map((item, idx) => {
                          const isSuccess = item.success !== false
                          const parsed = parseUserAgent(item.userAgent || '')
                          const deviceName = parsed.device || 'Desktop Workstation'
                          const dateStr = item.occurredAt
                          const formattedDate = dateStr
                            ? new Date(dateStr).toLocaleString(lang === 'km' ? 'km-KH' : 'en-GB', {
                                day: '2-digit',
                                month: 'short',
                                year: 'numeric',
                                hour: '2-digit',
                                minute: '2-digit',
                                hour12: false,
                              })
                            : 'Recent'

                          return (
                            <tr key={item.id || idx} className="hover:bg-muted/30 transition-colors">
                              <td className="py-3 px-4 text-muted-foreground whitespace-nowrap font-mono text-[11px]">
                                {formattedDate}
                              </td>
                              <td className="py-3 px-4 whitespace-nowrap">
                                <Badge
                                  variant="outline"
                                  className={`text-[10px] font-medium ${
                                    isSuccess
                                      ? 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-950 dark:text-emerald-300'
                                      : 'bg-rose-50 text-rose-700 border-rose-200 dark:bg-rose-950 dark:text-rose-300'
                                  }`}
                                >
                                  {isSuccess
                                    ? (tr ? tr('userDetail.signedIn') : 'Signed in')
                                    : (tr ? tr('userDetail.failed') : 'Failed')}
                                </Badge>
                              </td>
                              <td className="py-3 px-4 text-xs font-medium text-foreground">
                                {item.method || 'Password'}
                              </td>
                              <td className="py-3 px-4">
                                <p className="font-semibold text-foreground text-xs">{deviceName}</p>
                                <p className="font-mono text-[11px] text-muted-foreground">
                                  {item.ipAddress || '127.0.0.1'} {item.location ? `• ${item.location}` : ''}
                                </p>
                              </td>
                            </tr>
                          )
                        })
                      )}
                    </tbody>
                  </table>
                </div>

                <TablePagination
                  totalItems={loginHistory.length}
                  page={historyPage}
                  pageSize={historyPageSize}
                  onPageChange={setHistoryPage}
                  onPageSizeChange={setHistoryPageSize}
                  pageSizeOptions={[5, 10, 20, 50]}
                />
              </div>
            </div>

          </TabsContent>

          {/* TAB 4: Linked Accounts matching screenshot 5 */}
          <TabsContent value="linked-accounts" className="pt-6">
            <div className="rounded-xl border border-border bg-card shadow-xs">
              <div className="p-6 border-b border-border/60">
                <h2 className="text-sm font-semibold text-foreground">
                  {tr ? tr('userDetail.connectedProviders') : 'Connected sign-in providers'}
                </h2>
              </div>
              <div className="p-8">
                {identities.length === 0 ? (
                  <div className="flex flex-col items-center justify-center text-center py-6">
                    <div className="w-10 h-10 rounded-full border border-border flex items-center justify-center text-muted-foreground/50 mb-3">
                      <Link2 size={18} strokeWidth={1.5} />
                    </div>
                    <p className="text-xs font-semibold text-foreground">
                      {tr ? tr('userDetail.noLinked') : 'No linked accounts'}
                    </p>
                    <p className="text-xs text-muted-foreground mt-1 max-w-sm">
                      {tr ? tr('userDetail.noLinkedDesc') : 'Sign in with Google or another provider using your ISTAD email to link it.'}
                    </p>
                  </div>
                ) : (
                  <div className="space-y-3">
                    {identities.map((id, idx) => (
                      <div
                        key={id.id || idx}
                        className="flex items-center justify-between rounded-lg border border-border bg-card p-3 shadow-xs"
                      >
                        <div className="flex items-center gap-3">
                          <span className="font-semibold text-xs uppercase px-2 py-0.5 rounded bg-muted text-foreground">
                            {id.provider || 'Provider'}
                          </span>
                          <span className="text-xs font-mono text-muted-foreground">
                            {id.providerUserId || id.email || id.username || 'Linked'}
                          </span>
                        </div>
                        <Badge variant="outline" className="text-[10px] text-emerald-600 border-emerald-300">
                          {tr ? tr('userDetail.connected') : 'Connected'}
                        </Badge>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </TabsContent>

        </Tabs>
      </div>

      {/* Edit Profile Dialog */}
      <Dialog open={editProfileOpen} onOpenChange={setEditProfileOpen}>
        <DialogContent className="sm:max-w-lg">
          <DialogHeader>
            <DialogTitle className="text-base font-semibold">{tr ? tr('userDetail.editProfile') : 'Edit'} {tr ? tr('profile.title') : 'Profile'}</DialogTitle>
            <DialogDescription className="text-xs">
              Update directory profile details for @{user.username}.
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={handleSaveProfile} className="space-y-4 pt-2 text-xs">
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1">
                <Label>{tr ? tr('profile.firstName') : 'First name'}</Label>
                <Input
                  value={editForm.givenName}
                  onChange={e => setEditForm({ ...editForm, givenName: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
              <div className="space-y-1">
                <Label>{tr ? tr('profile.lastName') : 'Last name'}</Label>
                <Input
                  value={editForm.familyName}
                  onChange={e => setEditForm({ ...editForm, familyName: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
              <div className="space-y-1">
                <Label>{tr ? tr('profile.middleName') : 'Middle name'}</Label>
                <Input
                  value={editForm.middleName}
                  onChange={e => setEditForm({ ...editForm, middleName: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
              <div className="space-y-1">
                <Label>{tr ? tr('profile.nickname') : 'Nickname'}</Label>
                <Input
                  value={editForm.nickname}
                  onChange={e => setEditForm({ ...editForm, nickname: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
              <div className="space-y-1">
                <Label>{tr ? tr('profile.phone') : 'Phone number'}</Label>
                <Input
                  value={editForm.phoneNumber}
                  onChange={e => setEditForm({ ...editForm, phoneNumber: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
              <div className="space-y-1">
                <Label>{tr ? tr('profile.gender') : 'Gender'}</Label>
                <Input
                  value={editForm.gender}
                  onChange={e => setEditForm({ ...editForm, gender: e.target.value })}
                  className="h-8 text-xs"
                  placeholder="e.g. Female / Male"
                />
              </div>
              <div className="space-y-1">
                <Label>{tr ? tr('profile.dob') : 'Date of birth'}</Label>
                <Input
                  type="date"
                  value={editForm.birthdate}
                  onChange={e => setEditForm({ ...editForm, birthdate: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
              <div className="space-y-1">
                <Label>{tr ? tr('profile.website') : 'Website'}</Label>
                <Input
                  value={editForm.websiteUrl}
                  onChange={e => setEditForm({ ...editForm, websiteUrl: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
            </div>
            <div className="space-y-1">
              <Label>{tr ? tr('profile.address') : 'Address'}</Label>
              <Input
                value={editForm.addressStreetAddress}
                onChange={e => setEditForm({ ...editForm, addressStreetAddress: e.target.value })}
                className="h-8 text-xs"
              />
            </div>
            <DialogFooter className="pt-2">
              <Button type="button" variant="outline" size="sm" onClick={() => setEditProfileOpen(false)}>
                {tr ? tr('common.cancel') : 'Cancel'}
              </Button>
              <Button type="submit" size="sm">
                {tr ? tr('common.save') : 'Save Changes'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Assign Role Dialog */}
      <Dialog
        open={assignRoleOpen}
        onOpenChange={(open) => {
          setAssignRoleOpen(open)
          if (!open) setSelectedRoleUuid('')
        }}
      >
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="text-base font-semibold text-foreground flex items-center gap-2">
              <Shield className="h-4 w-4 text-primary" />
              <span>{tr ? tr('userDetail.assignRole') : 'Assign Role'}</span>
            </DialogTitle>
            <DialogDescription className="text-xs text-muted-foreground">
              Select an RBAC role to grant to @{user.username}.
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={handleAssignRole} className="space-y-4 pt-2">
            <div className="space-y-1.5">
              <Label className="text-xs font-medium text-foreground">{tr ? tr('userDetail.assignRole') : 'Select Role'}</Label>
              <Select value={selectedRoleUuid || undefined} onValueChange={setSelectedRoleUuid}>
                <SelectTrigger className="w-full h-9 text-xs rounded-lg border-border bg-background hover:bg-muted/30 focus:ring-2 focus:ring-primary/20 transition-colors">
                  <SelectValue placeholder="Choose a role to assign..." />
                </SelectTrigger>
                <SelectContent className="max-h-60">
                  {allRoles.filter(r => r.uuid).map(r => (
                    <SelectItem key={r.uuid} value={r.uuid} className="text-xs py-2 cursor-pointer">
                      <div className="flex flex-col gap-0.5 text-left">
                        <span className="font-semibold text-foreground">{r.name}</span>
                        {r.description && (
                          <span className="text-[11px] text-muted-foreground line-clamp-1">{r.description}</span>
                        )}
                      </div>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <DialogFooter className="pt-2">
              <Button type="button" variant="outline" size="sm" onClick={() => setAssignRoleOpen(false)}>
                {tr ? tr('common.cancel') : 'Cancel'}
              </Button>
              <Button type="submit" size="sm" disabled={!selectedRoleUuid} className="bg-primary hover:bg-primary/90 text-primary-foreground font-medium">
                {tr ? tr('userDetail.assignRole') : 'Assign Role'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Grant Direct Permission Dialog */}
      <Dialog
        open={grantPermOpen}
        onOpenChange={(open) => {
          setGrantPermOpen(open)
          if (!open) setSelectedPermUuid('')
        }}
      >
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="text-base font-semibold text-foreground flex items-center gap-2">
              <KeyIcon className="h-4 w-4 text-primary" />
              <span>{tr ? tr('userDetail.grantPermission') : 'Grant Direct Permission'}</span>
            </DialogTitle>
            <DialogDescription className="text-xs text-muted-foreground">
              Grant a resource permission directly to @{user.username}.
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={handleGrantPermission} className="space-y-4 pt-2">
            <div className="space-y-1.5">
              <Label className="text-xs font-medium text-foreground">{tr ? tr('userDetail.grantPermission') : 'Select Permission'}</Label>
              <Select value={selectedPermUuid || undefined} onValueChange={setSelectedPermUuid}>
                <SelectTrigger className="w-full h-9 text-xs rounded-lg border-border bg-background hover:bg-muted/30 focus:ring-2 focus:ring-primary/20 transition-colors">
                  <SelectValue placeholder="Choose a permission to grant..." />
                </SelectTrigger>
                <SelectContent className="max-h-60">
                  {allPerms.filter(p => p.uuid).map(p => (
                    <SelectItem key={p.uuid} value={p.uuid} className="text-xs py-2 cursor-pointer">
                      <div className="flex flex-col gap-0.5 text-left">
                        <span className="font-semibold text-foreground">{p.name || `${p.resource}:${p.action}`}</span>
                        <span className="text-[11px] text-muted-foreground font-mono">{p.resource}:{p.action}</span>
                      </div>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <DialogFooter className="pt-2">
              <Button type="button" variant="outline" size="sm" onClick={() => setGrantPermOpen(false)}>
                {tr ? tr('common.cancel') : 'Cancel'}
              </Button>
              <Button type="submit" size="sm" disabled={!selectedPermUuid} className="bg-primary hover:bg-primary/90 text-primary-foreground font-medium">
                {tr ? tr('userDetail.grantPermission') : 'Grant Permission'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Reset Password Dialog */}
      <Dialog
        open={pwdModalOpen}
        onOpenChange={(open) => {
          setPwdModalOpen(open)
          if (!open) {
            setNewPassword('')
            setConfirmPassword('')
            setPwdError('')
          }
        }}
      >
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="text-base font-semibold text-foreground">{tr ? tr('userDetail.resetPassword') : 'Reset Password'}</DialogTitle>
            <DialogDescription className="text-xs text-muted-foreground">
              Enter a new temporary password for @{user.username}.
            </DialogDescription>
          </DialogHeader>

          {pwdError && (
            <div className="rounded-lg bg-rose-50 border border-rose-200 dark:bg-rose-950/40 dark:border-rose-900/50 p-2.5 text-xs text-rose-700 dark:text-rose-300 flex items-center gap-2">
              <AlertCircle size={14} className="shrink-0 text-rose-600" />
              <span>{pwdError}</span>
            </div>
          )}

          <form onSubmit={handleResetPassword} className="space-y-3.5 pt-1">
            <div className="space-y-1.5">
              <Label className="text-xs font-medium text-foreground">{lang === 'km' ? 'ពាក្យសម្ងាត់ថ្មី' : 'New Password'}</Label>
              <Input
                type="password"
                required
                minLength={8}
                value={newPassword}
                onChange={e => setNewPassword(e.target.value)}
                placeholder="At least 8 characters"
                className="h-9 text-xs"
              />
            </div>
            <div className="space-y-1.5">
              <Label className="text-xs font-medium text-foreground">{lang === 'km' ? 'បញ្ជាក់ពាក្យសម្ងាត់' : 'Confirm Password'}</Label>
              <Input
                type="password"
                required
                minLength={8}
                value={confirmPassword}
                onChange={e => setConfirmPassword(e.target.value)}
                placeholder="Re-enter new password"
                className="h-9 text-xs"
              />
            </div>
            <DialogFooter className="pt-2">
                <Button type="button" variant="outline" size="sm" onClick={() => setPwdModalOpen(false)}>
                {tr ? tr('common.cancel') : 'Cancel'}
              </Button>
              <Button type="submit" size="sm">
                {tr ? tr('common.save') : 'Save New Password'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Confirmation Alert Dialog */}
      <AlertDialog open={Boolean(confirmAction)} onOpenChange={open => !open && setConfirmAction(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>{confirmAction?.title}</AlertDialogTitle>
            <AlertDialogDescription>{confirmAction?.description}</AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>{tr ? tr('common.cancel') : (lang === 'km' ? 'បោះបង់' : 'Cancel')}</AlertDialogCancel>
            <AlertDialogAction
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
              onClick={() => {
                const action = confirmAction?.onConfirm
                setConfirmAction(null)
                if (action) action()
              }}
            >
              {confirmAction?.actionLabel || (lang === 'km' ? 'បញ្ជាក់' : 'Confirm')}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

    </div>
  )
}
