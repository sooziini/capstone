package com.sangcom.capstone.dataclass

import java.io.Serializable

data class MasterStudent(
    val id: String,
    val studentId: String,
    val name: String,
    var role: String
):Serializable