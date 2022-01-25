package cat.bcn.commonmodule.analytics

class CommonAnalytics(private val wrapper: AnalyticsWrapper) {

    companion object {
        private const val EVENT_NAME_VERSION_CONTROL = "OSAM_COMMONS_SHOW_VERSIONCONTROL_DIALOG"
        private const val EVENT_NAME_RATING = "OSAM_COMMONS_SHOW_RATING_DIALOG"

        private const val ITEM_ID_KEY = "item_id"
        private const val ITEM_NAME_KEY = "item_name"
        private const val ITEM_ID_VALUE = "osamcommons"
    }

    enum class RatingAction {
        SHOWN, ACCEPTED, CANCELLED, LATER;

        fun toEventString(): String {
            return when (this) {
                SHOWN -> "rating_popup_showed"
                ACCEPTED -> "rating_popup_accepted"
                CANCELLED -> "rating_popup_cancelled"
                LATER -> "rating_popup_later"
            }
        }
    }

    enum class VersionControlAction {
        SHOWN, ACCEPTED, CANCELLED;

        fun toEventString(): String {
            return when (this) {
                SHOWN -> "version_control_popup_showed"
                ACCEPTED -> "version_control_popup_accepted"
                CANCELLED -> "version_control_popup_cancelled"
            }
        }
    }

    fun logRatingPopUp(action: RatingAction) {
        trackActionWithWrapper(
            EVENT_NAME_RATING,
            mapOf(ITEM_ID_KEY to ITEM_ID_VALUE, ITEM_NAME_KEY to action.toEventString())
        )
    }

    fun logVersionControlPopUp(action: VersionControlAction) {
        trackActionWithWrapper(
            EVENT_NAME_VERSION_CONTROL,
            mapOf(ITEM_ID_KEY to ITEM_ID_VALUE, ITEM_NAME_KEY to action.toEventString())
        )
    }

    private fun trackActionWithWrapper(eventName: String, params: Map<String, String>) {
        wrapper.logEvent(
            eventName.adaptedToFirebaseKey(),
            params.map {
                it.key.adaptedToFirebaseKey() to it.value.adaptedToFirebaseValue()
            }.toMap()
        )
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