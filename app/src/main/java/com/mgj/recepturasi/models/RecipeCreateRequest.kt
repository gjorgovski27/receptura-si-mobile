data class RecipeCreateRequest(
    val title: String,
    val description: String,
    val ingredients: String,
    val instructions: String,
    val cookingTime: Int,
    val servingSize: Int,
    val createdByUserId: Int, // The user ID of the recipe creator
    val imageFile: ByteArray? = null // Optional image file as a byte array
)
