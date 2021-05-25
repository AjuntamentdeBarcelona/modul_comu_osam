package presentation.ui.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cat.bcn.commonmodule.ui.versioncontrol.Language
import cat.bcn.commonmodule.ui.versioncontrol.OSAM
import cat.bcn.commonmodule.ui.versioncontrol.RatingControlResponse
import cat.bcn.commonmodule.ui.versioncontrol.VersionControlResponse
import com.app.app.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val osam by lazy { OSAM(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        versionControlButton.setOnClickListener {
            osam.versionControl(
                appId = "cat.bcn.festamerce",
                versionCode = 2019091811,
                language = Language.CA
            ) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        }

        ratingButton.setOnClickListener {
            osam.rating("cat.bcn.festamerce", Language.CA) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}
