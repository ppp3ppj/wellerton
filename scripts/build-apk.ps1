param(
    [string]$BuildType = "release"  # release | debug | prod
)

$ErrorActionPreference = "Stop"

$RootDir = Resolve-Path "$PSScriptRoot\.."
$KeystorePath = "$RootDir\app\test-keystore.jks"
$LocalProps = "$RootDir\local.properties"

$KeyAlias = "test-key"
$StorePassword = "test1234"
$KeyPassword = "test1234"
$Dname = "CN=Test, OU=Dev, O=Test, L=Bangkok, ST=TH, C=TH"

function Generate-Keystore {
    Write-Host "Generating test keystore..."
    keytool -genkey -v `
        -keystore $KeystorePath `
        -alias $KeyAlias `
        -keyalg RSA `
        -keysize 2048 `
        -validity 3650 `
        -storepass $StorePassword `
        -keypass $KeyPassword `
        -dname $Dname `
        -noprompt
    Write-Host "Keystore created at $KeystorePath"
}

function Write-SigningProps {
    $content = if (Test-Path $LocalProps) { Get-Content $LocalProps -Raw } else { "" }
    if ($content -match "signing.storeFile") {
        Write-Host "Signing config already in local.properties, skipping."
        return
    }
    # Use forward slashes — Java properties files treat backslash as escape char
    $KeystorePathFwd = $KeystorePath -replace '\\', '/'
    Add-Content $LocalProps "`nsigning.storeFile=$KeystorePathFwd"
    Add-Content $LocalProps "signing.storePassword=$StorePassword"
    Add-Content $LocalProps "signing.keyAlias=$KeyAlias"
    Add-Content $LocalProps "signing.keyPassword=$KeyPassword"
    Write-Host "Signing config written to local.properties"
}

function Assert-ProdEnv {
    $missing = @()
    if (-not $env:SIGNING_STORE_FILE)     { $missing += "SIGNING_STORE_FILE" }
    if (-not $env:SIGNING_STORE_PASSWORD) { $missing += "SIGNING_STORE_PASSWORD" }
    if (-not $env:SIGNING_KEY_ALIAS)      { $missing += "SIGNING_KEY_ALIAS" }
    if (-not $env:SIGNING_KEY_PASSWORD)   { $missing += "SIGNING_KEY_PASSWORD" }
    if ($missing.Count -gt 0) {
        Write-Error "Missing required env vars for prod build:`n  $($missing -join "`n  ")"
        exit 1
    }
    if (-not (Test-Path $env:SIGNING_STORE_FILE)) {
        Write-Error "Keystore file not found: $env:SIGNING_STORE_FILE"
        exit 1
    }
}

switch ($BuildType) {
    "prod" {
        Assert-ProdEnv
        Write-Host "Building production signed release APK..."
        & "$RootDir\gradlew.bat" assembleRelease
        Write-Host ""
        Write-Host "APK: app\build\outputs\apk\release\app-release.apk"
    }
    "release" {
        if (-not (Test-Path $KeystorePath)) { Generate-Keystore }
        Write-SigningProps
        Write-Host "Building test signed release APK..."
        & "$RootDir\gradlew.bat" assembleRelease
        Write-Host ""
        Write-Host "APK: app\build\outputs\apk\release\app-release.apk"
    }
    "debug" {
        Write-Host "Building unsigned debug APK..."
        & "$RootDir\gradlew.bat" assembleDebug
        Write-Host ""
        Write-Host "APK: app\build\outputs\apk\debug\app-debug.apk"
    }
    default {
        Write-Error "Usage: build-apk.ps1 -BuildType [release|debug|prod]"
        exit 1
    }
}
