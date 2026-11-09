@param kh.edu.istad.platform.iam.web.i18n.I18nMessages msg
@param String currentLang = "en"
@param Boolean error = false
@param Boolean logout = false
@param Boolean registered = false
@param org.springframework.security.web.csrf.CsrfToken csrfToken

<!DOCTYPE html>
<html lang="${currentLang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg.get("login.title")} - ISTAD IAM</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&family=Kantumruy+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Plus Jakarta Sans', 'Kantumruy Pro', sans-serif;
        }
    </style>
</head>
<body class="bg-slate-950 text-slate-100 min-h-screen flex flex-col justify-between">

    <!-- Top Language Switcher Bar -->
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

    <!-- Main Content -->
    <main class="w-full max-w-md mx-auto px-6 py-8">
        <div class="bg-slate-900/90 backdrop-blur-xl border border-slate-800 rounded-3xl p-8 shadow-2xl shadow-indigo-950/40">
            <div class="text-center mb-8">
                <h1 class="text-2xl font-bold text-white tracking-tight">${msg.get("login.title")}</h1>
                <p class="text-sm text-slate-400 mt-2">${msg.get("login.dontHaveAccount")} <a href="/register" class="text-indigo-400 hover:text-indigo-300 font-medium underline underline-offset-4">${msg.get("login.registerHere")}</a></p>
            </div>

            @if (error)
                <div class="mb-6 p-4 rounded-xl bg-red-500/10 border border-red-500/30 text-red-400 text-sm flex items-center space-x-3">
                    <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                    <span>${msg.get("login.error")}</span>
                </div>
            @endif

            @if (logout)
                <div class="mb-6 p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-sm flex items-center space-x-3">
                    <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
                    <span>${msg.get("login.logoutSuccess")}</span>
                </div>
            @endif

            @if (registered)
                <div class="mb-6 p-4 rounded-xl bg-indigo-500/10 border border-indigo-500/30 text-indigo-400 text-sm flex items-center space-x-3">
                    <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                    <span>${msg.get("register.success")}</span>
                </div>
            @endif

            <!-- Form Login -->
            <form action="/login" method="post" class="space-y-5">
                <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
                <div>
                    <label for="username" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("login.usernameOrEmail")}</label>
                    <input type="text" id="username" name="username" required autofocus autocomplete="username" class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                </div>

                <div>
                    <label for="password" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("login.password")}</label>
                    <div class="relative">
                        <input type="password" id="password" name="password" required autocomplete="current-password" class="w-full px-4 py-3 pr-12 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                        <button type="button" data-password-toggle data-show-label="${msg.get("password.show")}" data-hide-label="${msg.get("password.hide")}" aria-label="${msg.get("password.show")}" aria-controls="password" aria-pressed="false" class="absolute inset-y-0 right-0 flex items-center px-4 text-slate-400 hover:text-white focus:outline-none focus:text-white">
                            <svg data-eye aria-hidden="true" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M2.25 12s3.5-6.25 9.75-6.25S21.75 12 21.75 12 18.25 18.25 12 18.25 2.25 12 2.25 12Z"/><circle cx="12" cy="12" r="2.75" stroke-width="1.8"/></svg>
                            <svg data-eye-off aria-hidden="true" class="hidden h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="m3 3 18 18M10.58 10.59a2 2 0 0 0 2.83 2.83"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M9.88 5.24A10.7 10.7 0 0 1 12 5c6.25 0 9.75 7 9.75 7a16.2 16.2 0 0 1-3.1 3.87M6.18 6.18C3.62 7.83 2.25 12 2.25 12s3.5 7 9.75 7a10.7 10.7 0 0 0 4.22-.87"/></svg>
                        </button>
                    </div>
                </div>

                <button type="submit" class="w-full py-3.5 bg-gradient-to-r from-indigo-500 to-indigo-600 hover:from-indigo-600 hover:to-indigo-700 text-white font-semibold rounded-xl text-sm shadow-lg shadow-indigo-600/30 transition">
                    ${msg.get("login.submit")}
                </button>
            </form>

            <!-- Social Providers Divider -->
            <div class="relative my-8">
                <div class="absolute inset-0 flex items-center"><div class="w-full border-t border-slate-800"></div></div>
                <div class="relative flex justify-center text-xs uppercase"><span class="bg-slate-900 px-3 text-slate-500 font-medium">${msg.get("login.orContinueWith")}</span></div>
            </div>
            <!-- Social Buttons -->
            <div class="grid grid-cols-2 gap-3">
                <a href="/oauth2/authorization/google" class="flex items-center justify-center space-x-2 py-2.5 px-4 bg-slate-950 hover:bg-slate-800/80 border border-slate-800 rounded-xl transition text-xs font-medium text-slate-200">
                    <svg class="w-4 h-4" viewBox="0 0 24 24"><path fill="#EA4335" d="M12 5c1.6 0 3 .6 4.1 1.6l3.1-3.1C17.3 1.7 14.8 1 12 1 7.5 1 3.7 3.6 1.9 7.3l3.7 2.9C6.5 7.4 9 5 12 5z"/><path fill="#4285F4" d="M23.5 12.3c0-.8-.1-1.6-.2-2.3H12v4.5h6.5c-.3 1.5-1.1 2.8-2.4 3.7l3.7 2.9c2.2-2 3.7-5 3.7-8.8z"/><path fill="#FBBC05" d="M5.6 14.8c-.2-.7-.4-1.5-.4-2.3 0-.8.2-1.6.4-2.3L1.9 7.3C.7 9.7 0 12.3 0 15s.7 5.3 1.9 7.7l3.7-2.9z"/><path fill="#34A853" d="M12 23c3.2 0 6-1.1 8-3l-3.7-2.9c-1.1.7-2.5 1.2-4.3 1.2-3 0-5.5-2.4-6.4-5.2L1.9 16C3.7 19.7 7.5 23 12 23z"/></svg>
                    <span>Google</span>
                </a>
                <a href="/oauth2/authorization/github" class="flex items-center justify-center space-x-2 py-2.5 px-4 bg-slate-950 hover:bg-slate-800/80 border border-slate-800 rounded-xl transition text-xs font-medium text-slate-200">
                    <svg class="w-4 h-4 fill-current text-white" viewBox="0 0 24 24"><path fill-rule="evenodd" clip-rule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.53 1.032 1.53 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"/></svg>
                    <span>GitHub</span>
                </a>
                <a href="/oauth2/authorization/facebook" class="flex items-center justify-center space-x-2 py-2.5 px-4 bg-slate-950 hover:bg-slate-800/80 border border-slate-800 rounded-xl transition text-xs font-medium text-slate-200">
                    <svg class="w-4 h-4 fill-current text-[#1877F2]" viewBox="0 0 24 24"><path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/></svg>
                    <span>Facebook</span>
                </a>
                <a href="/login/telegram" class="flex items-center justify-center space-x-2 py-2.5 px-4 bg-slate-950 hover:bg-slate-800/80 border border-slate-800 rounded-xl transition text-xs font-medium text-slate-200">
                    <svg class="w-4 h-4 fill-current text-[#24A1DE]" viewBox="0 0 24 24"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm4.64 6.8c-.15 1.58-.8 5.42-1.13 7.19-.14.75-.42 1-.68 1.03-.58.05-1.02-.38-1.58-.75-.88-.58-1.38-.94-2.23-1.5-.99-.65-.35-1.01.22-1.59.15-.15 2.71-2.48 2.76-2.69a.2.2 0 00-.05-.18c-.06-.05-.14-.03-.21-.02-.09.02-1.49.95-4.22 2.79-.4.27-.76.41-1.08.4-.36-.01-1.04-.2-1.55-.37-.63-.2-1.12-.31-1.08-.66.02-.18.27-.36.74-.55 2.92-1.27 4.86-2.11 5.83-2.51 2.78-1.16 3.35-1.36 3.73-1.36.08 0 .27.02.39.12.1.08.13.19.14.27-.01.06.01.24 0 .38z"/></svg>
                    <span>Telegram</span>
                </a>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="w-full text-center py-6 text-xs text-slate-600">
        &copy; 2026 ISTAD ITE Platform. All rights reserved.
    </footer>
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
</body>
</html>
