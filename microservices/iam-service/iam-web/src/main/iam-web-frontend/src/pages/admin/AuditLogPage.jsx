import { useState, useEffect, useMemo } from 'react'
import { useApp } from '@/context/AppContext'
import { adminAuditApi } from '@/lib/api'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { TablePagination } from '@/components/ui/table-pagination'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Search, User } from 'lucide-react'

export default function AuditLogPage() {
  const { tr } = useApp()
  const [logs, setLogs] = useState([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [typeFilter, setTypeFilter] = useState('ALL')
  const [actionFilter, setActionFilter] = useState('ALL')
  const [timeFilter, setTimeFilter] = useState('ALL')
  const [currentPage, setCurrentPage] = useState(1)
  const [rowsPerPage, setRowsPerPage] = useState(20)

  function loadLogs() {
    setLoading(true)
    adminAuditApi.list(200)
      .then(data => {
        setLogs(Array.isArray(data) ? data : [])
      })
      .catch(err => {
        console.error('Failed to load audit logs:', err)
        setLogs([])
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadLogs()
  }, [])

  // Collect unique action names dynamically from backend loaded logs
  const availableActions = useMemo(() => {
    const set = new Set()
    logs.forEach(l => {
      if (l.action) set.add(l.action)
    })
    return Array.from(set).sort()
  }, [logs])

  const filtered = useMemo(() => {
    return logs.filter(log => {
      const q = search.toLowerCase()
      const matchSearch = !q ||
        (log.changed && log.changed.toLowerCase().includes(q)) ||
        (log.by && log.by.toLowerCase().includes(q)) ||
        (log.action && log.action.toLowerCase().includes(q))

      const matchAction = actionFilter === 'ALL' || log.action === actionFilter
      const matchType = typeFilter === 'ALL' || log.type === typeFilter

      let matchTime = true
      if (timeFilter === 'TODAY') {
        matchTime = log.whenRelative?.toLowerCase().includes('hour') ||
          log.whenRelative?.toLowerCase().includes('minute') ||
          log.whenRelative?.toLowerCase().includes('just now')
      } else if (timeFilter === 'WEEK') {
        matchTime = !log.whenRelative?.toLowerCase().includes('month') &&
          !log.whenRelative?.toLowerCase().includes('year')
      } else if (timeFilter === 'MONTH') {
        matchTime = !log.whenRelative?.toLowerCase().includes('year')
      }

      return matchSearch && matchAction && matchType && matchTime
    })
  }, [logs, search, typeFilter, actionFilter, timeFilter])

  const totalItems = filtered.length
  const totalPages = Math.max(1, Math.ceil(totalItems / rowsPerPage))
  const startIndex = (currentPage - 1) * rowsPerPage
  const paginatedLogs = filtered.slice(startIndex, startIndex + rowsPerPage)

  function getActionBadgeStyle(action) {
    const act = (action || '').toLowerCase()
    if (act.includes('role') || act.includes('assign')) {
      return 'bg-emerald-50 text-emerald-700 border-emerald-200/80 dark:bg-emerald-950/40 dark:text-emerald-300 dark:border-emerald-800/60'
    }
    if (act.includes('create') || act.includes('user')) {
      return 'bg-slate-50 text-slate-700 border-slate-200 dark:bg-slate-900 dark:text-slate-300 dark:border-slate-800'
    }
    if (act.includes('reset') || act.includes('password') || act.includes('security')) {
      return 'bg-blue-50 text-blue-700 border-blue-200 dark:bg-blue-950/40 dark:text-blue-300 dark:border-blue-800'
    }
    if (act.includes('terminate') || act.includes('delete') || act.includes('deactivate')) {
      return 'bg-rose-50 text-rose-700 border-rose-200 dark:bg-rose-950/40 dark:text-rose-300 dark:border-rose-800'
    }
    return 'bg-slate-50 text-slate-700 border-slate-200 dark:bg-slate-900 dark:text-slate-300 dark:border-slate-800'
  }

  return (
    <div className="min-h-full bg-background p-6 md:p-8">
      <div className="mx-auto max-w-6xl space-y-6">

        {/* Page Header matching screenshot */}
        <div className="space-y-1">
          <h1 className="text-2xl font-bold tracking-tight text-foreground">
            {tr('audit.title')}
          </h1>
          <p className="text-xs text-muted-foreground">
            {tr('audit.subtitle')}
          </p>
        </div>

        {/* Toolbar matching screenshot */}
        <div className="flex flex-col sm:flex-row items-center gap-3">
          <div className="relative flex-1 w-full">
            <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
            <Input
              placeholder={tr('audit.searchPlaceholder')}
              value={search}
              onChange={e => { setSearch(e.target.value); setCurrentPage(1) }}
              className="h-9 pl-9 text-xs bg-card border-border rounded-lg shadow-xs"
            />
          </div>

          <div className="flex items-center gap-2 w-full sm:w-auto flex-wrap">
            {/* Dropdown 1: All types */}
            <Select
              value={typeFilter}
              onValueChange={val => { setTypeFilter(val); setCurrentPage(1) }}
            >
              <SelectTrigger className="w-[130px] h-9 text-xs">
                <SelectValue placeholder={tr('audit.allTypes')} />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ALL">{tr('audit.allTypes')}</SelectItem>
                <SelectItem value="user">{tr('audit.typeUser')}</SelectItem>
                <SelectItem value="role">{tr('audit.typeRole')}</SelectItem>
                <SelectItem value="client">{tr('audit.typeClient')}</SelectItem>
              </SelectContent>
            </Select>

            {/* Dropdown 2: All actions */}
            <Select
              value={actionFilter}
              onValueChange={val => { setActionFilter(val); setCurrentPage(1) }}
            >
              <SelectTrigger className="w-[140px] h-9 text-xs">
                <SelectValue placeholder={tr('audit.allActions')} />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ALL">{tr('audit.allActions')}</SelectItem>
                {availableActions.map(act => (
                  <SelectItem key={act} value={act}>{act}</SelectItem>
                ))}
              </SelectContent>
            </Select>

            {/* Dropdown 3: Any time */}
            <Select
              value={timeFilter}
              onValueChange={val => { setTimeFilter(val); setCurrentPage(1) }}
            >
              <SelectTrigger className="w-[130px] h-9 text-xs">
                <SelectValue placeholder={tr('audit.anyTime')} />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ALL">{tr('audit.anyTime')}</SelectItem>
                <SelectItem value="TODAY">{tr('audit.today')}</SelectItem>
                <SelectItem value="WEEK">{tr('audit.week')}</SelectItem>
                <SelectItem value="MONTH">{tr('audit.month')}</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        {/* Table matching screenshot */}
        <div className="rounded-xl border border-border bg-card shadow-xs overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="border-b border-border/80 bg-transparent text-[11px] font-semibold text-muted-foreground">
                <tr>
                  <th className="py-3 px-4 w-[240px]">{tr('audit.when')}</th>
                  <th className="py-3 px-4 w-[200px]">{tr('audit.action')}</th>
                  <th className="py-3 px-4">{tr('audit.changed')}</th>
                  <th className="py-3 px-4 w-[180px]">{tr('audit.by')}</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {loading ? (
                  <tr>
                    <td colSpan={4} className="py-12 text-center text-muted-foreground">
                      <div className="flex justify-center">
                        <div className="h-6 w-6 animate-spin rounded-full border-2 border-blue-600 border-t-transparent" />
                      </div>
                    </td>
                  </tr>
                ) : filtered.length === 0 ? (
                  <tr>
                    <td colSpan={4} className="py-12 text-center text-muted-foreground">
                      {tr('audit.noRecords')}
                    </td>
                  </tr>
                ) : (
                  paginatedLogs.map(row => (
                    <tr key={row.id} className="hover:bg-muted/30 transition-colors">
                      {/* When */}
                      <td className="py-3.5 px-4">
                        <p className="font-semibold text-foreground text-xs leading-none">{row.whenRelative}</p>
                        <p className="text-[11px] text-muted-foreground mt-1 leading-none">{row.whenDate}</p>
                      </td>

                      {/* Action */}
                      <td className="py-3.5 px-4">
                        <span
                          className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-medium border ${getActionBadgeStyle(row.action)}`}
                        >
                          {row.action}
                        </span>
                      </td>

                      {/* Changed */}
                      <td className="py-3.5 px-4">
                        <div className="flex items-center gap-2">
                          <User size={13} className="text-muted-foreground stroke-[1.8]" />
                          <span className="text-xs text-foreground font-medium">
                            {row.changed}
                          </span>
                        </div>
                      </td>

                      {/* By */}
                      <td className="py-3.5 px-4 text-xs text-muted-foreground">
                        {row.by}
                      </td>
                    </tr>
                  ))
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
            pageSizeOptions={[10, 20, 50, 100]}
          />
        </div>

      </div>
    </div>
  )
}
