import { useEffect, useState } from 'react'
import { useApp } from '@/context/AppContext'
import { securityConfigApi } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Switch } from '@/components/ui/switch'
import { Checkbox } from '@/components/ui/checkbox'
import {
  Shield, Lock, Clock, CheckCircle2, AlertCircle, RefreshCw,
  Save
} from 'lucide-react'

export default function SystemConfigurationPage() {
  const { tr, lang } = useApp()
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [successMsg, setSuccessMsg] = useState('')

  const [config, setConfig] = useState({
    mfaRequired: false,
    maxFailedLoginAttempts: 5,
    sessionTimeoutMinutes: 30,
    passwordMinimumLength: 8,
    passwordRequireUppercase: true,
    passwordRequireNumber: true,
    passwordRequireSymbol: false,
    accessTokenLifetimeMinutes: 5,
    refreshTokenLifetimeMinutes: 60,
  })

  function loadConfig() {
    setLoading(true)
    setError('')
    securityConfigApi.get()
      .then(data => {
        if (data) {
          setConfig({
            mfaRequired: Boolean(data.mfaRequired),
            maxFailedLoginAttempts: Number(data.maxFailedLoginAttempts ?? 5),
            sessionTimeoutMinutes: Number(data.sessionTimeoutMinutes ?? 30),
            passwordMinimumLength: Number(data.passwordMinimumLength ?? 8),
            passwordRequireUppercase: Boolean(data.passwordRequireUppercase),
            passwordRequireNumber: Boolean(data.passwordRequireNumber),
            passwordRequireSymbol: Boolean(data.passwordRequireSymbol),
            accessTokenLifetimeMinutes: Number(data.accessTokenLifetimeMinutes ?? 5),
            refreshTokenLifetimeMinutes: Number(data.refreshTokenLifetimeMinutes ?? 60),
          })
        }
      })
      .catch(err => setError(err.message || 'Failed to load security configuration'))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadConfig()
  }, [])

  async function handleSave(e) {
    e.preventDefault()
    setSaving(true)
    setError('')
    setSuccessMsg('')
    try {
      const payload = {
        mfaRequired: config.mfaRequired,
        maxFailedLoginAttempts: parseInt(config.maxFailedLoginAttempts, 10) || 5,
        sessionTimeoutMinutes: parseInt(config.sessionTimeoutMinutes, 10) || 30,
        passwordMinimumLength: parseInt(config.passwordMinimumLength, 10) || 8,
        passwordRequireUppercase: config.passwordRequireUppercase,
        passwordRequireNumber: config.passwordRequireNumber,
        passwordRequireSymbol: config.passwordRequireSymbol,
        accessTokenLifetimeMinutes: parseInt(config.accessTokenLifetimeMinutes, 10) || 5,
        refreshTokenLifetimeMinutes: parseInt(config.refreshTokenLifetimeMinutes, 10) || 60,
      }
      await securityConfigApi.update(payload)
      setSuccessMsg(lang === 'km'
        ? 'បានរក្សាទុកការកំណត់ប្រព័ន្ធដោយជោគជ័យ'
        : 'System configuration updated successfully.')
      setTimeout(() => setSuccessMsg(''), 4000)
    } catch (err) {
      setError(err.message || 'Failed to update system configuration')
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-7 w-7 animate-spin rounded-full border-2 border-primary border-t-transparent" />
      </div>
    )
  }

  return (
    <div className="min-h-full bg-background p-6 md:p-8 pb-0 md:pb-0">
      <div className="mx-auto max-w-5xl space-y-8">

        {/* Header */}
        <div className="pb-4 border-b border-border/60">
          <h1 className="text-2xl sm:text-3xl font-bold tracking-tight text-foreground">
            {lang === 'km' ? 'ការកំណត់រចនាសម្ព័ន្ធប្រព័ន្ធ' : 'System Configuration'}
          </h1>
          <p className="text-xs sm:text-sm text-muted-foreground mt-1">
            {lang === 'km'
              ? 'កំណត់រចនាសម្ព័ន្ធគោលនយោបាយផ្ទៀងផ្ទាត់ គោលនយោបាយពាក្យសម្ងាត់ និងរយៈពេលផុតកំណត់នៃ Session'
              : 'Configure authentication policies, password rules, session expiration, and security constraints.'}
          </p>
        </div>

        {/* Feedback banners */}
        {error && (
          <div className="flex items-center gap-2 rounded-lg border border-red-200 bg-red-50/70 p-3 text-xs text-red-700 dark:border-red-900 dark:bg-red-950/40 dark:text-red-300">
            <AlertCircle size={15} /> {error}
          </div>
        )}
        {successMsg && (
          <div className="flex items-center gap-2 rounded-lg border border-emerald-200 bg-emerald-50/70 p-3 text-xs text-emerald-700 dark:border-emerald-900 dark:bg-emerald-950/40 dark:text-emerald-300">
            <CheckCircle2 size={15} /> {successMsg}
          </div>
        )}

        <form onSubmit={handleSave} className="space-y-10">

          {/* Authentication */}
          <div className="grid grid-cols-1 md:grid-cols-12 gap-6 pt-2">
            <div className="md:col-span-4 space-y-1">
              <h2 className="text-sm font-semibold text-foreground">
                {lang === 'km' ? 'គោលការណ៍ផ្ទៀងផ្ទាត់' : 'Authentication Policies'}
              </h2>
              <p className="text-xs text-muted-foreground leading-relaxed">
                {lang === 'km'
                  ? 'គ្រប់គ្រងការទាមទារ 2FA ការចាក់សោគណនីពេលបញ្ចូលខុសច្រើនដង និងអាយុកាលសម័យប្រជុំ។'
                  : 'Enforce multi-factor auth, account lockout rules, and session lifecycle.'}
              </p>
            </div>

            <div className="md:col-span-8 space-y-5">
              {/* 2FA */}
              <div className="flex items-center justify-between rounded-xl border border-border bg-card p-4 shadow-xs">
                <div>
                  <p className="text-xs font-bold text-foreground">
                    {lang === 'km' ? 'ទាមទារការផ្ទៀងផ្ទាត់ ២ ជំហាន (Enforce MFA)' : 'Require Two-Factor Authentication (MFA)'}
                  </p>
                  <p className="text-[11px] text-muted-foreground mt-0.5 leading-relaxed">
                    {lang === 'km'
                      ? 'តម្រូវឱ្យអ្នកប្រើប្រាស់ទាំងអស់នៅក្នុង Realm ត្រូវតែបើក 2FA មុនពេលចូលប្រើប្រាស់។'
                      : 'Enforces all realm users to configure two-step verification.'}
                  </p>
                </div>
                <Switch
                  checked={config.mfaRequired}
                  onCheckedChange={checked => setConfig({ ...config, mfaRequired: Boolean(checked) })}
                />
              </div>

              {/* Lockout & timeout */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="space-y-1.5">
                  <label htmlFor="max-attempts" className="text-xs font-medium text-foreground block">
                    {lang === 'km' ? 'ចំនួនប៉ុនប៉ងចូលមិនជោគជ័យអតិបរមា' : 'Max Failed Login Attempts'}
                  </label>
                  <Input
                    id="max-attempts"
                    type="number"
                    min="1"
                    max="20"
                    value={config.maxFailedLoginAttempts}
                    onChange={e => setConfig({ ...config, maxFailedLoginAttempts: e.target.value })}
                    className="h-9 text-xs"
                    required
                  />
                  <p className="text-[11px] text-muted-foreground">
                    {lang === 'km' ? 'ចាក់សោគណនីស្វ័យប្រវត្តិបន្ទាប់ពីបរាជ័យជាបន្តបន្ទាប់' : 'Locks user account after consecutive incorrect credentials.'}
                  </p>
                </div>

                <div className="space-y-1.5">
                  <label htmlFor="session-timeout" className="text-xs font-medium text-foreground block">
                    {lang === 'km' ? 'រយៈពេលផុតកំណត់ Session (នាទី)' : 'Session Inactivity Expiration (Minutes)'}
                  </label>
                  <Input
                    id="session-timeout"
                    type="number"
                    min="5"
                    max="10080"
                    value={config.sessionTimeoutMinutes}
                    onChange={e => setConfig({ ...config, sessionTimeoutMinutes: e.target.value })}
                    className="h-9 text-xs"
                    required
                  />
                  <p className="text-[11px] text-muted-foreground">
                    {lang === 'km' ? 'កំណត់រយៈពេលអតិបរមាដែល Session នៅសកម្ម' : 'Automatic logout time after user inactivity.'}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div className="border-t border-border/60"></div>

          {/* Password policies */}
          <div className="grid grid-cols-1 md:grid-cols-12 gap-6">
            <div className="md:col-span-4 space-y-1">
              <h2 className="text-sm font-semibold text-foreground">
                {lang === 'km' ? 'គោលការណ៍ពាក្យសម្ងាត់' : 'Password Policies'}
              </h2>
              <p className="text-xs text-muted-foreground leading-relaxed">
                {lang === 'km'
                  ? 'កំណត់ភាពស្មុគស្មាញ ប្រវែងអប្បបរមា និងលក្ខខណ្ឌតួអក្សរសម្រាប់ពាក្យសម្ងាត់។'
                  : 'Enforce password complexity standards across the platform.'}
              </p>
            </div>

            <div className="md:col-span-8 space-y-5">
              <div className="space-y-1.5">
                <label htmlFor="min-length" className="text-xs font-medium text-foreground block">
                  {lang === 'km' ? 'ប្រវែងពាក្យសម្ងាត់អប្បបរមា' : 'Minimum Password Length'}
                </label>
                <div className="w-full sm:w-48">
                  <Input
                    id="min-length"
                    type="number"
                    min="6"
                    max="64"
                    value={config.passwordMinimumLength}
                    onChange={e => setConfig({ ...config, passwordMinimumLength: e.target.value })}
                    className="h-9 text-xs"
                    required
                  />
                </div>
                <p className="text-[11px] text-muted-foreground">
                  {lang === 'km' ? 'យ៉ាងហោចណាស់ 8 តួអក្សរត្រូវបានណែនាំ' : 'Recommended: at least 8 characters.'}
                </p>
              </div>

              {/* Complexity */}
              <div className="rounded-xl border border-border bg-card p-4 space-y-3 shadow-xs">
                <div className="flex items-center space-x-2.5">
                  <Checkbox
                    id="reqUpper"
                    checked={config.passwordRequireUppercase}
                    onCheckedChange={c => setConfig({ ...config, passwordRequireUppercase: Boolean(c) })}
                  />
                  <Label htmlFor="reqUpper" className="text-xs font-normal text-foreground cursor-pointer">
                    {lang === 'km' ? 'តម្រូវឱ្យមានអក្សរធំ (A-Z)' : 'Require at least one uppercase letter (A-Z)'}
                  </Label>
                </div>

                <div className="border-t border-border/40"></div>

                <div className="flex items-center space-x-2.5">
                  <Checkbox
                    id="reqNum"
                    checked={config.passwordRequireNumber}
                    onCheckedChange={c => setConfig({ ...config, passwordRequireNumber: Boolean(c) })}
                  />
                  <Label htmlFor="reqNum" className="text-xs font-normal text-foreground cursor-pointer">
                    {lang === 'km' ? 'តម្រូវឱ្យមានលេខ (0-9)' : 'Require at least one numeric digit (0-9)'}
                  </Label>
                </div>

                <div className="border-t border-border/40"></div>

                <div className="flex items-center space-x-2.5">
                  <Checkbox
                    id="reqSym"
                    checked={config.passwordRequireSymbol}
                    onCheckedChange={c => setConfig({ ...config, passwordRequireSymbol: Boolean(c) })}
                  />
                  <Label htmlFor="reqSym" className="text-xs font-normal text-foreground cursor-pointer">
                    {lang === 'km' ? 'តម្រូវឱ្យមាននិមិត្តសញ្ញាពិសេស (!@#$%^&*)' : 'Require at least one special character symbol (!@#$%^&*)'}
                  </Label>
                </div>
              </div>
            </div>
          </div>

          <div className="border-t border-border/60"></div>

          {/* Token lifetimes */}
          <div className="grid grid-cols-1 md:grid-cols-12 gap-6">
            <div className="md:col-span-4 space-y-1">
              <h2 className="text-sm font-semibold text-foreground">
                {lang === 'km' ? 'អាយុកាល Token លំនាំដើម' : 'Token Lifetimes'}
              </h2>
              <p className="text-xs text-muted-foreground leading-relaxed">
                {lang === 'km'
                  ? 'កំណត់អាយុកាលសុពលភាពលំនាំដើមសម្រាប់ Access Token និង Refresh Token។'
                  : 'Define default authorization token validity across applications.'}
              </p>
            </div>

            <div className="md:col-span-8 space-y-5">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="space-y-1.5">
                  <label htmlFor="token-access-ttl" className="text-xs font-medium text-foreground block">
                    {lang === 'km' ? 'អាយុកាល Access Token (នាទី)' : 'Access Token Lifetime (Minutes)'}
                  </label>
                  <Input
                    id="token-access-ttl"
                    type="number"
                    min="1"
                    max="1440"
                    value={config.accessTokenLifetimeMinutes}
                    onChange={e => setConfig({ ...config, accessTokenLifetimeMinutes: e.target.value })}
                    className="h-9 text-xs"
                    required
                  />
                  <p className="text-[11px] text-muted-foreground">
                    1 to 1440 minutes (24 hours).
                  </p>
                </div>

                <div className="space-y-1.5">
                  <label htmlFor="token-refresh-ttl" className="text-xs font-medium text-foreground block">
                    {lang === 'km' ? 'អាយុកាល Refresh Token (នាទី)' : 'Refresh Token Lifetime (Minutes)'}
                  </label>
                  <Input
                    id="token-refresh-ttl"
                    type="number"
                    min="1"
                    max="129600"
                    value={config.refreshTokenLifetimeMinutes}
                    onChange={e => setConfig({ ...config, refreshTokenLifetimeMinutes: e.target.value })}
                    className="h-9 text-xs"
                    required
                  />
                  <p className="text-[11px] text-muted-foreground">
                    1 to 129600 minutes (90 days).
                  </p>
                </div>
              </div>
            </div>
          </div>

          {/* Actions */}
          <div className="sticky bottom-0 -mx-6 md:-mx-8 px-6 md:px-8 py-4 bg-background/95 backdrop-blur-md border-t border-border flex items-center justify-end gap-3 z-30 shadow-[0_-4px_16px_rgba(0,0,0,0.06)] dark:shadow-[0_-4px_16px_rgba(0,0,0,0.3)]">
            <Button
              type="submit"
              disabled={saving}
              className="px-5 cursor-pointer shadow-xs gap-1.5 h-9 text-xs"
            >
              {saving ? <RefreshCw size={13} className="animate-spin" /> : <Save size={13} />}
              <span>{saving ? (lang === 'km' ? 'កំពុងរក្សាទុក...' : 'Saving...') : (lang === 'km' ? 'រក្សាទុកការកំណត់' : 'Save configuration')}</span>
            </Button>
          </div>

        </form>
      </div>
    </div>
  )
}
