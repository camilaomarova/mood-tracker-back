package kz.kamilaomar.moodtrackerback.service

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kz.kamilaomar.moodtrackerback.models.Task
import kz.kamilaomar.moodtrackerback.repository.TaskRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskService(private val taskRepository: TaskRepository) {


    fun analyzeUserTasks(userId: Long): String {
        val userTasks = taskRepository.findByUserId(userId)

        val (productiveTimes, positiveMoodTimeRanges) = analyzeMoodAndTime(userId)

        val recommendedTasksObject = JsonObject()
        recommendedTasksObject.add("Recommended Tasks", JsonArray()) // Empty array

        val avoidTasksArray = JsonArray()
        avoidUncomfortableTasks(userTasks, productiveTimes.keys.toList()).forEach { avoidTasksArray.add(it) }
        val avoidTasksObject = JsonObject()
        avoidTasksObject.add("Avoid Tasks", avoidTasksArray)

        val exerciseRecommendationsObject = JsonObject()
        exerciseRecommendationsObject.addProperty("message", recommendExercises(userTasks))

        val motivationObject = JsonObject()
        motivationObject.addProperty("message", generateMotivationalMessages())

        val result = JsonObject()

        result.add("Productive Minutes per Mood", Gson().toJsonTree(productiveTimes))

        val pleasantTimeRangesForTasksCompletions = JsonObject()
        for ((mood, ranges) in positiveMoodTimeRanges) {
            val timeRangesArray = JsonArray()
            for (range in ranges) {
                val timeRangeObject = JsonObject()
                val times = range.split(" - ")
                if (times.size == 2) {
                    timeRangeObject.addProperty("start", times[0])
                    timeRangeObject.addProperty("end", times[1])
                    timeRangesArray.add(timeRangeObject)
                } else {
                    // Handle the case where the time range string is not in the expected format
                }
            }
            pleasantTimeRangesForTasksCompletions.add(mood, timeRangesArray)
        }
        result.add("Pleasant Time Ranges for Tasks Completions", pleasantTimeRangesForTasksCompletions)

        result.add("Recommended Tasks", recommendedTasksObject)
        result.add("Avoid Tasks", avoidTasksObject)
        result.add("Exercise Recommendations", exerciseRecommendationsObject)
        result.add("Motivation", motivationObject)

        return Gson().toJson(result)
    }

    // Helper function to convert a list of strings to a JsonObject
    private fun convertListToJsonObject(list: List<String>): JsonArray {
        val jsonArray = JsonArray()
        list.forEach { jsonArray.add(it) }
        return jsonArray
    }

    // Helper function to convert a string to a JsonObject
    private fun convertStringToJsonObject(value: String): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("message", value)
        return jsonObject
    }

    private fun analyzeMoodAndTime(userId: Long): Pair<Map<String, Int>, Map<String, List<String>>> {
        // Retrieve user tasks
        val userTasks = taskRepository.findByUserId(userId)

        // Group tasks by mood and calculate total time for each mood
        val moodTotalTimes = userTasks.groupBy { it.mood }
            .mapValues { entry ->
                val totalMinutes = entry.value.sumBy { task ->
                    calculateMinutesForRangeOfTime(task.startTime, task.finishTime)
                }

                entry.key to totalMinutes // Using entry.key for the mood
            }

        // Determine productive times based on mood total times
        val productiveTimes = mutableMapOf<String, Int>()
        val positiveMoodTimeRanges = mutableMapOf<String, List<String>>()

        for ((mood, totalMinutes) in moodTotalTimes) {
            if (totalMinutes.second > 0) {
                productiveTimes[mood!!] = totalMinutes.second

                // Check for positive moods and add time ranges
                if (isPositiveMood(mood)) {
                    val timeRanges = findPositiveMoodTimeRanges(userTasks, mood)
                        .map { "${it.first} - ${it.second}" }
                    positiveMoodTimeRanges[mood] = timeRanges
                }
            }
        }

        return productiveTimes to positiveMoodTimeRanges
    }

    private fun isPositiveMood(mood: String): Boolean {
        return mood in setOf("Energetic", "Focused", "Determined", "Creative", "Relaxed", "Satisfied")
    }

    private fun findPositiveMoodTimeRanges(tasks: List<Task>, mood: String): List<Pair<String, String>> {
        // Find time ranges when the user is in a positive mood
        return tasks.filter { it.mood == mood }
            .map { task ->
                val startTime = task.startTime ?: "00:00"
                val finishTime = task.finishTime ?: "00:00"
                Pair(startTime, finishTime)
            }
    }

    private fun calculateMinutesForRangeOfTime(startTime: String?, finishTime: String?): Int {
        // Convert start and finish times in HH:mm format to minutes
        val startMinutes = startTime?.split(":")?.let { it[0].toInt() * 60 + it[1].toInt() } ?: 0
        val finishMinutes = finishTime?.split(":")?.let { it[0].toInt() * 60 + it[1].toInt() } ?: 0

        return finishMinutes - startMinutes
    }

    private fun calculateMinutes(time: String?): Int {
        // Convert time in HH:mm format to minutes
        return time?.split(":")?.let { it[0].toInt() * 60 + it[1].toInt() } ?: 0
    }

    private fun recommendTasks(tasks: List<Task>, productiveTimes: List<String>): List<String> {
        // Recommend tasks based on productive times and comfortable moods
        return tasks.filter { it.mood in listOf("Energetic", "Focused", "Determined",
            "Creative", "Relaxed", "Satisfied") && calculateMinutes(it.startTime) in 0..720 }
            .map { it.title ?: "Untitled Task" }
    }

    private fun avoidUncomfortableTasks(tasks: List<Task>, productiveTimes: List<String>): List<String> {
        // Identify tasks associated with uncomfortable moods during productive times
        return tasks.filter { it.mood in listOf("Stressed", "Tired", "Stressed",
            "Overwhelmed", "Unmotivated", "Angry") && calculateMinutes(it.startTime) in 0..720 }
            .map { it.title ?: "Untitled Task" }
    }

    fun recommendExercises(tasks: List<Task>): String {
        val recommendations = mutableSetOf<String>()

        for (task in tasks) {
            val mood = task.mood?.lowercase(Locale.getDefault())
            val negativeMoods = listOf("stressed", "tired", "overwhelmed", "unmotivated", "angry")

            if (negativeMoods.contains(mood)) {
                val exercise = when (task.title?.lowercase(Locale.getDefault())) {
                    "work presentation" -> "Work Presentation - Visualization Exercise: Picture yourself confidently delivering the presentation, focusing on success rather than stress."
                    "grocery shopping" -> "Grocery Shopping - Positive Playlist: Create an uplifting playlist to boost your mood and motivation during shopping."
                    "home cleaning" -> "Home Cleaning - 20-Minute Timer: Set a timer for short cleaning bursts to prevent feeling overwhelmed and maintain motivation."
                    "job interview preparation" -> "Job Interview Preparation - Mock Interviews: Practice answering common questions with a friend or in front of a mirror to build confidence."
                    "fitness routine" -> "Fitness Routine - Variety Challenge: Keep motivation high by introducing new exercises or activities into your routine regularly."
                    "meal planning and cooking" -> "Meal Planning and Cooking - Weekly Menu: Plan your meals for the week ahead to reduce stress and make grocery shopping more efficient."
                    "budgeting" -> "Budgeting - Financial Goals: Set specific and achievable financial goals to stay motivated and focused on budgeting."
                    "academic study session" -> "Academic Study Session - Pomodoro Technique: Break study sessions into short, focused intervals with breaks to maintain concentration."
                    "home repairs" -> "Home Repairs - Prioritize and Delegate: Identify and focus on the most crucial repairs, and delegate tasks when possible."
                    "event planning" -> "Event Planning - Task Checklist: Create a detailed checklist for each aspect of the event to stay organized and avoid feeling overwhelmed."
                    else -> ""
                }

                if (exercise.isNotEmpty()) {
                    recommendations.add(exercise)
                }
            }
        }

        return recommendations.joinToString("\n")
    }

    private fun generateMotivationalMessages(): String {
        // Generate random motivational messages
        return """
            You're doing great! Keep pushing forward!
            Success is a journey, not a destination.
            Every small step counts.
        """
    }
}
