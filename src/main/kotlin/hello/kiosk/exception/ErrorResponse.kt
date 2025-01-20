package hello.kiosk.exception

class ErrorResponse (
    val code: String,
    val message: String,
    val validation: MutableMap<String, String> = mutableMapOf()
){

    fun addValidation(fieldName: String, errorMessage: String){
        validation[fieldName] = errorMessage
    }

    companion object {
        fun of(code: String, message: String): ErrorResponse{
            return ErrorResponse(
                code = code,
                message = message,
            )
        }

    }
}
