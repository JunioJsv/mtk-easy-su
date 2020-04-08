package juniojsv.mtk.easy.su

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference

/* d88b db    db d8b   db d888888b  .d88b.     d88b .d8888. db    db
   `8P' 88    88 888o  88   `88'   .8P  Y8.    `8P' 88'  YP 88    88
    88  88    88 88V8o 88    88    88    88     88  `8bo.   Y8    8P
    88  88    88 88 V8o88    88    88    88     88    `Y8b. `8b  d8'
db. 88  88b  d88 88  V888   .88.   `8b  d8' db. 88  db   8D  `8bd8'
Y8888P  ~Y8888P' VP   V8P Y888888P  `Y88P'  Y8888P  `8888Y'    YP  */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.elevation = 0.0f

        val preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)

        version_text.text = String.format("Version %s",
            BuildConfig.VERSION_NAME
        )

        switch_run_on_boot.apply {
            isChecked = preferences.getBoolean("run_on_boot", false)
            setOnCheckedChangeListener { _, isChecked ->
                preferences.edit().apply {
                    putBoolean("run_on_boot", isChecked).commit()
                }.apply()
            }
        }

        button_github.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://github.com/JunioJsv/mtk-easy-su"
                )
            })
        }

        button_xda.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://forum.xda-developers.com/android/development/amazing-temp-root-mediatek-armv8-t3922213/post82081703#post82081703"
                )
            })
        }

        button_try_root.setOnClickListener {
            button_try_root.isEnabled = false
            TryRoot(WeakReference(applicationContext)) { success, log ->
                button_try_root.isEnabled = success == false
                this.log.text = log
                button_copy.visibility = View.VISIBLE
            }.execute()
        }

        button_copy.setOnClickListener {
            (getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager)
                .setPrimaryClip(ClipData.newPlainText("log", log.text))

        }
    }
}
