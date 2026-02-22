package de.selfmade4u.tucanpluskmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val dataStore = createDataStore(this)

        setContent {
            App(intent.data.toString(), dataStore)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    val context = LocalContext.current
    App(null, createDataStore(context))
}