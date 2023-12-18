package kz.kamilaomar.moodtrackerback

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MoodTrackerBackApplication

fun main(args: Array<String>) {
	runApplication<MoodTrackerBackApplication>(*args)
}
