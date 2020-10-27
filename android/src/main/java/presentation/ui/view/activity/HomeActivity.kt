package presentation.ui.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cat.bcn.commonmodule.ui.versioncontrol.Language
import cat.bcn.commonmodule.ui.versioncontrol.OSAM
import cat.bcn.commonmodule.ui.versioncontrol.VersionControlResponse
import com.app.app.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val versionControl by lazy { OSAM(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        versionControlButton.setOnClickListener {
            versionControl.versionControl(
                appId = "cat.bcn.apropdaqui",
                versionCode = 20141104,
                language = Language.CA
            ) {
                Toast.makeText(
                    this@HomeActivity,
                    when (it) {
                        VersionControlResponse.ACCEPTED -> "Dialog ACCEPTED"
                        VersionControlResponse.DISMISSED -> "Dialog DISMISSED"
                        VersionControlResponse.CANCELLED -> "Dialog CANCELLED"
                        VersionControlResponse.ERROR -> "Dialog ERROR"
                    },
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
