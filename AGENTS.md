# AGENTS.md

Single-module Android app (`:app`) built with Jetpack Compose. Not a git repo (no VCS, CI, or release conventions exist here). Package/namespace: `ru.dimasokol.demo.drawingtext`.

Source of truth is the Gradle files — `gradle/libs.versions.toml`, `app/build.gradle.kts` — there is no README.

## Commands

```bash
./gradlew :app:assembleDebug              # ~5 min on a cold cache
./gradlew :app:testDebugUnitTest          # JVM unit tests
./gradlew :app:testDebugUnitTest --tests "*ExampleUnitTest.addition_isCorrect"
./gradlew :app:lintDebug                  # report: app/build/reports/lint-results-debug.html
./gradlew :app:connectedDebugAndroidTest  # needs a running emulator/device
```

There is no typecheck or formatter task; Kotlin compile is the typecheck (`kotlin.code.style=official`).

## Gotchas

- Task listing (`./gradlew :app:tasks --all`) works, but `generate<Debug|Release>ComposePreviewRunfiles` is IDE-only: run directly it fails with "property 'composePreviewManifestFile' doesn't have a configured value". That's expected — the IDE passes `--compose-preview-manifest-file`. Don't chase it, but do check that task *registration* still works, since configuration errors there break every build.
- `androidComponents { beforeVariants { … } }` in `app/build.gradle.kts` force-enables the release unit test component. AGP 9 only enables unit tests for the tested build type (debug) by default, and `generateReleaseComposePreviewRunfiles` throws at configuration time without a unit test component. Removing that block re-breaks the whole project, including `:app:tasks` and the IDE sync.
- `--offline` fails on plugin resolution for a fresh cache; AGP 9.5.1 must be fetched from Google maven once.
- First build downloads the Gradle 9.6.0 distribution plus a Java 25 daemon toolchain (via foojay in `gradle/gradle-daemon-jvm.properties`), while the launcher JVM is 17. Both are expected; don't "fix" the version mismatch.
- `org.gradle.configuration-cache=true` is on. Re-running after a build-script edit reconfigures the whole graph (~1–2 min) before doing anything.
- `local.properties` with `sdk.dir` is gitignored but required for any Gradle task; it already points at the local Android SDK.
- Repositories use `FAIL_ON_PROJECT_REPOS`: never add a `repositories {}` block to a module. Add dependencies through `libs.versions.toml` + the `libs.*` aliases.

## Build config that differs from the usual templates

- AGP 9 DSL, not the classic one: `compileSdk { version = release(37) }` (not `compileSdk = 37`) and release builds use `optimization { enable = true; packageScope = ... }` instead of `isMinifyEnabled`/`proguardFiles`.
- R8 keep rules live in `app/src/main/keepRules/` (`rules.keep`), **not** `proguard-rules.pro`. AGP merges every file in that directory.
- Kotlin support comes from AGP's built-in Kotlin; there is no `org.jetbrains.kotlin.android` plugin. Don't add one. Only `com.android.application` and `org.jetbrains.kotlin.plugin.compose` are applied.
- Java/Kotlin source and target compatibility is 11; `minSdk = 24`, `compileSdk`/`targetSdk` 37.
- Compose BOM `2026.02.01` pins all `androidx.compose.*` versions — don't add explicit versions for those artifacts.

## Code layout

- `ui/theme/` — `Theme.kt` (Material 3, dynamic color on API 31+), `Color.kt`, `Type.kt`. Any new screen should be wrapped in `DrawingTextTheme`.
- No DI, navigation, ViewModel, or networking dependencies exist yet; don't assume an architecture layer that isn't there.
- `app/src/main/res/values/themes.xml` uses the platform `android:Theme.Material.Light.NoActionBar`, kept only as the launch theme for the Compose activity.
