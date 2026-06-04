# AbilityBridge Backend

> Inclusive employment platform — Spring Boot 3.2 · PostgreSQL · JWT · WebSocket

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2.4 (Java 21) |
| Security | Spring Security + JWT (JJWT 0.12) |
| Database | PostgreSQL 15+ |
| Migrations | Flyway |
| Build | Maven 3.9+ |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Real-time | Spring WebSocket (STOMP) |
| Async | Spring @Async + @Scheduled |
| Mapping | MapStruct + Lombok |

---

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL 15+

### 1 — Create the database
```sql
CREATE DATABASE abilitybridge;
CREATE USER abilitybridge WITH PASSWORD 'abilitybridge';
GRANT ALL PRIVILEGES ON DATABASE abilitybridge TO abilitybridge;
```

### 2 — Configure environment
Copy and edit the application config:
```bash
cp src/main/resources/application.yml application-local.yml
```
Or set environment variables:
```bash
export DB_USERNAME=abilitybridge
export DB_PASSWORD=abilitybridge
export JWT_SECRET=YourSuperSecretKeyThatMustBe256BitsLongInProduction!!
export MAIL_USERNAME=your@email.com
export MAIL_PASSWORD=your-mail-password
```

### 3 — Run
```bash
mvn spring-boot:run
```

Flyway will auto-run `V1__initial_schema.sql` and `V2__seed_data.sql` on first start.

### 4 — Access Swagger UI
```
http://localhost:8080/api/v1/swagger-ui.html
```

---

## Architecture

```
com.abilitybridge
├── config/           # Security, WebSocket, OpenAPI, JPA config
├── security/         # JWT filter, JwtUtil, UserDetailsService
├── exception/        # Global exception handler + custom exceptions
├── user/             # FR1: Auth, registration, user management
├── profile/          # FR1: Seeker & employer profiles, accessibility settings
├── skills/           # FR2: Skills assessment, gap analysis, badges, portfolio
├── task/             # FR3: Micro task marketplace + payments
├── job/              # FR4: Job board, applications, smart matching
├── accommodation/    # FR5: Needs, compatibility check, negotiation channel
├── employer/         # FR6: Reality score, inclusivity badges, ratings
├── mentorship/       # FR7: Mentor profiles, requests, sessions
├── messaging/        # FR8: In-app chat, voice messages, interview scheduling
├── notification/     # FR8: Push notification feed
├── reporting/        # FR9: Discrimination reports, employer flagging
└── admin/            # FR10: Analytics dashboard, impact reports
```

Each module follows a strict layered structure:
```
{module}/
  entity/       JPA entities
  repository/   Spring Data JPA repos
  service/      Business logic
  controller/   REST controllers
  dto/          Request/response objects
```

---

## API Endpoints Summary

### Auth (`/auth`)
| Method | Path | Description |
|---|---|---|
| POST | `/auth/register` | Register (SEEKER / EMPLOYER / MENTOR) |
| POST | `/auth/login` | Login → access + refresh tokens |
| POST | `/auth/refresh` | Refresh access token |
| POST | `/auth/logout` | Revoke refresh token |

### Users (`/users`)
| Method | Path | Description |
|---|---|---|
| GET | `/users/me` | Get current user |
| GET | `/users/me/accessibility` | Get accessibility settings |
| PUT | `/users/me/accessibility` | Update accessibility settings |

### Profiles (`/profiles`)
| Method | Path | Description |
|---|---|---|
| PUT | `/profiles/seeker` | Create/update seeker profile |
| GET | `/profiles/seeker/{userId}` | Get seeker profile |
| PATCH | `/profiles/seeker/anonymous-mode` | Toggle anonymous apply |
| PUT | `/profiles/employer` | Create/update employer profile |
| GET | `/profiles/employer/{userId}` | Get employer profile |
| PUT | `/profiles/disability-info` | Save disability info (private) |

### Skills & Learning (`/skills`)
| Method | Path | Description |
|---|---|---|
| POST | `/skills/assessment` | Submit self-assessment |
| GET | `/skills/my-skills` | Get my skills |
| GET | `/skills/gap-analysis` | Get skill gap report |
| GET | `/skills/badges` | Get my badges |
| POST | `/skills/badges` | Award badge after course |
| GET | `/skills/portfolio` | Get portfolio |
| POST | `/skills/portfolio` | Add portfolio item |

### Micro Tasks (`/tasks`)
| Method | Path | Description |
|---|---|---|
| GET | `/tasks` | Browse open tasks |
| POST | `/tasks/post` | Post a micro task |
| POST | `/tasks/{taskId}/apply` | Apply for a task |
| POST | `/tasks/{taskId}/submit` | Submit completed work |
| POST | `/tasks/completions/{id}/approve` | Approve & trigger payment |
| POST | `/tasks/{taskId}/rate` | Rate task partner |

### Jobs (`/jobs`)
| Method | Path | Description |
|---|---|---|
| GET | `/jobs/public` | Search/filter jobs (smart match) |
| GET | `/jobs/public/{jobId}` | View job detail |
| POST | `/jobs/post` | Post a job listing |
| POST | `/jobs/{jobId}/apply` | Apply (CV/video/voice/portfolio) |
| GET | `/jobs/{jobId}/applications` | Employer views applicants |
| PATCH | `/jobs/applications/{id}/status` | Update application status |
| PATCH | `/jobs/{jobId}/close` | Close job listing |

### Accommodation (`/accommodations`)
| Method | Path | Description |
|---|---|---|
| POST | `/accommodations/needs` | Add accommodation need |
| GET | `/accommodations/needs` | Get my needs |
| GET | `/accommodations/compatibility` | Check employer compatibility |
| POST | `/accommodations/negotiations` | Open negotiation channel |
| POST | `/accommodations/negotiations/{id}/agree` | Log agreement |
| GET | `/accommodations/negotiations` | Get my negotiations |

### Employers (`/employers`)
| Method | Path | Description |
|---|---|---|
| GET | `/employers/public/{id}/dashboard` | Public accountability dashboard |
| GET | `/employers/public/{id}/score` | Workplace Reality Score |
| GET | `/employers/public/{id}/ratings` | Community ratings |
| POST | `/employers/{id}/rate` | Submit anonymous rating |
| POST | `/employers/{id}/badges` | Admin: award inclusivity badge |

### Mentorship (`/mentorship`)
| Method | Path | Description |
|---|---|---|
| GET | `/mentorship/mentors` | Search mentors |
| GET | `/mentorship/mentors/{userId}` | Get mentor profile |
| POST | `/mentorship/register-mentor` | Register as mentor |
| PATCH | `/mentorship/my-profile` | Update mentor profile |
| POST | `/mentorship/mentors/{id}/request` | Send mentorship request |
| PATCH | `/mentorship/requests/{id}/respond` | Accept/decline request |
| GET | `/mentorship/my-requests` | Get my requests |

### Messaging (`/messages`)
| Method | Path | Description |
|---|---|---|
| GET | `/messages/conversations` | List my conversations |
| POST | `/messages/conversations` | Start conversation |
| POST | `/messages/conversations/{id}` | Send message (text/voice) |
| GET | `/messages/conversations/{id}` | Get messages (marks read) |

### Interviews (`/interviews`)
| Method | Path | Description |
|---|---|---|
| POST | `/interviews` | Schedule interview |
| PATCH | `/interviews/{id}` | Reschedule / update link |
| GET | `/interviews` | My upcoming interviews |
| GET | `/interviews/job/{jobId}` | Interviews for a job |

### Notifications (`/notifications`)
| Method | Path | Description |
|---|---|---|
| GET | `/notifications` | My notification feed |
| GET | `/notifications/unread-count` | Count unread |
| PATCH | `/notifications/mark-all-read` | Mark all read |

### Reporting (`/reports`)
| Method | Path | Description |
|---|---|---|
| POST | `/reports` | File discrimination report |
| GET | `/reports` | Admin: all reports |
| GET | `/reports/employer/{id}` | Admin: reports by employer |
| PATCH | `/reports/{id}/review` | Admin: review report |
| GET | `/reports/flagged-employers` | Admin: flagged employers |
| POST | `/reports/employer/{id}/action` | Admin: suspend/remove |

### Admin (`/admin`)
| Method | Path | Description |
|---|---|---|
| GET | `/admin/impact-report` | Platform impact analytics |
| GET | `/admin/users` | List all users |
| GET | `/admin/users/{id}` | Get user detail |
| PATCH | `/admin/users/{id}/status` | Verify/suspend/remove user |
| POST | `/admin/employers/{id}/verify` | Verify employer profile |

---

## Security Model

- **JWT Access Token** — 24h expiry, signed with HS256
- **Refresh Token** — 7-day expiry, stored in DB, revocable
- **Role-based access** — `SEEKER`, `EMPLOYER`, `MENTOR`, `ADMIN`
- **Anonymous Apply** — disability identity never revealed until employer expresses interest (FR4/NFR3)
- **Disability data isolation** — stored in separate `seeker_disability_info` table (NFR3/GDPR)
- **AES-256 at rest / TLS 1.3 in transit** — configure at infrastructure level

---

## WebSocket (Real-time Messaging)

Connect via: `ws://localhost:8080/api/v1/ws`  
Protocol: STOMP over SockJS

```javascript
// Subscribe to your personal message queue
stompClient.subscribe('/user/queue/messages', (msg) => { ... });

// Send a message
stompClient.send('/app/messages/send', {}, JSON.stringify({ content, conversationId }));
```

---

## Default Admin Credentials (seed data)
```
Email:    admin@abilitybridge.io
Password: Admin@123!
```
**Change this immediately in production.**

---

## Running Tests
```bash
mvn test
```

---

## NFR Compliance Summary

| NFR | Implementation |
|---|---|
| WCAG 2.1 AA (NFR1) | Accessibility settings per user; sign language, dyslexia font, screen reader, high contrast flags |
| Performance (NFR2) | DB indexes on all hot query paths; match score cached; async notifications |
| Security (NFR3) | JWT + bcrypt; disability data in separate table; anonymous mode with zero leakage |
| Scalability (NFR4) | Stateless JWT; connection pooling (HikariCP); horizontal-scale ready |
| Availability (NFR5) | Spring Actuator health endpoint; configure with load balancer + DB replicas |
| Usability (NFR6) | RESTful + OpenAPI docs; consistent error responses |
| Maintainability (NFR7) | SOLID; layered architecture; Lombok + MapStruct reduce boilerplate |
| Localization (NFR8) | Region/currency stored per entity; extend with Spring MessageSource |
