package com.example.help.home.notification

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.help.R

class NotificationActivity : AppCompatActivity() {

    private lateinit var tvNoNotification: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        tvNoNotification = findViewById(R.id.tvNoNotification)

        val notificationList = ArrayList<String>()

        if (notificationList.isEmpty()) {
            tvNoNotification.visibility = View.VISIBLE
        } else {
            tvNoNotification.visibility = View.GONE
        }
    }
}