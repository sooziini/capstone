package com.sangcom.capstone.dataclass

data class ReplyReport (
    val replyId: Int,
    val boardId: Int,
    val sendId: String,
    val recvId: String,
    val body: String,
    val regDate: String
)