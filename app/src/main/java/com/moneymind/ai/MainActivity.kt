package com.moneymind.ai

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.moneymind.ai.core.navigation.MoneyMindNavGraph
import com.moneymind.ai.core.theme.MoneyMindTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoneyMindApp()
        }
    }
}

@Composable
private fun MoneyMindApp() {
    MoneyMindTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            MoneyMindNavGraph(navController = navController)
        }
    }
}
