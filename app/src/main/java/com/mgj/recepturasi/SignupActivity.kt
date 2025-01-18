package com.mgj.recepturasi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mgj.recepturasi.network.UserCreateRequest
import com.mgj.recepturasi.viewmodel.UserViewModel
import com.mgj.recepturasi.viewmodel.UserViewModelFactory

class SignupActivity : AppCompatActivity() {

    // Provide the context to UserViewModelFactory
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(applicationContext) // Pass the application context here
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val fullNameEditText: EditText = findViewById(R.id.fullNameEditText)
        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val phoneEditText: EditText = findViewById(R.id.phoneEditText)
        val signupButton: Button = findViewById(R.id.signupButton)
        val loginLink: TextView = findViewById(R.id.loginLink)

        // Set up Sign-Up button click listener
        signupButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()

            // Validate input fields
            if (username.isBlank() || fullName.isBlank() || email.isBlank() || password.isBlank() || phone.isBlank()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a UserCreateRequest object
            val user = UserCreateRequest(
                userName = username,
                userFullName = fullName,
                userEmail = email,
                userPassword = password,
                userPhone = phone
            )

            // Perform sign-up using ViewModel
            userViewModel.signUp(user)

            // Observe the sign-up response
            userViewModel.signUpResponse.observe(this) { result ->
                result.onSuccess {
                    Toast.makeText(this, "Sign-Up successful! Please log in.", Toast.LENGTH_SHORT).show()

                    // Navigate to LoginActivity
                    navigateToLoginPage()
                }.onFailure { error ->
                    Toast.makeText(this, "Sign-Up failed: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set up Log-In link click listener
        loginLink.setOnClickListener {
            navigateToLoginPage()
        }
    }

    /**
     * Navigate to the LoginActivity.
     */
    private fun navigateToLoginPage() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
