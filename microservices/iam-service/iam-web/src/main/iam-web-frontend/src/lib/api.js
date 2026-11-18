const BASE = '/api'

async function req(path, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  }
  const res = await fetch(`${BASE}${path}`, {
    ...options,
    credentials: 'same-origin',
    headers,
  })
  if (res.status === 204 || res.status === 205) return null
  if (!res.ok) {
    const err = await res.json().catch(() => ({ title: res.statusText }))
    let msg = err?.detail || err?.title || err?.message || err?.error || res.statusText
    if (Array.isArray(err?.errors) && err.errors.length > 0) {
      msg = err.errors.map(e => e.defaultMessage || e.message || e.field).join(', ')
    }
    throw Object.assign(new Error(msg || 'Request failed'), { status: res.status, body: err })
  }
  return res.json()
}

export const meApi = {
  get: () => req('/me'),
  authorities: () => req('/me/authorities'),
  update: (data) => req('/me', { method: 'PATCH', body: JSON.stringify(data) }),
  changePassword: (data) => req('/me/password', { method: 'PUT', body: JSON.stringify(data) }),
  resetPassword: (data) => req('/me/password/reset', { method: 'POST', body: JSON.stringify(data) }),
  requestPasswordReset: () => req('/me/password/reset-request', { method: 'POST' }),
  verifyEmail: () => req('/me/verify-email', { method: 'POST' }),
  updateTwoFactor: (enabled) => req('/me/two-factor', { method: 'PUT', body: JSON.stringify({ enabled }) }),
  identities: () => req('/me/identities'),
  unlinkIdentity: (id) => req(`/me/identities/${id}`, { method: 'DELETE' }),
  sessions: () => req('/me/sessions'),
  revokeSession: (id) => req(`/me/sessions/${id}`, { method: 'DELETE' }),
  revokeOthers: () => req('/me/sessions', { method: 'DELETE' }),
  loginHistory: (limit = 50) => req(`/me/login-history?limit=${limit}`),
  activity: (limit = 50) => req(`/me/activity?limit=${limit}`),
  connectedApplications: () => req('/me/connected-applications'),
  revokeConnectedApplication: (id) => req(`/me/connected-applications/${id}`, { method: 'DELETE' }),
  deactivate: (data) => req('/me/deactivate', { method: 'POST', body: JSON.stringify(data || {}) }),
}

export const usersApi = {
  list: (page = 0, size = 20, query = '', enabled) =>
    req(`/users?page=${page}&size=${size}${query ? `&query=${encodeURIComponent(query)}` : ''}${enabled === undefined ? '' : `&enabled=${enabled}`}`),
  get: (uuid) => req(`/users/${uuid}`),
  create: (data) => req('/users', { method: 'POST', body: JSON.stringify(data) }),
  update: (uuid, data) => req(`/users/${uuid}`, { method: 'PATCH', body: JSON.stringify(data) }),
  delete: (uuid) => req(`/users/${uuid}`, { method: 'DELETE' }),
  enable: (uuid) => req(`/users/${uuid}/enable`, { method: 'POST' }),
  disable: (uuid) => req(`/users/${uuid}/disable`, { method: 'POST' }),
  lock: (uuid) => req(`/users/${uuid}/lock`, { method: 'POST' }),
  unlock: (uuid) => req(`/users/${uuid}/unlock`, { method: 'POST' }),
  resetPassword: (uuid, newPassword, confirmPassword) =>
    req(`/users/${uuid}/password`, {
      method: 'PUT',
      body: JSON.stringify({
        newPassword,
        confirmPassword: confirmPassword || newPassword,
      }),
    }),
  authorities: (uuid) => req(`/users/${uuid}/authorities`),
  identities: (uuid) => req(`/users/${uuid}/identities`),
  assignRoles: (uuid, roleUuids) =>
    req(`/users/${uuid}/roles`, { method: 'POST', body: JSON.stringify({ roleUuids }) }),
  revokeRole: (uuid, roleUuid) => req(`/users/${uuid}/roles/${roleUuid}`, { method: 'DELETE' }),
  grantPermissions: (uuid, permissionUuids, effect = 'GRANT', reason = 'Assigned by admin') =>
    req(`/users/${uuid}/permissions`, { method: 'POST', body: JSON.stringify({ permissionUuids, effect, reason }) }),
  revokePermission: (uuid, permissionUuid, reason = 'Revoked by admin') =>
    req(`/users/${uuid}/permissions/${permissionUuid}?reason=${encodeURIComponent(reason)}`, { method: 'DELETE' }),
  sessions: (uuid) => req(`/users/${uuid}/sessions`),
  terminateSession: (uuid, sessionId) => req(`/users/${uuid}/sessions/${sessionId}`, { method: 'DELETE' }),
  loginHistory: (uuid, limit = 50) => req(`/users/${uuid}/login-history?limit=${limit}`),
}

export const rolesApi = {
  list: (page = 0, size = 50) => req(`/roles?page=${page}&size=${size}`),
  get: (uuid) => req(`/roles/${uuid}`),
  create: (data) => req('/roles', { method: 'POST', body: JSON.stringify(data) }),
  update: (uuid, data) => req(`/roles/${uuid}`, { method: 'PATCH', body: JSON.stringify(data) }),
  delete: (uuid) => req(`/roles/${uuid}`, { method: 'DELETE' }),
  permissions: (uuid) => req(`/roles/${uuid}/permissions`),
  assignPermissions: (uuid, permissionUuids) =>
    req(`/roles/${uuid}/permissions`, { method: 'POST', body: JSON.stringify({ permissionUuids }) }),
  revokePermission: (uuid, permUuid) =>
    req(`/roles/${uuid}/permissions/${permUuid}`, { method: 'DELETE' }),
  activate: (uuid) => req(`/roles/${uuid}/activate`, { method: 'POST' }),
  deactivate: (uuid) => req(`/roles/${uuid}/deactivate`, { method: 'POST' }),
}

export const permissionsApi = {
  list: (page = 0, size = 100) => req(`/permissions?page=${page}&size=${size}`),
  get: (uuid) => req(`/permissions/${uuid}`),
  create: (data) => req('/permissions', { method: 'POST', body: JSON.stringify(data) }),
  update: (uuid, data) => req(`/permissions/${uuid}`, { method: 'PATCH', body: JSON.stringify(data) }),
  delete: (uuid) => req(`/permissions/${uuid}`, { method: 'DELETE' }),
  activate: (uuid) => req(`/permissions/${uuid}/activate`, { method: 'POST' }),
  deactivate: (uuid) => req(`/permissions/${uuid}/deactivate`, { method: 'POST' }),
}

export const clientsApi = {
  list: () => req('/admin/oauth-clients'),
  get: (id) => req(`/admin/oauth-clients/${id}`),
  create: (data) => req('/admin/oauth-clients', { method: 'POST', body: JSON.stringify(data) }),
  update: (id, data) => req(`/admin/oauth-clients/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id) => req(`/admin/oauth-clients/${id}`, { method: 'DELETE' }),
}

export const adminSessionsApi = {
  list: () => req('/admin/sessions'),
  terminate: (id) => req(`/admin/sessions/${id}`, { method: 'DELETE' }),
}

export const securityConfigApi = {
  get: () => req('/admin/security/configuration'),
  update: (data) => req('/admin/security/configuration', { method: 'PUT', body: JSON.stringify(data) }),
}

export const adminAuditApi = {
  list: (limit = 100) => req(`/admin/audit-logs?limit=${limit}`),
}

export const authApi = {
  register: (data) => req('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
}
