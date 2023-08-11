package com.antweb.silentboot.feature.home

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.antweb.silentboot.NavHandler

@Composable
fun HomeScreen(
    navHandler: NavHandler,
    model: HomeViewModel = viewModel(),
) {
    Button(onClick = { navHandler("/help", null) }) {
        Text("Home -> Help")
    }
}
