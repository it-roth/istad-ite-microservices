import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { usersApi, rolesApi, permissionsApi, clientsApi, adminAuditApi } from '@/lib/api'
import { useApp } from '@/context/AppContext'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  Users, Shield, Plus, ArrowRight, AppWindow, Activity, User
} from 'lucide-react'
import KeyIcon from '@/components/icons/KeyIcon'

export default function AdminOverviewPage() {
  const { lang } = useApp?.() || { lang: 'en' }
  const [stats, setStats] = useState({
    usersCount: 0,
    rolesCount: 0,
    permsCount: 0,
    clientsCount: 0,
  })
  const [newestUsers, setNewestUsers] = useState([])
  const [recentActivities, setRecentActivities] = useState([])
  const [loading, setLoading] = useState(true)

  async function loadData() {
    try {
      const [usersRes, rolesRes, permsRes, clientsRes, auditRes] = await Promise.allSettled([
        usersApi.list(0, 5),
        rolesApi.list(0, 1),
        permissionsApi.list(0, 1),
        clientsApi.list(),
        adminAuditApi.list(5),
      ])

      const usersData = usersRes.status === 'fulfilled' ? usersRes.value : null
      const rolesData = rolesRes.status === 'fulfilled' ? rolesRes.value : null
      const permsData = permsRes.status === 'fulfilled' ? permsRes.value : null
      const clientsData = clientsRes.status === 'fulfilled' ? clientsRes.value : []
      const auditData = auditRes.status === 'fulfilled' ? auditRes.value : []

      if (usersRes.status === 'rejected') console.error('Failed to load users:', usersRes.reason)
      if (rolesRes.status === 'rejected') console.error('Failed to load roles:', rolesRes.reason)
      if (permsRes.status === 'rejected') console.error('Failed to load perms:', permsRes.reason)

      const userList = Array.isArray(usersData?.content) ? usersData.content : (Array.isArray(usersData) ? usersData : [])
      const totalUsers = Number(usersData?.page?.totalElements ?? usersData?.totalElements ?? userList.length)

      const roleList = Array.isArray(rolesData?.content) ? rolesData.content : (Array.isArray(rolesData) ? rolesData : [])
      const totalRoles = Number(rolesData?.page?.totalElements ?? rolesData?.totalElements ?? roleList.length)

      const permList = Array.isArray(permsData?.content) ? permsData.content : (Array.isArray(permsData) ? permsData : [])
      const totalPerms = Number(permsData?.page?.totalElements ?? permsData?.totalElements ?? permList.length)

      const totalClients = Array.isArray(clientsData) ? clientsData.length : 0

      setStats({
        usersCount: totalUsers,
        rolesCount: totalRoles,
        permsCount: totalPerms,
        clientsCount: totalClients,
      })

      setNewestUsers(userList)
      setRecentActivities(Array.isArray(auditData) ? auditData.slice(0, 5) : [])
    } catch (e) {
      console.error('Failed to load admin overview data', e)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [])

  return (
    <div className="min-h-full bg-background p-4 md:p-8">
      <div className="mx-auto max-w-6xl space-y-6">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold tracking-tight text-foreground">
              {lang === 'km' ? 'ទិដ្ឋភាពទូទៅនៃអ្នកគ្រប់គ្រង' : 'Admin overview'}
            </h1>
            <p className="mt-1 text-xs text-muted-foreground">
              {lang === 'km'
                ? 'គ្រប់គ្រងអ្នកដែលអាចចូលប្រើប្រាស់កម្មវិធី ISTAD និងសិទ្ធិរបស់ពួកគេ។'
                : 'Manage who can sign in to ISTAD applications and what they can do.'}
            </p>
          </div>
          <div className="flex items-center gap-2">
            <Link to="/admin/users">
              <Button size="sm" className="gap-1.5 cursor-pointer font-medium">
                <Plus size={14} /> {lang === 'km' ? 'បង្កើតអ្នកប្រើ' : 'Create user'}
              </Button>
            </Link>
          </div>
        </div>

        {/* 4 Dynamic Stat Cards matching requirement: total users, roles, permissions, clients */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <Link to="/admin/users">
            <div className="rounded-xl border border-border bg-card p-5 shadow-xs hover:border-blue-600/50 transition cursor-pointer">
              <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                <Users size={15} />
                <span>{lang === 'km' ? 'អ្នកប្រើប្រាស់' : 'Users'}</span>
              </div>
              <p className="mt-2 text-2xl font-bold text-foreground">
                {loading ? '...' : stats.usersCount}
              </p>
              <p className="mt-1 text-xs text-muted-foreground">
                {lang === 'km' ? 'គណនីទាំងអស់' : 'All accounts'}
              </p>
            </div>
          </Link>

          <Link to="/admin/roles">
            <div className="rounded-xl border border-border bg-card p-5 shadow-xs hover:border-blue-600/50 transition cursor-pointer">
              <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                <Shield size={15} />
                <span>{lang === 'km' ? 'តួនាទីសកម្ម' : 'Active roles'}</span>
              </div>
              <p className="mt-2 text-2xl font-bold text-foreground">
                {loading ? '...' : stats.rolesCount}
              </p>
              <p className="mt-1 text-xs text-muted-foreground">
                {lang === 'km' ? 'ក្រុមនៃសិទ្ធិ' : 'Groups of permissions'}
              </p>
            </div>
          </Link>

          <Link to="/admin/permissions">
            <div className="rounded-xl border border-border bg-card p-5 shadow-xs hover:border-blue-600/50 transition cursor-pointer">
              <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                <KeyIcon size={15} />
                <span>{lang === 'km' ? 'សិទ្ធិសកម្ម' : 'Active permissions'}</span>
              </div>
              <p className="mt-2 text-2xl font-bold text-foreground">
                {loading ? '...' : stats.permsCount}
              </p>
              <p className="mt-1 text-xs text-muted-foreground">service:resource:action</p>
            </div>
          </Link>

          <Link to="/admin/applications">
            <div className="rounded-xl border border-border bg-card p-5 shadow-xs hover:border-blue-600/50 transition cursor-pointer">
              <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                <AppWindow size={15} className="text-blue-600 dark:text-blue-400" />
                <span>{lang === 'km' ? 'កម្មវិធី / Clients' : 'Clients'}</span>
              </div>
              <p className="mt-2 text-2xl font-bold text-foreground">
                {loading ? '...' : stats.clientsCount}
              </p>
              <p className="mt-1 text-xs text-muted-foreground">
                {lang === 'km' ? 'OAuth2 & OIDC clients' : 'OAuth2 & OIDC clients'}
              </p>
            </div>
          </Link>
        </div>

        {/* Two Dynamic Widgets Below: Newest Users and Recent Activities */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          {/* Newest users (Left ~60%) */}
          <div className="lg:col-span-7 rounded-xl border border-border bg-card p-5 shadow-xs">
            <div className="flex items-center justify-between pb-3 border-b border-border">
              <h2 className="text-sm font-semibold text-foreground">
                {lang === 'km' ? 'អ្នកប្រើប្រាស់ថ្មីៗ' : 'Newest users'}
              </h2>
              <Link to="/admin/users" className="text-xs text-muted-foreground hover:text-foreground flex items-center gap-1">
                {lang === 'km' ? 'មើលទាំងអស់' : 'View all'} <ArrowRight size={12} />
              </Link>
            </div>

            {loading ? (
              <div className="py-14 text-center">
                <div className="h-6 w-6 animate-spin rounded-full border-2 border-blue-600 border-t-transparent mx-auto mb-2" />
                <p className="text-xs text-muted-foreground">
                  {lang === 'km' ? 'កំពុងផ្ទុកទិន្នន័យ...' : 'Loading users...'}
                </p>
              </div>
            ) : newestUsers.length > 0 ? (
              <div className="divide-y divide-border">
                {newestUsers.map((u, idx) => {
                  const name = [u.givenName, u.familyName].filter(Boolean).join(' ') || u.username
                  const initials = (u.givenName?.[0] || u.username?.[0] || 'U') + (u.familyName?.[0] || '')
                  let dateStr = ''
                  if (u.createdAt) {
                    try {
                      dateStr = new Date(u.createdAt).toLocaleDateString('en-GB', {
                        day: 'numeric',
                        month: 'short',
                        year: 'numeric'
                      })
                    } catch (e) {}
                  }

                  return (
                    <Link
                      key={u.uuid || u.id || idx}
                      to={`/admin/users/${u.uuid || u.id}`}
                      className="py-3 flex items-center justify-between hover:bg-muted/30 px-2 rounded-lg transition-colors"
                    >
                      <div className="flex items-center gap-3 min-w-0">
                        <div className="w-8 h-8 rounded-full bg-muted flex items-center justify-center text-xs font-semibold text-muted-foreground shrink-0 uppercase">
                          {initials}
                        </div>
                        <div className="min-w-0">
                          <p className="text-xs font-medium text-foreground truncate">{name}</p>
                          <p className="text-[11px] text-muted-foreground truncate">{u.email || `@${u.username}`}</p>
                        </div>
                      </div>

                      <div className="flex items-center gap-3 shrink-0 text-right">
                        {dateStr && <span className="text-[11px] text-muted-foreground">{dateStr}</span>}
                        <span className={`inline-flex items-center gap-1 text-[11px] font-medium ${u.enabled !== false ? 'text-emerald-600 dark:text-emerald-400' : 'text-rose-600 dark:text-rose-400'}`}>
                          <span className={`w-1.5 h-1.5 rounded-full ${u.enabled !== false ? 'bg-emerald-500' : 'bg-rose-500'}`}></span>
                          {u.enabled !== false ? (lang === 'km' ? 'សកម្ម' : 'Active') : (lang === 'km' ? 'អសកម្ម' : 'Disabled')}
                        </span>
                      </div>
                    </Link>
                  )
                })}
              </div>
            ) : (
              <div className="py-14 text-center">
                <Users className="mx-auto h-8 w-8 text-muted-foreground/40 mb-2" />
                <p className="text-xs text-muted-foreground">
                  {lang === 'km' ? 'មិនមានអ្នកប្រើប្រាស់នៅឡើយទេ' : 'No users found.'}
                </p>
              </div>
            )}
          </div>

          {/* Recent activities (Right ~40%) */}
          <div className="lg:col-span-5 rounded-xl border border-border bg-card p-5 shadow-xs">
            <div className="flex items-center justify-between pb-3 border-b border-border">
              <h2 className="text-sm font-semibold text-foreground">
                {lang === 'km' ? 'សកម្មភាពថ្មីៗ' : 'Recent activities'}
              </h2>
              <Link to="/admin/audit" className="text-xs text-muted-foreground hover:text-foreground flex items-center gap-1">
                {lang === 'km' ? 'មើលទាំងអស់' : 'View all'} <ArrowRight size={12} />
              </Link>
            </div>

            {recentActivities.length > 0 ? (
              <div className="divide-y divide-border">
                {recentActivities.map((act, idx) => (
                  <div key={act.id || idx} className="py-3 flex items-center justify-between gap-2 text-xs">
                    <div className="min-w-0">
                      <div className="flex items-center gap-1.5">
                        <User size={12} className="text-muted-foreground" />
                        <span className="font-medium text-foreground truncate">{act.changed || act.by}</span>
                      </div>
                      <p className="text-[11px] text-muted-foreground mt-0.5">{act.whenRelative || act.whenDate}</p>
                    </div>
                    <Badge variant="outline" className="text-[10px] shrink-0 font-normal">
                      {act.action}
                    </Badge>
                  </div>
                ))}
              </div>
            ) : (
              <div className="py-14 text-center">
                <Activity className="mx-auto h-8 w-8 text-muted-foreground/40 mb-2" />
                <p className="text-xs text-muted-foreground">
                  {lang === 'km' ? 'មិនទាន់មានសកម្មភាពថ្មីៗនៅឡើយទេ។' : 'No recent activities recorded yet.'}
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
