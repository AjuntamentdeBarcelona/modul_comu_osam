package presentation.ui.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cat.bcn.commonmodule.ui.versioncontrol.Language
import cat.bcn.commonmodule.ui.versioncontrol.OSAMCommons
import com.app.app.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val osamCommons by lazy { OSAMCommons(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        versionControlButton.setOnClickListener {
            osamCommons.versionControl(
                appId = "cat.bcn.apropdaqui",
                versionCode = 20141104,
                language = Language.CA
            ) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        }

        ratingButton.setOnClickListener {
            /*val bundle2 = Bundle()
            bundle2.putString(FirebaseAnalytics.Param.ITEM_ID, "osamcommons")
            bundle2.putString(FirebaseAnalytics.Param.ITEM_NAME, "rating_popup_showed")
            FirebaseAnalytics.getInstance(applicationContext).logEvent("OSAM_COMMONS_SHOW_RATING_DIALOG", bundle2)*/
            osamCommons.rating(
                appId = "cat.bcn.apropdaqui",
                language = Language.CA
            ) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}
