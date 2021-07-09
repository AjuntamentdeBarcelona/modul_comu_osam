package cat.bcn.commonmodule.model

sealed class CommonError {
    object NoInternet : CommonError()

    data class Default(val message: String = "") : CommonError()
    sealed class Unauthorized : CommonError() {
        object NoActive : Unauthorized()
        object Gibraltar : Unauthorized()
        object Company : Unauthorized()
    }

    object NotFound : CommonError()
}

object Success
