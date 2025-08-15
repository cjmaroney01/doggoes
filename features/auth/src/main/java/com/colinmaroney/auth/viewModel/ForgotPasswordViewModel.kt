package com.colinmaroney.auth.viewModel

import android.net.http.HttpException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colinmaroney.auth.R
import com.colinmaroney.core.exception.InvalidGuessException
import com.colinmaroney.core.exception.LockedOutException
import com.colinmaroney.core.exception.UnknownUserException
import com.colinmaroney.core.managers.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    val userManager: UserManager
): ViewModel() {
    private val _forgotPasswordUiState = MutableStateFlow(ForgotPasswordUIState())
    val uiState = _forgotPasswordUiState.asStateFlow()


    fun searchUser(username: String) {
        if (username.isEmpty().not()) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = userManager.getUserByName(username)
                if (result.isSuccess) {
                    _forgotPasswordUiState.update {
                        val user = result.getOrNull()
                        user?.let { u ->
                            it.copy(
                                unknownUserError = false,
                                otherError = false,
                                invalidGuess = false,
                                lockedOut = false,
                                passwordError = false,
                                passwordErrorMsg = -1,
                                showHint = true,
                                showNewPassword = false,
                                hint = u.hint?.hint ?: "",
                                hintId = u.hint?.hint_id ?: -1,
                                userId = u.userId,
                                token = ""
                            )
                        } ?: it
                    }
                } else {
                    val e = result.exceptionOrNull()
                    if (e is UnknownUserException) {
                        _forgotPasswordUiState.update {
                            it.copy(
                                unknownUserError = true,
                                otherError = false,
                                showHint = false,
                                showNewPassword = false,
                                userId = -1
                            )
                        }
                    } else {
                        _forgotPasswordUiState.update {
                            it.copy(
                                unknownUserError = false,
                                otherError = true,
                                showHint = false,
                                showNewPassword = false,
                                userId = -1
                            )
                        }
                    }
                }
            }
        }
    }

    fun submitHintAnswer(answer: String) {
        if (answer.isEmpty().not() && uiState.value.userId != -1) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = userManager.guessUserPasswordHint(uiState.value.userId, answer)
                if (result.isSuccess) {
                    val tokenResponse = result.getOrNull()
                    tokenResponse?.let { tokenresp ->
                        _forgotPasswordUiState.update {
                            it.copy(token = tokenresp.token,
                                showHint = false,
                                showNewPassword = true,
                                invalidGuess = false,
                                lockedOut = false,
                                otherError = false)
                        }
                    }
                } else {
                    val e = result.exceptionOrNull()
                    e?.let {
                        if (e is InvalidGuessException) {
                            _forgotPasswordUiState.update {
                                it.copy(invalidGuess = true,
                                    lockedOut = false,
                                    otherError = false)
                            }
                        } else if (e is LockedOutException) {
                            _forgotPasswordUiState.update {
                                it.copy(lockedOut = true,
                                    invalidGuess = false,
                                    otherError = false)
                            }
                        } else {
                            _forgotPasswordUiState.update {
                                it.copy(otherError = true,
                                    invalidGuess = false,
                                    lockedOut = false)
                            }
                        }
                    } ?: run {
                        _forgotPasswordUiState.update {
                            it.copy(otherError = true,
                                invalidGuess = false,
                                lockedOut = false)
                        }
                    }
                }
            }
        }
    }

    fun validatePasswordsAndSubmit(newPassword: String, repeatPassword: String) {
        if (newPassword != repeatPassword) {
              _forgotPasswordUiState.update {
                  it.copy(passwordError = true,
                      passwordErrorMsg = R.string.passwords_do_not_match
                  )
              }
        } else if (newPassword.isEmpty() || newPassword.isBlank()) {
            _forgotPasswordUiState.update {
                it.copy(passwordError = true,
                    passwordErrorMsg = R.string.you_must_supply_a_new_password
                )
            }
        } else if (newPassword.length > 25) {
            _forgotPasswordUiState.update {
                it.copy(passwordError = true,
                    passwordErrorMsg = R.string.passwords_are_limited_to_25_characters
                )
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val result = userManager.resetPassword(uiState.value.userId, uiState.value.token, newPassword)
                if (result.isSuccess) {
                    _forgotPasswordUiState.update {
                        it.copy(showNewPassword = false,
                            passwordError = false,
                            passwordErrorMsg = -1,
                            passwordResetSuccess = true)
                    }
                } else {
                    _forgotPasswordUiState.update {
                        it.copy(passwordError = true,
                            passwordErrorMsg = R.string.error_while_resetting_password
                        )
                    }
                }
            }
        }
    }
}


data class ForgotPasswordUIState(
    val unknownUserError: Boolean = false,
    val otherError: Boolean = false,
    val invalidGuess: Boolean = false,
    val lockedOut: Boolean = false,
    val showHint: Boolean = false,
    val hint: String = "",
    val hintId: Int = -1,
    val userId: Int = -1,
    val token: String = "",
    val showNewPassword: Boolean = false,
    val newPassword: String = "",
    val passwordError: Boolean = false,
    val passwordErrorMsg: Int = -1,
    val passwordResetSuccess: Boolean = false

)