package cat.bcn.commonmodule.ui.logic.event

import cat.bcn.commonmodule.crashlytics.InternalCrashlyticsWrapper
import cat.bcn.commonmodule.data.repository.CommonRepository
import cat.bcn.commonmodule.model.AppInformation
import cat.bcn.commonmodule.model.CommonError
import cat.bcn.commonmodule.model.DeviceInformation
import cat.bcn.commonmodule.model.Either
import cat.bcn.commonmodule.ui.executor.Executor
import cat.bcn.commonmodule.ui.versioncontrol.AppInformationResponse
import cat.bcn.commonmodule.ui.versioncontrol.DeviceInformationResponse
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class InfoEventTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val testScope = TestScope(testDispatcher)

    // Mocks
    private val executor = mock<Executor>()
    private val repository = mock<CommonRepository>()
    private val crashlytics = mock<InternalCrashlyticsWrapper>()

    private lateinit var infoEvent: InfoEvent

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Stub Executor to run on our test thread
        every { executor.main } returns testDispatcher
        every { executor.bg } returns testDispatcher

        infoEvent = InfoEvent(
            scope = testScope,
            executor = executor,
            commonRepository = repository,
            internalCrashlyticsWrapper = crashlytics
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region Device Information Tests

    @Test
    fun `deviceInformation returns ACCEPTED and data on success`() = runTest(testScheduler) {
        // Given
        val deviceInfo = DeviceInformation(
            platformName = "platformName",
            platformVersion = "platformVersion",
            platformModel = "platformModel"
        )
        everySuspend { repository.getDeviceInformation() } returns Either.Right(deviceInfo)

        // When
        var statusResult: DeviceInformationResponse? = null
        var dataResult: DeviceInformation? = null

        infoEvent.deviceInformation { status, data ->
            statusResult = status
            dataResult = data
        }
        testScheduler.runCurrent()

        // Then
        assertEquals(DeviceInformationResponse.ACCEPTED, statusResult)
        assertEquals(deviceInfo, dataResult)
    }

    @Test
    fun `deviceInformation returns ERROR and null on failure`() = runTest(testScheduler) {
        // Given
        val error = CommonError(Exception("Device info failed"))
        everySuspend { repository.getDeviceInformation() } returns Either.Left(error)
        every { crashlytics.recordException(any()) } returns Unit

        // When
        var statusResult: DeviceInformationResponse? = null
        var dataResult: DeviceInformation? = null

        infoEvent.deviceInformation { status, data ->
            statusResult = status
            dataResult = data
        }
        testScheduler.runCurrent()

        // Then
        verify { crashlytics.recordException(error.exception) }
        assertEquals(DeviceInformationResponse.ERROR, statusResult)
        assertNull(dataResult)
    }

    @Test
    fun `deviceInformation handles unexpected exceptions`() = runTest(testScheduler) {
        // Given
        val exception = RuntimeException("Unexpected crash")
        // Simulate a crash inside the coroutine (e.g. repository throws instead of returning Left)
        everySuspend { repository.getDeviceInformation() } throws exception
        every { crashlytics.recordException(any()) } returns Unit

        // When
        var statusResult: DeviceInformationResponse? = null

        infoEvent.deviceInformation { status, _ ->
            statusResult = status
        }
        testScheduler.runCurrent()

        // Then
        verify { crashlytics.recordException(any()) }
        assertEquals(DeviceInformationResponse.ERROR, statusResult)
    }

    // endregion

    // region App Information Tests

    @Test
    fun `appInformation returns ACCEPTED and data on success`() = runTest(testScheduler) {
        // Given
        val appInfo = AppInformation(
            appName = "appName",
            appVersionCode = "1",
            appVersionName = "1.0.0",

        )
        everySuspend { repository.getAppInformation() } returns Either.Right(appInfo)

        // When
        var statusResult: AppInformationResponse? = null
        var dataResult: AppInformation? = null

        infoEvent.appInformation { status, data ->
            statusResult = status
            dataResult = data
        }
        testScheduler.runCurrent()

        // Then
        assertEquals(AppInformationResponse.ACCEPTED, statusResult)
        assertEquals(appInfo, dataResult)
    }

    @Test
    fun `appInformation returns ERROR and null on failure`() = runTest(testScheduler) {
        // Given
        val error = CommonError(Exception("App info failed"))
        everySuspend { repository.getAppInformation() } returns Either.Left(error)
        every { crashlytics.recordException(any()) } returns Unit

        // When
        var statusResult: AppInformationResponse? = null
        var dataResult: AppInformation? = null

        infoEvent.appInformation { status, data ->
            statusResult = status
            dataResult = data
        }
        testScheduler.runCurrent()

        // Then
        verify { crashlytics.recordException(error.exception) }
        assertEquals(AppInformationResponse.ERROR, statusResult)
        assertNull(dataResult)
    }

    @Test
    fun `appInformation handles unexpected exceptions`() = runTest(testScheduler) {
        // Given
        val exception = RuntimeException("Unexpected crash")
        everySuspend { repository.getAppInformation() } throws exception
        every { crashlytics.recordException(any()) } returns Unit

        // When
        var statusResult: AppInformationResponse? = null

        infoEvent.appInformation { status, _ ->
            statusResult = status
        }
        testScheduler.runCurrent()

        // Then
        verify { crashlytics.recordException(any()) }
        assertEquals(AppInformationResponse.ERROR, statusResult)
    }

    // endregion
}