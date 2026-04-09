package de.selfmade4u.tucanpluskmp
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import org.koin.core.module.Module
import org.koin.dsl.module


@Composable
actual fun RequestNotificationPermission() {

}

fun retrieveNotifier(): Notifier {
    return object : Notifier {
        override fun sendNotification() {
            println("sending notification")
        }
    }
}

actual val platformModule: Module = module {
    single<AppDatabase> {
        createDatabase()
    }
    single<DataStore<Settings?>> {
        createDataStore()
    }
    single<Notifier> {
        retrieveNotifier()
    }
}
