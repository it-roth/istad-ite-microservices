/**
 * Helper to parse raw user-agent strings into clean, human-readable Browser and OS
 */
export function parseUserAgent(uaString) {
  if (!uaString) return { browser: 'Chrome', os: 'Windows 10/11', device: 'Chrome · Windows 10/11' }
  const ua = String(uaString)

  // 1. Detect browser
  let browser = 'Chrome'
  if (ua.includes('Edg/') || ua.includes('Edge/')) browser = 'Microsoft Edge'
  else if (ua.includes('OPR/') || ua.includes('Opera/')) browser = 'Opera'
  else if (ua.includes('Chrome/')) browser = 'Chrome'
  else if (ua.includes('Firefox/')) browser = 'Firefox'
  else if (ua.includes('Safari/') && !ua.includes('Chrome/')) browser = 'Safari'
  else if (ua.includes('PostmanRuntime')) browser = 'Postman API'
  else if (ua.includes('curl/')) browser = 'cURL CLI'
  else if (ua.includes('python-requests')) browser = 'Python Client'

  // 2. Detect OS / Device
  let os = 'Windows 10/11'
  if (ua.includes('Windows NT 10.0')) os = 'Windows 10/11'
  else if (ua.includes('Windows NT 6.3')) os = 'Windows 8.1'
  else if (ua.includes('Windows NT 6.1')) os = 'Windows 7'
  else if (ua.includes('Windows')) os = 'Windows'
  else if (ua.includes('Macintosh') || ua.includes('Mac OS')) os = 'macOS'
  else if (ua.includes('iPhone')) os = 'iOS (iPhone)'
  else if (ua.includes('iPad')) os = 'iPadOS'
  else if (ua.includes('Android')) os = 'Android'
  else if (ua.includes('Linux')) os = 'Linux'

  return {
    browser,
    os,
    device: `${browser} · ${os}`
  }
}

export function formatDeviceTitle(browserStr, deviceStr) {
  const p1 = parseUserAgent(browserStr)
  const p2 = parseUserAgent(deviceStr)

  const b = p1.browser !== 'Browser' ? p1.browser : (p2.browser || 'Chrome')
  const o = p2.os !== 'Windows 10/11' ? p2.os : (p1.os || 'Windows 10/11')

  return `${b} on ${o}`
}
