import { useEffect, useState, useMemo } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { useApp } from '@/context/AppContext'
import { rolesApi, permissionsApi } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { Search, Shield, Info, ArrowLeft, RefreshCw, Inbox, Lock } from 'lucide-react'

export default function RoleDetailPage() {
  const { uuid } = useParams()
  const navigate = useNavigate()
  const { setBreadcrumbSub } = useApp()

  const [role, setRole] = useState(null)
  const [rolePerms, setRolePerms] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [filter, setFilter] = useState('')

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
    if (!isoStr) return '—'
    try {
      const d = new Date(isoStr)
      return d.toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' }) + ', ' +
        d.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })
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
        <div className="space-y-1">
          <div className="flex items-center gap-3 flex-wrap">
            <h1 className="text-2xl sm:text-3xl font-bold tracking-tight text-foreground uppercase">
              {role.name}
            </h1>
            <Badge
              variant="outline"
              className="text-[10px] font-normal border-slate-200 dark:border-slate-800 text-muted-foreground bg-muted/30 px-2 py-0.5 rounded"
            >
              {isBuiltIn ? 'Built-in' : 'Custom'}
            </Badge>
            <span className="inline-flex items-center gap-1.5 text-xs text-slate-700 dark:text-slate-300 font-medium">
              <span className="h-1.5 w-1.5 rounded-full bg-emerald-500"></span>
              {isActive ? 'Active' : 'Inactive'}
            </span>
          </div>

          <p className="text-xs text-muted-foreground">
            {role.description || 'System access role'}
          </p>
          <p className="text-[11px] text-muted-foreground">
            Last changed {formatDateTime(role.updatedAt || role.createdAt)}
          </p>
        </div>

        {/* Notice Banner matching screenshots 1 & 3 */}
        {isBuiltIn && (
          <div className="flex items-center gap-2 rounded-lg border border-slate-200/80 bg-slate-50/50 dark:border-slate-800 dark:bg-slate-900/40 p-3 text-xs text-slate-600 dark:text-slate-400">
            <Info size={14} className="shrink-0 text-slate-500 dark:text-slate-400" />
            <span className="flex items-center gap-1">
              <span>Built-in roles come with IAM and cannot be changed.</span>
            </span>
          </div>
        )}

        {/* Permissions Container Card matching screenshots 1 & 3 */}
        <div className="rounded-xl border border-border bg-card shadow-xs p-6 space-y-6">

          {/* Section Header */}
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-border/60">
            <div>
              <h2 className="text-sm font-semibold text-foreground">
                Permissions ({rolePerms.length})
              </h2>
              <p className="text-xs text-muted-foreground mt-0.5">
                Grouped by service.
              </p>
            </div>

            <div className="relative w-full sm:w-64">
              <Input
                placeholder="Filter permissions..."
                value={filter}
                onChange={e => setFilter(e.target.value)}
                className="h-8 text-xs bg-background border-border"
              />
            </div>
          </div>

          {/* Content: Empty State (Screenshot 1) vs Grouped Grid (Screenshot 3) */}
          {rolePerms.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-16 text-center">
              <div className="w-10 h-10 rounded-full border border-border flex items-center justify-center text-muted-foreground/50 mb-3">
                <Inbox size={18} strokeWidth={1.5} />
              </div>
              <p className="text-xs font-semibold text-foreground">
                This role has no permissions yet.
              </p>
            </div>
          ) : Object.keys(groupedPermissions).length === 0 ? (
            <div className="py-12 text-center text-xs text-muted-foreground">
              No permissions matching "{filter}".
            </div>
          ) : (
            <div className="space-y-6">
              {Object.entries(groupedPermissions).map(([service, perms]) => (
                <div key={service} className="space-y-3">
                  <h3 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                    {service}
                  </h3>
                  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                    {perms.map((p, i) => (
                      <div
                        key={i}
                        className="rounded-lg border border-border bg-background px-3 py-2 text-xs font-mono text-foreground shadow-xs hover:border-slate-300 dark:hover:border-slate-700 transition"
                      >
                        <span className="truncate block">{p.displayName}</span>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )}

        </div>

      </div>
    </div>
  )
}
