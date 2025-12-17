package cat.bcn.commonmodule.data.utils

import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.ui.executor.Executor
import cat.bcn.commonmodule.ui.versioncontrol.Language
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommonRepositoryUtilsTest {

    // 1) Scheduler/Dispatcher
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    // 2) Mocks (Mokkery)
    private val executor = mock<Executor>()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { executor.main } returns testDispatcher
        every { executor.bg } returns testDispatcher
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `checkForSelectedLanguages sets language when empty`() = runTest {
        // 1. Create Mock
        val preferences = mock<Preferences>()
        val language = Language.ES

        // 2. Stub: Return EMPTY string to trigger the 'if' block
        every { preferences.getSelectedLanguage() } returns ""
        // Stub the setter to do nothing (just to prevent errors)
        every { preferences.setSelectedLanguage(any()) } returns Unit

        // 3. Execute the real object function
        CommonRepositoryUtils.checkForSelectedLanguages(preferences, language)

        // 4. Verify the setter was called
        verify { preferences.setSelectedLanguage("ES") }
    }
}
