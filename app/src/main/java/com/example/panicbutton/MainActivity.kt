package com.example.panicbutton

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.panicbutton.ui.navigation.AppNavigation
import com.example.panicbutton.ui.screens.PreloaderScreen
import com.example.panicbutton.ui.theme.PanicButtonTheme
import com.example.panicbutton.viewmodel.PanicViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: PanicViewModel

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        val app = application as PanicButtonApp
        viewModel = ViewModelProvider(
            this,
            PanicViewModel.Factory(app.repository, app.notificationHelper, applicationContext)
        )[PanicViewModel::class.java]

        requestNotificationPermission()

        enableEdgeToEdge()
        setContent {
            PanicButtonTheme {
                val panicState by viewModel.panicState.collectAsStateWithLifecycle()
                val users by viewModel.users.collectAsStateWithLifecycle()
                val history by viewModel.history.collectAsStateWithLifecycle()
                val historyFilter by viewModel.historyFilter.collectAsStateWithLifecycle()

                var isLoading by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(2000)
                    isLoading = false
                }

                AnimatedContent(
                    targetState = isLoading,
                    transitionSpec = {
                        fadeIn(tween(500)) togetherWith fadeOut(tween(500))
                    },
                    label = "preloader_transition"
                ) { loading ->
                    if (loading) {
                        PreloaderScreen(modifier = Modifier.fillMaxSize())
                    } else {
                        AppNavigation(
                            panicState = panicState,
                            users = users,
                            history = history,
                            historyFilter = historyFilter,
                            onPanicPressed = viewModel::onPanicPressed,
                            onCancelPanic = viewModel::onCancelPanic,
                            onAccept = { viewModel.onHelperResponse(true) },
                            onDecline = { viewModel.onHelperResponse(false) },
                            onRetry = viewModel::onRetry,
                            onDone = viewModel::onDone,
                            onToggleUserOnline = viewModel::toggleUserOnline,
                            onFilterSelected = viewModel::setHistoryFilter,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}
