package com.example.venusawm;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IBHadapter extends RecyclerView.Adapter<IBHadapter.ViewHolder> {
    private List<com.example.venusawm.IBHlistItem> IBHlistItem;
    private Context context;

    public IBHadapter(List<com.example.venusawm.IBHlistItem> IBHlistItem, Context applicationContext) {
        this.IBHlistItem = IBHlistItem;
        this.context = applicationContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ibhcard, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String id, grnum, qty, date, ven;

        id = IBHlistItem.get(position).getId();
        grnum = IBHlistItem.get(position).getGrnum();
        qty = IBHlistItem.get(position).getQty();
        date = IBHlistItem.get(position).getDate();
        ven = IBHlistItem.get(position).getVendor();

        holder.gr.setText("GR number : " + grnum);
        holder.qty.setText("Number of Items : " + qty);
        holder.date.setText("Post Date : " + date);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, IBprocess.class);
                intent.putExtra("grn", grnum);
                intent.putExtra("date", date);
                intent.putExtra("ven", ven);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return IBHlistItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView gr, qty, date;
        LinearLayout card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.linearlayout1);
            gr = itemView.findViewById(R.id.gr);
            qty = itemView.findViewById(R.id.qty);
            date = itemView.findViewById(R.id.date);
        }
    }
}

