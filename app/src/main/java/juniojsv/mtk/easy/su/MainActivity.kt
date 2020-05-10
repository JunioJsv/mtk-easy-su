package juniojsv.mtk.easy.su

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.asset_view.view.*

/* d88b db    db d8b   db d888888b  .d88b.     d88b .d8888. db    db
   `8P' 88    88 888o  88   `88'   .8P  Y8.    `8P' 88'  YP 88    88
    88  88    88 88V8o 88    88    88    88     88  `8bo.   Y8    8P
    88  88    88 88 V8o88    88    88    88     88    `Y8b. `8b  d8'
db. 88  88b  d88 88  V888   .88.   `8b  d8' db. 88  db   8D  `8bd8'
Y8888P  ~Y8888P' VP   V8P Y888888P  `Y88P'  Y8888P  `8888Y'    YP  */

class MainActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.elevation = 0.0f
        preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)

        if (!preferences.getBoolean("accepted", false))
            AlertDialog.Builder(this).run {
                setTitle("Warning")
                setMessage("Misuse of superuser access can seriously damage your device, moreover you are fully responsible for your device")
                setPositiveButton("accept") { _, _ ->
                    preferences.edit().putBoolean("accepted", true).apply()
                }
                create().apply { setCanceledOnTouchOutside(false) }
            }.show()

        AssetsManager.getAll(this) { assets ->
            LayoutInflater.from(this).apply {
                assets.forEach { asset ->
                    assets_list.addView(inflate(R.layout.asset_view, assets_list, false).apply {
                        name.text = asset.name
                        tag_name.text = asset.tagName
                        button.setOnClickListener { assetView ->
                            assetView.button.text = getString(R.string.installing)
                            AssetsManager.install(context, asset) {
                                runOnUiThread {
                                    "It is necessary to reboot the device".snack(
                                        main_conteiner,
                                        true
                                    )
                                    assets_list.notifyInstalledAsset()
                                }
                            }
                        }
                    })
                }
                assets_list.notifyInstalledAsset()
            }
        }

        version_text.text =
            String.format("Version %s", BuildConfig.VERSION_NAME)

        button_donate.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=UNXAC9R9MYB8C&source=url"
                )
            })
        }

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
            when {
                preferences.getString(
                    "asset_tag",
                    "null"
                ) == "null" -> "Please install a asset".snack(main_conteiner)
                preferences.getBoolean("need_reset", false) -> "Please reboot your device".snack(
                    main_conteiner
                )
                else -> {
                    button_try_root.isEnabled = false
                    "Please wait".snack(main_conteiner)
                    AssetsManager.tryRoot(this) { success, log ->
                        runOnUiThread {
                            button_try_root.isEnabled = success == false
                            this.log.text = log
                            button_copy.visibility = View.VISIBLE
                            if (success) "Success".snack(main_conteiner)
                            else "Fail try again".snack(main_conteiner)
                        }
                    }
                }
            }
        }

        button_copy.setOnClickListener {
            (getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager)
                .setPrimaryClip(ClipData.newPlainText("log", log.text))

        }
    }

    private fun LinearLayout.notifyInstalledAsset() {
        val installedAssetTag = preferences.getString("asset_tag", "null")!!
        children.forEach {
            it.tag_name.text.also { tagName ->
                if (tagName == installedAssetTag)
                    with(it.button) {
                        text = getString(R.string.installed)
                        isEnabled = false
                    }
                else {
                    with(it.button) {
                        text = getString(R.string.install)
                        isEnabled = true
                    }
                }
            }
        }
    }
}
