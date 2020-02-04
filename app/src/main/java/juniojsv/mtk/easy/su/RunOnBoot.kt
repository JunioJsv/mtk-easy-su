package juniojsv.mtk.easy.su

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.lang.ref.WeakReference

// JunioJsv 04/02/2020

class RunOnBoot : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.apply {
            if (getSharedPreferences("preferences", Context.MODE_PRIVATE).getBoolean("run_on_boot", false)) {
                Toast.makeText(this, "Trying to ensure root access", Toast.LENGTH_SHORT).show()
                TryRoot(WeakReference(this)).execute()
            }
        }
    }
}