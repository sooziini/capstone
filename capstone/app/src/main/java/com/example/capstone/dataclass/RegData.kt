package com.example.capstone.dataclass

import java.io.Serializable

data class RegData(
    val id: String,
    val password: String,
    val name: String,
    val phone: String,
    val nickname: String,
    val birth: String,

    val schoolGrade: Int,
    val schoolClass: Int
): Serializable