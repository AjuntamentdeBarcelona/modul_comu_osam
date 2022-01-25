package presentation.ui.view.activity

import analytics.AnalyticsWrapperAndroid
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
            context = this,
            backendEndpoint = getString(R.string.common_module_endpoint),
            crashlyticsWrapper = CrashlyticsWrapperAndroid(),
            analyticsWrapper = AnalyticsWrapperAndroid(this)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
