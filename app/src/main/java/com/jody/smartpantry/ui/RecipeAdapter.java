package com.jody.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jody.smartpantry.R;
import com.jody.smartpantry.model.Recipe;
import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface Listener {
        void onRecipeClicked(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final Listener listener;

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(recipes.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {

        private final TextView textName;
        private final TextView textIngredientCount;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_recipe_card_name);
            textIngredientCount = itemView.findViewById(R.id.text_recipe_card_ingredient_count);
        }

        void bind(Recipe recipe, Listener listener) {
            textName.setText(recipe.getName());
            int count = recipe.getIngredients().size();
            String label = itemView.getResources().getQuantityString(
                    R.plurals.ingredient_count, count, count);
            textIngredientCount.setText(label);
            itemView.setOnClickListener(v -> listener.onRecipeClicked(recipe));
        }
    }
}
