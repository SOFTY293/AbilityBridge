# AbilityBridge Flutter Frontend

> Cross-platform Flutter app for Android, iOS, Web, and Desktop

---

## Quick Start

### Prerequisites
- Flutter SDK 3.19+ installed
- Android Studio OR Xcode (for mobile)
- Chrome (for web)
- Spring Boot backend running on port 8080

### 1. Install dependencies
```bash
flutter pub get
```

### 2. Configure API URL

Open `lib/core/api/api_client.dart` and update `_baseUrl`:

| Platform | URL |
|---|---|
| Android Emulator | `http://10.0.2.2:8080/api/v1` (default) |
| iOS Simulator | `http://localhost:8080/api/v1` |
| Real Device | `http://YOUR_MACHINE_IP:8080/api/v1` |
| Web Browser | `http://localhost:8080/api/v1` |

To find your machine's IP: run `ipconfig` (Windows) or `ifconfig` (Mac/Linux).

### 3. Run the app

```bash
# Android
flutter run -d android

# Web
flutter run -d chrome

# Windows
flutter run -d windows

# iOS (Mac only)
flutter run -d ios
```

---

## Project Structure

```
lib/
├── main.dart                    # App entry point
├── core/
│   ├── api/
│   │   └── api_client.dart      # Dio HTTP client + JWT interceptor
│   ├── models/
│   │   ├── user_model.dart      # User, AuthResponse, SeekerProfile, EmployerProfile
│   │   └── job_model.dart       # JobListing, MicroTask, Notification
│   ├── providers/
│   │   └── auth_provider.dart   # Riverpod auth state (login/register/logout)
│   ├── theme/
│   │   └── app_theme.dart       # Full design system (colors, typography, spacing)
│   ├── widgets/
│   │   └── shared_widgets.dart  # AppButton, AppTextField, AppCard, MatchScoreBadge
│   └── router.dart              # GoRouter navigation with auth guards
└── features/
    ├── onboarding/              # 4-page animated onboarding
    ├── auth/                    # Login + Register screens
    ├── home/                    # Role-aware dashboard
    ├── jobs/                    # Job list, detail, apply (FR4)
    ├── tasks/                   # Micro task marketplace (FR3)
    ├── skills/                  # Assessment, gap analysis, badges, portfolio (FR2)
    ├── accommodation/           # Needs, compatibility, negotiation (FR5)
    ├── employer/                # Workplace reality score, badges (FR6)
    ├── mentorship/              # Mentor search and requests (FR7)
    ├── messaging/               # Conversations + chat (FR8)
    ├── notifications/           # Notification feed (FR8)
    ├── profile/                 # User profile + accessibility settings (FR1)
    └── admin/                   # Impact report, user management (FR10)
```

---

## Key Features

- **JWT auth** — auto-injected on every request, auto-refreshed on 401
- **Anonymous Apply** — toggle in profile, hides identity from employers
- **Smart Match Score** — shows % skill match on every job card
- **Accessibility settings** — screen reader, high contrast, voice nav, dyslexia font, sign language video
- **Dark mode** — full dark theme support, follows system setting
- **Offline-friendly** — cached tokens persist across app restarts
- **Role-aware UI** — different dashboards for Seeker, Employer, Mentor, Admin

---

## Default Admin Login
```
Email:    admin@abilitybridge.io
Password: Admin@123!
```
