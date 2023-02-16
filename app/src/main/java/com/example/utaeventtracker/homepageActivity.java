//package com.example.utaeventtracker;
//
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.util.Base64;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TableLayout;
//import android.widget.TableRow;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.gson.Gson;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//
//public class homepageActivity extends AppCompatActivity {
//    GoogleSignInOptions gso;
//    GoogleSignInClient gsc;
//    private Button saveButton;
//    public String jsonBody;
//    private TableLayout deptTable1;
//    SharedClass globalInstance = SharedClass.getInstance();
//    String[] departmentList = new String[50];
//    Integer[] departmentIdList = new Integer[50];
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_homepage);
//
//        // To set Action Title Bar color
//        ActionBar titleBar;
//        titleBar = getSupportActionBar();
//        ColorDrawable colorDrawable
//                = new ColorDrawable(Color.parseColor("#6200EE"));
//        titleBar.setBackgroundDrawable(colorDrawable);
//        fetchDeptList();
//
////        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
////        gsc = GoogleSignIn.getClient(this, gso);
////        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
////        if (acct!=null) {
////            // Receive logged in user name and email
////            String name = acct.getDisplayName();
////            String emailId = acct.getEmail();
////            Toast.makeText(getApplicationContext(), name+ "; " +emailId, Toast.LENGTH_SHORT).show();
////        }
//
////        saveButton = findViewById(R.id.saveButton);
////        saveButton.setOnClickListener(new View.OnClickListener() {
////            public void onClick(View v)
////            {
////                globalInstance.signupJsonBody += " \"departments\": [";
////                for (int i=0; i<selectedIndex.size(); i++) {
////                    globalInstance.signupJsonBody += "\""+selectedIndex.get(i).toString()+"\"";
////                    if (i<selectedIndex.size()-1) {
////                        globalInstance.signupJsonBody += ",";
////                    }
////                }
////                globalInstance.signupJsonBody += "] }";
////                System.out.println("***"+globalInstance.signupJsonBody);
////            }
////        });
//
//    }
//
//    void signOut() {
//        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(Task<Void> task) {
//                finish();
//                startActivity(new Intent(homepageActivity.this, MainActivity.class));
//            }
//        });
//    }
//
//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//    }
//
//    public void fetchDeptList() {
//        OkHttpClient client = new OkHttpClient();
//        String userCredentials = globalInstance.getAdminUser1() + ":" + globalInstance.getAdminPassword1();
//        final String basic =
//                "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
//        Request request = new Request.Builder().url(globalInstance.deptUrl).header("Authorization", basic).build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                homepageActivity.this.runOnUiThread(new Runnable() {
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
//                    homepageActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Departments[] deptList = new Gson().fromJson(myResponse, Departments[].class);
//                            for (int j=0; j<deptList.length; j++) {
//                                departmentList[j] = deptList[j].getName();
//                                departmentIdList[j] = deptList[j].getId();
//                                System.out.println("+++"+deptList[0].getId());
//                                addDeptRows(deptList);
//                            }
//                        }
//                    });
//                }
//            }
//        });
//    }
//
//    List<Integer> selectedIndex = new ArrayList<Integer>();
//
//    public void addDeptRows(Departments[] deptList) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                deptTable1 = (TableLayout) findViewById(R.id.regTable2);
//                for (int i=0; i< deptList.length; i++) {
//                    TableRow row1 = new TableRow(homepageActivity.this);
//                    // Setting up table column headers
//                    String deptId = deptList[i].getId().toString();
//                    String deptName = deptList[i].getName().toString();
//
//                    TextView view1 = new TextView(homepageActivity.this);
//                    view1.setText(deptList[i].getId().toString());
//                    view1.setTextColor(Color.BLACK);
//                    view1.setGravity(Gravity.CENTER);
//                    view1.setWidth(500);
//                    view1.setHeight(125);
//                    view1.setPadding(10, 10, 10, 10);
//                    view1.setTextSize(14);
//                    row1.addView(view1);
//
//                    TextView view2 = new TextView(homepageActivity.this);
//                    view2.setText(deptList[i].getName());
//                    view2.setTextColor(Color.BLACK);
//                    view2.setGravity(Gravity.CENTER);
//                    view2.setWidth(500);
//                    view2.setHeight(125);
//                    view2.setPadding(10, 10, 10, 10);
//                    view2.setTextSize(14);
//                    row1.addView(view2);
//
//                    deptTable1.addView(row1);
//                    Integer currentDeptId = deptList[i].getId();
//                    row1.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            row1.setBackgroundColor(Color.GREEN);
//                            selectedIndex.add(currentDeptId);
//                        }
//                    });
//                }
//            }
//        });
//    }
//
//    public void setupTable() {
//        deptTable1 = (TableLayout) findViewById(R.id.regTable2);
//        TableRow row1 = new TableRow(homepageActivity.this);
//        row1.setBackgroundColor(Color.parseColor("#673AB7"));
//
//        TextView view1 = new TextView(homepageActivity.this);
//        view1.setText("Department ID");
//        view1.setTextColor(Color.WHITE);
//        view1.setGravity(Gravity.CENTER);
//        view1.setWidth(500);
//        view1.setHeight(150);
//        view1.setPadding(10, 10, 10, 10);
//        view1.setTextSize(18);
//        row1.addView(view1);
//
//        TextView view2 = new TextView(homepageActivity.this);
//        view2.setText("Department Name");
//        view2.setTextColor(Color.WHITE);
//        view2.setGravity(Gravity.CENTER);
//        view2.setWidth(500);
//        view2.setHeight(150);
//        view2.setPadding(10, 10, 10, 10);
//        view2.setTextSize(18);
//        row1.addView(view2);
//
//        deptTable1.addView(row1);
//    }
//
//}