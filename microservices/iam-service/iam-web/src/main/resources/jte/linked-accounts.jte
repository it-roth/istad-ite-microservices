@param kh.edu.istad.platform.iam.web.i18n.I18nMessages msg
@param String currentLang = "en"
@param java.util.List<kh.edu.istad.platform.iam.domain.dto.IdentityResult> identities
@param String successMessage = null
@param String errorMessage = null
@param org.springframework.security.web.csrf.CsrfToken csrfToken

<!DOCTYPE html>
<html lang="${currentLang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg.get("accounts.title")} - ISTAD IAM</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&family=Kantumruy+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Plus Jakarta Sans', 'Kantumruy Pro', sans-serif; }
    </style>
</head>
<body class="bg-slate-950 text-slate-100 min-h-screen flex flex-col justify-between">

    <header class="w-full max-w-7xl mx-auto px-6 py-4 flex flex-col items-center gap-4 md:flex-row md:justify-between border-b border-slate-800/80">
        <div class="flex items-center space-x-3">
            <a href="/" class="flex items-center gap-2" aria-label="ISTAD IAM">
                <img src="https://www.istad.co/_next/image?url=%2Fschool-logo%2Fistad-lms-logo.png&amp;w=256&amp;q=75" alt="ISTAD LMS" class="h-10 w-auto max-w-[150px] object-contain">
                <span class="text-lg font-bold tracking-tight text-indigo-400">ISTAD-IAM</span>
            </a>
        </div>

        <nav class="flex flex-wrap justify-center gap-x-6 gap-y-2 md:gap-y-0 text-sm font-medium text-slate-300">
            <a href="/" class="hover:text-white transition">${msg.get("common.home")}</a>
            <a href="/profile/edit" class="hover:text-white transition">${msg.get("profile.edit")}</a>
            <a href="/profile/password" class="hover:text-white transition">${msg.get("profile.changePassword")}</a>
            <a href="/profile/accounts" class="text-indigo-400 font-semibold">${msg.get("profile.linkedAccounts")}</a>
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

    <main class="w-full max-w-5xl mx-auto px-6 py-8 flex-1">
        <div class="bg-slate-900/90 border border-slate-800 rounded-3xl p-8 shadow-xl">
            <div class="flex items-center justify-between mb-8 pb-4 border-b border-slate-800">
                <div>
                    <h1 class="text-2xl font-bold text-white tracking-tight">${msg.get("accounts.title")}</h1>
                    <p class="text-sm text-slate-400 mt-1">${msg.get("accounts.description")}</p>
                </div>
                <a href="/" class="text-xs font-semibold text-slate-400 hover:text-white flex items-center space-x-1 transition">
                    <span>&larr;</span>
                    <span>${msg.get("common.back")}</span>
                </a>
            </div>

            @if (successMessage != null)
                <div class="mb-6 p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-sm flex items-center space-x-3">
                    <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
                    <span>${msg.get("common.success")}</span>
                </div>
            @endif

            @if (errorMessage != null)
                <div class="mb-6 p-4 rounded-xl bg-red-500/10 border border-red-500/30 text-red-400 text-sm flex items-center space-x-3">
                    <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                    <span>${errorMessage}</span>
                </div>
            @endif

            <!-- Identities Table -->
            @if (identities == null || identities.isEmpty())
                <div class="text-center py-12 text-slate-500 text-sm">
                    ${msg.get("profile.noIdentities")}
                </div>
            @else
                <div class="overflow-x-auto">
                    <table class="w-full text-left text-sm">
                        <thead class="text-xs uppercase tracking-wider text-slate-400 border-b border-slate-800">
                            <tr>
                                <th class="pb-3">${msg.get("accounts.provider")}</th>
                                <th class="pb-3">${msg.get("accounts.displayName")}</th>
                                <th class="pb-3">${msg.get("accounts.email")}</th>
                                <th class="pb-3">${msg.get("accounts.linkedAt")}</th>
                                <th class="pb-3 text-right">${msg.get("accounts.action")}</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-slate-800/60">
                            @for (var id : identities)
                                <tr class="hover:bg-slate-800/30 transition">
                                    <td class="py-4 font-semibold text-white flex items-center space-x-2">
                                        <span class="w-2 h-2 rounded-full bg-indigo-500"></span>
                                        <span>${id.provider()}</span>
                                    </td>
                                    <td class="py-4 text-slate-300">${id.displayName() != null ? id.displayName() : "—"}</td>
                                    <td class="py-4 text-slate-300">${id.email() != null ? id.email() : "—"}</td>
                                    <td class="py-4 text-slate-400 text-xs">${id.linkedAt() != null ? id.linkedAt().toString() : "—"}</td>
                                    <td class="py-4 text-right">
                                        <form action="/profile/accounts/${id.uuid()}/unlink" method="post" class="inline" onsubmit="return confirm('${msg.get("profile.unlinkConfirmation")}');">
                                            <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
                                            <button type="submit" class="px-3 py-1.5 rounded-lg bg-red-500/10 hover:bg-red-500/20 text-red-400 border border-red-500/20 text-xs font-semibold transition">
                                                ${msg.get("profile.unlink")}
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            @endfor
                        </tbody>
                    </table>
                </div>
            @endif

            <!-- Quick Link Additional Accounts -->
            <div class="mt-12 pt-8 border-t border-slate-800">
                <h2 class="text-sm font-bold text-white mb-4">${msg.get("profile.linkAdditionalIdentity")}</h2>
                <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
                    <a href="/oauth2/authorization/google" class="flex items-center justify-center space-x-2 py-2.5 px-4 bg-slate-950 hover:bg-slate-800 border border-slate-800 rounded-xl text-xs font-medium text-slate-200 transition">
                        <span>Google</span>
                    </a>
                    <a href="/oauth2/authorization/github" class="flex items-center justify-center space-x-2 py-2.5 px-4 bg-slate-950 hover:bg-slate-800 border border-slate-800 rounded-xl text-xs font-medium text-slate-200 transition">
                        <span>GitHub</span>
                    </a>
                    <a href="/oauth2/authorization/facebook" class="flex items-center justify-center space-x-2 py-2.5 px-4 bg-slate-950 hover:bg-slate-800 border border-slate-800 rounded-xl text-xs font-medium text-slate-200 transition">
                        <span>Facebook</span>
                    </a>
                    <a href="/login/telegram" class="flex items-center justify-center space-x-2 py-2.5 px-4 bg-slate-950 hover:bg-slate-800 border border-slate-800 rounded-xl text-xs font-medium text-slate-200 transition">
                        <span>Telegram</span>
                    </a>
                </div>
            </div>
        </div>
    </main>

    <footer class="w-full text-center py-6 text-xs text-slate-600 border-t border-slate-800/80">
        &copy; 2026 ISTAD ITE Platform. All rights reserved.
    </footer>
</body>
</html>
