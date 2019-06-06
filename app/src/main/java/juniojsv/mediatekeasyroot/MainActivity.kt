package juniojsv.mediatekeasyroot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

/*
* Jeovane Santos 04/06/2019
*/

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_try_root.setOnClickListener {
            TryRoot(this, this).execute()
        }
    }
}
