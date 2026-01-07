package cat.bcn.commonmodule.ui.logic.event

import cat.bcn.commonmodule.analytics.CommonAnalytics
import cat.bcn.commonmodule.crashlytics.InternalCrashlyticsWrapper
import cat.bcn.commonmodule.data.datasource.local.CommonPreferences
import cat.bcn.commonmodule.data.repository.CommonRepository
import cat.bcn.commonmodule.model.CommonError
import cat.bcn.commonmodule.model.Either
import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Text
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.platform.PlatformUtil
import cat.bcn.commonmodule.ui.alert.AlertWrapper
import cat.bcn.commonmodule.ui.executor.Executor
import cat.bcn.commonmodule.ui.versioncontrol.Language
import cat.bcn.commonmodule.ui.versioncontrol.RatingControlResponse
import cat.bcn.commonmodule.ui.versioncontrol.VersionControlResponse
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode.Companion.exactly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DialogEventTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val testScope = TestScope(testDispatcher)

    // Mocks
    private val executor = mock<Executor>()
    private val analytics = mock<CommonAnalytics>()
    private val alertWrapper = mock<AlertWrapper>()
    private val repository = mock<CommonRepository>()
    private val crashlytics = mock<InternalCrashlyticsWrapper>()
    private val preferences = mock<CommonPreferences>()
    private val platformUtil = mock<PlatformUtil>()

    private lateinit var dialogEvent: DialogEvent

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Stub Executor to run on our test thread
        every { executor.main } returns testDispatcher
        every { executor.bg } returns testDispatcher

        dialogEvent = DialogEvent(
            scope = testScope,
            executor = executor,
            analytics = analytics,
            alertWrapper = alertWrapper,
            commonRepository = repository,
            internalCrashlyticsWrapper = crashlytics,
            preferences = preferences,
            platformUtil = platformUtil,
            currentLanguage = Language.DEFAULT
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region Version Control Tests

    @Test
    fun `versionControl FORCE mode shows force dialog`() = runTest(testScheduler) {
        // Given
        val language = Language.DEFAULT
        val version = createVersion(Version.ComparisonMode.FORCE)

        every { alertWrapper.isVersionControlShowing() } returns false
        everySuspend { repository.getVersion(language) } returns Either.Right(version)
        // Mock preferences for duration check (assuming it passes)
        every { preferences.getLastTimeUserClickedOnAcceptButton() } returns 0L

        // When
        var result: VersionControlResponse? = null
        dialogEvent.versionControl(language) { result = it }
        testScheduler.runCurrent()

        // Then
        verify {
            alertWrapper.showVersionControlForce(
                version = version,
                language = language,
                onPositiveClick = any()
            )
        }
    }

    @Test
    fun `versionControl LAZY mode shows lazy dialog if preference allows`() = runTest(testScheduler) {
        // Given
        val language = Language.DEFAULT
        val version = createVersion(Version.ComparisonMode.LAZY)

        every { alertWrapper.isVersionControlShowing() } returns false
        everySuspend { repository.getVersion(language) } returns Either.Right(version)
        every { preferences.getLastTimeUserClickedOnAcceptButton() } returns 0L

        // Crucial: User has NOT checked "Don't show again" (Active = true)
        every { preferences.getCheckBoxDontShowAgainActive() } returns true

        // When
        dialogEvent.versionControl(language) { }
        testScheduler.runCurrent()

        // Then
        verify {
            alertWrapper.showVersionControlLazy(
                version = version,
                language = language,
                onPositiveClick = any(),
                onNegativeClick = any(),
                onDismissClick = any()
            )
        }
    }

    @Test
    fun `versionControl LAZY mode is DISMISSED if preference blocks it`() = runTest(testScheduler) {
        // Given
        val language = Language.DEFAULT
        val version = createVersion(Version.ComparisonMode.LAZY)

        every { alertWrapper.isVersionControlShowing() } returns false
        everySuspend { repository.getVersion(language) } returns Either.Right(version)
        every { preferences.getLastTimeUserClickedOnAcceptButton() } returns 0L

        // Crucial: User HAS checked "Don't show again" (Active = false)
        every { preferences.getCheckBoxDontShowAgainActive() } returns false

        // When
        var result: VersionControlResponse? = null
        dialogEvent.versionControl(language) { result = it }
        testScheduler.runCurrent()

        // Then
        // Should NOT show dialog
        verify(exactly(0)) { alertWrapper.showVersionControlLazy(any(), any(), any(), any(), any()) }
        // Should return DISMISSED
        assertTrue { result == VersionControlResponse.DISMISSED   }
    }

    @Test
    fun `versionControl NONE mode returns DISMISSED without dialog`() = runTest(testScheduler) {
        // Given
        val language = Language.DEFAULT
        val version = createVersion(Version.ComparisonMode.NONE)

        every { alertWrapper.isVersionControlShowing() } returns false
        everySuspend { repository.getVersion(language) } returns Either.Right(version)
        every { preferences.getLastTimeUserClickedOnAcceptButton() } returns 0L

        // When
        var result: VersionControlResponse? = null
        dialogEvent.versionControl(language) { result = it }
        testScheduler.runCurrent()

        // Then
        verify(exactly(0)) {
            alertWrapper.showVersionControlForce(any(), any(), any())
            alertWrapper.showVersionControlLazy(any(), any(), any(), any(), any())
            alertWrapper.showVersionControlInfo(any(), any(), any(), any())
        }
        assertTrue { result == VersionControlResponse.DISMISSED }
    }

    @Test
    fun `versionControl returns ERROR on repository failure`() = runTest(testScheduler) {
        // Given
        val language = Language.DEFAULT
        val error = CommonError(Exception("Network error"))

        every { alertWrapper.isVersionControlShowing() } returns false
        everySuspend { repository.getVersion(language) } returns Either.Left(error)
        every { crashlytics.recordException(any()) } returns Unit

        // When
        var result: VersionControlResponse? = null
        dialogEvent.versionControl(language) { result = it }
        testScheduler.runCurrent()

        // Then
        verify { crashlytics.recordException(error.exception) }
        assertTrue {result == VersionControlResponse.ERROR}
    }

    // endregion

    // region Rating Tests

    @Test
    fun `rating shows dialog when criteria met`() = runTest(testScheduler) {
        // Given
        val language = Language.DEFAULT
        val rating = Rating(packageName = "Test", platform = Platform.ANDROID, minutes = 10, numAperture = 5, message = Text("Msg", "Msg", "Msg") )

        every { alertWrapper.isRatingShowing() } returns false
        every { preferences.getLastDatetime() } returns 100L
        every { preferences.setLastDatetime(any()) } returns Unit
        every { preferences.getNumApertures() } returns 5
        every { preferences.setNumApertures(any()) } returns Unit
        everySuspend { repository.getRating() } returns Either.Right(rating)

        // Mock logic for "shouldShowDialog" (assuming logic inside Rating model or helper)
        // Since we can't mock the data class logic easily, we ensure the inputs to it are correct.
        // Assuming preferences allow it:
        every { preferences.getDontShowAgain() } returns false

        // When
        var result: RatingControlResponse? = null
        dialogEvent.rating(language) { result = it }
        testScheduler.runCurrent()

        // Then
        // Note: This assumes Rating.shouldShowDialog returns true for the given inputs.
        // If Rating is a data class with logic, ensure the inputs (numAperture=5) match the criteria.
        verify {
            alertWrapper.showRating(
                rating = rating,
                language = language,
                onRatingPopupShown = any(),
                onRatingPopupError = any()
            )
        }
    }

    @Test
    fun `rating returns ERROR on repository failure`() = runTest(testScheduler) {
        // Given
        val language = Language.DEFAULT
        val error = CommonError(Exception("API Error"))

        every { alertWrapper.isRatingShowing() } returns false
        every { preferences.getLastDatetime() } returns 100L
        every { preferences.getNumApertures() } returns 1
        every { preferences.setNumApertures(any()) } returns Unit
        everySuspend { repository.getRating() } returns Either.Left(error)
        every { crashlytics.recordException(any()) } returns Unit

        // When
        var result: RatingControlResponse? = null
        dialogEvent.rating(language) { result = it }
        testScheduler.runCurrent()

        // Then
        verify { crashlytics.recordException(error.exception) }
        assertTrue {result == RatingControlResponse.ERROR}
    }

    // endregion

    private fun createVersion(mode: Version.ComparisonMode): Version {
        return Version(
            packageName = "cat.bcn.test",
            versionCode = 10,
            versionName = "1.0.0",
            platform = Platform.ANDROID,
            comparisonMode = mode,
            startDate = 0L,
            endDate = 9999999999999L, // Ensure it is in time range
            serverDate = 1000L,
            title = Text("Title", "Title", "Títol"),
            message = Text("Msg", "Msg", "Msg"),
            ok = Text("Ok", "Ok", "Ok"),
            cancel = Text("Cancel", "Cancel", "Cancel"),
            url = "http://test.com"
        )
    }
}