#!/usr/bin/env bash
set -e

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
GRADLEW="$ROOT_DIR/gradlew"

chmod +x "$GRADLEW"

# Usage: test.sh [unit|ui|instrumented|all]
MODE="${1:-unit}"

check_device() {
  if ! command -v adb &>/dev/null; then
    echo "ERROR: adb not found. Install Android SDK platform-tools and add to PATH."
    exit 1
  fi
  DEVICES=$(adb devices | grep -v "List of" | grep "device$" | wc -l)
  if [ "$DEVICES" -eq 0 ]; then
    echo "ERROR: No device/emulator connected. Start an emulator or connect a device first."
    exit 1
  fi
}

run_unit() {
  echo "========================================"
  echo " Unit Tests (JVM)"
  echo "========================================"
  "$GRADLEW" test
  echo ""
  echo "Report: app/build/reports/tests/testDebugUnitTest/index.html"
}

run_ui() {
  echo "========================================"
  echo " UI Tests (Compose - requires device)"
  echo "========================================"
  check_device
  "$GRADLEW" connectedAndroidTest \
    -Pandroid.testInstrumentationRunnerArguments.package=com.ppp3ppj.wellerton.presentation
  echo ""
  echo "Report: app/build/reports/androidTests/connected/index.html"
}

run_instrumented() {
  echo "========================================"
  echo " Instrumented Tests (Room - requires device)"
  echo "========================================"
  check_device
  "$GRADLEW" connectedAndroidTest \
    -Pandroid.testInstrumentationRunnerArguments.package=com.ppp3ppj.wellerton.data
  echo ""
  echo "Report: app/build/reports/androidTests/connected/index.html"
}

case "$MODE" in
  unit)
    run_unit
    ;;
  ui)
    run_ui
    ;;
  instrumented)
    run_instrumented
    ;;
  all)
    run_unit
    run_ui
    run_instrumented
    ;;
  *)
    echo "Usage: $0 [unit|ui|instrumented|all]"
    echo ""
    echo "  unit         - ViewModel unit tests, runs on JVM (no device needed)"
    echo "  ui           - Compose UI tests, requires emulator or device"
    echo "  instrumented - Room database tests, requires emulator or device"
    echo "  all          - all three suites in order"
    exit 1
    ;;
esac

echo ""
echo "Done."
