package com.example.venusawm;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.RetryPolicy;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Login extends AppCompatActivity {

    TextView head1, head2;
    Button login, forgpword;
    TextInputLayout username, password;
    String userName, passWord;
    String id, fullname, empid, phnum, loginpassword, mailid;
    String loginsts = "";
    String URL = "https://testapi.innovasivtech.com/emp_attendance/profile_api/read.php";
    SessionManager sessionManager;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        head1 = findViewById(R.id.head);
        head2 = findViewById(R.id.textView3);
        login = findViewById(R.id.login_button);
        forgpword = findViewById(R.id.fpw_button);
        username = findViewById(R.id.login_Username);
        password = findViewById(R.id.login_Password);

        sessionManager = new SessionManager(getApplicationContext());
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = Objects.requireNonNull(username.getEditText()).getText().toString();
                passWord = Objects.requireNonNull(password.getEditText()).getText().toString();

                if (!userName.equals("") && !passWord.equals("")) {
                    checkprofile(URL);
                } else {
                    Toast.makeText(Login.this, "Enter Username and Password", Toast.LENGTH_SHORT).show();

                }
            }
        });
        if (sessionManager.getlogin()) {
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
        }
        forgpword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void checkprofile(String url) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("body");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject hit = jsonArray.getJSONObject(i);
                    id = hit.getString("id");
                    fullname = hit.getString("username");
                    empid = hit.getString("emp_id");
                    phnum = hit.getString("mobile_no");
                    loginpassword = hit.getString("user_password");
                    mailid = hit.getString("official_email_id");


                    if ((userName.equals(empid) || userName.equals(fullname)) && passWord.equals(loginpassword)) {
                        loginsts = "OK";
                        SharedPreferences sharedPref = getSharedPreferences("myKey", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("fullname", fullname);
                        editor.putString("empid", empid);
                        editor.apply();
                        sessionManager.setlogin(true);
                        sessionManager.setUsername(userName);
                        startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    }

                }
                if (!loginsts.equals("OK")) {

                    Toast.makeText(Login.this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}