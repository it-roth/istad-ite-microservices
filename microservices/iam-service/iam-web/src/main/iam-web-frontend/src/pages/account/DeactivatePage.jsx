import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useApp } from '@/context/AppContext'
import { meApi } from '@/lib/api'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog'
import {
  AlertTriangle,
  UserX,
  ShieldAlert,
  CheckCircle2,
  AlertCircle,
  X,
  LogOut,
  Info,
} from 'lucide-react'

export default function DeactivatePage() {
  const { tr, user, lang } = useApp()
  const navigate = useNavigate()

  const [reasonCategory, setReasonCategory] = useState('')
  const [reasonText, setReasonText] = useState('')
  const [confirmInput, setConfirmInput] = useState('')
  const [loading, setLoading] = useState(false)
  const [status, setStatus] = useState(null)
  const [msg, setMsg] = useState('')

  const confirmText = 'deactivate my account'
  const userIdentifier = user?.username || user?.email || ''
  const isValid =
    confirmInput.trim().toLowerCase() === confirmText ||
    (userIdentifier && confirmInput.trim().toLowerCase() === userIdentifier.toLowerCase())

  async function handleDeactivate() {
    setLoading(true)
    setStatus(null)
    try {
      const fullReason = [reasonCategory, reasonText].filter(Boolean).join(' - ')
      await meApi.deactivate({ reason: fullReason })
      setStatus('success')
      setMsg(
        lang === 'km'
          ? 'គណនីរបស់អ្នកត្រូវបានបិទដំណើរការដោយជោគជ័យ។ កំពុងនាំអ្នកទៅកាន់ទំព័រ Login...'
          : 'Your account has been deactivated. Redirecting to login...'
      )
      // Redirect to login page immediately
      setTimeout(() => {
        window.location.href = '/login?logout'
      }, 1200)
    } catch (err) {
      console.error(err)
      setStatus('error')
      setMsg(
        err.message ||
        (lang === 'km'
          ? 'ការបិទដំណើរការគណនីបានបរាជ័យ សូមព្យាយាមម្តងទៀត។'
          : 'Failed to deactivate account. Please try again.')
      )
      setLoading(false)
    }
  }

  return (
    <div className="min-h-full bg-background p-4 md:p-8">
      <div className="mx-auto max-w-4xl space-y-8">
        {/* Breadcrumb & Header */}
        <div>
          <div className="flex items-center gap-2 text-xs text-muted-foreground mb-1">
            <Link to="/account" className="hover:text-foreground transition-colors">
              {tr('nav.myAccount')}
            </Link>
            <span>/</span>
            <Link to="/account/security" className="hover:text-foreground transition-colors">
              {lang === 'km' ? 'សុវត្ថិភាព' : 'Security'}
            </Link>
            <span>/</span>
            <span className="text-foreground font-medium">{tr('deactivate.title')}</span>
          </div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground flex items-center gap-2.5">
            {/* <div className="p-1.5 rounded-lg bg-rose-50 dark:bg-rose-950/50 text-rose-600 dark:text-rose-400">
              <UserX size={22} />
            </div> */}
            <span>{tr('deactivate.title')}</span>
          </h1>
          <p className="mt-1 text-xs text-muted-foreground">
            {tr('deactivate.subtitle')}
          </p>
        </div>

        {/* Top-Right Floating Notification Toast */}
        {status && (
          <div className="fixed top-6 right-6 z-50 max-w-sm w-full pointer-events-auto animate-in fade-in slide-in-from-top-3 duration-200">
            <div
              className={`rounded-xl border p-4 shadow-xl backdrop-blur-md flex items-start gap-3 ${status === 'success'
                ? 'bg-white/95 dark:bg-slate-900/95 border-emerald-200 dark:border-emerald-800 text-slate-900 dark:text-slate-100'
                : 'bg-white/95 dark:bg-slate-900/95 border-rose-200 dark:border-rose-800 text-slate-900 dark:text-slate-100'
                }`}
            >
              <div
                className={`p-1.5 rounded-full shrink-0 ${status === 'success'
                  ? 'bg-emerald-100 text-emerald-600 dark:bg-emerald-950/60 dark:text-emerald-400'
                  : 'bg-rose-100 text-rose-600 dark:bg-rose-950/60 dark:text-rose-400'
                  }`}
              >
                {status === 'success' ? <CheckCircle2 size={16} /> : <AlertCircle size={16} />}
              </div>
              <div className="flex-1 min-w-0 pr-2">
                <p className="text-xs font-semibold text-foreground">
                  {status === 'success' ? tr('common.success') : tr('common.error')}
                </p>
                <p className="text-xs text-muted-foreground mt-0.5 break-words">{msg}</p>
              </div>
              <button
                type="button"
                onClick={() => {
                  setStatus(null)
                  setMsg('')
                }}
                className="text-muted-foreground hover:text-foreground p-1 rounded-md transition-colors"
              >
                <X size={14} />
              </button>
            </div>
          </div>
        )}

        {/* Section 1: Warning Notice & Impact */}
        <div className="grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
          <div className="md:col-span-4">
            <h2 className="text-sm font-semibold text-foreground flex items-center gap-1.5">
              <ShieldAlert size={16} className="text-rose-600 dark:text-rose-400" />
              <span>{lang === 'km' ? 'ផលប៉ះពាល់នៃការបិទដំណើរការ' : 'What happens next'}</span>
            </h2>
            <p className="mt-1 text-xs text-muted-foreground leading-relaxed">
              {lang === 'km'
                ? 'សូមអានព័ត៌មានលម្អិតអំពីអ្វីដែលនឹងកើតឡើងចំពោះគណនី និងទិន្នន័យរបស់អ្នក។'
                : 'Review the consequences before permanently deactivating your account access.'}
            </p>
          </div>

          <div className="md:col-span-8 space-y-4">
            <Card className="border-rose-200 dark:border-rose-900/50 bg-rose-50/40 dark:bg-rose-950/20 shadow-xs">
              <CardContent className="p-5 space-y-3">
                <div className="flex items-start gap-3">
                  <AlertTriangle className="h-5 w-5 text-rose-600 dark:text-rose-400 shrink-0 mt-0.5" />
                  <div>
                    <h3 className="text-xs font-semibold text-rose-900 dark:text-rose-300">
                      {lang === 'km'
                        ? 'ការព្រមាន៖ សកម្មភាពនេះមិនអាចត្រឡប់វិញដោយខ្លួនឯងបានទេ'
                        : 'Warning: This action requires administrator support to reverse'}
                    </h3>
                    <p className="text-xs text-rose-800/90 dark:text-rose-300/80 mt-0.5 leading-relaxed">
                      {tr('deactivate.warning')}
                    </p>
                  </div>
                </div>

                <div className="pt-2 border-t border-rose-200/60 dark:border-rose-900/40 grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs text-slate-700 dark:text-slate-300">
                  <div className="flex items-start gap-2">
                    <span className="text-rose-600 font-bold">•</span>
                    <span>{lang === 'km' ? 'គណនីរបស់អ្នកនឹងត្រូវបិទមិនឱ្យ Login' : 'Account will be disabled immediately'}</span>
                  </div>
                  <div className="flex items-start gap-2">
                    <span className="text-rose-600 font-bold">•</span>
                    <span>{lang === 'km' ? 'Session លើគ្រប់ឧបករណ៍ទាំងអស់នឹងត្រូវផ្ដាច់' : 'All active sessions terminated'}</span>
                  </div>
                  <div className="flex items-start gap-2">
                    <span className="text-rose-600 font-bold">•</span>
                    <span>{lang === 'km' ? 'OAuth2 tokens & កម្មវិធីភ្ជាប់ត្រូវបានដកសិទ្ធិ' : 'OAuth2 tokens will be revoked'}</span>
                  </div>
                  <div className="flex items-start gap-2">
                    <span className="text-rose-600 font-bold">•</span>
                    <span>{lang === 'km' ? 'ប្រព័ន្ធនឹងនាំអ្នកទៅកាន់ទំព័រ Login ភ្លាមៗ' : 'Redirected directly to login page'}</span>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>

        {/* Section 2: Feedback & Reason */}
        <div className="border-t border-border pt-8 grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
          <div className="md:col-span-4">
            <h2 className="text-sm font-semibold text-foreground">
              {lang === 'km' ? 'មូលហេតុនៃការចាកចេញ' : 'Reason for leaving'}
            </h2>
            <p className="mt-1 text-xs text-muted-foreground leading-relaxed">
              {lang === 'km'
                ? 'សូមចែករំលែកមូលហេតុដើម្បីជួយយើងកែលម្អប្រព័ន្ធឱ្យកាន់តែប្រសើរ។ (ជម្រើស)'
                : 'Help us improve by telling us why you want to deactivate your account. (Optional)'}
            </p>
          </div>

          <div className="md:col-span-8 space-y-4">
            <Card className="shadow-xs">
              <CardContent className="p-5 space-y-4">
                <div className="space-y-1.5">
                  <Label htmlFor="reasonCategory" className="text-xs font-medium">
                    {lang === 'km' ? 'ជ្រើសរើសមូលហេតុ' : 'Select a primary reason'}
                  </Label>
                  <Select value={reasonCategory} onValueChange={setReasonCategory}>
                    <SelectTrigger id="reasonCategory" className="h-9 text-xs">
                      <SelectValue placeholder={lang === 'km' ? 'ជ្រើសរើសមូលហេតុ...' : 'Choose a reason...'} />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="no_longer_needed" className="text-xs">
                        {lang === 'km' ? 'ខ្ញុំលែងត្រូវការគណនីនេះទៀតហើយ' : 'I no longer need this account'}
                      </SelectItem>
                      <SelectItem value="privacy_concerns" className="text-xs">
                        {lang === 'km' ? 'កង្វល់អំពីឯកជនភាព និងសុវត្ថិភាព' : 'Privacy and security concerns'}
                      </SelectItem>
                      <SelectItem value="duplicate_account" className="text-xs">
                        {lang === 'km' ? 'ខ្ញុំមានគណនីផ្សេងទៀតរួចហើយ' : 'I have another account'}
                      </SelectItem>
                      <SelectItem value="temporary_break" className="text-xs">
                        {lang === 'km' ? 'ចង់ផ្អាកប្រើប្រាស់មួយរយៈសិន' : 'Taking a temporary break'}
                      </SelectItem>
                      <SelectItem value="other" className="text-xs">
                        {lang === 'km' ? 'មូលហេតុផ្សេងៗ' : 'Other reason'}
                      </SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-1.5">
                  <Label htmlFor="reasonText" className="text-xs font-medium">
                    {lang === 'km' ? 'មតិកែលម្អបន្ថែម (ជាជម្រើស)' : 'Additional feedback (Optional)'}
                  </Label>
                  <Textarea
                    id="reasonText"
                    rows={3}
                    className="text-xs resize-none"
                    placeholder={
                      lang === 'km'
                        ? 'ប្រាប់យើងបន្ថែមអំពីបទពិសោធន៍របស់អ្នក...'
                        : "Tell us more about your experience or why you're leaving..."
                    }
                    value={reasonText}
                    onChange={(e) => setReasonText(e.target.value)}
                  />
                </div>
              </CardContent>
            </Card>
          </div>
        </div>

        {/* Section 3: Danger Zone & Confirmation */}
        <div className="border-t border-border pt-8 grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
          <div className="md:col-span-4">
            <h2 className="text-sm font-semibold text-rose-600 dark:text-rose-400">
              {lang === 'km' ? 'ការបញ្ជាក់សុពលភាព' : 'Confirm Deactivation'}
            </h2>
            <p className="mt-1 text-xs text-muted-foreground leading-relaxed">
              {lang === 'km'
                ? 'ដើម្បីបញ្ជាក់ថានេះជាការសម្រេចចិត្តរបស់អ្នក សូមវាយពាក្យដែលបានកំណត់ខាងក្រោម។'
                : 'To confirm you want to proceed, type the confirmation phrase below.'}
            </p>
          </div>

          <div className="md:col-span-8 space-y-4">
            <Card className="border-border shadow-xs">
              <CardContent className="p-5 space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="confirmInput" className="text-xs font-medium text-foreground block">
                    {lang === 'km' ? (
                      <>
                        សូមវាយពាក្យ <span className="font-mono font-semibold text-rose-600 dark:text-rose-400">{confirmText}</span> ឬ Email/Username របស់អ្នក ដើម្បីបញ្ជាក់
                      </>
                    ) : (
                      <>
                        Type <span className="font-mono font-semibold text-rose-600 dark:text-rose-400">{confirmText}</span> to confirm
                      </>
                    )}
                  </Label>
                  <Input
                    id="confirmInput"
                    type="text"
                    className="h-9 font-mono text-xs"
                    placeholder={confirmText}
                    value={confirmInput}
                    onChange={(e) => setConfirmInput(e.target.value)}
                    autoComplete="off"
                  />
                  {confirmInput && !isValid && (
                    <p className="text-[11px] text-muted-foreground">
                      {lang === 'km'
                        ? 'ពាក្យដែលអ្នកបានវាយមិនទាន់ត្រូវគ្នាទេ'
                        : 'The phrase does not match yet'}
                    </p>
                  )}
                </div>

                <div className="pt-2 flex flex-col sm:flex-row items-center justify-between gap-3">
                  <Button
                    type="button"
                    variant="outline"
                    className="w-full sm:w-auto text-xs"
                    onClick={() => navigate('/account/security')}
                  >
                    {tr('common.cancel')}
                  </Button>

                  <AlertDialog>
                    <AlertDialogTrigger asChild>
                      <Button
                        type="button"
                        variant="destructive"
                        className="w-full sm:w-auto gap-2 bg-rose-600 hover:bg-rose-700 text-white text-xs font-semibold shadow-xs cursor-pointer"
                        disabled={!isValid || loading}
                      >
                        <UserX size={15} />
                        <span>
                          {loading
                            ? lang === 'km'
                              ? 'កំពុងបិទដំណើរការ...'
                              : 'Deactivating...'
                            : tr('deactivate.btn')}
                        </span>
                      </Button>
                    </AlertDialogTrigger>

                    <AlertDialogContent className="sm:max-w-md">
                      <AlertDialogHeader>
                        <AlertDialogTitle className="text-base font-semibold text-foreground flex items-center gap-2">
                          <AlertTriangle className="text-rose-600 dark:text-rose-400" size={18} />
                          <span>{lang === 'km' ? 'តើអ្នកប្រាកដដែរឬទេ?' : 'Are you absolutely sure?'}</span>
                        </AlertDialogTitle>
                        <AlertDialogDescription className="text-xs text-muted-foreground leading-relaxed pt-1">
                          {lang === 'km' ? (
                            <>
                              សកម្មភាពនេះនឹងបិទដំណើរការគណនី <strong>{user?.email || user?.username}</strong> ជាបន្ទាន់។ អ្នកនឹងត្រូវបាន Logout ចេញពីប្រព័ន្ធ ហើយបញ្ជូនទៅកាន់ទំព័រ Login។
                            </>
                          ) : (
                            <>
                              This action will immediately deactivate account <strong>{user?.email || user?.username}</strong>. You will be logged out and returned to the sign-in page.
                            </>
                          )}
                        </AlertDialogDescription>
                      </AlertDialogHeader>

                      <AlertDialogFooter className="mt-3">
                        <AlertDialogCancel className="text-xs">{tr('common.cancel')}</AlertDialogCancel>
                        <AlertDialogAction
                          onClick={handleDeactivate}
                          className="bg-rose-600 hover:bg-rose-700 text-white text-xs font-semibold"
                        >
                          {loading
                            ? lang === 'km'
                              ? 'កំពុងដំណើរការ...'
                              : 'Processing...'
                            : lang === 'km'
                              ? 'យល់ព្រម បិទគណនីឥឡូវនេះ'
                              : 'Yes, deactivate my account'}
                        </AlertDialogAction>
                      </AlertDialogFooter>
                    </AlertDialogContent>
                  </AlertDialog>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  )
}
