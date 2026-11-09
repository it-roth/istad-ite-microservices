@param kh.edu.istad.platform.iam.web.i18n.I18nMessages msg
@param String currentLang = "en"
@param String errorMessage = null
@param org.springframework.security.web.csrf.CsrfToken csrfToken

<!DOCTYPE html>
<html lang="${currentLang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg.get("password.title")} - ISTAD IAM</title>
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
            <a href="/profile/password" class="text-indigo-400 font-semibold">${msg.get("profile.changePassword")}</a>
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

    <main class="w-full max-w-lg mx-auto px-6 py-10 flex-1">
        <div class="bg-slate-900/90 border border-slate-800 rounded-3xl p-8 md:p-10 shadow-2xl shadow-indigo-950/30">
            <div class="flex items-center justify-between mb-8 pb-4 border-b border-slate-800">
                <div>
                    <h1 class="text-xl font-bold text-white tracking-tight">${msg.get("password.title")}</h1>
                    <p class="mt-2 text-sm text-slate-400">${msg.get("password.description")}</p>
                </div>
                <a href="/" class="text-xs font-semibold text-slate-400 hover:text-white flex items-center space-x-1 transition">
                    <span>&larr;</span>
                    <span>${msg.get("common.back")}</span>
                </a>
            </div>

            @if (errorMessage != null)
                <div class="mb-6 p-4 rounded-xl bg-red-500/10 border border-red-500/30 text-red-400 text-sm flex items-center space-x-3">
                    <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                    <span>${errorMessage}</span>
                </div>
            @endif

            <form action="/profile/password" method="post" class="space-y-6">
                <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
                <div>
                    <label for="currentPassword" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("password.current")}</label>
                    <div class="relative">
                        <input type="password" id="currentPassword" name="currentPassword" required autocomplete="current-password" class="w-full px-4 py-3 pr-12 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                        <button type="button" data-password-toggle data-show-label="${msg.get("password.show")}" data-hide-label="${msg.get("password.hide")}" aria-label="${msg.get("password.show")}" aria-controls="currentPassword" aria-pressed="false" class="absolute inset-y-0 right-0 flex items-center px-4 text-slate-400 hover:text-white focus:outline-none focus:text-white">
                            <svg data-eye aria-hidden="true" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M2.25 12s3.5-6.25 9.75-6.25S21.75 12 21.75 12 18.25 18.25 12 18.25 2.25 12 2.25 12Z"/><circle cx="12" cy="12" r="2.75" stroke-width="1.8"/></svg>
                            <svg data-eye-off aria-hidden="true" class="hidden h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="m3 3 18 18M10.58 10.59a2 2 0 0 0 2.83 2.83"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M9.88 5.24A10.7 10.7 0 0 1 12 5c6.25 0 9.75 7 9.75 7a16.2 16.2 0 0 1-3.1 3.87M6.18 6.18C3.62 7.83 2.25 12 2.25 12s3.5 7 9.75 7a10.7 10.7 0 0 0 4.22-.87"/></svg>
                        </button>
                    </div>
                </div>

                <div>
                    <label for="newPassword" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("password.new")}</label>
                    <div class="relative">
                        <input type="password" id="newPassword" name="newPassword" required autocomplete="new-password" class="w-full px-4 py-3 pr-12 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                        <button type="button" data-password-toggle data-show-label="${msg.get("password.show")}" data-hide-label="${msg.get("password.hide")}" aria-label="${msg.get("password.show")}" aria-controls="newPassword" aria-pressed="false" class="absolute inset-y-0 right-0 flex items-center px-4 text-slate-400 hover:text-white focus:outline-none focus:text-white">
                            <svg data-eye aria-hidden="true" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M2.25 12s3.5-6.25 9.75-6.25S21.75 12 21.75 12 18.25 18.25 12 18.25 2.25 12 2.25 12Z"/><circle cx="12" cy="12" r="2.75" stroke-width="1.8"/></svg>
                            <svg data-eye-off aria-hidden="true" class="hidden h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="m3 3 18 18M10.58 10.59a2 2 0 0 0 2.83 2.83"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M9.88 5.24A10.7 10.7 0 0 1 12 5c6.25 0 9.75 7 9.75 7a16.2 16.2 0 0 1-3.1 3.87M6.18 6.18C3.62 7.83 2.25 12 2.25 12s3.5 7 9.75 7a10.7 10.7 0 0 0 4.22-.87"/></svg>
                        </button>
                    </div>
                </div>

                <div>
                    <label for="confirmPassword" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("password.confirm")}</label>
                    <div class="relative">
                        <input type="password" id="confirmPassword" name="confirmPassword" required autocomplete="new-password" class="w-full px-4 py-3 pr-12 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                        <button type="button" data-password-toggle data-show-label="${msg.get("password.show")}" data-hide-label="${msg.get("password.hide")}" aria-label="${msg.get("password.show")}" aria-controls="confirmPassword" aria-pressed="false" class="absolute inset-y-0 right-0 flex items-center px-4 text-slate-400 hover:text-white focus:outline-none focus:text-white">
                            <svg data-eye aria-hidden="true" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M2.25 12s3.5-6.25 9.75-6.25S21.75 12 21.75 12 18.25 18.25 12 18.25 2.25 12 2.25 12Z"/><circle cx="12" cy="12" r="2.75" stroke-width="1.8"/></svg>
                            <svg data-eye-off aria-hidden="true" class="hidden h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="m3 3 18 18M10.58 10.59a2 2 0 0 0 2.83 2.83"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M9.88 5.24A10.7 10.7 0 0 1 12 5c6.25 0 9.75 7 9.75 7a16.2 16.2 0 0 1-3.1 3.87M6.18 6.18C3.62 7.83 2.25 12 2.25 12s3.5 7 9.75 7a10.7 10.7 0 0 0 4.22-.87"/></svg>
                        </button>
                    </div>
                </div>

                <button type="submit" class="w-full py-3.5 bg-gradient-to-r from-indigo-500 to-indigo-600 hover:from-indigo-600 hover:to-indigo-700 text-white font-semibold rounded-xl text-sm shadow-lg shadow-indigo-600/30 transition">
                    ${msg.get("password.submit")}
                </button>
            </form>
        </div>
    </main>
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

    <footer class="w-full text-center py-6 text-xs text-slate-600 border-t border-slate-800/80">
        &copy; 2026 ISTAD ITE Platform. All rights reserved.
    </footer>
</body>
</html>
