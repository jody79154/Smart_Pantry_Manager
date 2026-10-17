package com.example.smartpantry.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.ExpiryHelper;
import com.example.smartpantry.util.QuantityFormatter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface Listener {
        void onEdit(PantryItem item);

        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;
    private boolean expiryAlertsEnabled = true;
    private int warningDays = ExpiryHelper.DEFAULT_WARNING_DAYS;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setExpiryAlerts(boolean enabled, int days) {
        this.expiryAlertsEnabled = enabled;
        this.warningDays = days;
    }

    public void setItems(List<PantryItem> newItems) {
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
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText(QuantityFormatter.format(item.getQuantity()) + " " + item.getUnit());

        bindExpiry(holder, item);

        holder.edit.setContentDescription(holder.itemView.getContext()
                .getString(R.string.cd_edit_item, item.getName()));
        holder.delete.setContentDescription(holder.itemView.getContext()
                .getString(R.string.cd_delete_item, item.getName()));

        View.OnClickListener editClick = v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onEdit(items.get(adapterPosition));
            }
        };
        holder.itemView.setOnClickListener(editClick);
        holder.edit.setOnClickListener(editClick);
        holder.delete.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onDelete(items.get(adapterPosition));
            }
        });
    }

    private void bindExpiry(PantryViewHolder holder, PantryItem item) {
        String date = item.getExpiryDate();
        if (date == null) {
            holder.expiry.setVisibility(View.GONE);
            return;
        }

        Context context = holder.itemView.getContext();
        String text = context.getString(R.string.expires_on, date);
        int color = R.color.text_secondary;
        boolean flagged = false;

        if (expiryAlertsEnabled) {
            LocalDate today = LocalDate.now();
            Integer days = ExpiryHelper.daysUntil(date, today);
            ExpiryHelper.Status status = ExpiryHelper.statusOf(date, today, warningDays);
            if (status == ExpiryHelper.Status.EXPIRED && days != null) {
                int ago = -days;
                text = context.getResources().getQuantityString(R.plurals.expired_days_ago, ago, ago, date);
                color = R.color.error;
                flagged = true;
            } else if (status == ExpiryHelper.Status.EXPIRING_SOON && days != null) {
                text = days == 0
                        ? context.getString(R.string.expires_today, date)
                        : context.getResources().getQuantityString(R.plurals.expires_in_days, days, days, date);
                color = R.color.warning;
                flagged = true;
            }
        }

        holder.expiry.setText(text);
        holder.expiry.setTextColor(ContextCompat.getColor(context, color));
        holder.expiry.setTypeface(null, flagged ? Typeface.BOLD : Typeface.NORMAL);
        holder.expiry.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView quantity;
        final TextView expiry;
        final ImageButton edit;
        final ImageButton delete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvItemName);
            quantity = itemView.findViewById(R.id.tvItemQuantity);
            expiry = itemView.findViewById(R.id.tvItemExpiry);
            edit = itemView.findViewById(R.id.btnEdit);
            delete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
