package hello.kiosk.controller.product

import hello.kiosk.controller.ApiResponse
import hello.kiosk.controller.product.request.ProductCreateRequest
import hello.kiosk.service.product.ProductService
import hello.kiosk.service.product.response.ProductResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
@Tag(name = "Product", description = "상품 API")
class ProductController (
    private val productService: ProductService
) {

    @Operation(summary = "상품 등록", description = "상품을 등록합니다.")
    @PostMapping
    fun createProduct(@Valid @RequestBody request: ProductCreateRequest): ApiResponse<ProductResponse> {
        return ApiResponse.ok(productService.saveProduct(request.toServiceRequest()))
    }

    @Operation(summary = "판매중인 상품 조회", description = "판매중인 상품들을 필터링하여 목록으로 반환합니다.")
    @GetMapping("/selling")
    fun getSellingProducts(): ApiResponse<List<ProductResponse>> {
        return ApiResponse.ok(productService.getSellingProducts())
    }

    @Operation(summary = "판매중인 상품 중 타입별로 조회", description = "판매중인 상품들을 필터링하고 타입별로 핕터링하여 목록으로 반환합니다.")
    @GetMapping("/selling/{type}")
    fun getSellingProductsType(@PathVariable type: String): ApiResponse<List<ProductResponse>> {
        return ApiResponse.ok(productService.getSellingProductsType(type))
    }
}