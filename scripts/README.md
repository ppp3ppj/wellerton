# Build Scripts

Scripts for building signed and unsigned APKs without manual keystore setup.

## Requirements

- JDK (for `keytool`) — included with Android Studio
- Android SDK
- For Windows: PowerShell 5.1+
- For Linux/macOS: bash

---

## Usage

### Windows (PowerShell)

```powershell
# Signed release APK
.\scripts\build-apk.ps1

# Unsigned debug APK
.\scripts\build-apk.ps1 -BuildType debug
```

> If blocked by execution policy, run once:
> ```powershell
> Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
> ```

### Linux / macOS (bash)

```bash
# Signed release APK
bash scripts/build-apk.sh

# Unsigned debug APK
bash scripts/build-apk.sh debug
```

---

## What the script does

1. **(Release only)** Checks if `app/test-keystore.jks` exists — auto-generates a test keystore via `keytool` if not
2. **(Release only)** Appends signing config to `local.properties` (skipped if already present)
3. Runs `assembleRelease` or `assembleDebug`

## Output

| Build type | APK location |
|---|---|
| release (signed) | `app/build/outputs/apk/release/app-release.apk` |
| debug (unsigned) | `app/build/outputs/apk/debug/app-debug.apk` |

---

## Notes

- `test-keystore.jks` and `local.properties` are gitignored — safe for local/CI use
- This keystore is for **testing only**, do not use for production Play Store releases
- For CI/CD, the keystore is regenerated automatically on each fresh runner
