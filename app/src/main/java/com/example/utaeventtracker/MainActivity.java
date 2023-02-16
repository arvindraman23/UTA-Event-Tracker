package com.example.utaeventtracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Button signUpButton;
    private Button loginButton;
    private Button newUserButton;
    public Boolean isAdmin;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleButton;
    SharedClass globalInstance;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // To set Action Title Bar color
        ActionBar titleBar;
        titleBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#6200EE"));
        titleBar.setBackgroundDrawable(colorDrawable);
        globalInstance = SharedClass.getInstance();


        // Action for signUp button to navigate to signUp screen
        signUpButton = findViewById(R.id.signupButton);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        signUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // starting background task to update product
                Intent signUp=new Intent(MainActivity.this,signupActivity.class);
                startActivity(signUp);

                // ADMIN SCANNER VIEW
//                Intent signUp=new Intent(MainActivity.this,adminScannerActivity.class);
//                startActivity(signUp);


            }
        });

        // Action for Google login button
//        googleButton = findViewById(R.id.googleButton);
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//        gsc = GoogleSignIn.getClient(this, gso);
//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
//        if(acct!=null) {
//            navigateToSecondActivity();
//        }
//        googleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                googleSignIn();
//
//                Intent signUp=new Intent(MainActivity.this,userEventActivity.class);
//                startActivity(signUp);
//            }
//        });

        // Action for 'new User?' button
//        newUserButton = findViewById(R.id.newUserButton);
//        newUserButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v)
//            {
//                // starting background task to update product
//                Intent signUp=new Intent(MainActivity.this,signupActivity.class);
//                startActivity(signUp);
//            }
//        });

        // Action for login button to navigate to home page
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                EditText emailTextField = findViewById(R.id.emailField);
                EditText passwordTextField = findViewById(R.id.passwordField);
                System.out.println(passwordTextField.getText().toString());
//                if (emailTextField.getText().toString().isEmpty() || passwordTextField.getText().toString().isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "Missing Login Credentials!", Toast.LENGTH_LONG).show();
//                } else {
//                    if (emailTextField.getText().toString().equalsIgnoreCase("johndoe123") && passwordTextField.getText().toString().equalsIgnoreCase("johndoe123")) {
                        Intent mainScreen = new Intent(MainActivity.this, adminMainActivity.class);
//                        final Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(), "Signed-in Successfully!", Toast.LENGTH_LONG).show();
                                startActivity(mainScreen);
//                            }
//                        }, 500);
//                    } else {
//                        final Handler handler1 = new Handler();
//                        handler1.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(), "Invalid Login Credentials", Toast.LENGTH_LONG).show();
//                            }
//                        }, 500);
//                    }
//                }



                OkHttpClient postClient = new OkHttpClient();
                String postUrl = globalInstance.userLogin;
                String jsonBody = "{\"usernameOrEmail\":\""+emailTextField.getText().toString()+"\", \"password\": \""+passwordTextField.getText().toString()+"\" }";
                System.out.println("***"+jsonBody);
                globalInstance.currentUserName = emailTextField.getText().toString();
                globalInstance.currentUserPassword = passwordTextField.getText().toString();

                RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
                Request postRequest = new Request.Builder()
                        .url(postUrl)
                        .post(formBody)
                        .build();
//                postClient.newCall(postRequest).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        MainActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(), "Sign-in Failed.", Toast.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        if (response.isSuccessful()) {
//                            String myResponse = response.body().string();
//                            MainActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getApplicationContext(), "Signed-in Successully.", Toast.LENGTH_LONG).show();
//                                    Intent mainScreen = new Intent(MainActivity.this, adminMainActivity.class);
//
//                                    OkHttpClient client = new OkHttpClient();
//                                    String userCredentials = emailTextField.getText().toString() + ":" + passwordTextField.getText().toString();
//                                    final String basic =
//                                            "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
//                                    Request userDetailsRequest = new Request.Builder().url(globalInstance.userDetailsUrl+emailTextField.getText().toString()).header("Authorization", basic).build();
//                                    client.newCall(userDetailsRequest).enqueue(new Callback() {
//                                        @Override
//                                        public void onFailure(Call call, IOException e) {
//                                            MainActivity.this.runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
//                                                }
//                                            });
//                                        }
//                                        @Override
//                                        public void onResponse(Call call, Response response) throws IOException {
//                                            if (response.isSuccessful()) {
//                                                String myResponse = response.body().string();
//                                                MainActivity.this.runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        UserDetails userDetails = new Gson().fromJson(myResponse, UserDetails.class);
//                                                        globalInstance.currentUser = userDetails;
//                                                        System.out.println(("11"+ userDetails.getUser().getRoles()[0].getId()));
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    });
//
//                                    final Handler handler = new Handler();
//                                    handler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            startActivity(mainScreen);
//                                        }
//                                    }, 500);
//
//                                }
//                            });
//                        }
//                    }
//                });





                // Warning: The below mentioned logic is for testing purpose. Must be replaced with API Call

//                if (emailTextField.getText().toString().equalsIgnoreCase("admin") &&
//                        passwordTextField.getText().toString().equalsIgnoreCase("johndoe")) {
//                    globalInstance.setAdmin(true);
//                } else {
//                    globalInstance.setAdmin(false);
//                }
//
////                boolean isAdminLogin = emailTextField.getText().toString().equalsIgnoreCase("admin") &&
////                        passwordTextField.getText().toString().equalsIgnoreCase("admin");
//                if (!globalInstance.getAdmin()) {
//                    Intent adminMainPage=new Intent(MainActivity.this,adminMainActivity.class);
//                    startActivity(adminMainPage);
//                } else {
//                    Intent homePage=new Intent(MainActivity.this,homepageActivity.class);
//                    startActivity(homePage);
//                }

                // JSON ENCODER/DECODER
//                ExecutorService executor = Executors.newSingleThreadExecutor();
//                executor.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        Events event = JSONReader.convertJsonToObject(MainActivity.this);
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(), event.getTitle(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
            }
        });

    }

    void googleSignIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent,1000);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
    void navigateToSecondActivity() {
        finish();
//        Intent intent = new Intent(MainActivity.this, homepageActivity.class);
//        startActivity(intent);
    }
}