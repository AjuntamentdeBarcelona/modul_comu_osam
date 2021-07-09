package cat.bcn.commonmodule.data.datasource.remote

import cat.bcn.commonmodule.data.datasource.local.Preferences
import io.ktor.client.HttpClient
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.util.AttributeKey

class TokenFeature private constructor(private val tokenProvider: Preferences) {

    class Config {
        var tokenProvider: Preferences? = null
        fun build() = TokenFeature(
            tokenProvider ?: throw IllegalArgumentException("TokenProvider should be contain")
        )
    }

    companion object Feature : HttpClientFeature<Config, TokenFeature> {
        override val key = AttributeKey<TokenFeature>("TokenFeature")

        override fun prepare(block: Config.() -> Unit) = Config().apply(block).build()

        override fun install(feature: TokenFeature, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                // TODO, token
                // feature.tokenProvider.getToken().let {
                //     if (it.isNotBlank()) {
                //         context.header("Authorization", "bearer $it")
                //     }
                // }
            }
        }
    }
}
