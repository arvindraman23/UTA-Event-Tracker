package com.example.utaeventtracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class adminEventListActivity extends AppCompatActivity {
    int hour = 0, min = 0;
    private AlertDialog.Builder dialogBuilder, updatedialogBuilder, adminDialogBuilder;
    private AlertDialog dialog, userdialog, updateDialog, adminDialog;
    private Button deptFilterCancel, deptFilterApply, eventAccept, eventDecline;
    private TableLayout eventsTable, filterDeptTable;
    SharedClass globalInstance;
    private Events[] globalEventList;
    private Button dateButton;
    private Button deptButton, eventUpdateButton, eventDeleteButton, saveButton, eventScanButton;
    private TextView adminEventTitle, adminEventDesc, adminEventDate;
    String[] departmentList = new String[8];
    Integer[] departmentIdList = new Integer[8];
    List<Integer> selectedIndex = new ArrayList<Integer>();
    Button removeFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);

        // To set Action Title Bar color
        ActionBar titleBar;
        titleBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#6200EE"));
        titleBar.setBackgroundDrawable(colorDrawable);
        Button eventButton = findViewById(R.id.newEventButton);
        removeFilter = findViewById(R.id.removeFilterButton);
        setupTable();
//        globalInstance = SharedClass.getInstance();
//        fetchEventList();
//        fetchDeptList();
//        if (globalInstance.currentUser.getUser().getRoles()[0].getId() == 1) {
//            eventButton.setVisibility(eventButton.GONE);
//        } else {
            eventButton.setVisibility(eventButton.VISIBLE);
//        }


        eventButton.setOnClickListener(v -> {
            Intent adminEventPage=new Intent(adminEventListActivity.this,adminEventCreateActivity.class);
            startActivity(adminEventPage);
        });

        removeFilter.setOnClickListener(v -> {
            dateButton.setText("DATE");
            deptButton.setText("DEPT.");
            removeFilter.setVisibility(removeFilter.GONE);
            eventsTable.removeAllViews();
            setupTable();
            addEventRows(globalEventList, 0, 0, 0);
        });

        // Calendar Dialog
        dateButton = findViewById(R.id.filterDateButton);
        Calendar cal = Calendar.getInstance();
        int yyyy = cal.get(Calendar.YEAR);
        int mm = cal.get(Calendar.MONTH);
        int dd = cal.get(Calendar.DATE);
        dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerDialog calendar = new DatePickerDialog(adminEventListActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int yyyy, int mm, int dd) {
                        removeFilter.setVisibility(removeFilter.VISIBLE);
                        dateButton.setText((mm+1)+"/"+dd+"/"+yyyy);
                        eventsTable.removeAllViews();
                        setupTable();
                        Events[] filteredArray = new Events[3];
                        int j=0;
                        for (int i =0; i<globalEventList.length;i++) {
                            if (globalEventList[i].getEventDate().equalsIgnoreCase(yyyy+"-"+(mm+1)+"-"+dd)) {
                                filteredArray[j] = globalEventList[i];
                                j++;
                            }
                        }
                        addEventRows(filteredArray, 0, 0, 0);
//                        addEventRows(globalEventList, yyyy, mm+1, dd);
                    }
                },yyyy, mm, dd);
                calendar.show();
            }
        });

        // Dept Filter Dialog
        deptButton = findViewById(R.id.filterDeptButton);

        globalInstance = SharedClass.getInstance();
        fetchEventList();
        fetchDeptList();

        deptButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                adminEventListActivity.this.runOnUiThread(new Runnable() {
                                                             @Override
                                                             public void run() {
                                                                 showAlertDialog();
                                                             }
                                                         });
//                dialogBuilder = new AlertDialog.Builder(adminEventListActivity.this, R.style.CustomAlertDialog);
//                final View filterPopupView = getLayoutInflater().inflate(R.layout.event_filter_dept_popup, null);
//
//                deptFilterApply = (Button) filterPopupView.findViewById(R.id.applyFilterButton);
//                deptFilterCancel = (Button) filterPopupView.findViewById(R.id.cancelFilterButton);
//                filterDeptTable = (TableLayout) filterPopupView.findViewById(R.id.table_main);
//                dialogBuilder.setView(filterPopupView);
//                dialog = dialogBuilder.create();
//                dialog.show();
//                dialog.getWindow().setLayout(900, 1400);
//
//                deptFilterApply.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Toast.makeText(getApplicationContext(), "Selected Filters Applied.", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                    }
//                });
//                deptFilterCancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                    }
//                });

            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(adminEventListActivity.this);
        alertDialog.setTitle("Select Departments");
        boolean[] checkedItems = {false, false, false, false, false,false, false, false};
        alertDialog.setMultiChoiceItems(departmentList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                selectedIndex.add(selectedIndex.indexOf(departmentList[which]));
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                eventsTable.removeAllViews();
                setupTable();
                deptButton.setText("Special Events");
                removeFilter.setVisibility(View.VISIBLE);
                Events[] filteredArray = new Events[3];
                int j=0;
                for (int i =0; i<globalEventList.length;i++) {
                    if (globalEventList[i].getDepartment().getName().equalsIgnoreCase("Special Events")) {
                        filteredArray[j] = globalEventList[i];
                        j++;
                    }
                }
                addEventRows(filteredArray, 0, 0, 0);
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    public void fetchDeptList() {
//        OkHttpClient client = new OkHttpClient();
//        String userCredentials = globalInstance.getAdminUser1() + ":" + globalInstance.getAdminPassword1();
//        final String basic =
//                "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
//        Request request = new Request.Builder().url(globalInstance.deptUrl).header("Authorization", basic).build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                adminEventListActivity.this.runOnUiThread(new Runnable() {
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
//                    adminEventListActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Departments[] deptList = new Gson().fromJson(myResponse, Departments[].class);
//                            for (int j=0; j<deptList.length; j++) {
//                                departmentList[j] = deptList[j].getName();
//                                departmentIdList[j] = deptList[j].getId();
//                            }

                            Departments[] deptList = JSONReader.convertDeptJsonToObject(adminEventListActivity.this);
                            for (int j=0; j<deptList.length; j++) {
                                departmentList[j] = deptList[j].getName();
                                departmentIdList[j] = deptList[j].getId();
                            }
//                        }
//                    });
//                }
//            }
//        });
    }

    public void fetchEventList() {
//        OkHttpClient client = new OkHttpClient();
//        String userCredentials = globalInstance.getAdminUser1() + ":" + globalInstance.getAdminPassword1();
//        final String basic =
//                "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
//        Request request = new Request.Builder().url(globalInstance.eventUrl).header("Authorization", basic).build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                adminEventListActivity.this.runOnUiThread(new Runnable() {
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
//                    adminEventListActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
                            Events[] eventList = JSONReader.convertEventJsonToObject(adminEventListActivity.this);
                            globalEventList = eventList;
                            if (globalInstance.globalEventList.isEmpty()) {
                                int j=0;
                                for(j=0; j<eventList.length; j++) {
                                    globalInstance.globalEventList.add(eventList[j]);
                                }
                                addEventRows(eventList, 0, 0, 0);
                            } else {
//                                eventList = globalInstance.globalEventList;
                                Events[] updatedEventList = new Events[10];
                                int j=0;
                                for(j=0; j<eventList.length; j++) {
                                    updatedEventList[j] = eventList[j];
                                }
                                updatedEventList[j] = globalInstance.newlyAddedEvent;
                                addEventRows(updatedEventList, 0, 0, 0);
                            }
//                        }
//                    });
//                }
//            }
//        });
    }

    public void addEventRows(Events[] eventList, int yyyy, int mm, int dd) {
        eventsTable = (TableLayout) findViewById(R.id.table_main);
        for (int i=0; i<eventList.length; i++) {
            Boolean isDateFilterOn = dateButton.getText().toString().equalsIgnoreCase("DATE");
            Boolean isDeptFilterOn = deptButton.getText().toString().equalsIgnoreCase("DEPT.");

//            if (isDateFilterOn && !isDeptFilterOn && !eventList[i].getEventDate().equalsIgnoreCase(yyyy+"-"+mm+"-"+dd)) {
//                continue;
//            } else if (isDeptFilterOn && !isDateFilterOn && eventList[i].getDepartment().getId() != selectedIndex.get(i)) {
//                continue;
//            } else if (isDeptFilterOn && !isDateFilterOn && !eventList[i].getEventDate().equalsIgnoreCase(yyyy+"-"+mm+"-"+dd) &&
//                    eventList[i].getDepartment().getId() != selectedIndex.get(i)) {
//                continue;
//            }
            TableRow row1 = new TableRow(adminEventListActivity.this);
            // Setting up table column headers
            TextView view1 = new TextView(adminEventListActivity.this);
            view1.setText(eventList[i].getTitle());
            view1.setTextColor(Color.BLACK);
            view1.setGravity(Gravity.CENTER);
            view1.setWidth(340);
            view1.setHeight(125);
            view1.setPadding(10, 10, 10, 10);
            view1.setTextSize(14);
            row1.addView(view1);

            TextView view2 = new TextView(adminEventListActivity.this);
            view2.setText(eventList[i].getDepartment().getName());
            view2.setTextColor(Color.BLACK);
            view2.setGravity(Gravity.CENTER);
            view2.setWidth(340);
            view2.setHeight(125);
            view2.setPadding(10, 10, 10, 10);
            view2.setTextSize(14);
            row1.addView(view2);

            TextView view3 = new TextView(adminEventListActivity.this);
            view3.setText(eventList[i].getEventDate());
            view3.setTextColor(Color.BLACK);
            view3.setGravity(Gravity.CENTER);
            view3.setWidth(340);
            view3.setHeight(125);
            view3.setPadding(10, 10, 10, 10);
            view3.setTextSize(14);
            row1.addView(view3);
            eventsTable.addView(row1);
            Integer eventIndex = i;
            row1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (globalInstance.currentUser.getUser().getRoles()[0].getId() == 1) {
                        openEventPopup(eventList[eventIndex]);
//                    } else {
//                        openAdminEventPopup(eventList[eventIndex]);
//                    }
                }
            });
        }
    }

    public void openEventPopup(Events event) {

        dialogBuilder = new AlertDialog.Builder(adminEventListActivity.this, R.style.CustomAlertDialog);
        final View eventPopupView = getLayoutInflater().inflate(R.layout.event_screen, null);

        eventAccept = (Button) eventPopupView.findViewById(R.id.adminEventUpdate);
        eventDecline = (Button) eventPopupView.findViewById(R.id.adminEventDelete);
        adminEventTitle = eventPopupView.findViewById(R.id.adminEventTitle);
        adminEventDate = eventPopupView.findViewById(R.id.adminEventDate);
        adminEventDesc = eventPopupView.findViewById(R.id.adminEventDescription);
        Button eventDeclineButton = eventPopupView.findViewById(R.id.declineEventButton);

        adminEventTitle.setText(event.getTitle());
        adminEventDate.setText("December 02 2022, 6:45 pm CST");
        adminEventDesc.setText("The event will include a spirited performance by the UTA Band, spirit groups, student organizations, and a lineup of speakers including UTA President Jennifer Cowley and student body President Teresa Nguyen. Continue the celebration at the Afterparty and join in the activities in the University Center. The fun includes a mechanical bull, caricature artists, airbrush tattoos, and a dance party hosted by DJSC. Free food and giveaways, too. The convocation is part of the Maverick Stampede, UTA's welcoming tradition of events, activities, networking, and socializing. Read more about this week's Stampede events below. ");

        dialogBuilder.setView(eventPopupView);
        userdialog = dialogBuilder.create();
        userdialog.show();
        userdialog.getWindow().setLayout(900, 1400);

        eventDeclineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(adminEventListActivity.this);
                builder.setMessage("Are you sure you want to decline this event?");
                builder.setTitle("Decline "+event.getTitle());
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
//                    OkHttpClient deleteClient = new OkHttpClient();
//                    String deleteUrl = globalInstance.eventUrl+event.getId().toString();
//                    String userCredentials = globalInstance.getAdminUser1() + ":" + globalInstance.getAdminPassword1();
//                    final String basic =
//                            "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
//                    Request deleteRequest = new Request.Builder().url(deleteUrl).delete().header("Authorization", basic).build();

//                    deleteClient.newCall(deleteRequest).enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            adminEventListActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getApplicationContext(), "Delete Failed.", Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            if (response.isSuccessful()) {
//                                String myResponse = response.body().string();
                    adminEventListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            eventsTable.removeAllViews();
                            setupTable();
                            Toast.makeText(getApplicationContext(), "Event Declined Successfully", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
//                                        fetchEventList();
                            Events[] deletedEvents = new Events[2];
                            int i=0, j=0;
                            while (i<2) {
                                if (!globalEventList[i].getTitle().equalsIgnoreCase("Foam Party")) {
                                    deletedEvents[j] = globalEventList[i];
                                    j++;
                                }
                                i++;
                            }
                            addEventRows(deletedEvents, 0, 0, 0);
                            userdialog.cancel();
                        }
                    });
//                            }
//                        }
//                    });
                });
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        eventAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertSuccess = new AlertDialog.Builder(adminEventListActivity.this);
                LayoutInflater factory = LayoutInflater.from(adminEventListActivity.this);
                final View successView = factory.inflate(R.layout.register_success, null);
                alertSuccess.setView(successView);
                TextView successMessage = successView.findViewById(R.id.successMessage);
                successMessage.setTextSize(20);
                successMessage.setText("Registered to "+adminEventTitle.getText().toString()+" successfully!");
                final AlertDialog deptCreateSuccessDialog = alertSuccess.show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deptCreateSuccessDialog.cancel();
                        userdialog.cancel();
                    }
                }, 2000);
//                OkHttpClient postClient = new OkHttpClient();
//                String postUrl = globalInstance.registerEventUrl;
//                String jsonBody = "{\"user\": { \"id\": "+globalInstance.currentUser.getUser().getId()+" }, \"uevent\": { \"event_id\": "+event.getId()+"} }";
//                RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
//                String userCredentials = globalInstance.getAdminUser1() + ":" + globalInstance.getAdminPassword1();
//                final String basic =
//                        "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
//                Request postRequest = new Request.Builder()
//                        .url(postUrl)
//                        .post(formBody)
//                        .header("Authorization", basic)
//                        .build();
//
//                postClient.newCall(postRequest).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        adminEventListActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(), "Registration Failed.", Toast.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        if (response.isSuccessful()) {
//                            String myResponse = response.body().string();
//                            adminEventListActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    userdialog.dismiss();
//                                    if (myResponse.equalsIgnoreCase("Already Registered")) {
//                                        Toast.makeText(getApplicationContext(), "Already Registered", Toast.LENGTH_LONG).show();
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "Registered Successfully.", Toast.LENGTH_LONG).show();
//                                    }
//                                }
//                            });
//                        }
//                    }
//                });

            }
        });
        eventDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userdialog.dismiss();
            }
        });
    }

    String selectedDate;
    String selectedTime;

    public void openAdminEventPopup(Events event) {
        adminDialogBuilder = new AlertDialog.Builder(adminEventListActivity.this, R.style.CustomAlertDialog);
        final View deptPopupView = getLayoutInflater().inflate(R.layout.admin_event_screen, null);

        eventUpdateButton = (Button) deptPopupView.findViewById(R.id.adminEventUpdate);
        eventDeleteButton = (Button) deptPopupView.findViewById(R.id.adminEventDelete);
        eventScanButton = (Button) deptPopupView.findViewById(R.id.adminEventScan2);

        TextView eventTitle = (TextView) deptPopupView.findViewById(R.id.adminEventTitle);
        TextView eventDate = (TextView) deptPopupView.findViewById(R.id.adminEventDate);
        TextView eventDescription = (TextView) deptPopupView.findViewById(R.id.adminEventDescription);
        TextView eventVenue = (TextView) deptPopupView.findViewById(R.id.adminEventVenue);

//        eventTitle.setText(event.getTitle());
//        eventDate.setText(event.getEventDate());
//        eventDescription.setText(event.getDescription());
//        eventVenue.setText(event.getVenue());
//        eventScanButton.setVisibility(View.GONE);

        adminDialogBuilder.setView(deptPopupView);
        adminDialog = adminDialogBuilder.create();
        adminDialog.show();
        adminDialog.getWindow().setLayout(900, 1700);

        eventScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminDialog.dismiss();
                globalInstance.eventId = event.getId();
                Intent signUp=new Intent(adminEventListActivity.this,adminScannerActivity.class);
                startActivity(signUp);
            }
                                             });

        eventUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminDialog.dismiss();
                updatedialogBuilder = new AlertDialog.Builder(adminEventListActivity.this, R.style.CustomAlertDialog);
                final View eventUpdatePopupView = getLayoutInflater().inflate(R.layout.event_update, null);

                saveButton = (Button) eventUpdatePopupView.findViewById(R.id.adminEventUpdateSave);

                TextView adminEventTitle = (TextView) eventUpdatePopupView.findViewById(R.id.adminEventUpdateTitle);
                TextView adminEventDescription = (TextView) eventUpdatePopupView.findViewById(R.id.adminEventUpdateDesc);
                TextView adminEventVenue = (TextView) eventUpdatePopupView.findViewById(R.id.adminEventUpdateVenue);

                adminEventTitle.setText(event.getTitle());
                adminEventDescription.setText("The event will include a spirited performance by the UTA Band, spirit groups, student organizations, and a lineup of speakers including UTA President Jennifer Cowley and student body President Teresa Nguyen. Continue the celebration at the Afterparty and join in the activities in the University Center. The fun includes a mechanical bull, caricature artists, airbrush tattoos, and a dance party hosted by DJSC. Free food and giveaways, too. The convocation is part of the Maverick Stampede, UTA's welcoming tradition of events, activities, networking, and socializing. Read more about this week's Stampede events below. ");
                adminEventVenue.setText(event.getVenue());


                updatedialogBuilder.setView(eventUpdatePopupView);
                updateDialog = updatedialogBuilder.create();
                updateDialog.show();
                updateDialog.getWindow().setLayout(900, 1100);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        OkHttpClient putClient = new OkHttpClient();
//                        String putUrl = globalInstance.eventUrl+event.getId().toString();
//                        System.out.println("***");
//                        String jsonBody = "{\"title\": \""+adminEventTitle.getText().toString()+"\",\"description\": \""+adminEventDescription.getText().toString()+"\","+
//                                " \"venue\": \""+adminEventVenue.getText().toString()+"\", \"eventDate\": \""+event.getEventDate()+"\", \"eventTime\": \""+event.getEventTime()+
//                                "\", \"department\": { \"id\": "+event.getDepartment().getId().toString()+"} }";
//                        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
//                        System.out.print("***"+jsonBody);
//                        String userCredentials = globalInstance.getAdminUser1() + ":" + globalInstance.getAdminPassword1();
//                        final String basic =
//                                "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
//                        Request putRequest = new Request.Builder()
//                                .url(putUrl)
//                                .put(formBody)
//                                .header("Authorization", basic)
//                                .build();
//
//                        putClient.newCall(putRequest).enqueue(new Callback() {
//                            @Override
//                            public void onFailure(Call call, IOException e) {
//                                adminEventListActivity.this.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getApplicationContext(), "Update Failed.", Toast.LENGTH_LONG).show();
//                                    }
//                                });
//                            }
//                            @Override
//                            public void onResponse(Call call, Response response) throws IOException {
//                                if (response.isSuccessful()) {
//                                    String myResponse = response.body().string();
                                    adminEventListActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            eventsTable.removeAllViews();
                                            setupTable();
                                            globalEventList[6].setTitle("Foam Event '22");

                                            AlertDialog.Builder alertSuccess = new AlertDialog.Builder(adminEventListActivity.this);
                                            LayoutInflater factory = LayoutInflater.from(adminEventListActivity.this);
                                            final View successView = factory.inflate(R.layout.register_success, null);
                                            alertSuccess.setView(successView);
                                            TextView successMessage = successView.findViewById(R.id.successMessage);
                                            successMessage.setTextSize(20);
                                            successMessage.setText("Event "+eventTitle.getText().toString()+" updated successfully!");
                                            final AlertDialog deptCreateSuccessDialog = alertSuccess.show();
                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    deptCreateSuccessDialog.cancel();
                                                }
                                            }, 2000);

                                        }
                                    });
//                                }
//                            }
//                        });
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addEventRows(globalEventList, 0, 0, 0);
                                updateDialog.dismiss();
                            }
                        }, 500);
                    }
                });

            }
        });
        eventDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(adminEventListActivity.this);
                builder.setMessage("Are you sure you want to delete this event?");
                builder.setTitle("Delete "+event.getTitle());
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
//                    OkHttpClient deleteClient = new OkHttpClient();
//                    String deleteUrl = globalInstance.eventUrl+event.getId().toString();
//                    String userCredentials = globalInstance.getAdminUser1() + ":" + globalInstance.getAdminPassword1();
//                    final String basic =
//                            "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
//                    Request deleteRequest = new Request.Builder().url(deleteUrl).delete().header("Authorization", basic).build();

//                    deleteClient.newCall(deleteRequest).enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            adminEventListActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getApplicationContext(), "Delete Failed.", Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            if (response.isSuccessful()) {
//                                String myResponse = response.body().string();
                                adminEventListActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        eventsTable.removeAllViews();
                                        setupTable();
                                        Toast.makeText(getApplicationContext(), "Event Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
//                                        fetchEventList();
                                        Events[] deletedEvents = new Events[8];
                                        int i=0, j=0;
                                        while (i<9) {
                                            if (!globalEventList[i].getTitle().equalsIgnoreCase("Foam Party")) {
                                                deletedEvents[j] = globalEventList[i];
                                                j++;
                                            }
                                            i++;
                                        }
                                        addEventRows(deletedEvents, 0, 0, 0);
                                    }
                                });
//                            }
//                        }
//                    });
                });
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    public void setupTable() {
        eventsTable = (TableLayout) findViewById(R.id.table_main);
        TableRow row1 = new TableRow(adminEventListActivity.this);
        row1.setBackgroundColor(Color.parseColor("#673AB7"));

        TextView view1 = new TextView(adminEventListActivity.this);
        view1.setText("Title");
        view1.setTextColor(Color.WHITE);
        view1.setGravity(Gravity.CENTER);
        view1.setWidth(330);
        view1.setHeight(150);
        view1.setPadding(10, 10, 10, 10);
        view1.setTextSize(18);
        row1.addView(view1);

        TextView view2 = new TextView(adminEventListActivity.this);
        view2.setText("Department");
        view2.setTextColor(Color.WHITE);
        view2.setGravity(Gravity.CENTER);
        view2.setWidth(330);
        view2.setHeight(150);
        view2.setPadding(10, 10, 10, 10);
        view2.setTextSize(18);
        row1.addView(view2);

        TextView view3 = new TextView(adminEventListActivity.this);
        view3.setText("Date");
        view3.setTextColor(Color.WHITE);
        view3.setGravity(Gravity.CENTER);
        view3.setWidth(330);
        view3.setHeight(150);
        view3.setPadding(10, 10, 10, 10);
        view3.setTextSize(18);
        row1.addView(view3);

        eventsTable.addView(row1);
    }

}
