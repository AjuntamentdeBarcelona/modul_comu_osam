package cat.bcn.commonmodule.extensions

inline fun <reified T : Enum<T>> getEnumFromName(name: String, default: T): T {
    return try {
        enumValueOf(name)
    } catch (e: IllegalArgumentException) {
        default
    }
}

