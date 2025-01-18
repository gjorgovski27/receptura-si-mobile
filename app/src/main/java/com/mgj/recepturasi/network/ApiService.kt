package com.mgj.recepturasi.network

import Comment
import CommentRequest
import FavouriteRequest
import FavouriteResponse
import NewsArticle
import RatingRequest
import Recipe
import RecipeCreateRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // User Login
    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    // User Signup
    @POST("users/signup")
    suspend fun signUp(@Body userCreateRequest: UserCreateRequest): Response<Void>

    // Fetch user details by ID
    @GET("Users/{id}")
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

    @Multipart
    @POST("Recipes/add")
    suspend fun createRecipe(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("ingredients") ingredients: RequestBody,
        @Part("instructions") instructions: RequestBody,
        @Part("cookingTime") cookingTime: RequestBody,
        @Part("servingSize") servingSize: RequestBody,
        @Part("createdByUserId") createdByUserId: RequestBody,
        @Part imageFile: MultipartBody.Part? // Optional image file
    ): Response<Recipe>


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

    @GET("Favourites/{userId}")
    suspend fun getFavourites(@Path("userId") userId: Int): Response<List<FavouriteResponse>>

    @GET("Comments/{recipeId}")
    suspend fun getCommentsForRecipe(@Path("recipeId") recipeId: Int): Response<List<Comment>>

    @DELETE("Comments/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: Int): Response<Any>

    // Add a comment to a recipe
    @POST("Comments/add")
    suspend fun addComment(@Body commentRequest: CommentRequest): Response<Any>

    @POST("favourites/add")
    suspend fun addFavourite(@Body favouriteRequest: FavouriteRequest): Response<Any>

    @HTTP(method = "DELETE", path = "favourites/remove", hasBody = true)
    suspend fun removeFavourite(@Body favouriteRequest: FavouriteRequest): Response<Any>

}
