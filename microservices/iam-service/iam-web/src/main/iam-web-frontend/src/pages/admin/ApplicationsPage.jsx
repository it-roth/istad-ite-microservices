import { useEffect, useState, useMemo } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useApp } from '@/context/AppContext'
import { clientsApi } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { TablePagination } from '@/components/ui/table-pagination'
import { cn } from '@/lib/utils'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  Plus, Search, AppWindow, MoreHorizontal, Trash2,
  ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight, AlertCircle, RefreshCw
} from 'lucide-react'

export default function ApplicationsPage() {
  const navigate = useNavigate()
  const { tr } = useApp()
  const [clients, setClients] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [search, setSearch] = useState('')

  // Pagination state
  const [rowsPerPage, setRowsPerPage] = useState(20)
  const [currentPage, setCurrentPage] = useState(1)

  function loadClients() {
    setLoading(true)
    setError('')
    clientsApi.list()
      .then(data => {
        setClients(Array.isArray(data) ? data : [])
      })
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadClients()
  }, [])

  async function handleDelete(client, e) {
    e.stopPropagation()
    const id = client.id || client.clientId
    if (!window.confirm(`Are you sure you want to delete application "${client.clientName || client.clientId}"?`)) return
    try {
      await clientsApi.delete(id)
      loadClients()
    } catch (err) {
      alert(err.message)
    }
  }

  const filteredClients = useMemo(() => {
    return clients.filter(c => {
      const q = search.toLowerCase()
      const name = (c.clientName || '').toLowerCase()
      const cid = (c.clientId || '').toLowerCase()
      return !q || name.includes(q) || cid.includes(q)
    })
  }, [clients, search])

  const totalItems = filteredClients.length
  const startIndex = (currentPage - 1) * rowsPerPage
  const paginatedClients = filteredClients.slice(startIndex, startIndex + rowsPerPage)
  const totalPages = Math.max(1, Math.ceil(totalItems / rowsPerPage))

  function formatGrantType(gt) {
    if (gt === 'authorization_code') return 'Authorization code'
    if (gt === 'refresh_token') return 'Refresh token'
    if (gt === 'client_credentials') return 'Client credentials'
    return gt.replace(/_/g, ' ')
  }

  function formatDate(isoStr) {
    if (!isoStr) return '11 Nov 2026'
    try {
      return new Date(isoStr).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })
    } catch {
      return isoStr
    }
  }

  return (
    <div className="min-h-full bg-background p-6 md:p-8">
      <div className="mx-auto max-w-6xl space-y-6">

        {/* Top Header matching screenshot */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-border/60">
          <div>
            <h1 className="text-2xl sm:text-3xl font-bold tracking-tight text-foreground">
              {tr('apps.title')}
            </h1>
            <p className="text-xs sm:text-sm text-muted-foreground mt-1">
              {tr('apps.subtitle')}
            </p>
          </div>
          <div>
            <Button
              size="sm"
              onClick={() => navigate('/admin/clients/new')}
              className="gap-1.5 cursor-pointer"
            >
              <Plus size={14} /> {tr('apps.registerApp')}
            </Button>
          </div>
        </div>

        {error && (
          <div className="flex items-center gap-2 rounded-lg border border-red-200 bg-red-50/70 p-3 text-xs text-red-700 dark:border-red-900 dark:bg-red-950/40 dark:text-red-300">
            <AlertCircle size={15} /> {error}
          </div>
        )}

        {/* Search bar matching screenshot */}
        <div className="relative w-full max-w-md">
          <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder={tr('apps.searchPlaceholder')}
            value={search}
            onChange={e => { setSearch(e.target.value); setCurrentPage(1) }}
            className="h-9 pl-9 text-xs bg-background border-border"
          />
        </div>

        {/* Table matching screenshot */}
        <div className="rounded-lg border border-border bg-card shadow-xs overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="border-b border-border bg-muted/40 text-[11px] font-semibold text-muted-foreground">
                <tr>
                  <th className="py-3 px-4 w-[280px]">{tr('apps.application')}</th>
                  <th className="py-3 px-4 w-[120px]">{tr('apps.type')}</th>
                  <th className="py-3 px-4">{tr('apps.grantTypes')}</th>
                  <th className="py-3 px-4 w-[130px]">{tr('apps.registered')}</th>
                  <th className="py-3 px-4 w-10 text-right"></th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {loading ? (
                  <tr>
                    <td colSpan={5} className="py-12 text-center text-muted-foreground">
                      <div className="flex justify-center">
                        <div className="h-6 w-6 animate-spin rounded-full border-2 border-blue-600 border-t-transparent" />
                      </div>
                    </td>
                  </tr>
                ) : paginatedClients.length === 0 ? (
                  <tr>
                    <td colSpan={5} className="py-12 text-center text-muted-foreground">
                      No applications registered yet.
                    </td>
                  </tr>
                ) : (
                  paginatedClients.map(client => {
                    const authMethods = client.authenticationMethods || client.clientAuthenticationMethods || []
                    const isPublic = authMethods.includes('none') && authMethods.length === 1
                    const isConfidential = !isPublic

                    const grants = client.grantTypes || client.authorizationGrantTypes || ['authorization_code']

                    return (
                      <tr
                        key={client.clientId || client.id}
                        className="hover:bg-muted/30 transition-colors group"
                      >
                        {/* Application Icon + Name + Client ID */}
                        <td className="py-3.5 px-4 font-medium">
                          <div className="flex items-center gap-3">
                            <div className="h-8 w-8 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 flex items-center justify-center shrink-0 border border-slate-200 dark:border-slate-700">
                              <AppWindow size={15} />
                            </div>
                            <div className="min-w-0">
                              <p className="font-semibold text-foreground text-xs truncate">
                                {client.clientName || client.clientId}
                              </p>
                              <p className="font-mono text-[11px] text-muted-foreground truncate">
                                {client.clientId}
                              </p>
                            </div>
                          </div>
                        </td>

                        {/* Type */}
                        <td className="py-3.5 px-4">
                          <Badge
                            variant="outline"
                            className={cn(
                              'text-[10px] font-medium px-2.5 py-0.5 rounded-full border',
                              isConfidential
                                ? 'border-blue-200 bg-blue-50 text-blue-600 dark:border-blue-800 dark:bg-blue-950/40 dark:text-blue-400 font-semibold'
                                : 'border-slate-200 dark:border-slate-800 text-muted-foreground bg-muted/30'
                            )}
                          >
                            {isConfidential ? tr('apps.confidential') : tr('apps.public')}
                          </Badge>
                        </td>

                        {/* Grant types */}
                        <td className="py-3.5 px-4">
                          <div className="flex flex-wrap gap-1.5">
                            {Array.from(grants).map(gt => (
                              <span
                                key={gt}
                                className="px-2 py-0.5 rounded-full border border-border text-[11px] font-medium text-foreground bg-muted/30"
                              >
                                {formatGrantType(gt)}
                              </span>
                            ))}
                          </div>
                        </td>

                        {/* Registered */}
                        <td className="py-3.5 px-4 text-xs text-muted-foreground">
                          {formatDate(client.clientIdIssuedAt || client.createdAt)}
                        </td>

                        {/* Actions Menu */}
                        <td className="py-3.5 px-4 text-right">
                          <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                              <Button variant="ghost" size="icon" className="h-7 w-7 text-muted-foreground hover:text-foreground">
                                <MoreHorizontal size={14} />
                              </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end" className="w-36 text-xs">
                              <DropdownMenuItem
                                onClick={(e) => handleDelete(client, e)}
                                className="text-rose-600 focus:text-rose-600 cursor-pointer gap-2"
                              >
                                <Trash2 size={13} /> {tr('apps.deleteClient')}
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
            totalItems={totalItems}
            page={currentPage}
            pageSize={rowsPerPage}
            onPageChange={setCurrentPage}
            onPageSizeChange={setRowsPerPage}
            pageSizeOptions={[10, 20, 50]}
          />
        </div>

      </div>
    </div>
  )
}
