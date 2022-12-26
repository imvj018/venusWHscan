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
import java.util.Date;
import java.util.List;

public class IBHead extends AppCompatActivity implements View.OnClickListener {
    String iburl = "https://testapi.innovasivtech.com/warehouse_api/inbound_head/read.php",
            stockurl = "https://testapi.innovasivtech.com/warehouse_api/material_stock/read.php",
            vendorurl = "https://testapi.innovasivtech.com/warehouse_api/vendor_master/read.php";
    CardView scanBtn;
    Spinner vendd;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<IBHlistItem> IBHlistItem;
    private List<String> vcodelist;
    SimpleDateFormat cdate, ctime;
    String wholedata, mat, desc, qty, uom, ven;
    String post_date, post_time, lastid, newgrnum, vendor;
    String stock_id, new_stock_id, stock_mat_code, curr_stock, matcheck = "";
    int grnum, stock_update, count = 0, totalcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibhead);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        cdate = new SimpleDateFormat("dd-MM-yyyy");
        ctime = new SimpleDateFormat("HH:mm:ss");

        post_date = cdate.format(new Date());
        post_time = ctime.format(new Date());
        recyclerView = findViewById(R.id.ibheadlist);
        scanBtn = findViewById(R.id.scanbutton);
        vendd = findViewById(R.id.vendd);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        IBHlistItem = new ArrayList<>();
        vcodelist = new ArrayList<>();
        loadvendorlist(vendorurl);
        vendd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                vendor = vendd.getItemAtPosition(vendd.getSelectedItemPosition()).toString();
                System.out.println(vendor);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
        showIBHentries();
        scanBtn.setOnClickListener(this);
    }

    private void loadvendorlist(String vendorurl) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, vendorurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("body");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String vid = jsonObject1.getString("id");
                        String vcode = jsonObject1.getString("vendor_code");
                        String vname = jsonObject1.getString("vendor_name");
                        if (vcode.equals("Select Vendor")) {
                            vcodelist.add(vcode);
                        } else {
                            vcodelist.add(vcode + " - " + vname);
                        }


                    }
                    vendd.setAdapter(new ArrayAdapter<String>(IBHead.this, android.R.layout.simple_spinner_dropdown_item, vcodelist));
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

    private void showIBHentries() {
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
                                IBHlistItem item = new IBHlistItem(
                                        object.getString("id"),
                                        object.getString("GRnum"),
                                        object.getString("vendor"),
                                        object.getString("date"),
                                        object.getString("no_of_item")
                                );
                                if (!(object.getString("GRnum")).equals("")) {
                                    IBHlistItem.add(item);
                                }

                            }


                            JSONObject object = array.getJSONObject(0);
                            IBHlistItem item = new IBHlistItem(
                                    object.getString("id"),
                                    object.getString("GRnum"),
                                    object.getString("vendor"),
                                    object.getString("date"),
                                    object.getString("no_of_item")
                            );
                            if (!(object.getString("id")).equals("")) {
                                lastid = object.getString("id");


                            }
                            adapter = new IBHadapter(IBHlistItem, getApplicationContext());
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
        if (!vendor.equals("Select Vendor")) {
            IntentIntegrator intentIntegrator = new IntentIntegrator(this);
            intentIntegrator.setPrompt("Scan a Material");
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.initiateScan();
        } else {
            Toast.makeText(getApplicationContext(), "Select Vendor!", Toast.LENGTH_LONG).show();
        }

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
                    new postnewGR().execute("https://testapi.innovasivtech.com/warehouse_api/inbound_head/create.php");
                    Toast.makeText(getBaseContext(), totalcount + " items scanned!", Toast.LENGTH_SHORT).show();

                    count = 0;
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                    IBHlistItem.clear();
                    showIBHentries();
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
                    grnum = Integer.parseInt(lastid);
                    newgrnum = "GR-" + (grnum);

                    System.out.println("--------count----------" + count + "-----------------");
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
                        count = count + 1;
                        totalcount = count;
                        new_stock_id = stock_id;
                        stock_update = Integer.parseInt(curr_stock) + Integer.parseInt(qty);
                        new updatestock().execute("https://testapi.innovasivtech.com/warehouse_api/material_stock/update.php");
                        new postnewmat().execute("https://testapi.innovasivtech.com/warehouse_api/inbound_body/create.php");
//                        Toast.makeText(getApplicationContext(), "Material Added!", Toast.LENGTH_LONG).show();
                        IBHlistItem.clear();
                        showIBHentries();

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

    private class postnewGR extends AsyncTask<String, Void, String> {
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
                jsonObjectFinal.put("GRnum", newgrnum);
                jsonObjectFinal.put("vendor", vendor);
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
                jsonObjectFinal.put("GRnum", newgrnum);
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
        Intent intent = new Intent(IBHead.this, Dashboard.class);
        startActivity(intent);

    }
}