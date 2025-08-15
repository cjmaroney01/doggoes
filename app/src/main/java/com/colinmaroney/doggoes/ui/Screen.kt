package com.colinmaroney.doggoes.ui

import androidx.compose.runtime.Composable

data class Screen(
    val label: String,
    val icon: @Composable () -> Unit,
    val content: @Composable () -> Unit,
)
