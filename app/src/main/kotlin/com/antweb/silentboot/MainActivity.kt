package com.antweb.silentboot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.antweb.silentboot.feature.help.HelpScreen
import com.antweb.silentboot.feature.home.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Navigator()
        }
    }
}

@Composable
fun Navigator() {
    val navController = rememberNavController()

    val navHandler = { route: String, options: NavOptions? ->
        navController.navigate(route, options)
    }

    return NavHost(navController = navController, startDestination = Routes.home) {
        composable(Routes.home) { HomeScreen(navHandler) }
        composable(Routes.help) { HelpScreen(navHandler) }
    }
}

typealias NavHandler = (route: String, options: NavOptions?) -> Unit
