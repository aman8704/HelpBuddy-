package com.example.help.home.navigation_drawer.components

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.help.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import android.view.View

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Load user details
        loadUserData()

        // Email info logic (Read-only field)
        binding.emailEt.setOnClickListener {
            binding.emailInfoTv.visibility = View.VISIBLE
            binding.emailInfoTv.postDelayed({
                binding.emailInfoTv.visibility = View.GONE
            }, 3000)
        }

        // Update Button Click
        binding.updateBtn.setOnClickListener {
            val name = binding.nameEt.text.toString().trim()
            val branch = binding.branchEt.text.toString().trim()
            val mobile = binding.mobileEt.text.toString().trim()

            // Validations
            if (name.isEmpty()) {
                binding.nameEt.error = "Name is required"
                return@setOnClickListener
            }
            if (branch.isEmpty()) {
                binding.branchEt.error = "Branch is required"
                return@setOnClickListener
            }
            if (mobile.length != 10) {
                binding.mobileEt.error = "Enter a valid 10-digit mobile number"
                return@setOnClickListener
            }

            updateProfile(name, branch, mobile)
        }
    }

    private fun loadUserData() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            showToast("User not logged in")
            return
        }

        toggleLoading(true)

        firestore.collection("Users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                toggleLoading(false)

                if (document.exists()) {

                    val name = document.getString("name") ?: ""
                    val branch = document.getString("branch") ?: ""
                    val mobile = document.getString("mobile") ?: ""
                    val email = document.getString("email") ?: ""

                    binding.nameEt.setText(name)
                    binding.branchEt.setText(branch)
                    binding.mobileEt.setText(mobile)
                    binding.emailEt.setText(email)

                } else {
                    showToast("User data not found")
                }
            }
            .addOnFailureListener { e ->
                toggleLoading(false)
                showToast(e.localizedMessage ?: "Something went wrong")
            }
    }

    private fun updateProfile(name: String, branch: String, mobile: String) {

        val uid = auth.currentUser?.uid ?: return

        toggleLoading(true)

        val updates = hashMapOf<String, Any>(
            "name" to name,
            "branch" to branch,
            "mobile" to mobile
        )

        firestore.collection("Users")
            .document(uid)
            .update(updates)
            .addOnSuccessListener {

                toggleLoading(false)
                showToast("Profile Updated Successfully")
                finish()

            }
            .addOnFailureListener {

                toggleLoading(false)
                showToast(it.localizedMessage ?: "Update Failed")

            }
    }

    // Helper function to handle UI state
    private fun toggleLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.scrollView.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.updateBtn.isEnabled = !isLoading
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}