package cat.bcn.commonmodule.analytics

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle


@SuppressLint("MissingPermission")
actual class CommonAnalytics(context: Context) : Analytics {

    companion object {
        const val RATING_POPUP = "rating_popup_showed"
    }

    //No DI on this project
    override fun logRatingPopUp(params: Map<String, Any>) {
    }

}