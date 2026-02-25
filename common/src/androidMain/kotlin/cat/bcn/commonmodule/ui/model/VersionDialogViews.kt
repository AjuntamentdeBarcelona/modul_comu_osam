package cat.bcn.commonmodule.ui.model

import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton

data class VersionDialogViews(
    val root: View,
    val focusOrderViews: List<View>,
    val keyboardOrderViews: List<View>,
    val checkbox: CheckBox?,
    val positiveButton: Button,
    val negativeButton: Button?,
    val closeButton: ImageButton?
)
