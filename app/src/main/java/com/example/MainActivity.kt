package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.RowzehMainScreen
import com.example.ui.RowzehViewModel
import com.example.ui.RowzehViewModelFactory
import com.example.ui.guide.RowzehGuideScreen
import com.example.ui.settings.RowzehSettingsScreen
import com.example.ui.theme.RowzehClockTheme
import com.example.util.LocalAppLanguage
import com.example.util.LocalAppStrings
import com.example.util.StringsProvider

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
      val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
      val appStrings = remember(currentLanguage) { StringsProvider.getStrings(currentLanguage) }
      val layoutDirection = if (currentLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

      CompositionLocalProvider(
        LocalAppLanguage provides currentLanguage,
        LocalAppStrings provides appStrings,
        LocalLayoutDirection provides layoutDirection
      ) {
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
}

