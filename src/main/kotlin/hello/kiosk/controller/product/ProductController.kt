package hello.kiosk.controller.product

import hello.kiosk.controller.ApiResponse
import hello.kiosk.service.product.ProductService
import hello.kiosk.service.product.request.ProductCreateRequest
import hello.kiosk.service.product.response.ProductResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductController (
    private val productService: ProductService
) {
    @PostMapping
    fun createProduct(@RequestBody request: ProductCreateRequest): ApiResponse<ProductResponse> {
        return ApiResponse.ok(productService.saveProduct(request))
    }

    @GetMapping("/selling")
    fun getSellingProducts(): ApiResponse<List<ProductResponse>> {
        return ApiResponse.ok(productService.getSellingProducts())
    }
}