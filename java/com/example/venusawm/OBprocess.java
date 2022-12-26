package com.example.venusawm;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OBprocess extends AppCompatActivity implements View.OnClickListener {
    CardView scanBtn;
    TextView del, date, customername;
    String oburl = "https://testapi.innovasivtech.com/warehouse_api/outbound_body/read.php",
            obheadurl = "https://testapi.innovasivtech.com/warehouse_api/outbound_head/read.php",
            stockurl = "https://testapi.innovasivtech.com/warehouse_api/material_stock/read.php";
    String wholedata, mat, desc, qty, uom, serial_no;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<OblistItem> OblistItem;
    private List<ObhlistItem> ObhlistItem;
    SimpleDateFormat cdate, ctime;
    String post_date, post_time, lastid, newdelqty;
    String stock_id, new_stock_id, stock_mat_code, curr_stock, matcheck = "";
    int delqty, stock_update, count = 0, totalcount;

    String delid, delnum, delitems;
    String delhead, pdate, customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obprocess);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        scanBtn = findViewById(R.id.scanbutton);
        recyclerView = findViewById(R.id.oblist);
        del = findViewById(R.id.del);
        date = findViewById(R.id.pdate);
        customername = findViewById(R.id.customername);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        OblistItem = new ArrayList<>();
        ObhlistItem = new ArrayList<>();
        cdate = new SimpleDateFormat("dd-MM-yyyy");
        ctime = new SimpleDateFormat("HH:mm:ss");
        delhead = getIntent().getStringExtra("del");
        pdate = getIntent().getStringExtra("date");
        customer = getIntent().getStringExtra("cust");
        post_date = cdate.format(new Date());
        post_time = ctime.format(new Date());
        del.setText("Delivery number : " + delhead);
        date.setText("Post Date : " + pdate);
        customername.setText("Customer : " + customer);
        showOBentries();

        scanBtn.setOnClickListener(this);

    }

    private void obheadentries() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                obheadurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray array = jsonObject.getJSONArray("body");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                ObhlistItem item = new ObhlistItem(
                                        object.getString("id"),
                                        object.getString("delnum"),
                                        object.getString("customer"),
                                        object.getString("date"),
                                        object.getString("no_of_item")
                                );
                                if ((object.getString("delnum")).equals(delhead)) {
                                    ObhlistItem.add(item);
                                    delid = object.getString("id");
                                    delnum = object.getString("delnum");
                                    delitems = object.getString("no_of_item");

                                }

                            }


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

    private void showOBentries() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                oburl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray array = jsonObject.getJSONArray("body");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                OblistItem item = new OblistItem(
                                        object.getString("id"),
                                        object.getString("delnum"),
                                        object.getString("matcode"),
                                        object.getString("matdesc"),
                                        object.getString("quantity"),
                                        object.getString("uom"),
                                        object.getString("scan_date"),
                                        object.getString("scan_time"),
                                        object.getString("serial_num")
                                );
                                if ((object.getString("delnum")).equals(delhead)) {

                                    OblistItem.add(item);

                                }

                            }

                            adapter = new OBadapter(OblistItem, getApplicationContext());
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
    public void onClick(View v) {
        ObhlistItem.clear();
        obheadentries();
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a Material");
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                if (count == 0) {
                    Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), totalcount + " items scanned!", Toast.LENGTH_SHORT).show();
                    count = 0;
                    OblistItem.clear();
                    showOBentries();
                }

            } else {

                wholedata = intentResult.getContents();
                if (wholedata.equals("")) {
                    Toast.makeText(getBaseContext(), "No data found!", Toast.LENGTH_SHORT).show();
                    IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                    intentIntegrator.setPrompt("Scan a Material");
                    intentIntegrator.setOrientationLocked(false);
                    intentIntegrator.initiateScan();
                } else {
                    String[] splitString = wholedata.split("/");
                    mat = splitString[0];
                    desc = splitString[1];
                    qty = splitString[2];
                    uom = splitString[3];
                    serial_no = splitString[4];
                    count = count + 1;

                    getstockdata(stockurl);
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getstockdata(String stockurl) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, stockurl, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("body");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    stock_id = jsonObject1.getString("id");
                    stock_mat_code = jsonObject1.getString("mat_code");
                    curr_stock = jsonObject1.getString("curr_stock");


                    if (stock_mat_code.equals(mat)) {
                        matcheck = "OK";
                        if (Integer.parseInt(curr_stock) >= Integer.parseInt(qty)) {
                            new_stock_id = stock_id;
                            stock_update = Integer.parseInt(curr_stock) - Integer.parseInt(qty);

                            delqty = Integer.parseInt(delitems) + 1;
                            new updatestock().execute("https://testapi.innovasivtech.com/warehouse_api/material_stock/update.php");
                            new updatecount().execute("https://testapi.innovasivtech.com/warehouse_api/outbound_head/update.php");
                            new postnewmat().execute("https://testapi.innovasivtech.com/warehouse_api/outbound_body/create.php");
                            Toast.makeText(getApplicationContext(), "Material added!", Toast.LENGTH_LONG).show();
                            OblistItem.clear();
                            showOBentries();


                        } else {
                            Toast.makeText(getApplicationContext(), "Insufficient stock!", Toast.LENGTH_LONG).show();

                        }

                    }

                }
                if (!matcheck.equals("OK")) {
                    Toast.makeText(getApplicationContext(), "Material not found!", Toast.LENGTH_LONG).show();
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

    private class postnewmat extends AsyncTask<String, Void, String> {
        String server_response;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            java.net.URL url;
            HttpURLConnection urlConnection;
            try {


                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();


                JSONObject jsonObjectFinal = new JSONObject();
                jsonObjectFinal.put("delnum", delhead);
                jsonObjectFinal.put("matcode", mat);
                jsonObjectFinal.put("matdesc", desc);
                jsonObjectFinal.put("quantity", qty);
                jsonObjectFinal.put("scan_date", post_date);
                jsonObjectFinal.put("scan_time", post_time);
                jsonObjectFinal.put("serial_num", serial_no);
                jsonObjectFinal.put("uom", uom);


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
                jsonObjectFinal.put("id", new_stock_id);
                jsonObjectFinal.put("curr_stock", Integer.toString(stock_update));


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
                jsonObjectFinal.put("id", delid);
                jsonObjectFinal.put("no_of_item", Integer.toString(delqty));


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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OBprocess.this, com.example.venusawm.OBHead.class);

        startActivity(intent);

    }
}