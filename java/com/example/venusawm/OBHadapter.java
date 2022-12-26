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

public class OBHadapter extends RecyclerView.Adapter<OBHadapter.ViewHolder>{
    private List<ObhlistItem> ObhlistItem;
    private Context context;

    public OBHadapter(List<ObhlistItem> ObhlistItem, Context applicationContext) {
        this.ObhlistItem = ObhlistItem;
        this.context = applicationContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.obhcard, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String id, delnum, qty, date, cust;

        id = ObhlistItem.get(position).getId();
        delnum = ObhlistItem.get(position).getdelnum();
        qty = ObhlistItem.get(position).getQty();
        date = ObhlistItem.get(position).getDate();
        cust = ObhlistItem.get(position).getcustomer();

        holder.del.setText("Delivery number : " + delnum);
        holder.qty.setText("Number of Items : " + qty);
        holder.date.setText("Post Date : " + date);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, OBprocess.class);
                intent.putExtra("del", delnum);
                intent.putExtra("date", date);
                intent.putExtra("cust", cust);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ObhlistItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView del, qty, date;
        LinearLayout card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.linearlayout2);
            del = itemView.findViewById(R.id.del);
            qty = itemView.findViewById(R.id.qty);
            date = itemView.findViewById(R.id.date);
        }
    }

}
