package com.weloveyolo.moniguard.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.Camera;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.MoniGuardApi;
import com.weloveyolo.moniguard.util.CustomToast;
import com.weloveyolo.moniguard.util.HttpClient;

public class AddDeviceActivity extends AppCompatActivity {
    private EditText et;    // 设备名文本框
    private int sceneId;    // 场景id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_device);

        sceneId = getIntent().getIntExtra("sceneId", 0);

        et = this.findViewById(R.id.edit_scene_name);

        this.findViewById(R.id.button_finish).setOnClickListener(v -> {
            addDevice(et.getText().toString());
            et.setText("");
            et.clearFocus();
        });  // 完成按钮监听
    }

    // 返回上一页
    public void goBack(View view){
        onBackPressed();
    }

    public void addDevice(String deviceName){
//        new Thread(() -> {
//            IMoniGuardApi moniGuardApi = new MoniGuardApi();
//            moniGuardApi.getScenesApi().postCamera(sceneId, new Camera(), ((result, success) -> {
//                CustomToast ct = new CustomToast(getApplicationContext());
//                if(success) {
//                    onBackPressed();
//                }
//            }));
//        }).start();
    }
}