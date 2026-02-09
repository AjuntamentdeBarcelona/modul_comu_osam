package cat.bcn.commonmodule.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cat.bcn.commonmodule.R
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.ui.model.VersionDialogViews
import cat.bcn.commonmodule.ui.versioncontrol.Language

class UIHelper(private val context: Context) {
    companion object {
        const val VERY_DARK_GREY = "#1C1C1C"
        const val MEDIUM_LIGHT_GREY = "#B0B0B0"
    }

    internal fun buildVersionDialogView(
        version: Version,
        language: Language,
        showNegative: Boolean,
        showClose: Boolean
    ): VersionDialogViews {
        val root = buildDialogRoot()
        val closeButton = buildCloseButton()
        val closeRow = buildCloseRow(closeButton)

        if (showClose) {
            root.addView(closeRow)
        } else {
            closeButton.visibility = View.GONE
        }

        val appIcon = buildAppIcon()
        root.addView(appIcon)

        val titleView = buildTitleView(version, language)
        root.addView(titleView)

        val messageView = buildMessageView(version, language)
        root.addView(messageView)

        val checkboxResult = buildCheckboxRow(version, language)
        checkboxResult.row?.let { root.addView(it) }

        val primaryButton = buildPrimaryButton(version, language)
        root.addView(primaryButton)

        val secondaryButton = if (showNegative) {
            buildSecondaryButton(version, language).also { root.addView(it) }
        } else {
            null
        }

        return VersionDialogViews(
            root = root,
            checkbox = checkboxResult.checkbox,
            positiveButton = primaryButton,
            negativeButton = secondaryButton,
            closeButton = if (showClose) closeButton else null
        )
    }

    private fun Context.dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    fun buildDialogBackground(): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.dp(20).toFloat()
            setColor(Color.WHITE)
        }

    private fun buildDialogRoot(): LinearLayout {
        val paddingHorizontal = context.dp(24)
        val paddingTop = context.dp(16)
        val paddingBottom = context.dp(24)
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(paddingHorizontal, paddingTop, paddingHorizontal, paddingBottom)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun buildCloseButton(): ImageButton =
        ImageButton(context).apply {
            setImageResource(R.drawable.close_mark)
            imageTintList = null
            background = null
            setBackgroundColor(Color.TRANSPARENT)
            contentDescription = context.getString(android.R.string.cancel)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setPadding(0, 0, 0, 0)
        }

    private fun buildCloseRow(closeButton: ImageButton): LinearLayout =
        LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            addView(
                closeButton,
                LinearLayout.LayoutParams(context.dp(24), context.dp(24))
            )
        }

    private fun buildAppIcon(): ImageView {
        val iconSize = context.dp(72)
        return ImageView(context).apply {
            setImageDrawable(getAppIcon())
            layoutParams = LinearLayout.LayoutParams(iconSize, iconSize).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = context.dp(8)
            }
        }
    }

    private fun getAppIcon() = context.applicationInfo.loadIcon(context.packageManager)

    private fun buildTitleView(version: Version, language: Language): TextView =
        TextView(context).apply {
            text = version.title.localize(language)
            textSize = 22f
            typeface = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                Typeface.create(typeface, 600, false)
            } else {
                Typeface.create(typeface, Typeface.BOLD)
            }
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(16)
            }
        }

    private fun buildMessageView(version: Version, language: Language): TextView =
        TextView(context).apply {
            text = version.message.localize(language)
            textSize = 16f
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(12)
            }
        }

    private data class CheckboxRowResult(
        val checkbox: CheckBox?,
        val row: LinearLayout?
    )

    private fun buildCheckboxRow(version: Version, language: Language): CheckboxRowResult {
        if (!version.checkBoxDontShowAgain.isCheckBoxVisible) {
            return CheckboxRowResult(checkbox = null, row = null)
        }

        val checkBox = CheckBox(context).apply {
            text = version.checkBoxDontShowAgain.text.localize(language)
        }

        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(16)
            }
            addView(
                checkBox,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }

        return CheckboxRowResult(checkbox = checkBox, row = row)
    }

    private fun buildPrimaryButton(version: Version, language: Language): Button =
        Button(context).apply {
            text = version.ok.localize(language)
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                cornerRadius = context.dp(28).toFloat()
                setColor(colorHex(VERY_DARK_GREY))
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(20)
            }
        }

    private fun buildSecondaryButton(version: Version, language: Language): Button =
        Button(context).apply {
            text = version.cancel.localize(language)
            isAllCaps = false
            setTextColor(colorHex(VERY_DARK_GREY))
            background = GradientDrawable().apply {
                cornerRadius = context.dp(28).toFloat()
                setColor(Color.TRANSPARENT)
                setStroke(context.dp(1), colorHex(MEDIUM_LIGHT_GREY))
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(12)
            }
        }

    @SuppressLint("KtxExtensionAvailable")
    private fun colorHex(value: String): Int = Color.parseColor(value)
}
