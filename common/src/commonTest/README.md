# Common Tests (Preferred)

This directory is the **primary location for all unit tests** in the `common` module.

## Guiding Principle: Test Here First

All new tests for shared business logic should be written here. This ensures that your tests are platform-agnostic and validate the code on all supported targets (Android, iOS, etc.).

## Testing Libraries

*   **Mokkery**: This is our **preferred mocking library**. It is designed for Kotlin Multiplatform and works seamlessly across all targets (JVM, iOS, etc.).
*   **Kotlin Test**: The standard multiplatform testing framework.
*   **Coroutines Test**: For testing coroutines.

## Why Mokkery?

We use Mokkery because it is a true KMP library. Unlike MockK, which has limited support for native targets, Mokkery allows us to write mocks once in `commonTest` and have them run everywhere.

## When to AVOID `commonTest`

Do not write tests here if they rely on:
*   Platform-specific APIs (e.g., `android.*` or iOS frameworks).
*   JVM-only libraries like MockK.

For those cases, place the test in the corresponding platform-specific test directory (e.g., `androidUnitTest`).

## Test Execution

These tests are executed in the CI pipeline. While the pipeline currently runs on a Linux environment (which only executes the JVM tests), these tests are designed to be compatible with all platforms.
