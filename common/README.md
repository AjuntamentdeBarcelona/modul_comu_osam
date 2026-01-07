# OSAM Common Module

This module contains shared logic and UI components for the OSAM application, supporting both Android and iOS via Kotlin Multiplatform.

## Android Usage

### Initialization

Initialize `OSAMCommons` in your Activity or Application class.

```kotlin
val osamCommons = OSAMCommons(
    activity = this, // Current Activity
    context = applicationContext, // Application Context
    backendEndpoint = "https://your.backend.endpoint",
    crashlyticsWrapper = YourCrashlyticsWrapper(),
    performanceWrapper = YourPerformanceWrapper(),
    analyticsWrapper = YourAnalyticsWrapper(),
    platformUtil = PlatformUtil(this),
    messagingWrapper = YourMessagingWrapper()
)
```

### Managing Activity Context

Since `OSAMCommons` may show dialogs (e.g., for version control or app rating), it needs a reference to the current `Activity`.

If your application has multiple activities or recreates them (e.g., configuration changes), you must update the activity reference in `OSAMCommons` to ensure dialogs are shown correctly.

Use the `setActivity` method in your Activity's `onResume` or whenever the active activity changes:

```kotlin
override fun onResume() {
    super.onResume()
    osamCommons.setActivity(this)
}
```

### Context Usage

Internally, `OSAMCommons` handles contexts carefully:
*   **Application Context**: Used for long-lived components like `Settings` and `PlatformInformation` to prevent memory leaks.
*   **Activity Context**: Used by `AlertWrapper` to display UI elements (Dialogs). This is updated via `setActivity`.

## Testing

See `src/commonTest/README.md` and `src/androidUnitTest/README.md` for details on testing this module.
