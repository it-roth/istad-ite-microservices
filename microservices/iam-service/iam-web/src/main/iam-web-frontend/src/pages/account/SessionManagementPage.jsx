import { useState, useEffect } from 'react'
import { useApp } from '@/context/AppContext'
import { meApi } from '@/lib/api'
import { formatDeviceTitle } from '@/lib/deviceParser'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Monitor, Smartphone, Laptop, LogOut, RefreshCw, ShieldOff } from 'lucide-react'
import {
  AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent,
  AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger,
} from '@/components/ui/alert-dialog'

function DeviceIcon({ device = '' }) {
  const d = String(device).toLowerCase()
  if (d.includes('iphone') || d.includes('android') || d.includes('phone') || d.includes('mobile')) return <Smartphone size={18} />
  if (d.includes('macbook') || d.includes('laptop')) return <Laptop size={18} />
  return <Monitor size={18} />
}

export default function SessionManagementPage() {
  const { tr } = useApp()
  const [sessions, setSessions] = useState([])
  const [loading, setLoading] = useState(true)

  function loadSessions() {
    setLoading(true)
    meApi.sessions()
      .then(data => {
        setSessions(Array.isArray(data) ? data : [])
      })
      .catch(() => setSessions([]))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadSessions()
  }, [])

  async function terminate(id) {
    try {
      await meApi.revokeSession(id)
      setSessions(s => s.filter(x => x.id !== id))
    } catch (err) {
      alert(err.message)
    }
  }

  async function terminateOthers() {
    try {
      await meApi.revokeOthers()
      setSessions(s => s.filter(x => x.current))
    } catch (err) {
      alert(err.message)
    }
  }

  async function terminateAll() {
    try {
      await meApi.revokeOthers()
      window.location.href = '/logout'
    } catch (err) {
      window.location.href = '/logout'
    }
  }

  const otherSessions = sessions.filter(s => !s.current)

  return (
    <div className="p-6 max-w-2xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold">{tr('nav.sessionMgmt')}</h1>
        <p className="text-muted-foreground text-sm mt-1">Control where you're signed in and revoke access from unknown devices.</p>
      </div>

      {loading ? (
        <div className="py-12 text-center text-sm text-muted-foreground">
          <RefreshCw className="mx-auto mb-2 h-6 w-6 animate-spin text-muted-foreground/50" />
          Loading active sessions...
        </div>
      ) : sessions.length === 0 ? (
        <Card className="mb-4">
          <CardContent className="p-8 text-center text-muted-foreground text-sm">
            No active sessions found.
          </CardContent>
        </Card>
      ) : (
        <>
          {/* Current session */}
          {sessions.filter(s => s.current).map(sess => {
            const deviceName = formatDeviceTitle(sess.browser, sess.device)
            return (
              <Card key={sess.id} className="mb-4 border-primary/40">
                <CardHeader className="pb-3">
                  <CardTitle className="text-sm font-semibold text-primary">Current Session</CardTitle>
                </CardHeader>
                <CardContent className="flex items-center gap-4 pt-0">
                  <div className="p-2 rounded-lg bg-primary/10 text-primary"><DeviceIcon device={deviceName} /></div>
                  <div className="flex-1">
                    <p className="text-sm font-medium">{deviceName}</p>
                    <p className="text-xs text-muted-foreground">{sess.location || sess.ipAddress || 'Current IP'} · {sess.lastActivity ? new Date(sess.lastActivity).toLocaleTimeString() : 'Active now'}</p>
                  </div>
                  <Badge variant="default" className="bg-emerald-600 text-white">Active</Badge>
                </CardContent>
              </Card>
            )
          })}

          {/* Other sessions */}
          {otherSessions.length > 0 && (
            <Card className="mb-4">
              <CardHeader className="pb-3">
                <CardTitle className="text-sm font-semibold flex items-center justify-between">
                  <span>Other Sessions ({otherSessions.length})</span>
                  <AlertDialog>
                    <AlertDialogTrigger asChild>
                      <Button variant="destructive" size="sm" className="gap-1 text-xs h-7">
                        <ShieldOff size={12} /> Sign out all others
                      </Button>
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                      <AlertDialogHeader>
                        <AlertDialogTitle>Sign out all other devices?</AlertDialogTitle>
                        <AlertDialogDescription>This will terminate all sessions except your current one.</AlertDialogDescription>
                      </AlertDialogHeader>
                      <AlertDialogFooter>
                        <AlertDialogCancel>{tr('common.cancel')}</AlertDialogCancel>
                        <AlertDialogAction onClick={terminateOthers}>
                          Confirm
                        </AlertDialogAction>
                      </AlertDialogFooter>
                    </AlertDialogContent>
                  </AlertDialog>
                </CardTitle>
              </CardHeader>
              <CardContent className="pt-0 divide-y">
                {otherSessions.map(sess => {
                  const deviceName = formatDeviceTitle(sess.browser, sess.device)
                  return (
                    <div key={sess.id} className="flex items-center gap-4 py-3">
                      <div className="p-2 rounded-lg bg-muted text-muted-foreground"><DeviceIcon device={deviceName} /></div>
                      <div className="flex-1">
                        <div className="flex items-center gap-2">
                          <p className="text-sm font-medium">{deviceName}</p>
                        </div>
                        <p className="text-xs text-muted-foreground">{sess.location || sess.ipAddress || 'Unknown location'} · {sess.lastActivity ? new Date(sess.lastActivity).toLocaleTimeString() : 'Recent'}</p>
                      </div>
                      <Button variant="outline" size="sm" className="gap-1 text-destructive border-destructive/30 hover:bg-destructive/10"
                        onClick={() => terminate(sess.id)}>
                        <LogOut size={12}/> Terminate
                      </Button>
                    </div>
                  )
                })}
              </CardContent>
            </Card>
          )}

          {/* Sign out all */}
          <AlertDialog>
            <AlertDialogTrigger asChild>
              <Button variant="destructive" className="w-full gap-2">
                <LogOut size={16}/>{tr('sessions.terminateAll')}
              </Button>
            </AlertDialogTrigger>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>{tr('sessions.terminateAll')}</AlertDialogTitle>
                <AlertDialogDescription>You will be signed out from all devices including this one.</AlertDialogDescription>
              </AlertDialogHeader>
              <AlertDialogFooter>
                <AlertDialogCancel>{tr('common.cancel')}</AlertDialogCancel>
                <AlertDialogAction onClick={terminateAll}>Sign out everywhere</AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </>
      )}
    </div>
  )
}
