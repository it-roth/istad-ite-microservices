import React, { useState, useEffect } from 'react'
import { cn } from '@/lib/utils'

/**
 * Computes main name letter initials:
 * - "Super Admin" -> "SA"
 * - "Leng Saroth" -> "LS"
 * - "System Administrator" -> "SA"
 * - "superadmin" -> "SA"
 */
export function getMainNameInitials(user, fallbackName) {
  if (!user && !fallbackName) return 'U'

  // 1. If givenName and familyName exist
  const given = user?.givenName || user?.firstName || ''
  const family = user?.familyName || user?.lastName || ''
  if (given && family) {
    return `${given[0]}${family[0]}`.toUpperCase()
  }

  // 2. Look at full display name, name, or fallbackName
  const rawName = [given, user?.middleName, family].filter(Boolean).join(' ') ||
    user?.name ||
    user?.displayName ||
    (typeof fallbackName === 'string' ? fallbackName : '') ||
    user?.username ||
    ''

  const trimmed = rawName.trim()
  if (!trimmed) return 'U'

  const parts = trimmed.split(/\s+/).filter(Boolean)
  if (parts.length >= 2) {
    return `${parts[0][0]}${parts[parts.length - 1][0]}`.toUpperCase()
  }
  if (parts.length === 1) {
    const single = parts[0]
    if (single.toLowerCase() === 'superadmin') return 'SA'
    if (single.length >= 2) return single.slice(0, 2).toUpperCase()
    return single[0].toUpperCase()
  }
  return 'U'
}

/**
 * Checks whether the image source is a valid URL or data URL
 */
export function isValidPictureUrl(url) {
  if (!url || typeof url !== 'string') return false
  const trimmed = url.trim()
  if (!trimmed || trimmed === 'null' || trimmed === 'undefined' || trimmed === 'test' || trimmed === 'test123') return false
  if (trimmed.startsWith('data:image/')) {
    const commaIndex = trimmed.indexOf(',')
    if (commaIndex === -1) return false
    const base64Data = trimmed.slice(commaIndex + 1)
    return base64Data.length > 60 && !base64Data.startsWith('test')
  }
  if (trimmed.startsWith('http://') || trimmed.startsWith('https://') || trimmed.startsWith('/')) {
    return trimmed.length > 5 && !trimmed.endsWith('test123')
  }
  return false
}

/**
 * Universal UserAvatar component:
 * If no image or if the image fails to load, gracefully shows the main name initials.
 * Avoids any broken image icon in browser.
 */
export function UserAvatar({
  user,
  name,
  src,
  className = 'h-14 w-14',
  textClassName = 'font-bold text-lg',
  alt = ''
}) {
  const pictureUrl = src !== undefined ? src : user?.pictureUrl
  const initials = getMainNameInitials(user, name)
  const isValid = isValidPictureUrl(pictureUrl)

  const [isLoaded, setIsLoaded] = useState(false)
  const [hasError, setHasError] = useState(false)

  useEffect(() => {
    setIsLoaded(false)
    setHasError(false)
  }, [pictureUrl])

  const showImg = isValid && !hasError

  return (
    <div
      className={cn(
        'relative rounded-full overflow-hidden bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-slate-800 dark:to-slate-900 flex items-center justify-center shrink-0 select-none text-blue-700 dark:text-blue-300 font-bold',
        className
      )}
    >
      {showImg && (
        <img
          src={pictureUrl}
          alt={alt}
          onLoad={() => setIsLoaded(true)}
          onError={() => setHasError(true)}
          className={cn(
            'h-full w-full object-cover transition-opacity duration-200',
            isLoaded ? 'opacity-100' : 'opacity-0 absolute inset-0 pointer-events-none'
          )}
        />
      )}
      {(!showImg || !isLoaded) && (
        <span className={cn('font-bold tracking-tight text-blue-700 dark:text-blue-300', textClassName)}>
          {initials}
        </span>
      )}
    </div>
  )
}

export default UserAvatar
