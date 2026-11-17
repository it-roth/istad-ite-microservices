@param kh.edu.istad.platform.iam.web.i18n.I18nMessages msg
@param String currentLang = "en"
@param String username = "admin"
@param String email = ""
@param String target = "/account"
@param Boolean error = false
@param Boolean resent = false
@param org.springframework.security.web.csrf.CsrfToken csrfToken = null

<!DOCTYPE html>
<html lang="${currentLang}" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg.get("twofactor.title")} - ISTAD IAM</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex:opsz,wght@9..40,100..1000&family=Kantumruy+Pro:ital,wght@0,100..700;1,100..700&family=Noto+Sans+Khmer:wght@100..900&family=JetBrains+Mono:wght@400;500&display=swap"
    <link href="https://fonts.cdnfonts.com/css/google-sans" rel="stylesheet" rel="stylesheet">
    <script>
        tailwind.config = {
            darkMode: 'class',
            theme: {
                extend: {
                    colors: {
                        border: 'hsl(217.2 32.6% 17.5%)',
                        input: 'hsl(217.2 32.6% 17.5%)',
                        ring: 'hsl(226 70% 60%)',
                        background: 'hsl(224 71% 4%)',
                        foreground: 'hsl(210 40% 98%)',
                        primary: {
                            DEFAULT: '#2563eb',
                            hover: '#1d4ed8',
                            foreground: '#ffffff',
                        },
                        muted: {
                            DEFAULT: 'hsl(217.2 32.6% 14%)',
                            foreground: 'hsl(215 20.2% 65.1%)',
                        },
                        card: {
                            DEFAULT: 'hsl(222.2 47.4% 9.5%)',
                            foreground: 'hsl(210 40% 98%)',
                        },
                    },
                    fontFamily: {
                        sans: ['Google Sans', 'Google Sans Flex', 'Google Sans Text', 'Kantumruy Pro', 'Noto Sans Khmer', 'sans-serif'],
                        mono: ['JetBrains Mono', 'monospace'],
                    },
                },
            },
        };
    </script>
    <style>
        body {
            font-family: 'Google Sans', 'Google Sans Flex', 'Google Sans Text', 'Kantumruy Pro', 'Noto Sans Khmer', sans-serif;
        }

        /* Fix Chrome/Edge autofill turning inputs white on dark theme */
        input:-webkit-autofill,
        input:-webkit-autofill:hover {
            -webkit-text-fill-color: #0f172a !important;
            -webkit-box-shadow: 0 0 0 1000px #f8fafc inset !important;
            box-shadow: 0 0 0 1000px #f8fafc inset !important;
            transition: background-color 5000s ease-in-out 0s;
        }
        .dark input:-webkit-autofill,
        .dark input:-webkit-autofill:hover {
            -webkit-text-fill-color: #f8fafc !important;
            -webkit-box-shadow: 0 0 0 1000px #0b0f19 inset !important;
            box-shadow: 0 0 0 1000px #0b0f19 inset !important;
            caret-color: #f8fafc;
        }

        /* Distinct high-contrast colored border on focus & click for all inputs */
        input[type="text"],
        input[type="password"],
        input[type="email"] {
            transition: all 0.15s ease-in-out;
            border: 1.5px solid #cbd5e1 !important;
        }
        .dark input[type="text"],
        .dark input[type="password"],
        .dark input[type="email"] {
            border: 1.5px solid #334155 !important;
        }

        input[type="text"]:focus,
        input[type="password"]:focus,
        input[type="email"]:focus,
        input[type="text"]:focus-visible,
        input[type="password"]:focus-visible,
        input[type="email"]:focus-visible,
        input[type="text"]:active,
        input[type="password"]:active,
        input[type="email"]:active {
            border-color: #2563eb !important;
            border-width: 2px !important;
            outline: none !important;
            box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.2) !important;
        }
        .dark input[type="text"]:focus,
        .dark input[type="password"]:focus,
        .dark input[type="email"]:focus,
        .dark input[type="text"]:focus-visible,
        .dark input[type="password"]:focus-visible,
        .dark input[type="email"]:focus-visible,
        .dark input[type="text"]:active,
        .dark input[type="password"]:active,
        .dark input[type="email"]:active {
            border-color: #3b82f6 !important;
            border-width: 2px !important;
            outline: none !important;
            box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.3) !important;
        }

        input:-webkit-autofill:focus,
        input:-webkit-autofill:hover,
        input:-webkit-autofill:active {
            -webkit-text-fill-color: #0f172a !important;
            border-color: #2563eb !important;
            border-width: 2px !important;
            box-shadow: 0 0 0 1000px #ffffff inset, 0 0 0 4px rgba(37, 99, 235, 0.2) !important;
        }
        .dark input:-webkit-autofill:focus,
        .dark input:-webkit-autofill:hover,
        .dark input:-webkit-autofill:active {
            -webkit-text-fill-color: #f8fafc !important;
            border-color: #3b82f6 !important;
            border-width: 2px !important;
            box-shadow: 0 0 0 1000px #0b0f19 inset, 0 0 0 4px rgba(59, 130, 246, 0.3) !important;
        }
    </style>
</head>
<body class="min-h-screen bg-slate-950 text-slate-100 flex flex-col antialiased selection:bg-blue-600 selection:text-white">

    <div class="flex-1 flex flex-col lg:flex-row min-h-screen w-full">

        <!-- ========================================================= -->
        <!-- LEFT PANEL: Branded Enterprise Identity Showcase          -->
        <!-- ========================================================= -->
        <div class="lg:w-1/2 bg-[#060913] text-slate-100 flex flex-col justify-between p-8 sm:p-12 lg:p-16 border-b lg:border-b-0 lg:border-r border-slate-800/80 relative overflow-hidden">
            <!-- Ambient top radial glow -->
            <div class="absolute -top-32 -left-32 w-96 h-96 bg-blue-500/15 rounded-full blur-[120px] pointer-events-none"></div>
            <div class="absolute -bottom-32 -right-32 w-96 h-96 bg-blue-500/10 rounded-full blur-[120px] pointer-events-none"></div>

            <!-- Top Header on Left Side -->
            <div class="relative z-10 flex items-center space-x-3">
                <a href="/" class="flex items-center gap-3 transition hover:opacity-90">
                    <img src="https://www.istad.co/_next/image?url=%2Fschool-logo%2Fistad-lms-logo.png&amp;w=256&amp;q=75" alt="ISTAD Logo" class="h-10 w-auto object-contain">
                    <span class="text-sm font-semibold tracking-wide text-slate-200">
                        ISTAD <span class="text-slate-500 font-normal">/</span> ${"km".equals(currentLang) ? "អត្តសញ្ញាណ និងសិទ្ធិចូលប្រើ" : "Identity & Access"}
                    </span>
                </a>
            </div>

            <!-- Center Content: Headline & Mockup Window -->
            <div class="relative z-10 my-8 lg:my-0 space-y-6 max-w-lg">
                <!-- Pill Badge -->
                <div class="inline-flex items-center gap-2 rounded-full border border-slate-800 bg-slate-900/90 px-3 py-1 text-xs text-slate-300 shadow-sm backdrop-blur-sm">
                    <span class="h-2 w-2 rounded-full bg-rose-500 animate-pulse"></span>
                    <span class="font-mono text-[11px] font-medium tracking-wide">OAuth 2.1 · OpenID Connect</span>
                </div>

                <!-- Main Headline -->
                <div class="space-y-3">
                    <h1 class="text-2xl sm:text-3xl lg:text-4xl font-bold tracking-tight text-white leading-tight">
                        ${"km".equals(currentLang) ? "គណនីតែមួយសម្រាប់សេវាកម្ម ISTAD E-Commerce ទាំងអស់" : "One Account for All ISTAD E-Commerce Services"}
                    </h1>
                    <p class="text-xs sm:text-sm text-slate-400 leading-relaxed">
                        ${"km".equals(currentLang) ? "ចូលតែម្តង ដើម្បីទិញទំនិញ គ្រប់គ្រងការបញ្ជាទិញ និងសេវាកម្ម ISTAD ផ្សេងទៀត។ អ្នកគ្រាន់តែត្រូវការគណនីតែមួយគត់ដែលអាចប្រើបាន។" : "Sign in once to shop, manage orders, and access ISTAD e-commerce services. You only need one account to access everything."}
                    </p>
                </div>

                <!-- Mockup Terminal / Token Window -->
                <div class="rounded-xl border border-slate-800 bg-slate-950/80 shadow-2xl backdrop-blur-md overflow-hidden">
                    <!-- Window Titlebar -->
                    <div class="flex items-center justify-between border-b border-slate-800/80 bg-slate-900/60 px-4 py-2.5">
                        <div class="flex items-center space-x-1.5">
                            <span class="h-2.5 w-2.5 rounded-full bg-rose-500/80"></span>
                            <span class="h-2.5 w-2.5 rounded-full bg-amber-500/80"></span>
                            <span class="h-2.5 w-2.5 rounded-full bg-emerald-500/80"></span>
                            <span class="ml-2 font-mono text-[11px] text-slate-400">id_token.json</span>
                        </div>
                        <div class="flex items-center space-x-1.5">    
                            <span class="h-1.5 w-1.5 rounded-full bg-emerald-400"></span>
                            <span class="text-[10px] font-mono text-emerald-400 font-medium">${"km".equals(currentLang) ? "ផ្ទៀងផ្ទាត់រួចរាល់" : "Verified"}</span>
                        </div>
                    </div>

                    <!-- JSON Token Preview -->
                    <pre class="p-4 text-xs font-mono leading-relaxed overflow-x-auto text-slate-300"><code><span class="text-slate-500">{</span>
  <span class="text-sky-400">"iss"</span><span class="text-slate-500">:</span> <span class="text-emerald-400">"http://localhost:39000"</span><span class="text-slate-500">,</span>
  <span class="text-sky-400">"sub"</span><span class="text-slate-500">:</span> <span class="text-emerald-400">"saroth.ecommerce"</span><span class="text-slate-500">,</span>
  <span class="text-sky-400">"aud"</span><span class="text-slate-500">:</span> <span class="text-emerald-400">"istad-ecommerce"</span><span class="text-slate-500">,</span>
  <span class="text-sky-400">"scope"</span><span class="text-slate-500">:</span> <span class="text-emerald-400">"openid profile email"</span><span class="text-slate-500">,</span>
  <span class="text-sky-400">"roles"</span><span class="text-slate-500">:</span> <span class="text-purple-400">["CUSTOMER"]</span>
<span class="text-slate-500">}</span></code></pre>
                </div>

                <!-- Ecosystem Tags -->
                <div class="flex items-center gap-2 pt-2 text-xs text-slate-400 flex-wrap">
                    <span class="text-[11px] text-slate-400 font-medium">${"km".equals(currentLang) ? "ប្រព័ន្ធដែលរួមមាន:" : "Included systems:"}</span>
                    <span class="rounded-md border border-slate-800 bg-slate-900/60 px-2 py-0.5 text-[11px] font-medium text-slate-300">Storefront</span>
                    <span class="rounded-md border border-slate-800 bg-slate-900/60 px-2 py-0.5 text-[11px] font-medium text-slate-300">Orders</span>
                    <span class="rounded-md border border-slate-800 bg-slate-900/60 px-2 py-0.5 text-[11px] font-medium text-slate-300">Business</span>
                    <span class="rounded-md border border-slate-800 bg-slate-900/60 px-2 py-0.5 text-[11px] font-medium text-slate-300">Portal</span>
                </div>
            </div>

            <!-- Bottom Left Footer -->
            <div class="relative z-10 pt-4 text-xs text-slate-400">
                &copy; 2026 ISTAD
            </div>
        </div>

        <!-- ========================================================= -->
        <!-- RIGHT PANEL: 2FA Verification Form                        -->
        <!-- ========================================================= -->
        <div class="lg:w-1/2 bg-white dark:bg-[#070b14] text-slate-900 dark:text-slate-100 flex flex-col justify-between p-6 sm:p-12 lg:p-16 relative">
            
            <!-- Top Right: Language & Theme Switcher -->
            <div class="flex justify-end items-center space-x-2">
                <!-- Language Toggle -->
                <div class="inline-flex h-8 items-center rounded-lg bg-slate-100 dark:bg-slate-900 border border-slate-200 dark:border-slate-800 p-0.5 text-xs text-slate-600 dark:text-slate-400">
                    <a href="?lang=km&username=${username != null ? username : ""}" class="px-2 py-0.5 rounded-md font-medium transition ${"km".equals(currentLang) ? "bg-white dark:bg-slate-800 text-slate-900 dark:text-white shadow-xs font-semibold" : "hover:text-slate-900 dark:hover:text-white"}">ខ្មែរ</a>
                    <a href="?lang=en&username=${username != null ? username : ""}" class="px-2 py-0.5 rounded-md font-medium transition ${"en".equals(currentLang) ? "bg-white dark:bg-slate-800 text-slate-900 dark:text-white shadow-xs font-semibold" : "hover:text-slate-900 dark:hover:text-white"}">EN</a>
                </div>
                
                <!-- Dark / Light Mode Toggle Button -->
                <button type="button" id="theme-toggle" aria-label="Toggle Theme" class="h-8 w-8 rounded-lg border border-slate-200 dark:border-slate-800 bg-slate-100 dark:bg-slate-900 flex items-center justify-center text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white transition">
                    <svg id="theme-icon-sun" class="h-4 w-4 hidden" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <circle cx="12" cy="12" r="4" stroke-width="2"/>
                        <path stroke-linecap="round" stroke-width="2" d="M12 2v2m0 16v2M4.93 4.93l1.41 1.41m11.32 11.32l1.41 1.41M2 12h2m16 0h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41"/>
                    </svg>
                    <svg id="theme-icon-moon" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12.79A9 9 0 1111.21 3 7 7 0 0021 12.79z"/>
                    </svg>
                </button>
            </div>

            <!-- Main 2FA Card Container -->
            <div class="w-full max-w-sm mx-auto my-auto py-8">
                <!-- Title & Subtitle -->
                <div class="text-center mb-6 space-y-2">
                    <h2 class="text-2xl sm:text-3xl font-bold tracking-tight text-slate-900 dark:text-white">
                        ${msg.get("twofactor.title")}
                    </h2>
                    <p class="text-xs sm:text-sm text-slate-500 dark:text-slate-400 leading-relaxed" id="twofactor-subtitle">
                        ${msg.get("twofactor.subtitle")}
                    </p>
                </div>

                <!-- Account Badge with Email -->
                <div class="mb-6 flex justify-center">
                    <div class="inline-flex items-center gap-1.5 rounded-lg border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-900/70 px-3.5 py-1.5 text-xs text-slate-600 dark:text-slate-400">
                        <span>${msg.get("twofactor.signingInAs")}</span>
                        <span class="font-semibold text-slate-900 dark:text-slate-200 font-mono">${username != null ? username : "admin"}${email != null && !email.isBlank() ? " (" + email + ")" : ""}</span>
                    </div>
                </div>

                <!-- Alert: Resent -->
                @if (resent)
                    <div class="mb-5 flex items-start gap-2.5 rounded-lg border border-emerald-200 dark:border-emerald-900/60 bg-emerald-50 dark:bg-emerald-950/40 p-3 text-xs text-emerald-600 dark:text-emerald-400">
                        <svg class="h-4 w-4 shrink-0 mt-0.5 text-emerald-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                        </svg>
                        <span class="font-medium">${"km".equals(currentLang) ? "លេខកូដផ្ទៀងផ្ទាត់ថ្មីត្រូវបានផ្ញើទៅកាន់អ៊ីមែលរបស់អ្នករួចហើយ។" : "A new verification code has been dispatched to your email."}</span>
                    </div>
                @endif

                <!-- Alert: Error -->
                @if (error)
                    <div class="mb-5 flex items-start gap-2.5 rounded-lg border border-red-200 dark:border-red-900/60 bg-red-50 dark:bg-red-950/40 p-3 text-xs text-red-600 dark:text-red-400">
                        <svg class="h-4 w-4 shrink-0 mt-0.5 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <circle cx="12" cy="12" r="10" stroke-width="2"/>
                            <line x1="12" y1="8" x2="12" y2="12" stroke-width="2"/>
                            <line x1="12" y1="16" x2="12.01" y2="16" stroke-width="2"/>
                        </svg>
                        <span class="font-medium">${msg.get("twofactor.error")}</span>
                    </div>
                @endif

                <!-- 2FA Form -->
                <form action="/login/2fa" method="post" class="space-y-5">
                    @if (csrfToken != null)
                        <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
                    @endif
                    <input type="hidden" name="username" value="${username != null ? username : "admin"}">
                    <input type="hidden" name="email" value="${email != null ? email : ""}">
                    <input type="hidden" name="target" value="${target != null ? target : "/account"}">

                    <!-- Code Field -->
                    <div class="space-y-1.5">
                        <label for="twofactor-code" id="twofactor-label" class="text-xs font-semibold text-slate-700 dark:text-slate-300 block">
                            ${msg.get("twofactor.code")}
                        </label>
                        <input
                            type="text"
                            id="twofactor-code"
                            name="code"
                            required
                            autofocus
                            inputmode="numeric"
                            autocomplete="one-time-code"
                            maxlength="6"
                            placeholder="1 2 3 4 5 6"
                            class="w-full h-12 px-4 rounded-lg border border-slate-300 dark:border-slate-800 bg-white dark:bg-slate-900/90 text-lg sm:text-xl font-mono font-semibold text-center tracking-[0.4em] text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent transition"
                        >
                    </div>

                    <!-- Submit Button -->
                    <button
                        type="submit"
                        class="w-full h-10 px-4 rounded-lg bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white font-medium text-sm shadow-sm transition duration-150 flex items-center justify-center gap-2 cursor-pointer focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 dark:focus:ring-offset-slate-900"
                    >
                        <span>${msg.get("twofactor.verify")}</span>
                    </button>
                </form>

                <!-- Resend Email Option -->
                <div class="mt-4 text-center">
                    <form action="/login/2fa/resend" method="post" class="inline">
                        @if (csrfToken != null)
                            <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
                        @endif
                        <input type="hidden" name="username" value="${username != null ? username : "admin"}">
                        <input type="hidden" name="email" value="${email != null ? email : ""}">
                        <input type="hidden" name="target" value="${target != null ? target : "/account"}">
                        <button type="submit" class="text-xs text-blue-600 hover:text-blue-700 dark:text-blue-400 dark:hover:text-blue-300 font-medium transition cursor-pointer hover:underline">
                            ${"km".equals(currentLang) ? "មិនទាន់ទទួលបានលេខកូដ? ផ្ញើឡើងវិញ" : "Didn't receive code? Resend email"}
                        </button>
                    </form>
                </div>

                <!-- Actions: Backup code & Switch account -->
                <div class="mt-6 flex flex-col items-center gap-3 text-xs">
                    <button
                        type="button"
                        id="backup-toggle-btn"
                        class="inline-flex items-center gap-1.5 text-slate-500 hover:text-slate-800 dark:text-slate-400 dark:hover:text-slate-200 transition font-medium cursor-pointer"
                    >
                        <span>🔑</span>
                        <span id="backup-btn-text">${msg.get("twofactor.useBackupCode")}</span>
                    </button>

                    <a
                        href="/login"
                        class="text-slate-500 hover:text-blue-600 dark:text-slate-400 dark:hover:text-blue-400 transition inline-flex items-center gap-1 font-medium"
                    >
                        <span>${msg.get("twofactor.signInDifferentAccount")}</span>
                    </a>
                </div>
            </div>

            <!-- Bottom Spacer/Footer for alignment -->
            <div class="text-center text-xs text-slate-400 dark:text-slate-600">
                &copy; 2026 ISTAD ITE Platform. All rights reserved.
            </div>
        </div>

    </div>

    <!-- Theme and 2FA Interactive Scripts -->
    <script>
        (function() {
            // Theme toggle
            const themeToggleBtn = document.getElementById('theme-toggle');
            const sunIcon = document.getElementById('theme-icon-sun');
            const moonIcon = document.getElementById('theme-icon-moon');
            const html = document.documentElement;

            function applyTheme(theme) {
                if (theme === 'dark') {
                    html.classList.add('dark');
                    sunIcon.classList.remove('hidden');
                    moonIcon.classList.add('hidden');
                } else {
                    html.classList.remove('dark');
                    sunIcon.classList.add('hidden');
                    moonIcon.classList.remove('hidden');
                }
            }

            const storedTheme = localStorage.getItem('theme');
            if (storedTheme) {
                applyTheme(storedTheme);
            } else if (window.matchMedia && window.matchMedia('(prefers-color-scheme: light)').matches) {
                applyTheme('light');
            } else {
                applyTheme('dark');
            }

            if (themeToggleBtn) {
                themeToggleBtn.addEventListener('click', function() {
                    const isDark = html.classList.contains('dark');
                    const nextTheme = isDark ? 'light' : 'dark';
                    localStorage.setItem('theme', nextTheme);
                    applyTheme(nextTheme);
                });
            }

            // Backup code toggle
            let isBackupMode = false;
            const backupBtn = document.getElementById('backup-toggle-btn');
            const backupText = document.getElementById('backup-btn-text');
            const codeInput = document.getElementById('twofactor-code');
            const codeLabel = document.getElementById('twofactor-label');
            const subTitle = document.getElementById('twofactor-subtitle');

            const isKhmer = "${currentLang}" === "km";
            const totpPrompt = isKhmer ? "បញ្ចូលលេខកូដ ៦ ខ្ទង់ដែលបានផ្ញើទៅកាន់អ៊ីមែលរបស់អ្នក" : "Enter the 6-digit verification code sent to your email";
            const backupPrompt = isKhmer ? "បញ្ចូលលេខកូដសង្គ្រោះបន្ទាន់របស់អ្នក (ឧ. backup-xxxx)" : "Enter one of your emergency recovery backup codes (e.g. backup-xxxx)";
            const totpBtnLabel = "${msg.get("twofactor.useTotpCode")}";
            const backupBtnLabel = "${msg.get("twofactor.useBackupCode")}";

            if (backupBtn && codeInput) {
                backupBtn.addEventListener('click', function() {
                    isBackupMode = !isBackupMode;
                    if (isBackupMode) {
                        backupText.textContent = totpBtnLabel;
                        subTitle.textContent = backupPrompt;
                        codeInput.setAttribute('maxlength', '16');
                        codeInput.setAttribute('placeholder', 'backup-xxxx');
                        codeInput.classList.remove('tracking-[0.4em]');
                        codeInput.classList.add('tracking-normal');
                        codeInput.value = '';
                    } else {
                        backupText.textContent = backupBtnLabel;
                        subTitle.textContent = totpPrompt;
                        codeInput.setAttribute('maxlength', '6');
                        codeInput.setAttribute('placeholder', '1 2 3 4 5 6');
                        codeInput.classList.remove('tracking-normal');
                        codeInput.classList.add('tracking-[0.4em]');
                        codeInput.value = '';
                    }
                    codeInput.focus();
                });
            }

            // Dark / Light Theme Toggle & Lang Sync
            const themeBtn = document.getElementById('theme-toggle');
            const sunIcon = document.getElementById('theme-icon-sun');
            const moonIcon = document.getElementById('theme-icon-moon');

            function updateThemeIcons(isDark) {
                if (!sunIcon || !moonIcon) return;
                if (isDark) {
                    sunIcon.classList.remove('hidden');
                    moonIcon.classList.add('hidden');
                } else {
                    sunIcon.classList.add('hidden');
                    moonIcon.classList.remove('hidden');
                }
            }

            try {
                const savedTheme = localStorage.getItem('theme');
                if (savedTheme === 'light') {
                    document.documentElement.classList.remove('dark');
                } else if (savedTheme === 'dark') {
                    document.documentElement.classList.add('dark');
                }
            } catch(e) {}
            updateThemeIcons(document.documentElement.classList.contains('dark'));

            if (themeBtn) {
                themeBtn.addEventListener('click', () => {
                    const isDark = document.documentElement.classList.toggle('dark');
                    updateThemeIcons(isDark);
                    try {
                        const t = isDark ? 'dark' : 'light';
                        localStorage.setItem('theme', t);
                        document.cookie = 'theme=' + t + ';path=/;max-age=31536000;SameSite=Lax';
                    } catch(e) {}
                });
            }

            document.querySelectorAll('a[href*="lang="]').forEach(link => {
                link.addEventListener('click', () => {
                    try {
                        const url = new URL(link.href, window.location.href);
                        const l = url.searchParams.get('lang');
                        if (l) {
                            localStorage.setItem('lang', l);
                            document.cookie = 'APP_LOCALE=' + l + ';path=/;max-age=31536000;SameSite=Lax';
                            document.cookie = 'lang=' + l + ';path=/;max-age=31536000;SameSite=Lax';
                        }
                    } catch(e) {}
                });
            });
        })();
    </script>
</body>
</html>
