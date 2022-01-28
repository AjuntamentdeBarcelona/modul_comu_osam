package cat.bcn.commonmodule.data.datasource.remote.client

import cat.bcn.commonmodule.model.CommonError
import cat.bcn.commonmodule.model.Either
import io.ktor.client.request.HttpRequestBuilder

suspend fun <R> execute(f: suspend () -> R): Either<CommonError, R> =
    try {
        Either.Right(f())
    } catch (requestError: Throwable) {
        val error: CommonError = CommonError.Default(requestError.message ?: "")
        Either.Left(error)
    }

fun HttpRequestBuilder.apiUrl(path: String) {
    url {
        encodedPath = path
    }
}
