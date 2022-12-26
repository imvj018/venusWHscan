package com.example.venusawm;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class newOBhead extends AppCompatActivity  {
    String iburl = "https://testapi.innovasivtech.com/warehouse_api/outbound_head/read.php",
            stockurl = "https://testapi.innovasivtech.com/warehouse_api/material_stock/read.php",
            customerurl = "https://testapi.innovasivtech.com/warehouse_api/customer_master/read.php";

    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    LinearLayoutManager HorizontalLayout;
    private RecyclerView recyclerView, HrecyclerView;
    private RecyclerView.Adapter adapter, Hadapter;
    private List<ObhlistItem> ObhlistItem;
    private List<testlistItem> testlistItem;
    private List<String> vcodelist;
    SimpleDateFormat cdate, ctime;
    String wholedata, mat, desc, qty, uom, ven;
    String post_date, post_time, lastid, newdelnum, customer;
    String stock_id, new_stock_id, stock_mat_code, curr_stock, matcheck = "";
    int delnum, stock_update, count = 0, totalcount;
    String xcustomer, xdelhead, xpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_obhead);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        cdate = new SimpleDateFormat("dd-MM-yyyy");
        ctime = new SimpleDateFormat("HH:mm:ss");

        post_date = cdate.format(new Date());
        post_time = ctime.format(new Date());
        recyclerView = findViewById(R.id.obheadlist);

        xdelhead = getIntent().getStringExtra("del");
        xpdate = getIntent().getStringExtra("date");
        xcustomer = getIntent().getStringExtra("cust");


        System.out.println(xdelhead + xpdate + xcustomer);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ObhlistItem = new ArrayList<>();
        vcodelist = new ArrayList<>();
        HrecyclerView = findViewById(R.id.hrecyclerview);
        HrecyclerView.setHasFixedSize(true);
        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());

        // Set LayoutManager on Recycler View
        HrecyclerView.setLayoutManager(RecyclerViewLayoutManager);
        testlistItem = new ArrayList<>();
        showdataentries();

        HorizontalLayout
                = new LinearLayoutManager(
                newOBhead.this,
                LinearLayoutManager.HORIZONTAL,
                false);
        HrecyclerView.setLayoutManager(HorizontalLayout);
        loadcustomerlist(customerurl);

        showOBHentries();


    }
    private void showdataentries() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                iburl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray array = jsonObject.getJSONArray("body");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                testlistItem item = new testlistItem(
                                        object.getString("id"),
                                        object.getString("delnum"),
                                        object.getString("customer"),
                                        object.getString("date"),
                                        object.getString("no_of_item")
                                );
                                if (!(object.getString("delnum")).equals("")) {
                                    testlistItem.add(item);
                                }

                            }


                            Hadapter = new testAdapter(testlistItem, getApplicationContext());
                            HrecyclerView.setAdapter(Hadapter);

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
    private void loadcustomerlist(String customerurl) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, customerurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("body");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String vid = jsonObject1.getString("id");
                        String vcode = jsonObject1.getString("customer_code");
                        String vname = jsonObject1.getString("address");

                        if (vname.equals("")) {
                            vcodelist.add(vcode);
                        }
//                        if (vname.equals("Select Customer")) {
//                            vcodelist.add(vcode);
//                        }
//                        else {
//                            vcodelist.add(vcode + " - " + vname);
//
//                        }


                    }
//                    System.out.println(vcodelist);
//                    testclass();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private void testclass() {
        Set<String> distinct = new HashSet<>(vcodelist);
        for (String s: distinct) {
            System.out.println(s + ": " + Collections.frequency(vcodelist, s));
        }
    }

    private void showOBHentries() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                iburl,
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
                                if ((object.getString("delnum")).equals(xdelhead)) {
                                    ObhlistItem.add(item);
                                }

                            }



                            JSONObject object = array.getJSONObject(0);
                            ObhlistItem item = new ObhlistItem(
                                    object.getString("id"),
                                    object.getString("delnum"),
                                    object.getString("customer"),
                                    object.getString("date"),
                                    object.getString("no_of_item")
                            );
                            if (!(object.getString("id")).equals("")) {
                                lastid = object.getString("id");


                            }
                            adapter = new OBHadapter(ObhlistItem, getApplicationContext());
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                if (count == 0) {
                    Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    new postnewDEL().execute("https://testapi.innovasivtech.com/warehouse_api/outbound_head/create.php");
                    Toast.makeText(getBaseContext(), totalcount + " items scanned!", Toast.LENGTH_SHORT).show();

                    count = 0;

                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                    ObhlistItem.clear();
                    showOBHentries();
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
                    ven = splitString[4];
                    delnum = Integer.parseInt(lastid);
                    newdelnum = "Del-" + (delnum);


                    getstockdata(stockurl);
                    IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                    intentIntegrator.setPrompt("Scan a Material");
                    intentIntegrator.setOrientationLocked(false);
                    intentIntegrator.initiateScan();


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

                            count = count + 1;
                            totalcount = count;
                            new_stock_id = stock_id;
                            stock_update = Integer.parseInt(curr_stock) - Integer.parseInt(qty);
                            new updatestock().execute("https://testapi.innovasivtech.com/warehouse_api/material_stock/update.php");
                            new postnewmat().execute("https://testapi.innovasivtech.com/warehouse_api/outbound_body/create.php");
//                            Toast.makeText(getApplicationContext(), "Material Added!", Toast.LENGTH_LONG).show();

                            ObhlistItem.clear();
                            showOBHentries();
                        } else {
                            Toast.makeText(getApplicationContext(), "Insufficient Stock!", Toast.LENGTH_LONG).show();
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

    private class postnewDEL extends AsyncTask<String, Void, String> {
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
                jsonObjectFinal.put("delnum", newdelnum);
                jsonObjectFinal.put("customer", customer);
                jsonObjectFinal.put("date", post_date);
                jsonObjectFinal.put("no_of_item", totalcount);
                jsonObjectFinal.put("note1", "");
                jsonObjectFinal.put("note2", "");


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
                jsonObjectFinal.put("delnum", newdelnum);
                jsonObjectFinal.put("matcode", mat);
                jsonObjectFinal.put("matdesc", desc);
                jsonObjectFinal.put("quantity", qty);
                jsonObjectFinal.put("scan_date", post_date);
                jsonObjectFinal.put("scan_time", post_time);
                jsonObjectFinal.put("serial_num", ven);
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
        Intent intent = new Intent(newOBhead.this, Dashboard.class);
        startActivity(intent);

    }
}