package ru.pachan.main_kotlin.repository.main

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.pachan.main_kotlin.model.main.Skill

@Repository
interface SkillRepository : JpaRepository<Skill, Long> {
}