package cat.bcn.commonmodule.model

sealed class CommonError {
    object NoInternet : CommonError()

    data class Default(val message: String = "") : CommonError()
    sealed class Unauthorized : CommonError()
    object NotFound : CommonError()
}

object Success
