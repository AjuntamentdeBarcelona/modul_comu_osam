# Android Unit Tests

This directory contains unit tests that are specific to the **Android target** or rely on JVM-specific mocking libraries like **MockK**.

## ⚠️ Important: Prioritize CommonTest

**You should always prioritize writing tests in `commonTest`** using **Mokkery**.

Only use this `androidUnitTest` source set if:
1.  You are testing logic that is specific to the Android platform (e.g., uses `android.*` APIs).
2.  You are maintaining legacy tests that heavily rely on **MockK** features not available in Mokkery.
3.  You need to use reflection-based mocking strategies (like `mockkConstructor`) which are not supported in Kotlin Native (iOS).

## Test Execution

These tests are executed in the CI pipeline via:

```bash
./gradlew clean :common:testDebugUnitTest
```

## Libraries Used Here

*   **MockK**: Used for mocking in this source set because it runs on the JVM. Note that MockK does **not** fully support Kotlin Native (iOS), which is why we avoid it in `commonTest`.
*   **Kotlin Test (JUnit)**: Standard testing framework.

## Mocking Internal Classes (JVM Only)

If you are forced to test here and need to mock internal dependencies without dependency injection, you can use MockK's `mockkConstructor`:

```kotlin
mockkConstructor(CommonRepository::class)
coEvery { anyConstructed<CommonRepository>().getVersion(any()) } returns Result.success(version)
```
