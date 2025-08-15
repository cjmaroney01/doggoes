package com.colinmaroney.auth

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.colinmaroney.auth.viewModel.ForgotPasswordViewModel
import com.colinmaroney.core.ui.theme.bodyFontFamily
import com.colinmaroney.core.ui.theme.displayFontFamily
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun ForgotPasswordScreen(onCancelClick: () -> Unit,
                         onSuccessClick: () -> Unit) {
    val viewModel: ForgotPasswordViewModel = hiltViewModel()

    var username by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var hintAnswer by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var newPassword by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var repeatPassword by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var passwordVisible by remember { mutableStateOf(false) }
    var repeatPasswordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val hintFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)
        .padding(16.dp)
    ) {
        Column(modifier = Modifier
            .padding(top = 38.dp)
            .fillMaxSize(),
            ) {
            Text(
                stringResource(R.string.reset_password),
                modifier = Modifier.fillMaxWidth(),
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                fontFamily = displayFontFamily,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = Modifier
                    .padding(horizontal = 16.dp),
                    text = stringResource(R.string.username),
                    fontSize = 20.sp,
                    fontFamily = bodyFontFamily,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                TextField(
                    value = username,
                    onValueChange = {
                        username = it
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    modifier = Modifier
                        .height(68.dp)
                        .padding(end = 16.dp)
                        .weight(1f),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.searchUser(username.text)
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.unknownUserError ||
                uiState.otherError ||
                uiState.invalidGuess ||
                uiState.lockedOut) {
                Row(modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxWidth()
                    .shadow(elevation = 10.dp)
                    .border(width = 3.dp, color = Color.Black)
                    .background(color = Color.White),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        if (uiState.unknownUserError) {
                            stringResource(R.string.user_not_found)
                        } else if (uiState.otherError) {
                            stringResource(R.string.there_was_an_error)
                        } else if (uiState.invalidGuess) {
                            stringResource(R.string.incorrect_guess)
                        } else {
                            stringResource(R.string.three_wrong_guesses_locked_out)
                        },
                        color = MaterialTheme.colorScheme.error,
                        fontFamily = bodyFontFamily,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center)
                }
            }

            if (uiState.showHint) {
                Column {
                    Text(modifier = Modifier
                        .padding(horizontal = 16.dp),
                        text = stringResource(R.string.your_hint_question),
                        fontSize = 20.sp,
                        fontFamily = bodyFontFamily,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier
                        .height(10.dp))

                    Text(uiState.hint,
                        modifier = Modifier.padding(start = 30.dp),
                        fontFamily = bodyFontFamily,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.inversePrimary)

                    Spacer(modifier = Modifier
                        .height(10.dp))

                    Text(
                        stringResource(R.string.your_answer),
                        modifier = Modifier.padding(start = 16.dp),
                        fontFamily = bodyFontFamily,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimary)

                    Spacer(modifier = Modifier
                        .height(10.dp))

                    TextField(
                        value = hintAnswer,
                        onValueChange = {
                            hintAnswer = it
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .padding(horizontal = 16.dp)
                            .focusRequester(hintFocusRequester),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                viewModel.submitHintAnswer(hintAnswer.text)
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .padding(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            modifier = Modifier
                                .size(width = 125.dp, height = 50.dp)
                                .padding(end = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            onClick = {
                                viewModel.submitHintAnswer(hintAnswer.text)
                            }) {
                            Text(
                                stringResource(R.string.submit)
                            )
                        }
                    }
                    LaunchedEffect(true) {
                        hintFocusRequester.requestFocus()
                    }
                }
            }

            if (uiState.showNewPassword) {
                Column {
                    val focusRequester = remember { FocusRequester() }
                    Row {
                        Text(modifier = Modifier
                            .padding(horizontal = 16.dp),
                            text = stringResource(R.string.new_password),
                            fontSize = 20.sp,
                            fontFamily = bodyFontFamily,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        TextField(
                            value = newPassword,
                            onValueChange = {
                                newPassword = it
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .padding(horizontal = 16.dp)
                                .focusRequester(passwordFocusRequester),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusRequester.requestFocus()
                                }
                            ),
                            trailingIcon = {
                                IconButton(onClick = {
                                    passwordVisible = !passwordVisible
                                }) {
                                    if (passwordVisible) {
                                        Icon(
                                            painterResource(R.drawable.visibility_off),
                                            contentDescription =stringResource(R.string.hide_password)
                                        )
                                    } else {
                                        Icon(
                                            painterResource(R.drawable.visibility),
                                            contentDescription = stringResource(R.string.show_password))
                                    }
                                }
                            },
                            visualTransformation = if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row {
                        Text(modifier = Modifier
                            .padding(horizontal = 16.dp),
                            text = stringResource(R.string.repeat_password),
                            fontSize = 20.sp,
                            fontFamily = bodyFontFamily,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        TextField(
                            value = repeatPassword,
                            onValueChange = {
                                repeatPassword = it
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .padding(horizontal = 16.dp)
                                .focusRequester(focusRequester),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Go
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    viewModel.validatePasswordsAndSubmit(newPassword.text, repeatPassword.text)
                                }
                            ),
                            trailingIcon = {
                                IconButton(onClick = {
                                    repeatPasswordVisible = !repeatPasswordVisible
                                }) {
                                    if (repeatPasswordVisible) {
                                        Icon(
                                            painterResource(R.drawable.visibility_off),
                                            contentDescription = stringResource(R.string.hide_password)
                                        )
                                    } else {
                                        Icon(
                                            painterResource(R.drawable.visibility),
                                            contentDescription = stringResource(R.string.show_password)
                                        )
                                    }
                                }
                            },
                            visualTransformation = if (repeatPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                        )
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End) {
                         Button(
                            modifier = Modifier
                                .size(width = 125.dp, height = 50.dp)
                                .padding(end = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            onClick = {
                                viewModel.validatePasswordsAndSubmit(
                                    newPassword.text,
                                    repeatPassword.text
                                )
                            }) {
                            Text(
                                stringResource(R.string.submit),
                            )
                        }
                    }
                }
                LaunchedEffect(true) {
                    passwordFocusRequester.requestFocus()
                }
            }

            if (uiState.passwordError) {
                Row(modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .shadow(elevation = 10.dp)
                    .border(width = 3.dp, color = Color.Black)
                    .background(color = Color.White),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(uiState.passwordErrorMsg),
                        color = MaterialTheme.colorScheme.error,
                        fontFamily = bodyFontFamily,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center)
                }
            }

            if (uiState.passwordResetSuccess) {
                Row(modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxWidth()
                    .shadow(elevation = 10.dp)
                    .border(width = 3.dp, color = Color.Black)
                    .background(color = Color.White),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(R.string.your_password_has_been_reset_login_to_continue),
                        color = Color.Black,
                        fontFamily = bodyFontFamily,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        textAlign = TextAlign.Start)

                    Button(
                        modifier = Modifier
                            .size(width = 100.dp, height = 50.dp)
                            .padding(end = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        onClick = { onSuccessClick() }) {
                        Text(stringResource(R.string.ok))
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            Box(modifier = Modifier.padding(start = 16.dp)) {
                Button(
                    modifier = Modifier
                        .size(width = 125.dp, height = 50.dp)
                        .padding(end = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    onClick = {
                        onCancelClick()
                    })
                {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    }
}