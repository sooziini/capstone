package com.example.capstone.dataclass

import java.io.Serializable

data class NotiList(
    val success: String,
    val data: ArrayList<Noti>
): Serializable

data class Noti (
    val title: String,
    val body: String,
    val board_id: Int,
    val type: String,
    val regdate: String
): Serializable