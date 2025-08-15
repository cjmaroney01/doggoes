package com.colinmaroney.doggoes.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.colinmaroney.core.managers.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val userManager: UserManager
): ViewModel() {

    fun isLoggedIn(): Boolean {
        return userManager.isLoggedIn()
    }

}