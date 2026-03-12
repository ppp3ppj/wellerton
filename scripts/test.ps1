param(
    [ValidateSet("unit", "ui", "instrumented", "all")]
    [string]$Mode = "unit"
)

$ErrorActionPreference = "Stop"

$RootDir = Resolve-Path "$PSScriptRoot\.."
$Gradlew = Join-Path $RootDir "gradlew.bat"

function Assert-Device {
    if (-not (Get-Command adb -ErrorAction SilentlyContinue)) {
        Write-Error "adb not found. Install Android SDK platform-tools and add to PATH."
        exit 1
    }
    $devices = adb devices | Select-String "device$"
    if ($devices.Count -eq 0) {
        Write-Error "No device/emulator connected. Start an emulator or connect a device first."
        exit 1
    }
}

function Run-Unit {
    Write-Host "========================================"
    Write-Host " Unit Tests (JVM)"
    Write-Host "========================================"
    & "$Gradlew" test
    Write-Host ""
    Write-Host "Report: app\build\reports\tests\testDebugUnitTest\index.html"
}

function Run-UI {
    Write-Host "========================================"
    Write-Host " UI Tests (Compose - requires device)"
    Write-Host "========================================"
    Assert-Device
    & "$Gradlew" connectedAndroidTest `
        "-Pandroid.testInstrumentationRunnerArguments.package=com.ppp3ppj.wellerton.presentation"
    Write-Host ""
    Write-Host "Report: app\build\reports\androidTests\connected\index.html"
}

function Run-Instrumented {
    Write-Host "========================================"
    Write-Host " Instrumented Tests (Room - requires device)"
    Write-Host "========================================"
    Assert-Device
    & "$Gradlew" connectedAndroidTest `
        "-Pandroid.testInstrumentationRunnerArguments.package=com.ppp3ppj.wellerton.data"
    Write-Host ""
    Write-Host "Report: app\build\reports\androidTests\connected\index.html"
}

switch ($Mode) {
    "unit"         { Run-Unit }
    "ui"           { Run-UI }
    "instrumented" { Run-Instrumented }
    "all"          { Run-Unit; Run-UI; Run-Instrumented }
}

Write-Host ""
Write-Host "Done."
