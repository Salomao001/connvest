# CLAUDE.md

Keep the design and interface as it currently is and don't change the standard.

Always generate a summary of what you did after you finish processing.

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**ConnInvest** is a social networking platform connecting founders, startups, investors, advisors, and co-founders. It's a monorepo with an Angular 21 frontend and Spring Boot 4 backend.

## Commands

### Frontend (`frontend/`)
```bash
npm start          # Dev server on port 4200
npm run build      # Production build
npm test           # Vitest unit tests
npm run watch      # Build in watch mode
```

### Backend (`backend/`)
```bash
mvn spring-boot:run    # Dev server on port 8080
mvn test               # Run JUnit tests
mvn clean package      # Build JAR
```

### Running a single frontend test
```bash
npx vitest run --reporter=verbose src/app/path/to/file.spec.ts
```

## Architecture

### Backend (Spring Boot 4, Java 21)

Package root: `com.conninvest.backend`

- **controller/** — REST endpoints. Each entity has its own controller (`UserController`, `StartupController`, `PostController`, `ConnectionController`, `SearchController`, `StartupInvitationController`).
- **model/** — JPA entities persisted to in-memory H2. Key entities: `User`, `Startup`, `Post`, `ConnectionRequest`, `StartupMembership`, `StartupInvitation`, `Metric`.
- **repository/** — Spring Data JPA repositories (one per entity).
- **dto/** — Response DTOs: `AuthResponse`, `UserStartupDTO`, `StartupMemberDTO`, `SearchResultDTO`.
- **config/DataSeeder** — Seeds development data on startup.

Key design decisions:
- **Role-based access**: `StartupMembership` models team relationships with roles (Owner, Admin, Editor, Viewer, Advisor, Co-founder). Permissions are checked inline in controllers.
- **Metric visibility**: `StartupController.visibleStartupFor()` gates private metrics to members only.
- **No Spring Security**: Authentication is a manual email/password check returning an `AuthResponse`. The frontend stores session in `localStorage`.
- CORS is fully open (`allowedOrigins("*")`).
- H2 console accessible at `http://localhost:8080/h2-console` (credentials: `sa` / `password`).

### Frontend (Angular 21, TypeScript)

Source root: `frontend/src/app/`

- **services/** — All API calls are centralized here (`auth.service.ts`, `user.service.ts`, `startup.service.ts`, `post.service.ts`, `search.service.ts`, `connection.service.ts`). Base URL is hardcoded to `http://localhost:8080/api`.
- **components/** — Standalone Angular components per route (no NgModules). Key components: `feed/`, `discover/`, `profile-user/`, `profile-startup/`, `create-startup/`, `co-founder-match/`, `proposals/`.
- **app.routes.ts** — Route definitions with `auth.guard.ts` protecting authenticated routes.
- **app.config.ts** — Angular providers and global configuration.

Key design decisions:
- Angular standalone components with signals for reactive state.
- RxJS `tap` operators used for side effects (e.g., persisting auth state after login).
- Session persistence via `localStorage` — auth guard reads from there.
- Styling: Tailwind CSS 3 with a custom theme (primary blue `#0066ff`), configured in `tailwind.config.js`. SCSS is the preprocessor.
- Tests use Vitest + JSDOM (`*.spec.ts` pattern).

### Data Model Relationships

```
User ←→ Startup       (Many-to-Many via StartupMembership with role)
User ←→ User          (ConnectionRequest for invites)
Startup ← Post        (Feed posts authored by users or startups)
Startup → StartupInvitation  (Job/role invitations sent to users)
```

## Development Environment

Both servers must run simultaneously for full-stack development:
1. Backend on `http://localhost:8080`
2. Frontend on `http://localhost:4200`

The database is in-memory H2 — all data resets on backend restart. `DataSeeder` re-populates it automatically.