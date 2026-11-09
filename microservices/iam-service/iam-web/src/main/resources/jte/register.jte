@param kh.edu.istad.platform.iam.web.i18n.I18nMessages msg
@param String currentLang = "en"
@param String errorMessage = null
@param String username = null
@param String email = null
@param String givenName = null
@param String familyName = null
@param String locale = "en"
@param org.springframework.security.web.csrf.CsrfToken csrfToken

<!DOCTYPE html>
<html lang="${currentLang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg.get("register.title")} - ISTAD IAM</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&family=Kantumruy+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Plus Jakarta Sans', 'Kantumruy Pro', sans-serif; }
    </style>
</head>
<body class="bg-slate-950 text-slate-100 min-h-screen flex flex-col justify-between">

    <header class="w-full max-w-6xl mx-auto px-6 py-4 flex justify-between items-center">
        <div class="flex items-center space-x-3">
            <a href="/" class="flex items-center gap-2" aria-label="ISTAD IAM">
                <img src="https://www.istad.co/_next/image?url=%2Fschool-logo%2Fistad-lms-logo.png&amp;w=256&amp;q=75" alt="ISTAD LMS" class="h-10 w-auto max-w-[150px] object-contain">
                <span class="text-lg font-bold tracking-tight text-indigo-400">ISTAD-IAM</span>
            </a>
        </div>
        <div class="flex items-center space-x-2 bg-slate-900 border border-slate-800 rounded-full px-3 py-1.5 shadow-sm">
            <span class="text-xs text-slate-400 font-medium mr-1">${msg.get("common.language")}:</span>
            <a href="?lang=en" class="px-2.5 py-0.5 rounded-full text-xs font-semibold transition ${"en".equals(currentLang) ? "bg-indigo-600 text-white shadow-sm" : "text-slate-400 hover:text-white"}">EN</a>
            <a href="?lang=km" class="px-2.5 py-0.5 rounded-full text-xs font-semibold transition ${"km".equals(currentLang) ? "bg-indigo-600 text-white shadow-sm" : "text-slate-400 hover:text-white"}">ខ្មែរ</a>
        </div>
    </header>

    <main class="w-full max-w-lg mx-auto px-6 py-6">
        <div class="bg-slate-900/90 backdrop-blur-xl border border-slate-800 rounded-3xl p-8 shadow-2xl shadow-indigo-950/40">
            <div class="text-center mb-6">
                <h1 class="text-2xl font-bold text-white tracking-tight">${msg.get("register.title")}</h1>
                <p class="text-sm text-slate-400 mt-2">${msg.get("register.alreadyHaveAccount")} <a href="/login" class="text-indigo-400 hover:text-indigo-300 font-medium underline underline-offset-4">${msg.get("register.loginHere")}</a></p>
            </div>

            @if (errorMessage != null)
                <div class="mb-6 p-4 rounded-xl bg-red-500/10 border border-red-500/30 text-red-400 text-sm flex items-center space-x-3">
                    <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                    <span>${errorMessage}</span>
                </div>
            @endif

            <form action="/register" method="post" class="space-y-5">
                <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
                <div class="rounded-2xl border border-slate-800 bg-slate-950/40 p-5 space-y-4">
                    <h2 class="text-sm font-semibold text-white">${msg.get("register.personalDetails")}</h2>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div>
                        <label for="givenName" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("register.givenName")}</label>
                        <input type="text" id="givenName" name="givenName" value="${givenName}" autocomplete="given-name" required class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                    </div>
                    <div>
                        <label for="familyName" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("register.familyName")}</label>
                        <input type="text" id="familyName" name="familyName" value="${familyName}" autocomplete="family-name" required class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                    </div>
                    </div>
                </div>

                <div class="rounded-2xl border border-slate-800 bg-slate-950/40 p-5 space-y-4">
                    <h2 class="text-sm font-semibold text-white">${msg.get("register.accountDetails")}</h2>
                    <div>
                        <label for="username" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("register.username")}</label>
                        <input type="text" id="username" name="username" value="${username}" autocomplete="username" required class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                    </div>
                    <div>
                        <label for="email" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("register.email")}</label>
                        <input type="email" id="email" name="email" value="${email}" autocomplete="email" required class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                    </div>
                </div>

                <div class="rounded-2xl border border-slate-800 bg-slate-950/40 p-5 space-y-4">
                    <h2 class="text-sm font-semibold text-white">${msg.get("register.passwords")}</h2>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div>
                        <label for="password" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("register.password")}</label>
                        <div class="relative">
                            <input type="password" id="password" name="password" required autocomplete="new-password" class="w-full px-4 py-3 pr-12 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                            <button type="button" data-password-toggle data-show-label="${msg.get("password.show")}" data-hide-label="${msg.get("password.hide")}" aria-label="${msg.get("password.show")}" aria-controls="password" aria-pressed="false" class="absolute inset-y-0 right-0 flex items-center px-4 text-slate-400 hover:text-white focus:outline-none focus:text-white">
                                <svg data-eye aria-hidden="true" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M2.25 12s3.5-6.25 9.75-6.25S21.75 12 21.75 12 18.25 18.25 12 18.25 2.25 12 2.25 12Z"/><circle cx="12" cy="12" r="2.75" stroke-width="1.8"/></svg>
                                <svg data-eye-off aria-hidden="true" class="hidden h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="m3 3 18 18M10.58 10.59a2 2 0 0 0 2.83 2.83"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M9.88 5.24A10.7 10.7 0 0 1 12 5c6.25 0 9.75 7 9.75 7a16.2 16.2 0 0 1-3.1 3.87M6.18 6.18C3.62 7.83 2.25 12 2.25 12s3.5 7 9.75 7a10.7 10.7 0 0 0 4.22-.87"/></svg>
                            </button>
                        </div>
                    </div>
                    <div>
                        <label for="confirmPassword" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("register.confirmPassword")}</label>
                        <div class="relative">
                            <input type="password" id="confirmPassword" name="confirmPassword" required autocomplete="new-password" class="w-full px-4 py-3 pr-12 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                            <button type="button" data-password-toggle data-show-label="${msg.get("password.show")}" data-hide-label="${msg.get("password.hide")}" aria-label="${msg.get("password.show")}" aria-controls="confirmPassword" aria-pressed="false" class="absolute inset-y-0 right-0 flex items-center px-4 text-slate-400 hover:text-white focus:outline-none focus:text-white">
                                <svg data-eye aria-hidden="true" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M2.25 12s3.5-6.25 9.75-6.25S21.75 12 21.75 12 18.25 18.25 12 18.25 2.25 12 2.25 12Z"/><circle cx="12" cy="12" r="2.75" stroke-width="1.8"/></svg>
                                <svg data-eye-off aria-hidden="true" class="hidden h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="m3 3 18 18M10.58 10.59a2 2 0 0 0 2.83 2.83"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M9.88 5.24A10.7 10.7 0 0 1 12 5c6.25 0 9.75 7 9.75 7a16.2 16.2 0 0 1-3.1 3.87M6.18 6.18C3.62 7.83 2.25 12 2.25 12s3.5 7 9.75 7a10.7 10.7 0 0 0 4.22-.87"/></svg>
                            </button>
                        </div>
                    </div>
                    </div>
                </div>

                <div class="rounded-2xl border border-slate-800 bg-slate-950/40 p-5">
                    <label for="locale" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("register.locale")}</label>
                    <select id="locale" name="locale" class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                        @if ("km".equals(locale))
                            <option value="km" selected>${msg.get("common.khmer")}</option>
                            <option value="en">${msg.get("common.english")}</option>
                        @else
                            <option value="km">${msg.get("common.khmer")}</option>
                            <option value="en" selected>${msg.get("common.english")}</option>
                        @endif
                    </select>
                </div>

                <button type="submit" class="w-full mt-4 py-3 bg-gradient-to-r from-indigo-500 to-indigo-600 hover:from-indigo-600 hover:to-indigo-700 text-white font-semibold rounded-xl text-sm shadow-lg shadow-indigo-600/30 transition">
                    ${msg.get("register.submit")}
                </button>
            </form>
            <script>
                document.querySelectorAll("[data-password-toggle]").forEach(button => {
                    const input = document.getElementById(button.getAttribute("aria-controls"));
                    button.addEventListener("click", () => {
                        const showPassword = input.type === "password";
                        input.type = showPassword ? "text" : "password";
                        button.setAttribute("aria-pressed", String(showPassword));
                        button.setAttribute("aria-label", showPassword ? button.dataset.hideLabel : button.dataset.showLabel);
                        button.querySelector("[data-eye]").classList.toggle("hidden", showPassword);
                        button.querySelector("[data-eye-off]").classList.toggle("hidden", !showPassword);
                    });
                });
            </script>
        </div>
    </main>

    <footer class="w-full text-center py-6 text-xs text-slate-600">
        &copy; 2026 ISTAD ITE Platform. All rights reserved.
    </footer>
</body>
</html>
