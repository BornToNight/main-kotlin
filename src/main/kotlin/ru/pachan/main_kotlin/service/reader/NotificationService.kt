package ru.pachan.main_kotlin.service.reader

import com.google.protobuf.InvalidProtocolBufferException
import com.google.rpc.ErrorInfo
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto.fromThrowable
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.pachan.grpc.NotificationServiceGrpc
import ru.pachan.grpc.Reader
import ru.pachan.main_kotlin.dto.reader.NotificationDto
import ru.pachan.main_kotlin.exception.data.RequestException
import ru.pachan.main_kotlin.exception.data.RequestSystemException
import ru.pachan.main_kotlin.util.enums.ExceptionEnum

@Service
class NotificationService(
    @param:GrpcClient("reader-server")
    private val notificationServiceBlockingStub: NotificationServiceGrpc.NotificationServiceBlockingStub,
) {

    companion object {
        const val CIRCUIT_BREAKER_NAME = "writerCircuitBreaker"
        const val RETRY_NAME = "writerRetry"
        const val FALLBACK_METHOD = "fallback"
    }

//    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = FALLBACK_METHOD)
    fun findByIdNotification(notificationId: Long): NotificationDto {
        val findByIdNotificationRequest = Reader.FindByIdNotificationRequest.newBuilder()
            .setNotificationId(notificationId)
            .build()

        try {
            val findByIdNotificationResponse =
                notificationServiceBlockingStub.findByIdNotification(findByIdNotificationRequest)

            val notification = findByIdNotificationResponse.notification
            return NotificationDto(
                notification_id = notification.notificationId,
                person_id = notification.personId,
                count = notification.count
            )
        } catch (e: StatusRuntimeException) {
            try {
                val status = fromThrowable(e)
                var errorInfo: ErrorInfo? = null
                for (any in status!!.detailsList) {
                    if (!any.`is`(ErrorInfo::class.java)) {
                        continue
                    }
                    errorInfo = any.unpack(ErrorInfo::class.java)
                }
                throw RequestException(
                    errorInfo!!.metadataMap["message"]!!,
                    HttpStatus.valueOf((errorInfo.metadataMap["httpStatus"]!!).toInt())
                )
            } catch (_: NullPointerException) {
                throw RequestSystemException(ExceptionEnum.SYSTEM_ERROR.message, HttpStatus.INTERNAL_SERVER_ERROR)
            } catch (_: InvalidProtocolBufferException) {
                throw RequestSystemException(ExceptionEnum.SYSTEM_ERROR.message, HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }

//    @Retry(name = CIRCUIT_BREAKER_NAME, fallbackMethod = FALLBACK_METHOD)
    fun findByPersonIdNotification(personId: Long): NotificationDto {
        val findByPersonIdNotificationRequest = Reader.FindByPersonIdNotificationRequest.newBuilder()
            .setPersonId(personId)
            .build()

        try {
            val findByPersonIdNotificationResponse =
                notificationServiceBlockingStub.findByPersonIdNotification(findByPersonIdNotificationRequest)

            val notification = findByPersonIdNotificationResponse.notification
            return NotificationDto(
                notification_id = notification.notificationId,
                person_id = notification.personId,
                count = notification.count
            )
        } catch (e: StatusRuntimeException) {
            try {
                val status = fromThrowable(e)
                var errorInfo: ErrorInfo? = null
                for (any in status!!.detailsList) {
                    if (!any.`is`(ErrorInfo::class.java)) {
                        continue
                    }
                    errorInfo = any.unpack(ErrorInfo::class.java)
                }
                throw RequestException(
                    errorInfo!!.metadataMap["message"]!!,
                    HttpStatus.valueOf((errorInfo.metadataMap["httpStatus"]!!).toInt())
                )
            } catch (_: NullPointerException) {
                throw RequestSystemException(ExceptionEnum.SYSTEM_ERROR.message, HttpStatus.INTERNAL_SERVER_ERROR)
            } catch (_: InvalidProtocolBufferException) {
                throw RequestSystemException(ExceptionEnum.SYSTEM_ERROR.message, HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }

    fun fallback(id: Long, throwable: Throwable): NotificationDto {
        when (throwable) {
            is RequestException -> throw throwable
            else -> throw RequestException(ExceptionEnum.SERVICE_UNAVAILABLE.message, HttpStatus.SERVICE_UNAVAILABLE)
        }
    }

}