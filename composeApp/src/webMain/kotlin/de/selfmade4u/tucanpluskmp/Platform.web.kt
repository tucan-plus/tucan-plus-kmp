package de.selfmade4u.tucanpluskmp
import androidx.compose.runtime.Composable


@Composable
actual fun RequestNotificationPermission() {

}

actual fun retrieveNotifier(): Notifier {
    return object : Notifier {
        override fun sendNotification() {
            println("sending notification")
        }
    }
}

