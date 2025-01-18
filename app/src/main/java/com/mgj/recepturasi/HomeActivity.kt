package com.mgj.recepturasi

import NewsAdapter
import NewsArticle
import Recipe
import RecipeAdapter
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
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
    private lateinit var topRatedRecipesRecyclerView: RecyclerView
    private lateinit var favouritesRecyclerView: RecyclerView
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var logoutButton: Button
    private lateinit var createRecipeButton: Button

    private val baseUrl = "https://recepturasi.azurewebsites.net/api"
    private var allRecipes: List<Recipe> = emptyList() // Store all recipes for search functionality
    private var currentUserId: Int = -1 // Current user ID
    private var currentUserFullName: String = "" // Current user's full name

    companion object {
        private const val REQUEST_RECIPE_DETAILS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize UI elements
        welcomeText = findViewById(R.id.welcomeText)
        searchBox = findViewById(R.id.searchBox)
        topRatedRecipesRecyclerView = findViewById(R.id.topRatedRecipesRecyclerView)
        favouritesRecyclerView = findViewById(R.id.favouritesRecyclerView)
        newsRecyclerView = findViewById(R.id.newsRecyclerView)
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView)
        logoutButton = findViewById(R.id.logoutButton)
        createRecipeButton = findViewById(R.id.createRecipeButton)

        // Set up RecyclerViews with LinearLayoutManager
        topRatedRecipesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        favouritesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        newsRecyclerView.layoutManager = LinearLayoutManager(this)
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Set logout button listener
        logoutButton.setOnClickListener {
            logout()
        }

        // Fetch data dynamically
        currentUserId = intent.getIntExtra("userId", -1)
        currentUserFullName = intent.getStringExtra("userFullName") ?: "User"

        if (currentUserId != -1) {
            fetchHomePageData(currentUserId)

            // Set up search functionality
            searchBox.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filterRecipes(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            // Set up Create Recipe button
            createRecipeButton.setOnClickListener {
                openCreateRecipeActivity()
            }
        } else {
            Toast.makeText(this, "Invalid User ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchHomePageData(userId: Int) {
        val apiService = RetrofitInstance.getApi(applicationContext)
        val userRepository = UserRepository(apiService)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch all recipes from the backend
                val allRecipesResponse = userRepository.getAllRecipes()
                allRecipes = allRecipesResponse.body() ?: emptyList()

                // Filter top-rated recipes
                val topRatedRecipes = allRecipes.filter { it.averageRating >= 4 }

                // Fetch favourites
                val favourites = userRepository.getFavouritesMapped(userId)

                // Fetch news
                val news = userRepository.getNews().body() ?: emptyList()

                withContext(Dispatchers.Main) {
                    // Update welcome message
                    welcomeText.text = "Welcome, $currentUserFullName!"

                    favouritesRecyclerView.adapter = RecipeAdapter(favourites, baseUrl, R.layout.item_recipe_fav_card) { recipe ->
                        openRecipeDetails(recipe.recipeId)
                    }

                    topRatedRecipesRecyclerView.adapter = RecipeAdapter(topRatedRecipes, baseUrl, R.layout.item_recipe_card) { recipe ->
                        openRecipeDetails(recipe.recipeId)
                    }

                    newsRecyclerView.adapter = NewsAdapter(news, baseUrl) { article ->
                        openNewsDetails(article)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun filterRecipes(query: String) {
        val filteredRecipes = allRecipes.filter { it.title.contains(query, ignoreCase = true) }

        if (filteredRecipes.isNotEmpty()) {
            searchResultsRecyclerView.adapter = RecipeAdapter(filteredRecipes, baseUrl, R.layout.item_recipe_card) { recipe ->
                openRecipeDetails(recipe.recipeId)
            }
            searchResultsRecyclerView.visibility = RecyclerView.VISIBLE
        } else {
            searchResultsRecyclerView.adapter = null // Clear adapter if no results
            searchResultsRecyclerView.visibility = RecyclerView.GONE
        }
    }

    private fun openRecipeDetails(recipeId: Int) {
        val intent = Intent(this, RecipeDetailsActivity::class.java).apply {
            putExtra("RECIPE_ID", recipeId)
            putExtra("CURRENT_USER_ID", currentUserId)
        }
        startActivityForResult(intent, REQUEST_RECIPE_DETAILS)
    }

    private fun openCreateRecipeActivity() {
        val intent = Intent(this, CreateRecipeActivity::class.java).apply {
            putExtra("CURRENT_USER_ID", currentUserId)
        }
        startActivity(intent)
    }

    private fun openNewsDetails(article: NewsArticle) {
        val intent = Intent(this, NewsDetailsActivity::class.java).apply {
            putExtra("ARTICLE_ID", article.articleId)
            putExtra("ARTICLE_DESCRIPTION", article.articleDescription)
            putExtra("ARTICLE_CONTENT", article.articleContent)
            putExtra("CREATED_AT", article.createdAt)
            putExtra("IMAGE_URL", article.imageUrl)
        }
        startActivity(intent)
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun refreshFavourites() {
        val apiService = RetrofitInstance.getApi(applicationContext)
        val userRepository = UserRepository(apiService)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val favourites = userRepository.getFavouritesMapped(currentUserId)
                withContext(Dispatchers.Main) {
                    favouritesRecyclerView.adapter = RecipeAdapter(favourites, baseUrl, R.layout.item_recipe_fav_card) { recipe ->
                        openRecipeDetails(recipe.recipeId)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeActivity, "Error refreshing favourites: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_RECIPE_DETAILS && resultCode == RESULT_OK) {
            refreshFavourites() // Refresh the favourites list
        }
    }
}
