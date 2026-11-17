import { useEffect, useState } from 'react'
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
  Shield, Plus, X, Search, CheckCircle2,
  AlertCircle, ChevronDown, RefreshCw, Mail, Lock, UserX,
  Inbox, Monitor, Clock, Link2, EyeOff
} from 'lucide-react'
import KeyIcon from '@/components/icons/KeyIcon'
import { getMainNameInitials } from '@/components/common/UserAvatar'

export default function UserDetailPage() {
  const { uuid } = useParams()
  const navigate = useNavigate()
  const { setBreadcrumbSub } = useApp?.() || {}

  const [user, setUser] = useState(null)
  const [authorities, setAuthorities] = useState({ roles: [], permissions: [], directPermissions: [], effectivePermissions: [] })
  const [allRoles, setAllRoles] = useState([])
  const [allPerms, setAllPerms] = useState([])
  const [identities, setIdentities] = useState([])
  const [userSessions, setUserSessions] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [actionMsg, setActionMsg] = useState('')
  const [permFilter, setPermFilter] = useState('')

  // Modals state
  const [assignRoleOpen, setAssignRoleOpen] = useState(false)
  const [grantPermOpen, setGrantPermOpen] = useState(false)
  const [selectedRoleUuid, setSelectedRoleUuid] = useState('')
  const [selectedPermUuid, setSelectedPermUuid] = useState('')
  const [newPassword, setNewPassword] = useState('')
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
      adminSessionsApi.list().catch(() => [])
    ]).then(([uRes, authRes, rRes, pRes, iRes, sessRes]) => {
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
        setUserSessions(sessRes.value.filter(s => s.actor === uVal?.username || s.userUuid === uuid))
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
      await usersApi.update(uuid, editForm)
      setEditProfileOpen(false)
      setActionMsg('Profile updated successfully.')
      loadUserData()
    } catch (err) {
      alert(err.message)
    }
  }

  // Unassign Role
  async function handleRevokeRole(roleUuid) {
    if (!window.confirm('Remove this role from the user?')) return
    try {
      await usersApi.revokeRole(uuid, roleUuid)
      setActionMsg('Role revoked.')
      loadUserData()
    } catch (err) {
      alert(err.message)
    }
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
      loadUserData()
    } catch (err) {
      alert(err.message)
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
      loadUserData()
    } catch (err) {
      alert(err.message)
    }
  }

  // Revoke Direct Permission
  async function handleRevokePermission(permUuid) {
    if (!window.confirm('Revoke this direct permission?')) return
    try {
      await usersApi.revokePermission(uuid, permUuid)
      setActionMsg('Permission revoked.')
      loadUserData()
    } catch (err) {
      alert(err.message)
    }
  }

  // Reset Password
  async function handleResetPassword(e) {
    e.preventDefault()
    try {
      await usersApi.resetPassword(uuid, newPassword)
      setPwdModalOpen(false)
      setNewPassword('')
      setActionMsg('Password reset successfully.')
    } catch (err) {
      alert(err.message)
    }
  }

  // Toggle user state
  async function handleToggleStatus() {
    try {
      if (user.enabled) await usersApi.disable(uuid)
      else await usersApi.enable(uuid)
      loadUserData()
    } catch (err) {
      alert(err.message)
    }
  }

  // Terminate device session
  async function handleTerminateSession(sessionId) {
    try {
      await adminSessionsApi.terminate(sessionId)
      setUserSessions(s => s.filter(x => x.id !== sessionId))
      setActionMsg('Device session terminated.')
    } catch (err) {
      alert(err.message)
    }
  }

  function formatDate(isoStr) {
    if (!isoStr) return '—'
    try {
      return new Date(isoStr).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })
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

  return (
    <div className="min-h-full bg-background p-6 md:p-8">
      <div className="mx-auto max-w-6xl space-y-6">

        {/* Top Header matching screenshot 2 */}
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-2 border-b border-border/60">
          <div className="flex items-center gap-4">
            <Avatar className="h-14 w-14 border border-border bg-muted/60 text-base font-semibold">
              <AvatarFallback className="bg-slate-100 text-slate-800 dark:bg-slate-800 dark:text-slate-200">
                {initials}
              </AvatarFallback>
            </Avatar>
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
                  Manage <ChevronDown size={14} className="text-muted-foreground" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-48 text-xs">
                <DropdownMenuLabel>User Actions</DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={() => setPwdModalOpen(true)} className="gap-2 cursor-pointer">
                  <KeyIcon size={14} /> Reset password
                </DropdownMenuItem>
                <DropdownMenuItem onClick={handleToggleStatus} className="gap-2 cursor-pointer">
                  <Lock size={14} /> {user.enabled !== false ? 'Disable user' : 'Enable user'}
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem
                  onClick={async () => {
                    if (window.confirm(`Delete user @${user.username}?`)) {
                      await usersApi.delete(uuid)
                      navigate('/admin/users')
                    }
                  }}
                  className="gap-2 text-rose-600 focus:text-rose-600 cursor-pointer"
                >
                  <UserX size={14} /> Delete user
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

        {/* 3 Metric Stat Tiles matching screenshot 2 (Single segmented container with divide-x) */}
        <div className="rounded-xl border border-border bg-card shadow-xs grid grid-cols-1 divide-y sm:grid-cols-3 sm:divide-y-0 sm:divide-x divide-border">
          <div className="p-5 space-y-1">
            <p className="text-xs text-muted-foreground font-medium">Last sign-in</p>
            <p className="text-xl font-bold tracking-tight text-foreground">
              {user.lastLoginAt ? formatDate(user.lastLoginAt) : '—'}
            </p>
          </div>

          <div className="p-5 space-y-1">
            <p className="text-xs text-muted-foreground font-medium">Created</p>
            <p className="text-xl font-bold tracking-tight text-foreground">
              {formatDate(user.createdAt)}
            </p>
          </div>

          <div className="p-5 space-y-1">
            <p className="text-xs text-muted-foreground font-medium">Roles</p>
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
              Profile
            </TabsTrigger>
            <TabsTrigger
              value="roles-permissions"
              className="rounded-none border-b-2 border-transparent px-1 pb-3 pt-1 text-xs font-semibold data-[state=active]:border-foreground data-[state=active]:text-foreground data-[state=active]:bg-transparent shadow-none transition-none cursor-pointer"
            >
              Roles &amp; permissions
            </TabsTrigger>
            <TabsTrigger
              value="security"
              className="rounded-none border-b-2 border-transparent px-1 pb-3 pt-1 text-xs font-semibold data-[state=active]:border-foreground data-[state=active]:text-foreground data-[state=active]:bg-transparent shadow-none transition-none cursor-pointer"
            >
              Security
            </TabsTrigger>
            <TabsTrigger
              value="linked-accounts"
              className="rounded-none border-b-2 border-transparent px-1 pb-3 pt-1 text-xs font-semibold data-[state=active]:border-foreground data-[state=active]:text-foreground data-[state=active]:bg-transparent shadow-none transition-none cursor-pointer"
            >
              Linked accounts
            </TabsTrigger>
          </TabsList>

          {/* TAB 1: Profile matching screenshot 2 */}
          <TabsContent value="profile" className="pt-6">
            <div className="rounded-xl border border-border bg-card shadow-xs">
              <div className="flex items-start justify-between p-6 pb-5 border-b border-border/60">
                <div>
                  <h2 className="text-sm font-semibold text-foreground">Profile</h2>
                  <p className="text-xs text-muted-foreground mt-0.5">
                    OpenID Connect profile shared with applications.
                  </p>
                </div>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setEditProfileOpen(true)}
                  className="h-8 px-3 text-xs font-medium cursor-pointer"
                >
                  Edit
                </Button>
              </div>

              <div className="p-6 grid grid-cols-1 sm:grid-cols-2 gap-y-6 gap-x-12 text-xs">
                {/* Column 1 */}
                <div className="space-y-6">
                  <div>
                    <label className="text-xs text-muted-foreground block">First name</label>
                    <p className="font-semibold text-foreground text-xs mt-1">{user.givenName || '—'}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">Middle name</label>
                    <p className="font-semibold text-foreground text-xs mt-1">{user.middleName || '—'}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">Email</label>
                    <p className="font-semibold text-foreground text-xs mt-1 font-mono">{user.email || '—'}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">Gender</label>
                    <p className="font-semibold text-foreground text-xs mt-1">{user.gender || '—'}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">Website</label>
                    <p className="font-semibold text-foreground text-xs mt-1">{user.websiteUrl || user.website || '—'}</p>
                  </div>
                </div>

                {/* Column 2 */}
                <div className="space-y-6">
                  <div>
                    <label className="text-xs text-muted-foreground block">Last name</label>
                    <p className="font-semibold text-foreground text-xs mt-1">{user.familyName || '—'}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">Nickname</label>
                    <p className="font-semibold text-foreground text-xs mt-1">{user.nickname || '—'}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">Phone number</label>
                    <p className="font-semibold text-foreground text-xs mt-1 font-mono">{user.phoneNumber || '—'}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">Date of birth</label>
                    <p className="font-semibold text-foreground text-xs mt-1">{formatDate(user.birthdate)}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground block">Address</label>
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
                      <h2 className="text-sm font-semibold text-foreground">Roles</h2>
                      <p className="text-xs text-muted-foreground mt-0.5">
                        Each role gives a set of permissions.
                      </p>
                    </div>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setAssignRoleOpen(true)}
                      className="h-8 gap-1.5 text-xs font-medium cursor-pointer"
                    >
                      <Plus size={13} /> Assign
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
                      <h2 className="text-sm font-semibold text-foreground">Direct permissions</h2>
                      <p className="text-xs text-muted-foreground mt-0.5">
                        Given to this user only, outside their roles.
                      </p>
                    </div>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setGrantPermOpen(true)}
                      className="h-8 gap-1.5 text-xs font-medium cursor-pointer"
                    >
                      <Plus size={13} /> Grant
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
                    Effective permissions ({effectivePerms.length})
                  </h2>
                  <p className="text-xs text-muted-foreground mt-0.5">
                    What the user can do now: roles, plus grants, minus denies.
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
                <h3 className="text-sm font-semibold text-foreground">2-step verification</h3>
                <p className="text-xs text-muted-foreground leading-relaxed">
                  Reset it when the user lost their phone and recovery codes.
                </p>
              </div>

              <div className="md:col-span-8 rounded-xl border border-border bg-card p-5 shadow-xs flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-full border border-border flex items-center justify-center text-muted-foreground">
                    <EyeOff size={15} />
                  </div>
                  <div>
                    <span className="text-xs font-semibold px-2 py-0.5 rounded bg-muted text-muted-foreground inline-block">
                      {user.twoFactorEnabled ? 'On' : 'Off'}
                    </span>
                    <p className="text-xs text-muted-foreground mt-1">
                      {user.twoFactorEnabled
                        ? 'Enforced with two-factor verification.'
                        : 'The user signs in with a password only.'}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* Section 2: Signed-in devices */}
            <div className="grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
              <div className="md:col-span-4 space-y-1">
                <h3 className="text-sm font-semibold text-foreground">Signed-in devices</h3>
                <p className="text-xs text-muted-foreground leading-relaxed">
                  Browsers where the user is signed in now.
                </p>
              </div>

              <div className="md:col-span-8 rounded-xl border border-border bg-card p-8 shadow-xs">
                {userSessions.length === 0 ? (
                  <div className="flex flex-col items-center justify-center text-center">
                    <div className="w-10 h-10 rounded-full border border-border flex items-center justify-center text-muted-foreground/50 mb-2">
                      <Monitor size={18} strokeWidth={1.5} />
                    </div>
                    <p className="text-xs font-medium text-muted-foreground">No signed-in devices.</p>
                  </div>
                ) : (
                  <div className="space-y-3">
                    {userSessions.map(sess => (
                      <div key={sess.id} className="flex items-center justify-between p-3 rounded-lg border border-border">
                        <div>
                          <p className="font-semibold text-xs text-foreground">{sess.browser} on {sess.device}</p>
                          <p className="text-[11px] text-muted-foreground font-mono">{sess.ipAddress} &bull; {sess.location || 'Local'}</p>
                        </div>
                        <Button
                          variant="outline"
                          size="sm"
                          className="text-destructive h-7 text-xs cursor-pointer"
                          onClick={() => handleTerminateSession(sess.id)}
                        >
                          Terminate
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
                <h3 className="text-sm font-semibold text-foreground">Sign-in attempts</h3>
                <p className="text-xs text-muted-foreground leading-relaxed">
                  Successful and failed, newest first.
                </p>
              </div>

              <div className="md:col-span-8 rounded-xl border border-border bg-card shadow-xs overflow-hidden">
                <table className="w-full text-left text-xs">
                  <thead className="border-b border-border bg-muted/40 text-[11px] font-semibold text-muted-foreground">
                    <tr>
                      <th className="py-3 px-4 font-semibold text-xs text-foreground">When</th>
                      <th className="py-3 px-4 font-semibold text-xs text-foreground">Result</th>
                      <th className="py-3 px-4 font-semibold text-xs text-foreground">Method</th>
                      <th className="py-3 px-4 font-semibold text-xs text-foreground">Device</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td colSpan={4} className="py-10 text-center">
                        <div className="flex flex-col items-center justify-center">
                          <div className="w-10 h-10 rounded-full border border-border flex items-center justify-center text-muted-foreground/50 mb-2">
                            <Clock size={18} strokeWidth={1.5} />
                          </div>
                          <p className="text-xs font-medium text-muted-foreground">No sign-ins yet.</p>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

          </TabsContent>

          {/* TAB 4: Linked Accounts matching screenshot 5 */}
          <TabsContent value="linked-accounts" className="pt-6">
            <div className="rounded-xl border border-border bg-card shadow-xs">
              <div className="p-6 border-b border-border/60">
                <h2 className="text-sm font-semibold text-foreground">Connected sign-in providers</h2>
              </div>
              <div className="p-8">
                {identities.length === 0 ? (
                  <div className="flex flex-col items-center justify-center text-center py-6">
                    <div className="w-10 h-10 rounded-full border border-border flex items-center justify-center text-muted-foreground/50 mb-3">
                      <Link2 size={18} strokeWidth={1.5} />
                    </div>
                    <p className="text-xs font-semibold text-foreground">No linked accounts</p>
                    <p className="text-xs text-muted-foreground mt-1 max-w-sm">
                      Sign in with Google or another provider using your ISTAD email to link it.
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
                          Connected
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
            <DialogTitle className="text-base">Edit User Profile</DialogTitle>
            <DialogDescription className="text-xs">
              Update directory profile details for @{user.username}.
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={handleSaveProfile} className="space-y-4 pt-2 text-xs">
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1">
                <Label>First name</Label>
                <Input
                  value={editForm.givenName}
                  onChange={e => setEditForm({ ...editForm, givenName: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
              <div className="space-y-1">
                <Label>Last name</Label>
                <Input
                  value={editForm.familyName}
                  onChange={e => setEditForm({ ...editForm, familyName: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
              <div className="space-y-1">
                <Label>Middle name</Label>
                <Input
                  value={editForm.middleName}
                  onChange={e => setEditForm({ ...editForm, middleName: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
              <div className="space-y-1">
                <Label>Nickname</Label>
                <Input
                  value={editForm.nickname}
                  onChange={e => setEditForm({ ...editForm, nickname: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
              <div className="space-y-1">
                <Label>Phone number</Label>
                <Input
                  value={editForm.phoneNumber}
                  onChange={e => setEditForm({ ...editForm, phoneNumber: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
              <div className="space-y-1">
                <Label>Gender</Label>
                <Input
                  value={editForm.gender}
                  onChange={e => setEditForm({ ...editForm, gender: e.target.value })}
                  className="h-8 text-xs"
                  placeholder="e.g. Female / Male"
                />
              </div>
              <div className="space-y-1">
                <Label>Date of birth</Label>
                <Input
                  type="date"
                  value={editForm.birthdate}
                  onChange={e => setEditForm({ ...editForm, birthdate: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
              <div className="space-y-1">
                <Label>Website</Label>
                <Input
                  value={editForm.websiteUrl}
                  onChange={e => setEditForm({ ...editForm, websiteUrl: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
            </div>
            <div className="space-y-1">
              <Label>Address</Label>
              <Input
                value={editForm.addressStreetAddress}
                onChange={e => setEditForm({ ...editForm, addressStreetAddress: e.target.value })}
                className="h-8 text-xs"
              />
            </div>
            <DialogFooter className="pt-2">
              <Button type="button" variant="outline" size="sm" onClick={() => setEditProfileOpen(false)}>
                Cancel
              </Button>
              <Button type="submit" size="sm">
                Save Changes
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Assign Role Dialog */}
      <Dialog open={assignRoleOpen} onOpenChange={setAssignRoleOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="text-base">Assign Role</DialogTitle>
            <DialogDescription className="text-xs">
              Select an RBAC role to grant to @{user.username}.
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={handleAssignRole} className="space-y-4 pt-2">
            <div className="space-y-1.5">
              <Label className="text-xs font-medium">Select Role</Label>
              <Select value={selectedRoleUuid || undefined} onValueChange={setSelectedRoleUuid}>
                <SelectTrigger className="w-full h-9 text-xs">
                  <SelectValue placeholder="-- Choose a role --" />
                </SelectTrigger>
                <SelectContent>
                  {allRoles.filter(r => r.uuid).map(r => (
                    <SelectItem key={r.uuid} value={r.uuid}>
                      {r.name} - {r.description || 'No description'}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <DialogFooter>
              <Button type="button" variant="outline" size="sm" onClick={() => setAssignRoleOpen(false)}>
                Cancel
              </Button>
              <Button type="submit" size="sm">
                Assign Role
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Grant Direct Permission Dialog */}
      <Dialog open={grantPermOpen} onOpenChange={setGrantPermOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="text-base">Grant Direct Permission</DialogTitle>
            <DialogDescription className="text-xs">
              Grant a resource permission directly to @{user.username}.
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={handleGrantPermission} className="space-y-4 pt-2">
            <div className="space-y-1.5">
              <Label className="text-xs font-medium">Select Permission</Label>
              <Select value={selectedPermUuid || undefined} onValueChange={setSelectedPermUuid}>
                <SelectTrigger className="w-full h-9 text-xs">
                  <SelectValue placeholder="-- Choose a permission --" />
                </SelectTrigger>
                <SelectContent>
                  {allPerms.filter(p => p.uuid).map(p => (
                    <SelectItem key={p.uuid} value={p.uuid}>
                      {p.name || `${p.resource}:${p.action}`}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <DialogFooter>
              <Button type="button" variant="outline" size="sm" onClick={() => setGrantPermOpen(false)}>
                Cancel
              </Button>
              <Button type="submit" size="sm">
                Grant Permission
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Reset Password Dialog */}
      <Dialog open={pwdModalOpen} onOpenChange={setPwdModalOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="text-base">Reset Password</DialogTitle>
            <DialogDescription className="text-xs">
              Enter a new temporary password for @{user.username}.
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={handleResetPassword} className="space-y-4 pt-2">
            <div className="space-y-1.5">
              <Label className="text-xs font-medium">New Password</Label>
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
            <DialogFooter>
              <Button type="button" variant="outline" size="sm" onClick={() => setPwdModalOpen(false)}>
                Cancel
              </Button>
              <Button type="submit" size="sm">
                Save New Password
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

    </div>
  )
}
