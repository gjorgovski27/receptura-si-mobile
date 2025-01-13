package com.mgj.recepturasi.network

// Request for Login
data class LoginRequest(
    val email: String,
    val password: String
)

// Response for Login
data class LoginResponse(
    val token: String,
    val user: UserResponse
)

// Request for Signup
data class UserCreateRequest(
    val userName: String,
    val userFullName: String,
    val userEmail: String,
    val userPassword: String,
    val userPhone: String
)

// Response for User
data class UserResponse(
    val userId: Int,
    val userName: String,
    val userFullName: String,
    val userEmail: String
)
