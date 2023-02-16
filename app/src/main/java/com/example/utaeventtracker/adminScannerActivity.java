package com.example.utaeventtracker;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.gson.Gson;
import com.google.zxing.Result;

import java.io.IOException;
import java.lang.reflect.Array;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class adminScannerActivity extends AppCompatActivity {
    private Integer CAMERA_REQUEST_CODE = 101;
    private CodeScanner codeScanner;
    SharedClass globalInstance = SharedClass.getInstance();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_scanner);

        setupPerm();
        codeScanner();
    }

    private void codeScanner(){
        View scannerView = findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(adminScannerActivity.this, (CodeScannerView) scannerView);

        // Parameters (default values)
        codeScanner.setCamera(CodeScanner.CAMERA_BACK);  // or CAMERA_FRONT or specific camera id
        codeScanner.setFormats(CodeScanner.ALL_FORMATS); // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE); // or CONTINUOUS
        codeScanner.setScanMode(ScanMode.SINGLE); // or CONTINUOUS or PREVIEW
        codeScanner.setAutoFocusEnabled(true); // Whether to enable auto focus or not
        codeScanner.setFlashEnabled(false); // Whether to enable flash or not

        // Callbacks

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                System.out.println("***"+ result.getText().toString());
                OkHttpClient client = new OkHttpClient();
                String userCredentials = globalInstance.getAdminUser1() + ":" + globalInstance.getAdminPassword1();
                final String basic =
                        "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
                String jsonBody = "{\"user\": { \"id\": "+ new Integer(result.getText().toString()) +" }, \"uevent\": { \"event_id\": "+globalInstance.eventId+"} }";
                Request request = new Request.Builder().url(globalInstance.checkRegDetails).header("Authorization", basic).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        adminScannerActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String myResponse = response.body().string();
                            adminScannerActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (myResponse.equalsIgnoreCase("true")) {
                                        Toast.makeText(getApplicationContext(), "User Enrolled!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "User Not Registered!", Toast.LENGTH_LONG).show();
                                    }
                                    Intent adminEventPage=new Intent(adminScannerActivity.this,adminEventListActivity.class);
                                    startActivity(adminEventPage);
                                }
                            });
                        }
                    }
                });

//                OkHttpClient getClient = new OkHttpClient();
//                String getUrl = globalInstance.checkRegDetails;
//                String jsonBody = "{\"user\": { \"id\": "+globalInstance.currentUser.getUser().getId()+" }, \"uevent\": { \"event_id\": "+event.getId()+"} }";
//                RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
//                String userCredentials = globalInstance.getAdminUser1() + ":" + globalInstance.getAdminPassword1();
//                final String basic =
//                        "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
//                Request request = new Request.Builder().url(globalInstance.deptUrl).header("Authorization", basic).build();
//
//                Request putRequest = new Request.Builder()
//                        .url(getUrl)
//                        .get(formBody)
//                        .header("Authorization", basic)
//                        .build();
//
//                putClient.newCall(putRequest).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        adminDeptListActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(), "Update Failed.", Toast.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        if (response.isSuccessful()) {
//                            String myResponse = response.body().string();
//                            adminDeptListActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    deptTable.removeViews(1, departmentList.length);
//                                    Toast.makeText(getApplicationContext(), "Department Name Successfully Updated.", Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
//                    }
//                });

//                Toast.makeText(adminScannerActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        codeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Throwable thrown) {
                Toast.makeText(adminScannerActivity.this, "Camera initialization error: ${it.message}",Toast.LENGTH_LONG).show();
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();

                AlertDialog.Builder alertSuccess = new AlertDialog.Builder(adminScannerActivity.this);
                LayoutInflater factory = LayoutInflater.from(adminScannerActivity.this);
                final View successView = factory.inflate(R.layout.register_success, null);
                alertSuccess.setView(successView);
                TextView successMessage = successView.findViewById(R.id.successMessage);
                successMessage.setTextSize(20);
                successMessage.setText("User is not Registered!");
                final AlertDialog deptCreateSuccessDialog = alertSuccess.show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deptCreateSuccessDialog.cancel();
                        Intent adminEventPage=new Intent(adminScannerActivity.this,adminMainActivity.class);
                        startActivity(adminEventPage);
                    }
                }, 1500);

//                Toast.makeText(adminScannerActivity.this, "User Registered.",Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    public void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

    private void setupPerm(){
        Integer permission =
                ContextCompat.checkSelfPermission(adminScannerActivity.this, android.Manifest.permission.CAMERA);
        if (permission!= PackageManager.PERMISSION_GRANTED) {
            makeRequest();
        }
    }

    private void makeRequest(){
        ActivityCompat.requestPermissions(adminScannerActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(adminScannerActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(adminScannerActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
