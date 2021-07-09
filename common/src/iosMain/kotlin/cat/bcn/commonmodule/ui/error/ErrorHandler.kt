package cat.bcn.commonmodule.ui.error

import cat.bcn.commonmodule.model.CommonError


actual class ErrorHandler {
    actual fun convert(error: CommonError): String = error.toString()
}
