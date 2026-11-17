# IAM React frontend

This React frontend is packaged by the `iam-web` module and served by Spring Boot at `/` and `/dashboard`.
The JTE layer is used only for the `/login` form; all authenticated screens are React pages.

## Development

From this directory:

```powershell
npm ci
npm run dev
```

Use the Vite URL for frontend-only development. To create the static files used by `iam-web`, run:

```powershell
npm run build
```

The Gradle build does not run this command automatically. Rebuild the frontend manually
after source changes, then restart the IAM service so Spring Boot serves the updated files.
