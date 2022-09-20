package ru.tsu.testws.webview

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import okhttp3.internal.notify
import ru.tsu.testws.R
import ru.tsu.testws.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity(R.layout.activity_web) {

    private val binding: ActivityWebBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.webView.webViewClient = CustomWebViewClient(this)
        binding.webView.loadUrl("https://www.google.com")


        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "new_channel"
            val descriptionText = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(name, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        //}

        var builder = NotificationCompat.Builder(this, channel.id)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New notification")
            .setContentText("There is a new notification! Enjoy it!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder)
        }
    }
}

class CustomWebViewClient(private val context: Context): WebViewClient() {
    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        val value = Uri.parse(url).getQueryParameter("q")
        Toast.makeText(context, value, 10).show()
        super.doUpdateVisitedHistory(view, url, isReload)
    }
}