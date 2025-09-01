#!/usr/bin/env bash
# Simple helper to build the Android frontend using the Gradle wrapper in its module directory.
# Ensures we execute from the correct directory to avoid './gradlew: No such file or directory' errors.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="${SCRIPT_DIR}/ai_calculator_frontend"

if [[ ! -d "${APP_DIR}" ]]; then
  echo "Error: Expected app directory at ${APP_DIR} not found."
  exit 1
fi

cd "${APP_DIR}"

# Ensure gradlew is present
if [[ ! -f "./gradlew" ]]; then
  echo "Error: gradlew not found in ${APP_DIR}."
  echo "Check that the Gradle wrapper files exist: gradlew, gradlew.bat, gradle/wrapper/*"
  exit 1
fi

# If the file is not executable, try to set it executable. In CI this typically succeeds.
if [[ ! -x "./gradlew" ]]; then
  echo "gradlew is not executable; attempting to set executable bit..."
  chmod +x ./gradlew || true
fi

# Default to listing tasks if no args are provided; pass through any args to Gradle.
if [[ "$#" -eq 0 ]]; then
  ./gradlew tasks
else
  ./gradlew "$@"
fi
