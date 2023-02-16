package com.example.utaeventtracker;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;

public class JSONReader {
    public static Events[] convertEventJsonToObject(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.sampleevent);
        String jsonString = "";
        try {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            jsonString = new String(data, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Events[] enums = new Gson().fromJson(jsonString, Events[].class);
        return enums;
    }

    public static Departments[] convertDeptJsonToObject(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.sampledept);
        String jsonString = "";
        try {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            jsonString = new String(data, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Departments[] enums = new Gson().fromJson(jsonString, Departments[].class);
        return enums;
    }

}
