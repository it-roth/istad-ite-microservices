import { useEffect, useState, useMemo } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useApp } from '@/context/AppContext'
import { clientsApi } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { Checkbox } from '@/components/ui/checkbox'
import { TablePagination } from '@/components/ui/table-pagination'
import { cn } from '@/lib/utils'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from '@/components/ui/dialog'
import {
  Tabs,
  TabsList,
  TabsTrigger,
  TabsContent,
} from '@/components/ui/tabs'
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
  Plus, Search, AppWindow, MoreHorizontal, Trash2, Pencil, X,
  AlertCircle, Eye, Copy, Check, ExternalLink, Shield, Key, Clock, CheckCircle2,
  Lock, Globe, Calendar, RefreshCw, Layers, ShieldCheck, ArrowUpRight
} from 'lucide-react'

export default function ApplicationsPage() {
  const navigate = useNavigate()
  const { tr, lang } = useApp()
  const [clients, setClients] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [search, setSearch] = useState('')
  const [selectedClient, setSelectedClient] = useState(null)
  const [copied, setCopied] = useState(false)
  const [deleteConfirmClient, setDeleteConfirmClient] = useState(null)
  const [actionMsg, setActionMsg] = useState('')

  // Edit modal state
  const [editModalOpen, setEditModalOpen] = useState(false)
  const [editingClient, setEditingClient] = useState(null)
  const [editForm, setEditForm] = useState({
    clientName: '',
    redirectUris: '',
    postLogoutRedirectUris: '',
    scopes: '',
    accessTokenLifetime: 5,
    refreshTokenLifetime: 60,
    requireConsent: false,
  })
  const [updating, setUpdating] = useState(false)

  // Pagination state
  const [rowsPerPage, setRowsPerPage] = useState(5)
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

  function handleOpenEdit(client, e) {
    if (e) e.stopPropagation()
    const rUris = Array.isArray(client.redirectUris)
      ? client.redirectUris.join('\n')
      : (Array.from(client.redirectUris || [])).join('\n')
    const postUris = Array.isArray(client.postLogoutRedirectUris)
      ? client.postLogoutRedirectUris.join('\n')
      : (Array.from(client.postLogoutRedirectUris || [])).join('\n')
    const scps = Array.isArray(client.scopes)
      ? client.scopes.join(', ')
      : (Array.from(client.scopes || [])).join(', ')

    setEditingClient(client)
    setEditForm({
      clientName: client.clientName || client.clientId || '',
      redirectUris: rUris,
      postLogoutRedirectUris: postUris,
      scopes: scps,
      accessTokenLifetime: client.accessTokenTimeToLiveMinutes || 5,
      refreshTokenLifetime: client.refreshTokenTimeToLiveMinutes || 60,
      requireConsent: Boolean(client.requireConsent),
    })
    setEditModalOpen(true)
  }

  async function handleSaveEdit(e) {
    e.preventDefault()
    if (!editingClient) return
    setUpdating(true)
    try {
      const id = editingClient.id || editingClient.clientId
      const rList = editForm.redirectUris.split(/[\n,]/).map(u => u.trim()).filter(Boolean)
      const postList = editForm.postLogoutRedirectUris.split(/[\n,]/).map(u => u.trim()).filter(Boolean)
      const sList = editForm.scopes.split(/[\n, ]/).map(s => s.trim()).filter(Boolean)

      const payload = {
        clientId: editingClient.clientId,
        clientName: editForm.clientName.trim(),
        redirectUris: rList,
        postLogoutRedirectUris: postList,
        scopes: sList.length > 0 ? sList : ['openid', 'profile', 'email'],
        requireAuthorizationConsent: editForm.requireConsent,
        accessTokenTimeToLiveMinutes: parseInt(editForm.accessTokenLifetime, 10) || 5,
        refreshTokenTimeToLiveMinutes: parseInt(editForm.refreshTokenLifetime, 10) || 60,
        grantTypes: Array.from(editingClient.grantTypes || ['authorization_code']),
        authenticationMethods: Array.from(editingClient.authenticationMethods || ['none']),
      }

      const updated = await clientsApi.update(id, payload)
      setEditModalOpen(false)
      loadClients()
      if (selectedClient && (selectedClient.id === id || selectedClient.clientId === id)) {
        setSelectedClient(updated || { ...selectedClient, ...payload })
      }
    } catch (err) {
      alert(err.message || 'Failed to update application')
    } finally {
      setUpdating(false)
    }
  }

  function handleCopyClientId(text) {
    if (!text) return
    navigator.clipboard?.writeText(text)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  function handleDelete(client, e) {
    if (e) e.stopPropagation()
    setDeleteConfirmClient(client)
  }

  async function confirmDelete() {
    if (!deleteConfirmClient) return
    const id = deleteConfirmClient.id || deleteConfirmClient.clientId
    const name = deleteConfirmClient.clientName || deleteConfirmClient.clientId
    try {
      await clientsApi.delete(id)
      if (selectedClient && (selectedClient.id === id || selectedClient.clientId === id)) {
        setSelectedClient(null)
      }
      setDeleteConfirmClient(null)
      setActionMsg(lang === 'km' ? `បានលុបកម្មវិធី "${name}" ដោយជោគជ័យ` : `Application "${name}" deleted successfully.`)
      setTimeout(() => setActionMsg(''), 4000)
      loadClients()
    } catch (err) {
      setDeleteConfirmClient(null)
      setError(err.message || 'Failed to delete application')
      setTimeout(() => setError(''), 4000)
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
    if (!isoStr) return '—'
    try {
      return new Date(isoStr).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })
    } catch {
      return isoStr
    }
  }

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

  function formatMinutes(minutes) {
    if (!minutes && minutes !== 0) return '—'
    const m = Number(minutes)
    if (m < 60) return `${m} ${lang === 'km' ? 'នាទី' : 'min'}`
    if (m < 1440) {
      const h = Math.floor(m / 60)
      const rem = m % 60
      if (lang === 'km') {
        return rem > 0 ? `${m} នាទី (${h} ម៉ោង ${rem} នាទី)` : `${m} នាទី (${h} ម៉ោង)`
      }
      return rem > 0 ? `${m} min (${h}h ${rem}m)` : `${m} min (${h}h)`
    }
    const d = Math.floor(m / 1440)
    const remH = Math.floor((m % 1440) / 60)
    if (lang === 'km') {
      return remH > 0 ? `${m} នាទី (${d} ថ្ងៃ ${remH} ម៉ោង)` : `${m} នាទី (${d} ថ្ងៃ)`
    }
    return remH > 0 ? `${m} min (${d}d ${remH}h)` : `${m} min (${d} days)`
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
                        onClick={() => setSelectedClient(client)}
                        className="hover:bg-muted/40 transition-colors group cursor-pointer"
                      >
                        {/* Application Icon + Name + Client ID */}
                        <td className="py-3.5 px-4 font-medium">
                          <div className="flex items-center gap-3">
                            <div className="h-8 w-8 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 flex items-center justify-center shrink-0 border border-slate-200 dark:border-slate-700">
                              <AppWindow size={15} />
                            </div>
                            <div className="min-w-0">
                              <p className="font-semibold text-foreground text-xs truncate group-hover:text-blue-600 transition-colors">
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
                          {formatDate(client.clientIdIssuedAt || client.createdAt || client.issuedAt)}
                        </td>

                        {/* Actions Menu */}
                        <td className="py-3.5 px-4 text-right" onClick={(e) => e.stopPropagation()}>
                          <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                              <Button variant="ghost" size="icon" className="h-7 w-7 text-muted-foreground hover:text-foreground">
                                <MoreHorizontal size={14} />
                              </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end" className="w-40 text-xs">
                              <DropdownMenuItem
                                onClick={() => setSelectedClient(client)}
                                className="cursor-pointer gap-2"
                              >
                                <Eye size={13} /> {lang === 'km' ? 'មើលព័ត៌មានលម្អិត' : 'View details'}
                              </DropdownMenuItem>
                              <DropdownMenuItem
                                onClick={(e) => handleOpenEdit(client, e)}
                                className="cursor-pointer gap-2"
                              >
                                <Pencil size={13} /> {lang === 'km' ? 'កែប្រែ' : 'Edit application'}
                              </DropdownMenuItem>
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
            pageSizeOptions={[5, 10, 20, 50]}
          />
        </div>

        {/* Client Detail Dialog */}
        <Dialog open={!!selectedClient} onOpenChange={(open) => !open && setSelectedClient(null)}>
          <DialogContent className="max-w-2xl p-0 gap-0 overflow-hidden sm:rounded-2xl border border-border shadow-2xl bg-card">
            {selectedClient && (() => {
              const authMethods = selectedClient.authenticationMethods || selectedClient.clientAuthenticationMethods || []
              const isPublic = authMethods.includes('none') && authMethods.length === 1
              const isConfidential = !isPublic
              const grants = Array.from(selectedClient.grantTypes || selectedClient.authorizationGrantTypes || [])
              const scopes = Array.from(selectedClient.scopes || [])
              const redirectUris = Array.from(selectedClient.redirectUris || [])
              const postLogoutUris = Array.from(selectedClient.postLogoutRedirectUris || [])
              const accessTtl = selectedClient.accessTokenTimeToLiveMinutes || 5
              const refreshTtl = selectedClient.refreshTokenTimeToLiveMinutes || 60
              const requireConsent = selectedClient.requireConsent !== undefined ? selectedClient.requireConsent : true

              return (
                <>
                  {/* Dialog Header with modern shadcn styling (pr-14 prevents collision with close button) */}
                  <div className="p-6 pb-4 border-b border-border/60 bg-muted/20 pr-14 sm:pr-16">
                    <div className="flex items-center gap-3.5">
                      <div className="h-12 w-12 rounded-2xl bg-gradient-to-br from-blue-500/20 to-indigo-500/10 border border-blue-500/30 flex items-center justify-center text-blue-600 dark:text-blue-400 shrink-0 shadow-xs">
                        <AppWindow size={22} />
                      </div>
                      <div className="min-w-0 flex-1">
                        <div className="flex items-center gap-2.5 flex-wrap">
                          <DialogTitle className="text-lg font-bold tracking-tight text-foreground truncate">
                            {selectedClient.clientName || selectedClient.clientId}
                          </DialogTitle>
                          <Badge
                            variant="outline"
                            className={cn(
                              'text-[11px] font-medium px-2.5 py-0.5 rounded-full border shrink-0 inline-flex items-center gap-1.5 shadow-2xs',
                              isConfidential
                                ? 'border-blue-500/30 bg-blue-500/10 text-blue-600 dark:text-blue-400 font-semibold'
                                : 'border-emerald-500/30 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 font-semibold'
                            )}
                          >
                            {isConfidential ? <Lock size={11} /> : <Globe size={11} />}
                            {isConfidential
                              ? (lang === 'km' ? 'សម្ងាត់ (Confidential)' : 'Confidential')
                              : (lang === 'km' ? 'សាធារណៈ (Public)' : 'Public')}
                          </Badge>
                        </div>
                        <div className="flex items-center gap-2 mt-1.5">
                          <button
                            type="button"
                            onClick={() => handleCopyClientId(selectedClient.clientId)}
                            className="group inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-md bg-background/80 hover:bg-background border border-border/60 text-xs font-mono text-muted-foreground hover:text-foreground transition-all cursor-pointer shadow-2xs active:scale-95"
                            title="Copy Client ID"
                          >
                            <span>{selectedClient.clientId}</span>
                            {copied ? (
                              <Check size={12} className="text-emerald-500 animate-in fade-in" />
                            ) : (
                              <Copy size={12} className="opacity-60 group-hover:opacity-100 transition-opacity" />
                            )}
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Tabs Navigation */}
                  <Tabs defaultValue="overview" className="w-full">
                    <div className="px-6 border-b border-border/60 bg-muted/10">
                      <TabsList className="w-full justify-start rounded-none border-b border-transparent bg-transparent p-0 h-11 gap-6">
                        <TabsTrigger
                          value="overview"
                          className="rounded-none border-b-2 border-transparent px-1 pb-3 pt-2 font-medium data-[state=active]:border-blue-600 data-[state=active]:bg-transparent data-[state=active]:text-blue-600 dark:data-[state=active]:border-blue-400 dark:data-[state=active]:text-blue-400 data-[state=active]:shadow-none text-xs cursor-pointer"
                        >
                          {lang === 'km' ? 'ទិដ្ឋភាពទូទៅ & URLs' : 'Overview & URLs'}
                        </TabsTrigger>
                        <TabsTrigger
                          value="security"
                          className="rounded-none border-b-2 border-transparent px-1 pb-3 pt-2 font-medium data-[state=active]:border-blue-600 data-[state=active]:bg-transparent data-[state=active]:text-blue-600 dark:data-[state=active]:border-blue-400 dark:data-[state=active]:text-blue-400 data-[state=active]:shadow-none text-xs cursor-pointer"
                        >
                          {lang === 'km' ? 'សិទ្ធិ & Grants' : 'Grants & Scopes'}
                        </TabsTrigger>
                      </TabsList>
                    </div>

                    {/* Tab 1: Overview & Routing */}
                    <TabsContent value="overview" className="p-6 space-y-5 max-h-[60vh] overflow-y-auto no-scrollbar m-0">
                      {/* Metric Stat Cards Grid */}
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                        <div className="rounded-xl border border-border/70 bg-card p-3.5 space-y-1 shadow-2xs">
                          <div className="flex items-center gap-1.5 text-muted-foreground text-[11px] font-medium">
                            <Calendar size={13} className="text-blue-500" />
                            <span>{lang === 'km' ? 'កាលបរិច្ឆេទចុះឈ្មោះ' : 'Registered At'}</span>
                          </div>
                          <p className="text-xs font-semibold text-foreground pt-0.5">
                            {formatDateTime(selectedClient.clientIdIssuedAt || selectedClient.createdAt || selectedClient.issuedAt)}
                          </p>
                        </div>

                        <div className="rounded-xl border border-border/70 bg-card p-3.5 space-y-1 shadow-2xs">
                          <div className="flex items-center gap-1.5 text-muted-foreground text-[11px] font-medium">
                            <Key size={13} className="text-indigo-500" />
                            <span>{lang === 'km' ? 'វិធីសាស្ត្រផ្ទៀងផ្ទាត់' : 'Auth Methods'}</span>
                          </div>
                          <div className="flex flex-wrap gap-1 pt-0.5">
                            {authMethods.length > 0 ? authMethods.map(m => (
                              <span key={m} className="font-mono text-[11px] px-2 py-0.5 rounded-md bg-muted text-foreground border border-border/60">
                                {m}
                              </span>
                            )) : <span className="text-xs text-muted-foreground">—</span>}
                          </div>
                        </div>

                        <div className="rounded-xl border border-border/70 bg-card p-3.5 space-y-1 shadow-2xs">
                          <div className="flex items-center gap-1.5 text-muted-foreground text-[11px] font-medium">
                            <Clock size={13} className="text-amber-500" />
                            <span>{lang === 'km' ? 'អាយុកាល Access Token' : 'Access Token Lifetime'}</span>
                          </div>
                          <p className="text-xs font-semibold text-foreground pt-0.5">
                            {formatMinutes(accessTtl)}
                          </p>
                        </div>

                        <div className="rounded-xl border border-border/70 bg-card p-3.5 space-y-1 shadow-2xs">
                          <div className="flex items-center gap-1.5 text-muted-foreground text-[11px] font-medium">
                            <RefreshCw size={13} className="text-emerald-500" />
                            <span>{lang === 'km' ? 'អាយុកាល Refresh Token' : 'Refresh Token Lifetime'}</span>
                          </div>
                          <p className="text-xs font-semibold text-foreground pt-0.5">
                            {formatMinutes(refreshTtl)}
                          </p>
                        </div>
                      </div>

                      {/* Redirect URIs Section */}
                      <div className="rounded-xl border border-border/70 bg-card p-4 space-y-3 shadow-2xs">
                        <div className="flex items-center justify-between">
                          <div className="text-xs font-semibold text-foreground flex items-center gap-1.5">
                            <ArrowUpRight size={14} className="text-blue-500" />
                            <span>{lang === 'km' ? 'អាសយដ្ឋាន URL បញ្ជូនបន្ត (Redirect URLs)' : 'Redirect URLs'}</span>
                          </div>
                          <span className="text-[11px] text-muted-foreground">
                            {redirectUris.length} {lang === 'km' ? 'អាសយដ្ឋាន' : 'URIs'}
                          </span>
                        </div>

                        {redirectUris.length > 0 ? (
                          <div className="space-y-1.5">
                            {redirectUris.map((uri, idx) => (
                              <div
                                key={idx}
                                className="group flex items-center justify-between p-2.5 rounded-lg border border-border/60 bg-muted/30 hover:bg-muted/60 transition-colors font-mono text-[11px] text-foreground break-all"
                              >
                                <span>{uri}</span>
                                <div className="flex items-center gap-1 ml-2 shrink-0">
                                  <button
                                    type="button"
                                    onClick={() => handleCopyClientId(uri)}
                                    className="p-1 rounded text-muted-foreground hover:text-foreground hover:bg-background/80 transition-colors cursor-pointer"
                                    title="Copy URL"
                                  >
                                    <Copy size={12} />
                                  </button>
                                  <a
                                    href={uri}
                                    target="_blank"
                                    rel="noreferrer"
                                    className="p-1 rounded text-muted-foreground hover:text-blue-600 hover:bg-background/80 transition-colors"
                                    title="Open URL"
                                  >
                                    <ExternalLink size={12} />
                                  </a>
                                </div>
                              </div>
                            ))}
                          </div>
                        ) : (
                          <p className="text-muted-foreground text-xs italic">No redirect URLs configured</p>
                        )}
                      </div>

                      {/* Post-Logout Redirect URIs */}
                      {postLogoutUris.length > 0 && (
                        <div className="rounded-xl border border-border/70 bg-card p-4 space-y-3 shadow-2xs">
                          <div className="text-xs font-semibold text-foreground flex items-center gap-1.5">
                            <ExternalLink size={14} className="text-slate-500" />
                            <span>{lang === 'km' ? 'អាសយដ្ឋាន URL ចាកចេញ (Post-Logout URLs)' : 'Sign-out Redirect URLs'}</span>
                          </div>
                          <div className="space-y-1.5">
                            {postLogoutUris.map((uri, idx) => (
                              <div
                                key={idx}
                                className="flex items-center justify-between p-2.5 rounded-lg border border-border/60 bg-muted/30 font-mono text-[11px] text-foreground break-all"
                              >
                                <span>{uri}</span>
                                <a
                                  href={uri}
                                  target="_blank"
                                  rel="noreferrer"
                                  className="text-muted-foreground hover:text-blue-600 transition-colors ml-2 shrink-0"
                                >
                                  <ExternalLink size={12} />
                                </a>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}
                    </TabsContent>

                    {/* Tab 2: Grants, Scopes & Policies */}
                    <TabsContent value="security" className="p-6 space-y-5 max-h-[60vh] overflow-y-auto no-scrollbar m-0">
                      {/* Allowed Grant Types */}
                      <div className="rounded-xl border border-border/70 bg-card p-4 space-y-3 shadow-2xs">
                        <div className="text-xs font-semibold text-foreground flex items-center gap-1.5">
                          <ShieldCheck size={14} className="text-blue-500" />
                          <span>{lang === 'km' ? 'ប្រភេទ Grant (Allowed Grant Types)' : 'Allowed Grant Types'}</span>
                        </div>
                        <div className="flex flex-wrap gap-2">
                          {grants.map(gt => (
                            <div
                              key={gt}
                              className="px-3 py-1.5 rounded-lg border border-border bg-muted/30 text-xs font-medium text-foreground flex items-center gap-2"
                            >
                              <div className="h-1.5 w-1.5 rounded-full bg-blue-600 dark:bg-blue-400" />
                              <span>{formatGrantType(gt)}</span>
                            </div>
                          ))}
                        </div>
                      </div>

                      {/* User Consent Policy */}
                      <div className="rounded-xl border border-border/70 bg-card p-4 space-y-2 shadow-2xs">
                        <div className="text-xs font-semibold text-foreground flex items-center gap-1.5">
                          <Shield size={14} className="text-indigo-500" />
                          <span>{lang === 'km' ? 'គោលការណ៍យល់ព្រម (Consent Policy)' : 'Consent Policy'}</span>
                        </div>
                        {requireConsent ? (
                          <div className="flex items-start gap-3 p-3 rounded-lg border border-blue-200/70 bg-blue-50/50 dark:border-blue-900/50 dark:bg-blue-950/20">
                            <CheckCircle2 size={15} className="text-blue-600 dark:text-blue-400 shrink-0 mt-0.5" />
                            <div>
                              <p className="text-xs font-semibold text-foreground">
                                {lang === 'km' ? 'ទាមទារការយល់ព្រមពីអ្នកប្រើ' : 'User Consent Required'}
                              </p>
                              <p className="text-[11px] text-muted-foreground mt-0.5">
                                {lang === 'km'
                                  ? 'បង្ហាញទំព័រអនុម័តសិទ្ធិមុនពេលផ្តល់ Token ទៅកាន់កម្មវិធី។'
                                  : 'Shows the approval page where people confirm what the application can access.'}
                              </p>
                            </div>
                          </div>
                        ) : (
                          <div className="flex items-start gap-3 p-3 rounded-lg border border-border bg-muted/30">
                            <CheckCircle2 size={15} className="text-emerald-500 shrink-0 mt-0.5" />
                            <div>
                              <p className="text-xs font-semibold text-foreground">
                                {lang === 'km' ? 'មិនបាច់សួរការយល់ព្រម (Auto-Approved)' : 'Consent Auto-Approved'}
                              </p>
                              <p className="text-[11px] text-muted-foreground mt-0.5">
                                {lang === 'km'
                                  ? 'កម្មវិធីផ្ទៃក្នុងទុកចិត្តបាន នឹងត្រូវបានអនុញ្ញាតដោយស្វ័យប្រវត្តិ។'
                                  : 'Trusted internal application, authorized automatically without prompting the user.'}
                              </p>
                            </div>
                          </div>
                        )}
                      </div>

                      {/* Authorized Scopes */}
                      <div className="rounded-xl border border-border/70 bg-card p-4 space-y-3 shadow-2xs">
                        <div className="flex items-center justify-between">
                          <div className="text-xs font-semibold text-foreground flex items-center gap-1.5">
                            <Layers size={14} className="text-blue-500" />
                            <span>{lang === 'km' ? 'Scopes ដែលអនុញ្ញាត (Authorized Scopes)' : 'Authorized Scopes'}</span>
                          </div>
                          <span className="text-[11px] text-muted-foreground">
                            {scopes.length} {lang === 'km' ? 'Scopes' : 'scopes'}
                          </span>
                        </div>
                        <div className="flex flex-wrap gap-2">
                          {scopes.map(sc => (
                            <span
                              key={sc}
                              className="inline-flex items-center gap-1.5 font-mono text-[11px] px-2.5 py-1 rounded-md bg-blue-500/10 text-blue-700 dark:text-blue-300 border border-blue-500/20 font-medium"
                            >
                              <span className="h-1 w-1 rounded-full bg-blue-500" />
                              <span>{sc}</span>
                            </span>
                          ))}
                        </div>
                      </div>
                    </TabsContent>
                  </Tabs>

                  {/* Dialog Footer */}
                  <DialogFooter className="p-4 px-6 border-t border-border/60 bg-muted/20 flex items-center justify-between sm:justify-between w-full">
                    <Button
                      type="button"
                      variant="outline"
                      size="sm"
                      onClick={(e) => handleDelete(selectedClient, e)}
                      className="text-rose-600 hover:text-rose-700 hover:bg-rose-50 dark:hover:bg-rose-950/40 border-rose-200 dark:border-rose-900/60 text-xs gap-1.5 cursor-pointer"
                    >
                      <Trash2 size={13} /> {tr('apps.deleteClient')}
                    </Button>
                    <div className="flex items-center gap-2">
                      <Button
                        type="button"
                        variant="outline"
                        size="sm"
                        onClick={(e) => handleOpenEdit(selectedClient, e)}
                        className="text-xs px-3.5 gap-1.5 cursor-pointer"
                      >
                        <Pencil size={13} /> {lang === 'km' ? 'កែប្រែ' : 'Edit'}
                      </Button>
                      <Button
                        type="button"
                        variant="default"
                        size="sm"
                        onClick={() => setSelectedClient(null)}
                        className="text-xs px-5 cursor-pointer"
                      >
                        {lang === 'km' ? 'រួចរាល់' : 'Done'}
                      </Button>
                    </div>
                  </DialogFooter>
                </>
              )
            })()}
          </DialogContent>
        </Dialog>

        {/* Edit Application Dialog */}
        <Dialog open={editModalOpen} onOpenChange={setEditModalOpen}>
          <DialogContent className="max-w-xl max-h-[90vh] overflow-y-auto">
            <DialogHeader>
              <DialogTitle className="text-base font-semibold">
                {lang === 'km' ? 'កែប្រែកម្មវិធី OAuth 2.0' : 'Edit OAuth 2.0 Application'}
              </DialogTitle>
              <DialogDescription className="text-xs text-muted-foreground">
                {lang === 'km'
                  ? 'ធ្វើបច្ចុប្បន្នភាពឈ្មោះកម្មវិធី អាសយដ្ឋាន URL និងកំណត់សិទ្ធិ'
                  : 'Update client display name, redirect URIs, scopes, and token lifetimes.'}
              </DialogDescription>
            </DialogHeader>

            <form onSubmit={handleSaveEdit} className="space-y-4 py-2">
              <div className="space-y-1">
                <Label htmlFor="edit-name" className="text-xs font-medium">
                  {lang === 'km' ? 'ឈ្មោះកម្មវិធី (Application Name)' : 'Application Name'}
                </Label>
                <Input
                  id="edit-name"
                  value={editForm.clientName}
                  onChange={e => setEditForm({ ...editForm, clientName: e.target.value })}
                  className="h-9 text-xs"
                  required
                />
              </div>

              <div className="space-y-1">
                <Label htmlFor="edit-redirect" className="text-xs font-medium">
                  {lang === 'km' ? 'អាសយដ្ឋាន URL បញ្ជូនបន្ត (Redirect URIs - មួយបន្ទាត់មួយ)' : 'Redirect URIs (one per line)'}
                </Label>
                <textarea
                  id="edit-redirect"
                  rows={3}
                  value={editForm.redirectUris}
                  onChange={e => setEditForm({ ...editForm, redirectUris: e.target.value })}
                  placeholder="http://localhost:8080/login/oauth2/code/..."
                  className="w-full rounded-md border border-input bg-background px-3 py-2 text-xs font-mono ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                />
              </div>

              <div className="space-y-1">
                <Label htmlFor="edit-postlogout" className="text-xs font-medium">
                  {lang === 'km' ? 'អាសយដ្ឋាន URL ចាកចេញ (Post-Logout URLs - មួយបន្ទាត់មួយ)' : 'Post-Logout URLs (one per line)'}
                </Label>
                <textarea
                  id="edit-postlogout"
                  rows={2}
                  value={editForm.postLogoutRedirectUris}
                  onChange={e => setEditForm({ ...editForm, postLogoutRedirectUris: e.target.value })}
                  placeholder="http://localhost:8080/"
                  className="w-full rounded-md border border-input bg-background px-3 py-2 text-xs font-mono ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                />
              </div>

              <div className="space-y-1">
                <Label htmlFor="edit-scopes" className="text-xs font-medium">
                  {lang === 'km' ? 'Scopes (ញែកដោយសញ្ញាក្បៀស ឬដកឃ្លា)' : 'Scopes (comma or space separated)'}
                </Label>
                <Input
                  id="edit-scopes"
                  value={editForm.scopes}
                  onChange={e => setEditForm({ ...editForm, scopes: e.target.value })}
                  placeholder="openid, profile, email"
                  className="h-9 text-xs font-mono"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1">
                  <Label htmlFor="edit-access-ttl" className="text-xs font-medium">
                    {lang === 'km' ? 'Access Token (នាទី)' : 'Access Token TTL (min)'}
                  </Label>
                  <Input
                    id="edit-access-ttl"
                    type="number"
                    min="1"
                    value={editForm.accessTokenLifetime}
                    onChange={e => setEditForm({ ...editForm, accessTokenLifetime: e.target.value })}
                    className="h-9 text-xs"
                  />
                </div>
                <div className="space-y-1">
                  <Label htmlFor="edit-refresh-ttl" className="text-xs font-medium">
                    {lang === 'km' ? 'Refresh Token (នាទី)' : 'Refresh Token TTL (min)'}
                  </Label>
                  <Input
                    id="edit-refresh-ttl"
                    type="number"
                    min="1"
                    value={editForm.refreshTokenLifetime}
                    onChange={e => setEditForm({ ...editForm, refreshTokenLifetime: e.target.value })}
                    className="h-9 text-xs"
                  />
                </div>
              </div>

              <div className="flex items-center space-x-2 pt-1">
                <Checkbox
                  id="edit-consent"
                  checked={editForm.requireConsent}
                  onCheckedChange={c => setEditForm({ ...editForm, requireConsent: Boolean(c) })}
                />
                <Label htmlFor="edit-consent" className="text-xs font-normal cursor-pointer">
                  {lang === 'km' ? 'តម្រូវឱ្យមានការយល់ព្រមពីអ្នកប្រើ (Require consent)' : 'Require authorization consent screen'}
                </Label>
              </div>

              <DialogFooter className="pt-3 gap-2">
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={() => setEditModalOpen(false)}
                  className="text-xs cursor-pointer"
                >
                  {lang === 'km' ? 'បោះបង់' : 'Cancel'}
                </Button>
                <Button
                  type="submit"
                  size="sm"
                  disabled={updating}
                  className="text-xs cursor-pointer"
                >
                  {updating ? (lang === 'km' ? 'កំពុងរក្សាទុក...' : 'Saving...') : (lang === 'km' ? 'រក្សាទុក' : 'Save changes')}
                </Button>
              </DialogFooter>
            </form>
          </DialogContent>
        </Dialog>

        {/* Delete Confirmation Alert Dialog */}
        <AlertDialog open={Boolean(deleteConfirmClient)} onOpenChange={open => !open && setDeleteConfirmClient(null)}>
          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle>
                {lang === 'km' ? 'តើអ្នកប្រាកដជាចង់លុបកម្មវិធីនេះមែនទេ?' : 'Delete Application?'}
              </AlertDialogTitle>
              <AlertDialogDescription>
                {lang === 'km'
                  ? `តើអ្នកប្រាកដជាចង់លុបកម្មវិធី "${deleteConfirmClient?.clientName || deleteConfirmClient?.clientId}" មែនទេ? សកម្មភាពនេះមិនអាចត្រឡប់វិញបានទេ។`
                  : `Are you sure you want to permanently delete application "${deleteConfirmClient?.clientName || deleteConfirmClient?.clientId}"? This action cannot be undone.`}
              </AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter>
              <AlertDialogCancel>{tr('common.cancel') || (lang === 'km' ? 'បោះបង់' : 'Cancel')}</AlertDialogCancel>
              <AlertDialogAction
                className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
                onClick={confirmDelete}
              >
                {lang === 'km' ? 'លុបកម្មវិធី' : 'Delete Application'}
              </AlertDialogAction>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>

      </div>
    </div>
  )
}
