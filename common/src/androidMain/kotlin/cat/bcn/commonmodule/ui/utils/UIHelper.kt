package cat.bcn.commonmodule.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.Gravity
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
        showClose: Boolean,
        showCheckBox: Boolean = true,
        isDarkMode: Boolean
    ): VersionDialogViews {
        val root = buildDialogRoot()
        val closeButton = buildCloseButton(isDarkMode)
        val closeRow = buildCloseRow(closeButton)
        val focusOrderViews = mutableListOf<View>()
        val keyboardOrderViews = mutableListOf<View>()

        if (showClose) {
            root.addView(closeRow)
            focusOrderViews.add(closeButton)
            keyboardOrderViews.add(closeButton)
        } else {
            closeButton.visibility = View.GONE
        }

        val appIcon = buildAppIcon()
        root.addView(appIcon)
        focusOrderViews.add(appIcon)

        val titleView = buildTitleView(version, language, isDarkMode)
        root.addView(titleView)
        focusOrderViews.add(titleView)

        val messageView = buildMessageView(version, language, isDarkMode)
        root.addView(messageView)
        focusOrderViews.add(messageView)

        val checkboxResult = if (showCheckBox) {
            buildCheckboxRow(version, language, isDarkMode).also { result ->
                result.row?.let {
                    root.addView(it)
                    focusOrderViews.add(it)
                    keyboardOrderViews.add(it)
                }
            }
        } else {
            CheckboxRowResult(checkbox = null, row = null)
        }

        val primaryButton = buildPrimaryButton(version, language, isDarkMode)
        root.addView(primaryButton)
        focusOrderViews.add(primaryButton)
        keyboardOrderViews.add(primaryButton)

        val secondaryButton = if (showNegative) {
            buildSecondaryButton(version, language, isDarkMode).also { root.addView(it) }
        } else {
            null
        }
        secondaryButton?.let {
            focusOrderViews.add(it)
            keyboardOrderViews.add(it)
        }

        applyAccessibility(
            root = root,
            titleView = titleView,
            closeButton = if (showClose) closeButton else null,
            checkbox = checkboxResult.checkbox,
            primaryButton = primaryButton,
            secondaryButton = secondaryButton
        )

        return VersionDialogViews(
            root = root,
            focusOrderViews = focusOrderViews,
            keyboardOrderViews = keyboardOrderViews,
            checkbox = checkboxResult.checkbox,
            positiveButton = primaryButton,
            negativeButton = secondaryButton,
            closeButton = if (showClose) closeButton else null
        )
    }

    private fun Context.dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    fun buildDialogBackground(isDarkMode: Boolean): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.dp(20).toFloat()
            setColor(if (isDarkMode) colorHex(VERY_DARK_GREY) else Color.WHITE)
        }

    private fun buildDialogRoot(): LinearLayout {
        val paddingHorizontal = context.dp(24)
        val paddingTop = context.dp(16)
        val paddingBottom = context.dp(24)
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(paddingHorizontal, paddingTop, paddingHorizontal, paddingBottom)
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun buildCloseButton(isDarkMode: Boolean): ImageButton =
        ImageButton(context).apply {
            setImageResource(R.drawable.close_mark)
            if (isDarkMode) {
                setColorFilter(Color.WHITE)
            } else {
                clearColorFilter()
            }
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
            contentDescription = context.applicationInfo.loadLabel(context.packageManager)
            layoutParams = LinearLayout.LayoutParams(iconSize, iconSize).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = context.dp(8)
            }
        }
    }

    private fun getAppIcon() = context.applicationInfo.loadIcon(context.packageManager)

    private fun buildTitleView(version: Version, language: Language, isDarkMode: Boolean): TextView =
        TextView(context).apply {
            text = version.title.localize(language)
            textSize = 22f
            typeface = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                Typeface.create(typeface, 600, false)
            } else {
                Typeface.create(typeface, Typeface.BOLD)
            }
            setTextColor(if (isDarkMode) Color.WHITE else Color.BLACK)
            gravity = Gravity.CENTER
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                isAccessibilityHeading = true
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(16)
            }
        }

    private fun buildMessageView(version: Version, language: Language, isDarkMode: Boolean): TextView =
        TextView(context).apply {
            text = version.message.localize(language)
            textSize = 16f
            gravity = Gravity.CENTER
            setTextColor(if (isDarkMode) Color.WHITE else Color.BLACK)
            setLineSpacing(0f, 1.1f)
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

    private fun buildCheckboxRow(version: Version, language: Language, isDarkMode: Boolean): CheckboxRowResult {
        if (!version.checkBoxDontShowAgain.isCheckBoxVisible) {
            return CheckboxRowResult(checkbox = null, row = null)
        }

        val checkBox = CheckBox(context).apply {
            text = version.checkBoxDontShowAgain.text.localize(language)
            setTextColor(if (isDarkMode) Color.WHITE else Color.BLACK)
            buttonTintList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(
                    if (isDarkMode) Color.WHITE else colorHex(VERY_DARK_GREY),
                    colorHex(MEDIUM_LIGHT_GREY)
                )
            )
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

    private fun buildPrimaryButton(version: Version, language: Language, isDarkMode: Boolean): Button =
        Button(context).apply {
            text = version.ok.localize(language)
            isAllCaps = false
            setTextColor(if (isDarkMode) colorHex(VERY_DARK_GREY) else Color.WHITE)
            contentDescription = text
            background = GradientDrawable().apply {
                cornerRadius = context.dp(28).toFloat()
                setColor(if (isDarkMode) Color.WHITE else colorHex(VERY_DARK_GREY))
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(20)
            }
        }

    private fun buildSecondaryButton(version: Version, language: Language, isDarkMode: Boolean): Button =
        Button(context).apply {
            text = version.cancel.localize(language)
            isAllCaps = false
            setTextColor(if (isDarkMode) Color.WHITE else colorHex(VERY_DARK_GREY))
            contentDescription = text
            background = GradientDrawable().apply {
                cornerRadius = context.dp(28).toFloat()
                setColor(Color.TRANSPARENT)
                setStroke(context.dp(1), if (isDarkMode) Color.WHITE else colorHex(MEDIUM_LIGHT_GREY))
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

    private fun applyAccessibility(
        root: LinearLayout,
        titleView: TextView,
        closeButton: ImageButton?,
        checkbox: CheckBox?,
        primaryButton: Button,
        secondaryButton: Button?
    ) {
        closeButton?.let {
            it.isFocusable = true
            it.isFocusableInTouchMode = true
        }
        checkbox?.isFocusable = true
        primaryButton.isFocusable = true
        secondaryButton?.isFocusable = true
        titleView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            root.isKeyboardNavigationCluster = true
        }
    }
}