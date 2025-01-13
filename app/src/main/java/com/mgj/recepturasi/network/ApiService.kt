package com.mgj.recepturasi.network

import NewsArticle
import RatingRequest
import Recipe
import RecipeCreateRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // User Login
    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    // User Signup
    @POST("users/signup")
    suspend fun signUp(@Body userCreateRequest: UserCreateRequest): Response<UserResponse>

    // Fetch user details by ID
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): Response<UserResponse>

    // Fetch all recipes (including average rating)
    @GET("Recipes/all")
    suspend fun getAllRecipes(): Response<List<Recipe>>

    // Fetch a single recipe by ID (including average rating)
    @GET("Recipes/{id}")
    suspend fun getRecipeById(@Path("id") recipeId: Int): Response<Recipe>

    // Fetch recipes created by a specific user
    @GET("Recipes/user/{id}")
    suspend fun getUserRecipes(@Path("id") userId: Int): Response<List<Recipe>>

    // Fetch top-rated recipes
    @GET("Recipes/top-rated")
    suspend fun getTopRatedRecipes(): Response<List<Recipe>>

    // Fetch a recipe image by ID
    @GET("Recipes/{id}/image")
    suspend fun getRecipeImage(@Path("id") recipeId: Int): Response<ByteArray>

    // Create a new recipe
    @POST("Recipes/add")
    suspend fun createRecipe(@Body recipeCreateRequest: RecipeCreateRequest): Response<Recipe>

    // Rate a recipe
    @POST("Recipes/{id}/rate")
    suspend fun rateRecipe(@Path("id") recipeId: Int, @Body ratingRequest: RatingRequest): Response<Any>

    // Delete a recipe
    @DELETE("Recipes/{id}")
    suspend fun deleteRecipe(@Path("id") recipeId: Int): Response<Any>

    // Fetch all news articles
    @GET("News/all")
    suspend fun getAllNews(): Response<List<NewsArticle>>

    // Fetch a single news article by ID
    @GET("News/{id}")
    suspend fun getNewsById(@Path("id") newsId: Int): Response<NewsArticle>

    // Fetch the image of a news article by ID
    @GET("News/{id}/image")
    suspend fun getNewsImage(@Path("id") newsId: Int): Response<ByteArray>

    // Fetch all favourites for a user
    @GET("Favourites/{userId}")
    suspend fun getFavourites(@Path("userId") userId: Int): Response<List<Recipe>>

    // Delete a news article
    @DELETE("News/delete/{id}")
    suspend fun deleteNews(@Path("id") newsId: Int): Response<Any>
}
