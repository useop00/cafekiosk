package hello.kiosk.controller.product

import hello.kiosk.ControllerTestSupport
import hello.kiosk.controller.product.request.ProductCreateRequest
import hello.kiosk.domain.product.ProductSellingStatus
import hello.kiosk.domain.product.ProductType
import hello.kiosk.service.product.response.ProductResponse
import org.mockito.Mockito.`when`
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import kotlin.test.Test


class ProductControllerTest : ControllerTestSupport() {

    @Test
    fun createProduct() {
        //given
        val request = ProductCreateRequest(
            type = ProductType.COFFEE,
            sellingStatus = ProductSellingStatus.SELLING,
            name = "아메리카노",
            price = 3000
        )

        //when //then
        mockMvc.post("/products") {
            contentType = APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect { status { isOk() } }
            .andDo { print() }
    }

    @Test
    fun createProductValidationType() {
        //given
        val request = ProductCreateRequest(
            type = null,
            sellingStatus = ProductSellingStatus.SELLING,
            name = "아메리카노",
            price = 3000
        )

        //when //then
        mockMvc.post("/products") {
            contentType = APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect { status { isBadRequest() } }
            .andDo { print() }
            .andExpect {
                jsonPath("$.code") { value("400") }
                jsonPath("$.message") { value("잘못된 요청입니다.") }
                jsonPath("$.validation") { "{type=상품 타입은 필수입니다.}" }
            }
    }

    @Test
    fun createProductValidationPrice() {
        //given
        val request = ProductCreateRequest(
            type = ProductType.COFFEE,
            sellingStatus = ProductSellingStatus.SELLING,
            name = "아메리카노",
            price = 0
        )

        //when //then
        mockMvc.post("/products") {
            contentType = APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect { status { isBadRequest() } }
            .andDo { print() }
            .andExpect {
                jsonPath("$.code") { value("400") }
                jsonPath("$.message") { value("잘못된 요청입니다.") }
                jsonPath("$.validation") { "{price=상품 가격은 0원 이상이어야 합니다.}" }
            }
    }

    @Test
    fun getSellingProduct() {
        //given
        val result: List<ProductResponse> = listOf()

        `when`(productService.getSellingProducts()).thenReturn(result)

        //when //then
        mockMvc.get("/products/selling")
            .andExpect { status { isOk() } }
            .andDo { print() }
            .andExpect {
                jsonPath("$.code") { value("200") }
                jsonPath("$.status") { value("OK") }
                jsonPath("$.message") { value("OK") }
                jsonPath("$.data") { isArray() }
            }
    }

    @Test
    fun getSellingProductType() {
        //given
        val result: List<ProductResponse> = listOf()

        `when`(productService.getSellingProducts()).thenReturn(result)

        //when //then
        mockMvc.get("/products/selling/A")
            .andExpect { status { isOk() } }
            .andDo { print() }
            .andExpect {
                jsonPath("$.code") { value("200") }
                jsonPath("$.status") { value("OK") }
                jsonPath("$.message") { value("OK") }
                jsonPath("$.data") { isArray() }
            }
    }

}