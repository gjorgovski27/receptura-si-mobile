package com.mgj.recepturasi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mgj.recepturasi.viewmodel.UserViewModel
import com.mgj.recepturasi.viewmodel.UserViewModelFactory

class LoginActivity : AppCompatActivity() {

    // Provide the context to UserViewModelFactory
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(applicationContext) // Pass the application context here
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)
        val signupLink: TextView = findViewById(R.id.signupLink) // Add the signup link

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validate input
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Email or Password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Perform login using ViewModel
            userViewModel.login(email, password)

            // Observe login response
            userViewModel.loginResponse.observe(this) { result ->
                result.onSuccess { response ->
                    Log.d("LoginSuccess", "Response: $response")
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                    // Navigate to HomeActivity
                    navigateToHomePage(
                        response.user.userId,
                        response.user.userName,
                        response.user.userFullName,
                        response.user.userEmail
                    )
                }.onFailure { error ->
                    Log.e("LoginError", "Error: ${error.message}")
                    Toast.makeText(this, "Login failed: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set OnClickListener for the signup link
        signupLink.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Navigate to the HomeActivity with user details.
     */
    private fun navigateToHomePage(userId: Int, userName: String, userFullName: String, userEmail: String) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("userId", userId)
            putExtra("userName", userName)
            putExtra("userFullName", userFullName)
            putExtra("userEmail", userEmail)
        }
        startActivity(intent)
    }
}
