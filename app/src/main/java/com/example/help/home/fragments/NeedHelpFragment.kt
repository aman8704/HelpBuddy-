package com.example.help.home.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.example.help.R
import com.example.help.home.needs.Need
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class NeedHelpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database: DatabaseReference
    private lateinit var needEt: AppCompatEditText
    private lateinit var driveLinkEt: AppCompatEditText
    private lateinit var locationEt: AppCompatEditText
    private lateinit var priceEt: AppCompatEditText
    private lateinit var extraEt: AppCompatEditText
    private lateinit var totalTv: TextView
    private lateinit var postBtn: AppCompatButton

    private var existingNeed: Need? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_need_help, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Needs")

        // Initialize Views
        needEt = view.findViewById(R.id.help_need_et)
        driveLinkEt = view.findViewById(R.id.help_drive_link_et)
        locationEt = view.findViewById(R.id.help_delivery_location_et)
        priceEt = view.findViewById(R.id.help_price_et)
        extraEt = view.findViewById(R.id.help_extra_et)
        totalTv = view.findViewById(R.id.help_total_tv)
        postBtn = view.findViewById(R.id.help_post_btn)

        // Check if we are in Edit Mode
        arguments?.let {
            existingNeed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Modern type-safe way for API 33+
                it.getSerializable("EDIT_NEED", Need::class.java)
            } else {
                // Legacy way for older versions
                @Suppress("DEPRECATION")
                it.getSerializable("EDIT_NEED") as? Need
            }
            if (existingNeed != null) {
                populateFields(existingNeed!!)
                postBtn.text = "Update Need"
            }
        }

        // Setup TextWatcher for auto-calculating total price
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateTotal()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        priceEt.addTextChangedListener(textWatcher)
        extraEt.addTextChangedListener(textWatcher)

        postBtn.setOnClickListener {
            val needText = needEt.text.toString().trim()
            val driveLink = driveLinkEt.text.toString().trim()
            val location = locationEt.text.toString().trim()

            // Get raw strings to check if they are empty
            val priceText = priceEt.text.toString().trim()
            val extraText = extraEt.text.toString().trim()

            // 1. Validate Description
            if (needText.isEmpty()) {
                needEt.error = "Description is required"
                needEt.requestFocus()
                return@setOnClickListener
            }

            // 2. Validate Delivery Address
            if (location.isEmpty()) {
                locationEt.error = "Delivery address is required"
                locationEt.requestFocus()
                return@setOnClickListener
            }

            // 3. Validate Product Price
            if (priceText.isEmpty()) {
                priceEt.error = "Product price is required"
                priceEt.requestFocus()
                return@setOnClickListener
            }

            // 4. Validate Extra Charge
            if (extraText.isEmpty()) {
                extraEt.error = "Extra charge is required"
                extraEt.requestFocus()
                return@setOnClickListener
            }

            // If all validations pass, proceed with calculations and posting
            val price = priceText.toDoubleOrNull() ?: 0.0
            val extra = extraText.toDoubleOrNull() ?: 0.0
            val total = price + extra

            if (existingNeed != null) {
                updateNeed(existingNeed!!.id, needText, driveLink, location, price, extra, total)
            } else {
                postNeed(needText, driveLink, location, price, extra, total)
            }
        }


        return view

    }

    @SuppressLint("SetTextI18n")
    private fun populateFields(need: Need) {
        needEt.setText(need.needText)
        driveLinkEt.setText(need.driveLink)
        locationEt.setText(need.deliveryLocation)
        priceEt.setText(need.productPrice.toString())
        extraEt.setText(need.extraCharge.toString())
        totalTv.text = "Total Price: ₹ ${need.totalPrice}"
    }

    @SuppressLint("SetTextI18n")
    private fun calculateTotal() {
        val price = priceEt.text.toString().toDoubleOrNull() ?: 0.0
        val extra = extraEt.text.toString().toDoubleOrNull() ?: 0.0
        val total = price + extra
        totalTv.text = "Total Price: ₹ $total"
    }

    private fun postNeed(text: String, link: String, location: String, price: Double, extra: Double, total: Double) {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        // Fetch Username from Firestore before posting to Realtime Database
        firestore.collection("Users").document(uid).get()
            .addOnSuccessListener { document ->
                val userName = document.getString("name") ?: "Anonymous"

                val needId = database.push().key ?: return@addOnSuccessListener
                val need = Need(
                    id = needId,
                    userId = uid,
                    userName = userName,
                    needText = text,
                    driveLink = link,
                    deliveryLocation = location,
                    productPrice = price,
                    extraCharge = extra,
                    totalPrice = total,
                    timestamp = System.currentTimeMillis(),
                    status = "Pending"
                )

                database.child(needId).setValue(need).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Need posted successfully!", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Firestore Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateNeed(id: String, text: String, link: String, location: String, price: Double, extra: Double, total: Double) {
        val updates = hashMapOf<String, Any>(
            "needText" to text,
            "driveLink" to link,
            "deliveryLocation" to location,
            "productPrice" to price,
            "extraCharge" to extra,
            "totalPrice" to total
        )

        database.child(id).updateChildren(updates).addOnSuccessListener {
            Toast.makeText(requireContext(), "Need updated successfully!", Toast.LENGTH_SHORT).show()
            navigateToHome()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Update failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToHome() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, HomeFragment())
            .commit()
    }
}
