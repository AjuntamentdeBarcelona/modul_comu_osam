package cat.bcn.commonmodule.ui.strings

import cat.bcn.commonmodule.ui.versioncontrol.Language

class Strings {

    companion object {
        const val DIALOG_RATING_TITLE = "dialog_rating_title"
        const val DIALOG_RATING_POSITIVE = "dialog_rating_positive"
        const val DIALOG_RATING_NEGATIVE = "dialog_rating_negative"
        const val DIALOG_RATING_NEUTRAL = "dialog_rating_neutral"

        fun getString(key: String, language: Language): String {
            val value = CA[key] ?: ""
            when (language) {
                Language.CA -> CA[key]
                Language.ES -> ES[key]
                Language.EN -> EN[key]
            }
            return value;
        }

        private val CA: Map<String, String> = hashMapOf(
            DIALOG_RATING_TITLE to "Valorar",
            DIALOG_RATING_POSITIVE to "VALORAR ARA",
            DIALOG_RATING_NEGATIVE to "NO, GRÀCIES",
            DIALOG_RATING_NEUTRAL to "MÉS TARD"
        )

        private val ES: Map<String, String> = hashMapOf(
            DIALOG_RATING_TITLE to "Valorar",
            DIALOG_RATING_POSITIVE to "VALORAR AHORA",
            DIALOG_RATING_NEGATIVE to "NO, GRACIAS",
            DIALOG_RATING_NEUTRAL to "MÁS TARDE",
        )

        private val EN: Map<String, String> = hashMapOf(
            DIALOG_RATING_TITLE to "Rate",
            DIALOG_RATING_POSITIVE to "RATE NOW",
            DIALOG_RATING_NEGATIVE to "NO, THANKS",
            DIALOG_RATING_NEUTRAL to "LATER",
        )
    }
}