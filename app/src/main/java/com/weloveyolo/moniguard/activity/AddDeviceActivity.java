package com.weloveyolo.moniguard.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.R;

public class AddDeviceActivity extends AppCompatActivity {

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_device);

    }

    // 返回上一页
    public void goBack(View view){
        onBackPressed();
    }

}