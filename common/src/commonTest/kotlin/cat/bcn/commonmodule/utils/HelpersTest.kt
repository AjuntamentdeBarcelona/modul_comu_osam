package cat.bcn.commonmodule.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class HelpersTest {

    @Test
    fun `compareVersions correctly compares different version strings`() {
        // Equal
        assertEquals(0, compareVersions("1.0.0", "1.0.0"))
        assertEquals(0, compareVersions("1.0", "1.0.0"))
        assertEquals(0, compareVersions("1.0.0", "1.0"))

        // v1 > v2
        assertEquals(1, compareVersions("2.0", "1.9.9").let { if (it > 0) 1 else it })
        assertEquals(1, compareVersions("1.0.1", "1.0.0").let { if (it > 0) 1 else it })
        assertEquals(1, compareVersions("1.1", "1.0.5").let { if (it > 0) 1 else it })

        // v1 < v2
        assertEquals(-1, compareVersions("1.0.0", "1.0.1").let { if (it < 0) -1 else it })
        assertEquals(-1, compareVersions("1.9.9", "2.0").let { if (it < 0) -1 else it })
        assertEquals(-1, compareVersions("1.0.5", "1.1").let { if (it < 0) -1 else it })
    }

    @Test
    fun `compareVersions handles non-numeric parts gracefully`() {
        // Based on the implementation, it returns 0 if any part is non-numeric
        assertEquals(0, compareVersions("1.a", "1.0"))
        assertEquals(0, compareVersions("1.0", "1.b"))
    }
}