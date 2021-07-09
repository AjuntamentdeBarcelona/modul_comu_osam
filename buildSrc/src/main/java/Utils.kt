import java.io.File
import java.io.FileInputStream
import java.util.*

private fun getFile(file: String): File {
    return File("$file")
}

private fun getProperty(file: String, property: String): String {
    val properties = Properties()
    properties.load(FileInputStream(file))
    return properties.getProperty(property)
}

fun getSignFile(file: String): File = getFile(getProperty(file, "KEYSTORE_FILE"))
fun getSignFilePassword(file: String): String = getProperty(file, "KEYSTORE_PASSWORD")
fun getSignAlias(file: String) = getProperty(file, "KEY_ALIAS")
fun getSignAliasPassword(file: String) = getProperty(file, "KEY_PASSWORD")
fun getVersionName(file: String) = getProperty(file, "APP_VERSION")

class VariantProperties(variantName: String) {
    val appName: String = when (variantName) {
        "${AppFlavor.Demo}${AppBuildType.Debug}" -> "Debug Demo"
        "${AppFlavor.Demo}${AppBuildType.Release}" -> "Release Demo"
        "${AppFlavor.Jenkins}${AppBuildType.Debug}" -> "Debug Jenkins"
        "${AppFlavor.Jenkins}${AppBuildType.Release}" -> "Release Jenkins"
        else -> ""
    }

}

enum class AppFlavor(
    val signInFile: String,
    val appId: String,
    val signInName: String
) {
    Demo(
        appId = "cat.bcn.commonmodule",
        signInFile = "keystore.properties",
        signInName = "Demo"
    ),
    Jenkins(
        appId = "cat.bcn.appdemo",
        signInFile = "keystore.properties",
        signInName = "Jenkins"
    )
}

enum class AppBuildType(
    val minified: Boolean,
    val debuggable: Boolean,
    val signInConfig: String,
    val idSuffix: String,
    val nameSuffix: String
) {

    Debug(
        minified = false,
        debuggable = true,
        signInConfig = "debug",
        idSuffix = "",
        nameSuffix = ""
    ),
    Release(
        minified = false,
        debuggable = false,
        signInConfig = "release",
        idSuffix = "",
        nameSuffix = ""
    )


}

fun File.printFileName() {
    println("********** ${this.name} *********")
}
