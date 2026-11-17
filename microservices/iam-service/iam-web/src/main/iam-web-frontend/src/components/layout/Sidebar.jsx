import { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useApp } from '@/context/AppContext'
import istadSeal from '@/assets/istad-seal.png'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { Badge } from '@/components/ui/badge'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip'
import {
  LayoutGrid, User, Link2,
  ShieldCheck, Users, Fingerprint,
  Mail, ScrollText, ChevronsUpDown, LogOut,
  Eye,
  AppWindow,
  Settings
} from 'lucide-react'
import KeyIcon from '@/components/icons/KeyIcon'
import { getMainNameInitials, isValidPictureUrl } from '@/components/common/UserAvatar'
import { cn } from '@/lib/utils'

function NavItem({ to, icon: Icon, label, end = false, collapsed = false, aliases = [] }) {
  const location = useLocation()
  const currentPath = location.pathname

  const isDirectActive = end
    ? currentPath === to
    : currentPath === to || (to !== '/account' && to !== '/admin' && currentPath.startsWith(to + '/'))

  const isAliasActive = aliases.length > 0 && aliases.some(alias =>
    currentPath === alias || currentPath.startsWith(alias + '/')
  )

  const active = Boolean(isDirectActive || isAliasActive)

  const item = (
    <Link
      to={to}
      className={cn(
        'group relative flex items-center select-none outline-none transition-all duration-150',
        collapsed
          ? cn(
            'justify-center w-9 h-9 mx-auto my-1 p-0 rounded-md',
            active
              ? 'bg-primary text-primary-foreground shadow-xs'
              : 'text-muted-foreground hover:text-foreground hover:bg-accent'
          )
          : cn(
            'gap-3 px-3 py-2 text-sm font-medium my-0.5 rounded-md',
            active
              ? 'bg-accent text-accent-foreground font-semibold before:absolute before:left-0 before:top-1.5 before:bottom-1.5 before:w-1 before:bg-primary before:rounded-r'
              : 'text-muted-foreground hover:text-foreground hover:bg-accent'
          )
      )}
    >
      <Icon
        size={16}
        className={cn(
          'shrink-0 stroke-[1.8] transition-colors',
          collapsed && active
            ? 'text-primary-foreground'
            : active
              ? 'text-primary'
              : 'text-muted-foreground group-hover:text-foreground'
        )}
      />
      {!collapsed && <span className="truncate">{label}</span>}
    </Link>
  )

  if (collapsed) {
    return (
      <Tooltip delayDuration={0}>
        <TooltipTrigger asChild>{item}</TooltipTrigger>
        <TooltipContent side="right" className="font-medium text-xs">
          {label}
        </TooltipContent>
      </Tooltip>
    )
  }

  return item
}

function IstadLogo() {
  return (
    <img
      src={istadSeal}
      alt="ISTAD Institute"
      className="h-8 w-8 shrink-0 object-contain rounded-full shadow-sm"
    />
  )
}

export default function Sidebar({ user, collapsed, onToggle }) {
  const { tr, lang } = useApp()
  const navigate = useNavigate()
  const [previewAsUser, setPreviewAsUser] = useState(false)

  // Real Superadmin / Admin check
  const isSuperAdmin = user?.roles?.some(r =>
    r?.name === 'ROLE_SUPERADMIN' || r === 'ROLE_SUPERADMIN'
  ) || user?.username === 'superadmin' || user?.role === 'ROLE_SUPERADMIN'

  const isAdmin = isSuperAdmin || user?.roles?.some(r =>
    r?.name === 'ROLE_ADMIN' || r === 'ROLE_ADMIN'
  ) || user?.role?.includes('ADMIN') || user?.username === 'admin'

  const showAdminSection = isAdmin && !previewAsUser

  const displayName = user?.givenName
    ? `${user.givenName} ${user.familyName || ''}`.trim()
    : (user?.name || (isSuperAdmin ? 'Super Administrator' : 'System Administrator'))

  const initials = getMainNameInitials(user, displayName)

  const displayEmail = user?.email || (isSuperAdmin ? 'superadmin@istad.edu.kh' : 'admin@istad.edu.kh')
  const roleTitle = isSuperAdmin ? 'Super Admin' : (isAdmin ? 'Realm Admin' : 'End User')

  function handleSignOut() {
    try {
      localStorage.clear()
      sessionStorage.clear()
    } catch (e) {}
    window.location.href = '/logout'
  }

  return (
    <TooltipProvider>
      <aside
        className={cn(
          'flex h-full shrink-0 flex-col border-r border-border bg-card transition-[width] duration-200 ease-in-out',
          collapsed ? 'w-[72px]' : 'w-[256px]'
        )}
      >
        {/* Brand Header */}
        <div className="flex h-14 items-center gap-2.5 border-b border-border px-4">
          <IstadLogo />
          {!collapsed && (
            <div className="flex items-center gap-1.5 overflow-hidden">
              <span className="font-bold tracking-tight text-foreground">
                ISTAD
              </span>
              <span className="text-muted-foreground/40">/</span>
              <span className="truncate text-xs font-normal text-muted-foreground">
                Identity &amp; Access
              </span>
            </div>
          )}
        </div>

        {/* Navigation Groups */}
        <div className="flex-1 space-y-6 overflow-y-auto px-3 py-4">
          {/* Group 1: My account */}
          <div>
            {!collapsed && (
              <div className="mb-1.5 px-3">
                <p className="text-xs font-normal text-slate-500 dark:text-slate-400">
                  {tr('nav.myAccount')}
                </p>
              </div>
            )}
            <nav className="space-y-0.5">
              <NavItem
                to="/account"
                end
                icon={LayoutGrid}
                label={tr('nav.overview')}
                collapsed={collapsed}
              />
              <NavItem
                to="/account/profile"
                icon={User}
                label={tr('nav.profile')}
                collapsed={collapsed}
              />
              <NavItem
                to="/account/security"
                icon={KeyIcon}
                label={tr('nav.security')}
                collapsed={collapsed}
              />
              <NavItem
                to="/account/linked-accounts"
                aliases={['/account/linked', '/profile/accounts']}
                icon={Link2}
                label={tr('nav.linkedAccounts')}
                collapsed={collapsed}
              />
            </nav>
          </div>

          {/* Group 2: Administration */}
          {showAdminSection ? (
            <div>
              {!collapsed && (
                <div className="mb-1.5 px-3">
                  <p className="text-xs font-normal text-slate-500 dark:text-slate-400">
                    {tr('nav.admin')}
                  </p>
                </div>
              )}
              <nav className="space-y-0.5">
                <NavItem
                  to="/admin"
                  end
                  icon={ShieldCheck}
                  label={tr('nav.adminOverview')}
                  collapsed={collapsed}
                />
                <NavItem
                  to="/admin/users"
                  aliases={['/admin/users']}
                  icon={Users}
                  label={tr('nav.users')}
                  collapsed={collapsed}
                />
                <NavItem
                  to="/admin/roles"
                  aliases={['/admin/roles']}
                  icon={ShieldCheck}
                  label={tr('nav.roles')}
                  collapsed={collapsed}
                />
                <NavItem
                  to="/admin/permissions"
                  icon={Fingerprint}
                  label={tr('nav.permissions')}
                  collapsed={collapsed}
                />
                <NavItem
                  to="/admin/applications"
                  aliases={['/admin/clients', '/admin/clients/new', '/admin/applications/new']}
                  icon={AppWindow}
                  label={tr('nav.applications')}
                  collapsed={collapsed}
                />
                <NavItem
                  to="/admin/audit"
                  icon={ScrollText}
                  label={tr('nav.auditLog')}
                  collapsed={collapsed}
                />
                <NavItem
                  to="/admin/configuration"
                  aliases={['/admin/security-config']}
                  icon={Settings}
                  label={tr('nav.configuration')}
                  collapsed={collapsed}
                />
              </nav>
            </div>
          ) : (
            isAdmin && previewAsUser && !collapsed && (
              <div className="mx-2 rounded-lg border border-dashed border-amber-300 bg-amber-50/50 p-2.5 text-[11px] text-amber-800 dark:border-amber-800 dark:bg-amber-950/20 dark:text-amber-300">
                <p className="font-medium flex items-center gap-1">
                  <Eye size={12} /> Viewing as Regular User
                </p>
                <p className="mt-0.5 text-[10px] text-muted-foreground">Admin panel hidden. Click profile below to toggle back.</p>
              </div>
            )
          )}
        </div>

        {/* Footer Profile Dropdown */}
        <div className="border-t border-border p-2.5">
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <button
                className={cn(
                  'flex w-full items-center gap-2.5 rounded-lg p-2 text-left transition-colors duration-150 hover:bg-blue-50/70 dark:hover:bg-slate-800/50 focus:outline-none cursor-pointer'
                )}
              >
                <Avatar className="h-8 w-8 shrink-0">
                  {isValidPictureUrl(user?.pictureUrl) && (
                    <AvatarImage src={user.pictureUrl} alt="" className="object-cover" />
                  )}
                  <AvatarFallback className="bg-blue-100 text-blue-700 text-xs font-semibold dark:bg-blue-950 dark:text-blue-300">
                    {initials}
                  </AvatarFallback>
                </Avatar>
                {!collapsed && (
                  <div className="min-w-0 flex-1">
                    <p className="truncate text-xs font-semibold text-foreground leading-tight">
                      {displayName}
                    </p>
                    <p className="truncate text-[11px] text-muted-foreground leading-tight mt-0.5">
                      {displayEmail}
                    </p>
                  </div>
                )}
                {!collapsed && (
                  <ChevronsUpDown size={15} className="shrink-0 text-muted-foreground/70" />
                )}
              </button>
            </DropdownMenuTrigger>
            <DropdownMenuContent
              side={collapsed ? 'right' : 'top'}
              align="start"
              className="w-60 mb-2"
            >
              <DropdownMenuLabel className="font-normal">
                <div className="flex flex-col space-y-1">
                  <div className="flex items-center justify-between">
                    <p className="text-xs font-semibold text-foreground">{displayName}</p>
                    <Badge variant="secondary" className="text-[10px]">{roleTitle}</Badge>
                  </div>
                  <p className="text-[11px] text-muted-foreground truncate">{displayEmail}</p>
                </div>
              </DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem onClick={() => navigate('/account/profile')} className="cursor-pointer text-xs">
                <User className="mr-2 h-3.5 w-3.5" /> My Account Console
              </DropdownMenuItem>
              {isAdmin && (
                <DropdownMenuItem onClick={() => navigate('/admin')} className="cursor-pointer text-xs">
                  <ShieldCheck className="mr-2 h-3.5 w-3.5 text-blue-600" /> ISTAD IAM Admin Console
                </DropdownMenuItem>
              )}
              {isAdmin && (
                <DropdownMenuItem onClick={() => setPreviewAsUser(!previewAsUser)} className="cursor-pointer text-xs">
                  <Eye className="mr-2 h-3.5 w-3.5" />
                  {previewAsUser ? 'Exit User Preview' : 'Preview as Regular User'}
                </DropdownMenuItem>
              )}
              <DropdownMenuSeparator />
              <DropdownMenuItem onClick={handleSignOut} className="cursor-pointer text-xs text-rose-600 focus:text-rose-600">
                <LogOut className="mr-2 h-3.5 w-3.5" /> {tr('common.signOut')}
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </aside>
    </TooltipProvider>
  )
}
