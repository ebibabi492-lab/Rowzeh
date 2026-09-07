package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.RowzehMainScreen
import com.example.ui.RowzehViewModel
import com.example.ui.RowzehViewModelFactory
import com.example.ui.theme.RowzehClockTheme

class MainActivity : ComponentActivity() {

  private val viewModel: RowzehViewModel by viewModels {
    RowzehViewModelFactory(application)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      RowzehClockTheme {
        RowzehMainScreen(viewModel = viewModel)
      }
    }
  }
}

