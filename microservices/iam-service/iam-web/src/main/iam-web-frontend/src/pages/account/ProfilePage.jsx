import { useState, useEffect, useRef } from 'react'
import { Link } from 'react-router-dom'
import { z } from 'zod'
import { useApp } from '@/context/AppContext'
import { meApi } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  CheckCircle2, AlertCircle, Upload, Trash2,
  Check, ChevronDown, Calendar, X, RefreshCw
} from 'lucide-react'
import UserAvatar, { getMainNameInitials } from '@/components/common/UserAvatar'

// Zod validation schema for all profile fields
const profileSchema = z.object({
  username: z.string({ required_error: 'Username is required' })
    .trim()
    .min(3, 'Username must be at least 3 characters')
    .max(50, 'Username cannot exceed 50 characters')
    .regex(/^[a-zA-Z0-9._-]+$/, 'Username can only contain letters, numbers, dots, dashes, and underscores'),
  email: z.string({ required_error: 'Email is required' })
    .trim()
    .min(1, 'Email address is required')
    .email('Invalid email address format')
    .max(255, 'Email cannot exceed 255 characters'),
  givenName: z.string({ required_error: 'First name is required' })
    .trim()
    .min(1, 'First name is required')
    .max(100, 'First name cannot exceed 100 characters'),
  familyName: z.string({ required_error: 'Last name is required' })
    .trim()
    .min(1, 'Last name is required')
    .max(100, 'Last name cannot exceed 100 characters'),
  middleName: z.string().max(100, 'Middle name cannot exceed 100 characters').optional().nullable(),
  nickname: z.string().max(100, 'Nickname cannot exceed 100 characters').optional().nullable(),
  gender: z.string().optional().nullable(),
  birthdate: z.string().optional().nullable().refine((val) => {
    if (!val || !val.trim()) return true
    if (!/^\d{4}-\d{2}-\d{2}$/.test(val.trim())) return false
    const d = new Date(val.trim())
    return !isNaN(d.getTime()) && d <= new Date()
  }, 'Invalid date of birth (cannot be in the future)'),
  phoneNumber: z.string().optional().nullable().refine((val) => {
    if (!val || !val.trim()) return true
    return /^(\+?[0-9\s\-().]{7,20})$/.test(val.trim())
  }, 'Invalid phone number format (e.g. +855963097092)'),
  websiteUrl: z.string().optional().nullable().refine((val) => {
    if (!val || !val.trim() || val.trim() === 'https://' || val.trim() === 'http://') return true
    return /^https?:\/\/[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/i.test(val.trim()) || /^https?:\/\/.+/i.test(val.trim())
  }, 'Website must start with http:// or https:// and include a valid domain'),
  pictureUrl: z.string().optional().nullable(),
  streetAddress: z.string().max(255, 'Street address is too long').optional().nullable(),
  cityKhan: z.string().max(100, 'City is too long').optional().nullable(),
  province: z.string().max(100, 'Province is too long').optional().nullable(),
  postalCode: z.string().max(20, 'Postal code is too long').optional().nullable(),
  countryCode: z.string().optional().nullable().refine((val) => {
    if (!val || !val.trim()) return true
    return val.trim().length <= 2
  }, 'Country code must be 2 characters (e.g. KH)'),
})

export default function ProfilePage() {
  const { tr, setUser, lang } = useApp()
  const [initialLoaded, setInitialLoaded] = useState(false)
  const [originalForm, setOriginalForm] = useState(null)
  const [profileData, setProfileData] = useState(null)
  const fileInputRef = useRef(null)

  // Validation errors state
  const [fieldErrors, setFieldErrors] = useState({})
  const [sectionErrors, setSectionErrors] = useState({})

  // Date of birth formatted state (DD/MM/YYYY)
  const [dobDisplay, setDobDisplay] = useState('')
  const hiddenDateRef = useRef(null)

  const [form, setForm] = useState({
    username: '',
    email: '',
    givenName: '',
    familyName: '',
    middleName: '',
    nickname: '',
    gender: '',
    birthdate: '',
    phoneNumber: '',
    websiteUrl: '',
    pictureUrl: '',
    streetAddress: '',
    cityKhan: '',
    province: '',
    postalCode: '',
    countryCode: 'KH',
  })
  const [status, setStatus] = useState(null)
  const [msg, setMsg] = useState('')
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    let mounted = true
    async function loadProfile() {
      try {
        const data = await meApi.get()
        if (mounted && data) {
          setProfileData(data)
          const loaded = {
            username: data.username || '',
            email: data.email || '',
            givenName: data.givenName || '',
            familyName: data.familyName || '',
            middleName: data.middleName || '',
            nickname: data.nickname || '',
            gender: data.gender || '',
            birthdate: data.birthdate || '',
            phoneNumber: data.phoneNumber || '',
            websiteUrl: data.websiteUrl || '',
            pictureUrl: data.pictureUrl || '',
            streetAddress: data.address?.streetAddress || '',
            cityKhan: data.address?.locality || '',
            province: data.address?.region || '',
            postalCode: data.address?.postalCode || '',
            countryCode: data.address?.country || 'KH',
          }
          setForm(loaded)
          setOriginalForm(loaded)

          if (data.birthdate && /^\d{4}-\d{2}-\d{2}$/.test(data.birthdate)) {
            const [y, m, d] = data.birthdate.split('-')
            setDobDisplay(`${d}/${m}/${y}`)
          } else {
            setDobDisplay('')
          }
        }
      } catch (err) {
        console.error('Failed to load profile:', err)
      } finally {
        if (mounted) setInitialLoaded(true)
      }
    }
    loadProfile()
    return () => { mounted = false }
  }, [])

  useEffect(() => {
    if (status) {
      const timer = setTimeout(() => {
        setStatus(null)
        setMsg('')
      }, 4000)
      return () => clearTimeout(timer)
    }
  }, [status])

  function handleChange(e) {
    const { name, value } = e.target
    setForm(f => ({ ...f, [name]: value }))
    if (fieldErrors[name]) {
      setFieldErrors(prev => {
        const next = { ...prev }
        delete next[name]
        return next
      })
    }
  }

  function handleDobChange(e) {
    const digits = e.target.value.replace(/\D/g, '').slice(0, 8)
    let formatted = ''
    if (digits.length <= 2) {
      formatted = digits
    } else if (digits.length <= 4) {
      formatted = `${digits.slice(0, 2)}/${digits.slice(2)}`
    } else {
      formatted = `${digits.slice(0, 2)}/${digits.slice(2, 4)}/${digits.slice(4, 8)}`
    }
    setDobDisplay(formatted)

    if (fieldErrors.birthdate) {
      setFieldErrors(prev => {
        const next = { ...prev }
        delete next.birthdate
        return next
      })
    }

    if (digits.length === 8) {
      const d = digits.slice(0, 2)
      const m = digits.slice(2, 4)
      const y = digits.slice(4, 8)
      setForm(f => ({ ...f, birthdate: `${y}-${m}-${d}` }))
    } else if (digits.length === 0) {
      setForm(f => ({ ...f, birthdate: '' }))
    }
  }

  function handleNativeDatePick(e) {
    const isoDate = e.target.value
    setForm(f => ({ ...f, birthdate: isoDate }))
    if (fieldErrors.birthdate) {
      setFieldErrors(prev => {
        const next = { ...prev }
        delete next.birthdate
        return next
      })
    }
    if (isoDate && /^\d{4}-\d{2}-\d{2}$/.test(isoDate)) {
      const [y, m, d] = isoDate.split('-')
      setDobDisplay(`${d}/${m}/${y}`)
    } else {
      setDobDisplay('')
    }
  }

  function handleDiscard() {
    if (originalForm) {
      setForm({ ...originalForm })
      if (originalForm.birthdate && /^\d{4}-\d{2}-\d{2}$/.test(originalForm.birthdate)) {
        const [y, m, d] = originalForm.birthdate.split('-')
        setDobDisplay(`${d}/${m}/${y}`)
      } else {
        setDobDisplay('')
      }
    }
    setFieldErrors({})
    setSectionErrors({})
    setStatus(null)
    setMsg('')
  }

  function handleFileChange(e) {
    const file = e.target.files?.[0]
    if (!file) return

    if (file.size > 5 * 1024 * 1024) {
      setStatus('error')
      setMsg('Image size must be less than 5MB')
      setSectionErrors(prev => ({ ...prev, picture: 'Image size must be less than 5MB' }))
      return
    }

    const reader = new FileReader()
    reader.onload = (event) => {
      const img = new Image()
      img.onload = () => {
        const canvas = document.createElement('canvas')
        const maxDim = 256
        let width = img.width
        let height = img.height

        if (width > height) {
          if (width > maxDim) {
            height = Math.round((height * maxDim) / width)
            width = maxDim
          }
        } else {
          if (height > maxDim) {
            width = Math.round((width * maxDim) / height)
            height = maxDim
          }
        }

        canvas.width = width
        canvas.height = height
        const ctx = canvas.getContext('2d')
        ctx.drawImage(img, 0, 0, width, height)

        const compressed = canvas.toDataURL('image/jpeg', 0.85)
        setForm(f => ({ ...f, pictureUrl: compressed }))
        setSectionErrors(prev => {
          const next = { ...prev }
          delete next.picture
          return next
        })
      }
      img.src = event.target.result
    }
    reader.readAsDataURL(file)
  }

  function handleRemovePicture() {
    setForm(f => ({ ...f, pictureUrl: '' }))
    if (fileInputRef.current) {
      fileInputRef.current.value = ''
    }
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setSaving(true)
    setStatus(null)
    setMsg('')
    setFieldErrors({})
    setSectionErrors({})

    // Normalize website: treat "https://" or "http://" without a domain as empty
    const cleanWeb = form.websiteUrl?.trim() || ''
    const normalizedWebsite = (cleanWeb === 'https://' || cleanWeb === 'http://') ? '' : cleanWeb

    // Formatted DOB check (DD/MM/YYYY)
    let resolvedBirthdate = form.birthdate?.trim() || null
    const dobDigits = dobDisplay.replace(/\D/g, '')
    if (dobDigits.length > 0) {
      if (dobDigits.length !== 8) {
        setFieldErrors({ birthdate: 'Please enter a complete date (DD/MM/YYYY) or clear it' })
        setSectionErrors({ personalDetails: 'Please enter a complete date of birth (DD/MM/YYYY)' })
        setStatus('error')
        setMsg('Please enter a complete date of birth (DD/MM/YYYY)')
        setSaving(false)
        document.getElementById('birthdate')?.scrollIntoView({ behavior: 'smooth', block: 'center' })
        document.getElementById('birthdate')?.focus()
        return
      }
      const d = dobDigits.slice(0, 2)
      const m = dobDigits.slice(2, 4)
      const y = dobDigits.slice(4, 8)
      resolvedBirthdate = `${y}-${m}-${d}`
    } else {
      resolvedBirthdate = null
    }

    const formToValidate = {
      ...form,
      websiteUrl: normalizedWebsite,
      birthdate: resolvedBirthdate,
    }

    // 1. Zod client-side schema validation
    const parseResult = profileSchema.safeParse(formToValidate)
    if (!parseResult.success) {
      const fe = {}
      const se = {}
      parseResult.error.errors.forEach(err => {
        const field = err.path[0]
        if (!fe[field]) fe[field] = err.message
        if (['username', 'email'].includes(field)) {
          if (!se.accountInfo) se.accountInfo = err.message
        } else if (['givenName', 'familyName', 'middleName', 'nickname', 'gender', 'birthdate'].includes(field)) {
          if (!se.personalDetails) se.personalDetails = err.message
        } else if (['phoneNumber', 'websiteUrl'].includes(field)) {
          if (!se.contact) se.contact = err.message
        } else if (['streetAddress', 'cityKhan', 'province', 'postalCode', 'countryCode'].includes(field)) {
          if (!se.address) se.address = err.message
        }
      })
      setFieldErrors(fe)
      setSectionErrors(se)
      setStatus('error')
      setMsg(parseResult.error.errors[0]?.message || 'Please correct the highlighted fields')
      setSaving(false)

      // Smooth scroll to the first invalid field
      const firstErrorField = parseResult.error.errors[0]?.path[0]
      if (firstErrorField) {
        const targetEl = document.getElementById(firstErrorField)
        targetEl?.scrollIntoView({ behavior: 'smooth', block: 'center' })
        targetEl?.focus?.()
      }
      return
    }

    // 2. Perform backend update across all fields
    try {
      const payload = {
        username: form.username?.trim(),
        email: form.email?.trim(),
        givenName: form.givenName?.trim(),
        familyName: form.familyName?.trim(),
        middleName: form.middleName?.trim() || '',
        nickname: form.nickname?.trim() || '',
        gender: form.gender?.trim() || '',
        birthdate: resolvedBirthdate,
        phoneNumber: form.phoneNumber?.trim() || '',
        websiteUrl: normalizedWebsite,
        pictureUrl: form.pictureUrl?.trim() || '',
        address: {
          streetAddress: form.streetAddress?.trim() || '',
          locality: form.cityKhan?.trim() || '',
          region: form.province?.trim() || '',
          postalCode: form.postalCode?.trim() || '',
          country: form.countryCode?.trim() ? form.countryCode.trim().substring(0, 2).toUpperCase() : '',
        },
      }

      const updated = await meApi.update(payload)
      if (setUser) {
        setUser(u => ({ ...u, ...updated, pictureUrl: form.pictureUrl }))
      }
      setForm(f => ({ ...f, websiteUrl: normalizedWebsite, birthdate: resolvedBirthdate || '' }))
      setOriginalForm({ ...form, websiteUrl: normalizedWebsite, birthdate: resolvedBirthdate || '' })
      setStatus('success')
      setMsg(tr('profile.saved'))
    } catch (err) {
      setStatus('error')
      const errorMessage = err.message || 'Failed to update profile'
      setMsg(errorMessage)

      const fe = {}
      const se = {}
      const lower = errorMessage.toLowerCase()

      if (lower.includes('username')) {
        fe.username = errorMessage
        se.accountInfo = errorMessage
        document.getElementById('username')?.scrollIntoView({ behavior: 'smooth', block: 'center' })
      } else if (lower.includes('email')) {
        fe.email = errorMessage
        se.accountInfo = errorMessage
        document.getElementById('email')?.scrollIntoView({ behavior: 'smooth', block: 'center' })
      } else if (lower.includes('phone')) {
        fe.phoneNumber = errorMessage
        se.contact = errorMessage
        document.getElementById('phoneNumber')?.scrollIntoView({ behavior: 'smooth', block: 'center' })
      } else if (lower.includes('name')) {
        fe.givenName = errorMessage
        se.personalDetails = errorMessage
        document.getElementById('givenName')?.scrollIntoView({ behavior: 'smooth', block: 'center' })
      } else if (lower.includes('forbidden') || lower.includes('denied') || lower.includes('session')) {
        se.accountInfo = 'Permission denied or session expired. Please refresh the page.'
      }

      setFieldErrors(prev => ({ ...prev, ...fe }))
      setSectionErrors(prev => ({ ...prev, ...se }))
    } finally {
      setSaving(false)
    }
  }

  const currentUsername = form.username || profileData?.username || ''
  const currentEmail = form.email || profileData?.email || ''
  const fullName = [form.givenName, form.middleName, form.familyName].filter(Boolean).join(' ') || profileData?.name || currentUsername
  const emailVerified = Boolean(profileData?.emailVerified)
  const roleName = profileData?.roles?.[0]?.name?.replace(/^ROLE_/, '') || ''

  const [verifyingEmail, setVerifyingEmail] = useState(false)

  async function handleVerifyEmail() {
    setVerifyingEmail(true)
    try {
      await meApi.verifyEmail()
      setProfileData(prev => ({ ...prev, emailVerified: true }))
      if (setUser) {
        setUser(prev => ({ ...prev, emailVerified: true }))
      }
      localStorage.setItem('email_verified_' + (currentUsername || 'user'), 'true')
      setStatus('success')
      setMsg(lang === 'km' ? 'អ៊ីមែលត្រូវបានផ្ទៀងផ្ទាត់ដោយជោគជ័យ' : 'Email verified successfully')
    } catch (err) {
      setStatus('error')
      setMsg(err.message || (lang === 'km' ? 'ការផ្ទៀងផ្ទាត់អ៊ីមែលបរាជ័យ' : 'Failed to verify email'))
    } finally {
      setVerifyingEmail(false)
    }
  }

  if (!initialLoaded) {
    return (
      <div className="flex min-h-[400px] items-center justify-center p-8 bg-background">
        <div className="flex flex-col items-center gap-2.5">
          <div className="h-7 w-7 animate-spin rounded-full border-2 border-blue-600 border-t-transparent" />
          <p className="text-xs text-muted-foreground">{tr('profile.loading')}</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-full bg-background p-4 md:p-8">
      <div className="mx-auto max-w-4xl space-y-8">
        {/* Breadcrumb & Header */}
        <div>
          <div className="flex items-center gap-2 text-xs text-muted-foreground mb-1">
            <Link to="/account" className="hover:text-foreground transition-colors">{tr('nav.myAccount')}</Link>
            <span>/</span>
            <span>{tr('profile.title')}</span>
          </div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground">{tr('profile.title')}</h1>
          <p className="mt-1 text-xs text-muted-foreground">
            {tr('profile.headerSubtitle')}
          </p>
        </div>

        {/* Top-Right Floating Notification Toast (Positioned in the user's drawn box) */}
        {status && (
          <div className="fixed top-6 right-6 z-50 max-w-sm w-full pointer-events-auto animate-in fade-in slide-in-from-top-3 duration-200">
            <div className={`rounded-xl border p-4 shadow-xl backdrop-blur-md flex items-start gap-3 ${status === 'success'
              ? 'bg-white/95 dark:bg-slate-900/95 border-emerald-200 dark:border-emerald-800 text-slate-900 dark:text-slate-100'
              : 'bg-white/95 dark:bg-slate-900/95 border-rose-200 dark:border-rose-800 text-slate-900 dark:text-slate-100'
              }`}>
              <div className={`p-1.5 rounded-full shrink-0 ${status === 'success'
                ? 'bg-emerald-100 text-emerald-600 dark:bg-emerald-950/60 dark:text-emerald-400'
                : 'bg-rose-100 text-rose-600 dark:bg-rose-950/60 dark:text-rose-400'
                }`}>
                {status === 'success' ? <CheckCircle2 size={16} /> : <AlertCircle size={16} />}
              </div>
              <div className="flex-1 min-w-0 pr-2">
                <p className="text-xs font-semibold text-foreground">
                  {status === 'success' ? tr('common.success') : tr('common.error')}
                </p>
                <p className="text-xs text-muted-foreground mt-0.5 break-words">
                  {msg}
                </p>
              </div>
              <button
                type="button"
                onClick={() => { setStatus(null); setMsg('') }}
                className="text-muted-foreground hover:text-foreground p-1 rounded-md transition-colors"
              >
                <X size={14} />
              </button>
            </div>
          </div>
        )}

        {/* User Identity Summary Banner (100% Dynamic from API) */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 py-1">
          <div className="flex items-center gap-4">
            {/* <UserAvatar
              user={{ ...profileData, ...form }}
              src={form.pictureUrl}
              className="h-14 w-14 text-lg shrink-0"
              textClassName="font-bold"
            /> */}
            <div>
              <div className="flex items-center gap-2">
                <span className="text-base font-bold text-foreground">
                  {form.givenName || form.familyName ? `${form.givenName} ${form.familyName}`.trim() : currentUsername}
                </span>
                {roleName && (
                  <Badge variant="secondary" className="text-[10px] uppercase font-mono">
                    {roleName}
                  </Badge>
                )}
              </div>
              <p className="text-xs text-muted-foreground mt-0.5 flex items-center gap-1.5">
                <span>@{currentUsername}</span>
                {currentEmail && (
                  <>
                    <span>&bull;</span>
                    <span>{currentEmail}</span>
                  </>
                )}
                {emailVerified && (
                  <span className="inline-flex items-center gap-0.5 text-[10px] text-emerald-600 dark:text-emerald-400 font-medium ml-1">
                    <CheckCircle2 size={11} /> {tr('profile.verified')}
                  </span>
                )}
              </p>
            </div>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-8">
          {/* Section 1: Profile picture management */}
          <div className="grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
            <div className="md:col-span-4">
              <h2 className="text-sm font-semibold text-foreground">
                {tr('profile.picture')}
              </h2>
              <p className="mt-1 text-xs text-muted-foreground">
                {tr('profile.pictureDesc')}
              </p>
              {sectionErrors.picture && (
                <div className="mt-3 p-3 rounded-lg border border-rose-200 dark:border-rose-900/50 bg-rose-50 dark:bg-rose-950/30 text-rose-700 dark:text-rose-400 text-xs flex items-start gap-2 shadow-xs">
                  <AlertCircle size={15} className="shrink-0 mt-0.5 text-rose-600 dark:text-rose-400" />
                  <span className="font-medium">{sectionErrors.picture}</span>
                </div>
              )}
            </div>

            <div className="md:col-span-8 space-y-4">
              <div className="flex flex-col sm:flex-row sm:items-center gap-5">
                {/* Avatar Preview */}
                <div className="relative shrink-0">
                  <UserAvatar
                    user={{ ...profileData, ...form }}
                    src={form.pictureUrl}
                    className="h-20 w-20 border-2 border-blue-200 dark:border-blue-900 bg-slate-100 dark:bg-slate-800 shadow-xs"
                    textClassName="text-2xl font-bold text-slate-700 dark:text-slate-200"
                  />
                </div>

                {/* Actions: Upload file or remove */}
                <div className="space-y-2 flex-1">
                  <div className="flex flex-wrap items-center gap-2">
                    <Label
                      htmlFor="avatar-file-upload"
                      className="cursor-pointer inline-flex items-center gap-1.5 px-3 py-1.5 rounded-md text-xs font-medium border border-border bg-background hover:bg-muted/50 transition-colors shadow-xs"
                    >
                      <Upload size={13} className="text-primary" />
                      <span>{tr('profile.uploadPhoto')}</span>
                    </Label>
                    <input
                      ref={fileInputRef}
                      id="avatar-file-upload"
                      type="file"
                      accept="image/*"
                      className="hidden"
                      onChange={handleFileChange}
                    />

                    {form.pictureUrl && (
                      <Button
                        type="button"
                        variant="ghost"
                        size="sm"
                        onClick={handleRemovePicture}
                        className="text-xs h-8 text-rose-600 hover:text-rose-700 hover:bg-rose-50 dark:hover:bg-rose-950/30 gap-1"
                      >
                        <Trash2 size={13} />
                        <span>{tr('profile.removePhoto')}</span>
                      </Button>
                    )}
                  </div>
                  <p className="text-[11px] text-muted-foreground">
                    {tr('profile.photoHint')}
                  </p>
                </div>
              </div>

              {/* Direct Image URL input */}
              <div>
                <Label htmlFor="pictureUrl" className="text-xs font-medium text-foreground flex items-center h-5">
                  {tr('profile.pasteUrl')}
                </Label>
                <Input
                  id="pictureUrl"
                  name="pictureUrl"
                  value={form.pictureUrl}
                  onChange={handleChange}
                  placeholder="https://example.com/photo.jpg"
                  className="mt-1 text-xs h-9"
                />
                {fieldErrors.pictureUrl && (
                  <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.pictureUrl}</p>
                )}
              </div>
            </div>
          </div>

          {/* Section 2: Account Information (Username & Email) */}
          <div className="border-t border-border pt-8 grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
            <div className="md:col-span-4">
              <h2 className="text-sm font-semibold text-foreground">
                {tr('profile.accountInfo')}
              </h2>
              <p className="mt-1 text-xs text-muted-foreground">
                {tr('profile.accountInfoDesc')}
              </p>
              {sectionErrors.accountInfo && (
                <div className="mt-3 p-3 rounded-lg border border-rose-200 dark:border-rose-900/50 bg-rose-50 dark:bg-rose-950/30 text-rose-700 dark:text-rose-400 text-xs flex items-start gap-2 shadow-xs">
                  <AlertCircle size={15} className="shrink-0 mt-0.5 text-rose-600 dark:text-rose-400" />
                  <span className="font-medium">{sectionErrors.accountInfo}</span>
                </div>
              )}
            </div>

            <div className="md:col-span-8 space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="username" className="text-xs font-medium text-foreground flex items-center h-5">
                    {tr('profile.username')}
                  </Label>
                  <Input
                    id="username"
                    name="username"
                    value={form.username}
                    onChange={handleChange}
                    autoComplete="off"
                    className={`mt-1 text-xs h-9 ${fieldErrors.username ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                    required
                  />
                  {fieldErrors.username ? (
                    <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.username}</p>
                  ) : (
                    <p className="text-[10px] text-muted-foreground mt-1 leading-normal">{tr('profile.usernameDesc')}</p>
                  )}
                </div>

                <div>
                  <Label htmlFor="email" className="text-xs font-medium text-foreground flex items-center justify-between h-5">
                    <span>{tr('profile.emailAddress')}</span>
                    {emailVerified ? (
                      <span className="text-[10px] text-emerald-600 dark:text-emerald-400 font-medium flex items-center gap-0.5">
                        <CheckCircle2 size={11} /> {tr('profile.verified')}
                      </span>
                    ) : (
                      <button
                        type="button"
                        onClick={handleVerifyEmail}
                        disabled={verifyingEmail}
                        className="text-[11px] text-blue-600 hover:text-blue-700 font-medium cursor-pointer flex items-center gap-1 transition-colors"
                      >
                        {verifyingEmail && <RefreshCw size={11} className="animate-spin" />}
                        {lang === 'km' ? 'ផ្ទៀងផ្ទាត់ឥឡូវនេះ' : 'Verify now'}
                      </button>
                    )}
                  </Label>
                  <Input
                    id="email"
                    name="email"
                    type="email"
                    value={form.email}
                    onChange={handleChange}
                    className={`mt-1 text-xs h-9 ${fieldErrors.email ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                    required
                  />
                  {fieldErrors.email ? (
                    <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.email}</p>
                  ) : (
                    <p className="text-[10px] text-muted-foreground mt-1 leading-normal">{tr('profile.emailDesc')}</p>
                  )}
                </div>
              </div>
            </div>
          </div>

          {/* Section 3: Personal details */}
          <div className="border-t border-border pt-8 grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
            <div className="md:col-span-4">
              <h2 className="text-sm font-semibold text-foreground">{tr('profile.personalDetails')}</h2>
              <p className="mt-1 text-xs text-muted-foreground">
                {tr('profile.personalDetailsDesc')}
              </p>
              {sectionErrors.personalDetails && (
                <div className="mt-3 p-3 rounded-lg border border-rose-200 dark:border-rose-900/50 bg-rose-50 dark:bg-rose-950/30 text-rose-700 dark:text-rose-400 text-xs flex items-start gap-2 shadow-xs">
                  <AlertCircle size={15} className="shrink-0 mt-0.5 text-rose-600 dark:text-rose-400" />
                  <span className="font-medium">{sectionErrors.personalDetails}</span>
                </div>
              )}
            </div>

            <div className="md:col-span-8 space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="givenName" className="text-xs font-medium text-foreground flex items-center h-5">{tr('profile.firstName')}</Label>
                  <Input
                    id="givenName"
                    name="givenName"
                    value={form.givenName}
                    onChange={handleChange}
                    className={`mt-1 text-xs h-9 ${fieldErrors.givenName ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                    required
                  />
                  {fieldErrors.givenName && (
                    <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.givenName}</p>
                  )}
                </div>

                <div>
                  <Label htmlFor="familyName" className="text-xs font-medium text-foreground flex items-center h-5">{tr('profile.lastName')}</Label>
                  <Input
                    id="familyName"
                    name="familyName"
                    value={form.familyName}
                    onChange={handleChange}
                    className={`mt-1 text-xs h-9 ${fieldErrors.familyName ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                    required
                  />
                  {fieldErrors.familyName && (
                    <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.familyName}</p>
                  )}
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="middleName" className="text-xs font-medium text-foreground flex items-center h-5">{tr('profile.middleName')}</Label>
                  <Input
                    id="middleName"
                    name="middleName"
                    value={form.middleName}
                    onChange={handleChange}
                    className={`mt-1 text-xs h-9 ${fieldErrors.middleName ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                  />
                  {fieldErrors.middleName && (
                    <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.middleName}</p>
                  )}
                </div>

                <div>
                  <Label htmlFor="nickname" className="text-xs font-medium text-foreground flex items-center h-5">{tr('profile.nickname')}</Label>
                  <Input
                    id="nickname"
                    name="nickname"
                    value={form.nickname}
                    onChange={handleChange}
                    className={`mt-1 text-xs h-9 ${fieldErrors.nickname ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                  />
                  {fieldErrors.nickname && (
                    <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.nickname}</p>
                  )}
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="gender-trigger" className="text-xs font-medium text-foreground flex items-center h-5">{tr('profile.gender')}</Label>
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <button
                        type="button"
                        id="gender-trigger"
                        className={`mt-1 flex h-9 w-full items-center justify-between rounded-md border ${fieldErrors.gender ? 'border-rose-500 focus-visible:ring-rose-500' : 'border-input'
                          } bg-background px-3 py-2 text-xs text-foreground ring-offset-background hover:bg-muted/30 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 text-left transition-colors`}
                      >
                        <span className={form.gender ? 'text-foreground font-normal' : 'text-muted-foreground'}>
                          {form.gender === 'MALE'
                            ? tr('profile.male')
                            : form.gender === 'FEMALE'
                              ? tr('profile.female')
                              : form.gender === 'OTHER'
                                ? tr('profile.other')
                                : tr('profile.genderChoose')}
                        </span>
                        <ChevronDown size={14} className="text-muted-foreground shrink-0 opacity-70" />
                      </button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="start" className="w-[--radix-dropdown-menu-trigger-width] min-w-[180px] p-1 bg-popover text-popover-foreground border border-border rounded-md shadow-md">
                      <DropdownMenuItem
                        onClick={() => {
                          setForm(f => ({ ...f, gender: 'MALE' }))
                          setFieldErrors(prev => { const n = { ...prev }; delete n.gender; return n })
                        }}
                        className="text-xs flex items-center justify-between cursor-pointer py-1.5 px-2.5 rounded-sm hover:bg-accent focus:bg-accent"
                      >
                        <span>{tr('profile.male')}</span>
                        {form.gender === 'MALE' && <Check size={14} className="text-primary font-bold" />}
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        onClick={() => {
                          setForm(f => ({ ...f, gender: 'FEMALE' }))
                          setFieldErrors(prev => { const n = { ...prev }; delete n.gender; return n })
                        }}
                        className="text-xs flex items-center justify-between cursor-pointer py-1.5 px-2.5 rounded-sm hover:bg-accent focus:bg-accent"
                      >
                        <span>{tr('profile.female')}</span>
                        {form.gender === 'FEMALE' && <Check size={14} className="text-primary font-bold" />}
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        onClick={() => {
                          setForm(f => ({ ...f, gender: 'OTHER' }))
                          setFieldErrors(prev => { const n = { ...prev }; delete n.gender; return n })
                        }}
                        className="text-xs flex items-center justify-between cursor-pointer py-1.5 px-2.5 rounded-sm hover:bg-accent focus:bg-accent"
                      >
                        <span>{tr('profile.other')}</span>
                        {form.gender === 'OTHER' && <Check size={14} className="text-primary font-bold" />}
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                  {fieldErrors.gender && (
                    <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.gender}</p>
                  )}
                </div>

                <div>
                  <Label htmlFor="birthdate" className="text-xs font-medium text-foreground flex items-center h-5">
                    {tr('profile.dob')}
                  </Label>
                  <div className="relative mt-1">
                    <Input
                      id="birthdate"
                      name="birthdate"
                      type="text"
                      inputMode="numeric"
                      placeholder="DD/MM/YYYY"
                      value={dobDisplay}
                      onChange={handleDobChange}
                      className={`text-xs h-9 pr-9 ${fieldErrors.birthdate ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                    />
                    <button
                      type="button"
                      title="Open calendar"
                      onClick={() => {
                        try {
                          hiddenDateRef.current?.showPicker?.()
                        } catch {
                          hiddenDateRef.current?.focus()
                        }
                      }}
                      className="absolute right-2.5 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors p-0.5 rounded cursor-pointer"
                      tabIndex={-1}
                      aria-label="Pick date"
                    >
                      <Calendar size={15} />
                    </button>
                    <input
                      ref={hiddenDateRef}
                      type="date"
                      value={form.birthdate || ''}
                      onChange={handleNativeDatePick}
                      className="sr-only absolute"
                      tabIndex={-1}
                    />
                  </div>
                  {fieldErrors.birthdate && (
                    <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.birthdate}</p>
                  )}
                </div>
              </div>
            </div>
          </div>

          {/* Section 4: Contact */}
          <div className="border-t border-border pt-8 grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
            <div className="md:col-span-4">
              <h2 className="text-sm font-semibold text-foreground">{tr('profile.contact')}</h2>
              <p className="mt-1 text-xs text-muted-foreground">
                {tr('profile.contactDesc')}
              </p>
              {sectionErrors.contact && (
                <div className="mt-3 p-3 rounded-lg border border-rose-200 dark:border-rose-900/50 bg-rose-50 dark:bg-rose-950/30 text-rose-700 dark:text-rose-400 text-xs flex items-start gap-2 shadow-xs">
                  <AlertCircle size={15} className="shrink-0 mt-0.5 text-rose-600 dark:text-rose-400" />
                  <span className="font-medium">{sectionErrors.contact}</span>
                </div>
              )}
            </div>

            <div className="md:col-span-8 space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="phoneNumber" className="text-xs font-medium text-foreground flex items-center h-5">{tr('profile.phone')}</Label>
                  <Input
                    id="phoneNumber"
                    name="phoneNumber"
                    type="tel"
                    value={form.phoneNumber}
                    onChange={handleChange}
                    placeholder="+855 12 345 678"
                    className={`mt-1 text-xs h-9 ${fieldErrors.phoneNumber ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                  />
                  {fieldErrors.phoneNumber && (
                    <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.phoneNumber}</p>
                  )}
                </div>

                <div>
                  <Label htmlFor="websiteUrl" className="text-xs font-medium text-foreground flex items-center h-5">{tr('profile.website')}</Label>
                  <Input
                    id="websiteUrl"
                    name="websiteUrl"
                    type="url"
                    value={form.websiteUrl}
                    onChange={handleChange}
                    placeholder="https://"
                    className={`mt-1 text-xs h-9 ${fieldErrors.websiteUrl ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                  />
                  {fieldErrors.websiteUrl && (
                    <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.websiteUrl}</p>
                  )}
                </div>
              </div>
            </div>
          </div>

          {/* Section 5: Address */}
          <div className="border-t border-border pt-8 grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
            <div className="md:col-span-4">
              <h2 className="text-sm font-semibold text-foreground">{tr('profile.address')}</h2>
              <p className="mt-1 text-xs text-muted-foreground">
                {tr('profile.addressDesc')}
              </p>
              {sectionErrors.address && (
                <div className="mt-3 p-3 rounded-lg border border-rose-200 dark:border-rose-900/50 bg-rose-50 dark:bg-rose-950/30 text-rose-700 dark:text-rose-400 text-xs flex items-start gap-2 shadow-xs">
                  <AlertCircle size={15} className="shrink-0 mt-0.5 text-rose-600 dark:text-rose-400" />
                  <span className="font-medium">{sectionErrors.address}</span>
                </div>
              )}
            </div>

            <div className="md:col-span-8 space-y-4">
              <div>
                <Label htmlFor="streetAddress" className="text-xs font-medium text-foreground flex items-center h-5">{tr('profile.streetAddress')}</Label>
                <Input
                  id="streetAddress"
                  name="streetAddress"
                  value={form.streetAddress}
                  onChange={handleChange}
                  className={`mt-1 text-xs h-9 ${fieldErrors.streetAddress ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                />
                {fieldErrors.streetAddress && (
                  <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.streetAddress}</p>
                )}
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="cityKhan" className="text-xs font-medium text-foreground flex items-center h-5">{tr('profile.cityKhan')}</Label>
                  <Input
                    id="cityKhan"
                    name="cityKhan"
                    value={form.cityKhan}
                    onChange={handleChange}
                    className={`mt-1 text-xs h-9 ${fieldErrors.cityKhan ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                  />
                  {fieldErrors.cityKhan && (
                    <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.cityKhan}</p>
                  )}
                </div>

                <div>
                  <Label htmlFor="province" className="text-xs font-medium text-foreground flex items-center h-5">{tr('profile.province')}</Label>
                  <Input
                    id="province"
                    name="province"
                    value={form.province}
                    onChange={handleChange}
                    className={`mt-1 text-xs h-9 ${fieldErrors.province ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                  />
                  {fieldErrors.province && (
                    <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.province}</p>
                  )}
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="postalCode" className="text-xs font-medium text-foreground flex items-center h-5">{tr('profile.postalCode')}</Label>
                  <Input
                    id="postalCode"
                    name="postalCode"
                    value={form.postalCode}
                    onChange={handleChange}
                    className={`mt-1 text-xs h-9 ${fieldErrors.postalCode ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                  />
                  {fieldErrors.postalCode && (
                    <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.postalCode}</p>
                  )}
                </div>

                <div>
                  <Label htmlFor="countryCode" className="text-xs font-medium text-foreground flex items-center h-5">{tr('profile.countryCode')}</Label>
                  <Input
                    id="countryCode"
                    name="countryCode"
                    value={form.countryCode}
                    onChange={handleChange}
                    maxLength={2}
                    className={`mt-1 text-xs h-9 ${fieldErrors.countryCode ? 'border-rose-500 focus-visible:ring-rose-500' : ''}`}
                  />
                  {fieldErrors.countryCode && (
                    <p className="text-[11px] text-rose-500 font-medium mt-1">{fieldErrors.countryCode}</p>
                  )}
                </div>
              </div>
            </div>
          </div>

          {/* Sticky Bottom Action Bar */}
          <div className="sticky bottom-0 z-30 -mx-4 md:-mx-8 -mb-4 md:-mb-8 px-4 md:px-8 py-3.5 bg-background/90 dark:bg-background/90 backdrop-blur-md border-t border-border shadow-[0_-4px_12px_rgba(0,0,0,0.04)] dark:shadow-[0_-4px_12px_rgba(0,0,0,0.25)]">
            <div className="mx-auto max-w-4xl w-full flex items-center justify-end gap-3">
              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={handleDiscard}
                disabled={saving}
                className="text-xs h-9 px-4 cursor-pointer"
              >
                {tr('profile.discard')}
              </Button>
              <Button
                type="submit"
                size="sm"
                disabled={saving}
                className="px-5 cursor-pointer"
              >
                {saving ? tr('common.loading') : tr('common.save')}
              </Button>
            </div>
          </div>
        </form>
      </div>
    </div>
  )
}
