# declarative-samples-android-app
A sample Android application written in the Declarative Gradle DSL, using the prototype Declarative Gradle `androidApplication` Software Type defined in the `org.gradle.experimental.android-ecosystem` ecosystem plugin.

## Building and Running

This sample shows the definition of a multiproject Android application implemented using Kotlin 2.0.21 source code.
The project is the result of reproducing the project produced by the `gradle init` command in Gradle 8.9 as an Android project.

Important: Run the Gradle wrapper from this directory (ai_calculator_frontend), not from the repository root. Otherwise you may see:
`bash: line 1: ./gradlew: No such file or directory`

If needed, set the executable bit once:
```shell
chmod +x ./gradlew
```

To build the project without running, use:
```shell
./gradlew build
```

To list available tasks:
```shell
./gradlew tasks
```

To run the application, first install it on a connected Android device using:
```shell
./gradlew :app:installDebug
```

Then search for "Sample Declarative Gradle Android App" and launch app to see a hello world message.

From repository root, you can also use the helper script:
```shell
smartcalc-ai-voice-110/run_android_build.sh build
```