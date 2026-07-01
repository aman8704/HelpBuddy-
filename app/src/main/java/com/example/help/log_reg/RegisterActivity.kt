package com.example.help.log_reg

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.help.databinding.ActivityRegisterBinding
import com.example.help.home.HomeScreenActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    private var emailVerified = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        //----------------------------------------------------
        // Send Verification Email
        //----------------------------------------------------

        binding.sendVerificationBtn.setOnClickListener {

            val email = binding.emailReg.text.toString().trim()
            val password = binding.passwordReg.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailReg.error = "Enter Email"
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.passwordReg.error = "Minimum 6 characters"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            binding.linkSent.visibility = android.view.View.VISIBLE
                            Toast.makeText(
                                this,
                                "Verification Email Sent",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        it.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        //----------------------------------------------------
        // Check Verification
        //----------------------------------------------------

        binding.checkVerificationBtn.setOnClickListener {

            val user = auth.currentUser

            user?.reload()?.addOnSuccessListener {

                if (user.isEmailVerified) {
                    emailVerified = true
                    binding.verificationStatus.text = "✓ Email Verified"
                    binding.verificationStatus.setTextColor(getColor(android.R.color.holo_green_dark))

                    Toast.makeText(
                        this,
                        "Email Verified",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "Please verify from your Email first",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        //----------------------------------------------------
        // Register
        //----------------------------------------------------

        binding.registerBtn.setOnClickListener {
            if (!emailVerified) {
                Toast.makeText(
                    this,
                    "Verify Email First",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Get user details
            val name = binding.nameReg.text.toString().trim()
            val branch = binding.branchReg.text.toString().trim()
            val mobile = binding.mobileReg.text.toString().trim()
            val email = binding.emailReg.text.toString().trim()

            val uid = auth.currentUser?.uid

            if (uid == null) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val firestore = FirebaseFirestore.getInstance()

            val user = hashMapOf(
                "name" to binding.nameReg.text.toString().trim(),
                "branch" to binding.branchReg.text.toString().trim(),
                "mobile" to binding.mobileReg.text.toString().trim(),
                "email" to binding.emailReg.text.toString().trim()
            )
            firestore.collection("Users")
                .document(auth.currentUser!!.uid)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Registration Successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(
                        Intent(this, HomeScreenActivity::class.java)
                    )
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

    }
}