package juniojsv.mtk.easy.su

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.widget.Toast
import java.lang.ref.WeakReference
import java.util.logging.Handler

/* d88b db    db d8b   db d888888b  .d88b.     d88b .d8888. db    db
   `8P' 88    88 888o  88   `88'   .8P  Y8.    `8P' 88'  YP 88    88
    88  88    88 88V8o 88    88    88    88     88  `8bo.   Y8    8P
    88  88    88 88 V8o88    88    88    88     88    `Y8b. `8b  d8'
db. 88  88b  d88 88  V888   .88.   `8b  d8' db. 88  db   8D  `8bd8'
Y8888P  ~Y8888P' VP   V8P Y888888P  `Y88P'  Y8888P  `8888Y'    YP  */

class RunOnBoot : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.apply {
            getSharedPreferences("preferences", Context.MODE_PRIVATE).edit().putBoolean("need_reset", false).apply()
            if (getSharedPreferences("preferences", Context.MODE_PRIVATE).getBoolean(
                    "run_on_boot",
                    false
                )
            ) {
                "Trying to ensure root access".toast(context)
                if (context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                        .getString("asset_tag", "null") != "null"
                ) AssetsManager.tryRoot(context) { success, _ ->
                    android.os.Handler(Looper.getMainLooper()).post {
                        if (success) "Success".toast(this) else "Fail try again".toast(this)
                    }
                }
            }
        }
    }
}