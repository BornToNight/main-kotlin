package ru.pachan.main_kotlin.service.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.stereotype.Service
import ru.pachan.main_kotlin.dto.dictionary.PaginatedResponse
import ru.pachan.main_kotlin.exception.data.RequestException
import ru.pachan.main_kotlin.model.main.Certificate
import ru.pachan.main_kotlin.repository.main.CertificateRepository
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.OBJECT_NOT_FOUND
import java.util.concurrent.Executors

@Service
class CertificateService(
    private val repository: CertificateRepository,
) {

    fun getAll(pageable: Pageable): PaginatedResponse<Certificate> {
        val result = repository.findAll(pageable)
        return PaginatedResponse(result.totalElements, result.content)
    }

    fun getOne(id: Long): Certificate {
        return repository.findByIdOrNull(id) ?: throw RequestException(OBJECT_NOT_FOUND.message, HttpStatus.NOT_FOUND)
    }

    fun createOne(certificate: Certificate): Certificate {
        return repository.save(certificate)
    }

    fun updateOne(id: Long, certificate: Certificate): Certificate {
        val oldCertificate =
            repository.findByIdOrNull(id) ?: throw RequestException(OBJECT_NOT_FOUND.message, UNAUTHORIZED)

        oldCertificate.code = certificate.code
        return repository.save(oldCertificate)
    }

    fun deleteOne(id: Long) {
        repository.deleteById(id)
    }

    fun massiveCreation() {
        val threadCount = 3
        Executors.newFixedThreadPool(threadCount).use { executor ->
            // Каждый поток будет сохранять по 500 сертификатов
            for (i in 0 until threadCount) {
                executor.submit { saveCertificatesForThread(i) }
            }
        }
    }

    private fun saveCertificatesForThread(threadId: Int) {
        val totalCertificates = 500
        // Каждый поток сохраняет по 500 сертификатов
        for (i in 0 until totalCertificates) {
            repository.save(
                Certificate(
                    code = "massCert${i}threadId$threadId",
                    person = null
                )
            )
        }
    }

    fun massiveCreationCoroutine() = runBlocking {
        val threadCount = 3
        coroutineScope {
            repeat(threadCount) { threadId ->
                launch(Dispatchers.IO) {
                    saveCertificatesForThread(threadId)
                }
            }
        }
    }

    private suspend fun saveCertificatesForCoroutine(threadId: Int) = withContext(Dispatchers.IO) {
        val totalCertificates = 500
        repeat(totalCertificates) { i ->
            repository.save(
                Certificate(
                    code = "massCert${i}threadId$threadId",
                    person = null
                )
            )
        }
    }

}