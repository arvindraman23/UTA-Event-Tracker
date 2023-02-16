package com.example.utaeventtracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class adminEventCreateActivity extends AppCompatActivity {

    int hour = 0, min = 0;
    List<String> departmentList = new ArrayList<String>();
    List<String> categoryList = new ArrayList<String>();
    List<Integer> departmentIdList = new ArrayList<Integer>();
    SharedClass globalInstance = SharedClass.getInstance();
    String selectedDate;
    String selectedTime;
    Spinner spinnerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event);

        // To set Action Title Bar color
        ActionBar titleBar;
        titleBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#6200EE"));
        titleBar.setBackgroundDrawable(colorDrawable);

        fetchDeptList();

        Button addEventButton = findViewById(R.id.addEventButton);
        TextView evenTitle = findViewById(R.id.eventCreateTitle);
        TextView eventDescription = findViewById(R.id.eventCreateDescription);
        TextView eventVenue = findViewById(R.id.eventCreateVenue);

        addEventButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (selectedDate == null || selectedTime == null || eventDescription.getText().toString().isEmpty() ||
                        evenTitle.getText().toString().isEmpty() || eventVenue.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Missing Details!",Toast.LENGTH_SHORT).show();
                } else {

//                    OkHttpClient postClient = new OkHttpClient();
//                    String postUrl = globalInstance.eventUrl;
//                    String jsonBody = "{\"title\": \""+evenTitle.getText().toString()+"\",\"description\": \""+eventDescription.getText().toString()+"\","+
//                            " \"venue\": \""+eventVenue.getText().toString()+"\", \"eventDate\": \""+selectedDate+"\", \"eventTime\": \""+selectedTime+
//                            "\", \"department\": { \"id\": "+departmentIdList.get(departmentList.indexOf(spinnerView.getSelectedItem().toString()))+"} }";
//                    RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
//                    String userCredentials = globalInstance.getAdminUser1() + ":" + globalInstance.getAdminPassword1();
//                    final String basic =
//                            "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
//                    Request postRequest = new Request.Builder()
//                            .url(postUrl)
//                            .post(formBody)
//                            .header("Authorization", basic)
//                            .build();
//                    postClient.newCall(postRequest).enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            adminEventCreateActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getApplicationContext(), "Event Creation Failed.", Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            if (response.isSuccessful()) {
//                                String myResponse = response.body().string();
                                adminEventCreateActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder alertSuccess = new AlertDialog.Builder(adminEventCreateActivity.this);
                                        LayoutInflater factory = LayoutInflater.from(adminEventCreateActivity.this);
                                        final View successView = factory.inflate(R.layout.register_success, null);
                                        alertSuccess.setView(successView);
                                        TextView successMessage = successView.findViewById(R.id.successMessage);
                                        successMessage.setTextSize(20);
                                        successMessage.setText("Event "+evenTitle.getText().toString()+" created successfully!");
                                        final AlertDialog deptCreateSuccessDialog = alertSuccess.show();
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Events newEvent = new Events();
                                                newEvent.setTitle(evenTitle.getText().toString());
                                                newEvent.setVenue(eventVenue.getText().toString());
                                                newEvent.setEventDate(selectedDate);
                                                newEvent.setEventTime(selectedTime);
                                                Departments selectedDept = new Departments();
                                                selectedDept.setId(9);
                                                selectedDept.setName(spinnerView.getSelectedItem().toString());
                                                newEvent.setDepartment(selectedDept);
                                                newEvent.setDescription(eventDescription.getText().toString());
                                                globalInstance.globalEventList.add(newEvent);
                                                globalInstance.newlyAddedEvent = newEvent;
                                                deptCreateSuccessDialog.cancel();
                                                Intent adminEventListPage=new Intent(adminEventCreateActivity.this, adminEventListActivity.class);
                                                startActivity(adminEventListPage);
                                            }
                                        }, 2000);
                                    }
                                });
//                            }
//                        }
//                    });
                }
            }
        });
    }

    public void fetchDeptList() {
//        OkHttpClient client = new OkHttpClient();
//        String userCredentials = globalInstance.getAdminUser1() + ":" + globalInstance.getAdminPassword1();
//        final String basic =
//                "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
//        Request request = new Request.Builder().url(globalInstance.deptUrl).header("Authorization", basic).build();

//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                adminEventCreateActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String myResponse = response.body().string();
                    adminEventCreateActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Departments[] deptList = JSONReader.convertDeptJsonToObject(adminEventCreateActivity.this);
//                            Departments[] deptList = new Gson().fromJson(myResponse, Departments[].class);
                            for (int j=0; j<deptList.length; j++) {
                                departmentList.add(deptList[j].getName());
                                departmentIdList.add(deptList[j].getId());
                                categoryList.add(deptList[j].getName());
                            }
                            setupUIElements();
                        }
                    });
//                }
//            }
//        });
    }

    public void setupUIElements() {
        // Populating Category Spinner/Drop-Down menu
        List<String> deptChoices = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerView = (Spinner) findViewById(R.id.spinner);
        spinnerView.setAdapter(adapter);
        spinnerView.setPrompt("Select Category");

        // Calendar Dialog
        Button dateButton = findViewById(R.id.dateButton);
        Calendar cal = Calendar.getInstance();
        int yyyy = cal.get(Calendar.YEAR);
        int mm = cal.get(Calendar.MONTH);
        int dd = cal.get(Calendar.DATE);
        dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerDialog calendar = new DatePickerDialog(adminEventCreateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int yyyy, int mm, int dd) {
                        selectedDate = yyyy+"-"+(mm+1)+"-"+dd;
                        dateButton.setText(selectedDate);
                    }
                },yyyy, mm, dd);
                calendar.show();
            }
        });

        // Time Dialog
        Button timeButton = findViewById(R.id.timeButton);
        timeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int pickedHour, int pickedMin) {
                        hour = pickedHour;
                        min = pickedMin;
                        int sec = 0;
                        selectedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, min, sec);
                        timeButton.setText(selectedTime);
                    }
                };
                TimePickerDialog clock = new TimePickerDialog(adminEventCreateActivity.this, onTimeSetListener, hour, min, true);
                clock.setTitle("Select Event Time");
                clock.show();
            }
        });
    }
}