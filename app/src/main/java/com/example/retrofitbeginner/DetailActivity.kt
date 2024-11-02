package com.example.retrofitbeginner

import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.retrofitbeginner.model.Article

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val article : Article? = intent.getParcelableExtra("article_data")
        article?.let {
            val webView = findViewById<WebView>(R.id.webView)
            val progressBar = findViewById<ProgressBar>(R.id.progressBar)

            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    // Show ProgressBar when page starts loading
                    progressBar.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    // Hide ProgressBar when page finishes loading
                    progressBar.visibility = View.GONE
                }

            }

            webView.settings.javaScriptEnabled = true // Enable JavaScript if needed
            webView.loadUrl(it.url) // Load the article
        }
    }



}