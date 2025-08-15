package com.colinmaroney.auth.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colinmaroney.core.exception.InvalidPasswordException
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
class AuthViewModel @Inject constructor(
    val userManager: UserManager
): ViewModel() {
    private val _authUIState = MutableStateFlow(AuthUIState())
    val uiState = _authUIState.asStateFlow()

    fun loginUser(userName: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = userManager.login(userName, password)
            if (result.isFailure) {
                val e = result.exceptionOrNull()
                e?.let {
                    if (e is InvalidPasswordException ||
                        e is UnknownUserException) {
                        _authUIState.update { it.copy(invalidPassword = true) }
                    }
                }
            } else {
                _authUIState.update { it.copy(loginSuccess = true) }
            }
        }
    }

    fun clearInvalidPasswordError() {
        _authUIState.update { it.copy(invalidPassword = false) }
    }
}

data class AuthUIState(
    val loginSuccess: Boolean = false,
    val invalidPassword: Boolean = false
)