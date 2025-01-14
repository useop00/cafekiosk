package hello.kiosk.domain.product

enum class ProductSellingStatus(private val text: String) {
    SELLING("판매중"),
    SOLD_OUT("품절");

    fun getDescription(): String {
        return text
    }

    companion object{
        fun forDisplay(): List<ProductSellingStatus> {
            return listOf(SELLING)
        }
    }
}
