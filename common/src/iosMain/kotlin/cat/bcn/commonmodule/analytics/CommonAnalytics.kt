package cat.bcn.commonmodule.analytics

import cocoapods.FirebaseAnalytics.*

actual class CommonAnalytics : Analytics {

    companion object {
        const val EVENT_NAME_VERSION_CONTROL = "OSAM_COMMONS_SHOW_VERSIONCONTROL_DIALOG"
        const val EVENT_NAME_RATING = "OSAM_COMMONS_SHOW_RATING_DIALOG"
    }


    override fun logRatingPopUp(params: Map<String, String>) {
        sendLog(
            eventName = EVENT_NAME_RATING.adaptedToFirebaseKey(),
            params = params.toFirebaseDictionary()
        )
    }

    override fun logVersionControlPopUp(params: Map<String, String>) {
        sendLog(
            eventName = EVENT_NAME_VERSION_CONTROL.adaptedToFirebaseKey(),
            params = params.toFirebaseDictionary()
        )
    }

    private fun sendLog(eventName: String, params: Map<String, String>) {
        FIRAnalytics.logEventWithName(eventName, params.toMap())
    }

    private fun Map<String, String>.toFirebaseDictionary(): Map<String, String> =
        mutableMapOf<String, String>().also { returnMap ->
            this.forEach { returnMap[it.key.adaptedToFirebaseKey()] = it.value.adaptedToFirebaseValue() }
        }

    /**
     * @return A new string in which dashes have been replaced by underscores and characters that are not alphanumeric
     * or underscores have been removed. The new string only takes the first 40 characters of the original string.
     */
    private fun String.adaptedToFirebaseKey(): String = replace(Regex("-"), "_")
        .replace(Regex("\\W"), "")
        .take(40)

    /**
     * @return A new new string that only takes the first 100 characters of the original string.
     */
    private fun String.adaptedToFirebaseValue(): String = take(100)
}