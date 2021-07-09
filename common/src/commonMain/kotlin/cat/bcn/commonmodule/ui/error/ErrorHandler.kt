package cat.bcn.commonmodule.ui.error

import cat.bcn.commonmodule.model.CommonError

expect class ErrorHandler {
    fun convert(error: CommonError): String
}
