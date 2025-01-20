package hello.kiosk.controller

import org.springframework.http.HttpStatus

class ApiResponse<T> (
    val code: Int,
    val status: HttpStatus,
    val message: String,
    val data: T

) {
    companion object {

        fun <T> of(httpStatus: HttpStatus, message: String, data: T): ApiResponse<T> {
            return ApiResponse(
                code = httpStatus.value(),
                status = httpStatus,
                message = message,
                data = data
            )
        }

        fun <T> of(httpStatus: HttpStatus, data: T): ApiResponse<T> {
            return of(httpStatus, httpStatus.name, data)
        }

        /**
         * 200 OK
         */
        fun <T> ok(data: T): ApiResponse<T> {
            return of(HttpStatus.OK, data)
        }
    }
}