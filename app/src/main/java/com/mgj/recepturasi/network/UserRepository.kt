package com.mgj.recepturasi.network

import NewsArticle
import Recipe
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {

    // User login
    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return apiService.login(LoginRequest(email, password))
    }

    // User sign-up
    suspend fun signUp(user: UserCreateRequest): Response<UserResponse> {
        return apiService.signUp(user)
    }

    // Fetch user data
    suspend fun getUserData(userId: Int): Response<UserResponse> {
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

    // Fetch all favourite recipes for a user
    suspend fun getFavourites(userId: Int): Response<List<Recipe>> {
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
}
