param(
    [string]$BuildType = "release"
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

if ($BuildType -eq "release") {
    if (-not (Test-Path $KeystorePath)) { Generate-Keystore }
    Write-SigningProps
    Write-Host "Building signed release APK..."
    & "$RootDir\gradlew.bat" assembleRelease
    Write-Host ""
    Write-Host "APK: app\build\outputs\apk\release\app-release.apk"
} else {
    Write-Host "Building unsigned debug APK..."
    & "$RootDir\gradlew.bat" assembleDebug
    Write-Host ""
    Write-Host "APK: app\build\outputs\apk\debug\app-debug.apk"
}
