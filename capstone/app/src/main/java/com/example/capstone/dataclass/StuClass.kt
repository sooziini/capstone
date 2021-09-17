package com.example.capstone.dataclass

import java.io.Serializable

data class StuClass (
    var subject : String?,
    var day: String,
    var period : Int?,
    val location: String?,
    val teacher: String?,
    var startTime : String = when (period) {
        1 -> "8:10"
        2 -> "9:10"
        3 -> "10:10"
        4 -> "11:10"
        5 -> "13:00"
        6 -> "14:00"
        7 -> "15:00"
        else -> "12:00"
    },
    var endTime : String = when (period) {
        1 -> "9:00"
        2 -> "10:00"
        3 -> "11:00"
        4 -> "12:00"
        5 -> "13:50"
        6 -> "14:50"
        7 -> "15:50"
        else -> "13:00"
    }
): Serializable