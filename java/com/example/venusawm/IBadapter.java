package com.example.venusawm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class IBadapter extends RecyclerView.Adapter<IBadapter.ViewHolder> {
    String GRmatid, GRmatno, GRnumber;
    String checkid, checkGR, checkqty;
    String stockid, stockmat, stockqty, newstock;
    private List<com.example.venusawm.IBlistItem> IBlistItem;
    private Context context;

    public IBadapter(List<com.example.venusawm.IBlistItem> IBlistItem, Context applicationContext) {
        this.IBlistItem = IBlistItem;
        this.context = applicationContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ibcard, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String id, grnum, code, desc, qty, uom, date, time, vendor;

        id = IBlistItem.get(position).getId();
        grnum = IBlistItem.get(position).getGrnum();
        code = IBlistItem.get(position).getCode();
        desc = IBlistItem.get(position).getDesc();
        qty = IBlistItem.get(position).getQty();
        uom = IBlistItem.get(position).getUom();
        date = IBlistItem.get(position).getDate();
        time = IBlistItem.get(position).getTime();
        vendor = IBlistItem.get(position).getVendor();

        holder.mat.setText("Material : " + desc);
        holder.qty.setText("Quantity : " + qty + " " + uom);
        holder.date.setText("Added on " + date + " " + time);
        holder.ven.setText("Serial No : " + vendor);

        getstockdata("https://testapi.innovasivtech.com/warehouse_api/material_stock/read.php");
        getibdata("https://testapi.innovasivtech.com/warehouse_api/inbound_head/read.php");

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                GRmatid = id;
                GRnumber = grnum;
                GRmatno = code;


                new delmat().execute("https://testapi.innovasivtech.com/warehouse_api/inbound_body/delete.php");
                new updatecount().execute("https://testapi.innovasivtech.com/warehouse_api/inbound_head/update.php");
                new updatestock().execute("https://testapi.innovasivtech.com/warehouse_api/material_stock/update.php");
                getstockdata("https://testapi.innovasivtech.com/warehouse_api/material_stock/read.php");
                getibdata("https://testapi.innovasivtech.com/warehouse_api/inbound_head/read.php");
//                OblistItem.clear();
//                notifyDataSetChanged();


            }
        });
    }

    @Override
    public int getItemCount() {
        return IBlistItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mat, qty, date, ven;
        ImageView delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mat = itemView.findViewById(R.id.mat);
            qty = itemView.findViewById(R.id.qty);
            date = itemView.findViewById(R.id.date);
            ven = itemView.findViewById(R.id.ven);
            delete = itemView.findViewById(R.id.delbutton);
        }
    }


    private void getstockdata(String s) {
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, s, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("body");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    String id = jsonObject1.getString("id");
                    String mat_code = jsonObject1.getString("mat_code");
                    String curr_stock = jsonObject1.getString("curr_stock");
                    if (mat_code.equals(GRmatno)) {
                        stockid = id;
                        stockqty = curr_stock;
                        newstock = Integer.toString(Integer.parseInt(curr_stock) - 1);
                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private void getibdata(String ob) {
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ob, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("body");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    String id = jsonObject1.getString("id");
                    String GRnum = jsonObject1.getString("GRnum");
                    String no_of_item = jsonObject1.getString("no_of_item");
                    if (GRnum.equals(GRnumber)) {
                        checkid = id;
                        checkqty = no_of_item;
                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private class delmat extends AsyncTask<String, Void, String> {
        String server_response;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection;
            try {


                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();


                JSONObject jsonObjectFinal = new JSONObject();
                jsonObjectFinal.put("id", GRmatid);


                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(jsonObjectFinal.toString());

                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                    Log.v("CatalogClient", server_response);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Response", "" + server_response);
        }
    }

    private class updatestock extends AsyncTask<String, Void, String> {
        String server_response;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            java.net.URL url;
            HttpURLConnection urlConnection;
            try {


                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();


                JSONObject jsonObjectFinal = new JSONObject();
                jsonObjectFinal.put("id", stockid);
                jsonObjectFinal.put("curr_stock", newstock);


                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(jsonObjectFinal.toString());

                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                    Log.v("CatalogClient", server_response);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Response", "" + server_response);
        }
    }

    private class updatecount extends AsyncTask<String, Void, String> {
        String server_response;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            java.net.URL url;
            HttpURLConnection urlConnection;
            try {


                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();


                JSONObject jsonObjectFinal = new JSONObject();
                jsonObjectFinal.put("id", checkid);
                jsonObjectFinal.put("no_of_item", Integer.toString(IBlistItem.size() - 1));


                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(jsonObjectFinal.toString());

                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                    Log.v("CatalogClient", server_response);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Response", "" + server_response);
        }
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}
