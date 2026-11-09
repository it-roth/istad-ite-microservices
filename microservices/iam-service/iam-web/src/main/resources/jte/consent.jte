@param kh.edu.istad.platform.iam.web.i18n.I18nMessages msg
@param String currentLang = "en"
@param String clientId
@param String clientName
@param String state
@param java.util.Set<String> scopes
@param java.util.Set<String> previouslyApprovedScopes
@param String principalName
@param String userCode = null
@param org.springframework.security.web.csrf.CsrfToken csrfToken

<!DOCTYPE html>
<html lang="${currentLang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg.get("consent.title")} - ISTAD IAM</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&family=Kantumruy+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Plus Jakarta Sans', 'Kantumruy Pro', sans-serif; }
    </style>
</head>
<body class="bg-slate-950 text-slate-100 min-h-screen flex flex-col justify-between">

    <header class="w-full max-w-6xl mx-auto px-6 py-4 flex justify-between items-center">
        <div class="flex items-center space-x-3">
            <div class="flex items-center gap-2" aria-label="ISTAD IAM">
                <img src="https://www.istad.co/_next/image?url=%2Fschool-logo%2Fistad-lms-logo.png&amp;w=256&amp;q=75" alt="ISTAD LMS" class="h-10 w-auto max-w-[150px] object-contain">
                <span class="text-lg font-bold tracking-tight text-indigo-400">ISTAD-IAM</span>
            </div>
        </div>
        <div class="flex items-center space-x-2 bg-slate-900 border border-slate-800 rounded-full px-3 py-1.5 shadow-sm">
            <span class="text-xs text-slate-400 font-medium mr-1">${msg.get("common.language")}:</span>
            <a href="?lang=en" class="px-2.5 py-0.5 rounded-full text-xs font-semibold transition ${"en".equals(currentLang) ? "bg-indigo-600 text-white shadow-sm" : "text-slate-400 hover:text-white"}">EN</a>
            <a href="?lang=km" class="px-2.5 py-0.5 rounded-full text-xs font-semibold transition ${"km".equals(currentLang) ? "bg-indigo-600 text-white shadow-sm" : "text-slate-400 hover:text-white"}">ខ្មែរ</a>
        </div>
    </header>

    <main class="w-full max-w-lg mx-auto px-6 py-8">
        <div class="bg-slate-900/90 backdrop-blur-xl border border-slate-800 rounded-3xl p-8 shadow-2xl shadow-indigo-950/40">
            <div class="flex items-center space-x-4 mb-6">
                <div class="w-12 h-12 rounded-2xl bg-indigo-500/10 border border-indigo-500/30 flex items-center justify-center text-indigo-400 font-bold text-xl">
                    🔐
                </div>
                <div>
                    <h1 class="text-xl font-bold text-white tracking-tight">${msg.get("consent.title")}</h1>
                    <p class="text-xs text-slate-400 mt-1">${msg.get("consent.description", clientName != null ? clientName : clientId)}</p>
                </div>
            </div>

            <form id="consent-form" action="/oauth2/authorize" method="post" class="space-y-6">
                <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
                <input type="hidden" name="client_id" value="${clientId}">
                <input type="hidden" name="state" value="${state}">
                @if (userCode != null)
                    <input type="hidden" name="user_code" value="${userCode}">
                @endif

                @if (!scopes.isEmpty())
                    <div>
                        <h2 class="text-xs font-semibold uppercase tracking-wider text-slate-400 mb-3">${msg.get("consent.scopesRequested")}</h2>
                        <div class="space-y-2 bg-slate-950 border border-slate-800 rounded-2xl p-4">
                            @for (var s : scopes)
                                <label class="flex items-start space-x-3 text-sm text-slate-200 cursor-pointer">
                                    <input type="checkbox" name="scope" value="${s}" checked class="mt-1 h-4 w-4 rounded border-slate-700 text-indigo-600 focus:ring-indigo-500 bg-slate-900">
                                    <div>
                                        <span class="font-medium text-white">${s}</span>
                                        <span class="block text-xs text-slate-400">${msg.get("consent.scopeDescription", s)}</span>
                                    </div>
                                </label>
                            @endfor
                        </div>
                    </div>
                @endif

                @if (!previouslyApprovedScopes.isEmpty())
                    <div>
                        <h2 class="text-xs font-semibold uppercase tracking-wider text-slate-400 mb-3">${msg.get("consent.previouslyConsented")}</h2>
                        <div class="space-y-2 bg-slate-950/50 border border-slate-800/80 rounded-2xl p-4">
                            @for (var s : previouslyApprovedScopes)
                                <label class="flex items-start space-x-3 text-sm text-slate-300">
                                    <input type="checkbox" disabled checked class="mt-1 h-4 w-4 rounded border-slate-700 text-indigo-600/50 bg-slate-900">
                                    <div>
                                        <span class="font-medium text-slate-300">${s}</span>
                                        <span class="block text-xs text-slate-500">${msg.get("common.alreadyApproved")}</span>
                                    </div>
                                </label>
                            @endfor
                        </div>
                    </div>
                @endif

                <div class="flex items-center space-x-4 pt-2">
                    <button type="submit" class="flex-1 py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold rounded-xl text-sm shadow-lg shadow-indigo-600/30 transition text-center">
                        ${msg.get("consent.allow")}
                    </button>
                    <button type="button" onclick="cancelConsent()" class="flex-1 py-3 bg-slate-800 hover:bg-slate-700 text-slate-300 font-semibold rounded-xl text-sm transition text-center">
                        ${msg.get("consent.cancel")}
                    </button>
                </div>
            </form>
            <script>
                function cancelConsent() {
                    document.querySelectorAll('#consent-form input[name="scope"]').forEach(scope => scope.checked = false);
                    document.getElementById('consent-form').requestSubmit();
                }
            </script>
        </div>
    </main>

    <footer class="w-full text-center py-6 text-xs text-slate-600">
        &copy; 2026 ISTAD ITE Platform. All rights reserved.
    </footer>
</body>
</html>
