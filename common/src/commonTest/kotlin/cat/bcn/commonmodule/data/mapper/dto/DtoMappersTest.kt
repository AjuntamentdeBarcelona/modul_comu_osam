package cat.bcn.commonmodule.data.mapper.dto

import cat.bcn.commonmodule.data.datasource.models.dto.VersionResponseDto
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class DtoMappersTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `toModel maps models list from json array`() {
        val dto = decodeVersionDto()

        val mapped = dto.toModel()

        assertEquals(listOf("Test1", "Test2"), mapped.modelsData.models)
    }

    private fun decodeVersionDto() =
        json.decodeFromString<VersionResponseDto>(
            """
            {
              "data": {
                "id": 1,
                "appId": 1,
                "packageName": "pkg",
                "versionCode": 1,
                "versionName": "1.0.0",
                "platform": "ANDROID",
                "comparisonMode": "LAZY",
                "serverDate": 1,
                "title": { "es": "title", "en": "title", "ca": "title" },
                "message": { "es": "message", "en": "message", "ca": "message" },
                "ok": { "es": "ok", "en": "ok", "ca": "ok" },
                "cancel": { "es": "cancel", "en": "cancel", "ca": "cancel" },
                "url": "https://example.com",
                "checkBoxDontShowAgain": false,
                "dialogDisplayDuration": 3600,
                "models": ["Test1", "Test2"]
              }
            }
            """.trimIndent()
        ).data
}
