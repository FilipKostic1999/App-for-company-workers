package com.example.company_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PolicyCheck : AppCompatActivity() {
    private lateinit var checkImg: ImageView
    private lateinit var acceptBtn: Button
    private var isChecked = false // Track whether the terms are accepted

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user has already accepted the policy
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isPolicyAccepted = sharedPreferences.getBoolean("isPolicyAccepted", false)

        if (isPolicyAccepted) {
            // If already accepted, go directly to WorkerSignIn activity
            startActivity(Intent(this, WorkerSignIn::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_policy_check)

        // Find WebView and configure it
        val webView: WebView = findViewById(R.id.webView)
        checkImg = findViewById(R.id.checkImg) // Ensure this ID matches your XML
        acceptBtn = findViewById(R.id.acceptBtn) // Ensure this ID matches your XML

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

        // Click listener for the ImageView (checkbox)
        checkImg.setOnClickListener {
            // Toggle the image and the checked state
            if (!isChecked) {
                checkImg.setImageResource(R.drawable.baseline_check_box_24) // Checked image
            } else {
                checkImg.setImageResource(R.drawable.baseline_check_box_outline_blank_24) // Unchecked image
            }
            isChecked = !isChecked // Toggle state
        }

        // Click listener for the Accept button
        acceptBtn.setOnClickListener {
            if (isChecked) {
                // Save the preference indicating the policy has been accepted
                with(sharedPreferences.edit()) {
                    putBoolean("isPolicyAccepted", true)
                    apply()
                }

                // Navigate to WorkerSignIn activity
                val intent = Intent(this, WorkerSignIn::class.java)
                startActivity(intent)
                // Optionally, finish this activity if you don't want to return
                finish()
            } else {
                // Show a Toast message if the user hasn't checked the agreement
                showToast("You need to accept the terms and policy agreement")
            }
        }
    }

    // Helper function to show Toast messages
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
