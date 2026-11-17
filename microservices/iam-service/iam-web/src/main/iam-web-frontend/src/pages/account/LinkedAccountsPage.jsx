import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useApp } from '@/context/AppContext'
import { meApi } from '@/lib/api'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Info, CheckCircle2, AlertCircle } from 'lucide-react'
import { GoogleIcon, GitHubIcon, FacebookIcon, TelegramIcon } from '@/components/ui/social-icons'

const AVAILABLE_PROVIDERS = [
  { id: 'google', name: 'Google', icon: GoogleIcon, desc: 'Sign in with your Google account' },
  { id: 'github', name: 'GitHub', icon: GitHubIcon, desc: 'Sign in with your GitHub developer profile' },
  { id: 'facebook', name: 'Facebook', icon: FacebookIcon, desc: 'Sign in with your Facebook profile' },
  { id: 'telegram', name: 'Telegram', icon: TelegramIcon, desc: 'Sign in with your Telegram account' },
]

export default function LinkedAccountsPage() {
  const { tr, lang, currentUser } = useApp()
  const [identities, setIdentities] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(null)
  const [linking, setLinking] = useState(null)

  function loadIdentities() {
    setLoading(true)
    setError(null)
    meApi.identities()
      .then(data => {
        setIdentities(Array.isArray(data) ? data : [])
      })
      .catch(err => {
        setError(err.message)
        setIdentities([])
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadIdentities()
    const searchParams = new URLSearchParams(window.location.search)
    const linked = searchParams.get('linked')
    if (linked) {
      setSuccess(lang === 'km' ? `បានភ្ជាប់គណនី ${linked.toUpperCase()} ដោយជោគជ័យ!` : `Successfully linked ${linked.toUpperCase()} account!`)
      window.history.replaceState({}, '', window.location.pathname)
    }
    const err = searchParams.get('error')
    if (err) {
      setError(lang === 'km' ? 'មិនអាចភ្ជាប់គណនីបានទេ៖ គណនីនេះប្រហែលជាត្រូវបានភ្ជាប់ជាមួយអ្នកផ្សេងរួចហើយ។' : 'Failed to link account: it might already be linked to another user.')
      window.history.replaceState({}, '', window.location.pathname)
    }
  }, [])

  async function unlink(identityUuid) {
    const confirmMsg = lang === 'km' ? 'តើចង់ផ្តាច់ការភ្ជាប់គណនីនេះឬ?' : 'Are you sure you want to unlink this account?'
    if (!window.confirm(confirmMsg)) return
    try {
      await meApi.unlinkIdentity(identityUuid)
      setIdentities(ids => ids.filter(i => (i.uuid || i.id) !== identityUuid))
      setSuccess(lang === 'km' ? 'បានផ្តាច់គណនីដោយជោគជ័យ។' : 'Successfully unlinked account.')
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      setError(err.message)
      setTimeout(() => setError(null), 4000)
    }
  }

  function handleLink(provider) {
    setLinking(provider.id)
    setError(null)
    window.location.href = `/oauth2/link/${provider.id}`
  }

  return (
    <div className="min-h-full bg-background p-4 md:p-8">
      <div className="mx-auto max-w-4xl space-y-6">

        {/* Breadcrumb & Header */}
        <div>
          <div className="flex items-center gap-2 text-xs text-muted-foreground mb-1">
            <Link to="/account" className="hover:text-foreground transition-colors">{tr('nav.myAccount')}</Link>
            <span>/</span>
            <span>{tr('linked.title')}</span>
          </div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground">
            {tr('linked.title')}
          </h1>
          <p className="mt-1 text-xs text-muted-foreground">
            {lang === 'km' ? 'ជ្រើសរើសគណនីដែលអ្នកអាចប្រើដើម្បីចូល។' : tr('linked.subtitle')}
          </p>
        </div>

        {/* Feedback Alert */}
        {error && (
          <div className="rounded-lg bg-rose-50 border border-rose-200 dark:bg-rose-950/40 dark:border-rose-900/50 p-3 text-xs text-rose-700 dark:text-rose-300 flex items-center gap-2">
            <AlertCircle size={15} className="shrink-0 text-rose-600" />
            <span>{error}</span>
          </div>
        )}
        {success && (
          <div className="rounded-lg bg-emerald-50 border border-emerald-200 dark:bg-emerald-950/40 dark:border-emerald-900/50 p-3 text-xs text-emerald-700 dark:text-emerald-300 flex items-center gap-2">
            <CheckCircle2 size={15} className="shrink-0 text-emerald-600" />
            <span>{success}</span>
          </div>
        )}

        {/* Hint Notice */}
        <div className="flex items-center gap-2.5 p-3.5 rounded-lg border border-blue-200/60 bg-blue-50/50 dark:border-blue-900/40 dark:bg-blue-950/30 text-xs text-blue-700 dark:text-blue-300">
          <Info size={16} className="shrink-0 text-blue-600 dark:text-blue-400" />
          <span>
            {lang === 'km'
              ? 'អ្នកត្រូវរក្សាវិធីចូលគណនីយ៉ាងហោចណាស់មួយ។ ការផ្តាច់គណនីមានន័យថាអ្នកមិនអាចប្រើវាដើម្បីចូលគណនីបានទៀតទេ។'
              : 'You must maintain at least one sign-in method. Unlinking a provider means you can no longer sign in with it.'}
          </span>
        </div>

        {/* 4 Provider Cards Grid - matching Taing Sengkim's design */}
        {loading ? (
          <div className="flex justify-center py-16">
            <div className="h-7 w-7 animate-spin rounded-full border-2 border-blue-600 border-t-transparent" />
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {AVAILABLE_PROVIDERS.map(prov => {
              const Icon = prov.icon
              const linked = identities.find(i => (i.provider || '').toLowerCase() === prov.id)
              const isLinked = !!linked

              return (
                <Card
                  key={prov.id}
                  className="rounded-xl border border-border bg-card shadow-xs overflow-hidden flex flex-col justify-between transition-colors"
                >
                  <CardContent className="p-5 flex flex-col justify-between h-full gap-4">
                    <div>
                      {/* Card Header: Icon & Badge */}
                      <div className="flex items-center justify-between mb-3.5">
                        <div className="w-10 h-10 rounded-xl bg-muted/60 flex items-center justify-center shrink-0">
                          <Icon size={20} />
                        </div>
                        <Badge
                          variant="outline"
                          className={`text-[10px] font-medium transition-colors ${isLinked
                            ? 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-950 dark:text-emerald-300'
                            : 'bg-slate-100 text-slate-600 border-slate-300 dark:bg-slate-800 dark:text-slate-400'
                            }`}
                        >
                          {isLinked
                            ? (lang === 'km' ? 'បានភ្ជាប់' : 'Connected')
                            : (lang === 'km' ? 'មិនទាន់បានភ្ជាប់' : 'Not connected')}
                        </Badge>
                      </div>

                      {/* Provider Title */}
                      <h2 className="text-sm font-semibold text-foreground">
                        {prov.name}
                      </h2>

                      {/* Details / Description */}
                      {isLinked ? (
                        <div className="mt-2.5 space-y-1.5 text-xs text-muted-foreground">
                          <p className="font-mono text-foreground text-[11px] font-medium truncate bg-muted/30 p-1.5 rounded-md">
                            {linked.subject || linked.email || `${currentUser?.username || 'user'}@${prov.id}`}
                          </p>
                          <div className="flex items-center justify-between text-[11px] pt-1">
                            <span>{lang === 'km' ? 'បានភ្ជាប់នៅ' : 'Linked at'}:</span>
                            <span className="font-medium text-foreground">{new Date().toLocaleDateString()}</span>
                          </div>
                          <div className="flex items-center justify-between text-[11px]">
                            <span>{lang === 'km' ? 'ការចូលគណនីចុងក្រោយ' : 'Last sign-in'}:</span>
                            <span className="font-medium text-foreground">{new Date().toLocaleDateString()}</span>
                          </div>
                        </div>
                      ) : (
                        <p className="text-xs text-muted-foreground mt-1.5 min-h-[44px]">
                          {lang === 'km' ? `ចូលប្រើប្រាស់ជាមួយ ${prov.name}` : prov.desc}
                        </p>
                      )}
                    </div>

                    {/* Action Button */}
                    <div className="pt-2">
                      {isLinked ? (
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => unlink(linked.uuid || linked.id)}
                          className="w-full text-xs h-8 border-rose-200 text-rose-600 hover:bg-rose-50 hover:text-rose-700 hover:border-rose-300 dark:border-rose-900/50 dark:hover:bg-rose-950/40 cursor-pointer transition-colors"
                        >
                          {lang === 'km' ? 'ផ្តាច់ការភ្ជាប់' : 'Unlink'}
                        </Button>
                      ) : (
                        <Button
                          variant="outline"
                          size="sm"
                          disabled={linking === prov.id}
                          onClick={() => handleLink(prov)}
                          className="w-full text-xs h-8 border-border hover:bg-muted/80 cursor-pointer transition-colors flex items-center justify-center gap-1.5"
                        >
                          <span>
                            {linking === prov.id
                              ? (lang === 'km' ? 'កំពុងភ្ជាប់…' : 'Linking…')
                              : (lang === 'km' ? `ភ្ជាប់ ${prov.name}` : `Link ${prov.name}`)}
                          </span>
                          <span aria-hidden="true">&rarr;</span>
                        </Button>
                      )}
                    </div>
                  </CardContent>
                </Card>
              )
            })}
          </div>
        )}

      </div>
    </div>
  )
}
