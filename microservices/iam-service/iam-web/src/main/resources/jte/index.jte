@param kh.edu.istad.platform.iam.web.i18n.I18nMessages msg
@param String currentLang = "en"
@param kh.edu.istad.platform.iam.domain.dto.UserProfileResult user
@param java.util.List<String> roles
@param java.util.List<String> permissions
@param Boolean passwordChanged = false
@param org.springframework.security.web.csrf.CsrfToken csrfToken

<!DOCTYPE html>
<html lang="${currentLang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg.get("profile.title")} - ISTAD IAM</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&family=Kantumruy+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Plus Jakarta Sans', 'Kantumruy Pro', sans-serif; }
    </style>
</head>
<body class="bg-slate-950 text-slate-100 min-h-screen flex flex-col justify-between">

    <!-- Top Navigation Bar -->
    <header class="w-full max-w-7xl mx-auto px-6 py-4 flex flex-col items-center gap-4 md:flex-row md:justify-between border-b border-slate-800/80">
        <div class="flex items-center space-x-3">
            <a href="/" class="flex items-center gap-2" aria-label="ISTAD IAM">
                <img src="https://www.istad.co/_next/image?url=%2Fschool-logo%2Fistad-lms-logo.png&amp;w=256&amp;q=75" alt="ISTAD LMS" class="h-10 w-auto max-w-[150px] object-contain">
                <span class="text-lg font-bold tracking-tight text-indigo-400">ISTAD-IAM</span>
            </a>
        </div>

        <nav class="flex flex-wrap justify-center gap-x-6 gap-y-2 md:gap-y-0 text-sm font-medium text-slate-300">
            <a href="/" class="text-indigo-400 font-semibold">${msg.get("common.home")}</a>
            <a href="/profile/edit" class="hover:text-white transition">${msg.get("profile.edit")}</a>
            <a href="/profile/password" class="hover:text-white transition">${msg.get("profile.changePassword")}</a>
            <a href="/profile/accounts" class="hover:text-white transition">${msg.get("profile.linkedAccounts")}</a>
            <a href="/profile/apps" class="hover:text-white transition">${msg.get("profile.connectedApps")}</a>
        </nav>

        <div class="flex items-center space-x-4">
            <div class="flex items-center space-x-2 bg-slate-900 border border-slate-800 rounded-full px-3 py-1.5 shadow-sm">
                <span class="text-xs text-slate-400 font-medium mr-1">${msg.get("common.language")}:</span>
                <a href="?lang=en" class="px-2.5 py-0.5 rounded-full text-xs font-semibold transition ${"en".equals(currentLang) ? "bg-indigo-600 text-white shadow-sm" : "text-slate-400 hover:text-white"}">EN</a>
                <a href="?lang=km" class="px-2.5 py-0.5 rounded-full text-xs font-semibold transition ${"km".equals(currentLang) ? "bg-indigo-600 text-white shadow-sm" : "text-slate-400 hover:text-white"}">ខ្មែរ</a>
            </div>

            <form action="/logout" method="post" class="inline">
                <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
                <button type="submit" class="px-4 py-2 rounded-xl bg-red-500/10 hover:bg-red-500/20 text-red-400 text-xs font-semibold border border-red-500/20 transition">
                    ${msg.get("common.logout")}
                </button>
            </form>
        </div>
    </header>

    <main class="w-full max-w-7xl mx-auto px-6 py-8 flex-1">
        @if (passwordChanged)
            <div class="mb-6 p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-sm flex items-center space-x-3">
                <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
                <span>${msg.get("password.updateSuccess")}</span>
            </div>
        @endif

        <!-- Hero User Card -->
        <div class="bg-gradient-to-r from-slate-900 via-indigo-950/40 to-slate-900 border border-slate-800 rounded-3xl p-8 mb-8 shadow-xl flex flex-col md:flex-row items-center justify-between gap-6">
            <div class="flex items-center space-x-5">
                <div class="relative">
                    @if (user.getPictureUrl() != null && !user.getPictureUrl().isBlank())
                        <img src="${user.getPictureUrl()}" alt="Avatar" class="w-20 h-20 rounded-2xl object-cover border-2 border-indigo-500/40 shadow-lg">
                    @else
                        <div class="w-20 h-20 rounded-2xl bg-indigo-600/30 border-2 border-indigo-500/40 flex items-center justify-center text-indigo-300 font-bold text-2xl shadow-lg">
                            ${user.getGivenName() != null ? user.getGivenName().substring(0, 1).toUpperCase() : "U"}
                        </div>
                    @endif
                    <div class="absolute -bottom-1 -right-1 w-5 h-5 bg-emerald-500 rounded-full border-2 border-slate-950" title="Active"></div>
                </div>

                <div>
                    <h1 class="text-2xl font-bold text-white tracking-tight">${user.getGivenName()} ${user.getFamilyName()}</h1>
                    <p class="text-sm text-slate-400">@${user.getUsername()} • <span class="text-indigo-400">${user.getEmail()}</span></p>
                    <div class="flex items-center gap-2 mt-3">
                        <span class="text-xs px-2.5 py-1 rounded-lg bg-slate-800 text-slate-300 border border-slate-700">UUID: ${user.getId().toString()}</span>
                        <span class="text-xs px-2.5 py-1 rounded-lg bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">Verified: ${user.getEmailVerified()}</span>
                    </div>
                </div>
            </div>

            <div class="flex flex-wrap gap-3">
                <a href="/profile/edit" class="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-semibold shadow-lg shadow-indigo-600/30 transition">
                    ${msg.get("profile.edit")}
                </a>
                <a href="/profile/password" class="px-4 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-xl text-xs font-semibold transition">
                    ${msg.get("profile.changePassword")}
                </a>
                <a href="/profile/accounts" class="px-4 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-xl text-xs font-semibold transition">
                    ${msg.get("profile.linkedAccounts")}
                </a>
            </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <!-- Left 2 Cols: Claims & Personal Info -->
            <div class="lg:col-span-2 space-y-8">
                <div class="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl">
                    <h2 class="text-lg font-bold text-white mb-4 flex items-center space-x-2">
                        <span>📋</span>
                        <span>${msg.get("profile.claims")}</span>
                    </h2>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                        <div class="p-3 bg-slate-950 rounded-xl border border-slate-800/80">
                            <span class="text-xs text-slate-500 block uppercase font-semibold">${msg.get("profile.subjectId")}</span>
                            <span class="text-white font-mono text-xs break-all">${user.getId().toString()}</span>
                        </div>
                        <div class="p-3 bg-slate-950 rounded-xl border border-slate-800/80">
                            <span class="text-xs text-slate-500 block uppercase font-semibold">${msg.get("profile.preferredUsername")}</span>
                            <span class="text-white font-medium">${user.getUsername()}</span>
                        </div>
                        <div class="p-3 bg-slate-950 rounded-xl border border-slate-800/80">
                            <span class="text-xs text-slate-500 block uppercase font-semibold">${msg.get("profile.emailClaims")}</span>
                            <span class="text-white font-medium">${user.getEmail()} (${user.getEmailVerified()})</span>
                        </div>
                        <div class="p-3 bg-slate-950 rounded-xl border border-slate-800/80">
                            <span class="text-xs text-slate-500 block uppercase font-semibold">${msg.get("profile.phoneNumberClaim")}</span>
                            <span class="text-white font-medium">${user.getPhoneNumber() != null ? user.getPhoneNumber() : "—"}</span>
                        </div>
                        <div class="p-3 bg-slate-950 rounded-xl border border-slate-800/80">
                            <span class="text-xs text-slate-500 block uppercase font-semibold">${msg.get("profile.localeZoneinfoClaims")}</span>
                            <span class="text-white font-medium">${user.getLocale()} • ${user.getZoneinfo() != null ? user.getZoneinfo() : "—"}</span>
                        </div>
                        <div class="p-3 bg-slate-950 rounded-xl border border-slate-800/80">
                            <span class="text-xs text-slate-500 block uppercase font-semibold">${msg.get("profile.genderBirthdateClaims")}</span>
                            <span class="text-white font-medium">${user.getGender() != null ? user.getGender() : "—"} • ${user.getBirthdate() != null ? user.getBirthdate().toString() : "—"}</span>
                        </div>
                        <div class="p-3 bg-slate-950 rounded-xl border border-slate-800/80 md:col-span-2">
                            <span class="text-xs text-slate-500 block uppercase font-semibold">${msg.get("profile.addressClaim")}</span>
                            <span class="text-white font-medium">
                                ${user.getAddressStreetAddress() != null ? user.getAddressStreetAddress() : ""}, 
                                ${user.getAddressLocality() != null ? user.getAddressLocality() : ""}, 
                                ${user.getAddressRegion() != null ? user.getAddressRegion() : ""} 
                                ${user.getAddressCountry() != null ? user.getAddressCountry() : ""}
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Right Col: Roles & Effective Permissions -->
            <div class="space-y-8">
                <!-- Roles -->
                <div class="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl">
                    <h2 class="text-lg font-bold text-white mb-4 flex items-center space-x-2">
                        <span>🛡️</span>
                        <span>${msg.get("profile.roles")}</span>
                    </h2>
                    <div class="flex flex-wrap gap-2">
                        @for (var r : roles)
                            <span class="px-3 py-1.5 rounded-xl bg-indigo-500/20 text-indigo-300 border border-indigo-500/40 text-xs font-semibold uppercase tracking-wider">
                                ${r}
                            </span>
                        @endfor
                    </div>
                </div>

                <!-- Effective Permissions -->
                <div class="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl">
                    <h2 class="text-lg font-bold text-white mb-4 flex items-center space-x-2">
                        <span>🔑</span>
                        <span>${msg.get("profile.permissions")}</span>
                    </h2>
                    <div class="space-y-2 max-h-80 overflow-y-auto pr-1">
                        @for (var p : permissions)
                            <div class="px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono text-slate-300 flex items-center justify-between">
                                <span>${p}</span>
                                <span class="text-emerald-400 font-sans text-[10px]">${msg.get("common.active")}</span>
                            </div>
                        @endfor
                    </div>
                </div>
            </div>
        </div>
    </main>

    <footer class="w-full text-center py-6 text-xs text-slate-600 border-t border-slate-800/80">
        &copy; 2026 ISTAD ITE Platform. All rights reserved.
    </footer>
</body>
</html>
