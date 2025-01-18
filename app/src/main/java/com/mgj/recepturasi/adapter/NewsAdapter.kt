import NewsArticle
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mgj.recepturasi.NewsDetailsActivity
import com.mgj.recepturasi.R

class NewsAdapter(
    private val newsArticles: List<NewsArticle>,
    private val baseUrl: String, // Base URL for API
    private val onClick: (NewsArticle) -> Unit // Add click handler
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.newsTitle)
        val description: TextView = view.findViewById(R.id.newsDescription)
        val image: ImageView = view.findViewById(R.id.newsImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news_card, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = newsArticles[position]
        holder.title.text = article.articleDescription
        holder.description.text = article.articleContent.take(100) + "..." // Display the first 100 characters

        // Ensure baseUrl is sanitized
        val sanitizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
        val imageUrl = article.imageUrl ?: "$sanitizedBaseUrl/news/${article.articleId}/image"

        // Load image using Glide
        Glide.with(holder.image.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.image)

        // Set click listener to navigate to NewsDetailsActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, NewsDetailsActivity::class.java).apply {
                putExtra("ARTICLE_ID", article.articleId)
                putExtra("ARTICLE_DESCRIPTION", article.articleDescription)
                putExtra("ARTICLE_CONTENT", article.articleContent)
                putExtra("CREATED_AT", article.createdAt)
                putExtra("IMAGE_URL", imageUrl)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = newsArticles.size
}
