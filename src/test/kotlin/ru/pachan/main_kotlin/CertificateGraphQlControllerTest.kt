package ru.pachan.main_kotlin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.pachan.main_kotlin.model.main.Certificate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Testcontainers
@AutoConfigureGraphQlTester
class CertificateGraphQlControllerTest {

    @Autowired
    private lateinit var tester: GraphQlTester

    companion object {
        @Container
        val postgreSQLContainer = PostgreSQLContainer("postgres:16")
            .withUsername(System.getenv("POSTGRES_USER"))
            .withPassword("password")

        @DynamicPropertySource
        @JvmStatic
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
        }
    }

    @Test
    @DisplayName("Add certificate with GraphQL")
    fun addCertificate() {
        val query = """
            mutation {
              newCertificate(certificateGraphQlDto: { code: "codeTestGraphQl" }) {
                id
                code
              }
            }
        """.trimIndent()

        val certificate = tester.document(query)
            .execute()
            .path("data.newCertificate")
            .entity(Certificate::class.java)
            .get()

        assertNotNull(certificate)
        assertEquals("codeTestGraphQl", certificate.code)
    }

    @Test
    @DisplayName("Get all certificates with GraphQL")
    fun findAll() {
        val query = """
            {
              certificates {
                id
                code
                person {
                  id
                  firstName
                }
              }
            }
        """.trimIndent()

        val certificates = tester.document(query)
            .execute()
            .path("data.certificates[*]")
            .entityList(Certificate::class.java)
            .get()

        assertFalse(certificates.isEmpty())
        assertEquals(1, certificates.size)
        assertEquals("codeTestGraphQl", certificates.first().code)
        assertNull(certificates.first().person)
    }

    @Test
    @DisplayName("Get certificate by Id with GraphQL")
    fun findById() {
        val query = """
            {
              certificate(id: 1) {
                id,
                code
              }
            }
        """.trimIndent()

        val certificate = tester.document(query)
            .execute()
            .path("data.certificate")
            .entity(Certificate::class.java)
            .get()

        assertNotNull(certificate)
        assertEquals("codeTestGraphQl", certificate.code)
    }
}