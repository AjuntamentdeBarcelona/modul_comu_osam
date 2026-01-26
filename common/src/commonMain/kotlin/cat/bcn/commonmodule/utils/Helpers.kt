package cat.bcn.commonmodule.utils

/**
 * Compares two version strings (e.g., "2.0" vs "2.0.1").
 * Returns:
 *  > 0 if version1 is greater
 *  < 0 if version2 is greater
 *  0 if they are equal
 */
fun compareVersions(v1: String, v2: String): Int {
    val parts1 = v1.split(".").map { it.toIntOrNull() ?: return 0 }
    val parts2 = v2.split(".").map { it.toIntOrNull() ?: return 0 }

    val maxLength = maxOf(parts1.size, parts2.size)

    for (i in 0 until maxLength) {
        val p1 = parts1.getOrElse(i) { 0 }
        val p2 = parts2.getOrElse(i) { 0 }

        if (p1 != p2) {
            return p1.compareTo(p2)
        }
    }
    return 0
}