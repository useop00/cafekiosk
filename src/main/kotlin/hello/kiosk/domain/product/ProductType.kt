package hello.kiosk.domain.product

import hello.kiosk.domain.product.ProductType.BOTTLE
import hello.kiosk.domain.product.ProductType.DESSERT

enum class ProductType(val prefix:String,private val text:String) {
    COFFEE("A","커피"),
    BOTTLE("B","병 음료"),
    DESSERT("C","디저트");

    fun needStockCheck(): Boolean {
        return this == BOTTLE || this == DESSERT
    }
}

