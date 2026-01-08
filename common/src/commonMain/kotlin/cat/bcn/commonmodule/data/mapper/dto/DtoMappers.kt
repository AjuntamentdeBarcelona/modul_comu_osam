package cat.bcn.commonmodule.data.mapper.dto

import cat.bcn.commonmodule.data.datasource.models.dto.RatingDto
import cat.bcn.commonmodule.data.datasource.models.dto.TextDto
import cat.bcn.commonmodule.data.datasource.models.dto.VersionDto
import cat.bcn.commonmodule.model.CheckBoxDontShowAgain
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Text
import cat.bcn.commonmodule.model.Version

internal fun VersionDto.toModel(): Version = Version(
    packageName = packageName,
    versionCode = versionCode,
    versionName = versionName,
    platform = platform,
    comparisonMode = comparisonMode,
    startDate = startDate ?: 0L,
    endDate = endDate ?: Long.MAX_VALUE,
    serverDate = serverDate,
    title = title.toModel(),
    message = message.toModel(),
    ok = ok.toModel(),
    cancel = cancel.toModel(),
    url = url,
    checkBoxDontShowAgain = CheckBoxDontShowAgain(isCheckBoxVisible = isCheckBoxVisible),
    dialogDisplayDuration = dialogDisplayDuration
)

internal fun RatingDto.toModel(): Rating = Rating(
    packageName = packageName,
    platform = platform,
    minutes = minutes,
    numAperture = numAperture,
    message = message.toModel(),
)

internal fun TextDto.toModel(): Text = Text(
    es = es,
    en = en,
    ca = ca,
)