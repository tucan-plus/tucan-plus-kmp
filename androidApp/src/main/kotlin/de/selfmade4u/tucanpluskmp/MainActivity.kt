package de.selfmade4u.tucanpluskmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import de.selfmade4u.tucanpluskmp.getDatabaseBuilder
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val dataStore = createDataStore(this, lifecycleScope)
        val database = getRoomDatabase(getDatabaseBuilder(this))

        setContent {
            App(intent.data.toString(), dataStore, database)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    App(null, createDataStore(context, lifecycleOwner.lifecycleScope), null!!)
}