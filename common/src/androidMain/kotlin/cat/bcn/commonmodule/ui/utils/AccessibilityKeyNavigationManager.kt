package cat.bcn.commonmodule.ui.utils

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.view.KeyEvent
import android.view.View
import androidx.annotation.RequiresApi

class AccessibilityKeyNavigationManager(
    private val views: List<View>,
    private val highlightColor: Int = Color.parseColor("#0075FF")
) {
    private var isAccessibilityMode = false
    private var currentFocusedIndex = -1
    private val originalBackgrounds = mutableMapOf<View, Drawable?>()

    @RequiresApi(Build.VERSION_CODES.N)
    fun handleKeyEvent(event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) return false

        if (event.keyCode == KeyEvent.KEYCODE_TAB) {
            toggleAccessibilityMode()
            return true
        }

        if (isAccessibilityMode) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_DOWN -> {
                    focusNext()
                    return true
                }
                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_UP -> {
                    focusPrevious()
                    return true
                }
                KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                    if (currentFocusedIndex in views.indices) {
                        views[currentFocusedIndex].performClick()
                    }
                    return true
                }
            }
        }
        return false
    }

    private fun toggleAccessibilityMode() {
        isAccessibilityMode = !isAccessibilityMode
        if (isAccessibilityMode) {
            currentFocusedIndex = 0
            applyHighlight(views[currentFocusedIndex])
        } else {
            clearHighlights()
            currentFocusedIndex = -1
        }
    }

    private fun focusNext() {
        if (views.isEmpty()) return
        clearHighlight(views[currentFocusedIndex])
        currentFocusedIndex = (currentFocusedIndex + 1) % views.size
        applyHighlight(views[currentFocusedIndex])
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun focusPrevious() {
        if (views.isEmpty()) return
        clearHighlight(views[currentFocusedIndex])
        currentFocusedIndex = if (currentFocusedIndex <= 0) views.size - 1 else currentFocusedIndex - 1
        applyHighlight(views[currentFocusedIndex])
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun applyHighlight(view: View) {
        if (!originalBackgrounds.containsKey(view)) {
            originalBackgrounds[view] = view.background
        }
        
        val context = view.context
        val density = context.resources.displayMetrics.density
        val strokeWidth = (4 * density).toInt()
        
        val highlight = GradientDrawable().apply {
            setStroke(strokeWidth, highlightColor)
            cornerRadius = if (originalBackgrounds[view] is GradientDrawable) {
                (originalBackgrounds[view] as GradientDrawable).cornerRadius
            } else {
                (8 * density) // Default corner radius if not a GradientDrawable
            }
        }

        val layers = mutableListOf<Drawable>()
        originalBackgrounds[view]?.let { layers.add(it) }
        layers.add(highlight)
        
        view.background = LayerDrawable(layers.toTypedArray())
        view.requestFocus()
    }

    private fun clearHighlight(view: View) {
        if (originalBackgrounds.containsKey(view)) {
            view.background = originalBackgrounds[view]
        }
    }

    private fun clearHighlights() {
        views.forEach { clearHighlight(it) }
    }
}
