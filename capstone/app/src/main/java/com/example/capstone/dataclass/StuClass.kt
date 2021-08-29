package com.example.capstone.dataclass

import java.io.Serializable

data class StuClass (
    var subject : String?,
    var day: String,
    var period : Int?,
    var startTime : String = when (period) {
        1 -> "8:00"
        2 -> "9:00"
        3 -> "10:00"
        4 -> "11:00"
        5 -> "13:00"
        6 -> "14:00"
        7 -> "15:00"
        else -> "12:00"
    },
    var endTime : String = when (period) {
        1 -> "8:50"
        2 -> "9:50"
        3 -> "10:50"
        4 -> "11:50"
        5 -> "13:50"
        6 -> "14:50"
        7 -> "15:50"
        else -> "13:00"
    }
): Serializable