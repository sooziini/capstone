package com.example.capstone.dataclass

data class ReplyReport (
    val replyId: Int,
    val sendId: String,
    val recvId: String,
    val body: String,
    val regDate: String
)