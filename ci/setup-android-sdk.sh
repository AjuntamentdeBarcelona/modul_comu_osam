#!/usr/bin/env bash
set -euo pipefail

echo "== Debug: ANDROID env vars =="
env | grep -E 'ANDROID|SDK' || true

# 1) Prefer already-defined env var if it points to a real dir
if [ -n "${ANDROID_SDK_ROOT:-}" ] && [ -d "${ANDROID_SDK_ROOT:-}" ]; then
  SDK_ROOT="$ANDROID_SDK_ROOT"
elif [ -n "${ANDROID_HOME:-}" ] && [ -d "${ANDROID_HOME:-}" ]; then
  SDK_ROOT="$ANDROID_HOME"
else
  SDK_ROOT=""
fi

# 2) If not found, infer from sdkmanager path (best signal)
if [ -z "$SDK_ROOT" ] && command -v sdkmanager >/dev/null 2>&1; then
  SM="$(readlink -f "$(command -v sdkmanager)")"
  SDK_ROOT="$(dirname "$(dirname "$(dirname "$(dirname "$SM")")")")"
fi

# 3) If still not found, infer from adb path
if [ -z "$SDK_ROOT" ] && command -v adb >/dev/null 2>&1; then
  ADB="$(readlink -f "$(command -v adb)")"
  SDK_ROOT="$(dirname "$(dirname "$ADB")")"
fi

# 4) Validate SDK root (must contain either platform-tools or cmdline-tools)
if [ -z "$SDK_ROOT" ] || [ ! -d "$SDK_ROOT" ]; then
  echo "❌ Could not determine Android SDK root."
  echo "== Root directory listing ==" && ls -la /
  echo "== /opt listing ==" && ls -la /opt || true
  echo "== Find sdkmanager/adb ==" && (find / -maxdepth 5 -type f -name sdkmanager -o -name adb 2>/dev/null | head -n 50) || true
  exit 1
fi

export ANDROID_SDK_ROOT="$SDK_ROOT"
export ANDROID_HOME="$SDK_ROOT"

echo "✅ Using ANDROID_SDK_ROOT=$ANDROID_SDK_ROOT"
echo "sdk.dir=$ANDROID_SDK_ROOT" > local.properties
cat local.properties

# Licenses are optional; don't break CI if sdkmanager isn't functional
yes | sdkmanager --licenses >/dev/null 2>&1 || true
