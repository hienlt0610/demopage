package dev.hienlt.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> items;
    private LayoutInflater inflater;
    private OnItemClick onItemClick;
    private SharedPreferences preferences;

    public ItemAdapter(Context context) {
        items = new ArrayList<>();
        preferences = context.getSharedPreferences("myapp", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        if (item.isStatus()) {
            holder.tvName.setText(item.getName());
        } else {
            holder.tvName.setText(item.getName() +" (Đã khóa)");
        }
        holder.btnDetail.setEnabled(item.isStatus() && (position == 0 || item.isOpened()));
        int currentItemId = preferences.getInt("item_id", -1);
        if (item.isStatus()) {
            if (item.getId() == currentItemId) {
                holder.itemView.setBackgroundColor(Color.YELLOW);
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#f2f2f2"));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Item> listItem) {
        items.clear();
        items.addAll(listItem);
        notifyDataSetChanged();
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private Button btnDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            btnDetail = itemView.findViewById(R.id.btn_detail);
            btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClick != null) {
                        onItemClick.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnItemClick {
        void onItemClick(int position);
    }
}
