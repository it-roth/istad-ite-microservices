/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useState, useEffect } from 'react'
import { t } from '@/lib/i18n'

const AppContext = createContext(null)

function getStoredLocale() {
  try {
    const cookies = document.cookie || ''
    const match = cookies.match(/(?:^|;\s*)(?:APP_LOCALE|lang)=([^;]+)/)
    if (match) {
      const val = decodeURIComponent(match[1]).toLowerCase()
      if (val.includes('km')) return 'km'
      if (val.includes('en')) return 'en'
    }
    const local = localStorage.getItem('lang')
    if (local && (local === 'km' || local === 'en')) {
      return local
    }
  } catch (e) {
    console.error('Failed to read locale:', e)
  }
  return 'en'
}

export function AppProvider({ children }) {
  const [lang, setLang] = useState(getStoredLocale)
  const [user, setUser] = useState(null)
  const [breadcrumbSub, setBreadcrumbSub] = useState('')

  function switchLang(l) {
    setLang(l)
    try {
      localStorage.setItem('lang', l)
      document.cookie = `APP_LOCALE=${l};path=/;max-age=31536000;SameSite=Lax`
      document.cookie = `lang=${l};path=/;max-age=31536000;SameSite=Lax`
    } catch (e) {
      console.error('Failed to save locale:', e)
    }
  }

  function tr(key) {
    return t(lang, key)
  }

  return (
    <AppContext.Provider value={{ lang, switchLang, tr, user, setUser, breadcrumbSub, setBreadcrumbSub }}>
      {children}
    </AppContext.Provider>
  )
}

export function useApp() {
  return useContext(AppContext)
}
