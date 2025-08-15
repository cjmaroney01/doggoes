package com.colinmaroney.core.data

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("username")
    val userName: String,
    val numMatches: Int,
    val customRules: Boolean,
    val showAds: Boolean,
    val sessionToken: String?,
    val hint: HintModel?,
    val blockedUsers: List<User>
) {
    data class HintModel(
        val hint_id: Int,
        val hint: String
    )
}
