import React from 'react'

export class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props)
    this.state = { hasError: false, error: null }
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error }
  }

  componentDidCatch(error, errorInfo) {
    console.error('ErrorBoundary caught an error:', error, errorInfo)
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-full flex flex-col items-center justify-center p-8 text-center bg-background text-foreground">
          <div className="max-w-md space-y-4">
            <div className="h-12 w-12 rounded-full bg-rose-100 dark:bg-rose-950/40 text-rose-600 flex items-center justify-center mx-auto text-xl font-bold">
              !
            </div>
            <h2 className="text-lg font-bold">Something went wrong</h2>
            <p className="text-xs text-muted-foreground font-mono bg-muted/40 p-3 rounded-lg text-left break-all">
              {this.state.error?.message || String(this.state.error)}
            </p>
            <div className="flex gap-2 justify-center pt-2">
              <button
                onClick={() => this.setState({ hasError: false, error: null })}
                className="px-4 py-2 text-xs font-semibold rounded-md bg-primary text-primary-foreground hover:bg-primary/90"
              >
                Try again
              </button>
              <button
                onClick={() => window.location.href = '/admin'}
                className="px-4 py-2 text-xs font-semibold rounded-md border border-border hover:bg-muted"
              >
                Go to admin
              </button>
            </div>
          </div>
        </div>
      )
    }

    return this.props.children
  }
}
