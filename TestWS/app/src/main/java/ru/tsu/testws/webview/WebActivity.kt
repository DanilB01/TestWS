package ru.tsu.testws.webview

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.tsu.testws.R
import ru.tsu.testws.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity(R.layout.activity_web) {

    private val binding: ActivityWebBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.webView.webViewClient = CustomWebViewClient(this)
        binding.webView.loadUrl("https://www.google.com")
    }
}

class CustomWebViewClient(private val context: Context): WebViewClient() {
    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        val value = Uri.parse(url).getQueryParameter("q")
        Toast.makeText(context, value, 10).show()
        super.doUpdateVisitedHistory(view, url, isReload)
    }
}