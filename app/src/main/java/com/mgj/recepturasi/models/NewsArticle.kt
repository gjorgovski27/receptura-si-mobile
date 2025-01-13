import java.util.Date

data class NewsArticle(
    val articleId: Int,
    val articleDescription: String, // Required
    val articleContent: String, // Required
    val createdAt: String, // ISO 8601 (e.g., "2025-01-10T12:34:56Z")
    val imageMimeType: String?, // Optional
    val imageUrl: String? // URL constructed on the frontend
)
