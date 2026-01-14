package ru.pachan.main_kotlin.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import ru.pachan.main_kotlin.security.RequestProvider
import ru.pachan.main_kotlin.util.enums.MdcKeyEnum
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*

object RequestLogger {

    private const val MAX_BODY_LENGTH_KB = 10 * 1024 // 10 KB
    private val log = LoggerFactory.getLogger(RequestLogger::class.java)

    fun writeSlf4jLog(
        requestWrapper: ContentCachingRequestWrapper,
        responseWrapper: ContentCachingResponseWrapper,
        requestProvider: RequestProvider,
        exceptionMessage: String,
    ) {
        val url = requestWrapper.requestURI
        if (isActuatorOrSwagger(url)) {
            responseWrapper.copyBodyToResponse() // EXPLAIN_V вернуть бади респонса
            return
        }

        MDC.put(MdcKeyEnum.USER_ID.key, getUserId(requestWrapper, requestProvider))

        val method = requestWrapper.method
        val message = StringBuilder()

        message.append("Url - ").append(url)
        message.append(" | ")
        message.append("Status - ").append(responseWrapper.status)
        message.append(" | ")
        message.append("Method - ").append(method)
        message.append(" | ")
        message.append("Args - ").append(requestWrapper.queryString)
        message.append(" | ")

        if (isPostMethod(method)) {
            val requestBody = extractPostRequestBody(requestWrapper).trim()
            if (requestBody.isNotBlank()) {
                message.append("Request body - ").append(requestBody)
                message.append(" | ")
            }
        }
        message.append("Response body - ").append(extractResponseBody(responseWrapper).trim())

        if (exceptionMessage.isNotBlank()) {
            message.append(" | ").append(exceptionMessage)
        }

        if (responseWrapper.status == HttpStatus.OK.value()) {
            log.info(message.toString())
        } else {
            log.error(message.toString())
        }

        responseWrapper.copyBodyToResponse()
    }

    private fun getUserId(httpServletRequest: ContentCachingRequestWrapper, requestProvider: RequestProvider): String {
        return try {
            val payload =
                String(Base64.getDecoder().decode(requestProvider.resolveToken(httpServletRequest).split("\\.")[1]))
            ObjectMapper().readTree(payload).get("userId").asText()
        } catch (_: Exception) {
            ""
        }
    }

    private fun isActuatorOrSwagger(url: String): Boolean {
        return url.contains("/actuator") || url.contains("swagger") || url.contains("/v3/api-docs")
    }

    private fun extractPostRequestBody(request: ContentCachingRequestWrapper): String {
        return extractBody(request.contentAsByteArray)
    }

    private fun extractResponseBody(response: ContentCachingResponseWrapper): String {
        return extractBody(response.contentAsByteArray)
    }

    private fun extractBody(contentAsByteArray: ByteArray): String {
        if (contentAsByteArray.isEmpty()) return ""
        val body = String(contentAsByteArray, UTF_8)
        return if (body.isEmpty()) "" else truncate(body)
    }

    private fun isPostMethod(method: String): Boolean {
        return method.equals("POST", ignoreCase = true)
    }

    private fun truncate(body: String): String {
        return if (MAX_BODY_LENGTH_KB > body.length) body
        else body.take(MAX_BODY_LENGTH_KB) + "... [truncated]"
    }
}