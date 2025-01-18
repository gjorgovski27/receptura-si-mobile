package com.mgj.recepturasi

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class NewsDetailsActivity : AppCompatActivity() {

    private lateinit var newsTitle: TextView
    private lateinit var newsContent: TextView
    private lateinit var newsDate: TextView
    private lateinit var newsImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_details)

        // Initialize UI elements
        newsTitle = findViewById(R.id.newsTitle)
        newsContent = findViewById(R.id.newsContent)
        newsDate = findViewById(R.id.newsDate)
        newsImage = findViewById(R.id.newsImage)

        // Get intent data
        val articleId = intent.getIntExtra("ARTICLE_ID", -1)
        val articleDescription = intent.getStringExtra("ARTICLE_DESCRIPTION")
        val articleContent = intent.getStringExtra("ARTICLE_CONTENT")
        val createdAt = intent.getStringExtra("CREATED_AT")
        val imageUrl = intent.getStringExtra("IMAGE_URL")

        if (articleId != -1 && articleContent != null && createdAt != null && articleDescription != null) {
            // Populate UI with data
            newsTitle.text = articleDescription
            newsContent.text = articleContent
            newsDate.text = "Published on: ${formatDate(createdAt)}"

            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(newsImage)
            } else {
                newsImage.setImageResource(R.drawable.ic_placeholder) // Default placeholder
            }
        } else {
            Toast.makeText(this, "Invalid news details", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if data is invalid
        }
    }

    private fun formatDate(dateStr: String): String {
        return try {
            // Parse the input ISO 8601 date string with fractional seconds
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Set to UTC for ISO 8601

            // Output format: DD.MM.YYYY HH:MM
            val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

            val date = inputFormat.parse(dateStr) // Parse the input string
            date?.let { outputFormat.format(it) } ?: "Invalid Date"
        } catch (e: Exception) {
            "Invalid Date"
        }
    }
}
