data class LoginResponse(
    val token: String, // JWT Token for authentication
    val user: UserResponse // Nested user object
)
