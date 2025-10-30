package ru.pachan.main_kotlin.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import ru.pachan.main_kotlin.exception.data.RequestException
import ru.pachan.main_kotlin.util.RequestLogger
import ru.pachan.main_kotlin.util.enums.AuthorityEnum
import ru.pachan.main_kotlin.util.enums.MdcKeyEnum.REQUEST_UID
import ru.pachan.main_kotlin.util.enums.MdcKeyEnum.REQUEST_URL
import ru.pachan.main_kotlin.util.enums.MdcKeyEnum.USER_ID
import java.nio.charset.StandardCharsets
import java.util.*

class JwtFilter(
    private val requestProvider: RequestProvider,
    private val adminUsername: String,
    private val adminPassword: String,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val requestWrapper = ContentCachingRequestWrapper(request)
        val responseWrapper = ContentCachingResponseWrapper(response)

        val requestUid = request.getHeader(REQUEST_UID.key) ?: UUID.randomUUID().toString()

        val url = request.requestURI

        MDC.put(REQUEST_UID.key, requestUid)
        MDC.put(REQUEST_URL.key, url)


        try {
            if (request.requestURI.startsWith("/actuator")) {
                val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION) ?: ""

                var username = ""
                var password = ""

                if (authHeader.startsWith("Basic ")) {
                    val base64Credentials = authHeader.substring("Basic ".length).trim()
                    val credentials = String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8)
                    val loginAndPassword = credentials.split(":", limit = 2)
                    username = loginAndPassword[0]
                    password = loginAndPassword[1]
                }
                if (username == adminUsername && password == adminPassword) {
                    SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                        null, null,
                        mutableListOf(
                            SimpleGrantedAuthority(AuthorityEnum.ACTUATOR_ADMIN.authority)
                        )
                    )
                } else {
                    RequestLogger.writeSlf4jLog(
                        requestWrapper,
                        responseWrapper,
                        requestProvider,
                        UNAUTHORIZED.reasonPhrase
                    )
                    response.sendError(UNAUTHORIZED.value(), UNAUTHORIZED.reasonPhrase)
                    return
                }
            } else {
                try {
                    val token = requestProvider.resolveToken(request)
                    if (requestProvider.validateToken(token)) {
                        requestProvider.checkAdmin(token, request)
                        requestProvider.checkPermission(token, request)
                        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                            null, null,
                            mutableListOf(
                                SimpleGrantedAuthority(AuthorityEnum.VERIFIED_TOKEN.authority)
                            )
                        )
                    }
                } catch (e: RequestException) {
                    SecurityContextHolder.clearContext()
                    response.sendError(e.httpStatus.value(), e.message)
                    RequestLogger.writeSlf4jLog(requestWrapper, responseWrapper, requestProvider, "")
                    return
                }
            }
            filterChain.doFilter(requestWrapper, responseWrapper)
            RequestLogger.writeSlf4jLog(requestWrapper, responseWrapper, requestProvider, "")
        } finally {
            MDC.remove(REQUEST_UID.key)
            MDC.remove(REQUEST_URL.key)
            MDC.remove(USER_ID.key)
        }
    }

}