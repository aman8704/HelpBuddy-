package com.example.help.home.navigation_drawer.components

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.help.R

class AboutUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        val github = findViewById<LinearLayout>(R.id.githubLayout)
        val linkedin = findViewById<LinearLayout>(R.id.linkedinLayout)
        val instagram = findViewById<LinearLayout>(R.id.instagramLayout)

        github.setOnClickListener {
            openUrl("https://github.com/aman8704")
        }

        linkedin.setOnClickListener {
            openUrl("https://www.linkedin.com/in/aman-pandit-a46a05286/")
        }

        instagram.setOnClickListener {
            openUrl("https://www.instagram.com/_aman_pandit_874/")
        }
    }

    @SuppressLint("UseKtx")
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}