package com.jody.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jody.smartpantry.R;
import com.jody.smartpantry.model.RecipeIngredient;
import java.util.ArrayList;
import java.util.List;

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.IngredientViewHolder> {

    private final List<RecipeIngredient> ingredients = new ArrayList<>();

    public void submitList(List<RecipeIngredient> newIngredients) {
        ingredients.clear();
        ingredients.addAll(newIngredients);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        holder.bind(ingredients.get(position));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {

        private final TextView textName;
        private final TextView textAmount;

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_ingredient_name);
            textAmount = itemView.findViewById(R.id.text_ingredient_amount);
        }

        void bind(RecipeIngredient ingredient) {
            textName.setText(ingredient.getIngredientName());
            textAmount.setText(formatQuantity(ingredient.getQuantity()) + " " + ingredient.getUnit());
        }

        private String formatQuantity(double quantity) {
            if (quantity == Math.floor(quantity)) {
                return String.valueOf((long) quantity);
            }
            return String.valueOf(quantity);
        }
    }
}
