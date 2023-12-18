package kz.kamilaomar.moodtrackerback.repository

import kz.kamilaomar.moodtrackerback.models.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long> {
    fun findByUserId(userId: Long): List<Task>
}
