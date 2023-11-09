package juniojsv.mtk.easy.su

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.applovin.sdk.AppLovinPrivacySettings
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkSettings
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import juniojsv.mtk.easy.su.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var preferences: SharedPreferences
    private lateinit var github: GithubRepository
    private lateinit var binding: ActivityMainBinding
    private var advertising: InterstitialAd? = null
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        github = Retrofit.Builder().baseUrl(getString(R.string.github_api_entry))
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(GithubRepository::class.java)

        binding.mLog.makeScrollableInsideScrollView()

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
            if (BuildConfig.APPLOVIN_SDK_KEY.isNotBlank()) {
                AppLovinPrivacySettings.setHasUserConsent(true, this)
                AppLovinSdk.getInstance(
                    BuildConfig.APPLOVIN_SDK_KEY,
                    AppLovinSdkSettings(this).also { settings ->
                        if (BuildConfig.DEBUG) {
                            settings.setVerboseLogging(true)
                        }
                    },
                    this
                ).initializeSdk()
            }
            MobileAds.initialize(this) {
                onSetupBannerAd()
                if (BuildConfig.DEBUG && BuildConfig.ADMOB_TEST_DEVICE.isNotBlank()) {
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
            val update = getLatestUpdateAvailable()

            if (update != null) {
                withContext(Dispatchers.Main) {
                    getString(R.string.new_version_available).snack(
                        binding.root, true, getString(R.string.download)
                    ) {
                        startActivity(Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(update.url)
                        })
                    }
                }
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
            getString(R.string.please_wait).toast(this, true)
            button.isEnabled = false
            onLoadFullScreenAd {
                ExploitHandler(this) { result ->
                    advertising?.show(this)
                    binding.mLog.text = result.log
                    binding.mButtonCopy.isEnabled = true
                    button.isEnabled = true
                    if (result.isSuccessful)
                        getString(R.string.success).toast(this, true)
                    else
                        getString(R.string.fail).toast(this, false)
                }.execute()
            }
        }

        binding.mButtonCopy.setOnClickListener {
            (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                .setPrimaryClip(ClipData.newPlainText(getString(R.string.log), binding.mLog.text))
        }
    }

    private suspend fun getLatestUpdateAvailable(): GithubRelease? {
        return try {
            val release = github.getLatestRelease().await()
            val latest = release.tag.filter { it.isDigit() }.toInt()
            val current = BuildConfig.VERSION_NAME.filter { it.isDigit() }.toInt()

            if (current < latest) release else null
        } catch (e: Exception) {
            Log.e(LOG_VERITY_UPDATE, "${e.message}")
            null
        }
    }

    private fun onSetupBannerAd() {
        binding.mBannerAd.loadAd(AdRequest.Builder().build())
    }

    private fun onLoadFullScreenAd(onComplete: (error: LoadAdError?) -> Unit) =
        InterstitialAd.load(
            this, getString(R.string.intersticial_advertising_id),
            AdManagerAdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitial: InterstitialAd) {
                    advertising = interstitial
                    advertising
                        ?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            advertising = null
                        }
                    }
                    onComplete(null)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    advertising = null
                    onComplete(error)
                }
            },
        )

}
