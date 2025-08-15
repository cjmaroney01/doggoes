package com.colinmaroney.core.data


data class ResetPasswordRequest(
    val userId: Int,
    val passwordToken: String,
    val newPassword: String
)
