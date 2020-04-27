package juniojsv.mtk.easy.su

import android.content.Context
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader

/* d88b db    db d8b   db d888888b  .d88b.     d88b .d8888. db    db
   `8P' 88    88 888o  88   `88'   .8P  Y8.    `8P' 88'  YP 88    88
    88  88    88 88V8o 88    88    88    88     88  `8bo.   Y8    8P
    88  88    88 88 V8o88    88    88    88     88    `Y8b. `8b  d8'
db. 88  88b  d88 88  V888   .88.   `8b  d8' db. 88  db   8D  `8bd8'
Y8888P  ~Y8888P' VP   V8P Y888888P  `Y88P'  Y8888P  `8888Y'    YP  */

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
        return "$log" + "exit value $exit"
    }
}

fun String.toast(context: Context, long: Boolean = false) =
    Toast.makeText(context, this, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()