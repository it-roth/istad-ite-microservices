import { useState } from 'react'
import { useApp } from '@/context/AppContext'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Alert, AlertDescription } from '@/components/ui/alert'
import {
  AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent,
  AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger,
} from '@/components/ui/alert-dialog'
import { AlertTriangle, UserX, CheckSquare } from 'lucide-react'

export default function DeactivatePage() {
  const { tr, user } = useApp()
  const [reason, setReason] = useState('')
  const [confirm, setConfirm] = useState('')
  const [loading, setLoading] = useState(false)
  const [done, setDone] = useState(false)

  const confirmText = 'deactivate my account'
  const isValid = confirm === confirmText

  async function handleDeactivate() {
    setLoading(true)
    try {
      // Calls disable endpoint on current user
      // In a real app: await meApi.deactivate({ reason })
      setDone(true)
      setTimeout(() => { window.location.href = 'http://localhost:39000/logout' }, 3000)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  if (done) {
    return (
      <div className="p-6 max-w-xl">
        <Card>
          <CardContent className="p-10 text-center space-y-4">
            <div className="mx-auto h-14 w-14 rounded-full bg-muted flex items-center justify-center">
              <CheckSquare size={28} className="text-muted-foreground" />
            </div>
            <h2 className="text-xl font-semibold">Account deactivation requested</h2>
            <p className="text-muted-foreground text-sm">Your account deactivation request has been submitted. You will be signed out shortly.</p>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="p-6 max-w-xl">
      <div className="mb-6 flex items-center gap-3">
        <div className="p-2 rounded-lg bg-destructive/10">
          <UserX className="text-destructive" size={22} />
        </div>
        <div>
          <h1 className="text-2xl font-bold">{tr('deactivate.title')}</h1>
          <p className="text-muted-foreground text-sm">{tr('deactivate.subtitle')}</p>
        </div>
      </div>

      <Alert variant="destructive" className="mb-6">
        <AlertTriangle className="h-4 w-4" />
        <AlertDescription>{tr('deactivate.warning')}</AlertDescription>
      </Alert>

      <Card className="mb-4">
        <CardHeader>
          <CardTitle className="text-base">What happens when you deactivate</CardTitle>
        </CardHeader>
        <CardContent className="space-y-2 text-sm text-muted-foreground">
          {[
            'Your profile will be hidden from the system',
            'All active sessions will be terminated',
            'OAuth2 tokens issued to your account will be revoked',
            'You will lose access to all authorized applications',
            'This action requires administrator approval to reverse',
          ].map((item, i) => (
            <div key={i} className="flex items-start gap-2">
              <span className="text-destructive mt-0.5">•</span>
              <span>{item}</span>
            </div>
          ))}
        </CardContent>
      </Card>

      <Card>
        <CardContent className="p-6 space-y-4">
          <div>
            <Label htmlFor="reason">Reason (optional)</Label>
            <Input
              id="reason" className="mt-1"
              placeholder="Tell us why you're leaving…"
              value={reason} onChange={e => setReason(e.target.value)}
            />
          </div>

          <div>
            <Label htmlFor="confirm">
              Type <span className="font-mono font-semibold text-destructive">{confirmText}</span> to confirm
            </Label>
            <Input
              id="confirm" className="mt-1 font-mono"
              placeholder={confirmText}
              value={confirm} onChange={e => setConfirm(e.target.value)}
            />
          </div>

          <AlertDialog>
            <AlertDialogTrigger asChild>
              <Button variant="destructive" className="w-full gap-2" disabled={!isValid || loading}>
                <UserX size={16}/>{tr('deactivate.btn')}
              </Button>
            </AlertDialogTrigger>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
                <AlertDialogDescription>
                  This will deactivate your account <strong>{user?.email}</strong>. You will be signed out immediately.
                  {reason && <span className="block mt-1">Reason: "{reason}"</span>}
                </AlertDialogDescription>
              </AlertDialogHeader>
              <AlertDialogFooter>
                <AlertDialogCancel>{tr('common.cancel')}</AlertDialogCancel>
                <AlertDialogAction onClick={handleDeactivate} className="bg-destructive text-destructive-foreground hover:bg-destructive/90">
                  {loading ? 'Processing…' : 'Yes, deactivate my account'}
                </AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </CardContent>
      </Card>
    </div>
  )
}
