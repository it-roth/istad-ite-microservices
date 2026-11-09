# IAM Service

The Identity and Access Management (IAM) service is the platform's central service for user identity, authentication, and access control. It provides browser-based sign-in and profile management, OAuth 2.0 / OpenID Connect authorization, and a protected REST API for managing users, roles, and permissions.

## What it provides

- Local username/password registration and sign-in.
- Federated sign-in with Google, GitHub, and Facebook, plus the service's Telegram login flow.
- OAuth 2.0 authorization and token issuance, OpenID Connect, consent management, and dynamic OAuth client registration.
- User profile and password management, linked identity management, and a page for reviewing and revoking connected application consent.
- Role-based and permission-based access control. Roles can contain permissions, and users can receive roles and direct permissions.
- Administrative API operations for users, roles, and permissions, including enable/disable and lock/unlock actions.
- English and Khmer language support in the JTE web pages.

## Architecture

The service is a Gradle multi-project Spring Boot application. Requests are handled by the web UI or REST API, which call application use cases. The application layer depends on domain ports; persistence and infrastructure modules implement the adapters for storage, password hashing, authentication, and OAuth.

| Module | Responsibility |
| --- | --- |
| `iam-domain/iam-domain-core` | IAM entities, value objects, and domain rules; framework-independent |
| `iam-domain/iam-application-service` | Use cases, command/result DTOs, and input/output ports |
| `iam-persistence` | JPA entities, repositories, persistence adapters, and Liquibase migrations |
| `iam-infrastructure` | Spring Security, Authorization Server, JWT signing, federated login, and security adapters |
| `iam-rest-api` | REST controllers, request validation, mapping, and API error responses |
| `iam-web` | JTE pages, browser controllers, and localization bundles |
| `iam-main` | Spring Boot application assembly, runtime configuration, and service entry point |

## Main routes

The default local service URL and OAuth issuer are `http://localhost:9000`.

| Route | Purpose | Access |
| --- | --- | --- |
| `/login`, `/register` | Browser sign-in and registration | Public |
| `/` | Signed-in user dashboard | Sign-in required |
| `/profile/edit`, `/profile/password` | Edit profile and change password | Sign-in required |
| `/profile/accounts`, `/profile/apps` | Manage linked identities and connected-app consent | Sign-in required |
| `/api/auth/register` | Register a user through the REST API | Public |
| `/api/me` | Read or update the signed-in user's profile | Bearer access token |
| `/api/me/password` | Change the signed-in user's password | Bearer access token |
| `/api/me/identities` | List or unlink the signed-in user's linked identities | Bearer access token |
| `/api/users`, `/api/roles`, `/api/permissions` | Administrative IAM management | Bearer token with required permission |
| `/connect/register` | Dynamically register an OAuth client | Bearer token with `client.create` scope |
| `/actuator/health` | Service health endpoint | See runtime/management configuration |

The Authorization Server exposes its OAuth and OpenID Connect endpoints at the configured issuer. Use its discovery metadata to find the active endpoint URLs and supported capabilities rather than hard-coding them in clients. OIDC scopes include `openid`, `profile`, `email`, `phone`, and `address`.

REST endpoints other than `/api/auth/register` require a bearer access token. Administrative endpoints also check fine-grained authorities such as `user:read`, `role:create`, and `permission:update`. List endpoints support pagination with `page` and `size` (defaults: `0` and `20`); supported sort fields are allow-listed by resource.

## Configuration

Set configuration in the process environment or a local `.env` file. Do not commit credentials, private signing keys, or production secrets.

| Variable | Required | Purpose |
| --- | --- | --- |
| `SPRING_DATASOURCE_URL` | No | PostgreSQL JDBC URL; defaults to `jdbc:postgresql://localhost:9543/db_oauth2` |
| `SPRING_DATASOURCE_USERNAME` | Yes | PostgreSQL username |
| `SPRING_DATASOURCE_PASSWORD` | Yes | PostgreSQL password |
| `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET` | Yes | Google sign-in credentials |
| `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET` | Yes | GitHub sign-in credentials |
| `FACEBOOK_CLIENT_ID`, `FACEBOOK_CLIENT_SECRET` | Yes | Facebook sign-in credentials |
| `TELEGRAM_BOT_TOKEN`, `TELEGRAM_BOT_USERNAME` | Yes | Telegram login bot credentials |
| `TELEGRAM_PUBLIC_BASE_URL` | No | Public callback base URL for Telegram; has a development default |
| `IAM_DCR_CLIENT_SECRET` | Yes | Secret for the initial Dynamic Client Registration client |
| `IAM_DCR_CLIENT_ID` | No | Initial registration client ID; defaults to `initial-client` |
| `IAM_JWK_PRIVATE_KEY` | Production | RSA PKCS#8 PEM private key used to sign tokens |
| `SPRING_SECURITY_OAUTH2_AUTHORIZATIONSERVER_ISSUER` | No | Public issuer URL; defaults to `http://localhost:9000` |

The application imports optional `.env` files from its working directory and the repository-level microservices directory. It also has optional Consul configuration integration. Social-login redirect URIs must be registered with each provider and match `{baseUrl}/login/oauth2/code/{registrationId}`.

On a new database, Liquibase creates IAM and OAuth Authorization Server tables and seeds the `ADMIN` and `USER` roles and built-in permissions. The initial DCR client is also inserted at first startup; provide its secret before starting against a new database. For development, an RSA signing key is generated if `IAM_JWK_PRIVATE_KEY` is absent. Configure the same stable private key for every production instance so tokens continue to verify after restarts and across replicas; the key is mandatory outside the `dev` profile.

## Run locally

From the repository root in PowerShell, configure the required environment variables and start the application:

```powershell
.\gradlew.bat -p microservices :iam-service:iam-main:bootRun
```

The web pages are available at `http://localhost:9000`. Select English or Khmer with `?lang=en` or `?lang=km`; the selection is stored in the `APP_LOCALE` cookie.

## Test

Run the IAM application and infrastructure tests from the repository root:

```powershell
.\gradlew.bat -p microservices :iam-service:iam-infrastructure:test :iam-service:iam-main:test
```

The tests cover IAM use cases and OAuth consent behavior, including a persistence-adapter integration test. A Postman collection for API requests is available at `postman/iam-service.postman_collection.json`.
