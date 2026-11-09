@param kh.edu.istad.platform.iam.web.i18n.I18nMessages msg
@param String currentLang = "en"
@param java.util.List<kh.edu.istad.platform.iam.domain.dto.OAuthClientResult> apps
@param String successMessage = null
@param org.springframework.security.web.csrf.CsrfToken csrfToken

<!DOCTYPE html>
<html lang="${currentLang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg.get("apps.title")} - ISTAD IAM</title>
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
            <a href="/profile/accounts" class="hover:text-white transition">${msg.get("profile.linkedAccounts")}</a>
            <a href="/profile/apps" class="text-indigo-400 font-semibold">${msg.get("profile.connectedApps")}</a>
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
                    <h1 class="text-2xl font-bold text-white tracking-tight">${msg.get("apps.title")}</h1>
                    <p class="text-sm text-slate-400 mt-1">${msg.get("apps.description")}</p>
                </div>
                <a href="/" class="text-xs font-semibold text-slate-400 hover:text-white flex items-center space-x-1 transition">
                    <span>&larr;</span>
                    <span>${msg.get("common.back")}</span>
                </a>
            </div>

            @if (successMessage != null)
                <div class="mb-6 p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-sm flex items-center space-x-3">
                    <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
                    <span>${msg.get("apps.revokeSuccess")}</span>
                </div>
            @endif

            @if (apps == null || apps.isEmpty())
                <div class="text-center py-12 text-slate-500 text-sm">
                    ${msg.get("apps.noApps")}
                </div>
            @else
                <div class="space-y-4">
                    @for (var app : apps)
                        <div class="p-5 bg-slate-950 border border-slate-800 rounded-2xl flex flex-col md:flex-row md:items-center justify-between gap-4">
                            <div>
                                <div class="flex items-center space-x-3">
                                    <span class="w-10 h-10 rounded-xl bg-indigo-500/10 border border-indigo-500/30 flex items-center justify-center text-indigo-400 font-bold text-lg">
                                        📱
                                    </span>
                                    <div>
                                        <h3 class="font-bold text-white text-base">${app.clientName()}</h3>
                                        <p class="text-xs font-mono text-slate-400 mt-0.5">Client ID: ${app.clientId()}</p>
                                    </div>
                                </div>
                                <div class="flex flex-wrap gap-2 mt-4 ml-13">
                                    @for (var sc : app.scopes())
                                        <span class="px-2.5 py-1 rounded-lg bg-slate-900 border border-slate-800 text-[11px] font-mono text-indigo-300">
                                            ${sc}
                                        </span>
                                    @endfor
                                </div>
                            </div>

                            <form action="/profile/apps/${app.registeredClientId()}/revoke" method="post" onsubmit="return confirm('${msg.get("profile.revokeConfirmation")}');">
                                <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
                                <button type="submit" class="px-4 py-2 rounded-xl bg-red-500/10 hover:bg-red-500/20 text-red-400 border border-red-500/20 text-xs font-semibold transition w-full md:w-auto">
                                    ${msg.get("profile.revoke")}
                                </button>
                            </form>
                        </div>
                    @endfor
                </div>
            @endif
        </div>
    </main>

    <footer class="w-full text-center py-6 text-xs text-slate-600 border-t border-slate-800/80">
        &copy; 2026 ISTAD ITE Platform. All rights reserved.
    </footer>
</body>
</html>
