package cat.bcn.commonmodule.data.datasource.local

import cat.bcn.commonmodule.model.Topic
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.ui.versioncontrol.Language

object TopicPreferencesUtils
{
    internal fun getOldTopicWithPreviousLanguage(platformInformation: PlatformInformation, preferences: Preferences): Topic {
        return Topic(
            appName = platformInformation.getSmallPackageName(),
            versionName = platformInformation.getVersionName(),
            versionCode = platformInformation.getVersionCode(),
            languageCode = preferences.getPreviousLanguage()
        )
    }

    internal fun getOldTopicWithPreviousVersion(platformInformation: PlatformInformation, preferences: Preferences, language: Language): Topic {
        return Topic(
            appName = platformInformation.getSmallPackageName(),
            versionName = preferences.getVersionControlVersionNamePrevious(),
            versionCode = platformInformation.getVersionCode(),
            languageCode = language.name
        )
    }
}