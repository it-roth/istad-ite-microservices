@param kh.edu.istad.platform.iam.web.i18n.I18nMessages msg
@param String currentLang = "en"
@param Boolean error = false
@param Boolean logout = false
@param Boolean registered = false
@param org.springframework.security.web.csrf.CsrfToken csrfToken = null

<!DOCTYPE html>
<html lang="${currentLang}" class="dark h-full">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg.get("login.title")} - ISTAD IAM</title>
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
        html, body {
            height: 100%;
            margin: 0;
            padding: 0;
            font-family: 'Google Sans', 'Google Sans Flex', 'Google Sans Text', 'Kantumruy Pro', 'Noto Sans Khmer', sans-serif;
        }

        @media (min-width: 1024px) {
            html, body {
                overflow: hidden !important;
                height: 100vh !important;
                max-height: 100vh !important;
            }
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
<body class="min-h-screen lg:h-screen lg:overflow-hidden bg-slate-950 text-slate-100 flex flex-col antialiased selection:bg-blue-600 selection:text-white">

    <div class="flex-1 flex flex-col lg:flex-row h-full w-full lg:overflow-hidden">

        <!-- ========================================================= -->
        <!-- LEFT PANEL: Branded Enterprise Identity Showcase          -->
        <!-- ========================================================= -->
        <div class="lg:w-1/2 bg-[#060913] text-slate-100 flex flex-col justify-between p-6 sm:p-8 lg:p-10 border-b lg:border-b-0 lg:border-r border-slate-800/80 relative overflow-hidden h-full">
            <!-- Ambient top radial glow -->
            <div class="absolute -top-32 -left-32 w-96 h-96 bg-blue-500/15 rounded-full blur-[120px] pointer-events-none"></div>
            <div class="absolute -bottom-32 -right-32 w-96 h-96 bg-blue-500/10 rounded-full blur-[120px] pointer-events-none"></div>

            <!-- Top Header on Left Side -->
            <div class="relative z-10 flex items-center space-x-3">
                <a href="/" class="flex items-center gap-3 transition hover:opacity-90">
                    <img src="https://www.istad.co/_next/image?url=%2Fschool-logo%2Fistad-lms-logo.png&amp;w=256&amp;q=75" alt="ISTAD Logo" class="h-9 w-auto object-contain">
                    <span class="text-sm font-semibold tracking-wide text-slate-200">
                        ISTAD <span class="text-slate-500 font-normal">/</span> ${"km".equals(currentLang) ? "អត្តសញ្ញាណ និងសិទ្ធិចូលប្រើ" : "Identity & Access"}
                    </span>
                </a>
            </div>

            <!-- Center Content: Headline & Mockup Window -->
            <div class="relative z-10 my-4 lg:my-0 space-y-4 max-w-lg">
                <!-- Pill Badge -->
                <div class="inline-flex items-center gap-2 rounded-full border border-slate-800 bg-slate-900/90 px-3 py-1 text-xs text-slate-300 shadow-sm backdrop-blur-sm">
                    <span class="h-2 w-2 rounded-full bg-rose-500 animate-pulse"></span>
                    <span class="font-mono text-[11px] font-medium tracking-wide">OAuth 2.1 · OpenID Connect</span>
                </div>

                <!-- Main Headline -->
                <div class="space-y-2">
                    <h1 class="text-xl sm:text-2xl lg:text-3xl font-bold tracking-tight text-white leading-tight">
                        ${"km".equals(currentLang) ? "គណនីតែមួយសម្រាប់សេវាកម្ម ISTAD E-Commerce ទាំងអស់" : "One Account for All ISTAD E-Commerce Services"}
                    </h1>
                    <p class="text-xs text-slate-400 leading-relaxed">
                        ${"km".equals(currentLang) ? "ចូលតែម្តង ដើម្បីទិញទំនិញ គ្រប់គ្រងការបញ្ជាទិញ និងសេវាកម្ម ISTAD ផ្សេងទៀត។ អ្នកគ្រាន់តែត្រូវការគណនីតែមួយគត់ដែលអាចប្រើបាន។" : "Sign in once to shop, manage orders, and access ISTAD e-commerce services. You only need one account to access everything."}
                    </p>
                </div>

                <!-- Mockup Terminal / Token Window -->
                <div class="rounded-xl border border-slate-800 bg-slate-950/80 shadow-2xl backdrop-blur-md overflow-hidden">
                    <!-- Window Titlebar -->
                    <div class="flex items-center justify-between border-b border-slate-800/80 bg-slate-900/60 px-3.5 py-2">
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
                    <pre class="p-3 text-xs font-mono leading-relaxed overflow-x-auto text-slate-300"><code><span class="text-slate-500">{</span>
  <span class="text-sky-400">"iss"</span><span class="text-slate-500">:</span> <span class="text-emerald-400">"http://localhost:39000"</span><span class="text-slate-500">,</span>
  <span class="text-sky-400">"sub"</span><span class="text-slate-500">:</span> <span class="text-emerald-400">"dara.ecommerce"</span><span class="text-slate-500">,</span>
  <span class="text-sky-400">"aud"</span><span class="text-slate-500">:</span> <span class="text-emerald-400">"istad-ecommerce"</span><span class="text-slate-500">,</span>
  <span class="text-sky-400">"scope"</span><span class="text-slate-500">:</span> <span class="text-emerald-400">"openid profile email"</span><span class="text-slate-500">,</span>
  <span class="text-sky-400">"roles"</span><span class="text-slate-500">:</span> <span class="text-purple-400">["CUSTOMER"]</span>
<span class="text-slate-500">}</span></code></pre>
                </div>

                <!-- Ecosystem Tags -->
                <div class="flex items-center gap-2 pt-1 text-xs text-slate-400 flex-wrap">
                    <span class="text-[11px] text-slate-400 font-medium">${"km".equals(currentLang) ? "ប្រព័ន្ធដែលរួមមាន:" : "Included systems:"}</span>
                    <span class="rounded-md border border-slate-800 bg-slate-900/60 px-2 py-0.5 text-[11px] font-medium text-slate-300">Storefront</span>
                    <span class="rounded-md border border-slate-800 bg-slate-900/60 px-2 py-0.5 text-[11px] font-medium text-slate-300">Orders</span>
                    <span class="rounded-md border border-slate-800 bg-slate-900/60 px-2 py-0.5 text-[11px] font-medium text-slate-300">Business</span>
                    <span class="rounded-md border border-slate-800 bg-slate-900/60 px-2 py-0.5 text-[11px] font-medium text-slate-300">Portal</span>
                </div>
            </div>

            <!-- Bottom Left Footer -->
            <div class="relative z-10 pt-2 text-xs text-slate-400">
                &copy; 2026 ISTAD
            </div>
        </div>

        <!-- ========================================================= -->
        <!-- RIGHT PANEL: Clean Auth Portal Form                       -->
        <!-- ========================================================= -->
        <div class="lg:w-1/2 bg-white dark:bg-[#070b14] text-slate-900 dark:text-slate-100 flex flex-col justify-between p-4 sm:p-5 lg:p-6 relative overflow-y-auto lg:overflow-hidden h-full">
            
            <!-- Top Right: Language & Theme Switcher -->
            <div class="flex justify-end items-center space-x-2">
                <!-- Language Toggle -->
                <div class="inline-flex h-7 items-center rounded-lg bg-slate-100 dark:bg-slate-900 border border-slate-200 dark:border-slate-800 p-0.5 text-xs text-slate-600 dark:text-slate-400">
                    <a href="?lang=km" class="px-2 py-0.5 rounded-md font-medium transition ${"km".equals(currentLang) ? "bg-white dark:bg-slate-800 text-slate-900 dark:text-white shadow-xs font-semibold" : "hover:text-slate-900 dark:hover:text-white"}">ខ្មែរ</a>
                    <a href="?lang=en" class="px-2 py-0.5 rounded-md font-medium transition ${"en".equals(currentLang) ? "bg-white dark:bg-slate-800 text-slate-900 dark:text-white shadow-xs font-semibold" : "hover:text-slate-900 dark:hover:text-white"}">EN</a>
                </div>
                
                <!-- Dark / Light Mode Toggle Button -->
                <button type="button" id="theme-toggle" aria-label="Toggle Theme" class="h-7 w-7 rounded-lg border border-slate-200 dark:border-slate-800 bg-slate-100 dark:bg-slate-900 flex items-center justify-center text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white transition">
                    <svg id="theme-icon-sun" class="h-3.5 w-3.5 hidden" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <circle cx="12" cy="12" r="4" stroke-width="2"/>
                        <path stroke-linecap="round" stroke-width="2" d="M12 2v2m0 16v2M4.93 4.93l1.41 1.41m11.32 11.32l1.41 1.41M2 12h2m16 0h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41"/>
                    </svg>
                    <svg id="theme-icon-moon" class="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12.79A9 9 0 1111.21 3 7 7 0 0021 12.79z"/>
                    </svg>
                </button>
            </div>

            <!-- Main Form Card Container -->
            <div class="w-full max-w-sm mx-auto my-auto py-1 sm:py-2">
                <!-- Title & Subtitle -->
                <div class="text-center mb-3 space-y-0.5">
                    <h2 class="text-xl sm:text-2xl font-bold tracking-tight text-slate-900 dark:text-white">
                        ${msg.get("login.title")}
                    </h2>
                    <p class="text-xs text-slate-500 dark:text-slate-400 leading-snug">
                        ${msg.get("login.subtitle")}
                    </p>
                </div>

                <!-- Alert: Error -->
                @if (error)
                    <div class="mb-3 flex items-start gap-2 rounded-lg border border-red-200 dark:border-red-900/60 bg-red-50 dark:bg-red-950/40 p-2.5 text-xs text-red-600 dark:text-red-400">
                        <svg class="h-4 w-4 shrink-0 mt-0.5 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <circle cx="12" cy="12" r="10" stroke-width="2"/>
                            <line x1="12" y1="8" x2="12" y2="12" stroke-width="2"/>
                            <line x1="12" y1="16" x2="12.01" y2="16" stroke-width="2"/>
                        </svg>
                        <span class="font-medium">${msg.get("login.error")}</span>
                    </div>
                @endif

                <!-- Alert: Logout -->
                @if (logout)
                    <div class="mb-3 flex items-start gap-2 rounded-lg border border-emerald-200 dark:border-emerald-900/60 bg-emerald-50 dark:bg-emerald-950/40 p-2.5 text-xs text-emerald-600 dark:text-emerald-400">
                        <svg class="h-4 w-4 shrink-0 mt-0.5 text-emerald-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                        </svg>
                        <span class="font-medium">${msg.get("login.logoutSuccess")}</span>
                    </div>
                @endif

                <!-- Form Login -->
                <form action="/login" method="post" class="space-y-3">
                    @if (csrfToken != null)
                        <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
                    @endif

                    <!-- Username / Email Field -->
                    <div class="space-y-1">
                        <label for="username" class="text-xs font-semibold text-slate-700 dark:text-slate-300 block">
                            ${msg.get("login.usernameOrEmail")}
                        </label>
                        <input
                            type="text"
                            id="username"
                            name="username"
                            required
                            autofocus
                            autocomplete="username"
                            placeholder="username or email"
                            class="w-full h-9 px-3 rounded-lg border-2 border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-900 text-xs text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:border-blue-600 focus:ring-4 focus:ring-blue-500/20 dark:focus:border-blue-500 dark:focus:ring-blue-500/30 transition shadow-xs"
                        >
                    </div>

                    <!-- Password Field -->
                    <div class="space-y-1">
                        <label for="password" class="text-xs font-semibold text-slate-700 dark:text-slate-300 block">
                            ${msg.get("login.password")}
                        </label>
                        <div class="relative">
                            <input
                                type="password"
                                id="password"
                                name="password"
                                required
                                autocomplete="current-password"
                                placeholder="••••••••"
                                class="w-full h-9 px-3 pr-9 rounded-lg border-2 border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-900 text-xs text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:border-blue-600 focus:ring-4 focus:ring-blue-500/20 dark:focus:border-blue-500 dark:focus:ring-blue-500/30 transition shadow-xs"
                            >
                            <button
                                type="button"
                                data-password-toggle
                                data-show-label="${msg.get("password.show")}"
                                data-hide-label="${msg.get("password.hide")}"
                                aria-label="${msg.get("password.show")}"
                                aria-controls="password"
                                aria-pressed="false"
                                class="absolute right-0 top-0 h-9 w-9 flex items-center justify-center text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 focus:outline-none"
                            >
                                <svg data-eye aria-hidden="true" class="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M2.25 12s3.5-6.25 9.75-6.25S21.75 12 21.75 12 18.25 18.25 12 18.25 2.25 12 2.25 12Z"/>
                                    <circle cx="12" cy="12" r="2.75" stroke-width="1.8"/>
                                </svg>
                                <svg data-eye-off aria-hidden="true" class="hidden h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="m3 3 18 18M10.58 10.59a2 2 0 0 0 2.83 2.83"/>
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M9.88 5.24A10.7 10.7 0 0 1 12 5c6.25 0 9.75 7 9.75 7a16.2 16.2 0 0 1-3.1 3.87M6.18 6.18C3.62 7.83 2.25 12 2.25 12s3.5 7 9.75 7a10.7 10.7 0 0 0 4.22-.87"/>
                                </svg>
                            </button>
                        </div>
                    </div>

                    <!-- Remember Me Option -->
                    <div class="flex items-center justify-between text-xs pt-0.5">
                        <label class="flex items-center gap-2 cursor-pointer select-none text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200">
                            <input type="checkbox" name="remember-me" id="remember-me" checked class="h-3.5 w-3.5 rounded border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-900 text-blue-600 focus:ring-blue-500 cursor-pointer">
                            <span class="text-xs">${"km".equals(currentLang) ? "ចងចាំខ្ញុំលើឧបករណ៍នេះ" : "Remember me"}</span>
                        </label>
                    </div>

                    <!-- Submit Button -->
                    <button
                        type="submit"
                        class="w-full h-9 mt-1 bg-blue-600 hover:bg-blue-700 active:scale-[0.99] text-white font-medium rounded-lg text-xs shadow-sm transition flex items-center justify-center cursor-pointer"
                    >
                        ${msg.get("login.submit")}
                    </button>
                </form>

                <!-- Invitation / Registration Notice -->
                <div class="mt-3 text-center text-[11px] text-slate-500 dark:text-slate-400 leading-snug">
                    <p>${msg.get("login.invitationNotice")}</p>
                </div>

                <!-- Social Sign In Divider -->
                <div class="relative my-3 flex items-center justify-center">
                    <div class="absolute inset-0 flex items-center">
                        <span class="w-full border-t border-slate-200 dark:border-slate-800"></span>
                    </div>
                    <span class="relative bg-white dark:bg-[#070b14] px-2 text-[10px] font-medium uppercase tracking-wider text-slate-400">
                        ${msg.get("login.orContinueWith")}
                    </span>
                </div>

                <!-- Social Buttons Grid (All 4 Providers) -->
                <div class="grid grid-cols-2 gap-2">
                    <!-- Google -->
                    <a href="/oauth2/authorization/google" class="inline-flex h-8 items-center justify-center gap-2 rounded-lg border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-900/60 px-3 text-xs font-medium hover:bg-slate-100 dark:hover:bg-slate-800 transition">
                        <svg class="h-3.5 w-3.5 shrink-0" viewBox="0 0 24 24"><path fill="#EA4335" d="M12 5c1.6 0 3 .6 4.1 1.6l3.1-3.1C17.3 1.7 14.8 1 12 1 7.5 1 3.7 3.6 1.9 7.3l3.7 2.9C6.5 7.4 9 5 12 5z"/><path fill="#4285F4" d="M23.5 12.3c0-.8-.1-1.6-.2-2.3H12v4.5h6.5c-.3 1.5-1.1 2.8-2.4 3.7l3.7 2.9c2.2-2 3.7-5 3.7-8.8z"/><path fill="#FBBC05" d="M5.6 14.8c-.2-.7-.4-1.5-.4-2.3 0-.8.2-1.6.4-2.3L1.9 7.3C.7 9.7 0 12.3 0 15s.7 5.3 1.9 7.7l3.7-2.9z"/><path fill="#34A853" d="M12 23c3.2 0 6-1.1 8-3l-3.7-2.9c-1.1.7-2.5 1.2-4.3 1.2-3 0-5.5-2.4-6.4-5.2L1.9 16C3.7 19.7 7.5 23 12 23z"/></svg>
                        <span>Google</span>
                    </a>

                    <!-- GitHub -->
                    <a href="/oauth2/authorization/github" class="inline-flex h-8 items-center justify-center gap-2 rounded-lg border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-900/60 px-3 text-xs font-medium hover:bg-slate-100 dark:hover:bg-slate-800 transition">
                        <svg class="h-3.5 w-3.5 shrink-0 fill-current" viewBox="0 0 24 24"><path fill-rule="evenodd" clip-rule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.53 1.032 1.53 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"/></svg>
                        <span>GitHub</span>
                    </a>

                    <!-- Facebook -->
                    <a href="/oauth2/authorization/facebook" class="inline-flex h-8 items-center justify-center gap-2 rounded-lg border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-900/60 px-3 text-xs font-medium hover:bg-slate-100 dark:hover:bg-slate-800 transition">
                        <svg class="h-3.5 w-3.5 shrink-0" viewBox="0 0 24 24"><path fill="#1877F2" d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/></svg>
                        <span>Facebook</span>
                    </a>

                    <!-- Telegram -->
                    <a href="/login/telegram" class="inline-flex h-8 items-center justify-center gap-2 rounded-lg border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-900/60 px-3 text-xs font-medium hover:bg-slate-100 dark:hover:bg-slate-800 transition">
                        <svg class="h-3.5 w-3.5 shrink-0" viewBox="0 0 24 24"><path fill="#229ED9" d="M12 0C5.37 0 0 5.37 0 12s5.37 12 12 12 12-5.37 12-12S18.63 0 12 0zm5.56 8.16l-1.97 9.28c-.15.65-.53.81-1.08.51l-3-2.21-1.45 1.39c-.16.16-.3.3-.61.3l.21-3.06 5.57-5.03c.24-.22-.05-.34-.38-.13l-6.88 4.33-2.96-.92c-.64-.2-.66-.64.14-.95l11.57-4.46c.54-.19 1.01.13.86.94z"/></svg>
                        <span>Telegram</span>
                    </a>
                </div>
            </div>

            <!-- Bottom Right Footer -->
            <div class="text-center text-[11px] text-slate-400 py-1">
                ISTAD Identity & Access Management
            </div>
        </div>
    </div>

    <!-- Scripts: Theme & Password Visibility -->
    <script>
        // Password visibility toggle
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

        // Dark / Light Theme Toggle & Lang Sync
        const themeBtn = document.getElementById('theme-toggle');
        const sunIcon = document.getElementById('theme-icon-sun');
        const moonIcon = document.getElementById('theme-icon-moon');

        function updateThemeIcons(isDark) {
            if (isDark) {
                sunIcon.classList.remove('hidden');
                moonIcon.classList.add('hidden');
            } else {
                sunIcon.classList.add('hidden');
                moonIcon.classList.remove('hidden');
            }
        }

        // Initialize theme based on localStorage
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

        // Sync language selection to localStorage & cookies
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
    </script>
</body>
</html>
