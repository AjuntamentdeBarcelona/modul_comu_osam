import java.io.File
import java.io.FileInputStream
import java.util.*

private fun getFile(path: String, file: String): File {
    return File("$path/$file")
}

private fun getProperty(file: String, property: String): String {
    val properties = Properties()
    properties.load(FileInputStream(file))
    return properties.getProperty(property)
}

fun getSignFile(path: String, file: String): File = getFile(path, getProperty(file, "KEYSTORE_FILE"))
fun getSignFilePassword(file: String): String = getProperty(file, "KEYSTORE_PASSWORD")
fun getSignAlias(file: String) = getProperty(file, "KEY_ALIAS")
fun getSignAliasPassword(file: String) = getProperty(file, "KEY_PASSWORD")
fun getVersionName(file: String) = getProperty(file, "APP_VERSION")

class VariantProperties(variantName: String) {
    val appName: String = when (variantName) {
        "${AppFlavor.AppName1}${AppBuildType.Debug}" -> "Debug name1"
        "${AppFlavor.AppName1}${AppBuildType.Release}" -> "Release name1"
        "${AppFlavor.AppName1}${AppBuildType.Debug}" -> "Debug name2"
        "${AppFlavor.AppName1}${AppBuildType.Release}" -> "Release name2"
        else -> ""
    }

}

enum class AppFlavor(
    val signInFile: String,
    val appId: String,
    val signInName: String
) {
    AppName1(
        appId = "cat.bcn.demo",
        signInFile = "appname1.properties",
        signInName = "name1"
    ),
    AppName2(
        appId = "cat.bcn.demo",
        signInFile = "appname1.properties",
        signInName = "name2"
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
        idSuffix = ".devel",
        nameSuffix = "-devel"
    ),
    Release(
        minified = true,
        debuggable = true,
        signInConfig = "release",
        idSuffix = "",
        nameSuffix = ""
    )


}

fun File.printFileName() {
    println("********** ${this.name} *********")
}
