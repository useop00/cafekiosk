package hello.kiosk.service.product

import hello.kiosk.domain.product.ProductSellingStatus.Companion.forDisplay
import hello.kiosk.repository.ProductRepository
import hello.kiosk.service.product.request.ProductCreateServiceRequest
import hello.kiosk.service.product.response.ProductResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository
) {

    @Transactional
    fun saveProduct(request: ProductCreateServiceRequest): ProductResponse {
        val prefix = request.type.prefix
        val nextProductNumber = createNextProductNumber(prefix)

        val product = request.toEntity(nextProductNumber)
        val savedProduct = productRepository.save(product)

        return ProductResponse.of(savedProduct)
    }

    fun getSellingProductsType(type: String): List<ProductResponse> {
        val products = productRepository.findByProductNumberByPrefix(type)
        return products.map { ProductResponse.of(it) }
    }

    fun getSellingProducts(): List<ProductResponse> {
        val products = productRepository.findAllBySellingStatusIn(forDisplay())
        return products.map { ProductResponse.of(it) }
    }


    private fun createNextProductNumber(prefix: String): String {
        val latest = productRepository.findLatestProduct(prefix) ?: return "$prefix-001"
        val split = latest.split("-")
        if (split.size != 2) {
            return "$prefix-001"
        }

        val numberPart = split[1]
        val nextInt = numberPart.toIntOrNull()?.plus(1) ?: 1
        val nextStr = String.format("%03d", nextInt)

        return "$prefix-$nextStr"
    }
}