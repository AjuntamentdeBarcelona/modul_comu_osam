package cat.bcn.commonmodule.ui.versioncontrol

import cat.bcn.commonmodule.analytics.AnalyticsWrapper
import cat.bcn.commonmodule.crashlytics.InternalCrashlyticsWrapper
import cat.bcn.commonmodule.data.datasource.local.CommonPreferences
import cat.bcn.commonmodule.data.repository.CommonRepository
import cat.bcn.commonmodule.data.datasource.settings.Settings
import cat.bcn.commonmodule.messaging.MessagingWrapper
import cat.bcn.commonmodule.model.Either
import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.model.Text
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.performance.InternalPerformanceWrapper
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.platform.PlatformUtil
import cat.bcn.commonmodule.ui.alert.AlertWrapper
import cat.bcn.commonmodule.ui.executor.Executor
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@ExperimentalCoroutinesApi
class OSAMCommonsInternalTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private lateinit var osamCommonsInternal: OSAMCommonsInternal

    private val backendEndpoint: String = "https://test.com"
    private val executor: Executor = mockk()
    private val settings: Settings = mockk(relaxed = true)
    private val alertWrapper: AlertWrapper = mockk(relaxed = true)
    private val platformInformation: PlatformInformation = mockk(relaxed = true)
    private val internalCrashlyticsWrapper: InternalCrashlyticsWrapper = mockk(relaxed = true)
    private val internalPerformanceWrapper: InternalPerformanceWrapper = mockk(relaxed = true)
    private val analyticsWrapper: AnalyticsWrapper = mockk(relaxed = true)
    private val platformUtil: PlatformUtil = mockk(relaxed = true)
    private val messagingWrapper: MessagingWrapper = mockk(relaxed = true)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Configure the executor mock to return the test dispatcher
        every { executor.main } returns testDispatcher
        every { executor.bg } returns testDispatcher

        osamCommonsInternal = OSAMCommonsInternal(
            backendEndpoint = backendEndpoint,
            executor = executor,
            settings = settings,
            alertWrapper = alertWrapper,
            platformInformation = platformInformation,
            internalCrashlyticsWrapper = internalCrashlyticsWrapper,
            internalPerformanceWrapper = internalPerformanceWrapper,
            analyticsWrapper = analyticsWrapper,
            platformUtil = platformUtil,
            messagingWrapper = messagingWrapper
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `versionControl when FORCE comparison mode should show force update dialog`() {
        // Given
        val language = Language.DEFAULT
        val version = initializeVersion(Version.ComparisonMode.FORCE)
        mockkConstructor(CommonRepository::class)
        coEvery { anyConstructed<CommonRepository>().getVersion(language) } returns Either.Right(version)
        every { alertWrapper.isVersionControlShowing() } returns false

        // When
        osamCommonsInternal.versionControl(language) { }
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
    fun `versionControl when LAZY comparison mode should show lazy update dialog`() {
        // Given
        val language = Language.DEFAULT
        val version = initializeVersion(Version.ComparisonMode.LAZY)

        mockkConstructor(CommonRepository::class)
        mockkConstructor(CommonPreferences::class)
        coEvery { anyConstructed<CommonRepository>().getVersion(language) } returns Either.Right(version)
        every { alertWrapper.isVersionControlShowing() } returns false
        coEvery { anyConstructed<CommonPreferences>().getCheckBoxDontShowAgainActive() } returns true
        // When
        osamCommonsInternal.versionControl(language) { }
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
    fun `versionControl when INFO comparison mode should show info update dialog`() {
        // Given
        val language = Language.DEFAULT
        val version = initializeVersion(Version.ComparisonMode.INFO)
        mockkConstructor(CommonRepository::class)
        mockkConstructor(CommonPreferences::class)
        coEvery { anyConstructed<CommonRepository>().getVersion(language) } returns Either.Right(version)
        every { alertWrapper.isVersionControlShowing() } returns false
        coEvery { anyConstructed<CommonPreferences>().getCheckBoxDontShowAgainActive() } returns true

        // When
        osamCommonsInternal.versionControl(language) { }
        testScheduler.runCurrent()

        // Then
        verify {
            alertWrapper.showVersionControlInfo(
                version = version,
                language = language,
                onPositiveClick = any(),
                onDismissClick = any()
            )
        }
    }

    @Test
    fun `versionControl when NONE comparison mode should show none update dialog`() {
        // Given
        val language = Language.DEFAULT
        val version = initializeVersion(Version.ComparisonMode.NONE)
        val callback: (VersionControlResponse) -> Unit = mockk(relaxed = true)
        mockkConstructor(CommonRepository::class)
        coEvery { anyConstructed<CommonRepository>().getVersion(language) } returns Either.Right(version)
        every { alertWrapper.isVersionControlShowing() } returns false

        // When
        osamCommonsInternal.versionControl(language, callback)
        testScheduler.runCurrent()

        // Then
        verify(exactly = 0) {
            alertWrapper.showVersionControlInfo(any(), any(), any(), any())
            alertWrapper.showVersionControlLazy(any(), any(), any(), any(), any())
            alertWrapper.showVersionControlForce(any(), any(), any())
        }
        verify { callback(VersionControlResponse.DISMISSED) }
    }
}


private fun initializeVersion(comparisonMode: Version.ComparisonMode) =
    Version(
        packageName = "cat.bcn.commonmodule",
        versionCode = 1,
        versionName = "1.0.0",
        platform = Platform.ANDROID,
        comparisonMode = comparisonMode,
        startDate = 100L,
        endDate = 300L,
        serverDate = 200L,
        title = Text("Título", "Title", "Títol"),
        message = Text("Mensaje", "Message", "Missatge"),
        ok = Text("Aceptar", "Accept", "Acceptar"),
        cancel = Text("Cancelar", "Cancel", "Cancel·lar"),
        url = "https://test.com"
    )