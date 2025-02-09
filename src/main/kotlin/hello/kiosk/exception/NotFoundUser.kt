package hello.kiosk.exception

class NotFoundUser(
    message: String
) : kioskException(message) {
    override fun getStatusCode(): Int {
        return 404
    }
}