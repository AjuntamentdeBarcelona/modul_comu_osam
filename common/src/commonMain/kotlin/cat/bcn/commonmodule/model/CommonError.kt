package cat.bcn.commonmodule.model

data class CommonError(val exception: Exception)

object Success

class BackendTimeoutException(val env: String = "", message: String? = null, cause: Throwable? = null) : Exception(message, cause)