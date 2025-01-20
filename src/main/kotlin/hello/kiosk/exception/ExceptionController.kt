package hello.kiosk.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionController {

    @ExceptionHandler(kioskException::class)
    fun kioskException(e: kioskException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            code = e.getStatusCode().toString(),
            message = e.message.toString(),
            validation = e.validation.toMutableMap()
        )
        return ResponseEntity.status(e.getStatusCode()).body(body)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun invalidRequestHandler(e: MethodArgumentNotValidException): ErrorResponse {
        val response = ErrorResponse.of("400", "잘못된 요청입니다.")
        e.bindingResult.fieldErrors.forEach { fieldError ->
            response.addValidation(fieldError.field, fieldError.defaultMessage.toString())
        }
        return response
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun exceptionHandler(e: Exception): ErrorResponse {
        return ErrorResponse.of("500", "알 수 없는 오류가 발생했습니다.")
    }
}