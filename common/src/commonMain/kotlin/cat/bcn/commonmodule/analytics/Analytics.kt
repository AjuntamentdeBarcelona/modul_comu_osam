package cat.bcn.commonmodule.analytics

interface Analytics {
    fun logRatingPopUp(params: Map<String, String> = mapOf())
    fun logVersionControlPopUp(params: Map<String, String> = mapOf())
}
