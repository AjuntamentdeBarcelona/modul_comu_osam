package presentation.ui.view.activity

import analytics.AnalyticsWrapperAndroid
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cat.bcn.commonmodule.platform.PlatformUtilAndroid
import cat.bcn.commonmodule.ui.versioncontrol.Language
import cat.bcn.commonmodule.ui.versioncontrol.OSAMCommons
import com.app.app.R
import com.app.app.databinding.ActivityHomeBinding
import crashlytics.CrashlyticsWrapperAndroid

class HomeActivity : AppCompatActivity() {

    private var _binding: ActivityHomeBinding? = null

    protected val binding
        get() = requireNotNull(_binding)

    private val osamCommons by lazy {
        OSAMCommons(
            activity = this,
            context = this,
            backendEndpoint = getString(R.string.common_module_endpoint),
            crashlyticsWrapper = CrashlyticsWrapperAndroid(),
            analyticsWrapper = AnalyticsWrapperAndroid(this),
            platformUtil = PlatformUtilAndroid(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.versionControlButton.setOnClickListener {
            osamCommons.versionControl(
                language = Language.CA
            ) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        }

        binding.ratingButton.setOnClickListener {
            osamCommons.rating(
                language = Language.CA
            ) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        }

        binding.deviceInformationButton.setOnClickListener {
            osamCommons.deviceInformation { deviceInformationResponse, deviceInformation ->
                Toast.makeText(this, "deviceInformation: $deviceInformationResponse", Toast.LENGTH_LONG).show()
                Toast.makeText(this, "platformName: ${deviceInformation?.platformName}", Toast.LENGTH_LONG).show()
                Toast.makeText(this, "platformVersion: ${deviceInformation?.platformVersion}", Toast.LENGTH_LONG).show()
                Toast.makeText(this, "platformModel: ${deviceInformation?.platformModel}", Toast.LENGTH_LONG).show()
            }
        }

        binding.appInformationButton.setOnClickListener {
            osamCommons.appInformation { appInformationResponse, appInformation ->
                Toast.makeText(this, "$appInformationResponse, ${appInformation?.appName}, ${appInformation?.appVersionName}, ${appInformation?.appVersionCode}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
