package cat.bcn.commonmodule.ui.logic.event

import dev.mokkery.verify.VerifyMode.Companion.exactly
import cat.bcn.commonmodule.analytics.CommonAnalytics
import cat.bcn.commonmodule.crashlytics.InternalCrashlyticsWrapper
import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.messaging.TopicSubscriptionManager
import cat.bcn.commonmodule.model.CommonError
import cat.bcn.commonmodule.model.Either
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.ui.executor.Executor
import cat.bcn.commonmodule.ui.versioncontrol.AppLanguageResponse
import cat.bcn.commonmodule.ui.versioncontrol.Language
import cat.bcn.commonmodule.ui.versioncontrol.SubscriptionResponse
import cat.bcn.commonmodule.ui.versioncontrol.TokenResponse
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
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
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SubscriptionsEventTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val testScope = TestScope(testDispatcher)

    // Mocks
    private val topicSubscriptionManager = mock<TopicSubscriptionManager>()
    private val preferences = mock<Preferences>()
    private val platformInformation = mock<PlatformInformation>()
    private val analytics = mock<CommonAnalytics>()
    private val crashlytics = mock<InternalCrashlyticsWrapper>()
    private val executor = mock<Executor>()

    private lateinit var subscriptionsEvent: SubscriptionsEvent

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Stub Executor
        every { executor.main } returns testDispatcher
        every { executor.bg } returns testDispatcher

        subscriptionsEvent = SubscriptionsEvent(
            scope = testScope,
            topicSubscriptionManager = topicSubscriptionManager,
            preferences = preferences,
            platformInformation = platformInformation,
            analytics = analytics,
            internalCrashlyticsWrapper = crashlytics,
            executor = executor
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region changeLanguageEvent

    @Test
    fun `changeLanguageEvent returns UNCHANGED if language is the same`() = runTest(testScheduler) {
        // Given
        val language = Language.DEFAULT // e.g., "CA" (Catalan)
        // Mock preferences to return the SAME language
        every { preferences.getSelectedLanguage() } returns language.name

        // When
        var result: AppLanguageResponse? = null
        subscriptionsEvent.changeLanguageEvent(language) { result = it }
        testScheduler.runCurrent()

        // Then
        assertEquals(AppLanguageResponse.UNCHANGED, result)
        // Verify we didn't try to subscribe
        verify(exactly(0)) {
            preferences.setDisplayedLanguage(any())
            analytics.logLanguageChange(any(), any(), any())
        }
    }

    @Test
    fun `changeLanguageEvent returns SUCCESS when subscription succeeds`() = runTest(testScheduler) {
        // Given
        val newLanguage = Language.ES
        val oldLanguage = "CA"

        // 1. Mock Sync Logic (Preferences & Analytics)
        every { preferences.getSelectedLanguage() } returns oldLanguage
        every { platformInformation.getDeviceLanguage() } returns "en"
        every { preferences.setDisplayedLanguage(any()) } returns Unit
        every { preferences.setSelectedLanguage(any()) } returns Unit
        every { preferences.setPreviousLanguage(any()) } returns Unit
        every { preferences.getPreviousLanguage() } returns oldLanguage
        every { preferences.getDisplayedLanguage() } returns "en"
        every { analytics.logLanguageChange(any(), any(), any()) } returns Unit

        // 2. Mock Data needed for Topic creation (used by TopicPreferencesUtils)
        every { platformInformation.getSmallPackageName() } returns "cat.bcn.test"
        every { platformInformation.getVersionName() } returns "1.0.0"
        every { platformInformation.getVersionCode() } returns 10
        // Mocking preferences calls made by TopicPreferencesUtils
        every { preferences.getVersionControlVersionNamePrevious() } returns "1.0.0"
        every { preferences.getVersionControlVersionCode() } returns 10L

        // 3. Mock Async Logic (Subscription Manager)
        everySuspend {
            topicSubscriptionManager.subscriptionToLanguageChange(any(), any())
        } returns Either.Right(true) // Success = true

        // When
        var result: AppLanguageResponse? = null
        subscriptionsEvent.changeLanguageEvent(newLanguage) { result = it }
        testScheduler.runCurrent()

        // Then
        assertEquals(AppLanguageResponse.SUCCESS, result)
        verify { analytics.logLanguageChange(oldLanguage, newLanguage.name, "en") }
    }

    @Test
    fun `changeLanguageEvent returns ERROR on exception`() = runTest(testScheduler) {
        // Given
        val newLanguage = Language.ES
        every { preferences.getSelectedLanguage() } returns "CA"

        // Simulate crash during synchronous setup
        every { preferences.setDisplayedLanguage(any()) } throws RuntimeException("Prefs failed")
        every { crashlytics.recordException(any()) } returns Unit

        // When
        var result: AppLanguageResponse? = null
        subscriptionsEvent.changeLanguageEvent(newLanguage) { result = it }
        testScheduler.runCurrent()

        // Then
        assertEquals(AppLanguageResponse.ERROR, result)
        verify { crashlytics.recordException(any()) }
    }

    // endregion

    // region firstTimeOrUpdateAppEvent

    @Test
    fun `firstTimeOrUpdateAppEvent returns SUCCESS on successful subscription`() = runTest(testScheduler) {
        // Given
        val language = Language.DEFAULT

        // 1. Mock Data for Topic Utils
        every { platformInformation.getSmallPackageName() } returns "cat.bcn.test"
        every { platformInformation.getVersionName() } returns "1.0.0"
        every { platformInformation.getVersionCode() } returns 10
        // Mocking preferences calls made by TopicPreferencesUtils
        every { preferences.getVersionControlVersionNamePrevious() } returns "0.9.0" // Different version
        every { preferences.getVersionControlVersionCode() } returns 9L

        // 2. Mock Subscription Manager
        everySuspend {
            topicSubscriptionManager.subscriptionToAppInitializationOrUpdates(any(), any())
        } returns Either.Right(true)

        // 3. Mock Post-Success Logic
        every { preferences.setVersionControlVersionNamePrevious(any()) } returns Unit

        // When
        var result: AppLanguageResponse? = null
        subscriptionsEvent.firstTimeOrUpdateAppEvent(language) { result = it }
        testScheduler.runCurrent()

        // Then
        assertEquals(AppLanguageResponse.SUCCESS, result)
        verify { preferences.setVersionControlVersionNamePrevious("1.0.0") }
    }

    @Test
    fun `firstTimeOrUpdateAppEvent returns UNCHANGED if manager returns false`() = runTest(testScheduler) {
        // Given
        val language = Language.DEFAULT

        // Mock Data for Topic Utils (using relaxed/any where possible to reduce boilerplate)
        every { platformInformation.getSmallPackageName() } returns "test"
        every { platformInformation.getVersionName() } returns "1.0"
        every { platformInformation.getVersionCode() } returns 1
        every { preferences.getVersionControlVersionNamePrevious() } returns "1.0"
        every { preferences.getVersionControlVersionCode() } returns 1L

        // Mock Manager returning false (no update needed)
        everySuspend {
            topicSubscriptionManager.subscriptionToAppInitializationOrUpdates(any(), any())
        } returns Either.Right(false)

        // When
        var result: AppLanguageResponse? = null
        subscriptionsEvent.firstTimeOrUpdateAppEvent(language) { result = it }
        testScheduler.runCurrent()

        // Then
        assertEquals(AppLanguageResponse.UNCHANGED, result)
    }

    @Test
    fun `firstTimeOrUpdateAppEvent returns ERROR on failure`() = runTest(testScheduler) {
        // Given
        val language = Language.DEFAULT
        val error = CommonError(Exception("Network fail"))

        // Mock Data
        every { platformInformation.getSmallPackageName() } returns "test"
        every { platformInformation.getVersionName() } returns "1.0"
        every { platformInformation.getVersionCode() } returns 1
        every { preferences.getVersionControlVersionNamePrevious() } returns "1.0"
        every { preferences.getVersionControlVersionCode() } returns 1L

        // Mock Manager Failure
        everySuspend {
            topicSubscriptionManager.subscriptionToAppInitializationOrUpdates(any(), any())
        } returns Either.Left(error)

        every { crashlytics.recordException(any()) } returns Unit

        // When
        var result: AppLanguageResponse? = null
        subscriptionsEvent.firstTimeOrUpdateAppEvent(language) { result = it }
        testScheduler.runCurrent()

        // Then
        assertEquals(AppLanguageResponse.ERROR, result)
        verify { crashlytics.recordException(error.exception) }
    }

    // endregion

    // region Custom Topics

    @Test
    fun `subscribeToCustomTopic returns ACCEPTED on success`() = runTest(testScheduler) {
        // Given
        val topic = "news"
        everySuspend {
            topicSubscriptionManager.subscribeToCustomTopic(topic)
        } returns Either.Right(SubscriptionResponse.ACCEPTED)

        // When
        var result: SubscriptionResponse? = null
        subscriptionsEvent.subscribeToCustomTopic(topic) { result = it }
        testScheduler.runCurrent()

        // Then
        assertEquals(SubscriptionResponse.ACCEPTED, result)
    }

    @Test
    fun `unsubscribeToCustomTopic returns ACCEPTED on success`() = runTest(testScheduler) {
        // Given
        val topic = "news"
        everySuspend {
            topicSubscriptionManager.unsubscribeToCustomTopic(topic)
        } returns Either.Right(SubscriptionResponse.ACCEPTED)

        // When
        var result: SubscriptionResponse? = null
        subscriptionsEvent.unsubscribeToCustomTopic(topic) { result = it }
        testScheduler.runCurrent()

        // Then
        assertEquals(SubscriptionResponse.ACCEPTED, result)
    }

    // endregion

    // region FCM Token

    @Test
    fun `getFCMToken returns Success with token`() = runTest(testScheduler) {
        // Given
        val token = "sample_token_123"
        everySuspend { topicSubscriptionManager.getFCMToken() } returns Either.Right(token)

        // When
        var result: TokenResponse? = null
        subscriptionsEvent.getFCMToken { result = it }
        testScheduler.runCurrent()

        // Then
        assertTrue(result is TokenResponse.Success)
        assertEquals(token, (result as TokenResponse.Success).token)
    }

    @Test
    fun `getFCMToken returns Error on failure`() = runTest(testScheduler) {
        // Given
        val exception = Exception("No token")
        val error = CommonError(exception)
        everySuspend { topicSubscriptionManager.getFCMToken() } returns Either.Left(error)

        // When
        var result: TokenResponse? = null
        subscriptionsEvent.getFCMToken { result = it }
        testScheduler.runCurrent()

        // Then
        assertTrue(result is TokenResponse.Error)
        assertEquals(exception, (result as TokenResponse.Error).error)
    }

    // endregion
}