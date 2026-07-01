package com.example.help.home.needs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.help.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.FirebaseFirestore

class NeedDetailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var need: Need? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_need_detail)

        auth = FirebaseAuth.getInstance()

        val serializableNeed = intent.getSerializableExtra("NEED_DATA")
        if (serializableNeed == null) {
            Toast.makeText(this, "Error: Data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        need = serializableNeed as Need

        val userNameTv = findViewById<TextView>(R.id.detail_user_name)
        val descriptionTv = findViewById<TextView>(R.id.detail_description)
        val driveLinkLabel = findViewById<TextView>(R.id.detail_drive_label)
        val driveLinkTv = findViewById<TextView>(R.id.detail_drive_link)
        val priceTv = findViewById<TextView>(R.id.detail_price)
        val extraTv = findViewById<TextView>(R.id.detail_extra)
        val totalTv = findViewById<TextView>(R.id.detail_total)
        val locationTv = findViewById<TextView>(R.id.detail_location)
        val acceptBtn = findViewById<AppCompatButton>(R.id.detail_accept_btn)
        val deleteBtn = findViewById<AppCompatButton>(R.id.detail_delete_btn)
        val deliveredBtn = findViewById<AppCompatButton>(R.id.detail_delivered_btn)
        val mobileTv = findViewById<TextView>(R.id.detail_mobile)
        val firestore = FirebaseFirestore.getInstance()

        need?.let { n ->
            locationTv.text = if (n.deliveryLocation.isNullOrEmpty()) "No address provided" else n.deliveryLocation
            userNameTv.text = n.userName
            descriptionTv.text = n.needText
            priceTv.text = "₹ ${n.productPrice}"
            extraTv.text = "₹ ${n.extraCharge}"
            totalTv.text = "₹ ${n.totalPrice}"
            
            mobileTv.visibility = View.GONE

            if (n.status.equals("Accepted", ignoreCase = true) && n.acceptedBy == auth.currentUser?.uid) {
                n.userId.let { userId ->
                    firestore.collection("Users").document(userId).get()
                        .addOnSuccessListener { document ->
                            val mobile = document.getString("mobile") ?: "Not Available"
                            val email = document.getString("email") ?: "Not Available"
                            mobileTv.visibility = View.VISIBLE
                            mobileTv.text = "Mobile: $mobile\nEmail: $email"
                        }
                }
            } else {
                mobileTv.visibility = View.VISIBLE
                mobileTv.text = "Mobile number will be visible after accepting this request."
            }

            val currentUid = auth.currentUser?.uid
            if (currentUid == n.userId && n.status.equals("Accepted", true)) {
                firestore.collection("Users").document(n.acceptedBy).get()
                    .addOnSuccessListener { document ->
                        val helperName = document.getString("name") ?: "Unknown"
                        val helperMobile = document.getString("mobile") ?: "Not Available"
                        val helperEmail = document.getString("email") ?: "Not Available"
                        mobileTv.visibility = View.VISIBLE
                        mobileTv.text = "Accepted By\nName: $helperName\nMobile: $helperMobile\nEmail: $helperEmail"
                    }
            }

            if (n.driveLink.isNullOrEmpty()) {
                driveLinkLabel.visibility = View.GONE
                driveLinkTv.visibility = View.GONE
            } else {
                driveLinkTv.text = n.driveLink
                driveLinkTv.setOnClickListener {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(n.driveLink)))
                    } catch (e: Exception) {
                        Toast.makeText(this, "Invalid link", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            when {
                n.status == "Delivered" -> {
                    acceptBtn.visibility = View.GONE
                    deleteBtn.visibility = View.GONE
                    deliveredBtn.visibility = View.GONE
                }
                currentUid == n.userId -> {
                    acceptBtn.visibility = View.GONE
                    deleteBtn.visibility = View.VISIBLE
                    deliveredBtn.visibility = View.GONE
                }
                currentUid == n.acceptedBy -> {
                    acceptBtn.visibility = View.GONE
                    deleteBtn.visibility = View.GONE
                    deliveredBtn.visibility = View.VISIBLE
                }
                else -> {
                    deleteBtn.visibility = View.GONE
                    deliveredBtn.visibility = View.GONE
                    acceptBtn.visibility = if (n.status == "Accepted") View.GONE else View.VISIBLE
                }
            }
        }

        acceptBtn.setOnClickListener {
            val uid = auth.currentUser?.uid ?: return@setOnClickListener
            val db = FirebaseDatabase.getInstance()
            
            firestore.collection("Users").document(uid).get()
                .addOnSuccessListener { document ->
                    val helperName = document.getString("name") ?: "Unknown"
                    val updateMap = hashMapOf<String, Any>(
                        "status" to "Accepted",
                        "acceptedBy" to uid,
                        "acceptedByName" to helperName
                    )

                    need?.let { n ->
                        db.getReference("Needs").child(n.id).updateChildren(updateMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Accepted!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                    }
                }
        }

        deliveredBtn.setOnClickListener {
            val dbRef = FirebaseDatabase.getInstance().getReference("Needs")
            val earningsRef = FirebaseDatabase.getInstance().getReference("Earnings")
            val userId = auth.currentUser?.uid

            need?.let { n ->
                val updates = hashMapOf<String, Any>(
                    "status" to "Delivered",
                    "deliveredAt" to ServerValue.TIMESTAMP
                )
                
                dbRef.child(n.id).updateChildren(updates).addOnSuccessListener {
                    // Save to permanent Earnings node
                    if (userId != null) {
                        val earningRecord = hashMapOf(
                            "needId" to n.id,
                            "amount" to n.extraCharge, // Recording extraCharge as earning
                            "helperId" to userId,
                            "timestamp" to ServerValue.TIMESTAMP,
                            "needTitle" to n.needText
                        )
                        earningsRef.child(userId).push().setValue(earningRecord)
                    }
                    
                    Toast.makeText(this, "Marked as Delivered!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        deleteBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Post")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete") { _, _ ->
                    FirebaseDatabase.getInstance().getReference("Needs").child(need?.id ?: "").removeValue()
                        .addOnSuccessListener { finish() }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
