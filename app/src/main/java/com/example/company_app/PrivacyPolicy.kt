package com.example.company_app

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PrivacyPolicy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        // Find WebView and configure it
        val webView: WebView = findViewById(R.id.viewWeb)


        // Enable JavaScript in WebView
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        // Make WebView handle URL loading internally
        webView.webViewClient = WebViewClient()

        // Load the PDF URL into WebView using Google Drive's PDF viewer
        val pdfUrl = "https://drive.google.com/file/d/1Ooo8GbUy6lfhlLatdnOtBeRoRy-b79eC/view?usp=drive_link"
        webView.loadUrl(pdfUrl)

        // Handling insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        }
    }
