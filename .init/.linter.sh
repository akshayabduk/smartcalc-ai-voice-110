#!/bin/bash
cd /home/kavia/workspace/code-generation/smartcalc-ai-voice-110/ai_calculator_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

