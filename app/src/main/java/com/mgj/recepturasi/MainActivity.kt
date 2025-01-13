package com.mgj.recepturasi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.mgj.recepturasi.utils.PreferenceManager // Replace with actual package for your shared preferences utility

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check user authentication status
        val isLoggedIn = checkUserLoggedIn()

        // Navigate to the appropriate activity
        if (isLoggedIn) {
            // User is logged in, navigate to HomePageActivity
            val homeIntent = Intent(this, HomeActivity::class.java) // Replace with your home page activity
            startActivity(homeIntent)
        } else {
            // User is not logged in, navigate to LoginActivity
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        finish() // Close MainActivity after redirection
    }

    /**
     * Check if the user is already logged in.
     * Replace with your actual authentication check logic.
     */
    private fun checkUserLoggedIn(): Boolean {
        // Example using shared preferences (replace with your logic)
        val sharedPreferences = PreferenceManager(this)
        return sharedPreferences.isLoggedIn() // Assume `isLoggedIn` returns a boolean
    }
}
