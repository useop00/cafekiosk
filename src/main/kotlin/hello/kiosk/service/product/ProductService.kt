package hello.kiosk.service.product

import hello.kiosk.domain.product.ProductSellingStatus.Companion.forDisplay
import hello.kiosk.repository.ProductRepository
import hello.kiosk.service.product.request.ProductCreateRequest
import hello.kiosk.service.product.response.ProductResponse
import org.springframework.stereotype.Service

@Service
class ProductService (private val productRepository: ProductRepository) {

    fun saveProduct(request: ProductCreateRequest): ProductResponse {
        val nextProductNumber = createNextProductNumber()
        val product = request.toEntity(nextProductNumber)
        val savedProduct = productRepository.save(product)
        return ProductResponse.of(savedProduct)
    }

    fun getSellingProducts():List<ProductResponse>{
        val products = productRepository.findAllBySellingStatusIn(forDisplay())
        return products.map { ProductResponse.of(it) }
    }

    private fun createNextProductNumber(): String {
        val latestProductNumber = productRepository.findLatestProduct() ?: return "001"
        val latestProductNumberInt: Int = latestProductNumber.toInt()
        val nextProductNumberInt = latestProductNumberInt + 1

        return String.format("%03d", nextProductNumberInt)
    }
}