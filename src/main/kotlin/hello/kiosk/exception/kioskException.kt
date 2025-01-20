package hello.kiosk.exception

abstract class kioskException (
    message: String
) : RuntimeException(message) {

    val validation: MutableMap<String, String> = mutableMapOf()

    abstract fun getStatusCode(): Int

    fun addValidation(fieldName: String, message: String) {
        validation[fieldName] = message
    }
}