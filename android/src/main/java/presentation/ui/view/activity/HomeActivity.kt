package presentation.ui.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cat.bcn.commonmodule.ui.versioncontrol.Language
import cat.bcn.commonmodule.ui.versioncontrol.OSAMCommons
import com.app.app.BuildConfig
import com.app.app.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val osamCommons by lazy { OSAMCommons(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        versionControlButton.setOnClickListener {
            osamCommons.versionControl(
                versionCode = 2021050000,
                language = Language.CA
            ) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        }

        ratingButton.setOnClickListener {
            osamCommons.rating(
                language = Language.CA
            ) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}
