@param kh.edu.istad.platform.iam.web.i18n.I18nMessages msg
@param String currentLang = "en"
@param String botUsername
@param String telegramAuthUrl

<!DOCTYPE html>
<html lang="${currentLang}" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg.get("telegram.title")} - ISTAD IAM</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex:opsz,wght@9..40,100..1000&family=Kantumruy+Pro:ital,wght@0,100..700;1,100..700&family=Noto+Sans+Khmer:wght@100..900&display=swap"
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
                        sans: ['Google Sans', 'Google Sans Flex', 'Google Sans Text', 'Kantumruy Pro', 'Noto Sans Khmer', 'sans-serif'],
                    },
                },
            },
        };
    </script>
    <style>
        body { font-family: 'Google Sans', 'Google Sans Flex', 'Google Sans Text', 'Kantumruy Pro', 'Noto Sans Khmer', sans-serif; }
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
    <main class="w-full max-w-md mx-auto px-4 sm:px-6 py-6 sm:py-10 z-10">
        <div class="rounded-xl border border-border/80 bg-card/85 text-card-foreground shadow-2xl shadow-black/60 backdrop-blur-xl p-6 sm:p-8 relative overflow-hidden text-center before:absolute before:inset-x-0 before:top-0 before:h-px before:bg-gradient-to-r before:from-transparent before:via-blue-500/40 before:to-transparent">
            
            <div class="inline-flex items-center justify-center w-14 h-14 rounded-full bg-[#24A1DE]/10 border border-[#24A1DE]/30 text-[#24A1DE] mb-4">
                <svg class="w-7 h-7 fill-[#24A1DE]" viewBox="0 0 24 24"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm4.64 6.8c-.15 1.58-.8 5.42-1.13 7.19-.14.75-.42 1-.68 1.03-.58.05-1.02-.38-1.58-.75-.88-.58-1.38-.94-2.23-1.5-.99-.65-.35-1.01.22-1.59.15-.15 2.71-2.48 2.76-2.69a.2.2 0 00-.05-.18c-.06-.05-.14-.03-.21-.02-.09.02-1.49.95-4.22 2.79-.4.27-.76.41-1.08.4-.36-.01-1.04-.2-1.55-.37-.63-.2-1.12-.31-1.08-.66.02-.18.27-.36.74-.55 2.92-1.27 4.86-2.11 5.83-2.51 2.78-1.16 3.35-1.36 3.73-1.36.08 0 .27.02.39.12.1.08.13.19.14.27-.01.06.01.24 0 .38z"/></svg>
            </div>

            <h1 class="text-xl font-semibold tracking-tight text-foreground mb-1.5">${msg.get("telegram.title")}</h1>
            <p class="text-xs text-muted-foreground mb-6">${msg.get("telegram.description")} <strong class="text-foreground">@${botUsername}</strong></p>

            <div class="relative my-6 flex items-center justify-center">
                <div class="absolute inset-0 flex items-center">
                    <span class="w-full border-t border-border"></span>
                </div>
                <span class="relative bg-card px-2 text-[11px] font-medium uppercase tracking-wider text-muted-foreground">
                    ${msg.get("telegram.widgetPrompt")}
                </span>
            </div>

            <div class="min-h-[48px] flex justify-center items-center py-2">
                <script async src="https://telegram.org/js/telegram-widget.js?22"
                        data-telegram-login="${botUsername}"
                        data-size="large"
                        data-auth-url="${telegramAuthUrl}"
                        data-request-access="write"></script>
            </div>

            <div class="mt-6 pt-4 border-t border-border text-center">
                <a href="/login" class="text-xs text-muted-foreground hover:text-foreground transition-colors inline-flex items-center gap-1 font-medium">
                    &larr; ${msg.get("telegram.backToSignIn")}
                </a>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="w-full text-center py-6 text-xs text-muted-foreground/70 z-10">
        &copy; 2026 ISTAD ITE Platform. All rights reserved.
    </footer>
</body>
</html>
