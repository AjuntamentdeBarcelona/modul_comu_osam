package cat.bcn.commonmodule.ui.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.UIKit.*
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
class AccessibilityKeyNavigationManager(
    private val views: List<UIView>,
    private val highlightColor: UIColor = UIColor.colorWithRed(1.0/255.0, 74.0/255.0, 145.0/255.0, 1.0)
) : NSObject() {
    private var isAccessibilityMode = false
    private var currentFocusedIndex = -1
    
    private val highlightView: UIView = UIView(frame = platform.CoreGraphics.CGRectZero.readValue()).apply {
        layer.borderWidth = 4.0
        layer.borderColor = highlightColor.CGColor
        userInteractionEnabled = false
    }

    fun handleKeyCommand(command: UIKeyCommand) {
        when (command.input) {
            "\t" -> toggleMode()
            UIKeyInputRightArrow, UIKeyInputDownArrow -> moveNext()
            UIKeyInputLeftArrow, UIKeyInputUpArrow -> movePrevious()
            "\r", " " -> performClick()
        }
    }

    fun clearHighlight() {
        highlightView.removeFromSuperview()
        isAccessibilityMode = false
        currentFocusedIndex = -1
    }

    private fun toggleMode() {
        isAccessibilityMode = !isAccessibilityMode
        if (isAccessibilityMode) {
            currentFocusedIndex = 0
            updateHighlight()
        } else {
            highlightView.removeFromSuperview()
            currentFocusedIndex = -1
        }
    }

    private fun moveNext() {
        if (!isAccessibilityMode || views.isEmpty()) return
        currentFocusedIndex = (currentFocusedIndex + 1) % views.size
        updateHighlight()
    }

    private fun movePrevious() {
        if (!isAccessibilityMode || views.isEmpty()) return
        currentFocusedIndex = if (currentFocusedIndex <= 0) views.size - 1 else currentFocusedIndex - 1
        updateHighlight()
    }

    private fun performClick() {
        if (!isAccessibilityMode || currentFocusedIndex !in views.indices) return
        val view = views[currentFocusedIndex]
        
        if (view is UIButton) {
            view.sendActionsForControlEvents(UIControlEventTouchUpInside)
        } else if (view is UISwitch) {
            view.setOn(!view.isOn(), animated = true)
            view.sendActionsForControlEvents(UIControlEventValueChanged)
        } else if (view is UIControl) {
            view.sendActionsForControlEvents(UIControlEventTouchUpInside)
        }
    }

    private fun updateHighlight() {
        if (currentFocusedIndex !in views.indices) return
        val targetView = views[currentFocusedIndex]
        
        // Find a suitable root view for highlighting (preferably the window or the controller's main view)
        val rootView = targetView.window ?: targetView.superview ?: targetView
        
        highlightView.removeFromSuperview()
        rootView.addSubview(highlightView)
        
        // Convert coordinates correctly to the root view's coordinate space
        val rect = rootView.convertRect(targetView.bounds, fromView = targetView)
        highlightView.setFrame(rect)
        rootView.bringSubviewToFront(highlightView)
        
        UIAccessibilityPostNotification(UIAccessibilityLayoutChangedNotification, targetView)
    }
}
