package cat.bcn.commonmodule.data.datasource.local

import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.ui.versioncontrol.Language
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class TopicPreferencesUtilsTest {

    private val platformInformation = mock<PlatformInformation>()
    private val preferences = mock<Preferences>()

    @Test
    fun `getOldTopicWithPreviousLanguage maps correctly`() {
        // Given
        every { platformInformation.getSmallPackageName() } returns "appName"
        every { platformInformation.getVersionName() } returns "1.0.0"
        every { platformInformation.getVersionCode() } returns 10L
        every { preferences.getPreviousLanguage() } returns "CA"

        // When
        val topic = TopicPreferencesUtils.getOldTopicWithPreviousLanguage(platformInformation, preferences)

        // Then
        assertEquals("appName", topic.appName)
        assertEquals("1.0.0", topic.versionName)
        assertEquals(10L, topic.versionCode)
        assertEquals("CA", topic.languageCode)
    }

    @Test
    fun `getOldTopicWithPreviousVersion maps correctly`() {
        // Given
        val language = Language.ES
        every { platformInformation.getSmallPackageName() } returns "appName"
        every { preferences.getVersionControlVersionNamePrevious() } returns "0.9.0"
        every { platformInformation.getVersionCode() } returns 10L

        // When
        val topic = TopicPreferencesUtils.getOldTopicWithPreviousVersion(platformInformation, preferences, language)

        // Then
        assertEquals("appName", topic.appName)
        assertEquals("0.9.0", topic.versionName)
        assertEquals(10L, topic.versionCode)
        assertEquals("ES", topic.languageCode)
    }
}