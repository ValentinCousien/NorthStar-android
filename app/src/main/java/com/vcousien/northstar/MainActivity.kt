package com.vcousien.northstar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vcousien.northstar.app.AppViewModel
import com.vcousien.northstar.app.NorthStarApp
import com.vcousien.northstar.core.designsystem.components.ColorSystemDemo
import com.vcousien.northstar.features.home.MockHomeScreen
import com.vcousien.northstar.ui.theme.NorthStarTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for the NorthStar application
 * 
 * This activity serves as the single entry point for the app and hosts
 * the main Compose UI hierarchy.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val appViewModel: AppViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            NorthStarTheme {
                LaunchedEffect(Unit) {
                    appViewModel.appDidLaunch()
                }

                NorthStarApp(appViewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name! Mental Health Tracking App",
        modifier = modifier
    )
}

@Preview(showBackground = true, name = "Color System Light")
@Composable
fun ColorSystemPreview() {
    NorthStarTheme(darkTheme = false) {
        ColorSystemDemo()
    }
}

@Preview(showBackground = true, name = "Color System Dark")
@Composable
fun ColorSystemDarkPreview() {
    NorthStarTheme(darkTheme = true) {
        ColorSystemDemo()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NorthStarTheme {
        Greeting("NorthStar")
    }
}

@Preview(name = "Mock Home Screen", showBackground = true, showSystemUi = true)
@Composable
fun MockHomeScreenMainPreview() {
    NorthStarTheme {
        MockHomeScreen()
    }
}
