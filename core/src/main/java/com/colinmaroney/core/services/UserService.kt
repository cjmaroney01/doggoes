package com.colinmaroney.core.services

import com.colinmaroney.core.data.HintRequest
import com.colinmaroney.core.data.ResetPasswordRequest
import com.colinmaroney.core.data.TokenResponse
import com.colinmaroney.core.data.User
import com.colinmaroney.core.data.UserLoginRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

internal interface UserService {

    @POST("rest/user/login")
    suspend fun loginUser(@Body userRequest: UserLoginRequest): User

    @GET("rest/getUserByUsername/{name}")
    suspend fun getUserByUsername(@Path("name") username: String): User

    @POST("rest/guessPasswordHint")
    suspend fun guessPasswordHint(@Body hintRequest: HintRequest): TokenResponse

    @PUT("rest/resetPassword")
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest)
}
