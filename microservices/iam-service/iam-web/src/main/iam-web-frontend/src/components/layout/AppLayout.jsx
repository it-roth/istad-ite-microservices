import { Outlet, useLocation } from 'react-router-dom'
import { useEffect, useState, useRef } from 'react'
import { meApi } from '@/lib/api'
import { useApp } from '@/context/AppContext'
import Sidebar from '@/components/layout/Sidebar'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from '@/components/ui/breadcrumb'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  PanelLeft, Search, Moon, Sun, Laptop, Globe, Check, Languages
} from 'lucide-react'

const routeTitles = {
  '/account': { group: 'My account', title: 'Overview' },
  '/dashboard': { group: 'My account', title: 'Overview' },
  '/account/profile': { group: 'My account', title: 'Profile' },
  '/account/security': { group: 'My account', title: 'Password & security' },
  '/account/linked': { group: 'My account', title: 'Linked accounts' },
  '/account/linked-accounts': { group: 'My account', title: 'Linked accounts' },
  '/account/sessions': { group: 'My account', title: 'Active sessions' },
  '/account/session-mgmt': { group: 'My account', title: 'Session management' },
  '/account/history': { group: 'My account', title: 'Login history' },
  '/account/activity': { group: 'My account', title: 'Account activity' },
  '/account/deactivate': { group: 'My account', title: 'Account deactivation' },
  '/admin': { group: 'Administration', title: 'Admin overview' },
  '/admin/users': { group: 'Administration', title: 'Users' },
  '/admin/roles': { group: 'Administration', title: 'Roles' },
  '/admin/permissions': { group: 'Administration', title: 'Permissions' },
  '/admin/applications': { group: 'Administration', title: 'Applications' },
  '/admin/applications/new': { group: 'Administration', title: 'Applications', sub: 'New' },
  '/admin/clients': { group: 'Administration', title: 'Applications' },
  '/admin/clients/new': { group: 'Administration', title: 'Applications', sub: 'New' },
  '/admin/audit': { group: 'Administration', title: 'Audit log' },
}

export default function AppLayout() {
  const { setUser, user, lang, switchLang, breadcrumbSub, setBreadcrumbSub } = useApp()
  const location = useLocation()
  const mainRef = useRef(null)
  const [loading, setLoading] = useState(true)
  const [collapsed, setCollapsed] = useState(false)
  const [themeMode, setThemeMode] = useState(() => {
    return localStorage.getItem('theme') || 'light'
  })

  // Automatically scroll main content to top and reset breadcrumbSub on navigation
  useEffect(() => {
    if (mainRef.current) {
      mainRef.current.scrollTop = 0
    }
    window.scrollTo(0, 0)
    setBreadcrumbSub('')
  }, [location.pathname])

  // Theme Application Effect
  useEffect(() => {
    const root = document.documentElement
    const isDark = themeMode === 'dark'
    if (isDark) {
      root.classList.add('dark')
    } else {
      root.classList.remove('dark')
    }
    try {
      localStorage.setItem('theme', themeMode)
      document.cookie = `theme=${themeMode};path=/;max-age=31536000;SameSite=Lax`
    } catch (e) { }
  }, [themeMode])

  function toggleTheme() {
    setThemeMode(prev => (prev === 'dark' ? 'light' : 'dark'))
  }

  useEffect(() => {
    meApi.get()
      .then(data => { setUser(data); setLoading(false) })
      .catch(() => {
        // Fallback demo user
        setUser({
          username: 'admin',
          givenName: 'System',
          familyName: 'Administrator',
          email: 'admin@istad.edu.kh',
          role: 'ROLE_ADMIN'
        })
        setLoading(false)
      })
  }, [setUser])

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center bg-background">
        <div className="h-8 w-8 animate-spin rounded-full border-2 border-blue-600 border-t-transparent" />
      </div>
    )
  }

  let breadcrumb = routeTitles[location.pathname]
  if (!breadcrumb) {
    if (location.pathname.startsWith('/admin/users/')) {
      const parts = location.pathname.split('/')
      breadcrumb = { group: 'Administration', title: 'Users', sub: breadcrumbSub || parts[3] || 'Details' }
    } else if (location.pathname.startsWith('/admin/roles/')) {
      const parts = location.pathname.split('/')
      breadcrumb = { group: 'Administration', title: 'Roles', sub: breadcrumbSub || parts[3] || 'Details' }
    } else {
      breadcrumb = {
        group: location.pathname.startsWith('/admin') ? 'Administration' : 'My account',
        title: location.pathname.split('/').pop().replace('-', ' ')
      }
    }
  }

  return (
    <div className="flex h-full w-full overflow-hidden bg-background text-foreground transition-colors duration-200">
      {/* Sidebar */}
      <Sidebar
        user={user}
        collapsed={collapsed}
        onToggle={() => setCollapsed(val => !val)}
      />

      {/* Main Workspace */}
      <div className="flex min-w-0 flex-1 flex-col overflow-hidden bg-background">
        {/* Top Navbar */}
        <header className="sticky top-0 z-20 flex h-14 shrink-0 items-center justify-between border-b border-border bg-background px-4 backdrop-blur-md sm:px-6 transition-colors duration-200">
          {/* Left: Sidebar Toggle & shadcn Breadcrumb */}
          <div className="flex items-center gap-2.5">
            <Button
              variant="ghost"
              size="icon"
              onClick={() => setCollapsed(!collapsed)}
              className="h-8 w-8 text-muted-foreground hover:text-blue-600 hover:bg-blue-50/50 dark:hover:bg-slate-800"
              aria-label="Toggle sidebar"
            >
              <PanelLeft size={18} />
            </Button>

            <div className="h-4 w-px bg-border/70" />

            <Breadcrumb>
              <BreadcrumbList className="text-xs sm:text-sm">
                <BreadcrumbItem>
                  <span className="text-muted-foreground">{breadcrumb.group}</span>
                </BreadcrumbItem>
                <BreadcrumbSeparator className="text-muted-foreground/40">/</BreadcrumbSeparator>
                <BreadcrumbItem>
                  {breadcrumb.sub ? (
                    <span className="text-muted-foreground">{breadcrumb.title}</span>
                  ) : (
                    <BreadcrumbPage className="font-semibold text-foreground">
                      {breadcrumb.title}
                    </BreadcrumbPage>
                  )}
                </BreadcrumbItem>
                {breadcrumb.sub && (
                  <>
                    <BreadcrumbSeparator className="text-muted-foreground/40">/</BreadcrumbSeparator>
                    <BreadcrumbItem>
                      <BreadcrumbPage className="font-semibold text-foreground">
                        {breadcrumb.sub}
                      </BreadcrumbPage>
                    </BreadcrumbItem>
                  </>
                )}
              </BreadcrumbList>
            </Breadcrumb>
          </div>

          {/* Right: Search, Language Switcher, Theme Switcher — Clean Minimalist White & Blue */}
          <div className="flex items-center gap-2 sm:gap-3">
            {/* Search Input Box with Command Shortcut */}
            <div className="relative hidden items-center md:flex">
              <Search
                size={14}
                className="pointer-events-none absolute left-3 text-slate-400"
              />
              <Input
                type="text"
                placeholder={lang === 'km' ? 'ស្វែងរក ឬ ទៅកាន់...' : 'Search or jump to...'}
                className="h-8 w-56 rounded-md border border-slate-200 bg-white dark:border-slate-800 dark:bg-slate-900 pl-8 pr-12 text-xs placeholder:text-slate-400 focus-visible:ring-1 focus-visible:ring-blue-500 shadow-xs"
              />
              <kbd className="pointer-events-none absolute right-2 rounded border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/80 px-1.5 py-0.5 text-[10px] font-medium text-slate-500 dark:text-slate-400">
                ⌘ K
              </kbd>
            </div>

            {/* Language Switcher — Clean White & Blue */}
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <button
                  type="button"
                  className="flex items-center gap-1.5 px-2.5 py-1.5 rounded-md border border-slate-200 bg-white text-xs font-medium text-slate-700 hover:text-blue-600 hover:border-blue-300 dark:border-slate-800 dark:bg-slate-900 dark:text-slate-200 dark:hover:text-blue-400 transition-colors cursor-pointer outline-none shadow-xs"
                >
                  <Languages size={14} className="text-blue-600 dark:text-blue-400" />
                  <span>{lang === 'km' ? 'ខ្មែរ' : 'EN'}</span>
                </button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-32 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-md">
                <DropdownMenuItem
                  onClick={() => switchLang('km')}
                  className={`flex items-center justify-between text-xs cursor-pointer ${lang === 'km' ? 'text-blue-600 font-semibold bg-blue-50 dark:bg-blue-950/50' : 'hover:bg-blue-50/50'}`}
                >
                  <span>ភាសាខ្មែរ</span>
                  {lang === 'km' && <Check size={14} />}
                </DropdownMenuItem>
                <DropdownMenuItem
                  onClick={() => switchLang('en')}
                  className={`flex items-center justify-between text-xs cursor-pointer ${lang === 'en' ? 'text-blue-600 font-semibold bg-blue-50 dark:bg-blue-950/50' : 'hover:bg-blue-50/50'}`}
                >
                  <span>English</span>
                  {lang === 'en' && <Check size={14} />}
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>

            {/* Theme Toggle Button — Clean White & Blue */}
            <button
              type="button"
              onClick={toggleTheme}
              aria-label="Toggle Theme"
              className="h-8 w-8 rounded-md border border-slate-200 bg-white dark:border-slate-800 dark:bg-slate-900 text-slate-700 dark:text-slate-200 hover:text-blue-600 hover:border-blue-300 dark:hover:text-blue-400 transition-colors flex items-center justify-center cursor-pointer outline-none shadow-xs"
            >
              {themeMode === 'dark' ? (
                <Sun size={15} className="text-amber-400" />
              ) : (
                <Moon size={15} className="text-blue-600" />
              )}
            </button>
          </div>
        </header>

        {/* Scrollable Main Content */}
        <main ref={mainRef} className="flex-1 overflow-y-auto">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
