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

// JunioJsv 04/02/2020

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
                    "https://forum.xda-developers.com/android/development/amazing-temp-root-mediatek-armv8-t3922213/post79626434#post79626434"
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
