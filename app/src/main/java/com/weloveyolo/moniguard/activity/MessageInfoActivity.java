package com.weloveyolo.moniguard.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.MoniGuardApi;
import com.weloveyolo.moniguard.api.Settings;

import java.util.ArrayList;

public class MessageInfoActivity extends AppCompatActivity {

    Settings settings;
    Switch warningSwitch;
    Switch newGuest;
    Switch healthNotice;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_info);

        int residentId = getIntent().getIntExtra("residentId", 0);
        warningSwitch =  findViewById(R.id.switch_alert);
        newGuest = findViewById(R.id.switch_stranger);
        healthNotice = findViewById(R.id.switch_health);

        getSetting(residentId);

    }
    public void toBack(View view){
        boolean diff = false;
        if (settings.isReceiveWarning() != warningSwitch.isChecked()) {
            settings.setReceiveWarning(warningSwitch.isChecked());
            diff = true;
        }
        if (settings.isReceiveNewGuest() != newGuest.isChecked()) {
            settings.setReceiveNewGuest(newGuest.isChecked());
            diff = true;
        }
        if (settings.isHealthNotice() != healthNotice.isChecked()) {
            settings.setHealthNotice(healthNotice.isChecked());
            diff = true;
        }
        if (diff) putSetting();
        finish();
    }

    public void getSetting(int id){
        new Thread(() -> {
            IMoniGuardApi moniGuardApi = new MoniGuardApi();
            moniGuardApi.getResidentsApi().getSettings( (settings, success) -> {
                if(success){
                    this.settings = settings;
                    runOnUiThread(() ->{
                        warningSwitch.setChecked(settings.isReceiveWarning());
                        newGuest.setChecked(settings.isReceiveNewGuest());
                        healthNotice.setChecked(settings.isHealthNotice());
                    });
                }
            });

        }).start();
    }

    public void putSetting(){
        new Thread(() -> {
            IMoniGuardApi moniGuardApi = new MoniGuardApi();
            moniGuardApi.getResidentsApi().putSettings( settings,(result , success) -> {
                if(success){
                    runOnUiThread(() -> {
                        MainActivity.ct.showSuccessToast("设置已修改", 1000);

                    });
                }
            });

        }).start();
    }

}