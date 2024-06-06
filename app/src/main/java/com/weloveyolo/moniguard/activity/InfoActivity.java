package com.weloveyolo.moniguard.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.Message;
import com.weloveyolo.moniguard.util.DBHelper;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info);

    }
}
