@param kh.edu.istad.platform.iam.web.i18n.I18nMessages msg
@param String currentLang = "en"
@param String botUsername
@param String telegramAuthUrl

<!DOCTYPE html>
<html lang="${currentLang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg.get("telegram.title")} - ISTAD IAM</title>
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

    <main class="w-full max-w-md mx-auto px-6 py-8">
        <div class="bg-slate-900/90 backdrop-blur-xl border border-slate-800 rounded-3xl p-8 shadow-2xl shadow-indigo-950/40 text-center">
            <svg class="w-16 h-16 mx-auto mb-4" viewBox="0 0 240 240" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="120" cy="120" r="120" fill="#24A1DE"/>
                <path d="M182.5 68.5L51.5 119C42.5 122.5 42.6 127.8 50 130L83.5 140.5L161.5 91.5C165.2 89.2 168.5 90.5 165.8 93L102.6 150L99.8 190.5C103.8 190.5 105.5 188.7 107.8 186.5L127 167.8L167 197.3C174.3 201.3 179.6 199.3 181.4 190.6L207.6 67.5C210.3 56.7 203.4 51.8 196.5 54.8L182.5 68.5Z" fill="white"/>
            </svg>

            <h1 class="text-xl font-bold text-white mb-2">${msg.get("telegram.title")}</h1>
            <p class="text-xs text-slate-400 mb-6">${msg.get("telegram.description")} <strong>@${botUsername}</strong></p>

            <div class="relative my-6">
                <div class="absolute inset-0 flex items-center"><div class="w-full border-t border-slate-800"></div></div>
                <div class="relative flex justify-center text-xs uppercase"><span class="bg-slate-900 px-3 text-slate-500 font-medium">${msg.get("telegram.widgetPrompt")}</span></div>
            </div>

            <div class="min-h-[48px] flex justify-center items-center">
                <script async src="https://telegram.org/js/telegram-widget.js?22"
                        data-telegram-login="${botUsername}"
                        data-size="large"
                        data-auth-url="${telegramAuthUrl}"
                        data-request-access="write"></script>
            </div>

            <div class="mt-6 pt-4 border-t border-slate-800 text-center">
                <a href="/login" class="text-xs text-slate-400 hover:text-white transition">&larr; ${msg.get("telegram.backToSignIn")}</a>
            </div>
        </div>
    </main>

    <footer class="w-full text-center py-6 text-xs text-slate-600">
        &copy; 2026 ISTAD ITE Platform. All rights reserved.
    </footer>
</body>
</html>
