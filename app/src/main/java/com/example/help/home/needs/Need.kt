package com.example.help.home.needs

import java.io.Serializable

data class Need(

    val id: String = "",
    val userId: String = "",

    // User Details
    val userName: String = "",
    val branch: String = "",
    val mobile: String = "",
    val email: String = "",

    // Need Details
    val needText: String = "",
    val driveLink: String = "",
    val deliveryLocation: String = "",

    // Price Details
    val productPrice: Double = 0.0,
    val extraCharge: Double = 0.0,
    val totalPrice: Double = 0.0,
    val totalEarnings: Double = 0.0,

    // Time
    val timestamp: Long = System.currentTimeMillis(),

    // Request Status
    var status: String = "Pending",      // Pending, Accepted, Delivered
    var acceptedBy: String = "",
    var acceptedByName: String = "",

    var deliveredAt: Long = 0L

) : Serializable
