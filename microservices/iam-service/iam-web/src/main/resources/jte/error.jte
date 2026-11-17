@param Integer status = 500
@param String error = "Internal Server Error"
@param String message = "An unexpected error occurred while processing your request."
@param String currentLang = "en"

<!DOCTYPE html>
<html lang="${currentLang}" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${status != null ? status : 500} - ISTAD IAM</title>
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
<body class="min-h-screen bg-slate-950 text-slate-100 flex flex-col items-center justify-center p-4 antialiased">
    <div class="w-full max-w-md text-center space-y-6">
        <div class="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-red-500/10 border border-red-500/20 text-red-500 shadow-lg shadow-red-500/10">
            <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
            </svg>
        </div>

        <div class="space-y-2">
            <span class="text-xs font-semibold tracking-wider uppercase text-red-400 bg-red-950/60 border border-red-900/60 px-3 py-1 rounded-full">
                Error ${status != null ? status : 500}
            </span>
            <h1 class="text-2xl font-bold text-white tracking-tight pt-2">
                ${error != null ? error : "An Error Occurred"}
            </h1>
            <p class="text-sm text-slate-400 max-w-sm mx-auto leading-relaxed">
                ${message != null ? message : "The requested page encountered an issue. Please try returning to the account overview or login page."}
            </p>
        </div>

        <div class="pt-2 flex flex-col sm:flex-row items-center justify-center gap-3">
            <a href="/login" class="w-full sm:w-auto px-5 py-2.5 rounded-lg bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold shadow-md shadow-blue-600/20 transition">
                Return to Login
            </a>
            <a href="/account" class="w-full sm:w-auto px-5 py-2.5 rounded-lg border border-slate-700 bg-slate-900/80 hover:bg-slate-800 text-slate-200 text-xs font-semibold transition">
                Go to Account
            </a>
        </div>

        <p class="text-xs text-slate-600 pt-6">
            &copy; 2026 ISTAD Identity & Access Management
        </p>
    </div>
</body>
</html>
