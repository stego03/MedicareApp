package com.example.medicareapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.medicareapp.navigation.AppNavigation
import com.example.medicareapp.ui.theme.MedicareAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            MedicareAppTheme {

                AppNavigation()

            }
        }
    }
}