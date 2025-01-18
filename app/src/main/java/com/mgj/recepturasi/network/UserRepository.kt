package com.mgj.recepturasi.network

import Comment
import CommentRequest
import FavouriteRequest
import FavouriteResponse
import NewsArticle
import Recipe
import RecipeCreateRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {

    // User login
    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return apiService.login(LoginRequest(email, password))
    }

    // User sign-up
    suspend fun signUp(user: UserCreateRequest): Response<Void> {
        return apiService.signUp(user)
    }

    // Fetch user data
    suspend fun getUser(userId: Int): Response<UserResponse> {
        return apiService.getUser(userId)
    }

    // Fetch all recipes created by the user
    suspend fun getMyRecipes(userId: Int): Response<List<Recipe>> {
        return apiService.getUserRecipes(userId)
    }

    // Fetch top-rated recipes
    suspend fun getTopRatedRecipes(): Response<List<Recipe>> {
        return apiService.getTopRatedRecipes()
    }

    // Fetch all news articles
    suspend fun getNews(): Response<List<NewsArticle>> {
        return apiService.getAllNews()
    }

    suspend fun getFavourites(userId: Int): Response<List<FavouriteResponse>> {
        return apiService.getFavourites(userId)
    }

    // Optional helper for fetching news image URLs
    fun getNewsImageUrl(articleId: Int, baseUrl: String): String {
        val sanitizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
        return "$sanitizedBaseUrl/news/$articleId/image"
    }

    // Optional helper for fetching recipe image URLs
    fun getRecipeImageUrl(recipeId: Int, baseUrl: String): String {
        val sanitizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
        return "$sanitizedBaseUrl/recipes/$recipeId/image"
    }

    // Fetch recipe details by ID
    suspend fun getRecipeById(recipeId: Int): Response<Recipe> {
        return apiService.getRecipeById(recipeId)
    }

    suspend fun getAllRecipes(): Response<List<Recipe>> {
        return apiService.getAllRecipes()
    }

    // Mapped method for fetching favourites as Recipe objects
    suspend fun getFavouritesMapped(userId: Int): List<Recipe> {
        val response = apiService.getFavourites(userId)
        return response.body()?.map { favourite ->
            Recipe(
                recipeId = favourite.recipeId,
                title = favourite.recipeTitle, // Map recipeTitle to title
                description = "", // Optional default value
                ingredients = "",
                instructions = "",
                cookingTime = 0,
                servingSize = 0,
                averageRating = 0.0,
                createdByUserId = -1, // Default if not needed
                imageMimeType = null,
                imageUrl = "${RetrofitInstance.getBaseUrl()}/recipes/${favourite.recipeId}/image"
            )
        } ?: emptyList()
    }

    suspend fun getCommentsForRecipe(recipeId: Int): Response<List<Comment>> {
        return apiService.getCommentsForRecipe(recipeId)
    }

    // Delete a comment by ID
    suspend fun deleteComment(commentId: Int): Response<Any> {
        return apiService.deleteComment(commentId)
    }

    // Add a comment to a recipe
    suspend fun addComment(commentRequest: CommentRequest): Response<Any> {
        return apiService.addComment(commentRequest)
    }

    // Delete a recipe
    suspend fun deleteRecipe(recipeId: Int): Response<Any> {
        return apiService.deleteRecipe(recipeId)
    }

    // Add a recipe to favourites
    suspend fun addFavourite(favouriteRequest: FavouriteRequest): Response<Any> {
        return apiService.addFavourite(favouriteRequest)
    }

    // Remove a recipe from favourites
    suspend fun removeFavourite(favouriteRequest: FavouriteRequest): Response<Any> {
        return apiService.removeFavourite(favouriteRequest)
    }

    // Create a new recipe
    suspend fun createRecipe(recipeCreateRequest: RecipeCreateRequest): Response<Recipe> {
        val requestBodyMap = mapOf(
            "title" to recipeCreateRequest.title.toRequestBody("text/plain".toMediaType()),
            "description" to recipeCreateRequest.description.toRequestBody("text/plain".toMediaType()),
            "ingredients" to recipeCreateRequest.ingredients.toRequestBody("text/plain".toMediaType()),
            "instructions" to recipeCreateRequest.instructions.toRequestBody("text/plain".toMediaType()),
            "cookingTime" to recipeCreateRequest.cookingTime.toString().toRequestBody("text/plain".toMediaType()),
            "servingSize" to recipeCreateRequest.servingSize.toString().toRequestBody("text/plain".toMediaType()),
            "createdByUserId" to recipeCreateRequest.createdByUserId.toString().toRequestBody("text/plain".toMediaType())
        )

        val imagePart = recipeCreateRequest.imageFile?.let {
            val requestBody = it.toRequestBody("image/*".toMediaType())
            MultipartBody.Part.createFormData("imageFile", "recipe_image.jpg", requestBody)
        }

        return apiService.createRecipe(
            title = requestBodyMap["title"]!!,
            description = requestBodyMap["description"]!!,
            ingredients = requestBodyMap["ingredients"]!!,
            instructions = requestBodyMap["instructions"]!!,
            cookingTime = requestBodyMap["cookingTime"]!!,
            servingSize = requestBodyMap["servingSize"]!!,
            createdByUserId = requestBodyMap["createdByUserId"]!!,
            imageFile = imagePart
        )
    }

}

