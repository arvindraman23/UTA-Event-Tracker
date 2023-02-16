package com.example.utaeventtracker;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class adminMainActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private TableLayout regTable;
    SharedClass globalInstance = SharedClass.getInstance();
    private Events[] regEventList;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog userdialog;
    private Button declineEvent;
    private TextView regTitle, regDate, regVenue, regDesc;
    private AlertDialog.Builder updatedialogBuilder;
    private AlertDialog userProfileDialog;
    private Boolean[] selectedDeptPreference = new Boolean[] {false, false, false, false, false, false, false, false};
    private Boolean isDeptPreferenceSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main_page);

        // To set Action Title Bar color
        ActionBar titleBar;
        titleBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#6200EE"));
        titleBar.setBackgroundDrawable(colorDrawable);

        Button registeredEvents = findViewById(R.id.registeredEvents);
        Button logout = findViewById(R.id.logout);
        Button userProfile = findViewById(R.id.userProfile);
        Button deptPreference = findViewById(R.id.deptPreference);

        registeredEvents.setOnClickListener(v -> {
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(adminMainActivity.this, "My Notification");
//                    mBuilder.setContentTitle("REMINDER: FOAM PARTY!");
//                    mBuilder.setContentText("Hurry up! Only 30 minutes remain for the Foam Party to begin!");
//                    mBuilder.setSmallIcon(R.drawable.time_icon_foreground);
//                    mBuilder.setAutoCancel(true);
//                    Intent resultIntent = new Intent(adminMainActivity.this, adminMainActivity.class);
//                    PendingIntent yourPendingIntent = PendingIntent.getActivity(adminMainActivity.this, 0,
//                            resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    mBuilder.setContentIntent(yourPendingIntent);
//                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(adminMainActivity.this);
//                    managerCompat.notify(1, mBuilder.build());
//                }
//            }, 5000);
            Intent adminEventPage=new Intent(adminMainActivity.this,adminEventListActivity.class);
            startActivity(adminEventPage);
        });

        userProfile.setOnClickListener(v -> {
            updatedialogBuilder = new AlertDialog.Builder(adminMainActivity.this, R.style.CustomAlertDialog);
            final View userProfilePopup = getLayoutInflater().inflate(R.layout.user_profile_screen, null);
            updatedialogBuilder.setView(userProfilePopup);
            userProfileDialog = updatedialogBuilder.create();
            userProfileDialog.show();
            userProfileDialog.getWindow().setLayout(800, 600);
        });

        deptPreference.setOnClickListener(v -> {
            updatedialogBuilder = new AlertDialog.Builder(adminMainActivity.this, R.style.CustomAlertDialog);
            final View deptFilterPopup = getLayoutInflater().inflate(R.layout.event_filter_dept_popup, null);
            Button cancelFilter = (Button) deptFilterPopup.findViewById(R.id.cancelFilterButton);
            cancelFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userProfileDialog.dismiss();
                }
            });
            Button applyFilter = (Button) deptFilterPopup.findViewById(R.id.applyFilterButton);
            CheckBox cb1 = (CheckBox)deptFilterPopup.findViewById(R.id.checkBox6);
            CheckBox cb2 = (CheckBox)deptFilterPopup.findViewById(R.id.checkBox7);
            CheckBox cb3 = (CheckBox)deptFilterPopup.findViewById(R.id.checkBox8);
            CheckBox cb4 = (CheckBox)deptFilterPopup.findViewById(R.id.checkBox9);
            CheckBox cb5 = (CheckBox)deptFilterPopup.findViewById(R.id.checkBox10);
            CheckBox cb6 = (CheckBox)deptFilterPopup.findViewById(R.id.checkBox);
            CheckBox cb7 = (CheckBox)deptFilterPopup.findViewById(R.id.checkBox2);
            CheckBox cb8 = (CheckBox)deptFilterPopup.findViewById(R.id.checkBox3);

            for (int i=0; i<8; i++) {
                if (selectedDeptPreference[i] == true) {
                    isDeptPreferenceSet = true;
                    break;
                }
            }
            if (isDeptPreferenceSet) {
                cb1.setChecked(selectedDeptPreference[0]);
                cb2.setChecked(selectedDeptPreference[1]);
                cb3.setChecked(selectedDeptPreference[2]);
                cb4.setChecked(selectedDeptPreference[3]);
                cb5.setChecked(selectedDeptPreference[4]);
                cb6.setChecked(selectedDeptPreference[5]);
                cb7.setChecked(selectedDeptPreference[6]);
                cb8.setChecked(selectedDeptPreference[7]);
            }

            applyFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer[] deptPref = new Integer[]{0,0,0,0,0,0,0,0};
                    if (cb1.isChecked())
                        selectedDeptPreference[0] = true;
                    if (cb2.isChecked())
                        selectedDeptPreference[1] = true;
                    if (cb3.isChecked())
                        selectedDeptPreference[2] = true;
                    if (cb4.isChecked())
                        selectedDeptPreference[3] = true;
                    if (cb5.isChecked())
                        selectedDeptPreference[4] = true;
                    if (cb6.isChecked())
                        selectedDeptPreference[5] = true;
                    if (cb7.isChecked())
                        selectedDeptPreference[6] = true;
                    if (cb8.isChecked())
                        selectedDeptPreference[7] = true;
                    globalInstance.selectedDepartmentPreference = deptPref;
                    Toast.makeText(getApplicationContext(), "Department Preference Applied Successfully", Toast.LENGTH_LONG).show();
                    userProfileDialog.dismiss();
                }
            });
            updatedialogBuilder.setView(deptFilterPopup);
            userProfileDialog = updatedialogBuilder.create();
            userProfileDialog.show();
            userProfileDialog.getWindow().setLayout(800, 1800);
        });

        logout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(adminMainActivity.this);
            builder.setMessage("Are you sure you want to logout?");
            builder.setTitle("Logout");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                Intent adminEventPage=new Intent(adminMainActivity.this,MainActivity.class);
                startActivity(adminEventPage);
            });
            builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        Button eventButton = findViewById(R.id.eventButton);
        eventButton.setOnClickListener(v -> {
            Intent adminEventPage=new Intent(adminMainActivity.this,adminEventListActivity.class);
            startActivity(adminEventPage);
        });

        ImageView imageView = findViewById(R.id.barcodeImageView1);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode("10020505050", BarcodeFormat.CODE_128, 400, 200);
            Bitmap bitmap = Bitmap.createBitmap(400, 200, Bitmap.Config.RGB_565);
            for (int i = 0; i < 400; i++){
                for (int j = 0; j < 200; j++){
                    bitmap.setPixel(i,j,bitMatrix.get(i,j)? Color.BLACK:Color.WHITE);
                }
            }
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        Button scanbutton = findViewById(R.id.scanButton);
        scanbutton.setOnClickListener(v -> {
            Intent signUp=new Intent(adminMainActivity.this,adminScannerActivity.class);
                startActivity(signUp);});

        Button deptButton = findViewById(R.id.deptButton);
        deptButton.setOnClickListener(v -> {
            Intent adminEventPage=new Intent(adminMainActivity.this,adminDeptListActivity.class);
            startActivity(adminEventPage);
        });
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    notificationListenerMethod();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (TimeoutException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        if (globalInstance.currentUser.getUser().getRoles()[0].getId() == 1) {
//            deptButton.setVisibility(deptButton.GONE);
//            scanbutton.setVisibility(deptButton.GONE);
//            imageView.setVisibility(deptButton.VISIBLE);
//        } else {
            deptButton.setVisibility(deptButton.VISIBLE);
            imageView.setVisibility(deptButton.GONE);
            scanbutton.setVisibility(scanbutton.VISIBLE);
//        }

    }

    public void fetchEventList() {
        OkHttpClient client = new OkHttpClient();
        String userCredentials = globalInstance.getAdminUser1() + ":" + globalInstance.getAdminPassword1();
        final String basic =
                "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
        Request request = new Request.Builder().url(globalInstance.eventUrl).header("Authorization", basic).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                adminMainActivity.this.runOnUiThread(new Runnable() {
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
                    adminMainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Events[] eventList = new Gson().fromJson(myResponse, Events[].class);
                            regEventList = eventList;
                            addEventRows(eventList);
                        }
                    });
                }
            }
        });
    }

    public void addEventRows(Events[] eventList) {
        regTable = (TableLayout) findViewById(R.id.table_main);
        for (int i=0; i<eventList.length; i++) {

            TableRow row1 = new TableRow(adminMainActivity.this);
            // Setting up table column headers
            TextView view1 = new TextView(adminMainActivity.this);
            view1.setText(eventList[i].getTitle());
            view1.setTextColor(Color.BLACK);
            view1.setGravity(Gravity.CENTER);
            view1.setWidth(340);
            view1.setHeight(125);
            view1.setPadding(10, 10, 10, 10);
            view1.setTextSize(14);
            row1.addView(view1);

            TextView view2 = new TextView(adminMainActivity.this);
            view2.setText(eventList[i].getDepartment().getName());
            view2.setTextColor(Color.BLACK);
            view2.setGravity(Gravity.CENTER);
            view2.setWidth(340);
            view2.setHeight(125);
            view2.setPadding(10, 10, 10, 10);
            view2.setTextSize(14);
            row1.addView(view2);

            TextView view3 = new TextView(adminMainActivity.this);
            view3.setText(eventList[i].getEventDate());
            view3.setTextColor(Color.BLACK);
            view3.setGravity(Gravity.CENTER);
            view3.setWidth(340);
            view3.setHeight(125);
            view3.setPadding(10, 10, 10, 10);
            view3.setTextSize(14);
            row1.addView(view3);
            regTable.addView(row1);
            Integer eventIndex = i;
            row1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openEventPopup(eventList[eventIndex]);
                }
            });
        }
    }

    public void notificationListenerMethod() throws IOException, TimeoutException {
        String user_id= globalInstance.currentUser.getUser().getId().toString();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("18.219.9.194");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String consumerTag = "SimpleConsumer";
        channel.exchangeDeclare("public", BuiltinExchangeType.FANOUT);
        channel.exchangeDeclare("user", BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare("Computer Science", BuiltinExchangeType.FANOUT);
        channel.exchangeDeclare("Engineering Management", BuiltinExchangeType.FANOUT);
        channel.exchangeDeclare("Chemistry", BuiltinExchangeType.FANOUT);

        AMQP.Queue.DeclareOk queueResult = channel.queueDeclare("", false, true, true, null);
        AMQP.Queue.DeclareOk cs = channel.queueDeclare("", false, true, true, null);
        AMQP.Queue.DeclareOk em = channel.queueDeclare("", false, true, true, null);
        AMQP.Queue.DeclareOk chem = channel.queueDeclare("", false, true, true, null);


        channel.queueBind(queueResult.getQueue(), "public", "");
        channel.queueBind(cs.getQueue(), "Computer Science","");
        channel.queueBind(cs.getQueue(), "Engineering Management","");
        channel.queueBind(cs.getQueue(), "Chemistry","");

        channel.queueDeclare(user_id,false,true,true,null);
        channel.queueBind(user_id,"user",user_id);

        System.out.println("[$consumerTag] Waiting for messages...");

        DeliverCallback deliverCallback = (x, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[$x] Received message: '$message' "+message);

            // Notification
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(adminMainActivity.this, "My Notification");
            mBuilder.setContentTitle("New Notification!");
            mBuilder.setContentText(message);
            mBuilder.setSmallIcon(R.drawable.time_icon_foreground);
            mBuilder.setAutoCancel(true);
            Intent resultIntent = new Intent(this, adminEventListActivity.class);
            PendingIntent yourPendingIntent = PendingIntent.getActivity(this, 0,
                    resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(yourPendingIntent);
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(adminMainActivity.this);
            managerCompat.notify(1, mBuilder.build());
        };

        CancelCallback cancelCallback = (x) -> {
            System.out.println("[$consumerTag] was canceled");
        };

        channel.basicConsume(queueResult.getQueue(), false, consumerTag, deliverCallback, cancelCallback);
        channel.basicConsume(user_id, false, "userTag", deliverCallback, cancelCallback);
        channel.basicConsume(cs.getQueue(), false, "userTag3", deliverCallback, cancelCallback);
        channel.basicConsume(em.getQueue(), false, "userTag2", deliverCallback, cancelCallback);
        channel.basicConsume(chem.getQueue(), false, "userTag1", deliverCallback, cancelCallback);
    }

    public void openEventPopup(Events event) {
        userdialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(adminMainActivity.this);
        builder.setMessage("Are you sure you want to de-register?");
        builder.setTitle("Remove Event");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
//            OkHttpClient deleteClient = new OkHttpClient();
//            String deleteUrl = globalInstance.deRegisterUrl;
//            String jsonBody = "{\"user\": { \"id\": " + globalInstance.currentUser.getUser().getId() + " }, \"uevent\": { \"event_id\": " + event.getId() + "} }";
//            String userCredentials = globalInstance.getAdminUser1() + ":" + globalInstance.getAdminPassword1();
//            final String basic =
//                    "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP);
//            Request deleteRequest = new Request.Builder().url(deleteUrl).delete(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
//                    jsonBody)).header("Authorization", basic).build();
//            deleteClient.newCall(deleteRequest).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    adminMainActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(), "Delete Failed.", Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    if (response.isSuccessful()) {
//                        String myResponse = response.body().string();
//                        adminMainActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                regTable.removeAllViews();
//                                setupTable();
//                                Toast.makeText(getApplicationContext(), "Department Deleted.", Toast.LENGTH_SHORT).show();
//                                dialog.cancel();
//                                fetchEventList();
//                            }
//                        });
//                    }
//                }
//            });
        });
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void setupTable() {
        regTable = (TableLayout) findViewById(R.id.table_main);
        TableRow row1 = new TableRow(adminMainActivity.this);
        row1.setBackgroundColor(Color.parseColor("#673AB7"));

        TextView view1 = new TextView(adminMainActivity.this);
        view1.setText("Title");
        view1.setTextColor(Color.WHITE);
        view1.setGravity(Gravity.CENTER);
        view1.setWidth(330);
        view1.setHeight(150);
        view1.setPadding(10, 10, 10, 10);
        view1.setTextSize(18);
        row1.addView(view1);

        TextView view2 = new TextView(adminMainActivity.this);
        view2.setText("Department");
        view2.setTextColor(Color.WHITE);
        view2.setGravity(Gravity.CENTER);
        view2.setWidth(330);
        view2.setHeight(150);
        view2.setPadding(10, 10, 10, 10);
        view2.setTextSize(18);
        row1.addView(view2);

        TextView view3 = new TextView(adminMainActivity.this);
        view3.setText("Date");
        view3.setTextColor(Color.WHITE);
        view3.setGravity(Gravity.CENTER);
        view3.setWidth(330);
        view3.setHeight(150);
        view3.setPadding(10, 10, 10, 10);
        view3.setTextSize(18);
        row1.addView(view3);

        regTable.addView(row1);
    }

}
