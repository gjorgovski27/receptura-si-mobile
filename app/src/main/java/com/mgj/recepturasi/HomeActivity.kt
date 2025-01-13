package com.mgj.recepturasi

import NewsAdapter
import NewsArticle
import Recipe
import RecipeAdapter
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mgj.recepturasi.network.RetrofitInstance
import com.mgj.recepturasi.network.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var welcomeText: TextView
    private lateinit var searchBox: EditText
    private lateinit var myRecipesRecyclerView: RecyclerView
    private lateinit var topRatedRecipesRecyclerView: RecyclerView
    private lateinit var favouritesRecyclerView: RecyclerView
    private lateinit var newsRecyclerView: RecyclerView
    private val baseUrl = "https://recepturasi.azurewebsites.net/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize UI elements
        welcomeText = findViewById(R.id.welcomeText)
        searchBox = findViewById(R.id.searchBox)
        myRecipesRecyclerView = findViewById(R.id.myRecipesRecyclerView)
        topRatedRecipesRecyclerView = findViewById(R.id.topRatedRecipesRecyclerView)
        favouritesRecyclerView = findViewById(R.id.favouritesRecyclerView)
        newsRecyclerView = findViewById(R.id.newsRecyclerView)

        // Set up RecyclerViews with LinearLayoutManager
        myRecipesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        topRatedRecipesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        favouritesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        newsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch data dynamically
        val userId = intent.getIntExtra("userId", -1)
        if (userId != -1) {
            fetchHomePageData(userId)
        } else {
            Toast.makeText(this, "Invalid User ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchHomePageData(userId: Int) {
        val apiService = RetrofitInstance.getApi(applicationContext)
        val userRepository = UserRepository(apiService)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch data
                val myRecipes = userRepository.getMyRecipes(userId).body() ?: emptyList()
                val topRatedRecipes = userRepository.getTopRatedRecipes().body() ?: emptyList()
                val favourites = userRepository.getFavourites(userId).body() ?: emptyList()
                val news = userRepository.getNews().body() ?: emptyList()

                withContext(Dispatchers.Main) {
                    // Update welcome message
                    welcomeText.text = "Welcome, User $userId!"

                    // Update RecyclerViews
                    myRecipesRecyclerView.adapter = RecipeAdapter(myRecipes, baseUrl) { recipe ->
                        Toast.makeText(this@HomeActivity, "Clicked on ${recipe.title}", Toast.LENGTH_SHORT).show()
                    }
                    topRatedRecipesRecyclerView.adapter = RecipeAdapter(topRatedRecipes, baseUrl) { recipe ->
                        Toast.makeText(this@HomeActivity, "Clicked on ${recipe.title}", Toast.LENGTH_SHORT).show()
                    }
                    favouritesRecyclerView.adapter = RecipeAdapter(favourites, baseUrl) { recipe ->
                        Toast.makeText(this@HomeActivity, "Clicked on ${recipe.title}", Toast.LENGTH_SHORT).show()
                    }
                    newsRecyclerView.adapter = NewsAdapter(news, baseUrl) { article ->
                        Toast.makeText(this@HomeActivity, "Clicked on ${article.articleDescription}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
