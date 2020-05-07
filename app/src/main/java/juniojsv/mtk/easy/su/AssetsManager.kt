package juniojsv.mtk.easy.su

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

/* d88b db    db d8b   db d888888b  .d88b.     d88b .d8888. db    db
   `8P' 88    88 888o  88   `88'   .8P  Y8.    `8P' 88'  YP 88    88
    88  88    88 88V8o 88    88    88    88     88  `8bo.   Y8    8P
    88  88    88 88 V8o88    88    88    88     88    `Y8b. `8b  d8'
db. 88  88b  d88 88  V888   .88.   `8b  d8' db. 88  db   8D  `8bd8'
Y8888P  ~Y8888P' VP   V8P Y888888P  `Y88P'  Y8888P  `8888Y'    YP  */

object AssetsManager {
    fun getAll(context: Context, onSuccess: (assets: ArrayList<Asset>) -> Unit) {
        Thread {
            val json = jacksonObjectMapper()
            val assets = arrayListOf<Asset>()

            with(Volley.newRequestQueue(context)) {
                val api = "https://api.github.com/repos/juniojsv/mtk-easy-su/releases"
                add(
                    StringRequest(Request.Method.GET,
                        api, Response.Listener { response ->
                            json.readValue<List<Map<String, Any>>>(response).also { releases ->
                                releases.forEach { release: Map<String, Any> ->
                                    val tagName = release["tag_name"] as String
                                    if (tagName.startsWith("assets")) {
                                        val asset = (release["assets"] as List<*>)[0] as Map<*, *>
                                        val url = asset["browser_download_url"] as String
                                        val name = asset["name"] as String

                                        assets.add(
                                            Asset(url, name, tagName)
                                        )
                                    }
                                }
                                onSuccess(assets)
                            }
                        },
                        Response.ErrorListener { error ->

                        }
                    )
                )
            }
        }.start()
    }

    fun install(context: Context, asset: Asset, onSuccess: () -> Unit) {
        val directory = context.filesDir
        val dataDirectory = Environment.getDataDirectory()
        val shell = Runtime.getRuntime()
        clean(context)
        with(DownloadManager.Request(Uri.parse(asset.url))) {
            setTitle(asset.name)
            setDescription("from ${asset.url}")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalFilesDir(context, null, asset.name)
            (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(this)
                .also { id ->
                    context.registerReceiver(
                        object : BroadcastReceiver() {
                            override fun onReceive(broadcastContext: Context?, intent: Intent?) {
                                val extra = intent!!.getLongExtra(
                                    DownloadManager.EXTRA_DOWNLOAD_ID,
                                    -1
                                )
                                if (extra == id) {
                                    Thread {
                                        unZip(
                                            "${context.getExternalFilesDir(null)}/${asset.name}",
                                            context.filesDir.absolutePath
                                        )
                                        context.getSharedPreferences(
                                            "preferences",
                                            Context.MODE_PRIVATE
                                        ).edit().apply {
                                            putString("asset_tag", asset.tagName)
                                            putBoolean("need_reset", true)
                                            apply()
                                        }
                                        directory.listFiles()!!.forEach { file ->
                                            shell.exec("chmod 755 ${file.name}", null, directory)
                                                .waitFor()
                                        }
                                        shell.exec(
                                            "./mtk-su -c rm -rf ${dataDirectory.absolutePath}/adb/",
                                            null,
                                            directory
                                        ).waitFor()
                                        context.unregisterReceiver(this)
                                        onSuccess()
                                    }.start()
                                }
                            }
                        },
                        IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                    )
                }
        }
    }

    fun tryRoot(
        context: Context,
        onFinished: ((success: Boolean, log: String) -> Unit)? = null
    ) {
        val directory = context.filesDir
        val shell = Runtime.getRuntime()
        Thread {
            val log = shell.exec(
                "sh magisk-boot.sh ${directory.absolutePath}",
                null, directory
            ).getOutput() + " ${context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                .getString("asset_tag", "null")}"
            if (onFinished != null)
                onFinished(File("/sbin/su").exists(), log)
        }.start()
    }

    private fun unZip(zipped: String, destination: String) {
        ZipInputStream(FileInputStream(zipped)).also { data ->
            while (true) {
                data.nextEntry?.also { zipEntry ->
                    val output = FileOutputStream("$destination/${zipEntry.name}")
                    while (true) {
                        val chunk = data.read().takeIf { it != -1 } ?: break
                        output.write(chunk)
                    }
                    data.closeEntry()
                    output.close()
                } ?: break
            }
            data.close()
        }
    }

    private fun clean(context: Context): Boolean =
        (context.getExternalFilesDir(null)
            ?.deleteRecursively() ?: false && context.filesDir.deleteRecursively())
}