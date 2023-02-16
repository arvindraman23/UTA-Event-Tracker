package com.example.utaeventtracker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class adminDeptCreateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dept_create);

        // To set Action Title Bar color
        ActionBar titleBar;
        titleBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#6200EE"));
        titleBar.setBackgroundDrawable(colorDrawable);

        Button addDeptButton = findViewById(R.id.addEventButton);
        addDeptButton.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(),"Department Added Successfully!",Toast.LENGTH_SHORT).show();
            Intent adminEventPage=new Intent(adminDeptCreateActivity.this, adminDeptListActivity.class);
            startActivity(adminEventPage);
        });

    }
}
