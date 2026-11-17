import { useEffect, useState, useMemo } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useApp } from '@/context/AppContext'
import { usersApi, rolesApi, permissionsApi, adminSessionsApi } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { Checkbox } from '@/components/ui/checkbox'
import { TablePagination } from '@/components/ui/table-pagination'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
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
  Search, Mail, UserPlus, ChevronDown,
  MoreHorizontal, Trash2, CheckCircle2, AlertCircle, RefreshCw,
  Monitor, Lock, Unlock, Shield, Users
} from 'lucide-react'
import KeyIcon from '@/components/icons/KeyIcon'

export default function AdminUsersPage() {
  const navigate = useNavigate()
  const { tr } = useApp()
  const [users, setUsers] = useState([])
  const [availableRoles, setAvailableRoles] = useState([])
  const [query, setQuery] = useState('')
  const [statusFilter, setStatusFilter] = useState('ALL')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [actionMsg, setActionMsg] = useState('')
  const [currentPage, setCurrentPage] = useState(1)
  const [rowsPerPage, setRowsPerPage] = useState(5)

  // Checkbox selection state
  const [selectedUserIds, setSelectedUserIds] = useState([])

  // Modals state
  const [createOpen, setCreateOpen] = useState(false)
  const [pwdOpen, setPwdOpen] = useState(false)
  const [roleOpen, setRoleOpen] = useState(false)
  const [permOpen, setPermOpen] = useState(false)
  const [sessionOpen, setSessionOpen] = useState(false)
  const [selectedUser, setSelectedUser] = useState(null)

  // Form states
  const [createForm, setCreateForm] = useState({
    username: '',
    email: '',
    password: '',
    givenName: '',
    familyName: '',
  })
  const [newPassword, setNewPassword] = useState('')
  const [selectedRoles, setSelectedRoles] = useState([])
  const [userSessions, setUserSessions] = useState([])
  const [availablePerms, setAvailablePerms] = useState([])
  const [selectedPerms, setSelectedPerms] = useState([])
  const [initialPerms, setInitialPerms] = useState([])
  const [loadingPerms, setLoadingPerms] = useState(false)

  function load() {
    setLoading(true)
    setError('')
    Promise.allSettled([
      usersApi.list(0, 100, query),
      rolesApi.list(0, 100)
    ]).then(([uRes, rRes]) => {
      if (uRes.status === 'fulfilled' && uRes.value) {
        setUsers(uRes.value.content || [])
      }
      if (rRes.status === 'fulfilled' && rRes.value) {
        setAvailableRoles(rRes.value.content || [])
      }
    }).catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
  }, [])

  // Filter users based on search and status
  const filteredUsers = useMemo(() => {
    return users.filter(u => {
      const q = query.toLowerCase()
      const name = `${u.givenName || ''} ${u.familyName || ''} ${u.name || ''}`.toLowerCase()
      const username = (u.username || '').toLowerCase()
      const email = (u.email || '').toLowerCase()
      const matchQuery = !q || name.includes(q) || username.includes(q) || email.includes(q)

      let matchStatus = true
      if (statusFilter === 'ACTIVE') {
        matchStatus = u.enabled !== false && u.accountNonLocked !== false
      } else if (statusFilter === 'DISABLED') {
        matchStatus = u.enabled === false
      } else if (statusFilter === 'LOCKED') {
        matchStatus = u.accountNonLocked === false
      }

      return matchQuery && matchStatus
    })
  }, [users, query, statusFilter])

  // Select all checkboxes toggle
  const isAllSelected = filteredUsers.length > 0 && selectedUserIds.length === filteredUsers.length
  function toggleSelectAll() {
    if (isAllSelected) {
      setSelectedUserIds([])
    } else {
      setSelectedUserIds(filteredUsers.map(u => u.uuid))
    }
  }

  const paginatedUsers = useMemo(() => {
    return filteredUsers.slice((currentPage - 1) * rowsPerPage, currentPage * rowsPerPage)
  }, [filteredUsers, currentPage, rowsPerPage])

  function toggleSelectUser(uuid) {
    if (selectedUserIds.includes(uuid)) {
      setSelectedUserIds(selectedUserIds.filter(id => id !== uuid))
    } else {
      setSelectedUserIds([...selectedUserIds, uuid])
    }
  }

  // Format date helper
  function formatDate(isoStr) {
    if (!isoStr) return '—'
    try {
      return new Date(isoStr).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })
    } catch {
      return isoStr
    }
  }

  // Compute initials helper
  function getInitials(u) {
    const first = (u.givenName?.[0] || u.username?.[0] || 'U').toUpperCase()
    const last = (u.familyName?.[0] || u.username?.[1] || '').toUpperCase()
    return `${first}${last}`
  }

  // Create User
  async function handleCreateUser(e) {
    e.preventDefault()
    try {
      await usersApi.create(createForm)
      setCreateOpen(false)
      setCreateForm({ username: '', email: '', password: '', givenName: '', familyName: '' })
      setActionMsg('User created successfully.')
      load()
    } catch (err) {
      alert(err.message)
    }
  }

  // Toggle Enable / Disable
  async function handleToggleEnable(user) {
    try {
      if (user.enabled) await usersApi.disable(user.uuid)
      else await usersApi.enable(user.uuid)
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  // Lock / Unlock Account
  async function handleToggleLock(user) {
    try {
      if (user.accountNonLocked === false) {
        await usersApi.unlock(user.uuid)
        setActionMsg(`Account unlocked for ${user.username}`)
      } else {
        await usersApi.lock(user.uuid)
        setActionMsg(`Account locked for ${user.username}`)
      }
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  // Reset Password
  async function handleResetPassword(e) {
    e.preventDefault()
    if (!newPassword || !selectedUser) return
    try {
      await usersApi.resetPassword(selectedUser.uuid, newPassword)
      setPwdOpen(false)
      setNewPassword('')
      setActionMsg(`Password reset successfully for ${selectedUser.username}`)
    } catch (err) {
      alert(err.message)
    }
  }

  // Open Roles modal
  function openRolesModal(user) {
    setSelectedUser(user)
    const currentRoleUuids = user.roles ? user.roles.map(r => r.uuid || r) : []
    setSelectedRoles(currentRoleUuids)
    setRoleOpen(true)
  }

  // Save Role Assignment
  async function handleSaveRoles() {
    if (!selectedUser) return
    try {
      await usersApi.assignRoles(selectedUser.uuid, selectedRoles)
      setRoleOpen(false)
      setActionMsg(`Roles updated for ${selectedUser.username}`)
      load()
    } catch (err) {
      alert(err.message)
    }
  }

  // Open Direct Permissions Modal
  async function openPermsModal(user) {
    setSelectedUser(user)
    setLoadingPerms(true)
    setPermOpen(true)
    try {
      const [pRes, authRes] = await Promise.all([
        permissionsApi.list(0, 100),
        usersApi.authorities(user.uuid)
      ])
      setAvailablePerms(pRes?.content || [])
      const directUuids = (authRes?.directPermissions || authRes?.permissions || []).map(p => p.uuid || p)
      setSelectedPerms(directUuids)
      setInitialPerms(directUuids)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoadingPerms(false)
    }
  }

  // Save Direct Permissions
  async function handleSavePerms() {
    if (!selectedUser) return
    try {
      const toGrant = selectedPerms.filter(id => !initialPerms.includes(id))
      const toRevoke = initialPerms.filter(id => !selectedPerms.includes(id))

      if (toGrant.length > 0) {
        await usersApi.grantPermissions(selectedUser.uuid, toGrant)
      }
      for (const rId of toRevoke) {
        await usersApi.revokePermission(selectedUser.uuid, rId)
      }
      setPermOpen(false)
      setActionMsg(`Direct permissions updated for ${selectedUser.username}`)
      load()
    } catch (err) {
      alert(err.message)
    }
  }

  // Open User Sessions
  async function openUserSessions(user) {
    setSelectedUser(user)
    try {
      const all = await adminSessionsApi.list()
      setUserSessions(Array.isArray(all) ? all.filter(s => s.actor === user.username || s.userUuid === user.uuid) : [])
      setSessionOpen(true)
    } catch (err) {
      setUserSessions([])
      setSessionOpen(true)
    }
  }

  // Terminate session
  async function handleTerminateSession(sessionId) {
    try {
      await adminSessionsApi.terminate(sessionId)
      setUserSessions(s => s.filter(x => x.id !== sessionId))
    } catch (err) {
      alert(err.message)
    }
  }

  // Delete User
  async function handleRemoveUser(user) {
    if (!window.confirm(`Are you sure you want to permanently delete user ${user.username}?`)) return
    try {
      await usersApi.delete(user.uuid)
      setActionMsg(`User ${user.username} deleted.`)
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="min-h-full bg-background p-6 md:p-8">
      <div className="mx-auto max-w-6xl space-y-6">

        {/* Top Header matching screenshot 1 */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-border/60">
          <div>
            <h1 className="text-2xl sm:text-3xl font-bold tracking-tight text-foreground">
              {tr('users.title')}
            </h1>
            <p className="text-xs sm:text-sm text-muted-foreground mt-1">
              {tr('users.subtitle')}
            </p>
          </div>

          <div className="flex items-center gap-2">
            <Button
              size="sm"
              onClick={() => setCreateOpen(true)}
              className="gap-1.5"
            >
              <UserPlus size={14} />
              {tr('users.createUser')}
            </Button>
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

        {/* Toolbar matching screenshot 1 */}
        <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3">
          <div className="flex flex-1 items-center gap-2 max-w-lg">
            <div className="relative flex-1">
              <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
              <Input
                placeholder={tr('users.searchPlaceholder')}
                value={query}
                onChange={e => setQuery(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && load()}
                className="h-9 pl-9 text-xs bg-background border-border"
              />
            </div>

            <Select value={statusFilter} onValueChange={setStatusFilter}>
              <SelectTrigger className="w-[140px] h-9 text-xs">
                <SelectValue placeholder={tr('users.allStatuses')} />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ALL">{tr('users.allStatuses')}</SelectItem>
                <SelectItem value="ACTIVE">{tr('users.active')}</SelectItem>
                <SelectItem value="DISABLED">{tr('users.disabled')}</SelectItem>
                <SelectItem value="LOCKED">{tr('users.locked')}</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        {/* Table matching screenshot 1 */}
        <div className="rounded-lg border border-border bg-card shadow-xs overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="border-b border-border bg-muted/40 text-[11px] font-semibold text-muted-foreground">
                <tr>
                  <th className="py-3 px-4 w-10">
                    <Checkbox
                      checked={isAllSelected}
                      onCheckedChange={toggleSelectAll}
                      aria-label="Select all users"
                    />
                  </th>
                  <th className="py-3 px-4 w-[280px]">{tr('users.colUser')}</th>
                  <th className="py-3 px-4 w-[200px]">{tr('users.colUsername')}</th>
                  <th className="py-3 px-4 w-[140px]">{tr('users.colStatus')}</th>
                  <th className="py-3 px-4 w-[160px]">{tr('users.colCreated')}</th>
                  <th className="py-3 px-4 w-12 text-right"></th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {loading ? (
                  <tr>
                    <td colSpan={6} className="py-12 text-center text-muted-foreground">
                      <div className="flex justify-center">
                        <div className="h-6 w-6 animate-spin rounded-full border-2 border-blue-600 border-t-transparent" />
                      </div>
                    </td>
                  </tr>
                ) : filteredUsers.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="py-12 text-center text-muted-foreground">
                      {tr('users.noUsers')}
                    </td>
                  </tr>
                ) : (
                  paginatedUsers.map(user => {
                    const initials = getInitials(user)
                    const fullName = `${user.givenName || ''} ${user.familyName || ''}`.trim() || user.name || user.username
                    const isChecked = selectedUserIds.includes(user.uuid)
                    const isEnabled = user.enabled !== false
                    const isLocked = user.accountNonLocked === false

                    return (
                      <tr
                        key={user.uuid}
                        onClick={() => navigate(`/admin/users/${user.uuid}`)}
                        className="hover:bg-muted/30 transition-colors cursor-pointer group"
                      >
                        {/* Checkbox */}
                        <td className="py-3.5 px-4" onClick={e => e.stopPropagation()}>
                          <Checkbox
                            checked={isChecked}
                            onCheckedChange={() => toggleSelectUser(user.uuid)}
                            aria-label={`Select user ${user.username}`}
                          />
                        </td>

                        {/* User: Avatar + Name + Email */}
                        <td className="py-3.5 px-4">
                          <div className="flex items-center gap-3">
                            <div className="w-8 h-8 rounded-full bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-200 text-xs font-semibold flex items-center justify-center shrink-0 border border-slate-200 dark:border-slate-700">
                              {initials}
                            </div>
                            <div className="min-w-0">
                              <p className="font-semibold text-foreground text-xs group-hover:text-blue-600 transition truncate">
                                {fullName}
                              </p>
                              <p className="text-[11px] text-muted-foreground truncate">
                                {user.email || '—'}
                              </p>
                            </div>
                          </div>
                        </td>

                        {/* Username */}
                        <td className="py-3.5 px-4">
                          <span className="font-mono text-xs text-foreground font-medium">
                            {user.username}
                          </span>
                        </td>

                        {/* Status */}
                        <td className="py-3.5 px-4">
                          {isLocked ? (
                            <span className="inline-flex items-center gap-1.5 text-xs font-medium text-rose-600 dark:text-rose-400">
                              <span className="w-1.5 h-1.5 rounded-full bg-rose-500"></span> {tr('users.statusLocked')}
                            </span>
                          ) : isEnabled ? (
                            <span className="inline-flex items-center gap-1.5 text-xs font-medium text-emerald-600 dark:text-emerald-400">
                              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span> {tr('users.statusActive')}
                            </span>
                          ) : (
                            <span className="inline-flex items-center gap-1.5 text-xs font-medium text-slate-500">
                              <span className="w-1.5 h-1.5 rounded-full bg-slate-400"></span> {tr('users.statusDisabled')}
                            </span>
                          )}
                        </td>

                        {/* Created Date */}
                        <td className="py-3.5 px-4 text-xs text-muted-foreground">
                          {formatDate(user.createdAt)}
                        </td>

                        {/* Actions Menu */}
                        <td className="py-3.5 px-4 text-right" onClick={e => e.stopPropagation()}>
                          <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                              <Button variant="ghost" size="icon" className="h-7 w-7 text-muted-foreground hover:text-foreground">
                                <MoreHorizontal size={14} />
                              </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end" className="w-48 text-xs">
                              <DropdownMenuLabel>User Actions</DropdownMenuLabel>
                              <DropdownMenuItem
                                onClick={() => navigate(`/admin/users/${user.uuid}`)}
                                className="cursor-pointer gap-2"
                              >
                                <Users size={14} className="text-blue-600" /> View details
                              </DropdownMenuItem>
                              <DropdownMenuSeparator />
                              <DropdownMenuItem
                                onClick={() => { setSelectedUser(user); setPwdOpen(true) }}
                                className="cursor-pointer gap-2"
                              >
                                <KeyIcon size={14} /> Reset password
                              </DropdownMenuItem>
                              <DropdownMenuItem
                                onClick={() => openRolesModal(user)}
                                className="cursor-pointer gap-2"
                              >
                                <Shield size={14} /> Assign roles
                              </DropdownMenuItem>
                              <DropdownMenuItem
                                onClick={() => openPermsModal(user)}
                                className="cursor-pointer gap-2"
                              >
                                <Shield size={14} className="text-blue-500" /> Direct permissions
                              </DropdownMenuItem>
                              <DropdownMenuItem
                                onClick={() => openUserSessions(user)}
                                className="cursor-pointer gap-2"
                              >
                                <Monitor size={14} /> Active sessions
                              </DropdownMenuItem>
                              <DropdownMenuSeparator />
                              <DropdownMenuItem
                                onClick={() => handleToggleLock(user)}
                                className="cursor-pointer gap-2"
                              >
                                {isLocked ? <Unlock size={14} /> : <Lock size={14} />}
                                {isLocked ? 'Unlock account' : 'Lock account'}
                              </DropdownMenuItem>
                              <DropdownMenuItem
                                onClick={() => handleToggleEnable(user)}
                                className="cursor-pointer gap-2"
                              >
                                {isEnabled ? 'Deactivate account' : 'Activate account'}
                              </DropdownMenuItem>
                              <DropdownMenuSeparator />
                              <DropdownMenuItem
                                onClick={() => handleRemoveUser(user)}
                                className="cursor-pointer gap-2 text-rose-600 focus:text-rose-600"
                              >
                                <Trash2 size={14} /> Delete user
                              </DropdownMenuItem>
                            </DropdownMenuContent>
                          </DropdownMenu>
                        </td>
                      </tr>
                    )
                  })
                )}
              </tbody>
            </table>
          </div>

          {/* Pagination Footer */}
          <TablePagination
            totalItems={filteredUsers.length}
            page={currentPage}
            pageSize={rowsPerPage}
            onPageChange={setCurrentPage}
            onPageSizeChange={setRowsPerPage}
            pageSizeOptions={[5, 10, 20, 50]}
          />
        </div>

        {/* 1. Create User Modal */}
        <Dialog open={createOpen} onOpenChange={setCreateOpen}>
          <DialogContent className="sm:max-w-md">
            <DialogHeader>
              <DialogTitle className="text-base">Create New User Account</DialogTitle>
              <DialogDescription className="text-xs">
                Add an IAM identity to the directory.
              </DialogDescription>
            </DialogHeader>
            <form onSubmit={handleCreateUser} className="space-y-3 py-2 text-xs">
              <div className="space-y-1">
                <Label htmlFor="c-username">Username</Label>
                <Input
                  id="c-username"
                  required
                  value={createForm.username}
                  onChange={e => setCreateForm({ ...createForm, username: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
              <div className="space-y-1">
                <Label htmlFor="c-email">Email Address</Label>
                <Input
                  id="c-email"
                  type="email"
                  required
                  value={createForm.email}
                  onChange={e => setCreateForm({ ...createForm, email: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
              <div className="space-y-1">
                <Label htmlFor="c-pwd">Initial Password</Label>
                <Input
                  id="c-pwd"
                  type="password"
                  required
                  value={createForm.password}
                  onChange={e => setCreateForm({ ...createForm, password: e.target.value })}
                  className="h-8 text-xs"
                />
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div className="space-y-1">
                  <Label htmlFor="c-fname">First Name</Label>
                  <Input
                    id="c-fname"
                    value={createForm.givenName}
                    onChange={e => setCreateForm({ ...createForm, givenName: e.target.value })}
                    className="h-8 text-xs"
                  />
                </div>
                <div className="space-y-1">
                  <Label htmlFor="c-lname">Last Name</Label>
                  <Input
                    id="c-lname"
                    value={createForm.familyName}
                    onChange={e => setCreateForm({ ...createForm, familyName: e.target.value })}
                    className="h-8 text-xs"
                  />
                </div>
              </div>
              <DialogFooter className="pt-3">
                <Button type="button" variant="outline" size="sm" onClick={() => setCreateOpen(false)}>
                  Cancel
                </Button>
                <Button type="submit" size="sm">
                  Create User
                </Button>
              </DialogFooter>
            </form>
          </DialogContent>
        </Dialog>

        {/* 2. Reset Password Modal */}
        <Dialog open={pwdOpen} onOpenChange={setPwdOpen}>
          <DialogContent className="sm:max-w-sm">
            <DialogHeader>
              <DialogTitle className="text-base">Reset Password</DialogTitle>
              <DialogDescription className="text-xs">
                Set a new password for <span className="font-semibold text-foreground">{selectedUser?.username}</span>.
              </DialogDescription>
            </DialogHeader>
            <form onSubmit={handleResetPassword} className="space-y-3 py-2 text-xs">
              <div className="space-y-1">
                <Label htmlFor="new-p">New Password</Label>
                <Input
                  id="new-p"
                  type="password"
                  required
                  value={newPassword}
                  onChange={e => setNewPassword(e.target.value)}
                  className="h-8 text-xs"
                  placeholder="Min 8 characters"
                />
              </div>
              <DialogFooter className="pt-3">
                <Button type="button" variant="outline" size="sm" onClick={() => setPwdOpen(false)}>
                  Cancel
                </Button>
                <Button type="submit" size="sm">
                  Reset Password
                </Button>
              </DialogFooter>
            </form>
          </DialogContent>
        </Dialog>

        {/* 3. Assign Roles Modal */}
        <Dialog open={roleOpen} onOpenChange={setRoleOpen}>
          <DialogContent className="sm:max-w-md">
            <DialogHeader>
              <DialogTitle className="text-base">Assign Roles</DialogTitle>
              <DialogDescription className="text-xs">
                Select RBAC roles to grant to <span className="font-semibold text-foreground">{selectedUser?.username}</span>.
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-2 py-3 text-xs max-h-60 overflow-y-auto">
              {availableRoles.map(role => {
                const checked = selectedRoles.includes(role.uuid)
                return (
                  <label key={role.uuid} className="flex items-center gap-2 p-2 rounded border border-border hover:bg-muted/40 cursor-pointer">
                    <Checkbox
                      checked={checked}
                      onCheckedChange={val => {
                        if (val) setSelectedRoles([...selectedRoles, role.uuid])
                        else setSelectedRoles(selectedRoles.filter(id => id !== role.uuid))
                      }}
                    />
                    <div>
                      <span className="font-mono font-medium block">{role.name}</span>
                      <span className="text-[11px] text-muted-foreground">{role.description || 'System role'}</span>
                    </div>
                  </label>
                )
              })}
            </div>
            <DialogFooter>
              <Button variant="outline" size="sm" onClick={() => setRoleOpen(false)}>
                Cancel
              </Button>
              <Button size="sm" onClick={handleSaveRoles}>
                Save Roles
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>

        {/* 4. User Active Sessions Modal */}
        <Dialog open={sessionOpen} onOpenChange={setSessionOpen}>
          <DialogContent className="sm:max-w-lg">
            <DialogHeader>
              <DialogTitle className="text-base">Active Sessions: {selectedUser?.username}</DialogTitle>
              <DialogDescription className="text-xs">
                Monitor and terminate individual user device sessions.
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-2 py-3 text-xs max-h-60 overflow-y-auto">
              {userSessions.length > 0 ? (
                userSessions.map(sess => (
                  <div key={sess.id} className="flex items-center justify-between p-2.5 rounded border border-border">
                    <div>
                      <p className="font-medium text-xs">{sess.browser} on {sess.device}</p>
                      <p className="text-[11px] text-muted-foreground font-mono">{sess.ipAddress} · {sess.location}</p>
                    </div>
                    <Button
                      variant="outline"
                      size="sm"
                      className="text-destructive h-7 text-xs"
                      onClick={() => handleTerminateSession(sess.id)}
                    >
                      Terminate
                    </Button>
                  </div>
                ))
              ) : (
                <p className="text-center py-6 text-muted-foreground text-xs">
                  No active session records found for this user in database.
                </p>
              )}
            </div>
            <DialogFooter>
              <Button variant="outline" size="sm" onClick={() => setSessionOpen(false)}>
                Close
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>

        {/* 5. User Direct Permissions Modal */}
        <Dialog open={permOpen} onOpenChange={setPermOpen}>
          <DialogContent className="sm:max-w-md">
            <DialogHeader>
              <DialogTitle className="text-base">Grant / Revoke Direct Permissions</DialogTitle>
              <DialogDescription className="text-xs">
                Manage granular permissions directly attached to <span className="font-semibold text-foreground">{selectedUser?.username}</span>.
              </DialogDescription>
            </DialogHeader>
            {loadingPerms ? (
              <div className="py-8 text-center text-xs text-muted-foreground flex items-center justify-center gap-2">
                <RefreshCw size={14} className="animate-spin" /> Loading permissions...
              </div>
            ) : (
              <div className="space-y-2 py-3 text-xs max-h-64 overflow-y-auto">
                {availablePerms.map(p => {
                  const checked = selectedPerms.includes(p.uuid)
                  return (
                    <label key={p.uuid} className="flex items-center gap-2 p-2 rounded border border-border hover:bg-muted/40 cursor-pointer">
                      <Checkbox
                        checked={checked}
                        onCheckedChange={val => {
                          if (val) setSelectedPerms([...selectedPerms, p.uuid])
                          else setSelectedPerms(selectedPerms.filter(id => id !== p.uuid))
                        }}
                      />
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2">
                          <span className="font-mono font-medium text-xs text-foreground">{p.resource}:{p.action}</span>
                          <Badge variant="outline" className="text-[10px] px-1.5 py-0">Direct</Badge>
                        </div>
                        <span className="text-[11px] text-muted-foreground truncate block">{p.description || 'System permission'}</span>
                      </div>
                    </label>
                  )
                })}
                {availablePerms.length === 0 && (
                  <p className="text-center py-4 text-xs text-muted-foreground">No permissions registered in catalog.</p>
                )}
              </div>
            )}
            <DialogFooter>
              <Button variant="outline" size="sm" onClick={() => setPermOpen(false)}>
                Cancel
              </Button>
              <Button size="sm" onClick={handleSavePerms} disabled={loadingPerms}>
                Save Permissions
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>

      </div>
    </div>
  )
}
