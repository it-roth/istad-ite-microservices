import { useEffect, useState, useMemo } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useApp } from '@/context/AppContext'
import { permissionsApi, rolesApi } from '@/lib/api'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
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
  Shield, Key, Plus, Trash2, RefreshCw, CheckCircle2,
  AlertCircle, Search, MoreHorizontal, SlidersHorizontal, ChevronDown, Pencil
} from 'lucide-react'
import { Textarea } from '@/components/ui/textarea'

export default function AdminCatalogPage({ type }) {
  const navigate = useNavigate()
  const { tr } = useApp()
  const isRoles = type === 'roles'
  const title = isRoles ? tr('roles.title') : tr('permissions.title')
  const subtitle = isRoles ? tr('roles.subtitle') : tr('permissions.subtitle')

  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [successMsg, setSuccessMsg] = useState('')
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('ALL')
  const [typeFilter, setTypeFilter] = useState('ALL')
  const [deleteConfirmItem, setDeleteConfirmItem] = useState(null)

  // Pagination
  const [rowsPerPage, setRowsPerPage] = useState(5)
  const [currentPage, setCurrentPage] = useState(1)

  // Create form state
  const [createOpen, setCreateOpen] = useState(false)
  const [roleForm, setRoleForm] = useState({ name: '', description: '' })
  const [permForm, setPermForm] = useState({ resource: '', action: '', description: '' })

  // Role permissions modal state
  const [permModalOpen, setPermModalOpen] = useState(false)
  const [selectedRole, setSelectedRole] = useState(null)
  const [allPerms, setAllPerms] = useState([])
  const [assignedPerms, setAssignedPerms] = useState([])
  const [loadingPerms, setLoadingPerms] = useState(false)

  // Edit role modal state
  const [editRoleOpen, setEditRoleOpen] = useState(false)
  const [editingRole, setEditingRole] = useState(null)
  const [editRoleForm, setEditRoleForm] = useState({ name: '', description: '' })
  const [savingEditRole, setSavingEditRole] = useState(false)
  const [editRoleError, setEditRoleError] = useState('')

  function openEditRole(role) {
    setEditingRole(role)
    setEditRoleForm({
      name: role.name || '',
      description: role.description || ''
    })
    setEditRoleError('')
    setEditRoleOpen(true)
  }

  async function handleSaveEditRole(e) {
    e.preventDefault()
    if (!editingRole) return
    if (!editRoleForm.name.trim()) {
      setEditRoleError('Role name is required')
      return
    }
    setSavingEditRole(true)
    setEditRoleError('')
    try {
      await rolesApi.update(editingRole.uuid, {
        name: editRoleForm.name.trim().toUpperCase(),
        description: editRoleForm.description?.trim() || ''
      })
      setEditRoleOpen(false)
      setActionMsg(`Role updated successfully.`)
      load()
    } catch (err) {
      setEditRoleError(err.message || 'Failed to update role')
    } finally {
      setSavingEditRole(false)
    }
  }

  function load() {
    setLoading(true)
    setError('')
    const apiCall = isRoles ? rolesApi.list(0, 100) : permissionsApi.list(0, 200)
    apiCall
      .then(data => setItems(data?.content || []))
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
  }, [type])

  async function handleCreate(e) {
    e.preventDefault()
    try {
      if (isRoles) {
        await rolesApi.create({ ...roleForm, permissionUuids: [] })
        setRoleForm({ name: '', description: '' })
        setSuccessMsg(`Role ${roleForm.name} created successfully.`)
      } else {
        await permissionsApi.create(permForm)
        setPermForm({ resource: '', action: '', description: '' })
        setSuccessMsg(`Permission ${permForm.resource}:${permForm.action} created successfully.`)
      }
      setTimeout(() => setSuccessMsg(''), 4000)
      setCreateOpen(false)
      load()
    } catch (err) {
      setError(err.message || 'Failed to create')
      setTimeout(() => setError(''), 4000)
    }
  }

  function handleRemove(item) {
    setDeleteConfirmItem(item)
  }

  async function confirmRemove() {
    if (!deleteConfirmItem) return
    const label = deleteConfirmItem.name || `${deleteConfirmItem.resource}:${deleteConfirmItem.action}`
    try {
      if (isRoles) await rolesApi.delete(deleteConfirmItem.uuid)
      else await permissionsApi.delete(deleteConfirmItem.uuid)
      setSuccessMsg(`"${label}" deleted.`)
      setTimeout(() => setSuccessMsg(''), 4000)
      setDeleteConfirmItem(null)
      load()
    } catch (err) {
      setDeleteConfirmItem(null)
      setError(err.message || 'Failed to delete')
      setTimeout(() => setError(''), 4000)
    }
  }

  async function handleToggle(item) {
    try {
      if (isRoles) {
        if (item.active) await rolesApi.deactivate(item.uuid)
        else await rolesApi.activate(item.uuid)
      } else {
        if (item.active) await permissionsApi.deactivate(item.uuid)
        else await permissionsApi.activate(item.uuid)
      }
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  // Open Permission Assignment for a Role
  async function openRolePermissions(role) {
    setSelectedRole(role)
    setLoadingPerms(true)
    setPermModalOpen(true)
    try {
      const [pRes, rolePerms] = await Promise.all([
        permissionsApi.list(0, 200),
        rolesApi.permissions(role.uuid).catch(() => []),
      ])
      setAllPerms(pRes?.content || [])
      const assignedIds = Array.isArray(rolePerms) ? rolePerms.map(p => p.uuid || p) : []
      setAssignedPerms(assignedIds)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoadingPerms(false)
    }
  }

  async function handleSaveRolePermissions() {
    if (!selectedRole) return
    try {
      await rolesApi.assignPermissions(selectedRole.uuid, assignedPerms)
      setPermModalOpen(false)
      setSuccessMsg(`Permissions updated for role ${selectedRole.name}.`)
      load()
    } catch (err) {
      alert(err.message)
    }
  }

  const filteredItems = useMemo(() => {
    return items.filter(item => {
      const s = search.toLowerCase()
      const name = isRoles ? (item.name || '') : (item.name || `${item.resource}:${item.action}`)
      const desc = item.description || ''
      const matchSearch = !s || name.toLowerCase().includes(s) || desc.toLowerCase().includes(s)

      const isActive = item.active !== false
      const matchStatus = statusFilter === 'ALL' ||
        (statusFilter === 'ACTIVE' && isActive) ||
        (statusFilter === 'INACTIVE' && !isActive)

      const isBuiltin = ['SUPERADMIN', 'ADMIN', 'STUDENT', 'INSTRUCTOR', 'ACADEMIC', 'PARTNER', 'USER'].includes(item.name?.toUpperCase()) || item.builtin || !item.custom
      const matchType = typeFilter === 'ALL' ||
        (typeFilter === 'BUILTIN' && isBuiltin) ||
        (typeFilter === 'CUSTOM' && !isBuiltin)

      return matchSearch && matchStatus && matchType
    })
  }, [items, search, statusFilter, typeFilter, isRoles])

  // Pagination calculation
  const totalItems = filteredItems.length
  const startIndex = (currentPage - 1) * rowsPerPage
  const paginatedItems = filteredItems.slice(startIndex, startIndex + rowsPerPage)
  const totalPages = Math.max(1, Math.ceil(totalItems / rowsPerPage))

  return (
    <div className="min-h-full bg-background p-6 md:p-8">
      <div className="mx-auto max-w-6xl space-y-6">

        {/* Header matching screenshot 2 & 4 */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-border/60">
          <div>
            <h1 className="text-2xl sm:text-3xl font-bold tracking-tight text-foreground">
              {title}
            </h1>
            <p className="text-xs sm:text-sm text-muted-foreground mt-1">
              {subtitle}
            </p>
          </div>
          <div className="flex items-center gap-2">
            <Button
              size="sm"
              onClick={() => setCreateOpen(true)}
              className="gap-1.5 cursor-pointer"
            >
              <Plus size={15} /> {isRoles ? tr('roles.createRole') : tr('permissions.createPermission')}
            </Button>
          </div>
        </div>

        {successMsg && (
          <div className="flex items-center gap-2 rounded-lg border border-emerald-200 bg-emerald-50/70 p-3 text-xs text-emerald-800 dark:border-emerald-800 dark:bg-emerald-950/30 dark:text-emerald-300">
            <CheckCircle2 size={15} /> {successMsg}
          </div>
        )}

        {/* Toolbar: Search & Filter Dropdowns matching screenshots 2 & 4 */}
        <div className="flex flex-col sm:flex-row items-center gap-3">
          <div className="relative flex-1 w-full">
            <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
            <Input
              placeholder={tr('roles.searchPlaceholder')}
              value={search}
              onChange={e => setSearch(e.target.value)}
              className="h-9 pl-9 text-xs bg-background border-border"
            />
          </div>

          <div className="flex items-center gap-2 w-full sm:w-auto">
            <Select value={statusFilter} onValueChange={setStatusFilter}>
              <SelectTrigger className="w-[140px] h-9 text-xs">
                <SelectValue placeholder={tr('common.allStatuses')} />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ALL">{tr('common.allStatuses')}</SelectItem>
                <SelectItem value="ACTIVE">{tr('common.active')}</SelectItem>
                <SelectItem value="INACTIVE">{tr('common.inactive')}</SelectItem>
              </SelectContent>
            </Select>

            <Select value={typeFilter} onValueChange={setTypeFilter}>
              <SelectTrigger className="w-[130px] h-9 text-xs">
                <SelectValue placeholder={tr('common.allTypes')} />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ALL">{tr('common.allTypes')}</SelectItem>
                <SelectItem value="BUILTIN">{tr('common.builtIn')}</SelectItem>
                <SelectItem value="CUSTOM">{tr('common.custom')}</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        {/* Table matching screenshots 2 & 4 */}
        <div className="rounded-lg border border-border bg-card shadow-xs overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="border-b border-border bg-muted/40 text-[11px] font-semibold text-muted-foreground">
                <tr>
                  <th className="py-3 px-4 w-[280px]">{tr('common.name')}</th>
                  <th className="py-3 px-4">{tr('common.description')}</th>
                  <th className="py-3 px-4 w-[120px]">{tr('apps.type')}</th>
                  <th className="py-3 px-4 w-[120px]">{tr('common.status')}</th>
                  {isRoles && <th className="py-3 px-4 w-[60px] text-right"></th>}
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {loading ? (
                  <tr>
                    <td colSpan={isRoles ? 5 : 4} className="py-12 text-center text-muted-foreground">
                      <div className="flex justify-center">
                        <div className="h-6 w-6 animate-spin rounded-full border-2 border-blue-600 border-t-transparent" />
                      </div>
                    </td>
                  </tr>
                ) : paginatedItems.length === 0 ? (
                  <tr>
                    <td colSpan={isRoles ? 5 : 4} className="py-12 text-center text-muted-foreground">
                      {isRoles ? tr('roles.noRoles') : tr('permissions.noPermissions')}
                    </td>
                  </tr>
                ) : (
                  paginatedItems.map(item => {
                    const isBuiltin = ['SUPERADMIN', 'ADMIN', 'MERCHANT', 'CUSTOMER', 'USER'].includes(item.name?.toUpperCase()) || item.builtin || !item.custom
                    const isActive = item.active !== false
                    const permName = item.name || `${item.resource}:${item.action}`

                    return (
                      <tr
                        key={item.uuid}
                        className={`hover:bg-muted/30 transition-colors ${isRoles ? 'cursor-pointer' : ''}`}
                        onClick={() => {
                          if (isRoles) navigate(`/admin/roles/${item.uuid}`)
                        }}
                      >
                        {/* Name Column */}
                        <td className="py-3.5 px-4 font-medium">
                          {isRoles ? (
                            <div className="flex items-center gap-2.5">
                              <Shield size={16} className="text-slate-400 dark:text-slate-500 shrink-0" />
                              <Link
                                to={`/admin/roles/${item.uuid}`}
                                className="font-semibold text-foreground uppercase tracking-wide hover:text-blue-600 no-underline hover:no-underline transition-colors"
                                onClick={e => e.stopPropagation()}
                              >
                                {item.name}
                              </Link>
                            </div>
                          ) : (
                            <span className="font-mono text-xs text-foreground font-medium">
                              {permName}
                            </span>
                          )}
                        </td>

                        {/* Description Column */}
                        <td className="py-3.5 px-4 text-muted-foreground">
                          {item.description || '—'}
                        </td>

                        {/* Type Column */}
                        <td className="py-3.5 px-4">
                          <Badge variant="outline" className="text-[10px] font-normal border-slate-200 dark:border-slate-800 text-muted-foreground bg-muted/30 px-2 py-0.5">
                            {isBuiltin ? tr('common.builtIn') : tr('common.custom')}
                          </Badge>
                        </td>

                        {/* Status Column */}
                        <td className="py-3.5 px-4">
                          <span className="inline-flex items-center gap-1.5 text-xs text-slate-700 dark:text-slate-300 font-medium">
                            <span className="h-1.5 w-1.5 rounded-full bg-emerald-500"></span>
                            {isActive ? tr('common.active') : tr('common.inactive')}
                          </span>
                        </td>

                        {/* Actions Column (Roles only) */}
                        {isRoles && (
                          <td className="py-3.5 px-4 text-right" onClick={e => e.stopPropagation()}>
                            <DropdownMenu>
                              <DropdownMenuTrigger asChild>
                                <Button variant="ghost" size="icon" className="h-7 w-7 text-muted-foreground">
                                  <MoreHorizontal size={14} />
                                </Button>
                              </DropdownMenuTrigger>
                              <DropdownMenuContent align="end" className="w-48 text-xs">
                                <DropdownMenuItem onClick={() => navigate(`/admin/roles/${item.uuid}`)} className="cursor-pointer text-xs">
                                  <SlidersHorizontal size={13} className="mr-2 text-muted-foreground" /> {tr('roles.viewPermissions')}
                                </DropdownMenuItem>
                                <DropdownMenuItem onClick={() => openRolePermissions(item)} className="cursor-pointer text-xs">
                                  <Shield size={13} className="mr-2 text-blue-500" /> {tr('roles.managePermissions')}
                                </DropdownMenuItem>
                                <DropdownMenuItem onClick={() => openEditRole(item)} className="cursor-pointer text-xs font-medium">
                                  <Pencil size={13} className="mr-2 text-amber-500" /> {tr('roles.editRole')}
                                </DropdownMenuItem>
                                <DropdownMenuSeparator />
                                <DropdownMenuItem onClick={() => handleToggle(item)} className="cursor-pointer text-xs">
                                  {isActive ? tr('roles.deactivateRole') : tr('roles.activateRole')}
                                </DropdownMenuItem>
                                {!isBuiltin && (
                                  <DropdownMenuItem onClick={() => handleRemove(item)} className="cursor-pointer text-xs text-red-600 dark:text-red-400">
                                    <Trash2 size={13} className="mr-2" /> {tr('roles.deleteRole')}
                                  </DropdownMenuItem>
                                )}
                              </DropdownMenuContent>
                            </DropdownMenu>
                          </td>
                        )}
                      </tr>
                    )
                  })
                )}
              </tbody>
            </table>
          </div>

          {/* Pagination Footer */}
          <TablePagination
            totalItems={totalItems}
            page={currentPage}
            pageSize={rowsPerPage}
            onPageChange={setCurrentPage}
            onPageSizeChange={setRowsPerPage}
            pageSizeOptions={[5, 10, 20, 50]}
          />
        </div>

      </div>

      {/* Create Dialog */}
      <Dialog open={createOpen} onOpenChange={setCreateOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="text-base">{isRoles ? 'Create Role' : 'Create Permission'}</DialogTitle>
            <DialogDescription className="text-xs">
              {isRoles ? 'Define a new RBAC role.' : 'Define a new resource:action permission.'}
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={handleCreate} className="space-y-4 pt-2">
            {isRoles ? (
              <>
                <div className="space-y-1.5">
                  <label className="text-xs font-medium">Role Name</label>
                  <Input
                    required
                    placeholder="e.g. AUDITOR"
                    value={roleForm.name}
                    onChange={e => setRoleForm({ ...roleForm, name: e.target.value.toUpperCase() })}
                    className="h-9 text-xs"
                  />
                </div>
                <div className="space-y-1.5">
                  <label className="text-xs font-medium">Description</label>
                  <Input
                    placeholder="Short description of this role"
                    value={roleForm.description}
                    onChange={e => setRoleForm({ ...roleForm, description: e.target.value })}
                    className="h-9 text-xs"
                  />
                </div>
              </>
            ) : (
              <>
                <div className="grid grid-cols-2 gap-3">
                  <div className="space-y-1.5">
                    <label className="text-xs font-medium">Resource</label>
                    <Input
                      required
                      placeholder="e.g. document"
                      value={permForm.resource}
                      onChange={e => setPermForm({ ...permForm, resource: e.target.value })}
                      className="h-9 text-xs font-mono"
                    />
                  </div>
                  <div className="space-y-1.5">
                    <label className="text-xs font-medium">Action</label>
                    <Input
                      required
                      placeholder="e.g. read"
                      value={permForm.action}
                      onChange={e => setPermForm({ ...permForm, action: e.target.value })}
                      className="h-9 text-xs font-mono"
                    />
                  </div>
                </div>
                <div className="space-y-1.5">
                  <label className="text-xs font-medium">Description</label>
                  <Input
                    placeholder="Description of the permission"
                    value={permForm.description}
                    onChange={e => setPermForm({ ...permForm, description: e.target.value })}
                    className="h-9 text-xs"
                  />
                </div>
              </>
            )}
            <DialogFooter>
              <Button type="button" variant="outline" size="sm" onClick={() => setCreateOpen(false)}>
                Cancel
              </Button>
              <Button type="submit" size="sm">
                Save
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Role Permissions Assignment Modal */}
      <Dialog open={permModalOpen} onOpenChange={setPermModalOpen}>
        <DialogContent className="sm:max-w-xl max-h-[85vh] flex flex-col">
          <DialogHeader>
            <DialogTitle className="text-base flex items-center gap-2">
              <Shield size={16} className="text-blue-600" />
              Permissions for {selectedRole?.name}
            </DialogTitle>
            <DialogDescription className="text-xs">
              Select permissions that members of role {selectedRole?.name} automatically inherit.
            </DialogDescription>
          </DialogHeader>

          <div className="flex-1 overflow-y-auto py-2 space-y-2 border rounded-md p-3 my-2 bg-muted/10">
            {loadingPerms ? (
              <div className="flex justify-center p-6">
                <div className="h-6 w-6 animate-spin rounded-full border-2 border-blue-600 border-t-transparent" />
              </div>
            ) : allPerms.length === 0 ? (
              <p className="text-xs text-muted-foreground text-center py-4">No permissions available in catalog.</p>
            ) : (
              allPerms.map(p => {
                const pUuid = p.uuid || p.id || p
                const pName = p.name || `${p.resource}:${p.action}`
                const checked = assignedPerms.includes(pUuid)

                return (
                  <label
                    key={pUuid}
                    className="flex items-center gap-3 p-2 rounded hover:bg-muted/40 cursor-pointer text-xs"
                  >
                    <Checkbox
                      checked={checked}
                      onCheckedChange={() => {
                        if (checked) {
                          setAssignedPerms(assignedPerms.filter(id => id !== pUuid))
                        } else {
                          setAssignedPerms([...assignedPerms, pUuid])
                        }
                      }}
                    />
                    <div>
                      <p className="font-mono font-medium text-foreground">{pName}</p>
                      {p.description && <p className="text-[11px] text-muted-foreground">{p.description}</p>}
                    </div>
                  </label>
                )
              })
            )}
          </div>

          <DialogFooter>
            <Button variant="outline" size="sm" onClick={() => setPermModalOpen(false)}>
              Cancel
            </Button>
            <Button size="sm" onClick={handleSaveRolePermissions}>
              Save Permissions ({assignedPerms.length})
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Edit Role Dialog */}
      <Dialog open={editRoleOpen} onOpenChange={setEditRoleOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="text-base flex items-center gap-2">
              <Pencil size={16} className="text-amber-500" />
              {tr('roles.editRole')} {editingRole?.name ? `(${editingRole.name})` : ''}
            </DialogTitle>
            <DialogDescription className="text-xs">
              Update the name or description for this role.
            </DialogDescription>
          </DialogHeader>

          {editRoleError && (
            <div className="p-3 rounded-lg border border-red-200 dark:border-red-900 bg-red-50 dark:bg-red-950/40 text-xs text-red-600 dark:text-red-400 flex items-center gap-2">
              <AlertCircle size={14} className="shrink-0" />
              <span>{editRoleError}</span>
            </div>
          )}

          <form onSubmit={handleSaveEditRole} className="space-y-4 pt-2">
            <div className="space-y-1.5">
              <label htmlFor="edit-role-name" className="text-xs font-medium text-foreground">
                {tr('roles.roleName')}
              </label>
              <Input
                id="edit-role-name"
                value={editRoleForm.name}
                onChange={e => setEditRoleForm({ ...editRoleForm, name: e.target.value })}
                placeholder="e.g. MODERATOR, STAFF, USER"
                className="h-8 text-xs font-mono uppercase"
                disabled={editingRole?.name === 'ADMIN' || editingRole?.name === 'SUPERADMIN'}
              />
              {(editingRole?.name === 'ADMIN' || editingRole?.name === 'SUPERADMIN') && (
                <p className="text-[11px] text-muted-foreground">
                  System core role identifier is protected and cannot be renamed.
                </p>
              )}
            </div>

            <div className="space-y-1.5">
              <label htmlFor="edit-role-desc" className="text-xs font-medium text-foreground">
                {tr('roles.roleDesc')}
              </label>
              <Textarea
                id="edit-role-desc"
                value={editRoleForm.description}
                onChange={e => setEditRoleForm({ ...editRoleForm, description: e.target.value })}
                placeholder="Describe what people with this role can do..."
                rows={3}
                className="text-xs resize-none"
              />
            </div>

            <DialogFooter className="pt-2">
              <Button type="button" variant="outline" size="sm" onClick={() => setEditRoleOpen(false)} disabled={savingEditRole}>
                {tr('common.cancel')}
              </Button>
              <Button type="submit" size="sm" disabled={savingEditRole} className="cursor-pointer">
                {savingEditRole ? (
                  <>
                    <RefreshCw size={13} className="mr-1.5 animate-spin" /> Saving...
                  </>
                ) : (
                  tr('common.save')
                )}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Delete Item Confirmation Dialog */}
      <AlertDialog open={Boolean(deleteConfirmItem)} onOpenChange={open => !open && setDeleteConfirmItem(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>
              {isRoles ? 'Delete Role?' : 'Delete Permission?'}
            </AlertDialogTitle>
            <AlertDialogDescription>
              {`Are you sure you want to permanently delete "${deleteConfirmItem?.name || `${deleteConfirmItem?.resource}:${deleteConfirmItem?.action}`}"? This action cannot be undone.`}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>{tr('common.cancel') || 'Cancel'}</AlertDialogCancel>
            <AlertDialogAction
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
              onClick={confirmRemove}
            >
              {isRoles ? 'Delete Role' : 'Delete Permission'}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

    </div>
  )
}
