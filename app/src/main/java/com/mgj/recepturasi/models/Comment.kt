data class Comment(
    val commentId: Int,
    val userId: Int,
    val recipeId: Int,
    val content: String,
    val createdAt: String, // Ensure ISO 8601 format, e.g., "2025-01-13T10:23:24Z"
    val userName: String // Username of the commenter
)
