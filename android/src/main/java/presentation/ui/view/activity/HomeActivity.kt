package presentation.ui.view.activity

import analytics.AnalyticsWrapperAndroid
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cat.bcn.commonmodule.platform.PlatformUtilAndroid
import cat.bcn.commonmodule.ui.versioncontrol.Language
import cat.bcn.commonmodule.ui.versioncontrol.OSAMCommons
import com.app.app.R
import com.app.app.databinding.ActivityHomeBinding
import crashlytics.CrashlyticsWrapperAndroid
import messaging.MessagingWrapperAndroid
import performance.PerformanceWrapperAndroid

class HomeActivity : AppCompatActivity() {

    private var _binding: ActivityHomeBinding? = null

    protected val binding
        get() = requireNotNull(_binding)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // Inform user that your app will not show notifications.
        }
    }
    val topic = "Test_Topic"

    private val osamCommons by lazy {
        OSAMCommons(
            activity = this,
            context = this,
            backendEndpoint = getString(R.string.common_module_endpoint),
            crashlyticsWrapper = CrashlyticsWrapperAndroid(),
            performanceWrapper = PerformanceWrapperAndroid(),
            analyticsWrapper = AnalyticsWrapperAndroid(this),
            platformUtil = PlatformUtilAndroid(this),
            messagingWrapper = MessagingWrapperAndroid()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askNotificationPermission()

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

        binding.checkLanguageButton.setOnClickListener {
            osamCommons.changeLanguageEvent(
                language = Language.EN
            ) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        }

        binding.checkFirstTimeOrUpdateEvent.setOnClickListener {
            osamCommons.firstTimeOrUpdateEvent(
                language = Language.EN
            ) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        }

        binding.subscribeToCustomTopic.setOnClickListener {
            osamCommons.subscribeToCustomTopic(topic) {
                Toast.makeText(this, "${it}, topic: $topic", Toast.LENGTH_LONG).show()
            }
        }

        binding.unSubscribeToCustomTopic.setOnClickListener {
            osamCommons.unsubscribeToCustomTopic(topic) {
                Toast.makeText(this, "${it}, topic: $topic", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level 33 and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is already granted.
            } else {
                // Directly ask for the permission.
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
