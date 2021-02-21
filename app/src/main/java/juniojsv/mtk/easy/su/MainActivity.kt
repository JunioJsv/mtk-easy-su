package juniojsv.mtk.easy.su

import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import juniojsv.mtk.easy.su.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        if (Build.VERSION.SDK_INT > 22 && Build.VERSION.SECURITY_PATCH.replace("-", "")
                .toInt() >= 20200301 &&
            !preferences.getBoolean(PREF_SECURITY_PATCH_IGNORED, false)
        ) {
            AlertDialog.Builder(this).run {
                setTitle(R.string.warning_word)
                setMessage(R.string.security_patch_warning)
                setPositiveButton(R.string.close) { _, _ ->
                    finishAndRemoveTask()
                }
                setNegativeButton(getText(R.string.ignore)) { _, _ ->
                    preferences.edit(true) {
                        putBoolean(PREF_SECURITY_PATCH_IGNORED, true)
                    }
                }
                create().apply { setCanceledOnTouchOutside(false) }
            }.show()
        }

        try {
            MobileAds.initialize(this) {
                if (BuildConfig.DEBUG) {
                    MobileAds.setRequestConfiguration(
                        RequestConfiguration.Builder().setTestDeviceIds(
                            mutableListOf(BuildConfig.ADMOB_TEST_DEVICE)
                        ).build()
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(LOG_ADMOB_INITIALIZATION, "${e.message}")
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
                Log.e(LOG_VERITY_UPDATE, "${e.message}")
            }
        }

        if (!preferences.getBoolean(PREF_STARTUP_WARNING, false))
            AlertDialog.Builder(this).run {
                setTitle(getString(R.string.warning_word))
                setMessage(getString(R.string.startup_warning))
                setPositiveButton(getString(R.string.accept)) { _, _ ->
                    preferences.edit(true) {
                        putBoolean(PREF_STARTUP_WARNING, true)
                    }
                }
                create().apply { setCanceledOnTouchOutside(false) }
            }.show()

        binding.mRunAs64.apply {
            isChecked = preferences.getBoolean(PREF_RUN_AS_64_BITS, false)
            setOnCheckedChangeListener { _, isChecked ->
                preferences.edit(true) {
                    putBoolean(PREF_RUN_AS_64_BITS, isChecked)
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
            loadNewAdvertising()
            button.isEnabled = false
            ExploitHandler(this) { result ->
                binding.mLog.text = result.log
                binding.mButtonCopy.isEnabled = true
                button.isEnabled = true
                if (result.wasSucceeded)
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

    private fun loadNewAdvertising() = InterstitialAd.load(this, getString(R.string.advertising_id),
        AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitial: InterstitialAd) {
                advertising = interstitial
                advertising
                    ?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        advertising = null
                    }
                }
                advertising?.show(this@MainActivity)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                advertising = null
            }
        })

}
