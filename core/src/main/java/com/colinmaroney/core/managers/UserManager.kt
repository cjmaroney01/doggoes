package com.colinmaroney.core.managers

import android.content.Context
import android.util.Log
import com.colinmaroney.core.data.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.content.edit
import com.colinmaroney.core.data.HintRequest
import com.colinmaroney.core.data.ResetPasswordRequest
import com.colinmaroney.core.data.TokenResponse
import com.colinmaroney.core.data.UserLoginRequest
import com.colinmaroney.core.exception.InvalidGuessException
import com.colinmaroney.core.exception.InvalidPasswordException
import com.colinmaroney.core.exception.LockedOutException
import com.colinmaroney.core.exception.UnknownUserException
import com.colinmaroney.core.services.UserService
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface UserManager {
    fun isLoggedIn(): Boolean
    fun setUser(user: User)
    fun getUser(): User?
    fun logout()
    suspend fun login(username: String, password: String): Result<User>
    suspend fun getUserByName(username: String): Result<User>
    suspend fun guessUserPasswordHint(userId: Int, guess: String): Result<TokenResponse>
    suspend fun resetPassword(userId: Int, token: String, password: String): Result<Unit>
}

class UserManagerImpl @Inject constructor(
    @ApplicationContext val context: Context
) : UserManager {
    companion object {
        const val PREFS_NAME = "DOGGOES_USER"
        const val USER_KEY = "USER"
    }


    internal val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    internal val userService = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/doggoes/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(UserService::class.java)


    override fun isLoggedIn(): Boolean {
        return getUser() != null
    }

    override fun setUser(user: User) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(user)
        prefs.edit(commit = true) { putString(USER_KEY, json) }
    }

    override fun getUser(): User? {
        val userJson = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(
            USER_KEY, null)
        return userJson?.let {
            Gson().fromJson(it, User::class.java)
        }
    }

    override fun logout() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        prefs.edit(commit = true) { remove(USER_KEY) }
    }

    override suspend fun login(username: String, password: String): Result<User> {
        return try {
            val user = userService.loginUser(UserLoginRequest(username, password))
            setUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Log.d("COLIN", e.message ?: "Unknown error")
            if (e is HttpException) {
                if (e.code() == 403) {
                    Result.failure<User>(
                        InvalidPasswordException(
                            e.response()?.errorBody()?.string() ?: ""
                        )
                    )
                } else if (e.code() == 500 && e.response()?.errorBody()?.string() == "Cannot find user!") {
                    Result.failure(
                        UnknownUserException("unknown user")
                    )
                } else {
                    Result.failure(e)
                }
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getUserByName(username: String): Result<User> {
        return try {
            val user = userService.getUserByUsername(username)
            Result.success(user)
        } catch (e: Exception) {
            if (e is HttpException) {
                if (e.code() == 500 && e.response()?.errorBody()?.string() == "User not found") {
                    Result.failure(
                        UnknownUserException("unknown user")
                    )
                } else {
                    Result.failure(e)
                }
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun guessUserPasswordHint(userId: Int, guess: String): Result<TokenResponse> {
        return try {
            Result.success(userService.guessPasswordHint(HintRequest(id = userId, guess = guess)))
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 403) {
                val message = e.response()?.errorBody()?.string()
                if (message == "Incorrect guess!") {
                    Result.failure(InvalidGuessException("Incorrect guess!"))
                } else if (message == "LOCKOUT") {
                    Result.failure(LockedOutException("LOCKOUT"))
                } else {
                    Result.failure(e)
                }
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun resetPassword(userId: Int, token: String, password: String): Result<Unit> {
        return try {
            Result.success(userService.resetPassword(ResetPasswordRequest(
                userId = userId, passwordToken = token, newPassword = password
            )))
        } catch(e: Exception) {
            Result.failure(e)
        }
    }
}