package com.example.capstone.dataclass

data class ReplyReport (
    val replyId: String,
    val sendId: String,
    val recvId: String,
    val body: String,
    val regDate: String
)