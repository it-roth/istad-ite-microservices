import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useApp } from '@/context/AppContext'
import { meApi } from '@/lib/api'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import {
  Info,
  CheckCircle2,
  AlertCircle,
  Unlink2,
  ArrowRight,
  Calendar,
  AlertTriangle,
  Link2,
} from 'lucide-react'
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
  const [unlinkingTarget, setUnlinkingTarget] = useState(null)
  const [unlinkingLoading, setUnlinkingLoading] = useState(false)

  function loadIdentities() {
    setLoading(true)
    setError(null)
    meApi
      .identities()
      .then((data) => {
        setIdentities(Array.isArray(data) ? data : [])
      })
      .catch((err) => {
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
      setSuccess(
        lang === 'km'
          ? `បានភ្ជាប់គណនី ${linked.toUpperCase()} ដោយជោគជ័យ!`
          : `Successfully linked ${linked.toUpperCase()} account!`
      )
      window.history.replaceState({}, '', window.location.pathname)
    }
    const err = searchParams.get('error')
    if (err) {
      setError(
        lang === 'km'
          ? 'មិនអាចភ្ជាប់គណនីបានទេ៖ គណនីនេះប្រហែលជាត្រូវបានភ្ជាប់ជាមួយអ្នកផ្សេងរួចហើយ។'
          : 'Failed to link account: it might already be linked to another user.'
      )
      window.history.replaceState({}, '', window.location.pathname)
    }
  }, [])

  async function handleConfirmUnlink() {
    if (!unlinkingTarget) return
    setUnlinkingLoading(true)
    try {
      await meApi.unlinkIdentity(unlinkingTarget.uuid)
      setIdentities((ids) => ids.filter((i) => (i.uuid || i.id) !== unlinkingTarget.uuid))
      setSuccess(
        lang === 'km'
          ? `បានផ្តាច់ការភ្ជាប់ ${unlinkingTarget.providerName} ដោយជោគជ័យ។`
          : `Successfully unlinked ${unlinkingTarget.providerName} account.`
      )
      setUnlinkingTarget(null)
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      setError(err.message)
      setTimeout(() => setError(null), 4000)
    } finally {
      setUnlinkingLoading(false)
    }
  }

  function handleLink(provider) {
    setLinking(provider.id)
    setError(null)
    window.location.href = `/oauth2/link/${provider.id}`
  }

  const hasLinkedAccount = Boolean(identities && identities.length > 0)

  return (
    <div className="min-h-full bg-background p-4 md:p-8">
      <div className="mx-auto max-w-4xl space-y-6">
        {/* Breadcrumb & Header */}
        <div>
          <div className="flex items-center gap-2 text-xs text-muted-foreground mb-1">
            <Link to="/account" className="hover:text-foreground transition-colors">
              {tr('nav.myAccount')}
            </Link>
            <span>/</span>
            <span className="text-foreground font-medium">{tr('linked.title')}</span>
          </div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground">
            {tr('linked.title')}
          </h1>
          <p className="mt-1 text-xs text-muted-foreground">
            {lang === 'km'
              ? 'គ្រប់គ្រង និងភ្ជាប់គណនីសង្គមរបស់អ្នកដើម្បីចូលប្រើប្រាស់។'
              : 'Connect your social accounts to log in faster and securely.'}
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

        {/* Hint Notice: ONLY shown when user has NO linked accounts; HIDDEN once any account is linked */}
        {!hasLinkedAccount && !loading && (
          <div className="flex items-start gap-2.5 p-3 rounded-lg border border-blue-200/60 bg-blue-50/50 dark:border-blue-900/40 dark:bg-blue-950/30 text-xs text-blue-700 dark:text-blue-300 animate-in fade-in duration-150">
            <Info size={15} className="shrink-0 text-blue-600 dark:text-blue-400 mt-0.5" />
            <span className="leading-relaxed">
              {lang === 'km'
                ? 'អ្នកត្រូវរក្សាវិធីចូលគណនីយ៉ាងហោចណាស់មួយ។ ការផ្តាច់គណនីមានន័យថាអ្នកមិនអាចប្រើវាដើម្បីចូលគណនីបានទៀតទេ។'
                : 'You must maintain at least one sign-in method. Unlinking a provider means you can no longer sign in with it.'}
            </span>
          </div>
        )}

        {/* Modern Line-by-Line List Layout (Row by Row) */}
        {loading ? (
          <div className="flex justify-center py-16">
            <div className="h-7 w-7 animate-spin rounded-full border-2 border-blue-600 border-t-transparent" />
          </div>
        ) : (
          <Card className="rounded-xl border border-border bg-card shadow-xs overflow-hidden">
            <div className="divide-y divide-border">
              {AVAILABLE_PROVIDERS.map((prov) => {
                const Icon = prov.icon
                const linked = identities.find(
                  (i) => (i.provider || '').toLowerCase() === prov.id
                )
                const isLinked = !!linked
                const displayEmail =
                  linked?.email || linked?.displayName || linked?.subject || ''

                return (
                  <div
                    key={prov.id}
                    className="p-4 sm:p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4 hover:bg-muted/30 transition-colors"
                  >
                    {/* Left: Icon, Provider Title, Connected Email/Description */}
                    <div className="flex items-start sm:items-center gap-3.5 min-w-0 flex-1">
                      <div className="w-10 h-10 rounded-xl bg-muted/60 dark:bg-muted/40 border border-border/60 flex items-center justify-center shrink-0 shadow-2xs mt-0.5 sm:mt-0">
                        <Icon className="h-5 w-5" />
                      </div>

                      <div className="min-w-0 flex-1">
                        <div className="flex items-center gap-2 flex-wrap">
                          <h2 className="text-sm font-semibold text-foreground">
                            {prov.name}
                          </h2>
                          <Badge
                            variant="outline"
                            className={`text-[10px] font-medium px-2 py-0.5 rounded-full inline-flex items-center gap-1 ${
                              isLinked
                                ? 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-950/60 dark:text-emerald-400 dark:border-emerald-800/60'
                                : 'bg-muted/50 text-muted-foreground border-border/70'
                            }`}
                          >
                            <span
                              className={`w-1.5 h-1.5 rounded-full shrink-0 ${
                                isLinked ? 'bg-emerald-500' : 'bg-slate-400 dark:bg-slate-500'
                              }`}
                            />
                            <span>
                              {isLinked
                                ? lang === 'km'
                                  ? 'បានភ្ជាប់'
                                  : 'Connected'
                                : lang === 'km'
                                ? 'មិនបានភ្ជាប់'
                                : 'Not connected'}
                            </span>
                          </Badge>
                        </div>

                        {isLinked ? (
                          <div className="flex flex-wrap items-center gap-2 mt-1.5">
                            <span className="font-mono text-xs text-foreground bg-muted/50 dark:bg-muted/30 px-2 py-0.5 rounded-md border border-border/60 font-medium truncate max-w-xs sm:max-w-md">
                              {displayEmail}
                            </span>
                            {linked?.linkedAt && (
                              <span className="text-[11px] text-muted-foreground flex items-center gap-1">
                                <Calendar size={11} className="opacity-70" />
                                <span>
                                  {lang === 'km' ? 'ភ្ជាប់នៅ៖ ' : 'Linked '}
                                  {new Date(linked.linkedAt).toLocaleDateString()}
                                </span>
                              </span>
                            )}
                          </div>
                        ) : (
                          <p className="text-xs text-muted-foreground mt-0.5">
                            {lang === 'km' ? `ចូលប្រើប្រាស់តាមរយៈ ${prov.name}` : prov.desc}
                          </p>
                        )}
                      </div>
                    </div>

                    {/* Right: Action Button */}
                    <div className="flex items-center justify-end shrink-0 sm:pl-4">
                      {isLinked ? (
                        <Button
                          type="button"
                          variant="outline"
                          size="sm"
                          onClick={() =>
                            setUnlinkingTarget({
                              uuid: linked.uuid || linked.id,
                              providerName: prov.name,
                              email: displayEmail,
                            })
                          }
                          className="h-8 text-xs font-medium border-rose-200 dark:border-rose-900/50 text-rose-600 dark:text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-950/40 hover:text-rose-700 hover:border-rose-300 transition-colors cursor-pointer gap-1.5"
                        >
                          <Unlink2 size={13} />
                          <span>{lang === 'km' ? 'ផ្តាច់ការភ្ជាប់' : 'Unlink'}</span>
                        </Button>
                      ) : (
                        <Button
                          type="button"
                          variant="outline"
                          size="sm"
                          disabled={linking === prov.id}
                          onClick={() => handleLink(prov)}
                          className="h-8 text-xs font-medium border-border hover:border-blue-500 hover:text-blue-600 hover:bg-blue-50/50 dark:hover:border-blue-400 dark:hover:text-blue-300 dark:hover:bg-blue-950/30 transition-colors cursor-pointer flex items-center gap-1.5"
                        >
                          <span>
                            {linking === prov.id
                              ? lang === 'km'
                                ? 'កំពុងភ្ជាប់…'
                                : 'Connecting…'
                              : lang === 'km'
                              ? `ភ្ជាប់ ${prov.name}`
                              : `Connect ${prov.name}`}
                          </span>
                          <ArrowRight size={13} className="shrink-0" />
                        </Button>
                      )}
                    </div>
                  </div>
                )
              })}
            </div>
          </Card>
        )}

        {/* Confirmation Modal for Unlinking Account */}
        <AlertDialog
          open={!!unlinkingTarget}
          onOpenChange={(open) => {
            if (!open) setUnlinkingTarget(null)
          }}
        >
          <AlertDialogContent className="sm:max-w-md">
            <AlertDialogHeader>
              <AlertDialogTitle className="text-base font-semibold text-foreground flex items-center gap-2">
                <AlertTriangle className="text-rose-600 dark:text-rose-400" size={18} />
                <span>
                  {lang === 'km'
                    ? `ផ្តាច់ការភ្ជាប់គណនី ${unlinkingTarget?.providerName}?`
                    : `Unlink ${unlinkingTarget?.providerName} account?`}
                </span>
              </AlertDialogTitle>
              <AlertDialogDescription className="text-xs text-muted-foreground leading-relaxed pt-1">
                {lang === 'km' ? (
                  <>
                    តើអ្នកប្រាកដថាចង់ផ្តាច់ការភ្ជាប់គណនី <strong>{unlinkingTarget?.email}</strong> ដែរឬទេ? អ្នកនឹងមិនអាចប្រើវាដើម្បីចូលគណនីបានទៀតឡើយ។
                  </>
                ) : (
                  <>
                    Are you sure you want to unlink <strong>{unlinkingTarget?.email}</strong>? You will no longer be able to use this provider to sign in.
                  </>
                )}
              </AlertDialogDescription>
            </AlertDialogHeader>

            <AlertDialogFooter className="mt-3">
              <AlertDialogCancel disabled={unlinkingLoading} className="text-xs">
                {tr('common.cancel')}
              </AlertDialogCancel>
              <AlertDialogAction
                onClick={handleConfirmUnlink}
                disabled={unlinkingLoading}
                className="bg-rose-600 hover:bg-rose-700 text-white text-xs font-semibold"
              >
                {unlinkingLoading
                  ? lang === 'km'
                    ? 'កំពុងផ្តាច់…'
                    : 'Unlinking…'
                  : lang === 'km'
                  ? 'យល់ព្រម ផ្តាច់ការភ្ជាប់'
                  : 'Yes, unlink account'}
              </AlertDialogAction>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      </div>
    </div>
  )
}
