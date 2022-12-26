package com.example.venusawm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class testAdapter extends RecyclerView.Adapter<testAdapter.ViewHolder> {
    private List<testlistItem> testlistItem;
    private Context context;
//    Activity mActivity;
//    ArrayList<testlistItem> models;
//
//    public testAdapter(ArrayList<testlistItem> models,Activity mActivity){
//        this.mActivity=mActivity;
//        this.models=models;
//
//    }
    public testAdapter(List<testlistItem> testlistItem, Context applicationContext) {
        this.testlistItem = testlistItem;
        this.context = applicationContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.testcard, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String id, delnum, qty, date, cust;

        id = testlistItem.get(position).getId();
        delnum = testlistItem.get(position).getdelnum();
        qty = testlistItem.get(position).getQty();
        date = testlistItem.get(position).getDate();
        cust = testlistItem.get(position).getcustomer();

        holder.del.setText("Delivery number : " + delnum);
        holder.qty.setText("Number of Items : " + qty);
        holder.date.setText("Post Date : " + date);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, newOBhead.class);
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
        return testlistItem.size();
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
