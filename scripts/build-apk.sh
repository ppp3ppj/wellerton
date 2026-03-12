#!/usr/bin/env bash
set -e

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
KEYSTORE_PATH="$ROOT_DIR/app/test-keystore.jks"
LOCAL_PROPS="$ROOT_DIR/local.properties"

KEY_ALIAS="test-key"
STORE_PASSWORD="test1234"
KEY_PASSWORD="test1234"
DNAME="CN=Test, OU=Dev, O=Test, L=Bangkok, ST=TH, C=TH"

generate_keystore() {
  echo "Generating test keystore..."
  keytool -genkey -v \
    -keystore "$KEYSTORE_PATH" \
    -alias "$KEY_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 3650 \
    -storepass "$STORE_PASSWORD" \
    -keypass "$KEY_PASSWORD" \
    -dname "$DNAME" \
    -noprompt
  echo "Keystore created at $KEYSTORE_PATH"
}

write_local_properties() {
  # Preserve existing local.properties entries (e.g. sdk.dir), append signing keys
  if grep -qF "signing.storeFile" "$LOCAL_PROPS" 2>/dev/null; then
    echo "Signing config already in local.properties, skipping."
  else
    cat >> "$LOCAL_PROPS" <<EOF

signing.storeFile=$KEYSTORE_PATH
signing.storePassword=$STORE_PASSWORD
signing.keyAlias=$KEY_ALIAS
signing.keyPassword=$KEY_PASSWORD
EOF
    echo "Signing config written to local.properties"
  fi
}

# --- main ---
BUILD_TYPE="${1:-release}"  # default: release, pass "debug" for unsigned

chmod +x "$ROOT_DIR/gradlew"

if [ "$BUILD_TYPE" = "release" ]; then
  [ -f "$KEYSTORE_PATH" ] || generate_keystore
  write_local_properties
  echo "Building signed release APK..."
  "$ROOT_DIR/gradlew" assembleRelease
  echo ""
  echo "APK: app/build/outputs/apk/release/app-release.apk"
else
  echo "Building unsigned debug APK..."
  "$ROOT_DIR/gradlew" assembleDebug
  echo ""
  echo "APK: app/build/outputs/apk/debug/app-debug.apk"
fi
