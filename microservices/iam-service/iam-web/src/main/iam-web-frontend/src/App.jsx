import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { useApp } from '@/context/AppContext'
import AppLayout from '@/components/layout/AppLayout'
import { ErrorBoundary } from '@/components/common/ErrorBoundary'
import AccountOverviewPage from '@/pages/account/AccountOverviewPage'
import ProfilePage from '@/pages/account/ProfilePage'
import SecurityPage from '@/pages/account/SecurityPage'
import LinkedAccountsPage from '@/pages/account/LinkedAccountsPage'
import ActiveSessionsPage from '@/pages/account/ActiveSessionsPage'
import SessionManagementPage from '@/pages/account/SessionManagementPage'
import LoginHistoryPage from '@/pages/account/LoginHistoryPage'
import AccountActivityPage from '@/pages/account/AccountActivityPage'
import DeactivatePage from '@/pages/account/DeactivatePage'
import AdminOverviewPage from '@/pages/admin/AdminOverviewPage'
import AdminUsersPage from '@/pages/admin/AdminUsersPage'
import UserDetailPage from '@/pages/admin/UserDetailPage'
import AdminCatalogPage from '@/pages/admin/AdminCatalogPage'
import RoleDetailPage from '@/pages/admin/RoleDetailPage'
import ApplicationsPage from '@/pages/admin/ApplicationsPage'
import RegisterApplicationPage from '@/pages/admin/RegisterApplicationPage'
import AuditLogPage from '@/pages/admin/AuditLogPage'
import SystemConfigurationPage from '@/pages/admin/SystemConfigurationPage'
import './App.css'

function AdminRoute({ children }) {
  const { user } = useApp()
  const isAdmin = user?.roles?.some(r =>
    r?.name === 'ROLE_ADMIN' || r?.name === 'ROLE_SUPERADMIN' || r === 'ROLE_ADMIN' || r === 'ROLE_SUPERADMIN'
  ) || user?.role?.includes('ADMIN') || user?.username === 'admin' || user?.username === 'superadmin'

  if (!isAdmin) {
    return <Navigate to="/account" replace />
  }
  return children
}

export default function App() {
  return (
    <BrowserRouter>
      <ErrorBoundary>
        <Routes>
        <Route element={<AppLayout />}>
          <Route index element={<Navigate to="/account" replace />} />
          <Route path="dashboard" element={<AccountOverviewPage />} />
          
          {/* ========================================================= */}
          {/* 1. KEYCLOAK ACCOUNT CONSOLE (What All Regular Users See)  */}
          {/* ========================================================= */}
          <Route path="account" element={<AccountOverviewPage />} />
          <Route path="account/profile" element={<ProfilePage />} />
          <Route path="account/security" element={<SecurityPage />} />
          <Route path="account/linked" element={<LinkedAccountsPage />} />
          <Route path="account/linked-accounts" element={<LinkedAccountsPage />} />
          <Route path="account/sessions" element={<ActiveSessionsPage />} />
          <Route path="account/session-mgmt" element={<SessionManagementPage />} />
          <Route path="account/history" element={<LoginHistoryPage />} />
          <Route path="account/activity" element={<AccountActivityPage />} />
          <Route path="account/deactivate" element={<DeactivatePage />} />
          
          {/* ========================================================= */}
          {/* 2. KEYCLOAK ADMIN CONSOLE (What Superadmin / Admin Sees)   */}
          {/* ========================================================= */}
          <Route path="admin" element={<AdminRoute><AdminOverviewPage /></AdminRoute>} />
          <Route path="admin/users" element={<AdminRoute><AdminUsersPage /></AdminRoute>} />
          <Route path="admin/users/:uuid" element={<AdminRoute><UserDetailPage /></AdminRoute>} />
          <Route path="admin/roles" element={<AdminRoute><AdminCatalogPage type="roles" /></AdminRoute>} />
          <Route path="admin/roles/:uuid" element={<AdminRoute><RoleDetailPage /></AdminRoute>} />
          <Route path="admin/permissions" element={<AdminRoute><AdminCatalogPage type="permissions" /></AdminRoute>} />
          <Route path="admin/applications" element={<AdminRoute><ApplicationsPage /></AdminRoute>} />
          <Route path="admin/applications/new" element={<AdminRoute><RegisterApplicationPage /></AdminRoute>} />
          <Route path="admin/clients" element={<AdminRoute><ApplicationsPage /></AdminRoute>} />
          <Route path="admin/clients/new" element={<AdminRoute><RegisterApplicationPage /></AdminRoute>} />
          <Route path="admin/audit" element={<AdminRoute><AuditLogPage /></AdminRoute>} />
          <Route path="admin/configuration" element={<AdminRoute><SystemConfigurationPage /></AdminRoute>} />
          <Route path="admin/security-config" element={<AdminRoute><SystemConfigurationPage /></AdminRoute>} />
        </Route>
        <Route path="*" element={<Navigate to="/account" replace />} />
      </Routes>
    </ErrorBoundary>
  </BrowserRouter>
  )
}
