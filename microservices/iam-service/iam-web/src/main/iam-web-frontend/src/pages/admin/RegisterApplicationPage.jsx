import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useApp } from '@/context/AppContext'
import { clientsApi } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Switch } from '@/components/ui/switch'
import { Checkbox } from '@/components/ui/checkbox'
import { Server, Globe, Plus, Trash2, X, AlertCircle, Shield, Lock, Clock, RefreshCw } from 'lucide-react'

export default function RegisterApplicationPage() {
  const navigate = useNavigate()
  const { tr, lang } = useApp()
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  // Form State matching screenshot 2 & 3 exactly
  const [form, setForm] = useState({
    clientName: '',
    clientId: '',
    type: 'confidential', // 'confidential' | 'public'
    authCode: true,
    refreshToken: true,
    clientCredentials: false,
    redirectUris: ['https://lms.istad.co/login/oauth2/code/istad'],
    postLogoutRedirectUris: ['https://lms.istad.co/'],
    scopes: ['openid', 'profile', 'email'],
    requireConsent: false,
    accessTokenLifetime: 5,
    refreshTokenLifetime: 60,
  })

  const [scopeInput, setScopeInput] = useState('')

  // Suggested scopes list
  const suggestedScopes = ['phone', 'address', 'offline_access']

  function handleAddScope(scopeName) {
    const clean = scopeName.trim().toLowerCase()
    if (clean && !form.scopes.includes(clean)) {
      setForm({ ...form, scopes: [...form.scopes, clean] })
    }
    setScopeInput('')
  }

  function handleRemoveScope(scopeToRemove) {
    setForm({ ...form, scopes: form.scopes.filter(s => s !== scopeToRemove) })
  }

  function handleScopeKeyDown(e) {
    if (e.key === 'Enter' || e.key === ',' || e.key === ' ') {
      e.preventDefault()
      if (scopeInput.trim()) {
        handleAddScope(scopeInput)
      }
    } else if (e.key === 'Backspace' && !scopeInput && form.scopes.length > 0) {
      handleRemoveScope(form.scopes[form.scopes.length - 1])
    }
  }

  function handleAddRedirectUri() {
    setForm({ ...form, redirectUris: [...form.redirectUris, ''] })
  }

  function handleUpdateRedirectUri(index, val) {
    const list = [...form.redirectUris]
    list[index] = val
    setForm({ ...form, redirectUris: list })
  }

  function handleRemoveRedirectUri(index) {
    setForm({ ...form, redirectUris: form.redirectUris.filter((_, i) => i !== index) })
  }

  function handleAddSignOutUri() {
    setForm({ ...form, postLogoutRedirectUris: [...form.postLogoutRedirectUris, ''] })
  }

  function handleUpdateSignOutUri(index, val) {
    const list = [...form.postLogoutRedirectUris]
    list[index] = val
    setForm({ ...form, postLogoutRedirectUris: list })
  }

  function handleRemoveSignOutUri(index) {
    setForm({ ...form, postLogoutRedirectUris: form.postLogoutRedirectUris.filter((_, i) => i !== index) })
  }

  async function handleSubmit(e) {
    e.preventDefault()
    if (!form.clientId.trim() || !form.clientName.trim()) {
      setError('Please provide both application name and client ID.')
      return
    }

    setSaving(true)
    setError('')

    const grantTypes = []
    if (form.authCode) grantTypes.push('authorization_code')
    if (form.refreshToken) grantTypes.push('refresh_token')
    if (form.clientCredentials) grantTypes.push('client_credentials')

    const authMethods = form.type === 'confidential'
      ? ['client_secret_basic', 'client_secret_post']
      : ['none']

    const clientSecret = form.type === 'confidential'
      ? 'secret-' + Math.random().toString(36).substring(2, 12)
      : ''

    try {
      const payload = {
        clientId: form.clientId.trim(),
        clientName: form.clientName.trim(),
        clientSecret,
        type: form.type,
        grantTypes: grantTypes.length > 0 ? grantTypes : ['authorization_code'],
        authenticationMethods: authMethods,
        scopes: form.scopes.length > 0 ? form.scopes : ['openid', 'profile', 'email'],
        redirectUris: form.redirectUris.filter(u => u.trim().length > 0),
        postLogoutRedirectUris: form.postLogoutRedirectUris.filter(u => u.trim().length > 0),
        requireAuthorizationConsent: form.requireConsent,
        accessTokenTimeToLiveMinutes: parseInt(form.accessTokenLifetime, 10) || 5,
        refreshTokenTimeToLiveMinutes: parseInt(form.refreshTokenLifetime, 10) || 60,
      }

      await clientsApi.create(payload)
      navigate('/admin/applications')
    } catch (err) {
      setError(err.message || 'Failed to register application.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="min-h-full bg-background p-6 md:p-8 pb-0 md:pb-0">
      <div className="mx-auto max-w-5xl space-y-8">

        {/* Header */}
        <div className="pb-4 border-b border-border/60">
          <h1 className="text-2xl sm:text-3xl font-bold tracking-tight text-foreground">
            {tr('apps.registerTitle')}
          </h1>
          <p className="text-xs sm:text-sm text-muted-foreground mt-1">
            {tr('apps.registerSubtitle')}
          </p>
        </div>

        {error && (
          <div className="flex items-center gap-2 rounded-lg border border-red-200 bg-red-50/70 p-3 text-xs text-red-700 dark:border-red-900 dark:bg-red-950/40 dark:text-red-300">
            <AlertCircle size={15} /> {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-10">

          {/* Application */}
          <div className="grid grid-cols-1 md:grid-cols-12 gap-6 pt-2">
            <div className="md:col-span-4 space-y-1">
              <h2 className="text-sm font-semibold text-foreground">{tr('apps.application')}</h2>
              <p className="text-xs text-muted-foreground leading-relaxed">
                {tr('apps.nameDesc')}
              </p>
            </div>

            <div className="md:col-span-8 space-y-5">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="space-y-1.5">
                  <label htmlFor="app-name" className="text-xs font-medium text-foreground block">
                    {tr('apps.nameLabel')}
                  </label>
                  <Input
                    id="app-name"
                    required
                    placeholder="ISTAD LMS"
                    value={form.clientName}
                    onChange={e => setForm({ ...form, clientName: e.target.value })}
                    className="h-9 text-xs"
                  />
                </div>

                <div className="space-y-1.5">
                  <label htmlFor="client-id" className="text-xs font-medium text-foreground block">
                    {tr('apps.clientIdLabel')}
                  </label>
                  <Input
                    id="client-id"
                    required
                    placeholder="istad-lms"
                    value={form.clientId}
                    onChange={e => setForm({ ...form, clientId: e.target.value.toLowerCase().replace(/\s+/g, '-') })}
                    className="h-9 text-xs font-mono"
                  />
                  <p className="text-[11px] text-muted-foreground">
                    {tr('apps.clientIdDesc')}
                  </p>
                </div>
              </div>

              {/* Type Selectors */}
              <div className="space-y-2 pt-2">
                <label className="text-xs font-medium text-foreground block">
                  {tr('apps.type')}
                </label>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {/* Card 1: Confidential */}
                  <div
                    onClick={() => setForm({ ...form, type: 'confidential' })}
                    className={`cursor-pointer rounded-xl border p-4 transition duration-150 flex flex-col justify-between ${form.type === 'confidential'
                      ? 'border-blue-600 bg-blue-50/20 dark:border-blue-500 dark:bg-blue-950/20 ring-1 ring-blue-600'
                      : 'border-border bg-card hover:border-slate-300 dark:hover:border-slate-700'
                      }`}
                  >
                    <div className="flex items-center gap-2.5 mb-2">
                      <div className={`p-1.5 rounded-lg ${form.type === 'confidential' ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground'}`}>
                        <Server size={18} />
                      </div>
                      <span className="text-xs font-bold text-foreground">{tr('apps.confidential')}</span>
                    </div>
                    <p className="text-[11px] text-muted-foreground leading-relaxed">
                      {tr('apps.confidentialDesc')}
                    </p>
                  </div>

                  {/* Card 2: Public */}
                  <div
                    onClick={() => setForm({ ...form, type: 'public' })}
                    className={`cursor-pointer rounded-xl border p-4 transition duration-150 flex flex-col justify-between ${form.type === 'public'
                      ? 'border-blue-600 bg-blue-50/20 dark:border-blue-500 dark:bg-blue-950/20 ring-1 ring-blue-600'
                      : 'border-border bg-card hover:border-slate-300 dark:hover:border-slate-700'
                      }`}
                  >
                    <div className="flex items-center gap-2.5 mb-2">
                      <div className={`p-1.5 rounded-lg ${form.type === 'public' ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground'}`}>
                        <Globe size={18} />
                      </div>
                      <span className="text-xs font-bold text-foreground">{tr('apps.public')}</span>
                    </div>
                    <p className="text-[11px] text-muted-foreground leading-relaxed">
                      {tr('apps.publicDesc')}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="border-t border-border/60"></div>

          {/* Authentication */}
          <div className="grid grid-cols-1 md:grid-cols-12 gap-6">
            <div className="md:col-span-4 space-y-1">
              <h2 className="text-sm font-semibold text-foreground">{tr('apps.signInLabel')}</h2>
              <p className="text-xs text-muted-foreground leading-relaxed">
                {tr('apps.signInDesc')}
              </p>
            </div>

            <div className="md:col-span-8 space-y-6">
              {/* Grant types checkboxes */}
              <div className="space-y-3">
                <label className="text-xs font-medium text-foreground block">
                  {tr('apps.grantTypes')}
                </label>
                <div className="space-y-3 rounded-lg border border-border bg-card p-4">
                  <label className="flex items-start gap-3 cursor-pointer">
                    <Checkbox
                      checked={form.authCode}
                      onCheckedChange={checked => setForm({ ...form, authCode: !!checked })}
                      className="mt-0.5"
                    />
                    <div>
                      <p className="text-xs font-semibold text-foreground">{tr('apps.authCode')}</p>
                      <p className="text-[11px] text-muted-foreground">{tr('apps.authCodeDesc')}</p>
                    </div>
                  </label>

                  <label className="flex items-start gap-3 cursor-pointer">
                    <Checkbox
                      checked={form.refreshToken}
                      onCheckedChange={checked => setForm({ ...form, refreshToken: !!checked })}
                      className="mt-0.5"
                    />
                    <div>
                      <p className="text-xs font-semibold text-foreground">{tr('apps.refreshToken')}</p>
                      <p className="text-[11px] text-muted-foreground">{tr('apps.refreshTokenDesc')}</p>
                    </div>
                  </label>

                  <label className="flex items-start gap-3 cursor-pointer">
                    <Checkbox
                      checked={form.clientCredentials}
                      onCheckedChange={checked => setForm({ ...form, clientCredentials: !!checked })}
                      className="mt-0.5"
                    />
                    <div>
                      <p className="text-xs font-semibold text-foreground">{tr('apps.clientCredentials')}</p>
                      <p className="text-[11px] text-muted-foreground">{tr('apps.clientCredentialsDesc')}</p>
                    </div>
                  </label>
                </div>
              </div>

              {/* Redirect URLs */}
              <div className="space-y-2">
                <div className="flex items-center justify-between">
                  <label className="text-xs font-medium text-foreground block">
                    {tr('apps.redirectUris')}
                  </label>
                </div>
                <div className="space-y-2">
                  {form.redirectUris.map((uri, index) => (
                    <div key={index} className="flex items-center gap-2">
                      <Input
                        type="url"
                        placeholder="https://lms.istad.co/login/oauth2/code/istad"
                        value={uri}
                        onChange={e => handleUpdateRedirectUri(index, e.target.value)}
                        className="h-9 text-xs font-mono"
                      />
                      {form.redirectUris.length > 1 && (
                        <Button
                          type="button"
                          variant="ghost"
                          size="icon"
                          onClick={() => handleRemoveRedirectUri(index)}
                          className="h-9 w-9 text-muted-foreground hover:text-red-600"
                        >
                          <Trash2 size={14} />
                        </Button>
                      )}
                      {index === form.redirectUris.length - 1 && (
                        <Button
                          type="button"
                          variant="outline"
                          size="sm"
                          onClick={handleAddRedirectUri}
                          className="h-9 text-xs gap-1 shrink-0 cursor-pointer"
                        >
                          <Plus size={13} /> Add
                        </Button>
                      )}
                    </div>
                  ))}
                  <p className="text-[11px] text-muted-foreground">
                    {tr('apps.redirectUrisDesc')}
                  </p>
                </div>
              </div>

              {/* Sign-out redirect URLs */}
              <div className="space-y-2">
                <div className="flex items-center justify-between">
                  <label className="text-xs font-medium text-foreground block">
                    {tr('apps.postLogoutUris')}
                  </label>
                </div>
                <div className="space-y-2">
                  {form.postLogoutRedirectUris.map((uri, index) => (
                    <div key={index} className="flex items-center gap-2">
                      <Input
                        type="url"
                        placeholder="https://lms.istad.co/"
                        value={uri}
                        onChange={e => handleUpdateSignOutUri(index, e.target.value)}
                        className="h-9 text-xs font-mono"
                      />
                      {form.postLogoutRedirectUris.length > 1 && (
                        <Button
                          type="button"
                          variant="ghost"
                          size="icon"
                          onClick={() => handleRemoveSignOutUri(index)}
                          className="h-9 w-9 text-muted-foreground hover:text-red-600"
                        >
                          <Trash2 size={14} />
                        </Button>
                      )}
                      {index === form.postLogoutRedirectUris.length - 1 && (
                        <Button
                          type="button"
                          variant="outline"
                          size="sm"
                          onClick={handleAddSignOutUri}
                          className="h-9 text-xs gap-1 shrink-0 cursor-pointer"
                        >
                          <Plus size={13} /> Add
                        </Button>
                      )}
                    </div>
                  ))}
                  <p className="text-[11px] text-muted-foreground">
                    {tr('apps.postLogoutUrisDesc')}
                  </p>
                </div>
              </div>

            </div>
          </div>

          <div className="border-t border-border/60"></div>

          {/* Scopes & consent */}
          <div className="grid grid-cols-1 md:grid-cols-12 gap-6">
            <div className="md:col-span-4 space-y-1">
              <h2 className="text-sm font-semibold text-foreground">{tr('apps.accessLabel')}</h2>
              <p className="text-xs text-muted-foreground leading-relaxed">
                {tr('apps.accessDesc')}
              </p>
            </div>

            <div className="md:col-span-8 space-y-6">
              {/* Scopes Tag Input */}
              <div className="space-y-2">
                <label className="text-xs font-medium text-foreground block">
                  {tr('apps.scopes')}
                </label>

                {/* Tag Container */}
                <div className="flex flex-wrap items-center gap-1.5 p-2 rounded-lg border border-input bg-card min-h-10 text-xs">
                  {form.scopes.map(scope => (
                    <span
                      key={scope}
                      className="inline-flex items-center gap-1 px-2.5 py-1 rounded-md bg-muted text-foreground font-mono text-[11px]"
                    >
                      <span>{scope}</span>
                      <button
                        type="button"
                        onClick={() => handleRemoveScope(scope)}
                        className="hover:text-red-600 cursor-pointer"
                      >
                        <X size={11} />
                      </button>
                    </span>
                  ))}
                  <input
                    type="text"
                    value={scopeInput}
                    onChange={e => setScopeInput(e.target.value)}
                    onKeyDown={handleScopeKeyDown}
                    placeholder={form.scopes.length === 0 ? "Type a scope..." : ""}
                    className="flex-1 min-w-[120px] bg-transparent outline-none text-xs text-foreground placeholder:text-muted-foreground font-mono"
                  />
                </div>

                {/* Suggestions */}
                <div className="flex items-center gap-2 text-xs text-muted-foreground flex-wrap pt-1">
                  <span>Suggestions:</span>
                  {suggestedScopes.map(s => (
                    <button
                      key={s}
                      type="button"
                      onClick={() => handleAddScope(s)}
                      disabled={form.scopes.includes(s)}
                      className={`text-xs font-mono rounded px-1.5 py-0.5 border border-dashed transition cursor-pointer ${form.scopes.includes(s)
                        ? 'opacity-40 cursor-not-allowed border-muted'
                        : 'hover:border-blue-500 hover:text-blue-600 border-border'
                        }`}
                    >
                      + {s}
                    </button>
                  ))}
                </div>

                <p className="text-[11px] text-muted-foreground">
                  {tr('apps.scopesHint')}
                </p>
              </div>

              {/* Ask people for consent Switch */}
              <div className="flex items-center justify-between rounded-lg border border-border bg-card p-4">
                <div>
                  <p className="text-xs font-semibold text-foreground">{tr('apps.askConsent')}</p>
                  <p className="text-[11px] text-muted-foreground mt-0.5">{tr('apps.askConsentDesc')}</p>
                </div>
                <Switch
                  checked={form.requireConsent}
                  onCheckedChange={checked => setForm({ ...form, requireConsent: checked })}
                />
              </div>

            </div>
          </div>

          <div className="border-t border-border/60"></div>

          {/* Token lifetimes */}
          <div className="grid grid-cols-1 md:grid-cols-12 gap-6">
            <div className="md:col-span-4 space-y-1">
              <h2 className="text-sm font-semibold text-foreground">{tr('apps.tokensLabel')}</h2>
              <p className="text-xs text-muted-foreground leading-relaxed">
                {tr('apps.tokensDesc')}
              </p>
            </div>

            <div className="md:col-span-8 space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="space-y-1.5">
                  <label htmlFor="access-ttl" className="text-xs font-medium text-foreground block">
                    {tr('apps.accessTokenLifetime')}
                  </label>
                  <Input
                    id="access-ttl"
                    type="number"
                    min="1"
                    max="1440"
                    value={form.accessTokenLifetime}
                    onChange={e => setForm({ ...form, accessTokenLifetime: e.target.value })}
                    className="h-9 text-xs"
                  />
                  <p className="text-[11px] text-muted-foreground">
                    1 to 1440.
                  </p>
                </div>

                <div className="space-y-1.5">
                  <label htmlFor="refresh-ttl" className="text-xs font-medium text-foreground block">
                    {tr('apps.refreshTokenLifetime')}
                  </label>
                  <Input
                    id="refresh-ttl"
                    type="number"
                    min="1"
                    max="129600"
                    value={form.refreshTokenLifetime}
                    onChange={e => setForm({ ...form, refreshTokenLifetime: e.target.value })}
                    className="h-9 text-xs"
                  />
                  <p className="text-[11px] text-muted-foreground">
                    1 to 129600 (90 days).
                  </p>
                </div>
              </div>
            </div>
          </div>

          {/* Actions */}
          <div className="sticky bottom-0 -mx-6 md:-mx-8 px-6 md:px-8 py-4 bg-background/95 backdrop-blur-md border-t border-border flex items-center justify-end gap-3 z-30 shadow-[0_-4px_16px_rgba(0,0,0,0.06)] dark:shadow-[0_-4px_16px_rgba(0,0,0,0.3)]">
            <Button
              type="button"
              variant="outline"
              onClick={() => navigate('/admin/applications')}
              className="text-xs h-9 px-4 cursor-pointer"
            >
              {tr('common.cancel')}
            </Button>
            <Button
              type="submit"
              disabled={saving}
              className="px-5 cursor-pointer shadow-xs"
            >
              {saving ? '...' : tr('apps.registerBtn')}
            </Button>
          </div>

        </form>
      </div>
    </div>
  )
}
