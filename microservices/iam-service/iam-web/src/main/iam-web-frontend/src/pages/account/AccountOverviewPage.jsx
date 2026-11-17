import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useApp } from '@/context/AppContext'
import { meApi } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  ShieldCheck, Fingerprint, Clock, Calendar, Pencil, ExternalLink,
  User, Link2, ChevronRight, CheckCircle2, Globe, MapPin, AppWindow
} from 'lucide-react'
import KeyIcon from '@/components/icons/KeyIcon'
import UserAvatar, { getMainNameInitials } from '@/components/common/UserAvatar'

export default function AccountOverviewPage() {
  const { user: ctxUser, setUser, tr } = useApp()
  const [profile, setProfile] = useState(ctxUser || null)
  const [authorities, setAuthorities] = useState(null)
  const [loginHistory, setLoginHistory] = useState([])
  const [loading, setLoading] = useState(true)

  // Synchronize immediately whenever ctxUser updates in AppContext
  useEffect(() => {
    if (ctxUser) {
      setProfile(prev => ({ ...(prev || {}), ...ctxUser }))
    }
  }, [ctxUser])

  useEffect(() => {
    let mounted = true
    async function loadData() {
      setLoading(true)
      try {
        const [meRes, authRes, histRes] = await Promise.allSettled([
          meApi.get(),
          meApi.authorities(),
          meApi.loginHistory(5),
        ])

        if (mounted) {
          if (meRes.status === 'fulfilled' && meRes.value) {
            setProfile(meRes.value)
            if (setUser) {
              setUser(prev => ({ ...(prev || {}), ...meRes.value }))
            }
          }
          if (authRes.status === 'fulfilled' && authRes.value) {
            setAuthorities(authRes.value)
          }
          if (histRes.status === 'fulfilled' && Array.isArray(histRes.value)) {
            setLoginHistory(histRes.value)
          }
        }
      } catch (err) {
        console.error('Failed to load overview data', err)
      } finally {
        if (mounted) setLoading(false)
      }
    }
    loadData()
    return () => { mounted = false }
  }, [setUser])

  const currentUser = profile || ctxUser || {}

  // Names & Initials
  const given = currentUser?.givenName || ''
  const middle = currentUser?.middleName || ''
  const family = currentUser?.familyName || ''
  const nickname = currentUser?.nickname || ''
  const displayName = [given, middle, family].filter(Boolean).join(' ') || currentUser?.name || currentUser?.username || 'User'
  const username = currentUser?.username || ''
  const email = currentUser?.email || ''
  const initials = getMainNameInitials(currentUser, displayName)

  // Roles calculation
  const rolesList = authorities?.roles?.length
    ? authorities.roles.map(r => r.name)
    : (currentUser?.roles?.length
      ? currentUser.roles.map(r => (typeof r === 'object' ? r.name : r))
      : ['USER'])

  const primaryRole = rolesList[0]?.replace(/^ROLE_/, '') || 'USER'
  const rolesCount = rolesList.length || 1

  // Permissions count
  const permissionsCount = authorities?.effectivePermissions?.length
    ? authorities.effectivePermissions.length
    : (authorities?.effectivePermissions?.size || (authorities?.roles?.length ? authorities.roles.length : 1))

  // Last sign-in
  const latestLogin = loginHistory.find(h => h.success !== false && h.result !== 'Failed') || loginHistory[0]
  const loginTimestamp = latestLogin?.occurredAt || latestLogin?.timestamp || currentUser?.lastLoginAt || currentUser?.last_login_at
  const lastSignInFormatted = loginTimestamp
    ? new Date(loginTimestamp).toLocaleString('en-US', {
      month: 'short', day: 'numeric', year: 'numeric',
      hour: 'numeric', minute: '2-digit', hour12: true
    })
    : (currentUser?.updatedAt
      ? new Date(currentUser.updatedAt).toLocaleString('en-US', {
        month: 'short', day: 'numeric', year: 'numeric',
        hour: 'numeric', minute: '2-digit', hour12: true
      })
      : 'Active now')

  // Member since
  const memberSinceFormatted = currentUser?.createdAt
    ? new Date(currentUser.createdAt).toLocaleDateString('en-US', {
      month: 'short', day: 'numeric', year: 'numeric'
    })
    : '—'

  // Gender formatted
  const genderFormatted = currentUser?.gender === 'MALE'
    ? tr('profile.male')
    : currentUser?.gender === 'FEMALE'
      ? tr('profile.female')
      : currentUser?.gender === 'OTHER'
        ? tr('profile.other')
        : (currentUser?.gender || '—')

  // Date of birth formatted (DD/MM/YYYY)
  let dobFormatted = '—'
  if (currentUser?.birthdate) {
    const parts = String(currentUser.birthdate).split('-')
    if (parts.length === 3) {
      const [y, m, d] = parts
      dobFormatted = `${d}/${m}/${y}`
    } else {
      dobFormatted = String(currentUser.birthdate)
    }
  }

  // Address formatted
  const addr = currentUser?.address
  const addressParts = [
    addr?.streetAddress,
    addr?.locality,
    addr?.region,
    addr?.postalCode,
    addr?.country
  ].filter(Boolean)
  const addressFormatted = addressParts.length > 0 ? addressParts.join(', ') : '—'

  // Admin access check
  const isAdmin = rolesList.some(r => {
    const norm = String(r).toUpperCase().replace(/^ROLE_/, '')
    return norm === 'ADMIN' || norm === 'SUPERADMIN' || norm === 'STAFF'
  })

  return (
    <div className="min-h-full bg-background p-4 md:p-8">
      <div className="mx-auto max-w-6xl space-y-6">
        {/* Top Hero Banner */}
        <div className="rounded-xl border border-border bg-card p-6 shadow-sm">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div className="flex items-center gap-4">
              <UserAvatar
                user={currentUser}
                name={displayName}
                src={currentUser?.pictureUrl}
                className="h-14 w-14 shrink-0 text-lg shadow-sm"
              />
              <div>
                <p className="text-xs text-muted-foreground">{tr('overview.welcomeBack')}</p>
                <h1 className="text-2xl font-bold tracking-tight text-foreground">{displayName}</h1>
                <p className="text-xs text-muted-foreground mt-0.5">
                  @{username} &bull; {email}
                </p>
              </div>
            </div>

            <div className="flex items-center gap-2.5">
              <Link to="/account/profile">
                <Button variant="outline" size="sm" className="gap-1.5 text-xs h-9">
                  <Pencil size={13} /> {tr('overview.editProfile')}
                </Button>
              </Link>
              {isAdmin && (
                <Link to="/admin">
                  <Button size="sm" className="gap-1.5 shadow-sm">
                    <ShieldCheck size={14} /> {tr('overview.openDashboard')}
                  </Button>
                </Link>
              )}
            </div>
          </div>
        </div>

        {/* 4 Stat Tiles */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <div className="rounded-xl border border-border bg-card p-5 shadow-sm">
            <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
              <ShieldCheck size={15} />
              <span>{tr('overview.roles')}</span>
            </div>
            <p className="mt-2 text-2xl font-bold text-foreground">{rolesCount}</p>
          </div>

          <div className="rounded-xl border border-border bg-card p-5 shadow-sm">
            <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
              <Fingerprint size={15} />
              <span>{tr('overview.permissions')}</span>
            </div>
            <p className="mt-2 text-2xl font-bold text-foreground">{permissionsCount}</p>
          </div>

          <div className="rounded-xl border border-border bg-card p-5 shadow-sm">
            <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
              <Clock size={15} />
              <span>{tr('overview.lastSignIn')}</span>
            </div>
            <p className="mt-2 text-sm sm:text-base font-semibold text-foreground truncate">
              {lastSignInFormatted}
            </p>
          </div>

          <div className="rounded-xl border border-border bg-card p-5 shadow-sm">
            <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
              <Calendar size={15} />
              <span>{tr('overview.memberSince')}</span>
            </div>
            <p className="mt-2 text-sm sm:text-base font-semibold text-foreground truncate">
              {memberSinceFormatted}
            </p>
          </div>
        </div>

        {/* Two Columns Layout */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          {/* Left Column: Personal Information */}
          <div className="lg:col-span-8 rounded-xl border border-border bg-card p-6 shadow-sm">
            <div className="flex items-center justify-between pb-4 border-b border-border">
              <h2 className="text-base font-semibold text-foreground">
                {tr('overview.personalInfo')}
              </h2>
              <Link to="/account/profile" className="text-xs text-blue-600 dark:text-blue-400 hover:text-blue-700 transition-colors">
                {tr('overview.edit')}
              </Link>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-y-6 gap-x-8 pt-5 text-sm">
              <div>
                <p className="text-xs text-muted-foreground">{tr('profile.firstName')}</p>
                <p className="font-medium text-foreground text-sm mt-1">{given || '—'}</p>
              </div>

              <div>
                <p className="text-xs text-muted-foreground">{tr('profile.lastName')}</p>
                <p className="font-medium text-foreground text-sm mt-1">{family || '—'}</p>
              </div>

              {middle && (
                <div>
                  <p className="text-xs text-muted-foreground">{tr('profile.middleName')}</p>
                  <p className="font-medium text-foreground text-sm mt-1">{middle}</p>
                </div>
              )}

              {nickname && (
                <div>
                  <p className="text-xs text-muted-foreground">{tr('profile.nickname')}</p>
                  <p className="font-medium text-foreground text-sm mt-1">{nickname}</p>
                </div>
              )}

              <div>
                <p className="text-xs text-muted-foreground">{tr('profile.email')}</p>
                <div className="flex items-center gap-2 mt-1">
                  <span className="font-medium text-foreground text-sm">{email || '—'}</span>
                  {currentUser?.emailVerified !== false && (
                    <span className="inline-flex items-center px-2 py-0.5 rounded text-[11px] font-medium bg-emerald-50 text-emerald-700 dark:bg-emerald-950/60 dark:text-emerald-400 border border-emerald-200 dark:border-emerald-800">
                      {tr('overview.verified')}
                    </span>
                  )}
                </div>
              </div>

              <div>
                <p className="text-xs text-muted-foreground">{tr('profile.phone')}</p>
                <p className="font-medium text-foreground text-sm mt-1">
                  {currentUser?.phoneNumber || '—'}
                </p>
              </div>

              <div>
                <p className="text-xs text-muted-foreground">{tr('overview.gender')}</p>
                <p className="font-medium text-foreground text-sm mt-1">
                  {genderFormatted}
                </p>
              </div>

              <div>
                <p className="text-xs text-muted-foreground">{tr('overview.dob')}</p>
                <p className="font-medium text-foreground text-sm mt-1">
                  {dobFormatted}
                </p>
              </div>

              {currentUser?.websiteUrl && (
                <div className="sm:col-span-2">
                  <p className="text-xs text-muted-foreground">{tr('profile.website')}</p>
                  <a
                    href={currentUser.websiteUrl.startsWith('http') ? currentUser.websiteUrl : `https://${currentUser.websiteUrl}`}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="mt-1 font-medium text-blue-600 dark:text-blue-400 hover:text-blue-700 text-sm inline-flex items-center gap-1.5 transition-colors"
                  >
                    <span>{currentUser.websiteUrl}</span>
                    <ExternalLink size={13} />
                  </a>
                </div>
              )}

              <div className="sm:col-span-2">
                <p className="text-xs text-muted-foreground">{tr('profile.address')}</p>
                <p className="font-medium text-foreground text-sm mt-1">
                  {addressFormatted}
                </p>
              </div>

              <div className="sm:col-span-2">
                <p className="text-xs text-muted-foreground">{tr('overview.roles')}</p>
                <div className="mt-1.5 flex flex-wrap gap-1.5">
                  {rolesList.map(role => (
                    <Badge
                      key={role}
                      variant="secondary"
                      className="font-mono text-xs font-semibold px-2.5 py-0.5"
                    >
                      {String(role).replace(/^ROLE_/, '')}
                    </Badge>
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Right Column: Quick Links */}
          <div className="lg:col-span-4 space-y-3">
            <h2 className="text-sm font-semibold text-foreground mb-3">
              {tr('overview.quickLinks')}
            </h2>

            <Link
              to="/account/profile"
              className="flex items-center justify-between p-4 rounded-xl border border-border bg-card hover:bg-slate-50/70 dark:hover:bg-slate-800/50 hover:border-slate-300 transition cursor-pointer shadow-sm group"
            >
              <div className="flex items-center min-w-0">
                <div className="w-9 h-9 rounded-lg bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300 flex items-center justify-center shrink-0 group-hover:bg-blue-50 group-hover:text-blue-600 transition-colors">
                  <User size={18} />
                </div>
                <div className="ml-3 min-w-0">
                  <p className="font-medium text-sm text-foreground">{tr('nav.profile')}</p>
                  <p className="text-xs text-muted-foreground mt-0.5 truncate max-w-[200px]">
                    {tr('overview.profileDesc')}
                  </p>
                </div>
              </div>
              <ChevronRight size={18} className="text-muted-foreground group-hover:text-slate-800 dark:group-hover:text-slate-200 shrink-0 ml-2 transition-colors" />
            </Link>

            <Link
              to="/account/security"
              className="flex items-center justify-between p-4 rounded-xl border border-border bg-card hover:bg-slate-50/70 dark:hover:bg-slate-800/50 hover:border-slate-300 transition cursor-pointer shadow-sm group"
            >
              <div className="flex items-center min-w-0">
                <div className="w-9 h-9 rounded-lg bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300 flex items-center justify-center shrink-0 group-hover:bg-blue-50 group-hover:text-blue-600 transition-colors">
                  <KeyIcon size={18} />
                </div>
                <div className="ml-3 min-w-0">
                  <p className="font-medium text-sm text-foreground">{tr('nav.security')}</p>
                  <p className="text-xs text-muted-foreground mt-0.5 truncate max-w-[200px]">
                    {tr('overview.securityDesc')}
                  </p>
                </div>
              </div>
              <ChevronRight size={18} className="text-muted-foreground group-hover:text-slate-800 dark:group-hover:text-slate-200 shrink-0 ml-2 transition-colors" />
            </Link>

            <Link
              to="/account/linked-accounts"
              className="flex items-center justify-between p-4 rounded-xl border border-border bg-card hover:bg-slate-50/70 dark:hover:bg-slate-800/50 hover:border-slate-300 transition cursor-pointer shadow-sm group"
            >
              <div className="flex items-center min-w-0">
                <div className="w-9 h-9 rounded-lg bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300 flex items-center justify-center shrink-0 group-hover:bg-blue-50 group-hover:text-blue-600 transition-colors">
                  <Link2 size={18} />
                </div>
                <div className="ml-3 min-w-0">
                  <p className="font-medium text-sm text-foreground">{tr('nav.linkedAccounts')}</p>
                  <p className="text-xs text-muted-foreground mt-0.5 truncate max-w-[200px]">
                    {tr('overview.linkedDesc')}
                  </p>
                </div>
              </div>
              <ChevronRight size={18} className="text-muted-foreground group-hover:text-slate-800 dark:group-hover:text-slate-200 shrink-0 ml-2 transition-colors" />
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
