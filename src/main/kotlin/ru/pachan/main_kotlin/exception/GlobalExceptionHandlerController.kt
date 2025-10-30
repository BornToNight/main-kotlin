package ru.pachan.main_kotlin.exception

import jakarta.servlet.http.HttpServletResponse
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import ru.pachan.main_kotlin.exception.data.RequestException
import ru.pachan.main_kotlin.exception.data.RequestSystemException
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.DUPLICATE_UNIQUE_FIELD
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.INVALID_DATA_FORMAT
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.NOT_FOUND_REFERENCE
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.NOT_VALID_QUERY_PARAMETERS
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.REQUIRED_FIELDS_EMPTY

@RestControllerAdvice
class GlobalExceptionHandlerController {

    @ExceptionHandler(RequestSystemException::class)
    fun handleRequestSystemException(res: HttpServletResponse, e: RequestSystemException): ResponseEntity<String> {
        return ResponseEntity(e.message, e.httpStatus)
    }

    @ExceptionHandler(RequestException::class)
    fun handleRequestException(res: HttpServletResponse, e: RequestSystemException): ResponseEntity<String> {
        return ResponseEntity(e.message, e.httpStatus)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleSQLUniqueFieldException(e: DataIntegrityViolationException): ResponseEntity<String> {
        val message = e.message ?: return ResponseEntity(DUPLICATE_UNIQUE_FIELD.message, CONFLICT)

        return when {
            message.contains("not present in table") || message.contains("отсутствует в таблице") ->
                ResponseEntity(NOT_FOUND_REFERENCE.message, NOT_FOUND)

            message.contains("overflow") ->
                ResponseEntity(INVALID_DATA_FORMAT.message, BAD_REQUEST)

            else -> {
                val startIndex = message.indexOf("=") + 2
                val endIndex = message.indexOf(")", startIndex)
                val field = if (endIndex > startIndex) message.substring(startIndex, endIndex) else ""
                ResponseEntity(DUPLICATE_UNIQUE_FIELD.message + " - " + field, CONFLICT)
            }
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun requiredFieldsEmptyHandler(e: HttpMessageNotReadableException): ResponseEntity<String> {
        val message = e.message
        if (!message.isNullOrBlank()) {
            return when {
                message.contains("deserialize") -> ResponseEntity(INVALID_DATA_FORMAT.message, BAD_REQUEST)
                message.contains("non-nullable") -> ResponseEntity(REQUIRED_FIELDS_EMPTY.message, BAD_REQUEST)
                else -> ResponseEntity("", BAD_REQUEST)
            }
        }
        return ResponseEntity("", BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validEntityException(e: MethodArgumentNotValidException): ResponseEntity<Array<String>> {
        val fieldErrors = e.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }.toTypedArray()
        return ResponseEntity(fieldErrors, BAD_REQUEST)
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun validQueryParametersException(e: HandlerMethodValidationException): ResponseEntity<String> {
        return ResponseEntity(NOT_VALID_QUERY_PARAMETERS.message, BAD_REQUEST)
    }


}