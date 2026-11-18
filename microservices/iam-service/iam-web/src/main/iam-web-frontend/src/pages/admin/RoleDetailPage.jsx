import { useEffect, useState, useMemo } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { useApp } from '@/context/AppContext'
import { rolesApi, permissionsApi } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Badge } from '@/components/ui/badge'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
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
  Search, Shield, Info, ArrowLeft, RefreshCw, Inbox, Lock,
  Pencil, AlertCircle, CheckCircle2, Plus, X, Key
} from 'lucide-react'

export default function RoleDetailPage() {
  const { uuid } = useParams()
  const navigate = useNavigate()
  const { setBreadcrumbSub, tr, lang } = useApp() || {}

  const [role, setRole] = useState(null)
  const [rolePerms, setRolePerms] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [filter, setFilter] = useState('')
  const [actionMsg, setActionMsg] = useState('')
  const [deleteConfirmPerm, setDeleteConfirmPerm] = useState(null)

  // Edit modal state
  const [editOpen, setEditOpen] = useState(false)
  const [editForm, setEditForm] = useState({ name: '', description: '' })
  const [savingEdit, setSavingEdit] = useState(false)
  const [editError, setEditError] = useState('')

  function openEditModal() {
    setEditForm({
      name: role?.name || '',
      description: role?.description || ''
    })
    setEditError('')
    setEditOpen(true)
  }

  async function handleSaveEdit(e) {
    e.preventDefault()
    if (!role) return
    if (!editForm.name.trim()) {
      setEditError('Role name is required')
      return
    }
    setSavingEdit(true)
    setEditError('')
    try {
      await rolesApi.update(uuid, {
        name: editForm.name.trim().toUpperCase(),
        description: editForm.description?.trim() || ''
      })
      setEditOpen(false)
      setActionMsg('Role updated successfully.')
      loadRole()
    } catch (err) {
      setEditError(err.message || 'Failed to update role')
    } finally {
      setSavingEdit(false)
    }
  }

  // Add permission modal state
  const [addPermOpen, setAddPermOpen] = useState(false)
  const [allPerms, setAllPerms] = useState([])
  const [selectedPermUuid, setSelectedPermUuid] = useState('')
  const [addingPerm, setAddingPerm] = useState(false)
  const [addPermError, setAddPermError] = useState('')

  async function openAddPermissionModal() {
    setAddPermError('')
    setSelectedPermUuid('')
    setAddPermOpen(true)
    try {
      const res = await permissionsApi.list(0, 200)
      const list = Array.isArray(res) ? res : (res?.content || [])
      setAllPerms(list)
    } catch (e) {
      console.error('Failed to load permissions:', e)
    }
  }

  async function handleAddPermission(e) {
    e.preventDefault()
    if (!selectedPermUuid) return
    setAddingPerm(true)
    setAddPermError('')
    try {
      const currentUuids = rolePerms.map(p => p.uuid || p)
      if (!currentUuids.includes(selectedPermUuid)) {
        await rolesApi.assignPermissions(uuid, [...currentUuids, selectedPermUuid])
      }
      setAddPermOpen(false)
      setSelectedPermUuid('')
      setActionMsg(lang === 'km' ? 'បានបន្ថែមសិទ្ធិដោយជោគជ័យ។' : 'Permission added successfully.')
      setTimeout(() => setActionMsg(''), 4000)
      loadRole()
    } catch (err) {
      setAddPermError(err.message || 'Failed to add permission')
    } finally {
      setAddingPerm(false)
    }
  }

  function handleRevokePermission(permUuid, permName) {
    setDeleteConfirmPerm({ permUuid, permName })
  }

  async function confirmRevokePermission() {
    if (!deleteConfirmPerm) return
    try {
      await rolesApi.revokePermission(uuid, deleteConfirmPerm.permUuid)
      setActionMsg(lang === 'km' ? 'បានដកសិទ្ធិដោយជោគជ័យ។' : 'Permission removed.')
      setTimeout(() => setActionMsg(''), 4000)
      setDeleteConfirmPerm(null)
      loadRole()
    } catch (err) {
      setDeleteConfirmPerm(null)
      setError(err.message || 'Failed to revoke permission')
      setTimeout(() => setError(''), 4000)
    }
  }

  function loadRole() {
    setLoading(true)
    setError('')
    Promise.allSettled([
      rolesApi.get(uuid),
      rolesApi.permissions(uuid)
    ]).then(([rRes, pRes]) => {
      if (rRes.status === 'fulfilled' && rRes.value) {
        setRole(rRes.value)
        if (rRes.value.name) {
          setBreadcrumbSub(rRes.value.name.toUpperCase())
        }
      } else {
        setError('Failed to load role: ' + (rRes.reason?.message || 'Not found'))
      }

      if (pRes.status === 'fulfilled') {
        const list = Array.isArray(pRes.value) ? pRes.value : (pRes.value?.content || [])
        setRolePerms(list)
      }
    }).finally(() => setLoading(false))
  }

  useEffect(() => {
    loadRole()
  }, [uuid])

  // Group permissions by service prefix (e.g. "document", "high-degree", "iam")
  const groupedPermissions = useMemo(() => {
    const groups = {}
    const filtered = rolePerms.filter(p => {
      const name = p.name || `${p.resource}:${p.action}`
      return name.toLowerCase().includes(filter.toLowerCase())
    })

    filtered.forEach(p => {
      const name = p.name || `${p.resource}:${p.action}`
      let service = 'GENERAL'
      if (name.includes(':')) {
        service = name.split(':')[0]
      } else if (p.resource && p.resource.includes(':')) {
        service = p.resource.split(':')[0]
      } else if (p.resource) {
        service = p.resource
      }
      service = service.toUpperCase()
      if (!groups[service]) groups[service] = []
      groups[service].push({ ...p, displayName: name })
    })

    // Sort entries with e-commerce services priority: IAM, CUSTOMER, BUSINESS, ORDER, PAYMENT, MEDIA
    const priority = ['IAM', 'CUSTOMER', 'BUSINESS', 'ORDER', 'PAYMENT', 'MEDIA']
    const sortedEntries = Object.entries(groups).sort(([a], [b]) => {
      const idxA = priority.indexOf(a)
      const idxB = priority.indexOf(b)
      if (idxA !== -1 && idxB !== -1) return idxA - idxB
      if (idxA !== -1) return -1
      if (idxB !== -1) return 1
      return a.localeCompare(b)
    })

    return Object.fromEntries(sortedEntries)
  }, [rolePerms, filter])

  function formatDateTime(isoStr) {
    if (!isoStr) return null
    try {
      const d = new Date(isoStr)
      if (isNaN(d.getTime())) return null
      return d.toLocaleDateString(lang === 'km' ? 'km-KH' : 'en-GB', { day: 'numeric', month: 'short', year: 'numeric' }) + ', ' +
        d.toLocaleTimeString(lang === 'km' ? 'km-KH' : 'en-GB', { hour: '2-digit', minute: '2-digit', hour12: false })
    } catch {
      return null
    }
  }

  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-7 w-7 animate-spin rounded-full border-2 border-blue-600 border-t-transparent" />
      </div>
    )
  }

  if (error || !role) {
    return (
      <div className="p-8 max-w-4xl mx-auto space-y-4">
        <div className="rounded-lg bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-900 p-4 text-sm text-red-600 dark:text-red-400">
          {error || 'Role not found.'}
        </div>
        <Button variant="outline" onClick={() => navigate('/admin/roles')}>
          &larr; Back to roles
        </Button>
      </div>
    )
  }

  const isBuiltIn = ['SUPERADMIN', 'ADMIN', 'MERCHANT', 'CUSTOMER', 'USER'].includes(role.name?.toUpperCase()) || role.builtin || !role.custom
  const isActive = role.active !== false

  return (
    <div className="min-h-full bg-background p-6 md:p-8">
      <div className="mx-auto max-w-6xl space-y-6">

        {/* Header Block matching screenshots 1 & 3 */}
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div className="space-y-1">
            <div className="flex items-center gap-3 flex-wrap">
              <h1 className="text-2xl sm:text-3xl font-bold tracking-tight text-foreground uppercase">
                {role.name}
              </h1>
              <Badge
                variant="outline"
                className="text-[10px] font-normal border-slate-200 dark:border-slate-800 text-muted-foreground bg-muted/30 px-2 py-0.5 rounded"
              >
                {isBuiltIn ? (tr ? tr('roleDetail.builtIn') : 'Built-in') : (tr ? tr('roleDetail.custom') : 'Custom')}
              </Badge>
              <span className="inline-flex items-center gap-1.5 text-xs text-slate-700 dark:text-slate-300 font-medium">
                <span className="h-1.5 w-1.5 rounded-full bg-emerald-500"></span>
                {isActive ? (tr ? tr('roleDetail.active') : 'Active') : (tr ? tr('roleDetail.inactive') : 'Inactive')}
              </span>
            </div>

            <p className="text-xs text-muted-foreground leading-relaxed mt-1">
              {role.description || (tr ? tr('roleDetail.systemRole') : 'System access role')}
            </p>

            {(role.updatedAt || role.createdAt) ? (
              <p className="text-[11px] text-muted-foreground/80 leading-relaxed pt-0.5">
                {role.updatedAt
                  ? `${tr ? tr('roleDetail.lastChanged') : 'Last changed'} ${formatDateTime(role.updatedAt)}`
                  : `${tr ? tr('roleDetail.created') : 'Created'} ${formatDateTime(role.createdAt)}`}
              </p>
            ) : isBuiltIn ? (
              <p className="text-[11px] text-muted-foreground/80 leading-relaxed pt-0.5">
                {tr ? tr('roleDetail.systemPredefined') : 'System predefined'}
              </p>
            ) : null}
          </div>

          <Button
            variant="outline"
            size="sm"
            onClick={openEditModal}
            className="text-xs h-8 cursor-pointer shrink-0"
          >
            <Pencil size={13} className="mr-1.5 text-muted-foreground" />
            {tr ? tr('roleDetail.editRole') : 'Edit Role'}
          </Button>
        </div>

        {actionMsg && (
          <div className="flex items-center gap-2 rounded-lg border border-emerald-200 bg-emerald-50/70 p-3 text-xs text-emerald-800 dark:border-emerald-800 dark:bg-emerald-950/30 dark:text-emerald-300">
            <CheckCircle2 size={15} /> {actionMsg}
          </div>
        )}

        {/* Notice Banner matching screenshots 1 & 3 */}
        {isBuiltIn && (
          <div className="flex items-center gap-2 rounded-lg border border-slate-200/80 bg-slate-50/50 dark:border-slate-800 dark:bg-slate-900/40 p-3 text-xs text-slate-600 dark:text-slate-400">
            <Info size={14} className="shrink-0 text-slate-500 dark:text-slate-400" />
            <span className="flex items-center gap-1">
              <span>{tr ? tr('roleDetail.builtInNotice') : 'Built-in roles come with IAM and cannot be changed.'}</span>
            </span>
          </div>
        )}

        {/* Permissions Container Card matching screenshots 1 & 3 */}
        <div className="rounded-xl border border-border bg-card shadow-xs p-6 space-y-6">

          {/* Section Header */}
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-border/60">
            <div>
              <h2 className="text-sm font-semibold text-foreground">
                {tr ? tr('roleDetail.permissions') : 'Permissions'} ({rolePerms.length})
              </h2>
              <p className="text-xs text-muted-foreground mt-0.5">
                {tr ? tr('roleDetail.groupedByService') : 'Grouped by service.'}
              </p>
            </div>

            <div className="flex items-center gap-2.5 w-full sm:w-auto">
              <div className="relative w-full sm:w-64">
                <Input
                  placeholder={tr ? tr('roleDetail.filterPlaceholder') : 'Filter permissions...'}
                  value={filter}
                  onChange={e => setFilter(e.target.value)}
                  className="h-8 text-xs bg-background border-border"
                />
              </div>
              <Button
                size="sm"
                onClick={openAddPermissionModal}
                className="h-8 text-xs gap-1.5 cursor-pointer bg-primary text-primary-foreground hover:bg-primary/90 shrink-0 font-medium"
              >
                <Plus size={14} />
                <span>{tr ? tr('roleDetail.addPermission') : 'Add permission'}</span>
              </Button>
            </div>
          </div>

          {/* Content: Empty State (Screenshot 1) vs Grouped Grid (Screenshot 3) */}
          {rolePerms.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-16 text-center">
              <div className="w-10 h-10 rounded-full border border-border flex items-center justify-center text-muted-foreground/50 mb-3">
                <Inbox size={18} strokeWidth={1.5} />
              </div>
              <p className="text-xs font-semibold text-foreground">
                {tr ? tr('roleDetail.noPermissions') : 'This role has no permissions yet.'}
              </p>
            </div>
          ) : Object.keys(groupedPermissions).length === 0 ? (
            <div className="py-12 text-center text-xs text-muted-foreground">
              {tr ? tr('roleDetail.noMatchFilter') : `No permissions matching "${filter}".`}
            </div>
          ) : (
            <div className="space-y-6">
              {Object.entries(groupedPermissions).map(([service, perms]) => (
                <div key={service} className="space-y-3">
                  <h3 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                    {service}
                  </h3>
                  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                    {perms.map((p, i) => {
                      const pUuid = p.uuid || p.id
                      return (
                        <div
                          key={i}
                          className="flex items-center justify-between rounded-lg border border-border bg-background px-3 py-2 text-xs font-mono text-foreground shadow-xs hover:border-slate-300 dark:hover:border-slate-700 transition group"
                        >
                          <span className="truncate block font-medium">{p.displayName}</span>
                          {pUuid && (
                            <button
                              type="button"
                              onClick={() => handleRevokePermission(pUuid, p.displayName)}
                              className="text-muted-foreground/50 hover:text-red-500 opacity-0 group-hover:opacity-100 transition-opacity ml-2 shrink-0 cursor-pointer p-0.5 rounded hover:bg-muted"
                              title={lang === 'km' ? 'ដកសិទ្ធិនេះ' : 'Remove permission'}
                            >
                              <X size={13} />
                            </button>
                          )}
                        </div>
                      )
                    })}
                  </div>
                </div>
              ))}
            </div>
          )}

        </div>

      </div>

      {/* Edit Role Dialog */}
      <Dialog open={editOpen} onOpenChange={setEditOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="text-base font-semibold text-foreground flex items-center gap-2">
              <Pencil size={15} className="text-amber-500" />
              <span>{tr ? tr('roleDetail.editRole') : 'Edit Role'} {role?.name ? `(${role.name})` : ''}</span>
            </DialogTitle>
            <DialogDescription className="text-xs text-muted-foreground">
              {lang === 'km' ? 'ធ្វើបច្ចុប្បន្នភាពឈ្មោះ ឬការពិពណ៌នាតួនាទីនេះ។' : 'Update the name or description for this role.'}
            </DialogDescription>
          </DialogHeader>

          {editError && (
            <div className="p-3 rounded-lg border border-red-200 dark:border-red-900 bg-red-50 dark:bg-red-950/40 text-xs text-red-600 dark:text-red-400 flex items-center gap-2">
              <AlertCircle size={14} className="shrink-0" />
              <span>{editError}</span>
            </div>
          )}

          <form onSubmit={handleSaveEdit} className="space-y-4 pt-2">
            <div className="space-y-1.5">
              <Label htmlFor="edit-role-name" className="text-xs font-medium text-foreground">
                {tr ? tr('roles.roleName') : 'Role Name'}
              </Label>
              <Input
                id="edit-role-name"
                value={editForm.name}
                onChange={e => setEditForm({ ...editForm, name: e.target.value })}
                placeholder="e.g. MODERATOR, STAFF, USER"
                className="h-8 text-xs font-mono uppercase"
                disabled={role?.name === 'ADMIN' || role?.name === 'SUPERADMIN'}
              />
              {(role?.name === 'ADMIN' || role?.name === 'SUPERADMIN') && (
                <p className="text-[11px] text-muted-foreground">
                  {lang === 'km' ? 'ឈ្មោះតួនាទីស្នូលរបស់ប្រព័ន្ធត្រូវបានការពារ និងមិនអាចប្តូរឈ្មោះបានទេ។' : 'System core role identifier is protected and cannot be renamed.'}
                </p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="edit-role-desc" className="text-xs font-medium text-foreground">
                {tr ? tr('roles.roleDesc') : 'Description'}
              </Label>
              <Textarea
                id="edit-role-desc"
                value={editForm.description}
                onChange={e => setEditForm({ ...editForm, description: e.target.value })}
                placeholder={lang === 'km' ? 'ពិពណ៌នាអំពីអ្វីដែលតួនាទីនេះអាចធ្វើបាន...' : 'Describe what people with this role can do...'}
                rows={3}
                className="text-xs resize-none"
              />
            </div>

            <DialogFooter className="pt-2">
              <Button type="button" variant="outline" size="sm" onClick={() => setEditOpen(false)} disabled={savingEdit}>
                {tr ? tr('common.cancel') : 'Cancel'}
              </Button>
              <Button type="submit" size="sm" disabled={savingEdit} className="cursor-pointer">
                {savingEdit ? (
                  <>
                    <RefreshCw size={13} className="mr-1.5 animate-spin" /> {lang === 'km' ? 'កំពុងរក្សាទុក...' : 'Saving...'}
                  </>
                ) : (
                  tr ? tr('common.save') : 'Save Changes'
                )}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Add Permission Dialog */}
      <Dialog
        open={addPermOpen}
        onOpenChange={(open) => {
          setAddPermOpen(open)
          if (!open) {
            setSelectedPermUuid('')
            setAddPermError('')
          }
        }}
      >
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="text-base font-semibold text-foreground flex items-center gap-2">
              <Key className="h-4 w-4 text-primary" />
              <span>{tr ? tr('roleDetail.addPermission') : 'Add Permission'}</span>
            </DialogTitle>
            <DialogDescription className="text-xs text-muted-foreground">
              {tr ? tr('roleDetail.addPermissionDesc') : 'Select and assign a permission to this role.'}
            </DialogDescription>
          </DialogHeader>

          {addPermError && (
            <div className="p-3 rounded-lg border border-red-200 dark:border-red-900 bg-red-50 dark:bg-red-950/40 text-xs text-red-600 dark:text-red-400 flex items-center gap-2">
              <AlertCircle size={14} className="shrink-0" />
              <span>{addPermError}</span>
            </div>
          )}

          <form onSubmit={handleAddPermission} className="space-y-4 pt-2">
            <div className="space-y-1.5">
              <Label className="text-xs font-medium text-foreground">
                {tr ? tr('roleDetail.selectPermission') : 'Select Permission'}
              </Label>
              <Select value={selectedPermUuid || undefined} onValueChange={setSelectedPermUuid}>
                <SelectTrigger className="w-full h-9 text-xs rounded-lg border-border bg-background hover:bg-muted/30 focus:ring-2 focus:ring-primary/20 transition-colors">
                  <SelectValue placeholder={tr ? tr('roleDetail.choosePermission') : 'Choose a permission to add...'} />
                </SelectTrigger>
                <SelectContent className="max-h-60">
                  {allPerms
                    .filter(p => p.uuid && !rolePerms.some(rp => (rp.uuid || rp) === p.uuid))
                    .map(p => (
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
              <Button type="button" variant="outline" size="sm" onClick={() => setAddPermOpen(false)} disabled={addingPerm}>
                {tr ? tr('common.cancel') : 'Cancel'}
              </Button>
              <Button type="submit" size="sm" disabled={!selectedPermUuid || addingPerm} className="cursor-pointer bg-primary text-primary-foreground hover:bg-primary/90">
                {addingPerm ? (
                  <>
                    <RefreshCw size={13} className="mr-1.5 animate-spin" /> {lang === 'km' ? 'កំពុងបន្ថែម...' : 'Adding...'}
                  </>
                ) : (
                  tr ? tr('roleDetail.addPermission') : 'Add Permission'
                )}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Revoke Permission Confirmation Dialog */}
      <AlertDialog open={Boolean(deleteConfirmPerm)} onOpenChange={open => !open && setDeleteConfirmPerm(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>
              {lang === 'km' ? 'តើអ្នកពិតជាចង់ដកសិទ្ធិនេះមែនទេ?' : 'Remove Permission?'}
            </AlertDialogTitle>
            <AlertDialogDescription>
              {lang === 'km'
                ? `តើអ្នកពិតជាចង់ដកសិទ្ធិ "${deleteConfirmPerm?.permName}" ពីតួនាទីនេះមែនទេ?`
                : `Are you sure you want to remove permission "${deleteConfirmPerm?.permName}" from this role?`}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>{tr('common.cancel') || (lang === 'km' ? 'បោះបង់' : 'Cancel')}</AlertDialogCancel>
            <AlertDialogAction
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
              onClick={confirmRevokePermission}
            >
              {lang === 'km' ? 'ដកសិទ្ធិ' : 'Remove Permission'}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

    </div>
  )
}
