package cat.bcn.commonmodule.ui.logic.event

import cat.bcn.commonmodule.analytics.CommonAnalytics
import cat.bcn.commonmodule.crashlytics.InternalCrashlyticsWrapper
import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.data.datasource.local.TopicPreferencesUtils
import cat.bcn.commonmodule.messaging.TopicSubscriptionManager
import cat.bcn.commonmodule.model.Topic
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.ui.executor.Executor
import cat.bcn.commonmodule.ui.versioncontrol.AppLanguageResponse
import cat.bcn.commonmodule.ui.versioncontrol.Language
import cat.bcn.commonmodule.ui.versioncontrol.SubscriptionResponse
import cat.bcn.commonmodule.ui.versioncontrol.TokenResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Manages business logic for user-driven events within the common module.
 *
 * @param topicSubscriptionManager Handles Firebase topic subscriptions.
 * @param preferences Provides access to local storage.
 * @param platformInformation Provides platform-specific details like version and package name.
 * @param analytics Handles logging of analytics events.
 * @param internalCrashlyticsWrapper Handles crash reporting.
 * @param executor Provides coroutine dispatchers for main and background threads.
 */
internal class SubscriptionsEvent(
    private val scope: CoroutineScope,
    private val topicSubscriptionManager: TopicSubscriptionManager,
    private val preferences: Preferences,
    private val platformInformation: PlatformInformation,
    private val analytics: CommonAnalytics,
    private val internalCrashlyticsWrapper: InternalCrashlyticsWrapper,
    private val executor: Executor,
) {
    companion object Companion {
        private const val ERROR_LOG = "An exception occurred during topic subscription update:"
        private const val ERROR_TOKEN_LOG = "An exception occurred while fetching the FCM token:"
        private const val FCM_TOKEN = "FCM Token:"
    }

    /**
     * Handles all logic associated with a user changing the application language.
     *
     * This function performs the following actions:
     * 1. Checks if the language has actually changed.
     * 2. Updates local preferences with the new and previous language.
     * 3. Logs a language change analytics event.
     * 4. Asynchronously updates the Firebase Messaging topic subscription.
     *
     * @param language The new [Language] selected by the user.
     * @param f A callback that returns the result of the operation.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun changeLanguageEvent(
        language: Language,
        f: (AppLanguageResponse) -> Unit,
    ) {
        val response = sendLanguageChangeAnalytic(language)
        when (response) {
            AppLanguageResponse.UNCHANGED -> f(AppLanguageResponse.UNCHANGED)
            AppLanguageResponse.ERROR -> f(AppLanguageResponse.ERROR)
            AppLanguageResponse.SUCCESS -> {
                scope.launch(executor.main) {
                    try {
                        withContext(executor.bg) {
                            val oldTopic = TopicPreferencesUtils.getOldTopicWithPreviousLanguage(platformInformation, preferences)
                            val newTopic = Topic(
                                appName = platformInformation.getSmallPackageName(),
                                versionName = platformInformation.getVersionName(),
                                versionCode = platformInformation.getVersionCode(),
                                languageCode = language.name
                            )
                            topicSubscriptionManager.subscriptionToLanguageChange(
                                oldTopic = oldTopic,
                                newTopic = newTopic
                            )
                        }.fold(
                            error = { commonError ->
                                internalCrashlyticsWrapper.recordException(commonError.exception)
                                println("$ERROR_LOG ${commonError.exception.message}")
                                f(AppLanguageResponse.ERROR)
                            },
                            success = { wasSuccessful ->
                                if (wasSuccessful) {
                                    f(AppLanguageResponse.SUCCESS)
                                } else {
                                    f(AppLanguageResponse.UNCHANGED)
                                }
                            }
                        )
                    } catch (e: Exception) {
                        internalCrashlyticsWrapper.recordException(e)
                        println("$ERROR_LOG ${e.message}")
                        f(AppLanguageResponse.ERROR)
                    }
                }
            }
        }


    }

    @OptIn(DelicateCoroutinesApi::class)
    fun firstTimeOrUpdateAppEvent(
        language: Language,
        f: (AppLanguageResponse) -> Unit,
    ) {
        scope.launch(executor.main) {
            try {
                var newTopic: Topic? = null
                var isFirstTime = false
                withContext(executor.bg) {
                    val oldTopic = TopicPreferencesUtils.getOldTopicWithPreviousVersion(platformInformation, preferences, language)
                    isFirstTime = oldTopic.versionName.isEmpty()
                    // 2. Create it here (on Background Thread)
                    val createdTopic = Topic(
                        appName = platformInformation.getSmallPackageName(),
                        versionName = platformInformation.getVersionName(),
                        versionCode = platformInformation.getVersionCode(),
                        languageCode = language.name
                    )

                    // 3. Save it to the outer variable
                    newTopic = createdTopic

                    topicSubscriptionManager.subscriptionToAppInitializationOrUpdates(
                        newTopic = createdTopic,
                        oldTopic = oldTopic
                    )
                }.fold(
                    error = { commonError ->
                        internalCrashlyticsWrapper.recordException(commonError.exception)
                        println("$ERROR_LOG ${commonError.exception.message}")
                        f(AppLanguageResponse.ERROR)
                    },
                    success = { wasSuccessful ->
                        if (wasSuccessful) {
                            if(isFirstTime) { sendLanguageChangeAnalytic(language, true) }
                            preferences.setVersionControlVersionNamePrevious(newTopic?.versionName ?: "")
                            f(AppLanguageResponse.SUCCESS)
                        } else {
                            f(AppLanguageResponse.UNCHANGED)
                        }
                    }
                )

            } catch (e: Exception) {
                internalCrashlyticsWrapper.recordException(e)
                println("$ERROR_LOG ${e.message}")
                f(AppLanguageResponse.ERROR)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun subscribeToCustomTopic(topic: String, f: (SubscriptionResponse) -> Unit) {
        scope.launch(executor.main) {
            try {
                withContext(executor.bg) { topicSubscriptionManager.subscribeToCustomTopic(topic) }.fold(
                    error = { commonError ->
                        internalCrashlyticsWrapper.recordException(commonError.exception)
                    },
                    success = { response ->
                        f(response)
                    }
                )
            } catch (e: Exception) {
                internalCrashlyticsWrapper.recordException(e)
                f(SubscriptionResponse.ERROR)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun unsubscribeToCustomTopic(topic: String, f: (SubscriptionResponse) -> Unit) {
        scope.launch(executor.main) {
            try {
                withContext(executor.bg) { topicSubscriptionManager.unsubscribeToCustomTopic(topic) }.fold(
                    error = { commonError ->
                        internalCrashlyticsWrapper.recordException(commonError.exception)
                    },
                    success = { response ->
                        f(response)
                    }
                )
            } catch (e: Exception) {
                internalCrashlyticsWrapper.recordException(e)
                f(SubscriptionResponse.ERROR)
            }
        }
    }

    /**
     * Asynchronously retrieves the current Firebase Cloud Messaging (FCM) registration token.
     *
     * This function performs the token retrieval on a background thread and delivers
     * the result via a callback on the main thread, making it safe to use for UI updates.
     *
     * @param f A callback that receives a [TokenResponse] object, which will either
     *          be [TokenResponse.Success] containing the token or [TokenResponse.Error]
     *          containing the error details.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun getFCMToken(f: (TokenResponse) -> Unit) {
        // Launch a coroutine on the main thread.
        scope.launch(executor.main) {
            try {
                // Switch to a background thread to perform the network operation.
                val result = withContext(executor.bg) {
                    topicSubscriptionManager.getFCMToken()
                }
                result.fold(
                    error = { commonError ->
                        f(TokenResponse.Error(commonError.exception))
                    },
                    success = { token ->
                        println("$FCM_TOKEN $token")
                        f(TokenResponse.Success(token))
                    }
                )

            } catch (e: Exception) {
                // If any other exception occurs, deliver it as an error on the main thread.
                internalCrashlyticsWrapper.recordException(e)
                println("$ERROR_TOKEN_LOG ${e.message}")
                f(TokenResponse.Error(e))
            }
        }
    }

    /**
     * Handles the synchronous part of a language change event, updating preferences
     * and logging analytics.
     *
     * @return `true` if the synchronous operations were successful and the asynchronous
     * part should continue, `false` otherwise.
     */
    private fun sendLanguageChangeAnalytic(
        language: Language,
        isFirstTime: Boolean = false,
    ): AppLanguageResponse {
        val oldSelectedLanguage = preferences.getSelectedLanguage()

        // If the language hasn't changed, do nothing.
        if (oldSelectedLanguage == language.name) {
            return AppLanguageResponse.UNCHANGED
        }

        try {
            preferences.setDisplayedLanguage(platformInformation.getDeviceLanguage())
            preferences.setSelectedLanguage(language.name)
            preferences.setPreviousLanguage(oldSelectedLanguage)

            val previousLanguage = if(isFirstTime) "" else preferences.getPreviousLanguage()

            analytics.logLanguageChange(
                previousLanguage = previousLanguage,
                selectedLanguage = language.name,
                languageDisplay = preferences.getDisplayedLanguage(),
            )
        } catch (e: Exception) {
            internalCrashlyticsWrapper.recordException(e)
            return AppLanguageResponse.ERROR
        }

        return AppLanguageResponse.SUCCESS
    }
}