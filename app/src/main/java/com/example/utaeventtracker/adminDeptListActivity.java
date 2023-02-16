package com.example.utaeventtracker;

import android.app.AlertDialog;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class adminDeptListActivity extends AppCompatActivity {
    private AlertDialog.Builder dialogBuilder, updatedialogBuilder, createDialogBuilder;
    private AlertDialog dialog, updateDialog, createDialog;
    private Button addDeptButton, deptUpdateButton, deptDeleteButton, saveButton, cancelButton,
            deptCreateAddButton, deptCreateCancelButton;
    private TableLayout deptTable;
    SharedClass globalInstance;
    private Departments[] departmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dept_list);

        // To set Action Title Bar color
        ActionBar titleBar;
        titleBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#6200EE"));
        titleBar.setBackgroundDrawable(colorDrawable);
        setupTable();
        globalInstance = SharedClass.getInstance();

        fetchDeptList();

        Button addEventButton = findViewById(R.id.newDeptButton);
        addEventButton.setOnClickListener(v -> {
            createDialogBuilder = new AlertDialog.Builder(adminDeptListActivity.this, R.style.CustomAlertDialog);
            final View deptCreatePopupView = getLayoutInflater().inflate(R.layout.dept_create, null);

            deptCreateAddButton = (Button) deptCreatePopupView.findViewById(R.id.deptCreateAddButton);
            deptCreateCancelButton = (Button) deptCreatePopupView.findViewById(R.id.deptCreateCancelButton);
            TextView deptCreateName = (TextView) deptCreatePopupView.findViewById(R.id.deptCreateTextView);
            createDialogBuilder.setView(deptCreatePopupView);
            createDialog = createDialogBuilder.create();
            createDialog.show();
            createDialog.getWindow().setLayout(900, 600);

            deptCreateAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (deptCreateName.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Enter Department Name!", Toast.LENGTH_LONG).show();
                    } else {
//                        OkHttpClient postClient = new OkHttpClient();
//                        String postUrl = globalInstance.deptUrl;
//                        String jsonBody = "{\"name\": \"" + deptCreateName.getText().toString() + "\" }";
//                        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
//                        String userCredentials = globalInstance.getAdminUser1() + ":" + globalInstance.getAdminPassword1();
//                        final String basic =
//                                "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
//                        Request postRequest = new Request.Builder()
//                                .url(postUrl)
//                                .post(formBody)
//                                .header("Authorization", basic)
//                                .build();
//
//                        postClient.newCall(postRequest).enqueue(new Callback() {
//                            @Override
//                            public void onFailure(Call call, IOException e) {
//                                adminDeptListActivity.this.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getApplicationContext(), "Department Creation Failed.", Toast.LENGTH_LONG).show();
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onResponse(Call call, Response response) throws IOException {
//                                if (response.isSuccessful()) {
//                                    String myResponse = response.body().string();
//                                    adminDeptListActivity.this.runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
                                            Boolean isDeptExisting = false;
                                            int j = 0;
                                            for (j=0; j<departmentList.length; j++) {
                                                if (departmentList[j].getName().equalsIgnoreCase(deptCreateName.getText().toString())) {
                                                    isDeptExisting = true;
                                                    break;
                                                }
                                            }
                                            if (isDeptExisting) {
                                                Toast.makeText(getApplicationContext(), "Department Already Exists!", Toast.LENGTH_LONG).show();
                                            } else {
                                                deptTable.removeViews(1, departmentList.length);
                                                Departments arrNew[] = new Departments[departmentList.length + 1];
                                                Departments newDept = new Departments();
                                                newDept.setId(departmentList.length+1);
                                                newDept.setName(deptCreateName.getText().toString());
                                                int i;
                                                for(i = 0; i < departmentList.length; i++) {
                                                    arrNew[i] = departmentList[i];
                                                }
                                                arrNew[i] = newDept;
                                                departmentList = arrNew;
                                                AlertDialog.Builder alertSuccess = new AlertDialog.Builder(adminDeptListActivity.this);
                                                LayoutInflater factory = LayoutInflater.from(adminDeptListActivity.this);
                                                final View successView = factory.inflate(R.layout.register_success, null);
                                                alertSuccess.setView(successView);
                                                TextView successMessage = successView.findViewById(R.id.successMessage);
                                                successMessage.setTextSize(20);
                                                successMessage.setText("Department "+deptCreateName.getText().toString()+" created successfully!");
                                                final AlertDialog deptCreateSuccessDialog = alertSuccess.show();
                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        deptCreateSuccessDialog.cancel();
                                                    }
                                                }, 2000);
                                                addDeptRows(departmentList);
                                                createDialog.dismiss();
                                            }
//                                        }
//                                    });
//                                }
//                            }
//                        });
//                        final Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {

//                            }
//                        }, 500);
                    }
                }
            });
            deptCreateCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createDialog.dismiss();
                }
            });

        });

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
//                adminDeptListActivity.this.runOnUiThread(new Runnable() {
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
//                    adminDeptListActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                          Departments[] deptList = new Gson().fromJson(myResponse, Departments[].class);

        Departments[] deptList = JSONReader.convertDeptJsonToObject(adminDeptListActivity.this);
        departmentList = deptList;
        addDeptRows(deptList);
//                        }
//                    });
//                }
//            }
//        });
    }

    public void openDeptPopup(String deptId, String deptName) {
        dialogBuilder = new AlertDialog.Builder(adminDeptListActivity.this, R.style.CustomAlertDialog);
        final View deptPopupView = getLayoutInflater().inflate(R.layout.dept_screen, null);

        deptUpdateButton = (Button) deptPopupView.findViewById(R.id.deptUpdateButton);
        deptDeleteButton = (Button) deptPopupView.findViewById(R.id.deptDeleteButton);
        TextView deptTitle = (TextView) deptPopupView.findViewById(R.id.deptTitle);
        deptTitle.setText(deptName);
        dialogBuilder.setView(deptPopupView);
        dialog = dialogBuilder.create();
        dialog.show();
        dialog.getWindow().setLayout(900, 600);

        deptUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                updatedialogBuilder = new AlertDialog.Builder(adminDeptListActivity.this, R.style.CustomAlertDialog);
                final View deptUpdatePopupView = getLayoutInflater().inflate(R.layout.dept_update, null);

                saveButton = (Button) deptUpdatePopupView.findViewById(R.id.deptUpdateSaveButton);
                cancelButton = (Button) deptUpdatePopupView.findViewById(R.id.deptUpdateCancelButton);
                TextView deptTitle = (TextView) deptUpdatePopupView.findViewById(R.id.deptUpdateTextView);
                deptTitle.setText(deptName);
                deptTitle.setSelected(true);
                updatedialogBuilder.setView(deptUpdatePopupView);
                updateDialog = updatedialogBuilder.create();
                updateDialog.show();
                updateDialog.getWindow().setLayout(900, 500);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        OkHttpClient putClient = new OkHttpClient();
//                        String putUrl = globalInstance.deptUrl + deptId;
//                        String jsonBody = "{\"id\": " + deptId + ",\"name\": \"" + deptTitle.getText().toString() + "\" }";
//                        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
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
//                                adminDeptListActivity.this.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getApplicationContext(), "Update Failed.", Toast.LENGTH_LONG).show();
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onResponse(Call call, Response response) throws IOException {
//                                if (response.isSuccessful()) {
//                                    String myResponse = response.body().string();
//                                    adminDeptListActivity.this.runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
                                            deptTable.removeViews(1, departmentList.length);
                                            Departments newDept = new Departments();
                                            newDept.setId(Integer.parseInt(deptId));
                                            newDept.setName(deptTitle.getText().toString());
                                            departmentList[Integer.parseInt(deptId)-1] = newDept;
//                                        }
//                                    });
//                                }
//                            }
//                        });
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                fetchDeptList();
                                Toast.makeText(getApplicationContext(), "Department Name Successfully Updated", Toast.LENGTH_LONG).show();
                                addDeptRows(departmentList);
                                updateDialog.dismiss();
                            }
                        }, 500);
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateDialog.dismiss();
                    }
                });

            }
        });
        deptDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(adminDeptListActivity.this);
                builder.setMessage("Are you sure you want to delete this department?");
                builder.setTitle("Delete " + deptName);
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
//                    OkHttpClient deleteClient = new OkHttpClient();
//                    String deleteUrl = globalInstance.deptUrl + deptId;
//                    String userCredentials = globalInstance.getAdminUser1() + ":" + globalInstance.getAdminPassword1();
//                    final String basic =
//                            "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
//                    Request deleteRequest = new Request.Builder().url(deleteUrl).delete().header("Authorization", basic).build();
//
//                    deleteClient.newCall(deleteRequest).enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            adminDeptListActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getApplicationContext(), "Delete Failed.", Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            if (response.isSuccessful()) {
//                                String myResponse = response.body().string();
//                                adminDeptListActivity.this.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
                                        deptTable.removeViews(1, departmentList.length);
                                        departmentList = removeDeptElement(departmentList, Integer.parseInt(deptId)-1);
                                        Toast.makeText(getApplicationContext(), "Department Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                        addDeptRows(departmentList);
//                                    }
//                                });
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

    public static Departments[] removeDeptElement(Departments[] arr, int index)
    {
        if (arr == null || index < 0
                || index >= arr.length) {

            return arr;
        }
        Departments[] anotherArray = new Departments[arr.length - 1];
        for (int i = 0, k = 0; i < arr.length; i++) {
            if (i == index) {
                continue;
            }
            anotherArray[k++] = arr[i];
        }
        return anotherArray;
    }

    public void addDeptRows(Departments[] deptList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deptTable = (TableLayout) findViewById(R.id.table_main);
                for (int i = 0; i < deptList.length; i++) {
                    TableRow row1 = new TableRow(adminDeptListActivity.this);
                    // Setting up table column headers
                    String deptId = deptList[i].getId().toString();
                    String deptName = deptList[i].getName().toString();

                    TextView view1 = new TextView(adminDeptListActivity.this);
                    view1.setText(deptList[i].getId().toString());
                    view1.setTextColor(Color.BLACK);
                    view1.setGravity(Gravity.CENTER);
                    view1.setWidth(500);
                    view1.setHeight(125);
                    view1.setPadding(10, 10, 10, 10);
                    view1.setTextSize(14);
                    row1.addView(view1);

                    TextView view2 = new TextView(adminDeptListActivity.this);
                    view2.setText(deptList[i].getName());
                    view2.setTextColor(Color.BLACK);
                    view2.setGravity(Gravity.CENTER);
                    view2.setWidth(500);
                    view2.setHeight(125);
                    view2.setPadding(10, 10, 10, 10);
                    view2.setTextSize(14);
                    row1.addView(view2);

                    deptTable.addView(row1);

                    row1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openDeptPopup(deptId, deptName);
                        }
                    });
                }
            }
        });
    }

    public void setupTable() {
        deptTable = (TableLayout) findViewById(R.id.table_main);
        TableRow row1 = new TableRow(adminDeptListActivity.this);
        row1.setBackgroundColor(Color.parseColor("#673AB7"));

        TextView view1 = new TextView(adminDeptListActivity.this);
        view1.setText("Department ID");
        view1.setTextColor(Color.WHITE);
        view1.setGravity(Gravity.CENTER);
        view1.setWidth(500);
        view1.setHeight(150);
        view1.setPadding(10, 10, 10, 10);
        view1.setTextSize(18);
        row1.addView(view1);

        TextView view2 = new TextView(adminDeptListActivity.this);
        view2.setText("Department Name");
        view2.setTextColor(Color.WHITE);
        view2.setGravity(Gravity.CENTER);
        view2.setWidth(500);
        view2.setHeight(150);
        view2.setPadding(10, 10, 10, 10);
        view2.setTextSize(18);
        row1.addView(view2);

        deptTable.addView(row1);
    }

}
