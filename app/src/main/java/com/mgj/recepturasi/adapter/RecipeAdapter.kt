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
    private val layoutResId: Int, // Layout resource ID for dynamic layouts
    private val onClick: (Recipe) -> Unit // Click handler
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView? = view.findViewById(R.id.recipeTitle) // Nullable for flexibility
        val image: ImageView? = view.findViewById(R.id.recipeImage)
        val details: TextView? = view.findViewById(R.id.recipeDetails) // Nullable for layouts without details
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        // Set title if the TextView exists
        holder.title?.text = recipe.title

        // Set details if the TextView exists
        holder.details?.text = "Cooking Time: ${recipe.cookingTime} mins | Servings: ${recipe.servingSize}"

        // Load image if the ImageView exists
        holder.image?.let {
            val sanitizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
            val imageUrl = "$sanitizedBaseUrl/recipes/${recipe.recipeId}/image"

            Glide.with(it.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(it)
        }

        // Set click listener
        holder.itemView.setOnClickListener {
            onClick(recipe)
        }
    }

    override fun getItemCount() = recipes.size
}
