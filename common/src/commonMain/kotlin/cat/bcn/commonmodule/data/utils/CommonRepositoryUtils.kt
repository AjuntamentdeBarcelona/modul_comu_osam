package cat.bcn.commonmodule.data.utils

import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.data.datasource.remote.Remote
import cat.bcn.commonmodule.extensions.getCurrentDate
import cat.bcn.commonmodule.model.CheckBoxDontShowAgain
import cat.bcn.commonmodule.model.Text
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.performance.InternalPerformanceWrapper
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.ui.versioncontrol.Language
import kotlin.time.Duration.Companion.seconds

/**
 * Utility object for common operations related to version control in the repository.
 */
object CommonRepositoryUtils {

    private const val DONT_SHOW_AGAIN_ES = "No mostrar de nuevo"
    private const val DONT_SHOW_AGAIN_EN = "Don't show again"
    private const val DONT_SHOW_AGAIN_CA = "No mostrar de nou"

    /**
     * Gets the latest version information from a remote source and updates the cached version
     * if necessary. Also checks if the checkbox state needs to be updated based on version changes.
     */
    internal suspend fun getRemoteVersion(
        remote: Remote,
        internalPerformanceWrapper: InternalPerformanceWrapper,
        platformInformation: PlatformInformation,
        preferences: Preferences,
    ): Version {
        val remoteVersion = remote.getVersion(
            internalPerformanceWrapper,
            platformInformation.getPackageName(),
            platformInformation.getPlatform(),
            platformInformation.getVersionCode()
        )

        val cachedVersion = getCachedVersion(platformInformation, preferences)

        // Update Checkbox
        val updatedCheckbox =
            remoteVersion.checkBoxDontShowAgain.copy(
                isCheckBoxVisible = remoteVersion.checkBoxDontShowAgain.isCheckBoxVisible,
                text = setCheckBoxText()
            )

        val isCheckBoxChanged = hasVersionChanged(cachedVersion, remoteVersion, preferences)
        println("CommonRepositoryUtils - isCheckBoxChanged: $isCheckBoxChanged")
        if(isCheckBoxChanged){
            preferences.setLastTimeUserClickedOnAcceptButton(0)
            preferences.setCheckBoxDontShowAgainActive(true)
        }
        val remoteFinalVersion = remoteVersion.copy(checkBoxDontShowAgain = updatedCheckbox)
        setCachedVersion(preferences, remoteFinalVersion, platformInformation)

        return remoteFinalVersion
    }

    /**
     * Retrieves the locally cached version information using platform details and preferences.
     * This is useful for comparing against the remotely fetched version.
     */
    internal fun getCachedVersion(
        platformInformation: PlatformInformation,
        preferences: Preferences,
    ): Version {
        val cachedVersion = Version(
            packageName = platformInformation.getPackageName(),
            versionCode = preferences.getVersionControlVersionCode(),
            versionName = platformInformation.getVersionName(),
            platform = platformInformation.getPlatform(),
            comparisonMode = preferences.getVersionControlComparisonMode(),
            startDate = preferences.getVersionStartDate(),
            endDate = preferences.getVersionEndDate(),
            serverDate = getCurrentDate(),
            title = Text(
                es = preferences.getVersionControlTitleEs(),
                en = preferences.getVersionControlTitleEn(),
                ca = preferences.getVersionControlTitleCa()
            ),
            message = Text(
                es = preferences.getVersionControlMessageEs(),
                en = preferences.getVersionControlMessageEn(),
                ca = preferences.getVersionControlMessageCa()
            ),
            ok = Text(
                es = preferences.getVersionControlOkEs(),
                en = preferences.getVersionControlOkEn(),
                ca = preferences.getVersionControlOkCa()
            ),
            cancel = Text(
                es = preferences.getVersionControlCancelEs(),
                en = preferences.getVersionControlCancelEn(),
                ca = preferences.getVersionControlCancelCa(),
            ),
            url = preferences.getVersionControlUrl(),
            checkBoxDontShowAgain = CheckBoxDontShowAgain(
                isCheckBoxVisible = preferences.getCheckBoxDontShowAgainVisible(),
                text = setCheckBoxText()
            ),
            dialogDisplayDuration = preferences.getDialogDisplayDuration()
        )
        return cachedVersion
    }

    /**
     * Stores the given version data in the Preferences as the new cached version.
     * Localizes text resources for different languages before saving.
     */
    internal fun setCachedVersion(
        preferences: Preferences,
        versionResult: Version,
        platformInformation: PlatformInformation,
    ) {
        preferences.setVersionControlTitleEs(versionResult.title.localize(Language.ES))
        preferences.setVersionControlTitleEn(versionResult.title.localize(Language.EN))
        preferences.setVersionControlTitleCa(versionResult.title.localize(Language.CA))
        preferences.setVersionControlMessageEs(versionResult.message.localize(Language.ES))
        preferences.setVersionControlMessageEn(versionResult.message.localize(Language.EN))
        preferences.setVersionControlMessageCa(versionResult.message.localize(Language.CA))
        preferences.setVersionControlOkEs(versionResult.ok.localize(Language.ES))
        preferences.setVersionControlOkEn(versionResult.ok.localize(Language.EN))
        preferences.setVersionControlOkCa(versionResult.ok.localize(Language.CA))
        preferences.setVersionControlCancelEs(versionResult.cancel.localize(Language.ES))
        preferences.setVersionControlCancelEn(versionResult.cancel.localize(Language.EN))
        preferences.setVersionControlCancelCa(versionResult.cancel.localize(Language.CA))
        preferences.setVersionControlUrl(versionResult.url)
        preferences.setVersionControlComparisonMode(versionResult.comparisonMode)
        preferences.setVersionStartDate(versionResult.startDate)
        preferences.setVersionEndDate(versionResult.endDate)
        preferences.setVersionControlVersionCode(platformInformation.getVersionCode())
        preferences.setVersionControlRemoteVersionCode(versionResult.versionCode)
        preferences.setCheckBoxDontShowAgainVisible(versionResult.checkBoxDontShowAgain.isCheckBoxVisible)
        preferences.setDialogDisplayDuration(versionResult.dialogDisplayDuration)
    }

    /**
     * Returns a 'Text' object with localized "Don't show again" labels.
     */
    private fun setCheckBoxText() =
        Text(
            es = DONT_SHOW_AGAIN_ES,
            en = DONT_SHOW_AGAIN_EN,
            ca = DONT_SHOW_AGAIN_CA
        )

    /**
     * Compares cached version data with remote version data to determine if
     * anything significant has changed.
     */
    internal fun hasVersionChanged(cachedVersion: Version, remoteVersion: Version, preferences: Preferences): Boolean {

        // Check if the version code has changed.
        // If the user previously chose "Don't show again" without updating the app,
        // we store the last remote version code in preferences (getVersionControlRemoteVersionCode()).
        // This way, even if the cached version code is outdated, the dialog won't show again
        // because we can detect that the remote version is already acknowledged by the user.
        if (cachedVersion.versionCode != remoteVersion.versionCode &&
            preferences.getVersionControlRemoteVersionCode() != remoteVersion.versionCode) {
            println("Version changed: versionCode (cached=${cachedVersion.versionCode}, remote=${remoteVersion.versionCode})")
            return true
        }
        if (cachedVersion.comparisonMode != remoteVersion.comparisonMode) {
            println("Version changed: comparisonMode (cached=${cachedVersion.comparisonMode}, remote=${remoteVersion.comparisonMode})")
            return true
        }
        if (cachedVersion.startDate != remoteVersion.startDate) {
            println("Version changed: startDate (cached=${cachedVersion.startDate}, remote=${remoteVersion.startDate})")
            return true
        }
        if (cachedVersion.endDate != remoteVersion.endDate) {
            println("Version changed: endDate (cached=${cachedVersion.endDate}, remote=${remoteVersion.endDate})")
            return true
        }
        if (cachedVersion.title != remoteVersion.title) {
            println("Version changed: title (cached=${cachedVersion.title}, remote=${remoteVersion.title})")
            return true
        }
        if (cachedVersion.message != remoteVersion.message) {
            println("Version changed: message (cached=${cachedVersion.message}, remote=${remoteVersion.message})")
            return true
        }
        if (cachedVersion.ok != remoteVersion.ok) {
            println("Version changed: ok button text (cached=${cachedVersion.ok}, remote=${remoteVersion.ok})")
            return true
        }
        if (cachedVersion.cancel != remoteVersion.cancel) {
            println("Version changed: cancel button text (cached=${cachedVersion.cancel}, remote=${remoteVersion.cancel})")
            return true
        }
        if (cachedVersion.url != remoteVersion.url) {
            println("Version changed: url (cached=${cachedVersion.url}, remote=${remoteVersion.url})")
            return true
        }
        if (cachedVersion.checkBoxDontShowAgain.isCheckBoxVisible != remoteVersion.checkBoxDontShowAgain.isCheckBoxVisible) {
            println("Version changed: isCheckBoxVisible (cached=${cachedVersion.checkBoxDontShowAgain.isCheckBoxVisible}, remote=${remoteVersion.checkBoxDontShowAgain.isCheckBoxVisible})")
            return true
        }
        if (cachedVersion.dialogDisplayDuration != remoteVersion.dialogDisplayDuration) {
            println("Version changed: dialogDisplayDuration (cached=${cachedVersion.dialogDisplayDuration}, remote=${remoteVersion.dialogDisplayDuration})")
            return true
        }

        println("Version has not changed.")
        return false
    }

    fun isDialogDurationOver(lastTimeUserClickedOnButton: Long, dialogDisplayDuration: Long): Boolean {
        return (getCurrentDate() - lastTimeUserClickedOnButton) >= (dialogDisplayDuration.seconds.inWholeMilliseconds)
    }

}