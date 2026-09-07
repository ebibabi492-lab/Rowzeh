package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.ui.RowzehMainScreen
import com.example.ui.RowzehViewModel
import com.example.ui.RowzehViewModelFactory
import com.example.ui.guide.RowzehGuideScreen
import com.example.ui.settings.RowzehSettingsScreen
import com.example.ui.theme.RowzehClockTheme

enum class AppScreen {
    MAIN,
    SETTINGS,
    GUIDE
}

class MainActivity : ComponentActivity() {

  private val viewModel: RowzehViewModel by viewModels {
    RowzehViewModelFactory(application)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      RowzehClockTheme {
        var currentScreen by rememberSaveable { mutableStateOf(AppScreen.MAIN) }

        Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
          when (screen) {
            AppScreen.MAIN -> RowzehMainScreen(
              viewModel = viewModel,
              onNavigateToSettings = { currentScreen = AppScreen.SETTINGS },
              onNavigateToGuide = { currentScreen = AppScreen.GUIDE }
            )
            AppScreen.SETTINGS -> RowzehSettingsScreen(
              viewModel = viewModel,
              onNavigateBack = { currentScreen = AppScreen.MAIN },
              onNavigateToGuide = { currentScreen = AppScreen.GUIDE }
            )
            AppScreen.GUIDE -> RowzehGuideScreen(
              onNavigateBack = { currentScreen = AppScreen.MAIN }
            )
          }
        }
      }
    }
  }
}

