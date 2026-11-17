import { useState, useEffect } from 'react'
import { useApp } from '@/context/AppContext'
import { meApi } from '@/lib/api'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Activity, Shield, User, Globe, RefreshCw, AlertCircle } from 'lucide-react'
import KeyIcon from '@/components/icons/KeyIcon'

export default function AccountActivityPage() {
  const { tr } = useApp()
  const [activities, setActivities] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  function loadActivity() {
    setLoading(true)
    setError(null)
    meApi.activity(100)
      .then(data => {
        const filtered = (Array.isArray(data) ? data : []).filter(item => {
          const type = (item.eventType || '').toUpperCase()
          const msg = (item.message || '').toLowerCase()
          if (type.includes('LOGIN') || type.includes('SIGNIN')) return false
          if (msg.includes('signed in') || msg.includes('sign in') || msg.includes('successful login')) return false
          return true
        })
        setActivities(filtered)
      })
      .catch(() => {
        setActivities([])
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadActivity()
  }, [])

  function getIcon(eventType = '') {
    if (eventType.includes('PASSWORD')) return KeyIcon
    if (eventType.includes('ROLE') || eventType.includes('PERMISSION')) return Shield
    if (eventType.includes('PROFILE') || eventType.includes('USER')) return User
    if (eventType.includes('IDENTITY') || eventType.includes('SSO')) return Globe
    return Activity
  }

  return (
    <div className="p-6 max-w-3xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold">{tr('activity.title')}</h1>
        <p className="text-muted-foreground text-sm mt-1">{tr('activity.subtitle')}</p>
      </div>

      {error && (
        <div className="mb-4 rounded-lg bg-destructive/10 p-3 text-xs text-destructive">
          Failed to load activities from database: {error}
        </div>
      )}

      {loading ? (
        <div className="py-12 text-center text-sm text-muted-foreground">
          <RefreshCw className="mx-auto mb-2 h-6 w-6 animate-spin text-muted-foreground/50" />
          Loading security activity records from database...
        </div>
      ) : activities.length === 0 ? (
        <Card>
          <CardContent className="p-8 text-center text-muted-foreground text-sm">
            No activity log entries found in database.
          </CardContent>
        </Card>
      ) : (
        <div className="relative pl-6 space-y-6 before:absolute before:left-2 before:top-2 before:bottom-2 before:w-0.5 before:bg-border">
          {activities.map(act => {
            const Icon = getIcon(act.eventType)
            return (
              <div key={act.id} className="relative">
                <div className="absolute -left-6 top-1 h-4 w-4 rounded-full bg-background border-2 border-blue-600 flex items-center justify-center">
                  <div className="h-1.5 w-1.5 rounded-full bg-blue-600" />
                </div>
                <Card>
                  <CardContent className="p-4">
                    <div className="flex items-start justify-between gap-4">
                      <div className="flex items-start gap-3">
                        <div className="p-2 rounded-lg bg-muted text-muted-foreground shrink-0 mt-0.5">
                          <Icon size={16} />
                        </div>
                        <div>
                          <p className="text-xs font-semibold text-foreground font-mono">
                            {act.eventType}
                          </p>
                          <p className="text-xs text-muted-foreground mt-0.5">
                            {act.message || 'System activity'}
                          </p>
                          <div className="flex items-center gap-3 text-[11px] text-muted-foreground mt-1.5 font-mono">
                            <span>Actor: {act.actor || 'self'}</span>
                            {act.ipAddress && <span>IP: {act.ipAddress}</span>}
                          </div>
                        </div>
                      </div>
                      <span className="text-[11px] text-muted-foreground whitespace-nowrap">
                        {act.occurredAt ? new Date(act.occurredAt).toLocaleString() : 'N/A'}
                      </span>
                    </div>
                  </CardContent>
                </Card>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
