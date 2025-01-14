package hello.kiosk.domain.product

enum class ProductType(private val text:String) {
    COFFEE("커피"),
    BEVERAGE("음료"),
    DESSERT("디저트");

    fun getDescription(): String {
        return text
    }
}