package com.example.venusawm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class stockadapter extends RecyclerView.Adapter<stockadapter.ViewHolder> {
    private List<stocklistItem> stocklistItem;
    private Context context;

    public stockadapter(List<stocklistItem> stocklistItem, Context applicationContext) {
        this.stocklistItem = stocklistItem;
        this.context = applicationContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stockcard, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String id, code, desc, qty, uom;
        id = stocklistItem.get(position).getId();
        code = stocklistItem.get(position).getCode();
        desc = stocklistItem.get(position).getDesc();
        qty = stocklistItem.get(position).getQty();
        uom = stocklistItem.get(position).getUom();


        holder.mat.setText("Material : " + code + " - " + desc);
        holder.stock.setText("Available Stock : " + qty + " " + uom);
    }

    @Override
    public int getItemCount() {

        return stocklistItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mat, stock;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mat = itemView.findViewById(R.id.mat);
            stock = itemView.findViewById(R.id.stock);


        }
    }
}

