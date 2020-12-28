package juniojsv.mtk.easy.su

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.gms.ads.*
import juniojsv.mtk.easy.su.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences
    private var advertising: InterstitialAd? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.elevation = 0.0f
        preferences = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        binding = ActivityMainBinding.inflate(layoutInflater,findViewById(android.R.id.content))

        MobileAds.initialize(this) {
            advertising = InterstitialAd(this).apply {
                adUnitId = getString(R.string.advertising_id)
                adListener = object : AdListener() {
                    override fun onAdLoaded() = show()
                }
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
            String.format("%s %s", getString(R.string.version), BuildConfig.VERSION_NAME)

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
            ExploitHandler(this) { result, log ->
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
