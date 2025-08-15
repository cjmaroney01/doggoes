package com.colinmaroney.doggoes.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.colinmaroney.auth.AuthRoot
import com.colinmaroney.auth.AuthRoute
import com.colinmaroney.auth.ForgotPasswordRoute
import com.colinmaroney.auth.ForgotPasswordScreen
import com.colinmaroney.auth.HowToPlayRoute
import com.colinmaroney.auth.HowToPlayScreen
import com.colinmaroney.auth.RegisterRoute
import com.colinmaroney.auth.RegisterScreen
import com.colinmaroney.auth.TermsRoute
import com.colinmaroney.auth.TermsScreen
import com.colinmaroney.auth.AuthScreen
import com.colinmaroney.doggoes.R
import com.colinmaroney.core.ui.theme.DoggoesTheme
import com.colinmaroney.core.ui.theme.displayFontFamily
import com.colinmaroney.doggoes.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val mainViewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoggoesTheme {
                mainNavGraph(mainViewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun mainNavGraph(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel,
    startDestination: String = if (mainViewModel.isLoggedIn()) Routes.MAIN.routeName else AuthRoot.toString()
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.MAIN.routeName) {
            mainScreen(
                screens = listOf(
                    Screen(
                    label = stringResource(R.string.account),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(R.string.account),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    content = {
                        //accountScreen()
                    }
                ),
                Screen(
                    label = stringResource(R.string.home),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = stringResource(R.string.home),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    content = {
                        HomeScreen()
                    }
                ),
                Screen(
                    label = stringResource(R.string.rules),
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.rule_settings),
                            contentDescription = stringResource(R.string.custom_rules),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    content = {}
                ),
                Screen(
                    label = stringResource(R.string.leaderboard),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(R.string.leaderboard),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    content = {}
                )
            ),
            onFloatingButtonClick = {
                navController.navigate(AuthRoot.toString())
            })
        }
        navigation(startDestination = AuthRoute.toString(), route = AuthRoot.toString()) {
            composable(AuthRoute.toString()) {
                AuthScreen(
                    onRegisterClick = { navController.navigate(RegisterRoute.toString()) },
                    onForgotPasswordClick = { navController.navigate(ForgotPasswordRoute.toString()) },
                    onTermsClick = { navController.navigate(TermsRoute.toString()) },
                    onLoginSuccess = { navController.navigate(Routes.MAIN.routeName) },
                    onHowToPlayClick = { navController.navigate(HowToPlayRoute.toString()) }
                )
            }
            composable(RegisterRoute.toString()) {
                RegisterScreen()
            }
            composable(TermsRoute.toString()) {
                TermsScreen()
            }
            composable(ForgotPasswordRoute.toString()) {
                ForgotPasswordScreen(onCancelClick = {
                    navController.navigate(AuthRoute.toString())
                }, onSuccessClick = {
                    navController.navigate(AuthRoute.toString())
                })
            }

            composable(HowToPlayRoute.toString()) {
                HowToPlayScreen(onNavigateBack = {
                    navController.navigate(AuthRoute.toString())
                })
            }
        }
    }
}

@Composable
fun mainScreen(screens: List<Screen>,
               onFloatingButtonClick: () -> Unit) {
    var selectedItem by remember { mutableStateOf(1) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                screens.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        icon = screen.icon,
                        label = {
                            Text(
                                screen.label,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedItem == 1) {
                FloatingActionButton(
                    onClick = { onFloatingButtonClick() },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiary),
                    ) {
                    Icon(painter = painterResource(R.drawable.camera), "picture")
                }
            }
        }
    )
    { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
        ) {
            screens[selectedItem].content()
        }
    }
}

@Composable
fun HomeScreen() {
    Column() {
        Text(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.doggoes),
            fontFamily = displayFontFamily,
            fontSize = 48.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}