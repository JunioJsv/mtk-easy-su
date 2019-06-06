package juniojsv.mediatekeasyroot

import android.content.Context
import android.content.res.AssetManager
import android.os.AsyncTask
import android.util.Log
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/*
* Jeovane Santos 05/06/2019
*/

class TryRoot(private val context: Context, view: MainActivity): AsyncTask<Void, Void, Boolean>() {

    private val assets: AssetManager = context.assets
    private val scriptPath: File = context.filesDir
    private val button: Button = view.button_try_root

    override fun onPreExecute() {
        super.onPreExecute()
        Toast.makeText(context, "Please wait", Toast.LENGTH_SHORT).show()
        button.isEnabled = false
    }

    override fun doInBackground(vararg params: Void?): Boolean {
        var script: Process? = null

        assets.list("").forEach {
            if (it == "magiskinit" || it == "mtk-su" || it == "suboot.sh") {
                val path = FileOutputStream(File(scriptPath, it))
                val file = assets.open(it)

                try {
                    file.copyTo(path, 1024)
                    Runtime.getRuntime().exec("chmod 777 ${scriptPath.path}/$it").waitFor()
                } finally {
                    Log.d("copyScripts", "copyScripts Success: $it")
                    file.close()
                    path.flush()
                    path.close()
                }
            }
        }

        try {
            script = ProcessBuilder().command("${scriptPath.path}/suboot.sh").start()
        } catch (error: IOException) {
            error.printStackTrace()
        }

        return if (script?.waitFor() == 0) {
            verifyRoot()
        } else false

    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        if (result!!) {
            Toast.makeText(context, "Success, guaranteed root access", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to gain root access, please try again", Toast.LENGTH_SHORT).show()
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

        paths.forEach {
            if (File(it).exists()) return true
        }

        return false
    }

}