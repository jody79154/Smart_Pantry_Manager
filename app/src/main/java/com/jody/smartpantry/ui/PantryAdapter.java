package com.jody.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jody.smartpantry.R;
import com.jody.smartpantry.data.SettingsRepository;
import com.jody.smartpantry.model.PantryItem;
import java.util.ArrayList;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface Listener {
        void onEditClicked(PantryItem item);
        void onDeleteClicked(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {

        private final TextView textName;
        private final TextView textQuantity;
        private final TextView textExpiry;
        private final ImageButton buttonEdit;
        private final ImageButton buttonDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_item_name);
            textQuantity = itemView.findViewById(R.id.text_item_quantity);
            textExpiry = itemView.findViewById(R.id.text_item_expiry);
            buttonEdit = itemView.findViewById(R.id.button_edit_item);
            buttonDelete = itemView.findViewById(R.id.button_delete_item);
        }

        void bind(PantryItem item, Listener listener) {
            textName.setText(item.getName());
            textQuantity.setText(formatQuantity(item) + " " + item.getUnit());

            boolean alertsEnabled = new SettingsRepository(itemView.getContext()).isExpiryAlertsEnabled();
            if (item.hasExpiryDate() && alertsEnabled) {
                textExpiry.setVisibility(View.VISIBLE);
                textExpiry.setText(itemView.getContext().getString(R.string.label_expires_on, item.getExpiryDate()));
            } else {
                textExpiry.setVisibility(View.GONE);
            }

            buttonEdit.setOnClickListener(v -> listener.onEditClicked(item));
            buttonDelete.setOnClickListener(v -> listener.onDeleteClicked(item));
        }

        private String formatQuantity(PantryItem item) {
            double quantity = item.getQuantity();
            if (quantity == Math.floor(quantity)) {
                return String.valueOf((long) quantity);
            }
            return String.valueOf(quantity);
        }
    }
}
