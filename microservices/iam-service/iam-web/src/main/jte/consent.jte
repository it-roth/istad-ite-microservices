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
<html lang="${currentLang}" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg.get("consent.title")} - ISTAD IAM</title>
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
        body { font-family: 'Plus Jakarta Sans', 'Kantumruy Pro', sans-serif; }
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
            
            <div class="flex items-center space-x-4 mb-6">
                <div class="w-12 h-12 rounded-xl bg-primary/10 border border-primary/30 flex items-center justify-center text-primary text-xl shadow-xs">
                    🔐
                </div>
                <div>
                    <h1 class="text-xl font-semibold tracking-tight text-foreground">${msg.get("consent.title")}</h1>
                    <p class="text-xs text-muted-foreground mt-1">${msg.get("consent.description", clientName != null ? clientName : clientId)}</p>
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
                        <h2 class="text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-3">${msg.get("consent.scopesRequested")}</h2>
                        <div class="space-y-2 bg-background/50 border border-border rounded-lg p-3.5">
                            @for (var s : scopes)
                                <label class="flex items-start space-x-3 text-sm text-foreground/90 cursor-pointer">
                                    <input type="checkbox" name="scope" value="${s}" checked class="mt-1 h-4 w-4 rounded border-border text-primary focus:ring-ring bg-background">
                                    <div>
                                        <span class="font-medium text-foreground text-sm">${s}</span>
                                        <span class="block text-xs text-muted-foreground">${msg.get("consent.scopeDescription", s)}</span>
                                    </div>
                                </label>
                            @endfor
                        </div>
                    </div>
                @endif

                @if (!previouslyApprovedScopes.isEmpty())
                    <div>
                        <h2 class="text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-3">${msg.get("consent.previouslyConsented")}</h2>
                        <div class="space-y-2 bg-background/30 border border-border/60 rounded-lg p-3.5">
                            @for (var s : previouslyApprovedScopes)
                                <label class="flex items-start space-x-3 text-sm text-muted-foreground">
                                    <input type="checkbox" disabled checked class="mt-1 h-4 w-4 rounded border-border text-primary/40 bg-background/50">
                                    <div>
                                        <span class="font-medium text-foreground/75 text-sm">${s}</span>
                                        <span class="block text-xs text-muted-foreground/70">${msg.get("common.alreadyApproved")}</span>
                                    </div>
                                </label>
                            @endfor
                        </div>
                    </div>
                @endif

                <div class="flex items-center space-x-3 pt-2">
                    <button type="submit" class="flex-1 inline-flex h-10 items-center justify-center rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground shadow-sm transition-all hover:bg-primary/90 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 active:scale-[0.99] cursor-pointer">
                        ${msg.get("consent.allow")}
                    </button>
                    <button type="button" onclick="cancelConsent()" class="flex-1 inline-flex h-10 items-center justify-center rounded-md border border-input bg-background/60 px-4 py-2 text-sm font-medium text-foreground shadow-xs transition-colors hover:bg-accent hover:text-accent-foreground active:scale-[0.99] cursor-pointer">
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

    <!-- Footer -->
    <footer class="w-full text-center py-6 text-xs text-muted-foreground/70 z-10">
        &copy; 2026 ISTAD ITE Platform. All rights reserved.
    </footer>
</body>
</html>
