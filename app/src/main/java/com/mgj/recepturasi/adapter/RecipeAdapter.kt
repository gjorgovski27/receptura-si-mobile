import Recipe
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mgj.recepturasi.R

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val baseUrl: String, // Base URL for API
    private val onClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.recipeTitle)
        val image: ImageView = view.findViewById(R.id.recipeImage)
        val details: TextView = view.findViewById(R.id.recipeDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.title.text = recipe.title
        holder.details.text = "Cooking Time: ${recipe.cookingTime} mins | Servings: ${recipe.servingSize}"

        // Dynamically construct the image URL
        val sanitizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
        val imageUrl = "$sanitizedBaseUrl/recipes/${recipe.recipeId}/image"

        // Load image using Glide
        Glide.with(holder.image.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.image)

        // Set click listener
        holder.itemView.setOnClickListener { onClick(recipe) }
    }

    override fun getItemCount() = recipes.size
}
