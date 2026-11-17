import { useMemo, useState } from 'react'
import { ArrowUpRight, Search } from 'lucide-react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'

const datasets = {
  analytics: {
    title: 'Analytics',
    description: 'Understand performance trends across your IAM workspace.',
    metrics: [['Sessions', '24,892', '+18.2%'], ['Sign-ins', '18,420', '+9.4%'], ['Success rate', '96.8%', '+2.1%']],
    rows: [['Authentication', '12,480', 'Healthy'], ['Account updates', '4,821', 'Healthy'], ['OAuth activity', '1,119', 'Review']],
  },
  customers: {
    title: 'Customers',
    description: 'Review recent customer accounts and engagement.',
    metrics: [['Total customers', '12,480', '+12.5%'], ['Active today', '8,214', '+8.3%'], ['New this month', '1,284', '+14.2%']],
    rows: [['Sokha Lim', 'sokha@example.com', 'Active'], ['Dara Chann', 'dara@example.com', 'Active'], ['Malis Vann', 'malis@example.com', 'Pending']],
  },
  products: {
    title: 'Products',
    description: 'Monitor products, subscriptions, and access status.',
    metrics: [['Active products', '248', '+6.8%'], ['Subscriptions', '9,842', '+11.2%'], ['Churn rate', '2.4%', '-0.8%']],
    rows: [['IAM Professional', '4,820 seats', 'Active'], ['IAM Enterprise', '3,210 seats', 'Active'], ['Developer plan', '1,812 seats', 'Trial']],
  },
  settings: {
    title: 'Settings',
    description: 'Configure workspace preferences and account defaults.',
    metrics: [['Security score', '98%', '+4.1%'], ['Policies', '12', 'Configured'], ['Integrations', '6', 'Connected']],
    rows: [['Authentication policy', 'Password + OAuth2', 'Configured'], ['Session timeout', '30 minutes', 'Configured'], ['Default locale', 'English / Khmer', 'Configured']],
  },
}

export default function WorkspacePage({ section }) {
  const data = datasets[section]
  const [query, setQuery] = useState('')
  const rows = useMemo(() => data.rows.filter(row => row.join(' ').toLowerCase().includes(query.toLowerCase())), [data.rows, query])

  return (
    <div className="min-h-full bg-background p-6 md:p-8">
      <div className="mx-auto max-w-[1440px] space-y-6">
        <div><p className="text-sm text-muted-foreground">Workspace</p><h1 className="text-3xl font-semibold tracking-tight">{data.title}</h1><p className="mt-1 text-sm text-muted-foreground">{data.description}</p></div>
        <div className="grid gap-4 md:grid-cols-3">
          {data.metrics.map(([label, value, change]) => <Card key={label}><CardContent className="p-5"><p className="text-sm text-muted-foreground">{label}</p><div className="mt-2 flex items-center justify-between"><p className="text-2xl font-semibold">{value}</p><Badge variant={change.startsWith('-') ? 'warning' : 'success'}>{change}</Badge></div></CardContent></Card>)}
        </div>
        <Card><CardHeader className="flex-row items-center justify-between"><CardTitle className="text-base">Recent records</CardTitle><div className="relative w-56"><Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" /><Input value={query} onChange={event => setQuery(event.target.value)} placeholder="Filter records" className="pl-9" /></div></CardHeader><CardContent><div className="overflow-x-auto"><table className="w-full text-left text-sm"><thead><tr className="border-b border-border text-xs text-muted-foreground"><th className="pb-3 font-medium">Name</th><th className="pb-3 font-medium">Value</th><th className="pb-3 font-medium">Status</th><th className="pb-3 text-right font-medium">Details</th></tr></thead><tbody>{rows.map(row => <tr key={row[0]} className="border-b border-border last:border-0"><td className="py-4 font-medium">{row[0]}</td><td className="py-4 text-muted-foreground">{row[1]}</td><td className="py-4"><Badge variant={row[2] === 'Active' || row[2] === 'Healthy' || row[2] === 'Configured' || row[2] === 'Connected' ? 'success' : 'secondary'}>{row[2]}</Badge></td><td className="py-4 text-right"><button className="inline-flex items-center gap-1 text-xs text-primary">View <ArrowUpRight size={13} /></button></td></tr>)}</tbody></table>{rows.length === 0 && <p className="py-8 text-center text-sm text-muted-foreground">No matching records.</p>}</div></CardContent></Card>
      </div>
    </div>
  )
}
