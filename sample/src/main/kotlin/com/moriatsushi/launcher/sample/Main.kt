package com.moriatsushi.launcher.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.moriatsushi.launcher.Entry

@Entry(default = true)
@Composable
fun Main() {
    MaterialTheme {
        val otherLauncher = rememberOtherLauncher()

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Button(
                onClick = { otherLauncher.launch() },
            ) {
                Text(text = "launch Other page")
            }
        }
    }
}
