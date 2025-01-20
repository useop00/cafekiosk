package hello.kiosk.exception

class OutOfStock (
    message: String
) : kioskException(message) {
    override fun getStatusCode(): Int {
        return 404
    }
}