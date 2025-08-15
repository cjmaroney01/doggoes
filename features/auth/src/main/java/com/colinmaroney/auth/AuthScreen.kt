package com.colinmaroney.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.colinmaroney.auth.viewModel.AuthViewModel
import com.colinmaroney.core.ui.RandomDog
import com.colinmaroney.core.ui.theme.bodyFontFamily
import com.colinmaroney.core.ui.theme.displayFontFamily


@Composable
fun AuthScreen(onRegisterClick: () -> Unit,
               onForgotPasswordClick: () -> Unit,
               onTermsClick: () -> Unit,
               onLoginSuccess: () -> Unit,
               onHowToPlayClick: () -> Unit,
               authViewModel: AuthViewModel = hiltViewModel()
) {
    var username by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var password by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    val randomDogResId  by remember {
        mutableIntStateOf(RandomDog.getRandomDog().resId)
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(top = 40.dp)
,         horizontalAlignment = Alignment.CenterHorizontally) {
        Column(modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                stringResource(R.string.doggoes),
                fontSize = 40.sp,
                fontFamily = displayFontFamily,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .background(MaterialTheme.colorScheme.secondary),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.padding(top = 24.dp)) {
                    Column() {
                        Text(
                            stringResource(R.string.username),
                            fontSize = 18.sp,
                            fontFamily = bodyFontFamily,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(
                                start = 10.dp,
                                top = 10.dp,
                                end = 12.dp
                            )
                        )

                        Spacer(Modifier.height(17.dp))

                        Text(
                            stringResource(R.string.password),
                            fontSize = 18.sp,
                            fontFamily = bodyFontFamily,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(
                                start = 10.dp,
                                end = 12.dp,
                                top = 27.dp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(verticalArrangement = Arrangement.SpaceBetween) {
                        val focusRequester = remember { FocusRequester() }
                        TextField(
                            value = username,
                            onValueChange = {
                                username = it
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .padding(end = 16.dp),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusRequester.requestFocus()
                                }
                            ),
                            singleLine = true
                        )

                        Spacer(Modifier.height(22.dp))

                        TextField(
                            value = password,
                            onValueChange = {
                                password = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp)
                                .height(68.dp)
                                .focusRequester(focusRequester),
                            shape = RoundedCornerShape(16.dp),
                            visualTransformation = if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    authViewModel.loginUser(username.text, password.text)
                                }
                            ),
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    passwordVisible = !passwordVisible
                                }) {
                                    if (passwordVisible) {
                                        Icon(painterResource(R.drawable.visibility_off),
                                            contentDescription = stringResource(R.string.hide_password)
                                        )
                                    } else {
                                        Icon(painterResource(R.drawable.visibility),
                                            contentDescription = stringResource(R.string.show_password)
                                        )
                                    }
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(
                        modifier = Modifier
                            .padding(end = 16.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        onClick = { onForgotPasswordClick() } ) {
                        Text(
                            stringResource(R.string.forgot_password),
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = 12.sp,
                            fontFamily = bodyFontFamily,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }

                if (uiState.invalidPassword) {
                    Row(modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .fillMaxWidth()
                        .height(80.dp)
                        .shadow(elevation = 10.dp)
                        .border(width = 3.dp, color = Color.Black)
                        .background(color = Color.White),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                stringResource(R.string.invalid_username_or_password),
                                color = MaterialTheme.colorScheme.error,
                                fontFamily = bodyFontFamily,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 16.dp))

                            IconButton(onClick = {
                                authViewModel.clearInvalidPasswordError()
                            }) {
                                Icon(Icons.Default.Close,
                                    contentDescription = stringResource(R.string.close)
                                )
                            }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        modifier = Modifier
                            .padding(end = 16.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        onClick = {
                            onHowToPlayClick()
                        }) {
                            Text(
                                stringResource(R.string.how_to_play),
                                modifier = Modifier.padding(start = 16.dp),
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontFamily = bodyFontFamily,
                                textDecoration = TextDecoration.Underline
                            )
                        }

                    Button(
                        modifier = Modifier
                            .size(width = 125.dp, height = 50.dp)
                            .padding(end = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        onClick = {
                            authViewModel.loginUser(username.text, password.text)
                        })
                    {
                        Text(stringResource(R.string.login))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        stringResource(R.string.new_user),
                        modifier = Modifier.padding(end = 8.dp, top = 5.dp),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontFamily = bodyFontFamily
                    )

                    TextButton(
                        modifier = Modifier
                            .size(width = 145.dp, height = 55.dp)
                            .padding(end = 16.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        onClick = {}) {
                        Text(
                            stringResource(R.string.register), fontSize = 16.sp,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }
        }

        Image(painter = painterResource(randomDogResId),
            contentDescription = stringResource(R.string.dog),
            modifier = Modifier
                .height(250.dp)
                .width(250.dp)
                .align(Alignment.Start)
                .padding(start = 16.dp))
    }
}

@Preview
@Composable
fun AuthScreenPreview() {
    AuthScreen({}, {}, {}, {}, {})
}