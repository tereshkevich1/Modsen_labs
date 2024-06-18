package com.example.onboarding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.onboarding.main.OnBoardScreen
import com.example.onboarding.ui.theme.OnboardingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OnboardingTheme {
                // A surface container using the 'background' color from the theme
                OnBoardScreen()
            }
        }
    }
}