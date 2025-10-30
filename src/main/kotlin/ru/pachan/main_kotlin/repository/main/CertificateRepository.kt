package ru.pachan.main_kotlin.repository.main

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import ru.pachan.main_kotlin.model.main.Certificate

@Repository
interface CertificateRepository : JpaRepository<Certificate, Long>, JpaSpecificationExecutor<Certificate> {
}