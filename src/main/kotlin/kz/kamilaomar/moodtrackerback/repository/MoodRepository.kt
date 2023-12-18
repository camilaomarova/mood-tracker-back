package kz.kamilaomar.moodtrackerback.repository

import kz.kamilaomar.moodtrackerback.models.Mood
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MoodRepository : JpaRepository<Mood, Long>
