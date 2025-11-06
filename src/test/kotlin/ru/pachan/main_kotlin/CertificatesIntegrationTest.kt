package ru.pachan.main_kotlin

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.pachan.main_kotlin.dto.dictionary.PaginatedResponse
import ru.pachan.main_kotlin.model.main.Certificate
import ru.pachan.main_kotlin.repository.main.CertificateRepository

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CertificatesIntegrationTest {

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    private lateinit var certificateRepository: CertificateRepository

    @LocalServerPort
    private var port: Int = 0

    companion object {
        @Container
        val postgreSQLContainer = PostgreSQLContainer("postgres:16")
            .withUsername(System.getenv("POSTGRES_USER"))
            .withPassword("password")

        @DynamicPropertySource
        @JvmStatic
        fun postgresqlProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
        }
    }

    private fun createHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set(
            "Authorization",
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInJvbGVJZCI6MSwiaWF0IjoxNzIxMTI0" +
                "ODE1LCJleHAiOjE3MjExMjUxMTU5fQ.Vra7txDcKhzN_lJtneuijoUttm20cueLTHAZH3vc5Mg"
        )
        return headers
    }

    @Order(1)
    @Test
    @DisplayName("Check Certificates API with PostgreSQL - first batch")
    fun shouldReturnFirstBatchOfCertificates() {
        // Setup
        val certificate1 = Certificate("codeTest1", null)

        val certificate2 = Certificate("codeTest2", null)

        certificateRepository.save(certificate1)
        certificateRepository.save(certificate2)

        // Execute
        val response: ResponseEntity<PaginatedResponse<Certificate>> = testRestTemplate.exchange(
            "http://localhost:$port/api/main/certificate",
            HttpMethod.GET,
            HttpEntity<Void>(createHeaders()),
            object : org.springframework.core.ParameterizedTypeReference<PaginatedResponse<Certificate>>() {}
        )

        // Verify
        assertTrue(response.statusCode.is2xxSuccessful)
        assertNotNull(response.body)

        val mapper = ObjectMapper()
        val certificateList: List<Certificate> = mapper.convertValue(
            response.body!!.result,
            object : TypeReference<List<Certificate>>() {}
        )

        assertEquals(2, certificateList.size)
        assertEquals("codeTest1", certificateList[0].code)
        assertEquals("codeTest2", certificateList[1].code)
        assertEquals(2, response.body!!.total)
    }

    @Order(10)
    @Test
    @DisplayName("Check Certificates API with PostgreSQL - second batch")
    fun shouldReturnAllCertificatesWithTestRestTemplate() {
        // Setup
        val certificate3 = Certificate("codeTest3", null)

        val certificate4 = Certificate("codeTest4", null)

        certificateRepository.save(certificate3)
        certificateRepository.save(certificate4)

        // Execute
        val response: ResponseEntity<PaginatedResponse<Certificate>> = testRestTemplate.exchange(
            "http://localhost:$port/api/main/certificate",
            HttpMethod.GET,
            HttpEntity<Void>(createHeaders()),
            object : org.springframework.core.ParameterizedTypeReference<PaginatedResponse<Certificate>>() {}
        )

        // Verify
        assertTrue(response.statusCode.is2xxSuccessful)
        assertNotNull(response.body)

        val mapper = ObjectMapper()
        val certificateList: List<Certificate> = mapper.convertValue(
            response.body!!.result,
            object : TypeReference<List<Certificate>>() {}
        )

        assertEquals(4, certificateList.size)
        assertEquals("codeTest3", certificateList[2].code)
        assertEquals("codeTest4", certificateList[3].code)
        assertEquals(4, response.body!!.total)
    }
}