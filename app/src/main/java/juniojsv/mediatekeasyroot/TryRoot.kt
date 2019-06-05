package juniojsv.mediatekeasyroot

import android.content.Context
import android.content.res.AssetManager
import android.os.AsyncTask
import android.util.Log
import android.widget.Button
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/*
* Jeovane Santos 04/06/2019
*/

class TryRoot(
    private val context: Context,
    private val assets: AssetManager,
    private val scriptPath: File,
    private val button: Button
): AsyncTask<Void, Void, Boolean>() {

    override fun onPreExecute() {
        super.onPreExecute()
        button.isEnabled = false
    }

    override fun doInBackground(vararg params: Void?): Boolean {

        assets.list("").forEach {
            if (it == "magiskinit" || it == "mtk-su" || it == "suboot.sh") {
                val path = FileOutputStream(File(scriptPath, it))
                val file = assets.open(it)

                try {
                    file.copyTo(path, 1024)
                    Runtime.getRuntime().exec("chmod 777 ${scriptPath.path}/$it")
                } finally {
                    Log.d("copyScripts", "copyScripts Success: $it")
                    file.close()
                    path.flush()
                    path.close()
                }
            }
        }

        try {
            ProcessBuilder().command("${scriptPath.path}/suboot.sh").start()
        } catch (error: IOException) {
            error.printStackTrace()
        }

        Thread.sleep(5000)

        return verifyRoot()
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        if (result!!) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
            button.isEnabled = true
        }
    }

    private fun verifyRoot(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )

        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

}