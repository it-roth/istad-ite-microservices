import React from 'react'
import { KeyRound } from 'lucide-react'

/**
 * Simple outline Key Icon (Lucide KeyRound)
 * Matches the simple stroke outline style of all other sidebar and UI icons.
 * Head at top-right, shaft at bottom-left, clean linear outline (not solid).
 */
export function KeyIcon({ size = 18, className = '', strokeWidth = 1.8, ...props }) {
  return (
    <KeyRound
      size={size}
      className={className}
      strokeWidth={strokeWidth}
      {...props}
    />
  )
}

export default KeyIcon
