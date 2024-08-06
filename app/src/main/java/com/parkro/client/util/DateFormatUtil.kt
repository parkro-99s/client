package com.parkro.client.util

import java.text.SimpleDateFormat
import java.util.*

object DateFormatUtil {
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    // 기본 날짜 포맷팅 [yyyy-MM-dd HH:mm:ss] => [yyyy.MM.dd HH:mm]
    fun formatDate(dateString: String?): String {
        if (dateString == null) return "-"
        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            "-"
        }
    }

    // [hour: 2, minute: 20] => [02시간 20분]
    fun formatHourMinuteToTime(hour: Int, minute: Int): String {
        return String.format("%02d시간 %02d분", hour, minute)
    }

    // [hour: 2] => [2시간 00분]
    fun formatHourToTime(hour: Int): String {
        return String.format("%02d시간 00분", hour)
    }

    // [minute: 70] => [1시간 10분]
    fun formatMinuteToTime(minute: Int): String {
        val hours = minute / 60
        val remainingMinutes = minute % 60
        return String.format("%02d시간 %02d분", hours, remainingMinutes)
    }

    fun parseDate(dateString: String, pattern: String = "yyyy-MM-dd HH:mm"): Date {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.parse(dateString) ?: Date()
    }
}