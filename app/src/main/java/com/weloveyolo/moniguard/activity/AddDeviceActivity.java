package com.weloveyolo.moniguard.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.Camera;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.MoniGuardApi;
import com.weloveyolo.moniguard.util.CustomToast;
import com.weloveyolo.moniguard.util.HttpClient;

import java.util.Date;

public class AddDeviceActivity extends AppCompatActivity {

    private int sceneId;    // 场景id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_device);

        sceneId = getIntent().getIntExtra("sceneId", 0);

        EditText et = findViewById(R.id.edit_scene_name);

        Button finish_button = findViewById(R.id.button_finish);
        finish_button.setOnClickListener(v -> {
            String content = et.getText().toString().trim();
            et.setText("");
            et.clearFocus();
            if(content.isEmpty() || sceneId <= 0) return;
            addDevice(content);
        });
    }

    // 返回上一页
    public void goBack(View view){
        setResult(Activity.RESULT_CANCELED, new Intent());
        finish();
    }

    public void addDevice(String deviceName){
        new Thread(() -> {
            IMoniGuardApi moniGuardApi = new MoniGuardApi();
            moniGuardApi.getScenesApi().postCamera(sceneId, new Camera(deviceName, new Date()), ((result, success) -> {
                if(success) {
                    runOnUiThread(() -> {
                        MainActivity.ct.showSuccessToast("设备已添加", 1000);
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                    });
                }
            }));
        }).start();
    }
}