import { useState, useEffect, useMemo } from 'react'
import { useApp } from '@/context/AppContext'
import { meApi } from '@/lib/api'
import { parseUserAgent } from '@/lib/deviceParser'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'
import { TablePagination } from '@/components/ui/table-pagination'
import { CheckCircle2, XCircle, Search, RefreshCw } from 'lucide-react'

export default function LoginHistoryPage() {
  const { tr, lang } = useApp()
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [error, setError] = useState(null)
  const [currentPage, setCurrentPage] = useState(1)
  const [rowsPerPage, setRowsPerPage] = useState(10)

  function loadHistory() {
    setLoading(true)
    setError(null)
    meApi.loginHistory(200)
      .then(data => {
        setHistory(Array.isArray(data) ? data : [])
      })
      .catch(() => {
        setHistory([])
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadHistory()
  }, [])

  const filtered = useMemo(() => {
    return history.filter(h => {
      const s = search.toLowerCase()
      return (
        !search ||
        (h.method && h.method.toLowerCase().includes(s)) ||
        (h.userAgent && h.userAgent.toLowerCase().includes(s)) ||
        (h.ipAddress && h.ipAddress.includes(s)) ||
        (h.location && h.location.toLowerCase().includes(s))
      )
    })
  }, [history, search])

  const paginated = useMemo(() => {
    return filtered.slice((currentPage - 1) * rowsPerPage, currentPage * rowsPerPage)
  }, [filtered, currentPage, rowsPerPage])

  const successCount = history.filter(h => h.success).length
  const failedCount = history.filter(h => !h.success).length

  return (
    <div className="p-6 max-w-5xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold">{tr('history.title')}</h1>
        <p className="text-muted-foreground text-sm mt-1">{tr('history.subtitle')}</p>
      </div>

      {error && (
        <div className="mb-4 rounded-lg bg-destructive/10 p-3 text-xs text-destructive">
          Failed to load history from database: {error}
        </div>
      )}

      {/* Real Stats */}
      <div className="grid grid-cols-2 gap-4 mb-6">
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <CheckCircle2 className="text-emerald-500" size={20} />
            <div>
              <p className="text-2xl font-bold">{successCount}</p>
              <p className="text-xs text-muted-foreground">{lang === 'km' ? 'ចូលជោគជ័យ' : 'Successful sign-ins'}</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <XCircle className="text-rose-500" size={20} />
            <div>
              <p className="text-2xl font-bold">{failedCount}</p>
              <p className="text-xs text-muted-foreground">{lang === 'km' ? 'ចូលមិនបានសម្រេច' : 'Failed attempts'}</p>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Search */}
      <div className="relative mb-4">
        <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <Input
          placeholder={lang === 'km' ? 'ស្វែងរកតាមវិធីសាស្ត្រ កម្មវិធីរុករក IP ឬទីតាំង...' : 'Filter by method, browser, IP or location…'}
          value={search}
          onChange={e => {
            setSearch(e.target.value)
            setCurrentPage(1)
          }}
          className="pl-9 text-xs"
        />
      </div>

      {/* Unified Table */}
      <div className="rounded-xl border border-border bg-card shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs table-auto">
            <thead className="border-b border-border bg-muted/40 text-[11px] font-semibold text-muted-foreground">
              <tr>
                <th className="py-3 px-4 font-semibold text-xs text-foreground whitespace-nowrap">{tr('history.timestamp')}</th>
                <th className="py-3 px-4 font-semibold text-xs text-foreground">{tr('sessions.device')} &amp; {tr('sessions.ip')}</th>
                <th className="py-3 px-4 font-semibold text-xs text-foreground">{tr('sessions.location')}</th>
                <th className="py-3 px-4 font-semibold text-xs text-foreground">Method</th>
                <th className="py-3 px-4 font-semibold text-xs text-foreground whitespace-nowrap text-right">Result</th>
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
              ) : paginated.length === 0 ? (
                <tr>
                  <td colSpan={5} className="py-8 text-center text-xs text-muted-foreground">
                    {lang === 'km' ? 'មិនមានទិន្នន័យប្រវត្តិការចូលទេ' : 'No login history records found.'}
                  </td>
                </tr>
              ) : (
                paginated.map((item, idx) => {
                  const ua = parseUserAgent(item.userAgent)
                  const when = item.occurredAt
                    ? new Date(item.occurredAt).toLocaleString('en-GB', {
                        day: '2-digit', month: 'short', year: 'numeric',
                        hour: '2-digit', minute: '2-digit', hour12: false
                      })
                    : '—'

                  return (
                    <tr key={item.id || idx} className="hover:bg-muted/30 transition-colors">
                      <td className="py-3 px-4 text-muted-foreground whitespace-nowrap font-mono text-[11px]">
                        {when}
                      </td>
                      <td className="py-3 px-4">
                        <p className="font-semibold text-foreground">{ua.device || 'Unknown device'}</p>
                        <p className="font-mono text-[11px] text-muted-foreground">{item.ipAddress || '—'}</p>
                      </td>
                      <td className="py-3 px-4 text-muted-foreground">
                        {item.location || 'Phnom Penh, Cambodia'}
                      </td>
                      <td className="py-3 px-4">
                        <span className="font-mono text-[11px] text-foreground">{item.method || 'Password'}</span>
                      </td>
                      <td className="py-3 px-4 whitespace-nowrap text-right">
                        <Badge
                          variant="outline"
                          className={`text-[10px] font-medium ${item.success
                            ? 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-950 dark:text-emerald-300'
                            : 'bg-rose-50 text-rose-700 border-rose-200 dark:bg-rose-950 dark:text-rose-300'
                          }`}
                        >
                          {item.success ? tr('history.success') : tr('history.failed')}
                        </Badge>
                      </td>
                    </tr>
                  )
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Standard Table Pagination Footer */}
        <TablePagination
          totalItems={filtered.length}
          page={currentPage}
          pageSize={rowsPerPage}
          onPageChange={setCurrentPage}
          onPageSizeChange={setRowsPerPage}
          pageSizeOptions={[10, 20, 50]}
        />
      </div>
    </div>
  )
}
