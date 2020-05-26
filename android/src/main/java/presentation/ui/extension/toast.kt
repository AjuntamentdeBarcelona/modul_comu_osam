package presentation.ui.extension

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.util.AndroidRuntimeException
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.app.app.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar

/**
 * ContextExtensions
 */

/**
 * Loggging
 */

fun Any.logi(message: String) {
    Log.i(this::class.java.simpleName, message)
}

fun Any.loge(message: String) {
    Log.e(this::class.java.simpleName, message)
}

fun Any.loge(message: String, exception: Exception) {
    Log.e(this::class.java.simpleName, message, exception)
}

/**
 * Context
 * */
fun Context.toast(text: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, length).show()
}

fun Context.toast(textId: Int, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, textId, length).show()
}

fun Context.toPx(dp: Int): Int = resources.getDimensionPixelSize(dp)

fun AppCompatActivity.hideKeyboard() {
    val imm: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.hideKeyboard() {
    val imm: InputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity?.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Fragments
 * */
fun Fragment.toast(text: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(activity, text, length).show()
}

fun Fragment.toast(textId: Int, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(activity, textId, length).show()
}

/**
 * PreventMonkeyClicks.
 */
fun View.clickWithDebounce(debounceTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action.invoke()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

/**
 * ViewExtensions.
 */
fun ImageView.load(url: String) {
    Glide.with(this)
        .load(url)
        .into(this)
}

fun ImageView.loadAndRound(url: String) {
    Glide.with(this)
        .load(url)
        .fitCenter()
        .apply(RequestOptions.circleCropTransform())
        .into(this)
}

/**
 * View
 * */
fun View.hideMe(gone: Boolean = true) {
    this.visibility = if (gone) View.GONE else View.INVISIBLE
}

fun View.showMe() {
    this.visibility = View.VISIBLE
}

fun Context.snackbar(
    container: View,
    message: String,
    action: Int = R.string.retry,
    backgroundColor: Int = R.color.primary,
    showAction: Boolean = true,
    length: Int = Snackbar.LENGTH_INDEFINITE,
    retryCallback: () -> Unit = {}
) {
    val color = ContextCompat.getColor(this, R.color.white)
    val snackbar = Snackbar.make(container, message, length)
        .setActionTextColor(color)

    if (showAction) {
        snackbar.setAction(action) { retryCallback() }
    }

    snackbar.view.setBackgroundResource(backgroundColor)
    snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).setTextColor(color)
    snackbar.show()
}

fun ViewGroup.animateChild() {
    TransitionManager.beginDelayedTransition(this)
}

fun ViewGroup.animateChild(transition: Transition) {
    TransitionManager.beginDelayedTransition(this, transition)
}

fun ImageView.loadMap(address: String) {
    val url = "https://maps.googleapis.com/maps/api/staticmap?center=${Uri.encode(address)}" +
            "&zoom=13&size=300x200&maptype=roadmap" +
            "&markers=color:red%7C" +
            "label:%7C" +
            Uri.encode(address) +
            "&key=" + context.getString(TODO("Map api string here"))

    println(url)

    Glide.with(this)
        .load(url)
        .into(this)
}

@Suppress("DEPRECATION")
fun Context.removeCookies() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        try {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        } catch (e: AndroidRuntimeException) {
            e.printStackTrace()
        }
    } else {
        val cookieSyncManager = CookieSyncManager.createInstance(this)
        cookieSyncManager.startSync()
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookie()
        cookieManager.removeSessionCookie()
        cookieSyncManager.stopSync()
        cookieSyncManager.sync()
    }
}

fun Double.formatDistance(): String = String.format("%.2f", this / 1000)

