@param kh.edu.istad.platform.iam.web.i18n.I18nMessages msg
@param String currentLang = "en"
@param kh.edu.istad.platform.iam.domain.dto.UserProfileResult user
@param String successMessage = null
@param String errorMessage = null
@param String submittedEmail = null
@param org.springframework.security.web.csrf.CsrfToken csrfToken

<!DOCTYPE html>
<html lang="${currentLang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg.get("profile.edit")} - ISTAD IAM</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&family=Kantumruy+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Plus Jakarta Sans', 'Kantumruy Pro', sans-serif; }
        select { appearance: none; color-scheme: dark; }
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
            <a href="/profile/edit" class="text-indigo-400 font-semibold">${msg.get("profile.edit")}</a>
            <a href="/profile/password" class="hover:text-white transition">${msg.get("profile.changePassword")}</a>
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

    <main class="w-full max-w-5xl mx-auto px-6 py-8 flex-1">
        <div class="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 md:p-8 shadow-2xl shadow-indigo-950/30">
            <div class="flex items-center justify-between mb-8 pb-6 border-b border-slate-800">
                <div>
                    <h1 class="text-2xl font-bold text-white tracking-tight">${msg.get("profile.edit")}</h1>
                    <p class="text-sm text-slate-400 mt-1">${msg.get("profile.details")}</p>
                </div>
                <a href="/" class="text-xs font-semibold text-slate-400 hover:text-white flex items-center space-x-1 transition">
                    <span>&larr;</span>
                    <span>${msg.get("common.back")}</span>
                </a>
            </div>

            @if (successMessage != null)
                <div class="mb-6 p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-sm flex items-center space-x-3">
                    <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
                    <span>${msg.get("profile.updateSuccess")}</span>
                </div>
            @endif

            @if (errorMessage != null)
                <div class="mb-6 p-4 rounded-xl bg-red-500/10 border border-red-500/30 text-red-400 text-sm flex items-center space-x-3">
                    <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                    <span>${msg.get(errorMessage)}</span>
                </div>
            @endif

            <form action="/profile/edit" method="post" class="space-y-6">
                <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
                <section class="rounded-2xl border border-slate-800 bg-slate-950/40 p-5 md:p-6">
                    <div class="mb-4">
                        <h2 class="text-sm font-bold text-white">${msg.get("profile.accountInformation")}</h2>
                        <p class="mt-1 text-xs text-slate-400">${msg.get("profile.accountInformationHint")}</p>
                    </div>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div class="rounded-xl border border-slate-800 bg-slate-950/70 px-4 py-3">
                            <span class="block text-xs font-semibold uppercase tracking-wider text-slate-500">${msg.get("register.username")}</span>
                            <span class="mt-1 block text-sm text-slate-200">${user.getUsername()}</span>
                        </div>
                        <div>
                            <label for="email" class="block text-xs font-semibold uppercase tracking-wider text-slate-300 mb-2">${msg.get("register.email")}</label>
                            <input type="email" id="email" name="email" value="${submittedEmail != null ? submittedEmail : user.getEmail()}" autocomplete="email" maxlength="255" required class="w-full rounded-xl border border-slate-700/80 bg-slate-950/80 px-4 py-3 text-sm text-white transition placeholder:text-slate-500 focus:border-indigo-500 focus:outline-none focus:ring-4 focus:ring-indigo-500/10">
                            <p class="mt-2 text-xs leading-relaxed text-slate-500">${msg.get("profile.emailEditHint")}</p>
                        </div>
                        <div class="sm:col-span-2 flex items-center gap-2 text-xs">
                            @if (java.lang.Boolean.TRUE.equals(user.getEmailVerified()))
                                <span class="h-2 w-2 rounded-full bg-emerald-400"></span>
                                <span class="text-emerald-300">${msg.get("profile.emailVerified")}</span>
                            @else
                                <span class="h-2 w-2 rounded-full bg-amber-400"></span>
                                <span class="text-amber-300">${msg.get("profile.emailNotVerified")}</span>
                            @endif
                        </div>
                    </div>
                </section>

                <section class="rounded-2xl border border-slate-800 bg-slate-950/40 p-5 md:p-6">
                    <div class="mb-5">
                        <h2 class="text-sm font-bold text-white">${msg.get("edit.personalInformation")}</h2>
                        <p class="mt-1 text-xs text-slate-400">${msg.get("profile.details")}</p>
                    </div>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                    <div>
                        <label for="givenName" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.givenName")}</label>
                        <input type="text" id="givenName" name="givenName" value="${user.getGivenName()}" autocomplete="given-name" required class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                    </div>
                    <div>
                        <label for="familyName" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.familyName")}</label>
                        <input type="text" id="familyName" name="familyName" value="${user.getFamilyName()}" autocomplete="family-name" required class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                    </div>
                    <div>
                        <label for="middleName" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.middleName")}</label>
                        <input type="text" id="middleName" name="middleName" value="${user.getMiddleName()}" autocomplete="additional-name" class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                    </div>
                    <div>
                        <label for="nickname" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.nickname")}</label>
                        <input type="text" id="nickname" name="nickname" value="${user.getNickname()}" autocomplete="nickname" class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                    </div>
                </div>
                </section>

                <section class="rounded-2xl border border-slate-800 bg-slate-950/40 p-5 md:p-6">
                    <h2 class="mb-5 text-sm font-bold text-white">${msg.get("edit.contactAndPersonal")}</h2>
                    <div class="grid grid-cols-1 md:grid-cols-3 gap-5">
                    <div>
                        <label for="gender" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.gender")}</label>
                        <div class="relative">
                        <select id="gender" name="gender" class="profile-select w-full appearance-none rounded-xl border border-slate-700/80 bg-slate-950 px-4 py-3 pr-11 text-sm text-white transition hover:border-slate-600 focus:border-indigo-500 focus:outline-none focus:ring-4 focus:ring-indigo-500/10">
                            @if (user.getGender() == null || user.getGender().isBlank())
                                <option value="" selected>${msg.get("common.notSpecified")}</option>
                            @else
                                <option value="">${msg.get("common.notSpecified")}</option>
                            @endif
                            @if ("male".equalsIgnoreCase(user.getGender()))
                                <option value="male" selected>${msg.get("edit.gender.male")}</option>
                            @else
                                <option value="male">${msg.get("edit.gender.male")}</option>
                            @endif
                            @if ("female".equalsIgnoreCase(user.getGender()))
                                <option value="female" selected>${msg.get("edit.gender.female")}</option>
                            @else
                                <option value="female">${msg.get("edit.gender.female")}</option>
                            @endif
                            @if ("other".equalsIgnoreCase(user.getGender()))
                                <option value="other" selected>${msg.get("edit.gender.other")}</option>
                            @else
                                <option value="other">${msg.get("edit.gender.other")}</option>
                            @endif
                        </select>
                        <svg aria-hidden="true" class="pointer-events-none absolute right-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M5.22 7.47a.75.75 0 0 1 1.06 0L10 11.19l3.72-3.72a.75.75 0 1 1 1.06 1.06l-4.25 4.25a.75.75 0 0 1-1.06 0L5.22 8.53a.75.75 0 0 1 0-1.06Z" clip-rule="evenodd"/></svg>
                        </div>
                    </div>
                    <div>
                        <label for="birthdate" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.birthdate")}</label>
                        <input type="date" id="birthdate" name="birthdate" value="${user.getBirthdate() != null ? user.getBirthdate().toString() : ""}" autocomplete="bday" class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                    </div>
                    <div>
                        <label for="phoneNumber" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.phoneNumber")}</label>
                        <input type="tel" id="phoneNumber" name="phoneNumber" value="${user.getPhoneNumber()}" autocomplete="tel" placeholder="${msg.get("edit.phonePlaceholder")}" class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                    </div>
                </div>
                </section>

                <section class="rounded-2xl border border-slate-800 bg-slate-950/40 p-5 md:p-6">
                    <div class="mb-5">
                        <h2 class="text-sm font-bold text-white">${msg.get("edit.onlinePresence")}</h2>
                        <p class="mt-1 text-xs text-slate-400">${msg.get("edit.onlinePresenceHint")}</p>
                    </div>
                    <div class="grid grid-cols-1 md:grid-cols-3 gap-5">
                    <div>
                        <label for="pictureUrl" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.pictureUrl")}</label>
                        <input type="url" id="pictureUrl" name="pictureUrl" value="${user.getPictureUrl()}" autocomplete="url" placeholder="${msg.get("edit.urlPlaceholder")}" class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                    </div>
                    <div>
                        <label for="profileUrl" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.profileUrl")}</label>
                        <input type="url" id="profileUrl" name="profileUrl" value="${user.getProfileUrl()}" placeholder="${msg.get("edit.urlPlaceholder")}" class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                    </div>
                    <div>
                        <label for="websiteUrl" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.websiteUrl")}</label>
                        <input type="url" id="websiteUrl" name="websiteUrl" value="${user.getWebsiteUrl()}" placeholder="${msg.get("edit.urlPlaceholder")}" class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                    </div>
                </div>
                </section>

                <section class="rounded-2xl border border-slate-800 bg-slate-950/40 p-5 md:p-6">
                    <h2 class="mb-5 text-sm font-bold text-white">${msg.get("edit.preferences")}</h2>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                    <div>
                        <label for="locale" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.locale")}</label>
                        <div class="relative">
                        <select id="locale" name="locale" class="profile-select w-full appearance-none rounded-xl border border-slate-700/80 bg-slate-950 px-4 py-3 pr-11 text-sm text-white transition hover:border-slate-600 focus:border-indigo-500 focus:outline-none focus:ring-4 focus:ring-indigo-500/10">
                            @if ("km".equalsIgnoreCase(user.getLocale()))
                                <option value="km" selected>${msg.get("common.khmer")} (${msg.get("common.localeKm")})</option>
                                <option value="en">${msg.get("common.english")} (${msg.get("common.localeEn")})</option>
                            @else
                                <option value="km">${msg.get("common.khmer")} (${msg.get("common.localeKm")})</option>
                                <option value="en" selected>${msg.get("common.english")} (${msg.get("common.localeEn")})</option>
                            @endif
                        </select>
                        <svg aria-hidden="true" class="pointer-events-none absolute right-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M5.22 7.47a.75.75 0 0 1 1.06 0L10 11.19l3.72-3.72a.75.75 0 1 1 1.06 1.06l-4.25 4.25a.75.75 0 0 1-1.06 0L5.22 8.53a.75.75 0 0 1 0-1.06Z" clip-rule="evenodd"/></svg>
                        </div>
                    </div>
                    <div>
                        <label for="zoneinfo" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.zoneinfo")}</label>
                        <input type="text" id="zoneinfo" name="zoneinfo" value="${user.getZoneinfo()}" placeholder="${msg.get("edit.zoneinfoPlaceholder")}" class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                    </div>
                </div>
                </section>

                <section class="rounded-2xl border border-slate-800 bg-slate-950/40 p-5 md:p-6">
                    <h2 class="mb-5 text-sm font-bold text-white">${msg.get("edit.address")}</h2>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div class="md:col-span-2">
                            <label for="addressStreetAddress" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.streetAddress")}</label>
                            <input type="text" id="addressStreetAddress" name="addressStreetAddress" value="${user.getAddressStreetAddress()}" autocomplete="street-address" class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                        </div>
                        <div>
                            <label for="addressLocality" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.locality")}</label>
                            <input type="text" id="addressLocality" name="addressLocality" value="${user.getAddressLocality()}" autocomplete="address-level2" class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                        </div>
                        <div>
                            <label for="addressRegion" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.region")}</label>
                            <input type="text" id="addressRegion" name="addressRegion" value="${user.getAddressRegion()}" autocomplete="address-level1" class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                        </div>
                        <div>
                            <label for="addressPostalCode" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.postalCode")}</label>
                            <input type="text" id="addressPostalCode" name="addressPostalCode" value="${user.getAddressPostalCode()}" autocomplete="postal-code" class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                        </div>
                        <div>
                            <label for="addressCountry" class="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">${msg.get("edit.country")}</label>
                            <input type="text" id="addressCountry" name="addressCountry" value="${user.getAddressCountry()}" autocomplete="country" placeholder="${msg.get("edit.countryPlaceholder")}" maxlength="2" class="w-full px-4 py-3 bg-slate-950/80 border border-slate-700/80 rounded-xl text-white focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition text-sm">
                        </div>
                    </div>
                </section>

                <div class="flex flex-col-reverse sm:flex-row sm:items-center gap-3 pt-4">
                    <button type="submit" class="w-full sm:w-auto px-6 py-3 bg-gradient-to-r from-indigo-500 to-indigo-600 hover:from-indigo-600 hover:to-indigo-700 text-white rounded-xl text-sm font-semibold shadow-lg shadow-indigo-600/30 transition">
                        ${msg.get("profile.save")}
                    </button>
                    <a href="/" class="w-full sm:w-auto px-6 py-3 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl text-sm font-semibold text-center transition">
                        ${msg.get("profile.cancel")}
                    </a>
                </div>
            </form>
        </div>
    </main>

    <footer class="w-full text-center py-6 text-xs text-slate-600 border-t border-slate-800/80">
        &copy; 2026 ISTAD ITE Platform. All rights reserved.
    </footer>
</body>
</html>
