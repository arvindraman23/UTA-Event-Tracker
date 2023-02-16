package com.example.utaeventtracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class signupActivity extends AppCompatActivity {
    private Button signupButton;
    private TextView name, username, email, utaid, zipcode, phno, address, password;
    SharedClass globalInstance = SharedClass.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // To set Action Title Bar color
        ActionBar titleBar;
        titleBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#6200EE"));
        titleBar.setBackgroundDrawable(colorDrawable);

        signupButton = findViewById(R.id.signupButton_2);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        utaid = findViewById(R.id.utaid);
        zipcode = findViewById(R.id.zipcode);
        address = findViewById(R.id.address);
        phno = findViewById(R.id.phno);

        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (name.getText().toString().isEmpty() || username.getText().toString().isEmpty() || email.getText().toString().isEmpty() || password.getText().toString().isEmpty() ||
                        utaid.getText().toString().isEmpty() || zipcode.getText().toString().isEmpty() || address.getText().toString().isEmpty() || phno.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing Details", Toast.LENGTH_SHORT).show();
                } else {
                    String jsonBody = "{\"name\": \"" + name.getText().toString() + "\",\"username\": \"" + username.getText().toString() + "\"," +
                            " \"email\": \"" + email.getText().toString() + "\", \"password\": \"" + password.getText().toString() + "\", \"utaId\": " + utaid.getText() +
                            ", \"zipcode\": " + zipcode.getText().toString() + ", \"phNo\": " + phno.getText().toString() + ", \"address\": \"" + address.getText().toString() + "\"," +
                            "\"departments\": [\"1\", \"3\", \"5\"] }";

                    System.out.println("***" + jsonBody);
                    globalInstance.signupJsonBody = jsonBody;

                    OkHttpClient postClient = new OkHttpClient();
                    String postUrl = globalInstance.signupUser;
                    RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
                    Request postRequest = new Request.Builder()
                            .url(postUrl)
                            .post(formBody)
                            .build();

//                postClient.newCall(postRequest).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        signupActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(), "Registration Failed.", Toast.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        System.out.println("++"+ response.message());
//                        if (response.isSuccessful()) {
//                            String myResponse = response.body().string();
//                            signupActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                        Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_LONG).show();
//                                    Intent homePage=new Intent(signupActivity.this,MainActivity.class);
//                                    startActivity(homePage);
//                                }
//                            });
//                        }
//                    }
//                });

                    AlertDialog.Builder alertSuccess = new AlertDialog.Builder(signupActivity.this);
                    LayoutInflater factory = LayoutInflater.from(signupActivity.this);
                    final View view = factory.inflate(R.layout.register_success, null);
                    alertSuccess.setView(view);
                    alertSuccess.show();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent homePage = new Intent(signupActivity.this, MainActivity.class);
                            startActivity(homePage);
                        }
                    }, 1000);
                }
            }
        });

    }
}