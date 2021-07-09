package cat.bcn.commonmodule.ui.error

import cat.bcn.commonmodule.model.CommonError


actual class ErrorHandler {
    actual fun convert(error: CommonError): String =
        when (error) {
            CommonError.NoInternet -> "No internet"
            is CommonError.Default -> error.message
            CommonError.Unauthorized.NoActive -> "TODO()"
            CommonError.Unauthorized.Gibraltar -> "TODO()"
            CommonError.Unauthorized.Company -> "TODO()"
            CommonError.NotFound -> "NotFound" // TODO
        }
}
