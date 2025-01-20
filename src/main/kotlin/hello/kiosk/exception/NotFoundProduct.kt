package hello.kiosk.exception

class NotFoundProduct (
    message: String
) : kioskException(message) {
    override fun getStatusCode(): Int {
        return 404
    }
}