# Build Scripts

Scripts for building signed and unsigned APKs without manual keystore setup.

## Requirements

- JDK (for `keytool`) — included with Android Studio
- Android SDK
- For Windows: PowerShell 5.1+
- For Linux/macOS: bash

---

## Build modes

| Mode | Keystore | Use case |
|---|---|---|
| `release` | Auto-generated test keystore | Local dev / testing |
| `debug` | None (unsigned) | Quick local testing |
| `prod` | Real keystore via env vars | CI/CD production release |

---

## Usage

### Windows (PowerShell)

```powershell
# Test signed release APK (auto-generates keystore)
.\scripts\build-apk.ps1

# Unsigned debug APK
.\scripts\build-apk.ps1 -BuildType debug

# Production signed release APK
.\scripts\build-apk.ps1 -BuildType prod
```

> If blocked by execution policy, run once:
> ```powershell
> Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
> ```

### Linux / macOS (bash)

```bash
# Test signed release APK (auto-generates keystore)
bash scripts/build-apk.sh

# Unsigned debug APK
bash scripts/build-apk.sh debug

# Production signed release APK
bash scripts/build-apk.sh prod
```

---

## Production build setup

For `prod` mode, set these env vars before running the script:

```bash
# Linux/macOS
export SIGNING_STORE_FILE=/path/to/prod-keystore.jks
export SIGNING_STORE_PASSWORD=your_store_password
export SIGNING_KEY_ALIAS=your_key_alias
export SIGNING_KEY_PASSWORD=your_key_password
bash scripts/build-apk.sh prod
```

```powershell
# Windows
$env:SIGNING_STORE_FILE = "C:\path\to\prod-keystore.jks"
$env:SIGNING_STORE_PASSWORD = "your_store_password"
$env:SIGNING_KEY_ALIAS = "your_key_alias"
$env:SIGNING_KEY_PASSWORD = "your_key_password"
.\scripts\build-apk.ps1 -BuildType prod
```

In CI/CD (GitHub Actions, GitLab CI, etc.), store these as **repository secrets** and inject them as env vars in your pipeline.

---

## Output

| Build type | APK location |
|---|---|
| release / prod (signed) | `app/build/outputs/apk/release/app-release.apk` |
| debug (unsigned) | `app/build/outputs/apk/debug/app-debug.apk` |

---

## Notes

- `test-keystore.jks` and `local.properties` are gitignored — safe for local/CI use
- The test keystore is for **testing only**, do not use for Play Store releases
- For prod, env vars always take priority over `local.properties`
