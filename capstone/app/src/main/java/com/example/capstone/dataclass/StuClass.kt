package com.example.capstone.dataclass

import java.io.Serializable

data class StuClass (
    var classNum : Int?,
    var className : String?,
    var startTime : String,
    var endTime : String
): Serializable