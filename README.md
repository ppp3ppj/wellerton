# Wellerton

Android app built with Kotlin and Jetpack Compose. Uses MVVM + Repository pattern with Room for local persistence and Hilt for dependency injection.

---

## Architecture

```
Screen (Composable)
  └── ViewModel (@HiltViewModel)
        └── Repository (interface)
              └── RepositoryImpl
                    └── DAO (Room / SQLite)
```

- **No UseCase layer** — kept intentionally flat for simplicity
- **StateFlow** for UI state in every ViewModel
- **Repository interface** as the testable boundary between ViewModel and data

---

## Project Structure

```
app/src/main/java/com/ppp3ppj/wellerton/
├── WellertonApplication.kt          @HiltAndroidApp entry point
├── MainActivity.kt                  @AndroidEntryPoint, hosts NavHost
│
├── di/
│   └── DatabaseModule.kt            Hilt modules: Room DB, DAO, Repository binding
│
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt           Room database (seeds default admin user on first create)
│   │   ├── entity/
│   │   │   └── UserEntity.kt        users table: id, name, pin_hash
│   │   └── dao/
│   │       └── UserDao.kt           insert, findByName, getFirst, count
│   └── repository/
│       ├── UserRepository.kt        interface
│       └── UserRepositoryImpl.kt    SHA-256 PIN hashing, Room queries
│
├── presentation/
│   ├── pincode/
│   │   ├── PinCodeScreen.kt         PIN entry UI (6-digit numeric pad)
│   │   └── PinCodeViewModel.kt      loads username, verifies PIN
│   └── home/
│       └── HomeScreen.kt            Welcome screen with logout button
│
├── navigation/
│   └── AppNavGraph.kt               Routes: pin → home/{username}
│
└── ui/theme/                        Material3 theme (unchanged)
```

---

## Screens

### PIN Screen (`/pin`)
- 6-digit numeric PIN pad — no username field shown
- On first launch: default `admin` user is seeded with PIN `000000`
- Correct PIN → navigates to Home, PIN screen removed from back stack
- Wrong PIN → shows "Incorrect PIN" error, clears entered digits

### Home Screen (`/home/{username}`)
- Displays **Welcome** + the logged-in username
- **Logout** button navigates back to PIN screen, clears back stack

---

## Default Dev User

| Field    | Value    |
|----------|----------|
| Username | `admin`  |
| PIN      | `000000` |

Seeded automatically via `RoomDatabase.Callback.onCreate` on first app install.
PIN is stored as a SHA-256 hash — never plain text.

---

## Tech Stack

| Library | Version | Purpose |
|---------|---------|---------|
| Kotlin | 2.3.10 | Language |
| AGP | 9.1.0 | Android Gradle Plugin |
| KSP | 2.3.6 | Code generation (Room + Hilt) |
| Jetpack Compose BOM | 2026.03.00 | UI framework |
| Navigation Compose | 2.9.7 | Screen navigation |
| Hilt | 2.59.2 | Dependency injection |
| Hilt Navigation Compose | 1.3.0 | `hiltViewModel()` in Compose |
| Room | 2.8.4 | SQLite ORM |
| Lifecycle ViewModel Compose | 2.10.0 | ViewModel in Compose |
| Coroutines | 1.10.2 | Async / Flow |
| MockK | 1.14.9 | Mocking in unit tests |

---

## Build

```bash
# Debug APK
./scripts/build-apk.sh debug

# Signed release APK (generates a test keystore locally)
./scripts/build-apk.sh release

# Production APK (requires signing env vars)
./scripts/build-apk.sh prod
```

**Required env vars for `prod` build:**
```
SIGNING_STORE_FILE
SIGNING_STORE_PASSWORD
SIGNING_KEY_ALIAS
SIGNING_KEY_PASSWORD
```

---

## Testing

### Test suites

| Suite | What it tests | Needs device? |
|-------|--------------|---------------|
| **unit** | `PinCodeViewModel` — logic, state transitions | No |
| **ui** | `PinCodeScreen`, `HomeScreen` — Compose UI interactions | Yes |
| **instrumented** | `UserRepositoryImpl` — Room in-memory DB queries | Yes |

### Run tests

**Linux / macOS:**
```bash
./scripts/test.sh unit           # ViewModel unit tests (fast, JVM only)
./scripts/test.sh ui             # Compose UI tests
./scripts/test.sh instrumented   # Room database tests
./scripts/test.sh all            # all three suites
```

**Windows (PowerShell):**
```powershell
.\scripts\test.ps1 -Mode unit
.\scripts\test.ps1 -Mode ui
.\scripts\test.ps1 -Mode instrumented
.\scripts\test.ps1 -Mode all
```

### Run directly with Gradle
```bash
# Unit tests
./gradlew test

# All on-device tests (UI + instrumented)
./gradlew connectedAndroidTest

# UI tests only
./gradlew connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=com.ppp3ppj.wellerton.presentation

# Room tests only
./gradlew connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=com.ppp3ppj.wellerton.data
```

### Test reports
| Suite | Report path |
|-------|-------------|
| Unit | `app/build/reports/tests/testDebugUnitTest/index.html` |
| UI / Instrumented | `app/build/reports/androidTests/connected/index.html` |

### Unit test coverage (`PinCodeViewModelTest`)

| Test | Scenario |
|------|----------|
| `init loads username` | ViewModel fetches username from repository on init |
| `onPinDigit accumulates` | Digits append in order |
| `onPinDigit caps at 6` | Cannot enter more than 6 digits |
| `onDelete removes last digit` | Backspace removes last character |
| `onDelete on empty` | No crash when pin is already empty |
| `correct pin → isSuccess` | Happy-path login sets `isSuccess = true` |
| `wrong pin → error + clear` | Wrong PIN shows error and resets pin to `""` |
| `new digit clears error` | Typing after an error dismisses the error message |

### UI test coverage (`PinCodeScreenTest`, `HomeScreenTest`)

| Test | Scenario |
|------|----------|
| `showsEnterPinTitle` | "Enter PIN" heading visible |
| `showsSixDots / pad buttons visible` | Digit buttons rendered |
| `tapDigits_noErrorShown` | Partial entry shows no error |
| `correctPin_navigatesToHomeScreen` | 000000 → navigates to Welcome screen |
| `wrongPin_showsErrorMessage` | Wrong PIN → "Incorrect PIN" appears |
| `deleteButton_removesLastDigit` | ⌫ removes last digit without crashing |
| `displaysWelcomeAndUsername` | Home shows "Welcome" + username |
| `displaysLogoutButton` | Logout button is visible |
| `logoutButton_invokesCallback` | Tapping Logout fires the callback |

### Instrumented test coverage (`UserRepositoryImplTest`)

| Test | Scenario |
|------|----------|
| `getCurrentUsername empty db` | Returns `null` when no users exist |
| `getCurrentUsername returns name` | Returns first user's name |
| `verifyPin correct` | Returns `true` for correct PIN hash |
| `verifyPin wrong pin` | Returns `false` for wrong PIN |
| `verifyPin unknown user` | Returns `false` for non-existent username |

---

## Development Notes

- `fallbackToDestructiveMigration(true)` is set — the database is wiped on schema changes during development. Remove this before production.
- The admin seed user is inserted only on `onCreate` (first install). Uninstall the app to re-seed.
- PIN hashing uses `SHA-256` via `java.security.MessageDigest` — no external crypto dependency needed.
