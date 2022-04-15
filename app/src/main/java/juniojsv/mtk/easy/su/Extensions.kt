package juniojsv.mtk.easy.su

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.io.BufferedReader
import java.io.InputStreamReader

fun Process.getOutput(): String {
    val stdout = BufferedReader(InputStreamReader(inputStream))
    val stderr = BufferedReader(InputStreamReader(errorStream))
    val log = StringBuilder()
    var buff: String?
    while (true) {
        buff = stdout.readLine()?.plus("\n")
        if (buff != null) log.append(buff)
        else break
    }
    while (true) {
        buff = stderr.readLine()?.plus("\n")
        if (buff != null) log.append(buff)
        else break
    }
    waitFor().also { exit ->
        return "[$exit] $log\n"
    }
}

fun String.toast(context: Context, long: Boolean = false) =
    Toast.makeText(context, this, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()

fun String.snack(
    view: View,
    long: Boolean = false,
    actionLabel: String = view.context.getString(R.string.accept),
    action: View.OnClickListener?
) {
    val snackbar =
        Snackbar.make(view, this, if (long) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
    if (action != null)
        snackbar.setAction(actionLabel, action)
    snackbar.show()
}

fun TextView.makeScrollableInsideScrollView() {
    movementMethod = ScrollingMovementMethod()
    setOnTouchListener { view, event ->
        view.parent.requestDisallowInterceptTouchEvent(true)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_UP -> {
                view.parent.requestDisallowInterceptTouchEvent(false)
                performClick()
            }
        }
        false
    }
}