package com.colinmaroney.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterScreen() {
    Box(modifier = Modifier.fillMaxSize()
        .padding(16.dp)
        .background(MaterialTheme.colorScheme.primary),
        ) {
            Text("Register",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
}