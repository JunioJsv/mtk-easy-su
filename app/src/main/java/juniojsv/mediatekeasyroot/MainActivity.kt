package juniojsv.mediatekeasyroot

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/*
* Jeovane Santos 04/06/2019
*/

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.elevation = 0.0F

        val preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)

        version_text.text = "Version ${BuildConfig.VERSION_NAME}"

        button_donate.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://www.mercadopago.com/mlb/checkout/start?pref_id=365594257-359f7b8e-cc7c-4ff2-8fd1-4fc73eb6de50"
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
                    "https://github.com/JunioJsv/mediatek-easy-root"
                )
            })
        }

        button_try_root.setOnClickListener {
            TryRoot(this, this).execute()
        }
    }
}
