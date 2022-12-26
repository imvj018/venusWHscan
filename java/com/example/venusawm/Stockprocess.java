package com.example.venusawm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Stockprocess extends AppCompatActivity {
    String sturl = "https://testapi.innovasivtech.com/warehouse_api/material_stock/read.php";
    String id, matcode, matdesc, stock;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<stocklistItem> stocklistItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stockprocess);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        recyclerView = findViewById(R.id.mslist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        stocklistItem = new ArrayList<>();

        showstockdata();
    }

    private void showstockdata() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                sturl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray array = jsonObject.getJSONArray("body");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                stocklistItem item = new stocklistItem(
                                        object.getString("id"),
                                        object.getString("mat_code"),
                                        object.getString("mat_desc"),
                                        object.getString("curr_stock"),
                                        object.getString("uom")
                                );
                                if (!(object.getString("mat_code").equals(""))) {
                                    stocklistItem.add(item);


                                }

                            }
                            adapter = new stockadapter(stocklistItem, getApplicationContext());
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Stockprocess.this, Dashboard.class);
        startActivity(intent);

    }
}