package juniojsv.mtk.easy.su

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import juniojsv.mtk.easy.su.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var preferences: SharedPreferences
    private var advertising: InterstitialAd? = null
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

        try {
            MobileAds.initialize(this) {
                advertising = InterstitialAd(this).apply {
                    adUnitId = getString(R.string.advertising_id)
                    adListener = object : AdListener() {
                        override fun onAdLoaded() = show()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("admob_initialization", "${e.message}")
        }

        launch {
            try {
                val response =
                    URL("${getString(R.string.github_api_entry)}/releases/latest").readText()
                val latest =
                    JSONObject(response).getString("tag_name").filter { it.isDigit() }.toInt()
                val current = BuildConfig.VERSION_NAME.filter { it.isDigit() }.toInt()

                if (current < latest)
                    getString(R.string.new_version_available).snack(
                        binding.mRootView, true, getString(R.string.download)
                    ) {
                        startActivity(Intent(Intent.ACTION_VIEW).apply {
                            data =
                                Uri.parse("${getString(R.string.github_url)}/releases/latest")
                        })
                    }
            } catch (e: Exception) {
                Log.e("verity_update", "${e.message}")
            }
        }

        if (!preferences.getBoolean("startup_warning", false))
            AlertDialog.Builder(this).run {
                setTitle(getString(R.string.warning_word))
                setMessage(getString(R.string.startup_warning))
                setPositiveButton(getString(R.string.accept)) { _, _ ->
                    preferences.edit(true) {
                        putBoolean("startup_warning", true)
                    }
                }
                create().apply { setCanceledOnTouchOutside(false) }
            }.show()

        binding.mRunAs64.apply {
            isChecked = preferences.getBoolean("run_as_64", false)
            setOnCheckedChangeListener { _, isChecked ->
                preferences.edit(true) {
                    putBoolean("run_as_64", isChecked)
                }
            }
        }

        binding.mVersion.text =
            String.format(
                "%s %s",
                getString(R.string.version),
                BuildConfig.VERSION_NAME
            )

        binding.mButtonDonate.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.donate_url))
            })
        }

        binding.mButtonGithub.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.github_url))
            })
        }

        binding.mButtonXda.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.xda_url))
            })
        }

        binding.mButtonTryRoot.setOnClickListener { button ->
            advertising?.loadAd(AdRequest.Builder().build())
            button.isEnabled = false
            ExploitHandler(applicationContext) { result, log ->
                binding.mLog.text = log
                binding.mButtonCopy.isEnabled = true
                button.isEnabled = true
                if (result)
                    getString(R.string.success).toast(this, true)
                else
                    getString(R.string.fail).toast(this, false)

            }.execute()
        }

        binding.mButtonCopy.setOnClickListener {
            (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                .setPrimaryClip(ClipData.newPlainText(getString(R.string.log), binding.mLog.text))
        }
    }

}
