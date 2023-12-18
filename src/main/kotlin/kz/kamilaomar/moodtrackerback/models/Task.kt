package kz.kamilaomar.moodtrackerback.models

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

@Entity
@Table(name = "tasks")
data class Task(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var userId: Long? = null,
    val mood: String? = null,
    val startTime: String? = null,
    val finishTime: String? = null,
    val title: String? = null,
    val description: String? = null,
    val priority: String? = null
)
