import { useState, useEffect } from 'react'
import { useApp } from '@/context/AppContext'
import { meApi } from '@/lib/api'
import { formatDeviceTitle } from '@/lib/deviceParser'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Monitor, Smartphone, Globe, MapPin, Clock, Trash2, RefreshCw } from 'lucide-react'
import {
  AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent,
  AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger,
} from '@/components/ui/alert-dialog'

export default function ActiveSessionsPage() {
  const { tr } = useApp()
  const [list, setList] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  function loadSessions() {
    setLoading(true)
    setError(null)
    meApi.sessions()
      .then(data => {
        setList(Array.isArray(data) ? data : [])
      })
      .catch(err => {
        setError(err.message || 'Failed to load sessions')
        setList([])
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadSessions()
  }, [])

  async function terminate(id) {
    try {
      await meApi.revokeSession(id)
      setList(l => l.filter(s => s.id !== id))
    } catch (err) {
      alert(err.message)
    }
  }

  async function terminateAll() {
    try {
      await meApi.revokeOthers()
      setList(l => l.filter(s => s.current))
    } catch (err) {
      alert(err.message)
    }
  }

  return (
    <div className="p-6 max-w-3xl">
      <div className="mb-6 flex items-start justify-between">
        <div>
          <h1 className="text-2xl font-bold">{tr('sessions.title')}</h1>
          <p className="text-muted-foreground text-sm mt-1">{tr('sessions.subtitle')}</p>
        </div>
        <div className="flex gap-2">
          {list.some(s => !s.current) && (
            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button variant="destructive" size="sm">
                  {tr('sessions.terminateAll')}
                </Button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogHeader>
                  <AlertDialogTitle>Sign out of other devices?</AlertDialogTitle>
                  <AlertDialogDescription>
                    This will terminate all active sessions except your current device.
                  </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel>Cancel</AlertDialogCancel>
                  <AlertDialogAction onClick={terminateAll}>Sign out all</AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          )}
        </div>
      </div>

      {error && (
        <div className="mb-4 rounded-lg bg-destructive/10 p-3 text-xs text-destructive">
          Error loading sessions: {error}
        </div>
      )}

      {loading ? (
        <div className="py-12 text-center text-sm text-muted-foreground">
          <RefreshCw className="mx-auto mb-2 h-6 w-6 animate-spin text-muted-foreground/50" />
          Querying active sessions from database...
        </div>
      ) : list.length === 0 ? (
        <Card>
          <CardContent className="p-8 text-center text-muted-foreground text-sm">
            No active sessions found in database.
          </CardContent>
        </Card>
      ) : (
        <div className="space-y-3">
          {list.map(session => (
            <Card key={session.id} className={session.current ? 'border-primary/50 bg-primary/5' : ''}>
              <CardContent className="p-4 flex items-center justify-between gap-4">
                <div className="flex items-center gap-3 min-w-0">
                  <div className="h-10 w-10 rounded-lg bg-muted flex items-center justify-center shrink-0">
                    <Monitor size={18} className="text-muted-foreground" />
                  </div>
                  <div className="min-w-0">
                    <div className="flex items-center gap-2">
                      <p className="text-sm font-medium truncate">
                        {formatDeviceTitle(session.browser, session.device)}
                      </p>
                      {session.current && (
                        <Badge variant="default" className="text-xs bg-emerald-600">
                          {tr('sessions.current')}
                        </Badge>
                      )}
                    </div>
                    <div className="flex flex-wrap items-center gap-x-3 gap-y-0.5 text-xs text-muted-foreground mt-0.5">
                      <span className="flex items-center gap-1 font-mono">
                        <Globe size={11} /> {session.ipAddress || '—'}
                      </span>
                      {session.location && (
                        <span className="flex items-center gap-1">
                          <MapPin size={11} /> {session.location}
                        </span>
                      )}
                      <span className="flex items-center gap-1">
                        <Clock size={11} />
                        {session.lastActivity ? new Date(session.lastActivity).toLocaleString() : 'N/A'}
                      </span>
                    </div>
                  </div>
                </div>

                {!session.current && (
                  <Button
                    variant="ghost"
                    size="sm"
                    className="text-destructive hover:bg-destructive/10 shrink-0 gap-1.5"
                    onClick={() => terminate(session.id)}
                  >
                    <Trash2 size={14} />
                    <span className="hidden sm:inline">{tr('sessions.terminate')}</span>
                  </Button>
                )}
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
