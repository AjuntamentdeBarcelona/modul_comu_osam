package cat.bcn.commonmodule.ui.logic.event

import cat.bcn.commonmodule.crashlytics.InternalCrashlyticsWrapper
import cat.bcn.commonmodule.data.repository.CommonRepository
import cat.bcn.commonmodule.model.AppInformation
import cat.bcn.commonmodule.model.DeviceInformation
import cat.bcn.commonmodule.ui.executor.Executor
import cat.bcn.commonmodule.ui.versioncontrol.AppInformationResponse
import cat.bcn.commonmodule.ui.versioncontrol.DeviceInformationResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class InfoEvent(
    private val scope: CoroutineScope,
    private val executor: Executor,
    private val commonRepository: CommonRepository,
    private val internalCrashlyticsWrapper: InternalCrashlyticsWrapper
) {
    /**
     * Retrieves device-specific information asynchronously.
     *
     * This function fetches details about the device (e.g., model, OS version) from the repository.
     * It executes the request on a background thread and delivers the result via the provided callback
     * on the main thread. Any exceptions encountered during the process are recorded via the
     * crashlytics wrapper.
     *
     * @param f A callback function invoked with the result of the operation.
     *          It receives a [DeviceInformationResponse] status (ACCEPTED or ERROR) and a nullable
     *          [DeviceInformation] object containing the data if successful.
     */
    fun deviceInformation(
        f: (DeviceInformationResponse, DeviceInformation?) -> Unit,
    ) {
        scope.launch(executor.main) {
            try {
                withContext(executor.bg) { commonRepository.getDeviceInformation() }.fold(
                    error = { commonError ->
                        internalCrashlyticsWrapper.recordException(commonError.exception)
                        f(DeviceInformationResponse.ERROR, null)
                    },
                    success = { deviceInformation ->
                        f(DeviceInformationResponse.ACCEPTED, deviceInformation)
                    }
                )
            } catch (e: Exception) {
                internalCrashlyticsWrapper.recordException(e)
                f(DeviceInformationResponse.ERROR, null)
            }
        }
    }

    /**
     * Retrieves application-specific information asynchronously.
     *
     * This function fetches details about the application (e.g., version name, version code, package name)
     * from the repository. It executes the request on a background thread and delivers the result via
     * the provided callback on the main thread. Any exceptions encountered during the process are
     * recorded via the crashlytics wrapper.
     *
     * @param f A callback function invoked with the result of the operation.
     *          It receives an [AppInformationResponse] status (ACCEPTED or ERROR) and a nullable
     *          [AppInformation] object containing the data if successful.
     */
    fun appInformation(
        f: (AppInformationResponse, AppInformation?) -> Unit
    ) {
        scope.launch(executor.main) {
            try {
                withContext(executor.bg) { commonRepository.getAppInformation() }.fold(
                    error = { commonError ->
                        internalCrashlyticsWrapper.recordException(commonError.exception)
                        f(AppInformationResponse.ERROR, null)
                    },
                    success = { appInformation ->
                        f(AppInformationResponse.ACCEPTED, appInformation)
                    }
                )
            } catch (e: Exception) {
                internalCrashlyticsWrapper.recordException(e)
                f(AppInformationResponse.ERROR, null)
            }
        }
    }
}