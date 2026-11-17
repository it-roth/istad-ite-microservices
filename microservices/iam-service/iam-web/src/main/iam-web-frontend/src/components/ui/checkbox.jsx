import * as React from 'react'
import { Check } from 'lucide-react'
import { cn } from '@/lib/utils'

const Checkbox = React.forwardRef(({ className, checked, onCheckedChange, disabled, ...props }, ref) => {
  return (
    <button
      type="button"
      role="checkbox"
      aria-checked={checked}
      disabled={disabled}
      ref={ref}
      onClick={() => onCheckedChange?.(!checked)}
      className={cn(
        'peer h-4 w-4 shrink-0 rounded border transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-blue-500 disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer flex items-center justify-center bg-white dark:bg-card',
        checked
          ? 'border-blue-600 dark:border-blue-500'
          : 'border-slate-300 hover:border-slate-400 dark:border-slate-600',
        className
      )}
      {...props}
    >
      {checked && (
        <Check className="h-3.5 w-3.5 stroke-[3] text-blue-600 dark:text-blue-400" />
      )}
    </button>
  )
})
Checkbox.displayName = 'Checkbox'

export { Checkbox }
