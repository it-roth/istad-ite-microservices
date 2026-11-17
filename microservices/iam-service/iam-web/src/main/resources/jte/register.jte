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
<html lang="${currentLang}" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg.get("register.title")} - ISTAD IAM</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&family=Kantumruy+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
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
                            DEFAULT: 'hsl(226 70% 60%)',
                            foreground: 'hsl(210 40% 98%)',
                        },
                        secondary: {
                            DEFAULT: 'hsl(217.2 32.6% 15%)',
                            foreground: 'hsl(210 40% 98%)',
                        },
                        destructive: {
                            DEFAULT: 'hsl(0 62.8% 30.6%)',
                            foreground: 'hsl(210 40% 98%)',
                        },
                        muted: {
                            DEFAULT: 'hsl(217.2 32.6% 14%)',
                            foreground: 'hsl(215 20.2% 65.1%)',
                        },
                        accent: {
                            DEFAULT: 'hsl(217.2 32.6% 15%)',
                            foreground: 'hsl(210 40% 98%)',
                        },
                        card: {
                            DEFAULT: 'hsl(222.2 47.4% 9.5%)',
                            foreground: 'hsl(210 40% 98%)',
                        },
                    },
                    borderRadius: {
                        lg: '0.5rem',
                        md: 'calc(0.5rem - 2px)',
                        sm: 'calc(0.5rem - 4px)',
                    },
                    fontFamily: {
                        sans: ['Plus Jakarta Sans', 'Kantumruy Pro', 'sans-serif'],
                    },
                },
            },
        };
    </script>
    <style>
        body {
            font-family: 'Plus Jakarta Sans', 'Kantumruy Pro', sans-serif;
        }

        /* Fix Chrome/Edge autofill turning inputs white on dark theme */
        input:-webkit-autofill,
        input:-webkit-autofill:hover {
            -webkit-text-fill-color: #f8fafc !important;
            -webkit-box-shadow: 0 0 0 1000px hsl(224 71% 4%) inset !important;
            box-shadow: 0 0 0 1000px hsl(224 71% 4%) inset !important;
            transition: background-color 5000s ease-in-out 0s;
            caret-color: #f8fafc;
        }

        /* Distinct colored focus border and active glow on click */
        input:focus,
        input:focus-visible,
        select:focus {
            border-color: #2563eb !important;
            outline: none !important;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.25) !important;
        }
        .dark input:focus,
        .dark input:focus-visible,
        .dark select:focus {
            border-color: #3b82f6 !important;
            outline: none !important;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.35) !important;
        }

        input:-webkit-autofill:focus,
        input:-webkit-autofill:active {
            -webkit-text-fill-color: #f8fafc !important;
            box-shadow: 0 0 0 1000px hsl(224 71% 4%) inset, 0 0 0 3px rgba(37, 99, 235, 0.25) !important;
            border-color: #2563eb !important;
        }
    </style>
</head>
<body class="bg-background text-foreground min-h-screen flex flex-col justify-between relative selection:bg-primary selection:text-primary-foreground antialiased overflow-x-hidden">

    <!-- Ambient Shadcn Background Gradients & Grid Pattern -->
    <div class="fixed inset-0 -z-10 h-full w-full pointer-events-none">
        <div class="absolute inset-0 bg-[radial-gradient(ellipse_80%_60%_at_50%_-10%,rgba(99,102,241,0.18),rgba(255,255,255,0))]"></div>
        <div class="absolute inset-0 bg-[linear-gradient(to_right,#33415512_1px,transparent_1px),linear-gradient(to_bottom,#33415512_1px,transparent_1px)] bg-[size:3.5rem_3.5rem] [mask-image:radial-gradient(ellipse_70%_60%_at_50%_0%,#000_70%,transparent_100%)]"></div>
        <div class="absolute bottom-0 left-1/2 -translate-x-1/2 w-[700px] h-[300px] bg-blue-500/5 blur-[120px] rounded-full pointer-events-none"></div>
    </div>

    <!-- Header / Navbar -->
    <header class="w-full max-w-6xl mx-auto px-6 py-5 flex justify-between items-center z-10">
        <div class="flex items-center space-x-3">
            <a href="/" class="flex items-center gap-2.5 transition-opacity hover:opacity-90" aria-label="ISTAD IAM">
                <img src="https://www.istad.co/_next/image?url=%2Fschool-logo%2Fistad-lms-logo.png&amp;w=256&amp;q=75" alt="ISTAD LMS" class="h-9 w-auto max-w-[150px] object-contain">
                <span class="text-base font-semibold tracking-tight text-foreground flex items-center gap-1.5">
                    ISTAD<span class="text-xs px-1.5 py-0.5 rounded-md bg-primary/15 text-primary border border-primary/20 font-medium">IAM</span>
                </span>
            </a>
        </div>

        <!-- Language Switcher (Shadcn Segmented Control) -->
        <div class="inline-flex h-9 items-center justify-center rounded-lg bg-card/80 border border-border p-1 text-muted-foreground backdrop-blur-sm shadow-xs">
            <span class="text-xs px-2 font-medium text-muted-foreground hidden sm:inline">${msg.get("common.language")}:</span>
            <a href="?lang=en" class="inline-flex items-center justify-center whitespace-nowrap rounded-md px-2.5 py-1 text-xs font-medium transition-all ${"en".equals(currentLang) ? "bg-primary text-primary-foreground shadow-xs font-semibold" : "text-muted-foreground hover:text-foreground"}">EN</a>
            <a href="?lang=km" class="inline-flex items-center justify-center whitespace-nowrap rounded-md px-2.5 py-1 text-xs font-medium transition-all ${"km".equals(currentLang) ? "bg-primary text-primary-foreground shadow-xs font-semibold" : "text-muted-foreground hover:text-foreground"}">ខ្មែរ</a>
        </div>
    </header>

    <!-- Main Content Area -->
    <main class="w-full max-w-lg mx-auto px-4 sm:px-6 py-6 sm:py-8 z-10">
        <!-- Shadcn Card -->
        <div class="rounded-xl border border-border/80 bg-card/85 text-card-foreground shadow-2xl shadow-black/60 backdrop-blur-xl p-6 sm:p-8 relative overflow-hidden before:absolute before:inset-x-0 before:top-0 before:h-px before:bg-gradient-to-r before:from-transparent before:via-blue-500/40 before:to-transparent">
            
            <!-- Card Header -->
            <div class="text-center mb-6 space-y-1.5">
                <h1 class="text-2xl font-semibold tracking-tight text-foreground">${msg.get("register.title")}</h1>
                <p class="text-xs sm:text-sm text-muted-foreground">
                    ${msg.get("register.alreadyHaveAccount")}
                    <a href="/login" class="text-primary hover:text-primary/90 font-medium underline underline-offset-4 transition-colors">
                        ${msg.get("register.loginHere")}
                    </a>
                </p>
            </div>

            <!-- Error Alert -->
            @if (errorMessage != null)
                <div class="mb-5 flex items-start gap-3 rounded-lg border border-destructive/40 bg-destructive/10 p-3.5 text-xs text-destructive">
                    <svg class="h-4 w-4 shrink-0 mt-0.5 text-destructive" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <circle cx="12" cy="12" r="10" stroke-width="2"/>
                        <line x1="12" y1="8" x2="12" y2="12" stroke-width="2"/>
                        <line x1="12" y1="16" x2="12.01" y2="16" stroke-width="2"/>
                    </svg>
                    <div class="leading-relaxed font-medium">${errorMessage}</div>
                </div>
            @endif

            <!-- Registration Form -->
            <form action="/register" method="post" class="space-y-5">
                <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
                <input type="hidden" name="locale" value="${locale != null ? locale : "en"}">

                <!-- Section: Personal Details -->
                <div class="rounded-lg border border-border/60 bg-background/40 p-4 space-y-3">
                    <div class="text-xs font-semibold text-foreground/90 uppercase tracking-wider">${msg.get("register.personalDetails")}</div>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                        <div class="space-y-1.5">
                            <label for="givenName" class="text-xs font-medium text-foreground leading-none tracking-normal block">${msg.get("register.givenName")}</label>
                            <input type="text" id="givenName" name="givenName" value="${givenName}" autocomplete="given-name" required
                                   class="flex h-10 w-full rounded-md border border-input bg-background/60 px-3 py-2 text-sm text-foreground ring-offset-background placeholder:text-muted-foreground/50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-1 focus-visible:border-transparent transition-colors">
                        </div>
                        <div class="space-y-1.5">
                            <label for="familyName" class="text-xs font-medium text-foreground leading-none tracking-normal block">${msg.get("register.familyName")}</label>
                            <input type="text" id="familyName" name="familyName" value="${familyName}" autocomplete="family-name" required
                                   class="flex h-10 w-full rounded-md border border-input bg-background/60 px-3 py-2 text-sm text-foreground ring-offset-background placeholder:text-muted-foreground/50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-1 focus-visible:border-transparent transition-colors">
                        </div>
                    </div>
                </div>

                <!-- Section: Account Details -->
                <div class="rounded-lg border border-border/60 bg-background/40 p-4 space-y-3">
                    <div class="text-xs font-semibold text-foreground/90 uppercase tracking-wider">${msg.get("register.accountDetails")}</div>
                    <div class="space-y-3">
                        <div class="space-y-1.5">
                            <label for="username" class="text-xs font-medium text-foreground leading-none tracking-normal block">${msg.get("register.username")}</label>
                            <input type="text" id="username" name="username" value="${username}" autocomplete="username" required
                                   class="flex h-10 w-full rounded-md border border-input bg-background/60 px-3 py-2 text-sm text-foreground ring-offset-background placeholder:text-muted-foreground/50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-1 focus-visible:border-transparent transition-colors">
                        </div>
                        <div class="space-y-1.5">
                            <label for="email" class="text-xs font-medium text-foreground leading-none tracking-normal block">${msg.get("register.email")}</label>
                            <input type="email" id="email" name="email" value="${email}" autocomplete="email" required
                                   class="flex h-10 w-full rounded-md border border-input bg-background/60 px-3 py-2 text-sm text-foreground ring-offset-background placeholder:text-muted-foreground/50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-1 focus-visible:border-transparent transition-colors">
                        </div>
                    </div>
                </div>

                <!-- Section: Passwords -->
                <div class="rounded-lg border border-border/60 bg-background/40 p-4 space-y-3">
                    <div class="text-xs font-semibold text-foreground/90 uppercase tracking-wider">${msg.get("register.passwords")}</div>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                        <div class="space-y-1.5">
                            <label for="password" class="text-xs font-medium text-foreground leading-none tracking-normal block">${msg.get("register.password")}</label>
                            <div class="relative">
                                <input type="password" id="password" name="password" required autocomplete="new-password"
                                       class="flex h-10 w-full rounded-md border border-input bg-background/60 px-3 py-2 pr-10 text-sm text-foreground ring-offset-background placeholder:text-muted-foreground/50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-1 focus-visible:border-transparent transition-colors">
                                <button type="button" data-password-toggle
                                        data-show-label="${msg.get("password.show")}"
                                        data-hide-label="${msg.get("password.hide")}"
                                        aria-label="${msg.get("password.show")}"
                                        aria-controls="password"
                                        aria-pressed="false"
                                        class="absolute right-0 top-0 h-10 w-10 flex items-center justify-center text-muted-foreground hover:text-foreground focus:outline-none transition-colors">
                                    <svg data-eye aria-hidden="true" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M2.25 12s3.5-6.25 9.75-6.25S21.75 12 21.75 12 18.25 18.25 12 18.25 2.25 12 2.25 12Z"/>
                                        <circle cx="12" cy="12" r="2.75" stroke-width="1.8"/>
                                    </svg>
                                    <svg data-eye-off aria-hidden="true" class="hidden h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="m3 3 18 18M10.58 10.59a2 2 0 0 0 2.83 2.83"/>
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M9.88 5.24A10.7 10.7 0 0 1 12 5c6.25 0 9.75 7 9.75 7a16.2 16.2 0 0 1-3.1 3.87M6.18 6.18C3.62 7.83 2.25 12 2.25 12s3.5 7 9.75 7a10.7 10.7 0 0 0 4.22-.87"/>
                                    </svg>
                                </button>
                            </div>
                        </div>
                        <div class="space-y-1.5">
                            <label for="confirmPassword" class="text-xs font-medium text-foreground leading-none tracking-normal block">${msg.get("register.confirmPassword")}</label>
                            <div class="relative">
                                <input type="password" id="confirmPassword" name="confirmPassword" required autocomplete="new-password"
                                       class="flex h-10 w-full rounded-md border border-input bg-background/60 px-3 py-2 pr-10 text-sm text-foreground ring-offset-background placeholder:text-muted-foreground/50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-1 focus-visible:border-transparent transition-colors">
                                <button type="button" data-password-toggle
                                        data-show-label="${msg.get("password.show")}"
                                        data-hide-label="${msg.get("password.hide")}"
                                        aria-label="${msg.get("password.show")}"
                                        aria-controls="confirmPassword"
                                        aria-pressed="false"
                                        class="absolute right-0 top-0 h-10 w-10 flex items-center justify-center text-muted-foreground hover:text-foreground focus:outline-none transition-colors">
                                    <svg data-eye aria-hidden="true" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M2.25 12s3.5-6.25 9.75-6.25S21.75 12 21.75 12 18.25 18.25 12 18.25 2.25 12 2.25 12Z"/>
                                        <circle cx="12" cy="12" r="2.75" stroke-width="1.8"/>
                                    </svg>
                                    <svg data-eye-off aria-hidden="true" class="hidden h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="m3 3 18 18M10.58 10.59a2 2 0 0 0 2.83 2.83"/>
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M9.88 5.24A10.7 10.7 0 0 1 12 5c6.25 0 9.75 7 9.75 7a16.2 16.2 0 0 1-3.1 3.87M6.18 6.18C3.62 7.83 2.25 12 2.25 12s3.5 7 9.75 7a10.7 10.7 0 0 0 4.22-.87"/>
                                    </svg>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Submit Button -->
                <button type="submit" class="inline-flex h-10 w-full items-center justify-center rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground shadow-sm transition-all hover:bg-primary/90 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 active:scale-[0.99] cursor-pointer mt-2">
                    ${msg.get("register.submit")}
                </button>
            </form>
        </div>
    </main>

    <!-- Footer -->
    <footer class="w-full text-center py-6 text-xs text-muted-foreground/70 z-10">
        &copy; 2026 ISTAD ITE Platform. All rights reserved.
    </footer>

    <!-- Password visibility toggle script -->
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
