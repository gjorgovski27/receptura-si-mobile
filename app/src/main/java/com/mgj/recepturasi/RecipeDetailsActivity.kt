package com.mgj.recepturasi

import CommentRequest
import CommentsAdapter
import FavouriteRequest
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mgj.recepturasi.network.RetrofitInstance
import com.mgj.recepturasi.network.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeDetailsActivity : AppCompatActivity() {

    private lateinit var recipeTitle: TextView
    private lateinit var recipeDescription: TextView
    private lateinit var recipeIngredients: TextView
    private lateinit var recipeInstructions: TextView
    private lateinit var recipeCookingTime: TextView
    private lateinit var recipeServingSize: TextView
    private lateinit var recipeImage: ImageView
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentInput: EditText
    private lateinit var postCommentButton: Button
    private lateinit var deleteRecipeButton: Button
    private lateinit var favouriteButton: ImageView
    private var isFavourite: Boolean = false

    private val baseUrl = "https://recepturasi.azurewebsites.net/api"

    private var recipeId: Int = -1
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        // Initialize UI elements
        recipeTitle = findViewById(R.id.recipeTitle)
        recipeDescription = findViewById(R.id.recipeDescription)
        recipeIngredients = findViewById(R.id.recipeIngredients)
        recipeInstructions = findViewById(R.id.recipeInstructions)
        recipeCookingTime = findViewById(R.id.recipeCookingTime)
        recipeServingSize = findViewById(R.id.recipeServingSize)
        recipeImage = findViewById(R.id.recipeImage)
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView)
        commentInput = findViewById(R.id.commentInput)
        postCommentButton = findViewById(R.id.postCommentButton)
        deleteRecipeButton = findViewById(R.id.deleteRecipeButton)
        favouriteButton = findViewById(R.id.favouriteButton)

        favouriteButton.setOnClickListener {
            if (isFavourite) {
                removeFromFavourites()
            } else {
                addToFavourites()
            }
        }
        // Set up RecyclerView
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Get recipe ID and current user ID from intent
        recipeId = intent.getIntExtra("RECIPE_ID", -1)
        currentUserId = intent.getIntExtra("CURRENT_USER_ID", -1)

        if (recipeId != -1 && currentUserId != -1) {
            fetchRecipeDetails()
            fetchComments()
            fetchFavouriteState()


            // Set up Post Comment Button
            postCommentButton.setOnClickListener {
                val content = commentInput.text.toString().trim()
                if (content.isNotEmpty()) {
                    postComment(content)
                } else {
                    Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }

            // Set up Delete Recipe Button
            deleteRecipeButton.setOnClickListener {
                deleteRecipe()
            }

        } else {
            Toast.makeText(this, "Invalid Recipe or User ID", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchRecipeDetails() {
        val apiService = RetrofitInstance.getApi(applicationContext)
        val userRepository = UserRepository(apiService)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userRepository.getRecipeById(recipeId)
                val recipe = response.body()

                if (recipe != null) {
                    withContext(Dispatchers.Main) {
                        // Populate the UI
                        recipeTitle.text = recipe.title
                        recipeDescription.text = recipe.description
                        recipeIngredients.text = "Ingredients:\n${recipe.ingredients}"
                        recipeInstructions.text = "Instructions:\n${recipe.instructions}"
                        recipeCookingTime.text = "Cooking Time: ${recipe.cookingTime} mins"
                        recipeServingSize.text = "Servings: ${recipe.servingSize}"

                        val imageUrl = "$baseUrl/recipes/${recipe.recipeId}/image"
                        Glide.with(this@RecipeDetailsActivity)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_placeholder)
                            .into(recipeImage)

                        // Show delete button if current user created the recipe
                        if (recipe.createdByUserId == currentUserId) {
                            deleteRecipeButton.visibility = Button.VISIBLE
                        }
                        else {
                            deleteRecipeButton.visibility = Button.GONE
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RecipeDetailsActivity, "Recipe not found.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RecipeDetailsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun fetchComments() {
        val apiService = RetrofitInstance.getApi(applicationContext)
        val userRepository = UserRepository(apiService)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userRepository.getCommentsForRecipe(recipeId)
                val comments = response.body()

                if (comments != null && comments.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        // Set the adapter for comments RecyclerView
                        commentsRecyclerView.adapter = CommentsAdapter(comments, currentUserId) { commentId ->
                            deleteComment(commentId)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RecipeDetailsActivity, "No comments available.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RecipeDetailsActivity, "Error fetching comments: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun postComment(content: String) {
        val apiService = RetrofitInstance.getApi(applicationContext)
        val userRepository = UserRepository(apiService)

        // Create the CommentRequest object
        val commentRequest = CommentRequest(
            recipeId = recipeId,
            userId = currentUserId,
            content = content
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userRepository.addComment(commentRequest)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RecipeDetailsActivity, "Comment posted successfully", Toast.LENGTH_SHORT).show()
                        commentInput.text.clear() // Clear the input field
                        fetchComments() // Refresh the comments list
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RecipeDetailsActivity, "Failed to post comment", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RecipeDetailsActivity, "Error posting comment: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteComment(commentId: Int) {
        val apiService = RetrofitInstance.getApi(applicationContext)
        val userRepository = UserRepository(apiService)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userRepository.deleteComment(commentId)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RecipeDetailsActivity, "Comment deleted successfully.", Toast.LENGTH_SHORT).show()
                        fetchComments() // Refresh comments list
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RecipeDetailsActivity, "Failed to delete comment.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RecipeDetailsActivity, "Error deleting comment: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteRecipe() {
        val apiService = RetrofitInstance.getApi(applicationContext)
        val userRepository = UserRepository(apiService)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userRepository.deleteRecipe(recipeId)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RecipeDetailsActivity, "Recipe deleted successfully.", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity after deletion
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RecipeDetailsActivity, "Failed to delete recipe.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RecipeDetailsActivity, "Error deleting recipe: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchFavouriteState() {
        val apiService = RetrofitInstance.getApi(applicationContext)
        val userRepository = UserRepository(apiService)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val favourites = userRepository.getFavourites(currentUserId).body() ?: emptyList()
                isFavourite = favourites.any { it.recipeId == recipeId }

                withContext(Dispatchers.Main) {
                    updateFavouriteButtonState()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RecipeDetailsActivity, "Error fetching favourites: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateFavouriteButtonState() {
        if (isFavourite) {
            favouriteButton.setImageResource(R.drawable.ic_heart_filled)
            favouriteButton.setColorFilter(getColor(R.color.teal_700))
        } else {
            favouriteButton.setImageResource(R.drawable.ic_heart_outline)
            favouriteButton.setColorFilter(getColor(R.color.gray))
        }
    }

    private fun addToFavourites() {
        val apiService = RetrofitInstance.getApi(applicationContext)
        val userRepository = UserRepository(apiService)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userRepository.addFavourite(FavouriteRequest(recipeId, currentUserId))
                if (response.isSuccessful) {
                    isFavourite = true
                    withContext(Dispatchers.Main) {
                        updateFavouriteButtonState()
                        Toast.makeText(this@RecipeDetailsActivity, "Added to favourites!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK) // Indicate success to calling activity
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RecipeDetailsActivity, "Failed to add to favourites: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun removeFromFavourites() {
        val apiService = RetrofitInstance.getApi(applicationContext)
        val userRepository = UserRepository(apiService)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userRepository.removeFavourite(FavouriteRequest(recipeId, currentUserId))
                if (response.isSuccessful) {
                    isFavourite = false
                    withContext(Dispatchers.Main) {
                        updateFavouriteButtonState()
                        Toast.makeText(this@RecipeDetailsActivity, "Removed from favourites!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK) // Indicate success to calling activity
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RecipeDetailsActivity, "Failed to remove from favourites: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
