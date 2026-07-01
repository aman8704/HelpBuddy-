package com.example.help.log_reg

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.help.databinding.ActivityLoginBinding
import com.example.help.home.HomeScreenActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Login Button
        binding.loginBtn.setOnClickListener {

            val email = binding.emailLogin.text.toString().trim()
            val password = binding.passwordLogin.text.toString().trim()

            // Check if email is empty
            if (email.isEmpty()) {
                binding.emailLogin.error = "Enter Email"
                binding.emailLogin.requestFocus()
                return@setOnClickListener
            }

            // Check if password is empty
            if (password.isEmpty()) {
                binding.passwordLogin.error = "Enter Password"
                binding.passwordLogin.requestFocus()
                return@setOnClickListener
            }

            // Login using Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val user = auth.currentUser

                        // Reload user information
                        user?.reload()

                        if (user != null && user.isEmailVerified) {

                            Toast.makeText(
                                this,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(
                                Intent(this, HomeScreenActivity::class.java)
                            )

                            finish()

                        } else {

                            Toast.makeText(
                                this,
                                "Please verify your email first.",
                                Toast.LENGTH_LONG
                            ).show()

                            auth.signOut()
                        }

                    } else {

                        Toast.makeText(
                            this,
                            task.exception?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        binding.forgotPassword.setOnClickListener {

            val email = binding.emailLogin.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailLogin.error = "Enter your email first"
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Password reset email sent",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener {

                    Toast.makeText(
                        this,
                        it.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}