data class Recipe(
    val recipeId: Int,
    val title: String,
    val description: String?,
    val ingredients: String, // Required
    val instructions: String, // Required
    val cookingTime: Int, // Required in backend; assume non-null
    val servingSize: Int, // Required in backend; assume non-null
    val averageRating: Double = 0.0, // Default to 0.0 if no ratings
    val createdByUserId: Int,
    val imageMimeType: String?, // Optional in backend
    val imageUrl: String? // URL constructed on the frontend
)
