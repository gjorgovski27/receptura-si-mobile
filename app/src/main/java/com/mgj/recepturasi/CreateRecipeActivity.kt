package com.mgj.recepturasi

import RecipeCreateRequest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mgj.recepturasi.network.RetrofitInstance
import com.mgj.recepturasi.network.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

class CreateRecipeActivity : AppCompatActivity() {

    private lateinit var recipeTitle: EditText
    private lateinit var recipeDescription: EditText
    private lateinit var recipeIngredients: EditText
    private lateinit var recipeInstructions: EditText
    private lateinit var recipeCookingTime: EditText
    private lateinit var recipeServingSize: EditText
    private lateinit var uploadImageButton: Button
    private lateinit var createRecipeButton: Button

    private var imageBytes: ByteArray? = null
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)

        // Initialize UI elements
        recipeTitle = findViewById(R.id.recipeTitle)
        recipeDescription = findViewById(R.id.recipeDescription)
        recipeIngredients = findViewById(R.id.recipeIngredients)
        recipeInstructions = findViewById(R.id.recipeInstructions)
        recipeCookingTime = findViewById(R.id.recipeCookingTime)
        recipeServingSize = findViewById(R.id.recipeServingSize)
        uploadImageButton = findViewById(R.id.uploadImageButton)
        createRecipeButton = findViewById(R.id.createRecipeButton)

        currentUserId = intent.getIntExtra("CURRENT_USER_ID", -1)

        // Set up image upload
        uploadImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, 1001)
        }

        // Set up form submission
        createRecipeButton.setOnClickListener {
            submitRecipe()
        }
    }

    private fun submitRecipe() {
        val title = recipeTitle.text.toString().trim()
        val description = recipeDescription.text.toString().trim()
        val ingredients = recipeIngredients.text.toString().trim()
        val instructions = recipeInstructions.text.toString().trim()
        val cookingTime = recipeCookingTime.text.toString().toIntOrNull()
        val servingSize = recipeServingSize.text.toString().toIntOrNull()

        if (title.isEmpty() || ingredients.isEmpty() || instructions.isEmpty() || cookingTime == null || servingSize == null) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val recipeRequest = RecipeCreateRequest(
            title = title,
            description = description,
            ingredients = ingredients,
            instructions = instructions,
            cookingTime = cookingTime,
            servingSize = servingSize,
            createdByUserId = currentUserId,
            imageFile = imageBytes
        )

        val userRepository = UserRepository(RetrofitInstance.getApi(applicationContext))

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userRepository.createRecipe(recipeRequest)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CreateRecipeActivity, "Recipe created successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CreateRecipeActivity, "Failed to create recipe.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateRecipeActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                val inputStream: InputStream? = contentResolver.openInputStream(it)
                imageBytes = inputStream?.readBytes()
                Toast.makeText(this, "Image selected successfully.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
